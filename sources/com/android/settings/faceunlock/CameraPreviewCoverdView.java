package com.android.settings.faceunlock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes.dex */
public class CameraPreviewCoverdView extends View {
    private int cameraPreviewHeight;
    private int cameraPreviewTop;
    private int cameraPreviewWidth;
    private Bitmap mBgBitmap;
    private boolean mDrawCoverdView;
    private boolean mFromCicleToRect;
    private boolean mOnlyDrawCircle;
    private float mPosition;
    private boolean mSkipFrame;

    public CameraPreviewCoverdView(Context context) {
        super(context);
        initView();
    }

    public CameraPreviewCoverdView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public CameraPreviewCoverdView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    private void initView() {
        this.cameraPreviewWidth = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_width);
        this.cameraPreviewHeight = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_height);
        this.cameraPreviewTop = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_iner_top);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        RectF rectF;
        if (this.mDrawCoverdView) {
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
            if (this.mBgBitmap == null) {
                this.mBgBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas2 = new Canvas(this.mBgBitmap);
            canvas2.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
            canvas.save();
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Rect rect = new Rect(0, 0, this.cameraPreviewWidth, this.cameraPreviewHeight);
            paint.setColor(-16777216);
            canvas2.drawRect(rect, paint);
            if (this.mFromCicleToRect) {
                int i = this.cameraPreviewWidth;
                float f = this.mPosition;
                int i2 = this.cameraPreviewHeight;
                rectF = new RectF((i / 2.0f) - f, (i2 / 2.0f) - f, (i / 2.0f) + f, (i2 / 2.0f) + f);
            } else {
                float f2 = this.mPosition;
                rectF = new RectF(f2, this.cameraPreviewTop + f2, this.cameraPreviewWidth - f2, (r8 + r6) - f2);
            }
            if (this.mSkipFrame) {
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                paint.setColor(0);
            } else {
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                paint.setColor(getResources().getColor(R.color.miui_face_input_cameraview_cover_color));
            }
            if (this.mOnlyDrawCircle) {
                canvas2.drawCircle(this.cameraPreviewWidth / 2.0f, this.cameraPreviewHeight / 2.0f, this.mPosition, paint);
            } else if (this.mFromCicleToRect) {
                int i3 = this.cameraPreviewWidth;
                float f3 = this.mPosition;
                canvas2.drawRoundRect(rectF, (i3 / 2.0f) - f3, (i3 / 2.0f) - f3, paint);
            } else {
                float f4 = this.mPosition;
                canvas2.drawRoundRect(rectF, f4 * 2.35f, f4 * 2.35f, paint);
            }
            paint.setXfermode(null);
            canvas.drawBitmap(this.mBgBitmap, 0.0f, 0.0f, new Paint());
            canvas.restore();
        }
    }

    public void refreshCameraView(float f, boolean z, boolean z2, boolean z3) {
        this.mDrawCoverdView = true;
        this.mSkipFrame = z;
        this.mOnlyDrawCircle = z2;
        this.mFromCicleToRect = z3;
        this.mPosition = f;
        invalidate();
    }
}
