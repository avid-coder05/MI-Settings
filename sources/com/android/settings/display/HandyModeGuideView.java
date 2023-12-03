package com.android.settings.display;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import com.android.settings.R;
import miui.util.HandyModeUtils;

/* loaded from: classes.dex */
public class HandyModeGuideView extends View {
    ValueAnimator mAnimator;
    Rect mGuidePhoneScreen;
    boolean mIsLeft;
    Paint mPaint;
    boolean mReversing;
    int mState;

    public HandyModeGuideView(Context context) {
        this(context, null);
    }

    public HandyModeGuideView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HandyModeGuideView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPaint = new Paint(1);
        this.mAnimator = null;
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.handy_mode_guide_screen_left);
        int dimensionPixelOffset2 = getResources().getDimensionPixelOffset(R.dimen.handy_mode_guide_screen_top);
        this.mGuidePhoneScreen = new Rect(dimensionPixelOffset, dimensionPixelOffset2, getResources().getDimensionPixelOffset(R.dimen.handy_mode_guide_screen_width) + dimensionPixelOffset, getResources().getDimensionPixelOffset(R.dimen.handy_mode_guide_screen_height) + dimensionPixelOffset2);
        if (Settings.Global.getFloat(((View) this).mContext.getContentResolver(), "animator_duration_scale", 1.0f) == 0.0f) {
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.display.HandyModeGuideView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                HandyModeGuideView.this.post(new Runnable() { // from class: com.android.settings.display.HandyModeGuideView.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        HandyModeGuideView handyModeGuideView = HandyModeGuideView.this;
                        int i2 = handyModeGuideView.mState + 1;
                        handyModeGuideView.mState = i2;
                        if (i2 > 4) {
                            handyModeGuideView.mState = 0;
                            boolean z = !handyModeGuideView.mReversing;
                            handyModeGuideView.mReversing = z;
                            if (!z) {
                                handyModeGuideView.mIsLeft = !handyModeGuideView.mIsLeft;
                            }
                        }
                        int i3 = handyModeGuideView.mState;
                        if (i3 == 0) {
                            handyModeGuideView.mAnimator.setDuration(handyModeGuideView.getResources().getInteger(R.integer.handy_mode_guide_animate_duration_idle));
                        } else if (i3 == 1) {
                            handyModeGuideView.mAnimator.setDuration(handyModeGuideView.getResources().getInteger(R.integer.handy_mode_guide_animate_duration_circle_showing));
                            HandyModeGuideView.this.mAnimator.setInterpolator(new LinearInterpolator());
                        } else if (i3 == 2) {
                            handyModeGuideView.mAnimator.setDuration(handyModeGuideView.getResources().getInteger(R.integer.handy_mode_guide_animate_duration_circle_moving));
                            HandyModeGuideView.this.mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        } else if (i3 == 3) {
                            handyModeGuideView.mAnimator.setDuration(handyModeGuideView.getResources().getInteger(R.integer.handy_mode_guide_animate_duration_circle_hiding));
                            HandyModeGuideView.this.mAnimator.setInterpolator(new LinearInterpolator());
                        } else if (i3 == 4) {
                            handyModeGuideView.mAnimator.setDuration(handyModeGuideView.getResources().getInteger(R.integer.handy_mode_guide_animate_duration_circle_hided));
                        }
                        HandyModeGuideView.this.mAnimator.start();
                    }
                });
            }
        });
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.display.HandyModeGuideView.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                HandyModeGuideView handyModeGuideView = HandyModeGuideView.this;
                int i2 = handyModeGuideView.mState;
                if (i2 == 2 || i2 == 1 || i2 == 3) {
                    handyModeGuideView.invalidate();
                }
            }
        });
        this.mAnimator.setDuration(getResources().getInteger(R.integer.handy_mode_guide_animate_duration_idle));
        this.mAnimator.start();
    }

    private void drawScaleScreen(Canvas canvas) {
        float floatValue;
        int i = this.mState;
        if (i != 2) {
            if (i == 3 || i == 4) {
                floatValue = 1.0f;
            }
            floatValue = 0.0f;
        } else {
            ValueAnimator valueAnimator = this.mAnimator;
            if (valueAnimator != null) {
                floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            }
            floatValue = 0.0f;
        }
        if (this.mReversing) {
            floatValue = 1.0f - floatValue;
        }
        float scale = 1.0f - (floatValue * (1.0f - HandyModeUtils.getInstance(((View) this).mContext).getScale()));
        this.mPaint.setColor(getResources().getColor(R.color.handy_mode_guide_scale_screen_color));
        canvas.save();
        canvas.scale(scale, scale, this.mIsLeft ? this.mGuidePhoneScreen.left : this.mGuidePhoneScreen.right, this.mGuidePhoneScreen.bottom);
        canvas.drawRect(this.mGuidePhoneScreen, this.mPaint);
        canvas.restore();
    }

    private void drawTouchCircle(Canvas canvas) {
        ValueAnimator valueAnimator = this.mAnimator;
        float floatValue = valueAnimator != null ? ((Float) valueAnimator.getAnimatedValue()).floatValue() : 0.0f;
        float dimension = getResources().getDimension(R.dimen.handy_mode_guide_circle_radius_large);
        float dimension2 = getResources().getDimension(R.dimen.handy_mode_guide_circle_radius_small);
        float dimension3 = getResources().getDimension(R.dimen.handy_mode_guide_circle_move_distance);
        if (this.mIsLeft) {
            dimension3 = -dimension3;
        }
        float dimension4 = getResources().getDimension(R.dimen.handy_mode_guide_circle_vertical_center);
        float width = getWidth() / 2;
        int i = this.mState;
        float f = 1.0f;
        if (i != 1) {
            if (i == 2) {
                if (this.mReversing) {
                    floatValue = 1.0f - floatValue;
                }
                width += floatValue * dimension3;
            } else if (i != 3) {
                floatValue = 0.0f;
            } else {
                f = 1.0f - floatValue;
                dimension2 += (dimension - dimension2) * floatValue;
                if (!this.mReversing) {
                    width += dimension3;
                }
            }
            floatValue = f;
        } else {
            dimension2 = dimension - ((dimension - dimension2) * floatValue);
            if (this.mReversing) {
                width += dimension3;
            }
        }
        if (floatValue > 0.0f) {
            this.mPaint.setColor((((int) (Color.alpha(r1) * floatValue)) << 24) | (getResources().getColor(R.color.handy_mode_guide_touch_circle_color) & 16777215));
            canvas.drawCircle(width, dimension4, dimension2, this.mPaint);
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.mAnimator.removeAllUpdateListeners();
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawScaleScreen(canvas);
        drawTouchCircle(canvas);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(getBackground().getIntrinsicWidth(), getBackground().getIntrinsicHeight());
    }
}
