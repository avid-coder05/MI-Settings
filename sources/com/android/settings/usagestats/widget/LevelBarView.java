package com.android.settings.usagestats.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.usagestats.utils.AppInfoUtils;

/* loaded from: classes2.dex */
public class LevelBarView extends View {
    private boolean isNoti;
    private float mBarDynamicX;
    private long mCurrentLevel;
    private int mDefaultHeight;
    private int mHeight;
    private float mMargin;
    private long mMaxLevel;
    private Paint mPaint;
    private int mWidth;

    public LevelBarView(Context context) {
        this(context, null);
    }

    public LevelBarView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LevelBarView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isNoti = false;
        init();
    }

    private void init() {
        this.mDefaultHeight = 10;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(AppInfoUtils.getColor(getContext().getApplicationContext(), R.color.usage_stats_item_level_bar));
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mMargin = MiuiUtils.dp2px(getContext().getApplicationContext(), 10.0f);
    }

    private void resetLevel() {
        long j = this.mMaxLevel;
        if (j < 600000) {
            this.mMaxLevel = 600000L;
        } else if (j < 1200000) {
            this.mMaxLevel = 1200000L;
        }
    }

    private boolean shouldDrawPoint() {
        return this.mCurrentLevel <= 180000 && this.mMaxLevel > 1200000;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mCurrentLevel < 0 || this.mMaxLevel < 0) {
            return;
        }
        this.mPaint.setStrokeWidth(this.mHeight);
        if (shouldDrawPoint() && !this.isNoti) {
            canvas.drawPoint(isLayoutRtl() ? this.mWidth - this.mHeight : this.mHeight, this.mHeight / 2, this.mPaint);
        } else if (this.mMaxLevel != 0) {
            if (!isLayoutRtl()) {
                float f = ((this.mWidth - this.mMargin) * ((float) this.mCurrentLevel)) / ((float) this.mMaxLevel);
                this.mBarDynamicX = f;
                int i = this.mHeight;
                if (f <= i / 2) {
                    canvas.drawPoint(i, i / 2, this.mPaint);
                    return;
                } else {
                    canvas.drawLine(i, i / 2, f, i / 2, this.mPaint);
                    return;
                }
            }
            int i2 = this.mWidth;
            float f2 = i2 - (((i2 - this.mMargin) * ((float) this.mCurrentLevel)) / ((float) this.mMaxLevel));
            this.mBarDynamicX = f2;
            int i3 = this.mHeight;
            if (f2 >= i2 - i3) {
                canvas.drawPoint(i2 - i3, i3 / 2, this.mPaint);
            } else {
                canvas.drawLine(f2, i3 / 2, i2 - i3, i3 / 2, this.mPaint);
            }
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode == 1073741824) {
            this.mWidth = size;
        } else {
            this.mWidth = getMeasuredWidth();
            size = 0;
        }
        if (mode2 == 1073741824) {
            this.mHeight = size2;
        } else {
            this.mHeight = this.mDefaultHeight;
            size2 = 0;
        }
        setMeasuredDimension(size, size2);
    }

    public void setCurrentLevel(long j) {
        this.mCurrentLevel = j;
        invalidate();
    }

    public void setIsNoti(boolean z) {
        this.isNoti = z;
    }

    public void setMaxLevel(long j) {
        this.mMaxLevel = j;
        if (this.isNoti) {
            return;
        }
        resetLevel();
    }
}
