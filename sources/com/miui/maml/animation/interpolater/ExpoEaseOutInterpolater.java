package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

/* loaded from: classes2.dex */
public class ExpoEaseOutInterpolater implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        if (f == 1.0f) {
            return 1.0f;
        }
        return (float) ((-Math.pow(2.0d, f * (-10.0f))) + 1.0d);
    }
}
