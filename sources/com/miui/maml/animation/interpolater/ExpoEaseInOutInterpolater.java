package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

/* loaded from: classes2.dex */
public class ExpoEaseInOutInterpolater implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        if (f == 0.0f) {
            return 0.0f;
        }
        if (f == 1.0f) {
            return 1.0f;
        }
        return ((float) (f * 2.0f < 1.0f ? Math.pow(2.0d, (r5 - 1.0f) * 10.0f) : (-Math.pow(2.0d, (r5 - 1.0f) * (-10.0f))) + 2.0d)) * 0.5f;
    }
}
