package miuix.smooth.internal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import miuix.smooth.SmoothPathProvider2;

/* loaded from: classes5.dex */
public class SmoothDrawHelper {
    private Path mClipPath;
    private RectF mLayer;
    private Path mOutterPath;
    private SmoothPathProvider2 mPathProvider;
    private float[] mRadii;
    private float mRadius;
    private Paint mStrokePaint;
    private int mStrokeWidth = 0;
    private int mStrokeColor = 0;
    private Paint mClipPaint = new Paint(1);

    public SmoothDrawHelper() {
        Paint paint = new Paint(1);
        this.mStrokePaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.mOutterPath = new Path();
        this.mClipPath = new Path();
        this.mPathProvider = new SmoothPathProvider2();
        this.mLayer = new RectF();
    }

    private Path getSmoothPathFromProvider(Path path, RectF rectF, float[] fArr, float f, float f2, float f3) {
        return this.mPathProvider.getSmoothPath(path, fArr == null ? this.mPathProvider.buildSmoothData(rectF, f, f2, f3) : this.mPathProvider.buildSmoothData(rectF, fArr, f2, f3));
    }

    public void drawMask(Canvas canvas, Xfermode xfermode) {
        this.mClipPaint.setXfermode(xfermode);
        canvas.drawPath(this.mClipPath, this.mClipPaint);
        this.mClipPaint.setXfermode(null);
    }

    public void drawStroke(Canvas canvas) {
        if ((this.mStrokeWidth == 0 || this.mStrokePaint.getAlpha() == 0 || Color.alpha(this.mStrokeColor) == 0) ? false : true) {
            canvas.save();
            this.mStrokePaint.setStrokeWidth(this.mStrokeWidth);
            this.mStrokePaint.setColor(this.mStrokeColor);
            canvas.drawPath(this.mOutterPath, this.mStrokePaint);
            canvas.restore();
        }
    }

    public float[] getRadii() {
        return this.mRadii;
    }

    public float getRadius() {
        return this.mRadius;
    }

    public Path getSmoothPath(Rect rect) {
        float f = this.mStrokeWidth != 0 && this.mStrokePaint.getAlpha() != 0 && Color.alpha(this.mStrokeColor) != 0 ? 0.5f + (this.mStrokeWidth / 2.0f) : 0.5f;
        return getSmoothPathFromProvider(new Path(), new RectF(rect), this.mRadii, this.mRadius, f, f);
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    public int getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public void onBoundsChange(Rect rect) {
        this.mLayer.set(rect.left - 0.5f, rect.top - 0.5f, rect.right + 0.5f, rect.bottom + 0.5f);
        float f = this.mStrokeWidth != 0 && this.mStrokePaint.getAlpha() != 0 && Color.alpha(this.mStrokeColor) != 0 ? 0.5f + (this.mStrokeWidth / 2.0f) : 0.5f;
        this.mOutterPath = getSmoothPathFromProvider(this.mOutterPath, this.mLayer, this.mRadii, this.mRadius, f, f);
        Path path = this.mClipPath;
        if (path != null) {
            path.reset();
        } else {
            this.mClipPath = new Path();
        }
        this.mClipPath.addRect(this.mLayer, Path.Direction.CW);
        this.mClipPath.op(this.mOutterPath, Path.Op.DIFFERENCE);
    }

    public void setAlpha(int i) {
        this.mStrokePaint.setAlpha(i);
    }

    public void setRadii(float[] fArr) {
        this.mRadii = fArr;
    }

    public void setRadius(float f) {
        this.mRadius = f;
    }

    public void setStrokeColor(int i) {
        this.mStrokeColor = i;
    }

    public void setStrokeWidth(int i) {
        this.mStrokeWidth = i;
    }
}
