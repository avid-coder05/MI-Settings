package com.android.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class LineView extends View {
    private boolean mIsVertical;
    private Paint mPaint;
    private int mStrokeWidth;
    private int mXWidth;
    private int mYHeight;

    public LineView(Context context) {
        super(context);
        this.mIsVertical = true;
        this.mStrokeWidth = 5;
        init();
    }

    public LineView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsVertical = true;
        this.mStrokeWidth = 5;
        init();
    }

    public LineView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsVertical = true;
        this.mStrokeWidth = 5;
        init();
    }

    void init() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(-7829368);
        this.mPaint.setStrokeWidth(5.0f);
        this.mPaint.setAlpha(50);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.mIsVertical) {
            int i = this.mXWidth;
            int i2 = this.mStrokeWidth;
            canvas.drawLine(i - i2, 0.0f, i - i2, this.mYHeight, this.mPaint);
            return;
        }
        int i3 = this.mYHeight;
        int i4 = this.mStrokeWidth;
        canvas.drawLine(0.0f, i3 - i4, this.mXWidth, i3 - i4, this.mPaint);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mXWidth = i;
        this.mYHeight = i2;
    }

    public void setColor(int i) {
        this.mPaint.setColor(i);
    }

    public void setOrientation(boolean z) {
        this.mIsVertical = z;
    }

    public void setStrokeWidth(int i) {
        this.mStrokeWidth = i;
        this.mPaint.setStrokeWidth(i);
    }
}
