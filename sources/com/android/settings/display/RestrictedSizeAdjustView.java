package com.android.settings.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.settings.R;
import miuix.androidbasewidget.widget.SeekBar;

/* loaded from: classes.dex */
public class RestrictedSizeAdjustView extends SeekBar {
    private boolean mMiddle;
    private int mMiddlePoint;
    private Paint mPointPaint;
    private float mPointsRadius;
    private int mSmallPointColor;

    public RestrictedSizeAdjustView(Context context) {
        super(context);
        this.mMiddle = false;
        init();
    }

    public RestrictedSizeAdjustView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMiddle = false;
        init();
    }

    public RestrictedSizeAdjustView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMiddle = false;
        init();
    }

    private void init() {
        this.mSmallPointColor = getResources().getColor(R.color.font_weight_view_small_color, null);
        this.mPointsRadius = getResources().getDimension(R.dimen.font_size_view_small_radius);
        Paint paint = new Paint();
        this.mPointPaint = paint;
        paint.setAntiAlias(true);
        this.mPointPaint.setStyle(Paint.Style.FILL);
        this.mPointPaint.setStrokeWidth(0.0f);
    }

    private void setMiddlePoint(float f, int i) {
        this.mMiddlePoint = 0;
        if (Math.abs(f - (i / 2)) >= 30.0f) {
            this.mMiddle = false;
            return;
        }
        this.mMiddle = true;
        int max = getMax() / 2;
        this.mMiddlePoint = max;
        setProgress(max);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.AppCompatSeekBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mPointPaint.setColor(this.mSmallPointColor);
        setMiddlePoint(getProgress(), getMax());
        if (this.mMiddle) {
            return;
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.mPointsRadius, this.mPointPaint);
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 1 || action == 2) {
            super.onTouchEvent(motionEvent);
            setMiddlePoint(motionEvent.getX(), getWidth());
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }
}
