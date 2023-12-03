package com.android.settings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class MomentView extends View {
    Bitmap mBitmap;
    float mCx;
    float mCy;
    Paint mPaint;
    float mRadius;

    public MomentView(Context context) {
        super(context);
        init();
    }

    public MomentView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public MomentView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(-7829368);
        this.mBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.moment_smalldot)).getBitmap();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawCircle(this.mCx, this.mCy, this.mRadius, this.mPaint);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mCx = i / 2;
        this.mCy = (float) ((i2 / 2) - 1);
        this.mRadius = 5.0f;
    }

    public void setColor(int i) {
        this.mPaint.setColor(i);
        invalidate();
    }
}
