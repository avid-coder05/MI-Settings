package com.xiaomi.mirror.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.xiaomi.mirror.opensdk.R;

/* loaded from: classes2.dex */
public class MiuiDragShadowBuilder extends View.DragShadowBuilder {
    private Attributes mAttrs;
    private int mBorderLevel;
    private Rect mBound;
    private int mCount;
    private Rect mDest;
    private Bitmap mThumb;

    /* loaded from: classes2.dex */
    public static class Attributes {
        public int thumbnailBorderColor;
        public int thumbnailBorderLevel;
        public int thumbnailBorderRoundOval;
        public int thumbnailBorderWidth;
        public int thumbnailCountColor;
        public int thumbnailCountMaxSize;
        public int thumbnailHeight;
        public int thumbnailMaskColor;
        public int thumbnailMaxWidth;
        public int thumbnailMinWidth;
        public int thumbnailShadowLayerColor;
        public int thumbnailShadowLayerRadius;
        public int thumbnailShadowLayerX;
        public int thumbnailShadowLayerY;
    }

    public MiuiDragShadowBuilder(Context context) {
        this(context, null);
    }

    public MiuiDragShadowBuilder(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MiuiDragShadowBuilder(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.MiuiDragShadowStyle);
    }

    public MiuiDragShadowBuilder(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.MiuiDragShadow, i, i2);
        Attributes attributes = new Attributes();
        this.mAttrs = attributes;
        attributes.thumbnailHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuiDragShadow_thumbnailHeight, 256);
        this.mAttrs.thumbnailMinWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuiDragShadow_thumbnailMinWidth, 98);
        this.mAttrs.thumbnailMaxWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuiDragShadow_thumbnailMaxWidth, 600);
        this.mAttrs.thumbnailBorderWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuiDragShadow_thumbnailBorderWidth, 8);
        this.mAttrs.thumbnailBorderColor = obtainStyledAttributes.getColor(R.styleable.MiuiDragShadow_thumbnailBorderColor, -1);
        this.mAttrs.thumbnailBorderLevel = obtainStyledAttributes.getInt(R.styleable.MiuiDragShadow_thumbnailBorderLevel, 3);
        this.mAttrs.thumbnailBorderRoundOval = obtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuiDragShadow_thumbnailBorderRoundOval, 4);
        this.mAttrs.thumbnailShadowLayerRadius = obtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuiDragShadow_thumbnailShadowLayerRadius, 150);
        this.mAttrs.thumbnailShadowLayerX = obtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuiDragShadow_thumbnailShadowLayerX, 0);
        this.mAttrs.thumbnailShadowLayerY = obtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuiDragShadow_thumbnailShadowLayerY, 10);
        this.mAttrs.thumbnailShadowLayerColor = obtainStyledAttributes.getColor(R.styleable.MiuiDragShadow_thumbnailShadowLayerColor, 1140850688);
        this.mAttrs.thumbnailMaskColor = obtainStyledAttributes.getColor(R.styleable.MiuiDragShadow_thumbnailMaskColor, 1291845632);
        this.mAttrs.thumbnailCountMaxSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuiDragShadow_thumbnailCountMaxSize, 126);
        this.mAttrs.thumbnailCountColor = obtainStyledAttributes.getColor(R.styleable.MiuiDragShadow_thumbnailCountColor, -1);
        obtainStyledAttributes.recycle();
    }

    public MiuiDragShadowBuilder(Attributes attributes) {
        this.mAttrs = attributes;
    }

    private void computeIfNeeded() {
        if (this.mBorderLevel == 0 || this.mBound == null || this.mDest == null) {
            this.mBound = new Rect();
            this.mDest = new Rect();
            this.mBorderLevel = Math.min(this.mCount, this.mAttrs.thumbnailBorderLevel);
            Attributes attributes = this.mAttrs;
            int i = attributes.thumbnailHeight;
            int width = (int) (i * (this.mThumb.getWidth() / this.mThumb.getHeight()));
            int i2 = attributes.thumbnailMinWidth;
            if (width < i2) {
                width = i2;
            } else {
                int i3 = attributes.thumbnailMaxWidth;
                if (width > i3) {
                    width = i3;
                }
            }
            this.mDest.set(0, 0, width, i);
            Attributes attributes2 = this.mAttrs;
            int i4 = attributes2.thumbnailBorderWidth;
            int i5 = this.mBorderLevel;
            int i6 = width + ((i5 + 1) * i4);
            int i7 = i + (i4 * (i5 + 1));
            int i8 = attributes2.thumbnailShadowLayerRadius;
            if (i8 == 0) {
                this.mBound.set(0, 0, i6, i7);
                return;
            }
            Rect rect = this.mBound;
            int min = Math.min(0, attributes2.thumbnailShadowLayerX - i8);
            Attributes attributes3 = this.mAttrs;
            int min2 = Math.min(0, attributes3.thumbnailShadowLayerY - attributes3.thumbnailShadowLayerRadius);
            Attributes attributes4 = this.mAttrs;
            int max = Math.max(i6, attributes4.thumbnailShadowLayerX + i6 + attributes4.thumbnailShadowLayerRadius);
            Attributes attributes5 = this.mAttrs;
            rect.set(min, min2, max, Math.max(i7, attributes5.thumbnailShadowLayerY + i7 + attributes5.thumbnailShadowLayerRadius));
        }
    }

    @Deprecated
    public void build() {
    }

    @Override // android.view.View.DragShadowBuilder
    public void onDrawShadow(Canvas canvas) {
        computeIfNeeded();
        Paint paint = new Paint();
        paint.setColor(this.mAttrs.thumbnailBorderColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(this.mAttrs.thumbnailCountMaxSize);
        Attributes attributes = this.mAttrs;
        int i = attributes.thumbnailShadowLayerRadius;
        if (i != 0) {
            paint.setShadowLayer(i, attributes.thumbnailShadowLayerX, attributes.thumbnailShadowLayerY, attributes.thumbnailShadowLayerColor);
        }
        canvas.save();
        Rect rect = this.mBound;
        canvas.translate(-rect.left, -rect.top);
        RectF rectF = new RectF(0.0f, 0.0f, this.mDest.width() + (this.mAttrs.thumbnailBorderWidth * 2), this.mDest.height() + (this.mAttrs.thumbnailBorderWidth * 2));
        canvas.save();
        int i2 = this.mAttrs.thumbnailBorderWidth;
        int i3 = this.mBorderLevel;
        canvas.translate((i3 - 1) * i2, i2 * (i3 - 1));
        for (int i4 = 0; i4 < this.mBorderLevel; i4++) {
            int i5 = this.mAttrs.thumbnailBorderRoundOval;
            canvas.drawRoundRect(rectF, i5, i5, paint);
            int i6 = this.mAttrs.thumbnailBorderWidth;
            canvas.translate(-i6, -i6);
        }
        canvas.restore();
        int i7 = this.mAttrs.thumbnailBorderWidth;
        canvas.translate(i7, i7);
        canvas.drawBitmap(this.mThumb, (Rect) null, this.mDest, (Paint) null);
        if (this.mCount > 1) {
            Paint paint2 = new Paint();
            paint2.setColor(this.mAttrs.thumbnailMaskColor);
            canvas.drawRect(this.mDest, paint2);
            Rect rect2 = new Rect();
            Paint paint3 = new Paint();
            paint3.setColor(this.mAttrs.thumbnailCountColor);
            paint3.setTextSize(this.mAttrs.thumbnailCountMaxSize);
            String num = Integer.toString(this.mCount);
            paint.getTextBounds(num, 0, num.length(), rect2);
            if (rect2.width() > this.mDest.width() - (this.mAttrs.thumbnailBorderWidth * 2)) {
                paint3.setTextSize((paint3.getTextSize() / rect2.width()) * (this.mDest.width() - (this.mAttrs.thumbnailBorderWidth * 2)));
                paint3.getTextBounds(num, 0, num.length(), rect2);
            }
            canvas.translate(this.mDest.centerX() - rect2.centerX(), this.mDest.centerY() - rect2.centerY());
            canvas.drawText(num, 0.0f, 0.0f, paint3);
        }
        canvas.restore();
    }

    @Override // android.view.View.DragShadowBuilder
    public void onProvideShadowMetrics(Point point, Point point2) {
        computeIfNeeded();
        point.set(this.mBound.width(), this.mBound.height());
        point2.set(this.mDest.centerX() - this.mBound.left, this.mDest.centerY() - this.mBound.top);
    }

    public void setCount(int i) {
        this.mCount = i;
    }

    public void setThumb(Bitmap bitmap) {
        this.mThumb = bitmap;
    }
}
