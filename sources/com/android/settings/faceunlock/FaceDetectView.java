package com.android.settings.faceunlock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes.dex */
public class FaceDetectView extends View {
    private Context mContext;
    private int mDetectSize;
    private Bitmap mLefrBottomBitmap;
    private float mLeftBottomX;
    private float mLeftBottomXInit;
    private float mLeftBottomY;
    private float mLeftBottomYInit;
    private Bitmap mLeftTopBitmap;
    private float mLeftTopX;
    private float mLeftTopXInit;
    private float mLeftTopY;
    private float mLeftTopYInit;
    private Bitmap mRightBottomBitmap;
    private float mRightBottomX;
    private float mRightBottomXInit;
    private float mRightBottomY;
    private float mRightBottomYInit;
    private Bitmap mRightTopBitmap;
    private float mRightTopX;
    private float mRightTopXInit;
    private float mRightTopY;
    private float mRightTopYInit;

    public FaceDetectView(Context context) {
        super(context);
        this.mContext = context;
    }

    public FaceDetectView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        this.mDetectSize = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_detect_size);
        this.mLeftTopBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.miui_face_suggestion_detect_left_top);
        this.mRightTopBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.miui_face_suggestion_detect_right_top);
        this.mLefrBottomBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.miui_face_suggestion_detect_left_bottom);
        this.mRightBottomBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.miui_face_suggestion_detect_right_bottom);
        Bitmap bitmap = this.mLeftTopBitmap;
        int i = this.mDetectSize;
        this.mLeftTopBitmap = zoomImg(bitmap, i, i);
        Bitmap bitmap2 = this.mRightTopBitmap;
        int i2 = this.mDetectSize;
        this.mRightTopBitmap = zoomImg(bitmap2, i2, i2);
        Bitmap bitmap3 = this.mLefrBottomBitmap;
        int i3 = this.mDetectSize;
        this.mLefrBottomBitmap = zoomImg(bitmap3, i3, i3);
        Bitmap bitmap4 = this.mRightBottomBitmap;
        int i4 = this.mDetectSize;
        this.mRightBottomBitmap = zoomImg(bitmap4, i4, i4);
        this.mLeftTopXInit = 0.0f;
        Resources resources = getResources();
        int i5 = R.dimen.miui_face_enroll_detect_initY;
        this.mLeftTopYInit = resources.getDimensionPixelSize(i5);
        Resources resources2 = getResources();
        int i6 = R.dimen.miui_face_enroll_detect_initX;
        this.mRightTopXInit = resources2.getDimensionPixelSize(i6);
        this.mRightTopYInit = getResources().getDimensionPixelSize(i5);
        this.mLeftBottomXInit = 0.0f;
        this.mLeftBottomYInit = getResources().getDimensionPixelSize(i6) + getResources().getDimensionPixelSize(i5);
        this.mRightBottomXInit = getResources().getDimensionPixelSize(i6);
        float dimensionPixelSize = getResources().getDimensionPixelSize(i6) + getResources().getDimensionPixelSize(i5);
        this.mRightBottomYInit = dimensionPixelSize;
        this.mLeftTopX = this.mLeftTopXInit;
        this.mLeftTopY = this.mLeftTopYInit;
        this.mRightTopX = this.mRightTopXInit;
        this.mRightTopY = this.mRightTopYInit;
        this.mLeftBottomX = this.mLeftBottomXInit;
        this.mLeftBottomY = this.mLeftBottomYInit;
        this.mRightBottomX = this.mRightBottomXInit;
        this.mRightBottomY = dimensionPixelSize;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(this.mLeftTopBitmap, this.mLeftTopX, this.mLeftTopY, (Paint) null);
        canvas.drawBitmap(this.mRightTopBitmap, this.mRightTopX, this.mRightTopY, (Paint) null);
        canvas.drawBitmap(this.mLefrBottomBitmap, this.mLeftBottomX, this.mLeftBottomY, (Paint) null);
        canvas.drawBitmap(this.mRightBottomBitmap, this.mRightBottomX, this.mRightBottomY, (Paint) null);
    }

    public void updateDetectImage(float f) {
        float f2 = this.mDetectSize + f;
        this.mLeftTopBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.miui_face_input_detect_left_top);
        this.mRightTopBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.miui_face_input_detect_right_top);
        this.mLefrBottomBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.miui_face_input_detect_left_bottom);
        this.mRightBottomBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.miui_face_input_detect_right_bottom);
        this.mLeftTopBitmap = zoomImg(this.mLeftTopBitmap, f2, f2);
        this.mRightTopBitmap = zoomImg(this.mRightTopBitmap, f2, f2);
        this.mLefrBottomBitmap = zoomImg(this.mLefrBottomBitmap, f2, f2);
        this.mRightBottomBitmap = zoomImg(this.mRightBottomBitmap, f2, f2);
    }

    public void updateFaceDetectPosition(float f, boolean z, float f2) {
        if (z) {
            updateDetectImage(f2);
        }
        this.mLeftTopX = f;
        this.mLeftTopY = this.mLeftTopYInit + f;
        this.mRightTopX = this.mRightTopXInit - f;
        this.mRightTopY = this.mRightTopYInit + f;
        this.mLeftBottomX = f;
        this.mLeftBottomY = this.mLeftBottomYInit - f;
        this.mRightBottomX = this.mRightBottomXInit - f;
        this.mRightBottomY = this.mRightBottomYInit - f;
        invalidate();
    }

    public Bitmap zoomImg(Bitmap bitmap, float f, float f2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(f / width, f2 / height);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
