package miuix.animation.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import miuix.animation.IAnimTarget;
import miuix.animation.ViewTarget;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.LinkNode;
import miuix.animation.utils.LogUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class RunnerHandler extends Handler {
    private final List<IAnimTarget> mDelList;
    private int mFrameCount;
    private boolean mIsTaskRunning;
    private long mLastRun;
    private final Map<IAnimTarget, AnimOperationInfo> mOpMap;
    private boolean mRunnerStart;
    private final int[] mSplitInfo;
    private boolean mStart;
    private final List<AnimTask> mTaskList;
    private long mTotalT;
    private final List<TransitionInfo> mTransList;
    private final Map<IAnimTarget, TransitionInfo> mTransMap;
    private final Set<IAnimTarget> runningTarget;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class SetToInfo {
        AnimState state;
        IAnimTarget target;

        private SetToInfo() {
        }
    }

    public RunnerHandler(Looper looper) {
        super(looper);
        this.runningTarget = new HashSet();
        this.mOpMap = new ConcurrentHashMap();
        this.mTransMap = new HashMap();
        this.mTaskList = new ArrayList();
        this.mDelList = new ArrayList();
        this.mTransList = new ArrayList();
        this.mLastRun = 0L;
        this.mTotalT = 0L;
        this.mFrameCount = 0;
        this.mSplitInfo = new int[2];
    }

    private void addAnimTask(List<TransitionInfo> list, int i, int i2) {
        Iterator<TransitionInfo> it = list.iterator();
        while (it.hasNext()) {
            for (AnimTask animTask : it.next().animTasks) {
                AnimTask taskOfMinCount = getTaskOfMinCount();
                if (taskOfMinCount == null || (this.mTaskList.size() < i2 && taskOfMinCount.getTotalAnimCount() + animTask.getAnimCount() > i)) {
                    this.mTaskList.add(animTask);
                } else {
                    taskOfMinCount.addToTail(animTask);
                }
            }
        }
    }

    private <T extends LinkNode> void addToMap(IAnimTarget iAnimTarget, T t, Map<IAnimTarget, T> map) {
        T t2 = map.get(iAnimTarget);
        if (t2 == null) {
            map.put(iAnimTarget, t);
        } else {
            t2.addToTail(t);
        }
    }

    private static void doSetOperation(AnimTask animTask, AnimStats animStats, UpdateInfo updateInfo, AnimOperationInfo animOperationInfo) {
        byte b = updateInfo.animInfo.op;
        if (!AnimTask.isRunning(b) || animOperationInfo.op == 0) {
            return;
        }
        List<FloatProperty> list = animOperationInfo.propList;
        if ((list == null || list.contains(updateInfo.property)) && AnimTask.isRunning(updateInfo.animInfo.op)) {
            animOperationInfo.usedCount++;
            byte b2 = animOperationInfo.op;
            if (b2 == 3) {
                if (updateInfo.animInfo.targetValue != Double.MAX_VALUE) {
                    AnimInfo animInfo = updateInfo.animInfo;
                    animInfo.value = animInfo.targetValue;
                }
                animTask.animStats.endCount++;
                animStats.endCount++;
            } else if (b2 == 4) {
                animTask.animStats.cancelCount++;
                animStats.cancelCount++;
            }
            updateInfo.setOp(animOperationInfo.op);
            TransitionInfo.decreaseStartCountForDelayAnim(animTask, animStats, updateInfo, b);
        }
    }

    private void doSetup() {
        for (TransitionInfo transitionInfo : this.mTransMap.values()) {
            this.runningTarget.add(transitionInfo.target);
            do {
                transitionInfo.target.animManager.setupTransition(transitionInfo);
                transitionInfo = transitionInfo.remove();
            } while (transitionInfo != null);
        }
        this.mTransMap.clear();
        if (this.mRunnerStart) {
            return;
        }
        this.mRunnerStart = true;
        AnimRunner.getInst().start();
    }

    private AnimTask getTaskOfMinCount() {
        AnimTask animTask = null;
        int i = Integer.MAX_VALUE;
        for (AnimTask animTask2 : this.mTaskList) {
            int totalAnimCount = animTask2.getTotalAnimCount();
            if (totalAnimCount < i) {
                animTask = animTask2;
                i = totalAnimCount;
            }
        }
        return animTask;
    }

    private int getTotalAnimCount() {
        Iterator<IAnimTarget> it = this.runningTarget.iterator();
        int i = 0;
        while (it.hasNext()) {
            i += it.next().animManager.getTotalAnimCount();
        }
        return i;
    }

    private static boolean handleSetTo(AnimTask animTask, AnimStats animStats, UpdateInfo updateInfo) {
        if (AnimValueUtils.handleSetToValue(updateInfo)) {
            if (AnimTask.isRunning(updateInfo.animInfo.op)) {
                animTask.animStats.cancelCount++;
                animStats.cancelCount++;
                updateInfo.setOp((byte) 4);
                TransitionInfo.decreaseStartCountForDelayAnim(animTask, animStats, updateInfo, updateInfo.animInfo.op);
            }
            return true;
        }
        return false;
    }

    private static void handleUpdate(TransitionInfo transitionInfo, AnimOperationInfo animOperationInfo, AnimStats animStats) {
        boolean contains = transitionInfo.target.animManager.mStartAnim.contains(transitionInfo.key);
        for (AnimTask animTask : transitionInfo.animTasks) {
            List<UpdateInfo> list = transitionInfo.updateList;
            int i = animTask.startPos;
            int animCount = animTask.getAnimCount() + i;
            while (i < animCount) {
                UpdateInfo updateInfo = list.get(i);
                if (updateInfo != null && !handleSetTo(animTask, animStats, updateInfo) && contains && animOperationInfo != null) {
                    doSetOperation(animTask, animStats, updateInfo, animOperationInfo);
                }
                i++;
            }
        }
        if (!contains) {
            transitionInfo.target.animManager.mStartAnim.add(transitionInfo.key);
        }
        if (animStats.isRunning() && animStats.updateCount > 0 && transitionInfo.target.animManager.mBeginAnim.add(transitionInfo.key)) {
            TransitionInfo.sMap.put(Integer.valueOf(transitionInfo.id), transitionInfo);
            transitionInfo.target.handler.obtainMessage(0, transitionInfo.id, 0).sendToTarget();
        }
    }

    private boolean isInfoInTransMap(TransitionInfo transitionInfo) {
        for (TransitionInfo transitionInfo2 = this.mTransMap.get(transitionInfo.target); transitionInfo2 != null; transitionInfo2 = (TransitionInfo) transitionInfo2.next) {
            if (transitionInfo2 == transitionInfo) {
                return true;
            }
        }
        return false;
    }

    private void onSetTo(SetToInfo setToInfo) {
        boolean z = setToInfo.target instanceof ViewTarget;
        Iterator<Object> it = setToInfo.state.keySet().iterator();
        while (it.hasNext()) {
            FloatProperty property = setToInfo.state.getProperty(it.next());
            UpdateInfo updateInfo = setToInfo.target.animManager.mUpdateMap.get(property);
            if (updateInfo != null) {
                updateInfo.animInfo.setToValue = setToInfo.state.get(setToInfo.target, property);
                if (!z) {
                    updateInfo.setTargetValue(setToInfo.target);
                }
            }
        }
        if (setToInfo.target.isAnimRunning(new FloatProperty[0])) {
            return;
        }
        setToInfo.target.animManager.mUpdateMap.clear();
    }

    private void runAnim(long j, long j2, boolean z) {
        if (this.runningTarget.isEmpty()) {
            stopAnimRunner();
            return;
        }
        this.mLastRun = j;
        long averageDelta = AnimRunner.getInst().getAverageDelta();
        int i = this.mFrameCount;
        if (i == 1 && j2 > 2 * averageDelta) {
            j2 = averageDelta;
        }
        this.mTotalT += j2;
        this.mFrameCount = i + 1;
        ThreadPoolUtil.getSplitCount(getTotalAnimCount(), this.mSplitInfo);
        int[] iArr = this.mSplitInfo;
        int i2 = iArr[0];
        int i3 = iArr[1];
        Iterator<IAnimTarget> it = this.runningTarget.iterator();
        while (it.hasNext()) {
            it.next().animManager.getTransitionInfos(this.mTransList);
        }
        addAnimTask(this.mTransList, i3, i2);
        this.mIsTaskRunning = !this.mTaskList.isEmpty();
        AnimTask.sTaskCount.set(this.mTaskList.size());
        Iterator<AnimTask> it2 = this.mTaskList.iterator();
        while (it2.hasNext()) {
            it2.next().start(this.mTotalT, j2, z);
        }
        this.mTransList.clear();
        this.mTaskList.clear();
    }

    private boolean setupWaitTrans(IAnimTarget iAnimTarget) {
        TransitionInfo poll = iAnimTarget.animManager.mWaitState.poll();
        if (poll != null) {
            addToMap(poll.target, poll, this.mTransMap);
            return true;
        }
        return false;
    }

    private void stopAnimRunner() {
        if (this.mStart) {
            if (LogUtils.isLogEnabled()) {
                LogUtils.debug("RunnerHandler.stopAnimRunner", "total time = " + this.mTotalT, "frame count = " + this.mFrameCount);
            }
            this.mStart = false;
            this.mRunnerStart = false;
            this.mTotalT = 0L;
            this.mFrameCount = 0;
            AnimRunner.getInst().end();
        }
    }

    private void updateAnim() {
        boolean z = false;
        this.mIsTaskRunning = false;
        for (IAnimTarget iAnimTarget : this.runningTarget) {
            if (updateTarget(iAnimTarget, this.mTransList) || setupWaitTrans(iAnimTarget)) {
                z = true;
            } else {
                this.mDelList.add(iAnimTarget);
            }
            this.mTransList.clear();
        }
        this.runningTarget.removeAll(this.mDelList);
        this.mDelList.clear();
        if (!this.mTransMap.isEmpty()) {
            doSetup();
            z = true;
        }
        if (z) {
            return;
        }
        stopAnimRunner();
    }

    private boolean updateTarget(IAnimTarget iAnimTarget, List<TransitionInfo> list) {
        AnimOperationInfo animOperationInfo;
        int i;
        int i2;
        iAnimTarget.animManager.getTransitionInfos(list);
        AnimOperationInfo animOperationInfo2 = this.mOpMap.get(iAnimTarget);
        char c = 0;
        int i3 = 0;
        int i4 = 0;
        for (TransitionInfo transitionInfo : list) {
            if (isInfoInTransMap(transitionInfo)) {
                i4++;
            } else {
                if (animOperationInfo2 == null || transitionInfo.startTime <= animOperationInfo2.sendTime) {
                    animOperationInfo = animOperationInfo2;
                } else {
                    i3++;
                    animOperationInfo = null;
                }
                AnimStats animStats = transitionInfo.getAnimStats();
                if (animStats.isStarted()) {
                    handleUpdate(transitionInfo, animOperationInfo, animStats);
                }
                if (LogUtils.isLogEnabled()) {
                    String str = "---- updateAnim, target = " + iAnimTarget;
                    Object[] objArr = new Object[6];
                    objArr[c] = "key = " + transitionInfo.key;
                    objArr[1] = "useOp = " + animOperationInfo;
                    objArr[2] = "info.startTime = " + transitionInfo.startTime;
                    StringBuilder sb = new StringBuilder();
                    sb.append("opInfo.time = ");
                    sb.append(animOperationInfo2 != null ? Long.valueOf(animOperationInfo2.sendTime) : null);
                    i = 3;
                    objArr[3] = sb.toString();
                    i2 = 4;
                    objArr[4] = "stats.isRunning = " + animStats.isRunning();
                    objArr[5] = "stats = " + animStats;
                    LogUtils.debug(str, objArr);
                } else {
                    i = 3;
                    i2 = 4;
                }
                if (animStats.isRunning()) {
                    i4++;
                } else {
                    iAnimTarget.animManager.notifyTransitionEnd(transitionInfo, 2, animStats.cancelCount > animStats.endCount ? i2 : i);
                }
                c = 0;
            }
        }
        if (animOperationInfo2 != null && (i3 == list.size() || animOperationInfo2.isUsed())) {
            this.mOpMap.remove(iAnimTarget);
        }
        list.clear();
        return i4 > 0;
    }

    public void addSetToState(IAnimTarget iAnimTarget, AnimState animState) {
        SetToInfo setToInfo = new SetToInfo();
        setToInfo.target = iAnimTarget;
        if (animState.isTemporary) {
            AnimState animState2 = new AnimState();
            setToInfo.state = animState2;
            animState2.set(animState);
        } else {
            setToInfo.state = animState;
        }
        obtainMessage(4, setToInfo).sendToTarget();
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            TransitionInfo remove = TransitionInfo.sMap.remove(Integer.valueOf(message.arg1));
            if (remove != null) {
                addToMap(remove.target, remove, this.mTransMap);
                if (!this.mIsTaskRunning) {
                    doSetup();
                }
            }
        } else if (i == 2) {
            updateAnim();
        } else if (i != 3) {
            if (i == 4) {
                onSetTo((SetToInfo) message.obj);
            } else if (i == 5) {
                this.runningTarget.clear();
                stopAnimRunner();
            }
        } else if (this.mRunnerStart) {
            long currentTimeMillis = System.currentTimeMillis();
            long averageDelta = AnimRunner.getInst().getAverageDelta();
            boolean booleanValue = ((Boolean) message.obj).booleanValue();
            if (!this.mStart) {
                this.mStart = true;
                this.mTotalT = 0L;
                this.mFrameCount = 0;
                runAnim(currentTimeMillis, averageDelta, booleanValue);
            } else if (!this.mIsTaskRunning) {
                runAnim(currentTimeMillis, currentTimeMillis - this.mLastRun, booleanValue);
            }
        }
        message.obj = null;
    }

    public void setOperation(AnimOperationInfo animOperationInfo) {
        if (animOperationInfo.target.isAnimRunning(new FloatProperty[0])) {
            animOperationInfo.sendTime = System.nanoTime();
            this.mOpMap.put(animOperationInfo.target, animOperationInfo);
        }
    }
}
