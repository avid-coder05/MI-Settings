package com.google.android.setupdesign.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.view.View;

/* loaded from: classes2.dex */
public class DrawableLayoutDirectionHelper {
    @SuppressLint({"InlinedApi"})
    public static InsetDrawable createRelativeInsetDrawable(Drawable drawable, int i, int i2, int i3, int i4, View view) {
        return createRelativeInsetDrawable(drawable, i, i2, i3, i4, Build.VERSION.SDK_INT >= 17 && view.getLayoutDirection() == 1);
    }

    private static InsetDrawable createRelativeInsetDrawable(Drawable drawable, int i, int i2, int i3, int i4, boolean z) {
        return z ? new InsetDrawable(drawable, i3, i2, i, i4) : new InsetDrawable(drawable, i, i2, i3, i4);
    }
}
