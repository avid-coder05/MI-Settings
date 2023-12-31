package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

/* loaded from: classes2.dex */
public class CircEaseInOutInterpolater implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float f2 = f * 2.0f;
        if (f2 < 1.0f) {
            return ((float) (Math.sqrt(1.0f - (f2 * f2)) - 1.0d)) * (-0.5f);
        }
        float f3 = f2 - 2.0f;
        return ((float) (Math.sqrt(1.0f - (f3 * f3)) + 1.0d)) * 0.5f;
    }
}
