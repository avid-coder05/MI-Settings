package miuix.animation.internal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.controller.AnimState;
import miuix.animation.physics.AnimationHandler;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LogUtils;

/* loaded from: classes5.dex */
public class AnimRunner implements AnimationHandler.AnimationFrameCallback {
    private static final Handler sMainHandler;
    public static final RunnerHandler sRunnerHandler;
    private static final HandlerThread sRunnerThread;
    private volatile long mAverageDelta;
    private long[] mDeltaRecord;
    private volatile boolean mIsRunning;
    private long mLastFrameTime;
    private float mRatio;
    private int mRecordCount;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class Holder {
        static final AnimRunner inst = new AnimRunner();
    }

    static {
        HandlerThread handlerThread = new HandlerThread("AnimRunnerThread", 5);
        sRunnerThread = handlerThread;
        handlerThread.start();
        sRunnerHandler = new RunnerHandler(handlerThread.getLooper());
        sMainHandler = new Handler(Looper.getMainLooper()) { // from class: miuix.animation.internal.AnimRunner.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 0) {
                    AnimRunner.startAnimRunner();
                } else if (i != 1) {
                } else {
                    AnimRunner.endAnimation();
                }
            }
        };
    }

    private AnimRunner() {
        this.mAverageDelta = 16L;
        this.mDeltaRecord = new long[]{0, 0, 0, 0, 0};
        this.mRecordCount = 0;
    }

    private long average(long[] jArr) {
        int i = 0;
        long j = 0;
        for (long j2 : jArr) {
            j += j2;
            if (j2 > 0) {
                i++;
            }
        }
        if (i > 0) {
            return j / i;
        }
        return 0L;
    }

    private long calculateAverageDelta(long j) {
        long average = average(this.mDeltaRecord);
        if (average > 0) {
            j = average;
        }
        if (j == 0 || j > 16) {
            j = 16;
        }
        return (long) Math.ceil(((float) j) / this.mRatio);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void endAnimation() {
        AnimRunner inst = getInst();
        if (inst.mIsRunning) {
            if (LogUtils.isLogEnabled()) {
                LogUtils.debug("AnimRunner.endAnimation", new Object[0]);
            }
            inst.mIsRunning = false;
            AnimationHandler.getInstance().removeCallback(inst);
        }
    }

    public static AnimRunner getInst() {
        return Holder.inst;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void startAnimRunner() {
        AnimRunner inst = getInst();
        if (inst.mIsRunning) {
            return;
        }
        if (LogUtils.isLogEnabled()) {
            LogUtils.debug("AnimRunner.start", new Object[0]);
        }
        inst.mRatio = Folme.getTimeRatio();
        inst.mIsRunning = true;
        AnimationHandler.getInstance().addAnimationFrameCallback(inst, 0L);
    }

    private static void updateAnimRunner(Collection<IAnimTarget> collection, boolean z) {
        if (collection.size() == 0) {
            sRunnerHandler.sendEmptyMessage(5);
        }
        for (IAnimTarget iAnimTarget : collection) {
            boolean isAnimRunning = iAnimTarget.animManager.isAnimRunning(new FloatProperty[0]);
            boolean isAnimSetup = iAnimTarget.animManager.isAnimSetup();
            boolean isValidFlag = iAnimTarget.isValidFlag();
            if (isAnimRunning) {
                if (z) {
                    iAnimTarget.animManager.runUpdate();
                } else {
                    iAnimTarget.animManager.update(false);
                }
            } else if (!isAnimSetup && !isAnimRunning && iAnimTarget.hasFlags(1L) && isValidFlag) {
                Folme.clean(iAnimTarget);
            }
        }
    }

    private void updateRunningTime(long j) {
        long j2 = this.mLastFrameTime;
        long j3 = 0;
        if (j2 == 0) {
            this.mLastFrameTime = j;
        } else {
            j3 = j - j2;
            this.mLastFrameTime = j;
        }
        int i = this.mRecordCount;
        this.mDeltaRecord[i % 5] = j3;
        this.mRecordCount = i + 1;
        this.mAverageDelta = calculateAverageDelta(j3);
    }

    public void cancel(IAnimTarget iAnimTarget, FloatProperty... floatPropertyArr) {
        sRunnerHandler.setOperation(new AnimOperationInfo(iAnimTarget, (byte) 4, null, floatPropertyArr));
    }

    @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallback
    public boolean doAnimationFrame(long j) {
        updateRunningTime(j);
        if (this.mIsRunning) {
            Collection<IAnimTarget> targets = Folme.getTargets();
            int i = 0;
            for (IAnimTarget iAnimTarget : targets) {
                if (iAnimTarget.animManager.isAnimRunning(new FloatProperty[0])) {
                    i += iAnimTarget.animManager.getTotalAnimCount();
                }
            }
            boolean z = i > 500;
            if ((!z && targets.size() > 0) || targets.size() == 0) {
                updateAnimRunner(targets, z);
            }
            RunnerHandler runnerHandler = sRunnerHandler;
            Message obtainMessage = runnerHandler.obtainMessage();
            obtainMessage.what = 3;
            obtainMessage.obj = Boolean.valueOf(z);
            runnerHandler.sendMessage(obtainMessage);
            if (z && targets.size() > 0) {
                updateAnimRunner(targets, z);
            }
        }
        return this.mIsRunning;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void end() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            endAnimation();
        } else {
            sMainHandler.sendEmptyMessage(1);
        }
    }

    public void end(IAnimTarget iAnimTarget, String... strArr) {
        if (CommonUtils.isArrayEmpty(strArr)) {
            iAnimTarget.handler.sendEmptyMessage(3);
        }
        sRunnerHandler.setOperation(new AnimOperationInfo(iAnimTarget, (byte) 3, strArr, null));
    }

    public void end(IAnimTarget iAnimTarget, FloatProperty... floatPropertyArr) {
        if (CommonUtils.isArrayEmpty(floatPropertyArr)) {
            iAnimTarget.handler.sendEmptyMessage(3);
        }
        sRunnerHandler.setOperation(new AnimOperationInfo(iAnimTarget, (byte) 3, null, floatPropertyArr));
    }

    public long getAverageDelta() {
        return this.mAverageDelta;
    }

    public void run(IAnimTarget iAnimTarget, AnimState animState, AnimState animState2, AnimConfigLink animConfigLink) {
        run(new TransitionInfo(iAnimTarget, animState, animState2, animConfigLink));
    }

    public void run(final TransitionInfo transitionInfo) {
        transitionInfo.target.executeOnInitialized(new Runnable() { // from class: miuix.animation.internal.AnimRunner.2
            @Override // java.lang.Runnable
            public void run() {
                TransitionInfo transitionInfo2 = transitionInfo;
                transitionInfo2.target.animManager.startAnim(transitionInfo2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            startAnimRunner();
        } else {
            sMainHandler.sendEmptyMessage(0);
        }
    }
}
