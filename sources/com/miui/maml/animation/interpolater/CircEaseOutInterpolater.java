package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

/* loaded from: classes2.dex */
public class CircEaseOutInterpolater implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float f2 = f - 1.0f;
        return (float) Math.sqrt(1.0f - (f2 * f2));
    }
}
