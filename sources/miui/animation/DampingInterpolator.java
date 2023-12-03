package miui.animation;

import android.animation.TimeInterpolator;

/* loaded from: classes3.dex */
public class DampingInterpolator implements TimeInterpolator {
    private final double mAtanValue;
    private final float mFactor;

    public DampingInterpolator(float f) {
        this.mFactor = f;
        this.mAtanValue = Math.atan(f);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        return (float) (Math.atan(this.mFactor * f) / this.mAtanValue);
    }
}
