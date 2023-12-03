package com.android.settings.faceunlock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes.dex */
public class FaceInputGridView extends View {
    private int cameraPreviewHeight;
    private int cameraPreviewWidth;
    private boolean draw;
    private float innerRectSize;
    private Bitmap mBitmap;
    private int mCircleRadius;
    private float mGridAlpha;
    private int mLineEndPointYPosition;
    private int mLineEndPointxPosition;
    private int mLineStartPointXPosition;
    private int mLineStartPointYPosition;
    private Paint mPaint;
    private Path mPath;
    private int mPointColumn;
    private float mPointTime;
    private float mScaleSize;

    public FaceInputGridView(Context context) {
        super(context);
        this.mPath = new Path();
        initView();
    }

    public FaceInputGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPath = new Path();
        initView();
    }

    public FaceInputGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPath = new Path();
        initView();
    }

    private void initView() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(-1);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(3.0f);
        this.mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.face_input);
        this.mGridAlpha = 0.97f;
        this.draw = false;
        this.cameraPreviewWidth = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_width);
        this.cameraPreviewHeight = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_height);
        this.mCircleRadius = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_circle_radius);
        this.mLineStartPointXPosition = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_line_start_xposition);
        this.mLineStartPointYPosition = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_line_start_yposition);
        this.mLineEndPointxPosition = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_line_end_xposition);
        this.mLineEndPointYPosition = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_line_end_yposition);
        this.innerRectSize = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_line_distance);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.draw) {
            this.mPath.reset();
            this.mPath.addCircle(this.cameraPreviewWidth / 2.0f, this.cameraPreviewHeight / 2.0f, this.mCircleRadius, Path.Direction.CCW);
            canvas.clipPath(this.mPath);
            this.mPaint.setAlpha((int) (this.mGridAlpha * 255.0f));
            for (int i = 0; i < 14; i++) {
                float f = this.mLineStartPointXPosition;
                int i2 = this.mLineStartPointYPosition;
                float f2 = this.innerRectSize;
                float f3 = i;
                canvas.drawLine(f, i2 + (f2 * f3), this.mLineEndPointxPosition, i2 + (f2 * f3), this.mPaint);
                int i3 = this.mLineStartPointXPosition;
                float f4 = this.innerRectSize;
                canvas.drawLine(i3 + (f4 * f3), this.mLineStartPointYPosition, i3 + (f4 * f3), this.mLineEndPointYPosition, this.mPaint);
            }
            for (int i4 = 0; i4 < this.mPointColumn; i4++) {
                float f5 = this.mPointTime;
                int i5 = i4 * 50;
                float f6 = f5 <= ((float) (i5 + 300)) ? (f5 - i5) / 300.0f : ((600.0f - f5) + i5) / 300.0f;
                this.mScaleSize = f6;
                int i6 = (int) (f6 * 15.0f);
                if (i6 > 0) {
                    for (int i7 = 0; i7 < 14; i7++) {
                        float f7 = i6 / 2.0f;
                        float f8 = this.innerRectSize;
                        canvas.drawBitmap(Bitmap.createScaledBitmap(this.mBitmap, i6, i6, true), (this.mLineStartPointXPosition - f7) + (i7 * f8), (this.mLineStartPointYPosition - f7) + (f8 * i4), new Paint());
                    }
                }
            }
        }
        super.onDraw(canvas);
    }

    public void setGridViewAlpha(float f) {
        this.draw = true;
        this.mGridAlpha = f;
        invalidate();
    }

    public void updateFaceInputPoint(float f) {
        this.draw = true;
        this.mPointTime = f;
        int ceil = (int) Math.ceil(f / 50.0f);
        this.mPointColumn = ceil;
        if (ceil > 14) {
            this.mPointColumn = 14;
        }
        invalidate();
    }
}
