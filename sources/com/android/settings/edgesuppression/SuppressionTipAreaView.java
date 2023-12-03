package com.android.settings.edgesuppression;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes.dex */
public class SuppressionTipAreaView extends View {
    private Bitmap mBitmap;
    private float mBottomDispalyCurvature;
    private Paint mBottomTransparentPaint;
    private final RectF mBottomTransparentRect;
    private Canvas mCanvas;
    private Paint mPaintTip;
    private final RectF mRect;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mTipWidth;
    private float mTopDispalyCurvature;
    private Paint mTopTransparentPaint;
    private final RectF mTopTransparentRect;

    public SuppressionTipAreaView(Context context, int i, int i2, int i3) {
        super(context);
        this.mRect = new RectF();
        this.mTopTransparentRect = new RectF();
        this.mBottomTransparentRect = new RectF();
        this.mTipWidth = i;
        Paint paint = new Paint();
        this.mPaintTip = paint;
        paint.setColor(getContext().getResources().getColor(R.color.restricted_tip_area_color, null));
        this.mPaintTip.setAntiAlias(true);
        this.mScreenHeight = i3;
        this.mScreenWidth = i2;
        updateFilletCurvature();
        Paint paint2 = new Paint();
        this.mTopTransparentPaint = paint2;
        paint2.setAntiAlias(true);
        this.mTopTransparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        Paint paint3 = new Paint();
        this.mBottomTransparentPaint = paint3;
        paint3.setAntiAlias(true);
        this.mBottomTransparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    public SuppressionTipAreaView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRect = new RectF();
        this.mTopTransparentRect = new RectF();
        this.mBottomTransparentRect = new RectF();
    }

    public SuppressionTipAreaView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mRect = new RectF();
        this.mTopTransparentRect = new RectF();
        this.mBottomTransparentRect = new RectF();
    }

    private void drawBottomRect() {
        RectF rectF = this.mTopTransparentRect;
        rectF.left = this.mTipWidth;
        rectF.right = this.mScreenWidth - r1;
        rectF.top = this.mScreenHeight / 2.0f;
        rectF.bottom = r2 - r1;
        Canvas canvas = this.mCanvas;
        float f = this.mBottomDispalyCurvature;
        canvas.drawRoundRect(rectF, f, f, this.mBottomTransparentPaint);
        Canvas canvas2 = this.mCanvas;
        int i = this.mTipWidth;
        canvas2.drawRect(new RectF(i, (this.mScreenWidth / 2.0f) + i, r5 - i, (this.mScreenHeight - i) - this.mBottomDispalyCurvature), this.mBottomTransparentPaint);
    }

    private void drawTopRect() {
        RectF rectF = this.mBottomTransparentRect;
        int i = this.mTipWidth;
        rectF.left = i;
        rectF.right = this.mScreenWidth - i;
        rectF.top = i;
        rectF.bottom = (this.mScreenHeight / 2.0f) - i;
        Canvas canvas = this.mCanvas;
        float f = this.mTopDispalyCurvature;
        canvas.drawRoundRect(rectF, f, f, this.mTopTransparentPaint);
        Canvas canvas2 = this.mCanvas;
        int i2 = this.mTipWidth;
        canvas2.drawRect(new RectF(i2, i2 + this.mTopDispalyCurvature, this.mScreenWidth - i2, (this.mScreenHeight / 2.0f) - i2), this.mTopTransparentPaint);
    }

    private void updateFilletCurvature() {
        String str = SystemProperties.get("persist.sys.miui_resolution", "");
        if ("".equals(str) || Integer.parseInt(str.split(",")[0]) != 1080) {
            this.mTopDispalyCurvature = 180.0f;
            this.mBottomDispalyCurvature = 152.0f;
            return;
        }
        this.mTopDispalyCurvature = 155.0f;
        this.mBottomDispalyCurvature = 135.0f;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        RectF rectF = this.mRect;
        rectF.left = 0.0f;
        rectF.right = this.mScreenWidth;
        rectF.top = 0.0f;
        rectF.bottom = this.mScreenHeight;
        this.mCanvas.drawRect(rectF, this.mPaintTip);
        drawTopRect();
        drawBottomRect();
        this.mBitmap.setWidth(this.mScreenWidth);
        this.mBitmap.setHeight(this.mScreenHeight);
        canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, (Paint) null);
        super.onDraw(canvas);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mBitmap = Bitmap.createBitmap(this.mScreenWidth, this.mScreenHeight, Bitmap.Config.ARGB_8888);
        this.mCanvas = new Canvas(this.mBitmap);
    }

    public void setTipWidth(int i, int i2, int i3) {
        this.mTipWidth = i;
        if (i2 == this.mScreenWidth || i3 == this.mScreenHeight) {
            return;
        }
        this.mScreenWidth = i2;
        this.mScreenHeight = i3;
        updateFilletCurvature();
    }
}
