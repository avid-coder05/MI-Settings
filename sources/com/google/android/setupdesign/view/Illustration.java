package com.google.android.setupdesign.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.google.android.setupdesign.R$styleable;

/* loaded from: classes2.dex */
public class Illustration extends FrameLayout {
    private float aspectRatio;
    private Drawable background;
    private float baselineGridSize;
    private Drawable illustration;
    private final Rect illustrationBounds;
    private float scale;
    private final Rect viewBounds;

    public Illustration(Context context) {
        super(context);
        this.viewBounds = new Rect();
        this.illustrationBounds = new Rect();
        this.scale = 1.0f;
        this.aspectRatio = 0.0f;
        init(null, 0);
    }

    public Illustration(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.viewBounds = new Rect();
        this.illustrationBounds = new Rect();
        this.scale = 1.0f;
        this.aspectRatio = 0.0f;
        init(attributeSet, 0);
    }

    @TargetApi(11)
    public Illustration(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.viewBounds = new Rect();
        this.illustrationBounds = new Rect();
        this.scale = 1.0f;
        this.aspectRatio = 0.0f;
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        if (isInEditMode()) {
            return;
        }
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SudIllustration, i, 0);
            this.aspectRatio = obtainStyledAttributes.getFloat(R$styleable.SudIllustration_sudAspectRatio, 0.0f);
            obtainStyledAttributes.recycle();
        }
        this.baselineGridSize = getResources().getDisplayMetrics().density * 8.0f;
        setWillNotDraw(false);
    }

    private boolean shouldMirrorDrawable(Drawable drawable, int i) {
        if (i == 1) {
            int i2 = Build.VERSION.SDK_INT;
            return i2 >= 19 ? drawable.isAutoMirrored() : i2 >= 17 && (getContext().getApplicationInfo().flags & MiuiWindowManager$LayoutParams.EXTRA_FLAG_ACQUIRES_SLEEP_TOKEN) != 0;
        }
        return false;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.background != null) {
            canvas.save();
            canvas.translate(0.0f, this.illustrationBounds.height());
            float f = this.scale;
            canvas.scale(f, f, 0.0f, 0.0f);
            if (Build.VERSION.SDK_INT > 17 && shouldMirrorDrawable(this.background, getLayoutDirection())) {
                canvas.scale(-1.0f, 1.0f);
                canvas.translate(-this.background.getBounds().width(), 0.0f);
            }
            this.background.draw(canvas);
            canvas.restore();
        }
        if (this.illustration != null) {
            canvas.save();
            if (Build.VERSION.SDK_INT > 17 && shouldMirrorDrawable(this.illustration, getLayoutDirection())) {
                canvas.scale(-1.0f, 1.0f);
                canvas.translate(-this.illustrationBounds.width(), 0.0f);
            }
            this.illustration.draw(canvas);
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        Drawable drawable = this.illustration;
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = this.illustration.getIntrinsicHeight();
            this.viewBounds.set(0, 0, i5, i6);
            if (this.aspectRatio != 0.0f) {
                float f = i5 / intrinsicWidth;
                this.scale = f;
                intrinsicHeight = (int) (intrinsicHeight * f);
                intrinsicWidth = i5;
            }
            Gravity.apply(55, intrinsicWidth, intrinsicHeight, this.viewBounds, this.illustrationBounds);
            this.illustration.setBounds(this.illustrationBounds);
        }
        Drawable drawable2 = this.background;
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, (int) Math.ceil(i5 / this.scale), (int) Math.ceil((i6 - this.illustrationBounds.height()) / this.scale));
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.aspectRatio != 0.0f) {
            float size = (int) (View.MeasureSpec.getSize(i) / this.aspectRatio);
            setPadding(0, (int) (size - (size % this.baselineGridSize)), 0, 0);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(ViewOutlineProvider.BOUNDS);
        }
        super.onMeasure(i, i2);
    }

    public void setAspectRatio(float f) {
        this.aspectRatio = f;
        invalidate();
        requestLayout();
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        if (drawable == this.background) {
            return;
        }
        this.background = drawable;
        invalidate();
        requestLayout();
    }

    @Override // android.view.View
    @Deprecated
    public void setForeground(Drawable drawable) {
        setIllustration(drawable);
    }

    public void setIllustration(Drawable drawable) {
        if (drawable == this.illustration) {
            return;
        }
        this.illustration = drawable;
        invalidate();
        requestLayout();
    }
}
