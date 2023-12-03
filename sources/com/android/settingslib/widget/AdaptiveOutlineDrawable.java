package com.android.settingslib.widget;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.DrawableWrapper;
import android.util.PathParser;

/* loaded from: classes2.dex */
public class AdaptiveOutlineDrawable extends DrawableWrapper {
    private Bitmap mBitmap;
    private int mInsetPx;
    Paint mOutlinePaint;
    private Path mPath;
    private int mStrokeWidth;
    private int mType;

    public AdaptiveOutlineDrawable(Resources resources, Bitmap bitmap) {
        super(new AdaptiveIconShapeDrawable(resources));
        init(resources, bitmap, 0);
    }

    private int getColor(Resources resources, int i) {
        return resources.getColor(i != 1 ? R$color.bt_outline_color : R$color.advanced_outline_color, null);
    }

    private int getDimensionPixelSize(Resources resources, int i) {
        return resources.getDimensionPixelSize(i != 1 ? R$dimen.dashboard_tile_foreground_image_inset : R$dimen.advanced_dashboard_tile_foreground_image_inset);
    }

    private void init(Resources resources, Bitmap bitmap, int i) {
        this.mType = i;
        getDrawable().setTint(-1);
        this.mPath = new Path(PathParser.createPathFromPathData(resources.getString(17039971)));
        this.mStrokeWidth = resources.getDimensionPixelSize(R$dimen.adaptive_outline_stroke);
        Paint paint = new Paint();
        this.mOutlinePaint = paint;
        paint.setColor(getColor(resources, i));
        this.mOutlinePaint.setStyle(Paint.Style.STROKE);
        this.mOutlinePaint.setStrokeWidth(this.mStrokeWidth);
        this.mOutlinePaint.setAntiAlias(true);
        this.mInsetPx = getDimensionPixelSize(resources, i);
        this.mBitmap = bitmap;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect bounds = getBounds();
        int save = canvas.save();
        canvas.scale((bounds.right - bounds.left) / 100.0f, (bounds.bottom - bounds.top) / 100.0f);
        if (this.mType == 0) {
            canvas.drawPath(this.mPath, this.mOutlinePaint);
        } else {
            canvas.drawCircle(50.0f, 50.0f, 48.0f, this.mOutlinePaint);
        }
        canvas.restoreToCount(save);
        Bitmap bitmap = this.mBitmap;
        int i = bounds.left;
        int i2 = this.mInsetPx;
        canvas.drawBitmap(bitmap, i + i2, bounds.top + i2, (Paint) null);
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mBitmap.getHeight() + (this.mInsetPx * 2);
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mBitmap.getWidth() + (this.mInsetPx * 2);
    }
}
