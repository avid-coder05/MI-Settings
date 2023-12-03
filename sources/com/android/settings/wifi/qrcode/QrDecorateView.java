package com.android.settings.wifi.qrcode;

import android.content.Context;
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
public class QrDecorateView extends View {
    private final int mBackgroundColor;
    private final Paint mBackgroundPaint;
    private final int mCornerColor;
    private boolean mFocused;
    private final int mFocusedCornerColor;
    private int mHeight;
    private RectF mInnerFrame;
    private final float mInnerRidus;
    private Bitmap mMaskBitmap;
    private Canvas mMaskCanvas;
    private RectF mOuterFrame;
    private final float mRadius;
    private final Paint mStrokePaint;
    private final Paint mTransparentPaint;
    private int mWidth;

    public QrDecorateView(Context context) {
        this(context, null);
    }

    public QrDecorateView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public QrDecorateView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public QrDecorateView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mWidth = -1;
        this.mHeight = -1;
        this.mFocused = false;
        this.mRadius = TypedValue.applyDimension(1, 16.0f, getResources().getDisplayMetrics());
        this.mInnerRidus = TypedValue.applyDimension(1, 12.0f, getResources().getDisplayMetrics());
        this.mCornerColor = context.getResources().getColor(R.color.qr_corner_line_color);
        this.mFocusedCornerColor = context.getResources().getColor(R.color.qr_focused_corner_line_color);
        int color = context.getResources().getColor(R.color.qr_background_color);
        this.mBackgroundColor = color;
        Paint paint = new Paint();
        this.mStrokePaint = paint;
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.mTransparentPaint = paint2;
        paint2.setAntiAlias(true);
        paint2.setColor(getResources().getColor(17170445));
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint3 = new Paint();
        this.mBackgroundPaint = paint3;
        paint3.setColor(color);
    }

    private void calculateFramePos() {
        int i = this.mWidth / 2;
        int i2 = this.mHeight / 2;
        float applyDimension = TypedValue.applyDimension(1, 264.0f, getResources().getDisplayMetrics()) / 2.0f;
        float applyDimension2 = TypedValue.applyDimension(1, 4.0f, getResources().getDisplayMetrics());
        float f = i;
        float f2 = i2;
        this.mOuterFrame = new RectF(f - applyDimension, f2 - applyDimension, f + applyDimension, f2 + applyDimension);
        RectF rectF = this.mOuterFrame;
        this.mInnerFrame = new RectF(rectF.left + applyDimension2, rectF.top + applyDimension2, rectF.right - applyDimension2, rectF.bottom - applyDimension2);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mStrokePaint.setColor(this.mFocused ? this.mFocusedCornerColor : this.mCornerColor);
        this.mMaskCanvas.drawColor(this.mBackgroundColor);
        Canvas canvas2 = this.mMaskCanvas;
        RectF rectF = this.mOuterFrame;
        float f = this.mRadius;
        canvas2.drawRoundRect(rectF, f, f, this.mStrokePaint);
        Canvas canvas3 = this.mMaskCanvas;
        RectF rectF2 = this.mInnerFrame;
        float f2 = this.mInnerRidus;
        canvas3.drawRoundRect(rectF2, f2, f2, this.mTransparentPaint);
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
