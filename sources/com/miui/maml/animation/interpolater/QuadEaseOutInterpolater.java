package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

/* loaded from: classes2.dex */
public class QuadEaseOutInterpolater implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        return (-f) * (f - 2.0f);
    }
}
