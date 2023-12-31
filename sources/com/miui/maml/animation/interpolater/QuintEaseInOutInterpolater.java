package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

/* loaded from: classes2.dex */
public class QuintEaseInOutInterpolater implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float f2 = f * 2.0f;
        if (f2 < 1.0f) {
            return 0.5f * f2 * f2 * f2 * f2 * f2;
        }
        float f3 = f2 - 2.0f;
        return ((f3 * f3 * f3 * f3 * f3) + 2.0f) * 0.5f;
    }
}
