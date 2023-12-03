package com.android.settings.faceunlock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes.dex */
public class FaceEnrollSuccessView extends View {
    private int cameraPreviewHeight;
    private int cameraPreviewWidth;
    private int mCircleRadius;
    private Bitmap mFaceImage;
    private Paint mPaint;
    private Path mPath;
    private int previewHeight;
    private int previewWidth;
    private int successViewLeft;
    private int successViewTop;

    public FaceEnrollSuccessView(Context context) {
        super(context);
        ((View) this).mContext = context;
    }

    public FaceEnrollSuccessView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        ((View) this).mContext = context;
        this.mCircleRadius = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_circle_radius);
        this.previewWidth = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_width);
        this.previewHeight = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_height);
        this.mPaint = new Paint();
        this.mPath = new Path();
    }

    public void drawFaceSuccessView(float f) {
        this.mCircleRadius = (int) f;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mFaceImage != null) {
            this.mPath.reset();
            this.mPath.addCircle(this.previewWidth / 2, this.previewHeight / 2, this.mCircleRadius, Path.Direction.CCW);
            canvas.clipPath(this.mPath);
            canvas.drawBitmap(this.mFaceImage, this.successViewTop, this.successViewLeft, this.mPaint);
        }
    }

    public void updateFaceBitmap(Bitmap bitmap) {
        if (KeyguardSettingsFaceUnlockUtils.isSupportMultiFaceInput(((View) this).mContext)) {
            this.cameraPreviewWidth = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_width);
            this.cameraPreviewHeight = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_height);
            this.successViewTop = 0;
            this.successViewLeft = 0;
        } else {
            this.cameraPreviewWidth = getResources().getDimensionPixelSize(R.dimen.miui_face_input_cameraview_width);
            int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.miui_face_input_cameraview_height);
            this.cameraPreviewHeight = dimensionPixelSize;
            int i = this.previewWidth / 2;
            int i2 = this.mCircleRadius;
            this.successViewTop = i - i2;
            this.successViewLeft = ((this.previewHeight / 2) - i2) - ((dimensionPixelSize - (i2 * 2)) / 2);
        }
        this.mFaceImage = KeyguardSettingsFaceUnlockUtils.getCircleBitmap(bitmap, this.mCircleRadius, this.cameraPreviewWidth / 2, this.cameraPreviewHeight / 2);
        invalidate();
    }
}
