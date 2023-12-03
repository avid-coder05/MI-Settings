package com.android.settings.haptic.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import miuix.smooth.SmoothFrameLayout;

/* loaded from: classes.dex */
public class CustomSmoothFrameLayout extends SmoothFrameLayout {
    public CustomSmoothFrameLayout(Context context) {
        super(context);
    }

    public CustomSmoothFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomSmoothFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    public void setBackground(Drawable drawable) {
        super.setBackground(null);
    }
}
