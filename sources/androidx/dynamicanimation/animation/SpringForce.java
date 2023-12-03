package androidx.dynamicanimation.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;

/* loaded from: classes.dex */
public final class SpringForce {
    private double mDampedFreq;
    double mDampingRatio;
    private double mFinalPosition;
    private double mGammaMinus;
    private double mGammaPlus;
    private boolean mInitialized;
    private final DynamicAnimation.MassState mMassState;
    double mNaturalFreq;
    private double mValueThreshold;
    private double mVelocityThreshold;

    public SpringForce() {
        this.mNaturalFreq = Math.sqrt(1500.0d);
        this.mDampingRatio = 0.5d;
        this.mInitialized = false;
        this.mFinalPosition = Double.MAX_VALUE;
        this.mMassState = new DynamicAnimation.MassState();
    }

    public SpringForce(float finalPosition) {
        this.mNaturalFreq = Math.sqrt(1500.0d);
        this.mDampingRatio = 0.5d;
        this.mInitialized = false;
        this.mFinalPosition = Double.MAX_VALUE;
        this.mMassState = new DynamicAnimation.MassState();
        this.mFinalPosition = finalPosition;
    }

    private void init() {
        if (this.mInitialized) {
            return;
        }
        if (this.mFinalPosition == Double.MAX_VALUE) {
            throw new IllegalStateException("Error: Final position of the spring must be set before the animation starts");
        }
        double d = this.mDampingRatio;
        if (d > 1.0d) {
            double d2 = this.mNaturalFreq;
            this.mGammaPlus = ((-d) * d2) + (d2 * Math.sqrt((d * d) - 1.0d));
            double d3 = this.mDampingRatio;
            double d4 = this.mNaturalFreq;
            this.mGammaMinus = ((-d3) * d4) - (d4 * Math.sqrt((d3 * d3) - 1.0d));
        } else if (d >= 0.0d && d < 1.0d) {
            this.mDampedFreq = this.mNaturalFreq * Math.sqrt(1.0d - (d * d));
        }
        this.mInitialized = true;
    }

    public float getFinalPosition() {
        return (float) this.mFinalPosition;
    }

    public boolean isAtEquilibrium(float value, float velocity) {
        return ((double) Math.abs(velocity)) < this.mVelocityThreshold && ((double) Math.abs(value - getFinalPosition())) < this.mValueThreshold;
    }

    public SpringForce setDampingRatio(float dampingRatio) {
        if (dampingRatio >= 0.0f) {
            this.mDampingRatio = dampingRatio;
            this.mInitialized = false;
            return this;
        }
        throw new IllegalArgumentException("Damping ratio must be non-negative");
    }

    public SpringForce setFinalPosition(float finalPosition) {
        this.mFinalPosition = finalPosition;
        return this;
    }

    public SpringForce setStiffness(float stiffness) {
        if (stiffness > 0.0f) {
            this.mNaturalFreq = Math.sqrt(stiffness);
            this.mInitialized = false;
            return this;
        }
        throw new IllegalArgumentException("Spring stiffness constant must be positive.");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setValueThreshold(double threshold) {
        double abs = Math.abs(threshold);
        this.mValueThreshold = abs;
        this.mVelocityThreshold = abs * 62.5d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DynamicAnimation.MassState updateValues(double lastDisplacement, double lastVelocity, long timeElapsed) {
        double cos;
        double d;
        init();
        double d2 = timeElapsed / 1000.0d;
        double d3 = lastDisplacement - this.mFinalPosition;
        double d4 = this.mDampingRatio;
        if (d4 > 1.0d) {
            double d5 = this.mGammaMinus;
            double d6 = this.mGammaPlus;
            double d7 = d3 - (((d5 * d3) - lastVelocity) / (d5 - d6));
            double d8 = ((d3 * d5) - lastVelocity) / (d5 - d6);
            d = (Math.pow(2.718281828459045d, d5 * d2) * d7) + (Math.pow(2.718281828459045d, this.mGammaPlus * d2) * d8);
            double d9 = this.mGammaMinus;
            double pow = d7 * d9 * Math.pow(2.718281828459045d, d9 * d2);
            double d10 = this.mGammaPlus;
            cos = pow + (d8 * d10 * Math.pow(2.718281828459045d, d10 * d2));
        } else if (d4 == 1.0d) {
            double d11 = this.mNaturalFreq;
            double d12 = lastVelocity + (d11 * d3);
            double d13 = d3 + (d12 * d2);
            d = Math.pow(2.718281828459045d, (-d11) * d2) * d13;
            double pow2 = d13 * Math.pow(2.718281828459045d, (-this.mNaturalFreq) * d2);
            double d14 = this.mNaturalFreq;
            cos = (d12 * Math.pow(2.718281828459045d, (-d14) * d2)) + (pow2 * (-d14));
        } else {
            double d15 = 1.0d / this.mDampedFreq;
            double d16 = this.mNaturalFreq;
            double d17 = d15 * ((d4 * d16 * d3) + lastVelocity);
            double pow3 = Math.pow(2.718281828459045d, (-d4) * d16 * d2) * ((Math.cos(this.mDampedFreq * d2) * d3) + (Math.sin(this.mDampedFreq * d2) * d17));
            double d18 = this.mNaturalFreq;
            double d19 = this.mDampingRatio;
            double d20 = (-d18) * pow3 * d19;
            double pow4 = Math.pow(2.718281828459045d, (-d19) * d18 * d2);
            double d21 = this.mDampedFreq;
            double sin = (-d21) * d3 * Math.sin(d21 * d2);
            double d22 = this.mDampedFreq;
            cos = d20 + (pow4 * (sin + (d17 * d22 * Math.cos(d22 * d2))));
            d = pow3;
        }
        DynamicAnimation.MassState massState = this.mMassState;
        massState.mValue = (float) (d + this.mFinalPosition);
        massState.mVelocity = (float) cos;
        return massState;
    }
}
