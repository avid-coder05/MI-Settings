package miuix.appcompat.internal.app.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;
import miuix.appcompat.R$color;
import miuix.internal.util.ViewUtils;

/* loaded from: classes5.dex */
public class ScrollingTabTextView extends TextView {
    private ValueAnimator mAnimator;
    private int mClipPosition;
    private boolean mLeftToRight;
    private int mNormalColor;
    private ColorStateList mOriginColor;
    private int mSelectedColor;

    public ScrollingTabTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setupColors();
    }

    private void setupColors() {
        ColorStateList textColors = getTextColors();
        this.mOriginColor = textColors;
        this.mNormalColor = textColors.getColorForState(TextView.ENABLED_STATE_SET, getResources().getColor(R$color.miuix_appcompat_action_bar_tab_text_color_normal_light));
        this.mSelectedColor = this.mOriginColor.getColorForState(TextView.ENABLED_SELECTED_STATE_SET, getResources().getColor(R$color.miuix_appcompat_action_bar_tab_text_color_selected_light));
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            super.onDraw(canvas);
            return;
        }
        int i2 = ((!this.mLeftToRight || isSelected()) && (this.mLeftToRight || !isSelected())) ? this.mSelectedColor : this.mNormalColor;
        setTextColor(i2);
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int i3 = this.mClipPosition;
        int height = getHeight();
        if (isLayoutRtl) {
            i = getScrollX() + 0;
            i3 += getScrollX();
        } else {
            i = 0;
        }
        canvas.save();
        canvas.clipRect(i, 0, i3, height);
        super.onDraw(canvas);
        canvas.restore();
        int i4 = this.mNormalColor;
        if (i2 == i4) {
            i2 = this.mSelectedColor;
        } else if (i2 == this.mSelectedColor) {
            i2 = i4;
        }
        setTextColor(i2);
        int i5 = this.mClipPosition;
        int width = getWidth();
        if (isLayoutRtl) {
            i5 += getScrollX();
            width += getScrollX();
        }
        canvas.save();
        canvas.clipRect(i5, 0, width, height);
        super.onDraw(canvas);
        canvas.restore();
        setTextColor(this.mOriginColor);
    }

    @Override // android.widget.TextView
    public void setTextColor(ColorStateList colorStateList) {
        super.setTextColor(colorStateList);
        setupColors();
    }

    public void startScrollAnimation(boolean z) {
        int width;
        int i;
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator == null) {
            this.mAnimator = new ValueAnimator();
        } else {
            valueAnimator.cancel();
        }
        this.mLeftToRight = z;
        if (z) {
            i = getWidth();
            width = 0;
        } else {
            width = getWidth();
            i = 0;
        }
        this.mAnimator.setIntValues(width, i);
        this.mAnimator.setDuration(200L);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.appcompat.internal.app.widget.ScrollingTabTextView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ScrollingTabTextView.this.mClipPosition = ((Integer) valueAnimator2.getAnimatedValue()).intValue();
                ScrollingTabTextView.this.invalidate();
            }
        });
        this.mAnimator.addListener(new AnimatorListenerAdapter() { // from class: miuix.appcompat.internal.app.widget.ScrollingTabTextView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ScrollingTabTextView scrollingTabTextView = ScrollingTabTextView.this;
                scrollingTabTextView.mClipPosition = scrollingTabTextView.getWidth();
            }
        });
        this.mAnimator.start();
    }
}
