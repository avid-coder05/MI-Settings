package miuix.animation.physics;

import miuix.animation.IAnimTarget;
import miuix.animation.property.FloatProperty;

/* loaded from: classes5.dex */
public class EquilibriumChecker {
    private double mTargetValue = Double.MAX_VALUE;
    private float mValueThreshold;
    private float mVelocityThreshold;

    private boolean isAt(double d, double d2) {
        return Math.abs(this.mTargetValue) == 3.4028234663852886E38d || Math.abs(d - d2) < ((double) this.mValueThreshold);
    }

    public void init(IAnimTarget iAnimTarget, FloatProperty floatProperty, double d) {
        float minVisibleChange = iAnimTarget.getMinVisibleChange(floatProperty) * 0.75f;
        this.mValueThreshold = minVisibleChange;
        this.mVelocityThreshold = minVisibleChange * 16.666666f;
        this.mTargetValue = d;
    }

    public boolean isAtEquilibrium(int i, double d, double d2) {
        return (i != -2 || isAt(d, this.mTargetValue)) && i != -3 && Math.abs(d2) < ((double) this.mVelocityThreshold);
    }
}
