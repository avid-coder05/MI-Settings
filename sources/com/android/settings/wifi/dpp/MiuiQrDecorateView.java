package com.android.settings.wifi.dpp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class MiuiQrDecorateView extends View {
    private final int mBackgroundColor;
    private final Paint mBackgroundPaint;
    private Paint mBorderPaint;
    private float mBorderThickness;
    private final int mCornerColor;
    private float mCornerLength;
    private Paint mCornerPaint;
    private float mCornerThickness;
    private boolean mFocused;
    private final int mFocusedCornerColor;
    private Paint mGuideLinePaint;
    private int mHeight;
    private float mInnerBottom;
    private RectF mInnerFrame;
    private float mInnerLeft;
    private final float mInnerRidus;
    private float mInnerRight;
    private float mInnerTop;
    private Bitmap mMaskBitmap;
    private Canvas mMaskCanvas;
    private RectF mOuterFrame;
    private final float mRadius;
    private final Paint mStrokePaint;
    private final Paint mTransparentPaint;
    private int mWidth;

    public MiuiQrDecorateView(Context context) {
        this(context, null);
    }

    public MiuiQrDecorateView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MiuiQrDecorateView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public MiuiQrDecorateView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mWidth = -1;
        this.mHeight = -1;
        this.mFocused = false;
        this.mRadius = TypedValue.applyDimension(1, 16.0f, getResources().getDisplayMetrics());
        this.mInnerRidus = TypedValue.applyDimension(1, 15.0f, getResources().getDisplayMetrics());
        this.mCornerColor = context.getResources().getColor(R.color.qr_code_corner_line_color);
        this.mFocusedCornerColor = context.getResources().getColor(R.color.qr_code_focused_corner_line_color);
        int color = context.getResources().getColor(R.color.qr_code_background_color);
        this.mBackgroundColor = color;
        Paint paint = new Paint();
        this.mStrokePaint = paint;
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.mTransparentPaint = paint2;
        paint2.setAntiAlias(true);
        paint2.setColor(getResources().getColor(17170445));
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Resources resources = getResources();
        int i3 = R.dimen.dpp_corner_thickness;
        this.mCornerThickness = resources.getDimension(i3);
        this.mBorderThickness = getResources().getDimension(R.dimen.dpp_border_thickness);
        this.mCornerLength = getResources().getDimension(R.dimen.dpp_corner_length);
        Paint paint3 = new Paint();
        this.mCornerPaint = paint3;
        paint3.setStyle(Paint.Style.STROKE);
        this.mCornerPaint.setStrokeWidth(getResources().getDimension(i3));
        this.mCornerPaint.setColor(getResources().getColor(R.color.qr_scanner_corner));
        Paint paint4 = new Paint();
        this.mBorderPaint = paint4;
        paint4.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setStrokeWidth(this.mBorderThickness);
        this.mBorderPaint.setColor(getResources().getColor(R.color.qr_scanner_border));
        Paint paint5 = new Paint();
        this.mGuideLinePaint = paint5;
        paint5.setStyle(Paint.Style.STROKE);
        this.mGuideLinePaint.setStrokeWidth(getResources().getDimension(R.dimen.dpp_guideline_thickness));
        this.mGuideLinePaint.setColor(getResources().getColor(R.color.qr_scanner_guide_line));
        Paint paint6 = new Paint();
        this.mBackgroundPaint = paint6;
        paint6.setColor(color);
    }

    private void calculateFramePos() {
        int i = this.mWidth / 2;
        int i2 = this.mHeight / 2;
        float applyDimension = TypedValue.applyDimension(1, 264.0f, getResources().getDisplayMetrics()) / 2.0f;
        float applyDimension2 = TypedValue.applyDimension(1, 1.0f, getResources().getDisplayMetrics());
        float f = i;
        float f2 = i2;
        this.mOuterFrame = new RectF(f - applyDimension, f2 - applyDimension, f + applyDimension, f2 + applyDimension);
        RectF rectF = this.mOuterFrame;
        this.mInnerFrame = new RectF(rectF.left + applyDimension2, rectF.top + applyDimension2, rectF.right - applyDimension2, rectF.bottom - applyDimension2);
        RectF rectF2 = this.mOuterFrame;
        this.mInnerLeft = rectF2.left + applyDimension2;
        this.mInnerTop = rectF2.top + applyDimension2;
        this.mInnerRight = rectF2.right - applyDimension2;
        this.mInnerBottom = rectF2.bottom - applyDimension2;
    }

    private void drawCorners(Canvas canvas) {
        float f = this.mInnerLeft;
        float f2 = this.mInnerTop;
        float f3 = this.mInnerRight;
        float f4 = this.mInnerBottom;
        float f5 = this.mCornerThickness;
        float f6 = this.mBorderThickness;
        float f7 = (f5 - f6) / 2.0f;
        float f8 = f5 - (f6 / 2.0f);
        float f9 = f - f7;
        float f10 = f2 - f8;
        canvas.drawLine(f9, f10, f9, f2 + this.mCornerLength, this.mCornerPaint);
        float f11 = f - f8;
        float f12 = f2 - f7;
        canvas.drawLine(f11, f12, f + this.mCornerLength, f12, this.mCornerPaint);
        float f13 = f3 + f7;
        canvas.drawLine(f13, f10, f13, f2 + this.mCornerLength, this.mCornerPaint);
        float f14 = f3 + f8;
        canvas.drawLine(f14, f12, f3 - this.mCornerLength, f12, this.mCornerPaint);
        float f15 = f4 + f8;
        canvas.drawLine(f9, f15, f9, f4 - this.mCornerLength, this.mCornerPaint);
        float f16 = f4 + f7;
        canvas.drawLine(f11, f16, f + this.mCornerLength, f16, this.mCornerPaint);
        canvas.drawLine(f13, f15, f13, f4 - this.mCornerLength, this.mCornerPaint);
        canvas.drawLine(f14, f16, f3 - this.mCornerLength, f16, this.mCornerPaint);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mStrokePaint.setColor(this.mFocused ? this.mFocusedCornerColor : this.mCornerColor);
        this.mMaskCanvas.drawColor(this.mBackgroundColor);
        Canvas canvas2 = this.mMaskCanvas;
        RectF rectF = this.mOuterFrame;
        float f = this.mRadius;
        canvas2.drawRoundRect(rectF, f, f, this.mStrokePaint);
        this.mMaskCanvas.drawRect(this.mInnerFrame, this.mTransparentPaint);
        drawCorners(this.mMaskCanvas);
        canvas.drawBitmap(this.mMaskBitmap, 0.0f, 0.0f, this.mBackgroundPaint);
        super.onDraw(canvas);
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mMaskBitmap == null) {
            this.mMaskBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            this.mMaskCanvas = new Canvas(this.mMaskBitmap);
        }
        if (this.mWidth == -1) {
            this.mWidth = getWidth();
        }
        if (this.mHeight == -1) {
            this.mHeight = getHeight();
        }
        calculateFramePos();
    }

    public void setFocused(boolean z) {
        this.mFocused = z;
        invalidate();
    }
}
