package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

/* loaded from: classes2.dex */
public class BounceEaseInOutInterpolater implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        return f < 0.5f ? BounceEaseInInterpolater.getInterpolationImp(f * 2.0f) * 0.5f : (BounceEaseOutInterpolater.getInterpolationImp((f * 2.0f) - 1.0f) * 0.5f) + 0.5f;
    }
}
