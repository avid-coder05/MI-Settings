package com.google.android.setupcompat.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/* loaded from: classes2.dex */
public class StatusBarBackgroundLayout extends FrameLayout {
    private Object lastInsets;
    private Drawable statusBarBackground;

    public StatusBarBackgroundLayout(Context context) {
        super(context);
    }

    public StatusBarBackgroundLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @TargetApi(11)
    public StatusBarBackgroundLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public Drawable getStatusBarBackground() {
        return this.statusBarBackground;
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        return super.onApplyWindowInsets(windowInsets);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT < 21 || this.lastInsets != null) {
            return;
        }
        requestApplyInsets();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Object obj;
        int systemWindowInsetTop;
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT < 21 || (obj = this.lastInsets) == null || (systemWindowInsetTop = ((WindowInsets) obj).getSystemWindowInsetTop()) <= 0) {
            return;
        }
        this.statusBarBackground.setBounds(0, 0, getWidth(), systemWindowInsetTop);
        this.statusBarBackground.draw(canvas);
    }

    public void setStatusBarBackground(Drawable drawable) {
        this.statusBarBackground = drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            setWillNotDraw(drawable == null);
            setFitsSystemWindows(drawable != null);
            invalidate();
        }
    }
}
