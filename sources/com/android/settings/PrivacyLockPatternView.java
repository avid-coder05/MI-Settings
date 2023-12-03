package com.android.settings;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.LockPatternView;
import com.android.settings.privacypassword.PrivacyPasswordUtils;

/* loaded from: classes.dex */
public class PrivacyLockPatternView extends LockPatternView {
    public PrivacyLockPatternView(Context context) {
        super(context);
    }

    public PrivacyLockPatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private Bitmap getNewBitmap(Bitmap bitmap, float f, float f2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(f / width, f2 / height);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    @Override // com.android.settings.LockPatternView
    protected void drawCircle(Canvas canvas, int i, int i2, boolean z) {
        Bitmap bitmap;
        if (!z || (this.mInStealthMode && this.mPatternDisplayMode != LockPatternView.DisplayMode.Wrong)) {
            bitmap = this.mBitmapBtnTouched;
        } else if (this.mPatternInProgress) {
            bitmap = this.mBitmapBtnTouched;
        } else {
            LockPatternView.DisplayMode displayMode = this.mPatternDisplayMode;
            if (displayMode == LockPatternView.DisplayMode.Wrong) {
                bitmap = this.mBitmapBtnRed;
            } else if (displayMode != LockPatternView.DisplayMode.Correct && displayMode != LockPatternView.DisplayMode.Animate) {
                throw new IllegalStateException("unknown display mode " + this.mPatternDisplayMode);
            } else {
                bitmap = this.mBitmapBtnTouched;
            }
        }
        Resources resources = getResources();
        int i3 = R.integer.privacy_patterview_dot_width;
        Bitmap newBitmap = getNewBitmap(bitmap, resources.getInteger(i3), getResources().getInteger(i3));
        float f = this.mSquareWidth;
        float f2 = this.mSquareHeight;
        if (newBitmap != null) {
            int width = (int) ((f - newBitmap.getWidth()) / 2.0f);
            int height = (int) ((f2 - newBitmap.getHeight()) / 2.0f);
            float min = Math.min(this.mSquareWidth / this.mBitmapWidth, 1.0f);
            float min2 = Math.min(this.mSquareHeight / this.mBitmapHeight, 1.0f);
            this.mCircleMatrix.setTranslate(i + width, i2 + height);
            this.mCircleMatrix.preTranslate(this.mBitmapWidth / 2, this.mBitmapHeight / 2);
            this.mCircleMatrix.preScale(min, min2);
            this.mCircleMatrix.preTranslate((-this.mBitmapWidth) / 2, (-this.mBitmapHeight) / 2);
            canvas.drawBitmap(newBitmap, this.mCircleMatrix, this.mPaint);
        }
    }

    @Override // com.android.settings.LockPatternView, android.view.View
    protected void onMeasure(int i, int i2) {
        int suggestedMinimumWidth = getSuggestedMinimumWidth();
        int suggestedMinimumHeight = getSuggestedMinimumHeight();
        int resolveMeasured = resolveMeasured(i, suggestedMinimumWidth);
        int resolveMeasured2 = resolveMeasured(i2, suggestedMinimumHeight);
        int i3 = this.mAspect;
        if (i3 != 0) {
            if (i3 == 1) {
                resolveMeasured2 = Math.min(resolveMeasured, resolveMeasured2);
            } else if (i3 == 2) {
                resolveMeasured = Math.min(resolveMeasured, resolveMeasured2);
            } else if (i3 == 3) {
                resolveMeasured = PrivacyPasswordUtils.getDimen(((View) this).mContext, R.dimen.lock_view_size);
            }
            setMeasuredDimension(resolveMeasured, resolveMeasured2);
        }
        resolveMeasured = Math.min(resolveMeasured, resolveMeasured2);
        resolveMeasured2 = resolveMeasured;
        setMeasuredDimension(resolveMeasured, resolveMeasured2);
    }
}
