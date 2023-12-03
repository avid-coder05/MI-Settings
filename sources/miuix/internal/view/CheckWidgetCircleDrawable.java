package miuix.internal.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* loaded from: classes5.dex */
public class CheckWidgetCircleDrawable extends Drawable {
    private boolean mHasStroke;
    private Paint mPaint;
    private float mScale;
    private int mStrokeDisableAlpha;
    private int mStrokeNormalAlpha;
    private Paint mStrokePaint;
    private int mUncheckedDisableAlpha;
    private int mUncheckedNormalAlpha;

    public CheckWidgetCircleDrawable(int i, int i2, int i3) {
        this(i, i2, i3, 0, 0, 0);
    }

    public CheckWidgetCircleDrawable(int i, int i2, int i3, int i4, int i5, int i6) {
        this.mPaint = new Paint();
        this.mStrokePaint = new Paint();
        this.mScale = 1.0f;
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(i);
        this.mUncheckedNormalAlpha = i2;
        this.mUncheckedDisableAlpha = i3;
        boolean z = i4 != 0;
        this.mHasStroke = z;
        if (z) {
            this.mStrokePaint.setAntiAlias(true);
            this.mStrokePaint.setColor(i4);
            this.mStrokePaint.setStyle(Paint.Style.STROKE);
            this.mStrokePaint.setStrokeWidth(2.0f);
        }
        this.mStrokeNormalAlpha = i5;
        this.mStrokeDisableAlpha = i6;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int i = bounds.right;
        int i2 = bounds.left;
        int i3 = bounds.top;
        int i4 = bounds.bottom;
        int i5 = (i3 + i4) / 2;
        int min = Math.min(i - i2, i4 - i3) / 2;
        float f = (i + i2) / 2;
        float f2 = i5;
        float f3 = min;
        canvas.drawCircle(f, f2, (this.mScale * f3) - 1.0f, this.mPaint);
        if (this.mHasStroke) {
            canvas.drawCircle(f, f2, ((f3 * this.mScale) - 1.0f) - 1.0f, this.mStrokePaint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mPaint.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
        if (this.mHasStroke) {
            if (i == this.mUncheckedNormalAlpha) {
                this.mStrokePaint.setAlpha(this.mStrokeNormalAlpha);
            } else if (i == this.mUncheckedDisableAlpha) {
                this.mStrokePaint.setAlpha(this.mStrokeDisableAlpha);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public void setScale(float f) {
        this.mScale = f;
    }
}
