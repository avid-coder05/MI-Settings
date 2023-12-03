package miuix.animation.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.controller.AnimState;
import miuix.animation.internal.TransitionInfo;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LogUtils;

/* loaded from: classes5.dex */
public class AnimManager implements TransitionInfo.IUpdateInfoCreator {
    IAnimTarget mTarget;
    private List<UpdateInfo> mUpdateList;
    final Set<Object> mStartAnim = new HashSet();
    final Set<Object> mBeginAnim = new HashSet();
    final ConcurrentHashMap<FloatProperty, UpdateInfo> mUpdateMap = new ConcurrentHashMap<>();
    final ConcurrentHashMap<Object, TransitionInfo> mRunningInfo = new ConcurrentHashMap<>();
    final ConcurrentLinkedQueue<TransitionInfo> mWaitState = new ConcurrentLinkedQueue<>();
    private final Runnable mUpdateTask = new Runnable() { // from class: miuix.animation.internal.AnimManager.1
        @Override // java.lang.Runnable
        public void run() {
            AnimManager.this.update(true);
        }
    };

    private boolean containProperties(TransitionInfo transitionInfo, FloatProperty... floatPropertyArr) {
        for (FloatProperty floatProperty : floatPropertyArr) {
            if (transitionInfo.containsProperty(floatProperty)) {
                return true;
            }
        }
        return false;
    }

    private boolean pendState(TransitionInfo transitionInfo) {
        if (CommonUtils.hasFlags(transitionInfo.to.flags, 1L)) {
            this.mWaitState.add(transitionInfo);
            return true;
        }
        return false;
    }

    private void removeSameAnim(TransitionInfo transitionInfo) {
        for (TransitionInfo transitionInfo2 : this.mRunningInfo.values()) {
            if (transitionInfo2 != transitionInfo) {
                List<UpdateInfo> list = transitionInfo2.updateList;
                if (this.mUpdateList == null) {
                    this.mUpdateList = new ArrayList();
                }
                for (UpdateInfo updateInfo : list) {
                    if (!transitionInfo.to.contains(updateInfo.property)) {
                        this.mUpdateList.add(updateInfo);
                    }
                }
                if (this.mUpdateList.isEmpty()) {
                    notifyTransitionEnd(transitionInfo2, 5, 4);
                } else if (this.mUpdateList.size() != transitionInfo2.updateList.size()) {
                    transitionInfo2.updateList = this.mUpdateList;
                    this.mUpdateList = null;
                    transitionInfo2.setupTasks(false);
                } else {
                    this.mUpdateList.clear();
                }
            }
        }
    }

    private void setTargetValue(AnimState animState, AnimConfigLink animConfigLink) {
        Iterator<Object> it = animState.keySet().iterator();
        while (it.hasNext()) {
            FloatProperty tempProperty = animState.getTempProperty(it.next());
            double d = animState.get(this.mTarget, tempProperty);
            UpdateInfo updateInfo = this.mTarget.animManager.mUpdateMap.get(tempProperty);
            if (updateInfo != null) {
                updateInfo.animInfo.setToValue = d;
            }
            if (tempProperty instanceof IIntValueProperty) {
                this.mTarget.setIntValue((IIntValueProperty) tempProperty, (int) d);
            } else {
                this.mTarget.setValue(tempProperty, (float) d);
            }
            this.mTarget.trackVelocity(tempProperty, d);
        }
        this.mTarget.setToNotify(animState, animConfigLink);
    }

    public void clear() {
        this.mStartAnim.clear();
        this.mBeginAnim.clear();
        this.mUpdateMap.clear();
        this.mRunningInfo.clear();
        this.mWaitState.clear();
    }

    public int getTotalAnimCount() {
        Iterator<TransitionInfo> it = this.mRunningInfo.values().iterator();
        int i = 0;
        while (it.hasNext()) {
            i += it.next().getAnimCount();
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getTransitionInfos(List<TransitionInfo> list) {
        for (TransitionInfo transitionInfo : this.mRunningInfo.values()) {
            if (transitionInfo.updateList != null && !transitionInfo.updateList.isEmpty()) {
                list.add(transitionInfo);
            }
        }
    }

    @Override // miuix.animation.internal.TransitionInfo.IUpdateInfoCreator
    public UpdateInfo getUpdateInfo(FloatProperty floatProperty) {
        UpdateInfo updateInfo = this.mUpdateMap.get(floatProperty);
        if (updateInfo == null) {
            UpdateInfo updateInfo2 = new UpdateInfo(floatProperty);
            UpdateInfo putIfAbsent = this.mUpdateMap.putIfAbsent(floatProperty, updateInfo2);
            return putIfAbsent != null ? putIfAbsent : updateInfo2;
        }
        return updateInfo;
    }

    public boolean isAnimRunning(FloatProperty... floatPropertyArr) {
        if (!CommonUtils.isArrayEmpty(floatPropertyArr) || (this.mRunningInfo.isEmpty() && this.mWaitState.isEmpty())) {
            Iterator<TransitionInfo> it = this.mRunningInfo.values().iterator();
            while (it.hasNext()) {
                if (containProperties(it.next(), floatPropertyArr)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean isAnimSetup() {
        return AnimRunner.sRunnerHandler.hasMessages(1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyTransitionEnd(TransitionInfo transitionInfo, int i, int i2) {
        this.mRunningInfo.remove(transitionInfo.key);
        if (this.mStartAnim.remove(transitionInfo.key)) {
            this.mBeginAnim.remove(transitionInfo.key);
            TransitionInfo.sMap.put(Integer.valueOf(transitionInfo.id), transitionInfo);
            this.mTarget.handler.obtainMessage(i, transitionInfo.id, i2, transitionInfo).sendToTarget();
        }
        if (isAnimRunning(new FloatProperty[0])) {
            return;
        }
        this.mUpdateMap.clear();
    }

    public void runUpdate() {
        this.mTarget.post(this.mUpdateTask);
    }

    public void setTarget(IAnimTarget iAnimTarget) {
        this.mTarget = iAnimTarget;
    }

    public void setTo(AnimState animState, AnimConfigLink animConfigLink) {
        if (LogUtils.isLogEnabled()) {
            LogUtils.debug("setTo, target = " + this.mTarget, "to = " + animState);
        }
        if (animState.keySet().size() > 150) {
            AnimRunner.sRunnerHandler.addSetToState(this.mTarget, animState);
        } else {
            setTargetValue(animState, animConfigLink);
        }
    }

    public void setVelocity(FloatProperty floatProperty, float f) {
        getUpdateInfo(floatProperty).velocity = f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setupTransition(TransitionInfo transitionInfo) {
        this.mRunningInfo.put(transitionInfo.key, transitionInfo);
        transitionInfo.initUpdateList(this);
        transitionInfo.setupTasks(true);
        removeSameAnim(transitionInfo);
        boolean contains = transitionInfo.target.animManager.mStartAnim.contains(transitionInfo.key);
        if (transitionInfo.config.listeners.isEmpty() || !contains) {
            return;
        }
        TransitionInfo.sMap.put(Integer.valueOf(transitionInfo.id), transitionInfo);
        transitionInfo.target.handler.obtainMessage(4, transitionInfo.id, 0, transitionInfo).sendToTarget();
    }

    public void startAnim(TransitionInfo transitionInfo) {
        if (!pendState(transitionInfo)) {
            TransitionInfo.sMap.put(Integer.valueOf(transitionInfo.id), transitionInfo);
            AnimRunner.sRunnerHandler.obtainMessage(1, transitionInfo.id, 0).sendToTarget();
            return;
        }
        LogUtils.debug(this + ".startAnim, pendState", new Object[0]);
    }

    public void update(boolean z) {
        this.mTarget.handler.update(z);
    }
}
