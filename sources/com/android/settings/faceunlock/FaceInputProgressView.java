package com.android.settings.faceunlock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes.dex */
public class FaceInputProgressView extends View {
    private boolean mDrawInitCircle;
    private Paint mInitPaint;
    private Paint mPaint;
    private int mProgress;
    private int mProgressCircleLeft;
    private int mProgressCircleTop;
    private int mProgressCircleWidth;

    public FaceInputProgressView(Context context) {
        super(context);
        initView();
    }

    public FaceInputProgressView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public FaceInputProgressView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    private void initView() {
        this.mDrawInitCircle = true;
        Paint paint = new Paint();
        this.mInitPaint = paint;
        paint.setColor(-13421773);
        this.mInitPaint.setStrokeWidth(12.0f);
        this.mInitPaint.setStyle(Paint.Style.STROKE);
        this.mInitPaint.setAntiAlias(true);
        this.mInitPaint.setStrokeCap(Paint.Cap.ROUND);
        Paint paint2 = new Paint();
        this.mPaint = paint2;
        paint2.setColor(-15891201);
        this.mPaint.setStrokeWidth(12.0f);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mDrawInitCircle = true;
        this.mProgressCircleLeft = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_progress_circle_left);
        this.mProgressCircleTop = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_progress_circle_top);
        this.mProgressCircleWidth = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_progress_circle_width);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i = this.mProgressCircleLeft;
        int i2 = this.mProgressCircleTop;
        int i3 = this.mProgressCircleWidth;
        RectF rectF = new RectF(i, i2, i + i3, i2 + i3);
        if (this.mDrawInitCircle) {
            canvas.drawArc(rectF, 0.0f, 360.0f, false, this.mInitPaint);
        } else {
            canvas.drawArc(rectF, (float) (this.mProgress - 83), 352 - r0, false, this.mInitPaint);
            canvas.drawArc(rectF, -87.0f, this.mProgress, false, this.mPaint);
        }
        super.onDraw(canvas);
    }

    public void updateFaceInputProgress(int i) {
        if (i == 0) {
            this.mDrawInitCircle = true;
        } else {
            this.mDrawInitCircle = false;
        }
        this.mProgress = i;
        if (i >= 0) {
            invalidate();
        }
    }
}
