package miuix.overscroller.widget;

import android.content.Context;
import android.view.animation.AnimationUtils;
import miuix.overscroller.internal.dynamicanimation.animation.DynamicAnimation;
import miuix.overscroller.internal.dynamicanimation.animation.FlingAnimation;
import miuix.overscroller.internal.dynamicanimation.animation.FloatValueHolder;
import miuix.overscroller.internal.dynamicanimation.animation.SpringAnimation;
import miuix.overscroller.internal.dynamicanimation.animation.SpringForce;
import miuix.overscroller.widget.OverScroller;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class DynamicScroller extends OverScroller.SplineOverScroller implements FlingAnimation.FinalValueListener {
    private FlingAnimation mFlingAnimation;
    private OverScrollHandler mHandler;
    private SpringAnimation mSpringAnimation;
    private FloatValueHolder mValue;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class OverScrollHandler {
        private float mAnimMaxValue;
        private float mAnimMinValue;
        DynamicAnimation<?> mAnimation;
        private long mLastUpdateTime;
        private final int mMaxLegalValue;
        private final int mMinLegalValue;
        private Monitor mMonitor = new Monitor();
        private OnFinishedListener mOnFinishedListener;
        int mStartValue;
        int mValue;
        float mVelocity;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes5.dex */
        public class Monitor implements DynamicAnimation.OnAnimationUpdateListener {
            private Monitor() {
            }

            @Override // miuix.overscroller.internal.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                OverScrollHandler overScrollHandler = OverScrollHandler.this;
                overScrollHandler.mVelocity = f2;
                overScrollHandler.mValue = overScrollHandler.mStartValue + ((int) f);
                OverScrollLogger.verbose("%s updating value(%f), velocity(%f), min(%f), max(%f)", dynamicAnimation.getClass().getSimpleName(), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(OverScrollHandler.this.mAnimMinValue), Float.valueOf(OverScrollHandler.this.mAnimMaxValue));
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes5.dex */
        public interface OnFinishedListener {
            boolean whenFinished(float f, float f2);
        }

        OverScrollHandler(DynamicAnimation<?> dynamicAnimation, int i, float f) {
            this.mAnimation = dynamicAnimation;
            dynamicAnimation.setMinValue(-3.4028235E38f);
            this.mAnimation.setMaxValue(Float.MAX_VALUE);
            this.mStartValue = i;
            this.mVelocity = f;
            int i2 = Integer.MAX_VALUE;
            int i3 = Integer.MIN_VALUE;
            if (i > 0) {
                i3 = Integer.MIN_VALUE + i;
            } else if (i < 0) {
                i2 = Integer.MAX_VALUE + i;
            }
            this.mMinLegalValue = i3;
            this.mMaxLegalValue = i2;
            this.mAnimation.setStartValue(0.0f);
            this.mAnimation.setStartVelocity(f);
        }

        void cancel() {
            this.mLastUpdateTime = 0L;
            this.mAnimation.cancel();
            this.mAnimation.removeUpdateListener(this.mMonitor);
        }

        boolean continueWhenFinished() {
            OnFinishedListener onFinishedListener = this.mOnFinishedListener;
            if (onFinishedListener != null) {
                return onFinishedListener.whenFinished(this.mValue, this.mVelocity);
            }
            return false;
        }

        DynamicAnimation<?> getAnimation() {
            return this.mAnimation;
        }

        int getOffset(int i) {
            return i - this.mStartValue;
        }

        void setMaxValue(int i) {
            int i2 = this.mMaxLegalValue;
            if (i > i2) {
                i = i2;
            }
            float max = Math.max(i - this.mStartValue, 0);
            this.mAnimation.setMaxValue(max);
            this.mAnimMaxValue = max;
        }

        void setMinValue(int i) {
            int i2 = this.mMinLegalValue;
            if (i < i2) {
                i = i2;
            }
            float min = Math.min(i - this.mStartValue, 0);
            this.mAnimation.setMinValue(min);
            this.mAnimMinValue = min;
        }

        void setOnFinishedListener(OnFinishedListener onFinishedListener) {
            this.mOnFinishedListener = onFinishedListener;
        }

        void start() {
            this.mAnimation.addUpdateListener(this.mMonitor);
            this.mAnimation.start(true);
            this.mLastUpdateTime = 0L;
        }

        boolean update() {
            long j = this.mLastUpdateTime;
            long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            if (currentAnimationTimeMillis == j) {
                OverScrollLogger.verbose("update done in this frame, dropping current update request");
                return !this.mAnimation.isRunning();
            }
            boolean doAnimationFrame = this.mAnimation.doAnimationFrame(currentAnimationTimeMillis);
            if (doAnimationFrame) {
                OverScrollLogger.verbose("%s finishing value(%d) velocity(%f)", this.mAnimation.getClass().getSimpleName(), Integer.valueOf(this.mValue), Float.valueOf(this.mVelocity));
                this.mAnimation.removeUpdateListener(this.mMonitor);
                this.mLastUpdateTime = 0L;
            }
            this.mLastUpdateTime = currentAnimationTimeMillis;
            return doAnimationFrame;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DynamicScroller(Context context) {
        super(context);
        this.mValue = new FloatValueHolder();
        SpringAnimation springAnimation = new SpringAnimation(this.mValue);
        this.mSpringAnimation = springAnimation;
        springAnimation.setSpring(new SpringForce());
        this.mSpringAnimation.setMinimumVisibleChange(0.5f);
        this.mSpringAnimation.getSpring().setDampingRatio(0.97f);
        this.mSpringAnimation.getSpring().setStiffness(130.5f);
        this.mSpringAnimation.getSpring().setTimeRatio(1000.0d);
        FlingAnimation flingAnimation = new FlingAnimation(this.mValue, this);
        this.mFlingAnimation = flingAnimation;
        flingAnimation.setMinimumVisibleChange(0.5f);
        this.mFlingAnimation.setFriction(0.4761905f);
    }

    private void doFling(int i, int i2, final int i3, final int i4, final int i5) {
        int i6;
        int predictDuration;
        this.mFlingAnimation.setStartValue(0.0f);
        float f = i2;
        this.mFlingAnimation.setStartVelocity(f);
        long predictNaturalDest = i + this.mFlingAnimation.predictNaturalDest();
        if (predictNaturalDest > i4) {
            predictDuration = (int) this.mFlingAnimation.predictTimeTo(i4 - i);
            i6 = i4;
        } else if (predictNaturalDest < i3) {
            predictDuration = (int) this.mFlingAnimation.predictTimeTo(i3 - i);
            i6 = i3;
        } else {
            i6 = (int) predictNaturalDest;
            predictDuration = (int) this.mFlingAnimation.predictDuration();
        }
        setFinished(false);
        setCurrVelocity(f);
        setStartTime(AnimationUtils.currentAnimationTimeMillis());
        setCurrentPosition(i);
        setStart(i);
        setDuration(predictDuration);
        setFinal(i6);
        setState(0);
        int min = Math.min(i3, i);
        int max = Math.max(i4, i);
        OverScrollHandler overScrollHandler = new OverScrollHandler(this.mFlingAnimation, i, f);
        this.mHandler = overScrollHandler;
        overScrollHandler.setOnFinishedListener(new OverScrollHandler.OnFinishedListener() { // from class: miuix.overscroller.widget.DynamicScroller.1
            @Override // miuix.overscroller.widget.DynamicScroller.OverScrollHandler.OnFinishedListener
            public boolean whenFinished(float f2, float f3) {
                OverScrollLogger.debug("fling finished: value(%f), velocity(%f), scroller boundary(%d, %d)", Float.valueOf(f2), Float.valueOf(f3), Integer.valueOf(i3), Integer.valueOf(i4));
                DynamicScroller.this.mFlingAnimation.setStartValue(DynamicScroller.this.mHandler.mValue);
                DynamicScroller.this.mFlingAnimation.setStartVelocity(DynamicScroller.this.mHandler.mVelocity);
                float predictNaturalDest2 = DynamicScroller.this.mFlingAnimation.predictNaturalDest();
                if (((int) f2) == 0 || (predictNaturalDest2 <= i4 && predictNaturalDest2 >= i3)) {
                    OverScrollLogger.debug("fling finished, no more work.");
                    return false;
                }
                OverScrollLogger.debug("fling destination beyound boundary, start spring");
                DynamicScroller.this.resetHandler();
                DynamicScroller dynamicScroller = DynamicScroller.this;
                dynamicScroller.doSpring(2, dynamicScroller.getCurrentPosition(), DynamicScroller.this.getCurrVelocity(), DynamicScroller.this.getFinal(), i5);
                return true;
            }
        });
        this.mHandler.setMinValue(min);
        this.mHandler.setMaxValue(max);
        this.mHandler.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSpring(int i, int i2, float f, int i3, int i4) {
        if (f > 8000.0f) {
            OverScrollLogger.debug("%f is too fast for spring, slow down", Float.valueOf(f));
            f = 8000.0f;
        }
        setFinished(false);
        setCurrVelocity(f);
        setStartTime(AnimationUtils.currentAnimationTimeMillis());
        setCurrentPosition(i2);
        setStart(i2);
        setDuration(Integer.MAX_VALUE);
        setFinal(i3);
        setState(i);
        this.mHandler = new OverScrollHandler(this.mSpringAnimation, i2, f);
        this.mSpringAnimation.getSpring().setFinalPosition(this.mHandler.getOffset(i3));
        if (i4 != 0) {
            if (f < 0.0f) {
                this.mHandler.setMinValue(i3 - i4);
                this.mHandler.setMaxValue(Math.max(i3, i2));
            } else {
                this.mHandler.setMinValue(Math.min(i3, i2));
                this.mHandler.setMaxValue(i3 + i4);
            }
        }
        this.mHandler.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetHandler() {
        if (this.mHandler != null) {
            OverScrollLogger.debug("resetting current handler: state(%d), anim(%s), value(%d), velocity(%f)", Integer.valueOf(getState()), this.mHandler.getAnimation().getClass().getSimpleName(), Integer.valueOf(this.mHandler.mValue), Float.valueOf(this.mHandler.mVelocity));
            this.mHandler.cancel();
            this.mHandler = null;
        }
    }

    private void startAfterEdge(int i, int i2, int i3, int i4, int i5) {
        boolean z = false;
        OverScrollLogger.debug("startAfterEdge: start(%d) velocity(%d) boundary(%d, %d) over(%d)", Integer.valueOf(i), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i5));
        if (i > i2 && i < i3) {
            setFinished(true);
            return;
        }
        boolean z2 = i > i3;
        int i6 = z2 ? i3 : i2;
        int i7 = i - i6;
        if (i4 != 0 && Integer.signum(i7) * i4 >= 0) {
            z = true;
        }
        if (z) {
            OverScrollLogger.debug("spring forward");
            doSpring(2, i, i4, i6, i5);
            return;
        }
        this.mFlingAnimation.setStartValue(i);
        float f = i4;
        this.mFlingAnimation.setStartVelocity(f);
        float predictNaturalDest = this.mFlingAnimation.predictNaturalDest();
        if ((!z2 || predictNaturalDest >= i3) && (z2 || predictNaturalDest <= i2)) {
            OverScrollLogger.debug("spring backward");
            doSpring(1, i, f, i6, i5);
            return;
        }
        OverScrollLogger.debug("fling to content");
        doFling(i, i4, i2, i3, i5);
    }

    @Override // miuix.overscroller.widget.OverScroller.SplineOverScroller
    boolean continueWhenFinished() {
        OverScrollHandler overScrollHandler = this.mHandler;
        if (overScrollHandler == null || !overScrollHandler.continueWhenFinished()) {
            return false;
        }
        OverScrollLogger.debug("checking have more work when finish");
        update();
        return true;
    }

    @Override // miuix.overscroller.widget.OverScroller.SplineOverScroller
    void finish() {
        OverScrollLogger.debug("finish scroller");
        setCurrentPosition(getFinal());
        setFinished(true);
        resetHandler();
    }

    @Override // miuix.overscroller.widget.OverScroller.SplineOverScroller
    void fling(int i, int i2, int i3, int i4, int i5) {
        OverScrollLogger.debug("FLING: start(%d) velocity(%d) boundary(%d, %d) over(%d)", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5));
        resetHandler();
        if (i2 == 0) {
            setCurrentPosition(i);
            setStart(i);
            setFinal(i);
            setDuration(0);
            setFinished(true);
            return;
        }
        updateStiffness(i2);
        if (i > i4 || i < i3) {
            startAfterEdge(i, i3, i4, i2, i5);
        } else {
            doFling(i, i2, i3, i4, i5);
        }
    }

    @Override // miuix.overscroller.widget.OverScroller.SplineOverScroller
    void notifyEdgeReached(int i, int i2, int i3) {
        if (getState() == 0) {
            if (this.mHandler != null) {
                resetHandler();
            }
            startAfterEdge(i, i2, i2, (int) getCurrVelocity(), i3);
        }
    }

    @Override // miuix.overscroller.internal.dynamicanimation.animation.FlingAnimation.FinalValueListener
    public void onFinalValueArrived(int i) {
        setFinalPosition(getStart() + i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // miuix.overscroller.widget.OverScroller.SplineOverScroller
    public void setFinalPosition(int i) {
        super.setFinalPosition(i);
    }

    @Override // miuix.overscroller.widget.OverScroller.SplineOverScroller
    boolean springback(int i, int i2, int i3) {
        OverScrollLogger.debug("SPRING_BACK start(%d) boundary(%d, %d)", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
        if (this.mHandler != null) {
            resetHandler();
        }
        if (i < i2) {
            doSpring(1, i, 0.0f, i2, 0);
        } else if (i > i3) {
            doSpring(1, i, 0.0f, i3, 0);
        } else {
            setCurrentPosition(i);
            setStart(i);
            setFinal(i);
            setDuration(0);
            setFinished(true);
        }
        return !isFinished();
    }

    @Override // miuix.overscroller.widget.OverScroller.SplineOverScroller
    boolean update() {
        OverScrollHandler overScrollHandler = this.mHandler;
        if (overScrollHandler == null) {
            OverScrollLogger.debug("no handler found, aborting");
            return false;
        }
        boolean update = overScrollHandler.update();
        setCurrentPosition(this.mHandler.mValue);
        setCurrVelocity(this.mHandler.mVelocity);
        if (getState() == 2 && Math.signum(this.mHandler.mValue) * Math.signum(this.mHandler.mVelocity) < 0.0f) {
            OverScrollLogger.debug("State Changed: BALLISTIC -> CUBIC");
            setState(1);
        }
        return !update;
    }

    public void updateStiffness(double d) {
        if (Math.abs(d) <= 5000.0d) {
            this.mSpringAnimation.getSpring().setStiffness(246.7f);
        } else {
            this.mSpringAnimation.getSpring().setStiffness(130.5f);
        }
    }
}
