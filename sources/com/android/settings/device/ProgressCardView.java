package com.android.settings.device;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.android.settings.R;
import com.android.settings.display.DarkModeTimeModeUtil;
import miuix.animation.utils.EaseManager;

/* loaded from: classes.dex */
public class ProgressCardView extends View {
    private int mCurrentWaterHeight;
    private int mCurrentWaveHeight;
    private float mDamping;
    private int mDuration;
    private int mFinalWaterHeight;
    private boolean mFirstStartAnim;
    private Paint mPaint;
    private Path mPath;
    private float mPercent;
    private float mResponse;
    private int mViewHeight;
    private int mWaveHeight;
    private int mWaveOffset;
    private int mWaveWidth;

    public ProgressCardView(Context context) {
        super(context);
        this.mPercent = 0.0f;
        this.mCurrentWaterHeight = 0;
        this.mWaveHeight = 30;
        this.mWaveWidth = 400;
        this.mWaveOffset = 0;
        this.mCurrentWaveHeight = 0;
        this.mDamping = 0.6f;
        this.mResponse = 0.6f;
        this.mDuration = 1600;
        this.mFirstStartAnim = true;
        init();
    }

    public ProgressCardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPercent = 0.0f;
        this.mCurrentWaterHeight = 0;
        this.mWaveHeight = 30;
        this.mWaveWidth = 400;
        this.mWaveOffset = 0;
        this.mCurrentWaveHeight = 0;
        this.mDamping = 0.6f;
        this.mResponse = 0.6f;
        this.mDuration = 1600;
        this.mFirstStartAnim = true;
        init();
    }

    private void init() {
        this.mPaint = new Paint(1);
        this.mPath = new Path();
    }

    private void startAnim() {
        ValueAnimator ofInt = ValueAnimator.ofInt(this.mViewHeight, this.mFinalWaterHeight);
        EaseManager.SpringInterpolator springInterpolator = new EaseManager.SpringInterpolator();
        springInterpolator.setDamping(this.mDamping);
        springInterpolator.setResponse(this.mResponse);
        ofInt.setDuration(this.mDuration);
        ofInt.setInterpolator(springInterpolator);
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.device.ProgressCardView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProgressCardView.this.mCurrentWaterHeight = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                ProgressCardView.this.postInvalidate();
            }
        });
        ofInt.start();
        ValueAnimator ofInt2 = ValueAnimator.ofInt(0, this.mWaveWidth);
        ofInt2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.device.ProgressCardView.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProgressCardView.this.mWaveOffset = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                if (ProgressCardView.this.mFinalWaterHeight != ProgressCardView.this.mViewHeight) {
                    ProgressCardView progressCardView = ProgressCardView.this;
                    progressCardView.mCurrentWaveHeight = ((progressCardView.mWaveHeight * (ProgressCardView.this.mViewHeight - ProgressCardView.this.mCurrentWaterHeight)) / (ProgressCardView.this.mViewHeight - ProgressCardView.this.mFinalWaterHeight)) - ((ProgressCardView.this.mWaveHeight * ProgressCardView.this.mWaveOffset) / ProgressCardView.this.mWaveWidth);
                }
                if (ProgressCardView.this.mCurrentWaveHeight < 0) {
                    ProgressCardView.this.mCurrentWaveHeight = 0;
                }
                ProgressCardView.this.postInvalidate();
            }
        });
        ofInt2.setInterpolator(new LinearInterpolator());
        ofInt2.setDuration(this.mDuration);
        ofInt2.start();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mFirstStartAnim) {
            this.mFirstStartAnim = false;
            startAnim();
        }
        setLayerType(1, null);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        this.mPaint.setColor(((View) this).mContext.getColor(R.color.progress_paint_color));
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        int i = this.mWaveWidth / 2;
        this.mPath.reset();
        this.mPath.moveTo((-this.mWaveWidth) + this.mWaveOffset, this.mViewHeight - this.mCurrentWaterHeight);
        int i2 = -this.mWaveWidth;
        while (i2 < this.mWaveWidth + getWidth()) {
            float f = i / 2;
            float f2 = i;
            this.mPath.rQuadTo(f, -this.mCurrentWaveHeight, f2, 0.0f);
            this.mPath.rQuadTo(f, this.mCurrentWaveHeight, f2, 0.0f);
            i2 += this.mWaveWidth;
        }
        if (DarkModeTimeModeUtil.isDarkModeEnable(((View) this).mContext)) {
            this.mPath.lineTo(getWidth(), 0.0f);
            this.mPath.lineTo(0.0f, 0.0f);
        } else {
            this.mPath.lineTo(getWidth(), getHeight());
            this.mPath.lineTo(0.0f, getHeight());
        }
        this.mPath.close();
        canvas.drawPath(this.mPath, this.mPaint);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredHeight = getMeasuredHeight();
        this.mViewHeight = measuredHeight;
        this.mFinalWaterHeight = (int) (measuredHeight * this.mPercent);
    }

    public void setPercent(float f) {
        this.mPercent = f;
        this.mFirstStartAnim = true;
        this.mFinalWaterHeight = (int) (this.mViewHeight * f);
        postInvalidate();
    }
}
