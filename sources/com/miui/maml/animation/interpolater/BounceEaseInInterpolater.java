package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

/* loaded from: classes2.dex */
public class BounceEaseInInterpolater implements Interpolator {
    public static float getInterpolationImp(float f) {
        return 1.0f - BounceEaseOutInterpolater.getInterpolationImp(1.0f - f);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        return getInterpolationImp(f);
    }
}
