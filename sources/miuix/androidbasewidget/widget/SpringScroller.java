package miuix.androidbasewidget.widget;

import android.view.animation.AnimationUtils;

/* loaded from: classes5.dex */
public class SpringScroller {
    private double mCurrX;
    private double mCurrY;
    private long mCurrentTime;
    private double mEndX;
    private double mEndY;
    private boolean mFinished = true;
    private boolean mLastStep;
    private int mOrientation;
    private double mOriginStartY;
    private SpringOperator mSpringOperator;
    private long mStartTime;
    private double mStartX;
    private double mStartY;
    private double mVelocity;

    public final void abortAnimation() {
        this.mFinished = true;
    }

    public boolean computeScrollOffset() {
        if (this.mSpringOperator == null || this.mFinished) {
            return false;
        }
        if (this.mLastStep) {
            this.mFinished = true;
            this.mCurrY = this.mEndY;
            this.mCurrX = this.mEndX;
            return true;
        }
        long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        this.mCurrentTime = currentAnimationTimeMillis;
        float min = Math.min(((float) (currentAnimationTimeMillis - this.mStartTime)) / 1000.0f, 0.016f);
        float f = min != 0.0f ? min : 0.016f;
        this.mStartTime = this.mCurrentTime;
        if (this.mOrientation == 2) {
            double updateVelocity = this.mSpringOperator.updateVelocity(this.mVelocity, f, this.mEndY, this.mStartY);
            double d = this.mStartY + (f * updateVelocity);
            this.mCurrY = d;
            this.mVelocity = updateVelocity;
            if (isAtEquilibrium(d, this.mEndY)) {
                this.mLastStep = true;
            } else {
                this.mStartY = this.mCurrY;
            }
        } else {
            double updateVelocity2 = this.mSpringOperator.updateVelocity(this.mVelocity, f, this.mEndX, this.mStartX);
            double d2 = this.mStartX + (f * updateVelocity2);
            this.mCurrX = d2;
            this.mVelocity = updateVelocity2;
            if (isAtEquilibrium(d2, this.mEndX)) {
                this.mLastStep = true;
            } else {
                this.mStartX = this.mCurrX;
            }
        }
        return true;
    }

    public final int getCurrX() {
        return (int) this.mCurrX;
    }

    public final int getCurrY() {
        return (int) this.mCurrY;
    }

    public final int getFinalX() {
        return (int) this.mEndX;
    }

    public final int getStartX() {
        return (int) this.mStartX;
    }

    public boolean isAtEquilibrium(double d, double d2) {
        return Math.abs(d - d2) < 1.0d;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public void setFinalX(int i) {
        this.mEndX = i;
        this.mFinished = false;
    }

    public void startScroll(float f, float f2, float f3, float f4, float f5) {
        this.mFinished = false;
        this.mLastStep = false;
        this.mStartX = f;
        this.mEndX = f2;
        double d = f3;
        this.mStartY = d;
        this.mOriginStartY = d;
        this.mCurrY = (int) d;
        this.mEndY = f4;
        double d2 = f5;
        this.mVelocity = d2;
        if (Math.abs(d2) <= 5000.0d) {
            this.mSpringOperator = new SpringOperator(0.9f, 0.35f);
        } else {
            this.mSpringOperator = new SpringOperator(0.9f, 0.35f);
        }
        this.mOrientation = Math.abs(f4 - f3) > Math.abs(f2 - f) ? 2 : 1;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
    }
}
