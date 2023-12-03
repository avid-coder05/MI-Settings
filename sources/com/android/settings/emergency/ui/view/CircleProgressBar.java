package com.android.settings.emergency.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Keep;

/* loaded from: classes.dex */
public class CircleProgressBar extends View {
    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private int mMax;
    private float mProgress;
    private RectF mRectF;
    private float mStrokeWidth;

    public CircleProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mStrokeWidth = 10.0f;
        this.mProgress = 0.0f;
        this.mMax = 100;
        init();
    }

    private void init() {
        this.mRectF = new RectF();
        Paint paint = new Paint(1);
        this.mBackgroundPaint = paint;
        paint.setColor(Color.parseColor("#FF2B1F28"));
        this.mBackgroundPaint.setStyle(Paint.Style.STROKE);
        this.mBackgroundPaint.setStrokeWidth(this.mStrokeWidth);
        Paint paint2 = new Paint(1);
        this.mForegroundPaint = paint2;
        paint2.setColor(Color.parseColor("#FFC6242B"));
        this.mForegroundPaint.setStyle(Paint.Style.STROKE);
        this.mForegroundPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mForegroundPaint.setStrokeWidth(this.mStrokeWidth);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(this.mRectF, this.mBackgroundPaint);
        canvas.drawArc(this.mRectF, -90.0f, (this.mProgress * 360.0f) / this.mMax, false, this.mForegroundPaint);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int min = Math.min(View.getDefaultSize(getSuggestedMinimumWidth(), i), View.getDefaultSize(getSuggestedMinimumHeight(), i2));
        setMeasuredDimension(min, min);
        RectF rectF = this.mRectF;
        float f = this.mStrokeWidth;
        float f2 = min;
        rectF.set((f / 2.0f) + 0.0f, (f / 2.0f) + 0.0f, f2 - (f / 2.0f), f2 - (f / 2.0f));
    }

    public void setMax(int i) {
        this.mMax = i;
        invalidate();
    }

    @Keep
    public void setProgress(float f) {
        this.mProgress = f;
        invalidate();
    }
}
