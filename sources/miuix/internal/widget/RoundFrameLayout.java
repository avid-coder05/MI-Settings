package miuix.internal.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import java.util.Arrays;
import miuix.appcompat.R$dimen;

/* loaded from: classes5.dex */
public class RoundFrameLayout extends FrameLayout {
    private Region mAreaRegion;
    private int mBorderColor;
    private float mBorderWidth;
    private Path mClipOutPath;
    private Path mClipPath;
    private boolean mEnableSmoothRound;
    private RectF mLayer;
    private Paint mPaint;
    private float[] mRadii;
    private float mRadius;

    public RoundFrameLayout(Context context) {
        this(context, null);
    }

    public RoundFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RoundFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mEnableSmoothRound = false;
        init();
    }

    private void init() {
        float dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_immersion_menu_background_radius);
        this.mRadius = dimensionPixelSize;
        this.mRadii = new float[]{dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize};
        this.mLayer = new RectF();
        this.mClipPath = new Path();
        this.mClipOutPath = new Path();
        this.mAreaRegion = new Region();
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(-1);
        this.mPaint.setAntiAlias(true);
    }

    private void onBorderDraw(Canvas canvas) {
        if (this.mRadii == null || this.mBorderWidth == 0.0f || Color.alpha(this.mBorderColor) == 0) {
            return;
        }
        int width = (int) this.mLayer.width();
        int height = (int) this.mLayer.height();
        RectF rectF = new RectF();
        float f = this.mBorderWidth / 2.0f;
        rectF.left = getPaddingLeft() + f;
        rectF.top = getPaddingTop() + f;
        rectF.right = (width - getPaddingRight()) - f;
        rectF.bottom = (height - getPaddingBottom()) - f;
        this.mPaint.reset();
        this.mPaint.setColor(this.mBorderColor);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(this.mBorderWidth);
        float f2 = this.mRadius - f;
        canvas.drawRoundRect(rectF, f2, f2, this.mPaint);
    }

    private void refreshRegion() {
        if (this.mRadii == null) {
            return;
        }
        int width = (int) this.mLayer.width();
        int height = (int) this.mLayer.height();
        RectF rectF = new RectF();
        rectF.left = getPaddingLeft();
        rectF.top = getPaddingTop();
        rectF.right = width - getPaddingRight();
        rectF.bottom = height - getPaddingBottom();
        this.mClipPath.reset();
        this.mClipPath.addRoundRect(rectF, this.mRadii, Path.Direction.CW);
        this.mAreaRegion.setPath(this.mClipPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        this.mClipOutPath.reset();
        this.mClipOutPath.addRect(0.0f, 0.0f, (int) this.mLayer.width(), (int) this.mLayer.height(), Path.Direction.CW);
        this.mClipOutPath.op(this.mClipPath, Path.Op.DIFFERENCE);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.mEnableSmoothRound) {
            int saveLayer = canvas.saveLayer(this.mLayer, null, 31);
            super.dispatchDraw(canvas);
            onClipDraw(canvas);
            canvas.restoreToCount(saveLayer);
        } else {
            onClipDraw(canvas);
            super.dispatchDraw(canvas);
        }
        onBorderDraw(canvas);
    }

    public void onClipDraw(Canvas canvas) {
        if (this.mRadii == null) {
            return;
        }
        if (!this.mEnableSmoothRound) {
            canvas.clipPath(this.mClipPath);
            return;
        }
        this.mPaint.setColor(-1);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawPath(this.mClipOutPath, this.mPaint);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mLayer.set(0.0f, 0.0f, i, i2);
        refreshRegion();
    }

    public void setBorder(float f, int i) {
        this.mBorderWidth = f;
        this.mBorderColor = i;
        invalidate();
    }

    public void setRadius(float f) {
        this.mRadius = f;
        setRadius(new float[]{f, f, f, f, f, f, f, f});
    }

    public void setRadius(float[] fArr) {
        if (Arrays.equals(this.mRadii, fArr)) {
            return;
        }
        this.mRadii = fArr;
        invalidate();
    }
}
