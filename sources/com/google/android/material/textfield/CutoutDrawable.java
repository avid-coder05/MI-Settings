package com.google.android.material.textfield;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

/* loaded from: classes2.dex */
class CutoutDrawable extends MaterialShapeDrawable {
    private final RectF cutoutBounds;
    private final Paint cutoutPaint;
    private int savedLayer;

    CutoutDrawable() {
        this(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CutoutDrawable(ShapeAppearanceModel shapeAppearanceModel) {
        super(shapeAppearanceModel == null ? new ShapeAppearanceModel() : shapeAppearanceModel);
        this.cutoutPaint = new Paint(1);
        setPaintStyles();
        this.cutoutBounds = new RectF();
    }

    private void postDraw(Canvas canvas) {
        if (useHardwareLayer(getCallback())) {
            return;
        }
        canvas.restoreToCount(this.savedLayer);
    }

    private void preDraw(Canvas canvas) {
        Drawable.Callback callback = getCallback();
        if (!useHardwareLayer(callback)) {
            saveCanvasLayer(canvas);
            return;
        }
        View view = (View) callback;
        if (view.getLayerType() != 2) {
            view.setLayerType(2, null);
        }
    }

    private void saveCanvasLayer(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.savedLayer = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), null);
        } else {
            this.savedLayer = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), null, 31);
        }
    }

    private void setPaintStyles() {
        this.cutoutPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.cutoutPaint.setColor(-1);
        this.cutoutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    private boolean useHardwareLayer(Drawable.Callback callback) {
        return callback instanceof View;
    }

    @Override // com.google.android.material.shape.MaterialShapeDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        preDraw(canvas);
        super.draw(canvas);
        canvas.drawRect(this.cutoutBounds, this.cutoutPaint);
        postDraw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasCutout() {
        return !this.cutoutBounds.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeCutout() {
        setCutout(0.0f, 0.0f, 0.0f, 0.0f);
    }

    void setCutout(float f, float f2, float f3, float f4) {
        RectF rectF = this.cutoutBounds;
        if (f == rectF.left && f2 == rectF.top && f3 == rectF.right && f4 == rectF.bottom) {
            return;
        }
        rectF.set(f, f2, f3, f4);
        invalidateSelf();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCutout(RectF rectF) {
        setCutout(rectF.left, rectF.top, rectF.right, rectF.bottom);
    }
}
