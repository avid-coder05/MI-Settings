package com.android.settings.identification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/* loaded from: classes.dex */
public class CornerView extends RelativeLayout {
    private final Paint maskPaint;
    private float rect_radius;
    private final RectF roundRect;
    private final Paint zonePaint;

    public CornerView(Context context) {
        super(context);
        this.roundRect = new RectF();
        this.rect_radius = 20.0f;
        this.maskPaint = new Paint();
        this.zonePaint = new Paint();
        init();
    }

    public CornerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.roundRect = new RectF();
        this.rect_radius = 20.0f;
        this.maskPaint = new Paint();
        this.zonePaint = new Paint();
        init();
    }

    private void init() {
        this.maskPaint.setAntiAlias(true);
        this.maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        this.zonePaint.setAntiAlias(true);
        this.zonePaint.setColor(-1);
        this.rect_radius *= getResources().getDisplayMetrics().density;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        canvas.saveLayer(this.roundRect, this.zonePaint, 31);
        RectF rectF = this.roundRect;
        float f = this.rect_radius;
        canvas.drawRoundRect(rectF, f, f, this.zonePaint);
        canvas.saveLayer(this.roundRect, this.maskPaint, 31);
        super.draw(canvas);
        canvas.restore();
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.roundRect.set(0.0f, 0.0f, getWidth(), getHeight());
    }

    public void setCorner(float f) {
        this.rect_radius = f;
        invalidate();
    }
}
