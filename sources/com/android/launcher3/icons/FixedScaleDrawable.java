package com.android.launcher3.icons;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: classes.dex */
public class FixedScaleDrawable extends DrawableWrapper {
    private float mScaleX;
    private float mScaleY;

    public FixedScaleDrawable() {
        super(new ColorDrawable());
        this.mScaleX = 0.46669f;
        this.mScaleY = 0.46669f;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int save = canvas.save();
        canvas.scale(this.mScaleX, this.mScaleY, getBounds().exactCenterX(), getBounds().exactCenterY());
        super.draw(canvas);
        canvas.restoreToCount(save);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) {
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
    }

    public void setScale(float f) {
        float intrinsicHeight = getIntrinsicHeight();
        float intrinsicWidth = getIntrinsicWidth();
        float f2 = f * 0.46669f;
        this.mScaleX = f2;
        this.mScaleY = f2;
        if (intrinsicHeight > intrinsicWidth && intrinsicWidth > 0.0f) {
            this.mScaleX = f2 * (intrinsicWidth / intrinsicHeight);
        } else if (intrinsicWidth <= intrinsicHeight || intrinsicHeight <= 0.0f) {
        } else {
            this.mScaleY = f2 * (intrinsicHeight / intrinsicWidth);
        }
    }
}
