package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/* loaded from: classes2.dex */
public class FontLinearLayout extends LinearLayout {
    private boolean mIsDragging;
    private OnFontLinearLayoutClickListener mOnFontLinearLayoutClickListener;
    private float mScale;
    private int mScaledTouchSlop;
    private float mTouchDownX;
    private float mTouchUpX;

    /* loaded from: classes2.dex */
    public interface OnFontLinearLayoutClickListener {
        void onStopTrackingTouch(FontLinearLayout fontLinearLayout, float f);
    }

    public FontLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private boolean trackTouchEvent(MotionEvent motionEvent) {
        if (Math.abs(this.mTouchUpX - this.mTouchDownX) < this.mScaledTouchSlop) {
            int width = getWidth();
            int i = (width - ((LinearLayout) this).mPaddingLeft) - ((LinearLayout) this).mPaddingRight;
            int x = (int) motionEvent.getX();
            if (x < ((LinearLayout) this).mPaddingLeft) {
                this.mScale = 0.0f;
                return true;
            } else if (x > width - ((LinearLayout) this).mPaddingRight) {
                this.mScale = 1.0f;
                return true;
            } else {
                this.mScale = (x - r2) / i;
                return true;
            }
        }
        return false;
    }

    void onStartTrackingTouch() {
        this.mIsDragging = true;
    }

    void onStopTrackingTouch() {
        this.mIsDragging = false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        OnFontLinearLayoutClickListener onFontLinearLayoutClickListener;
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mTouchDownX = motionEvent.getX();
            onStartTrackingTouch();
        } else if (action == 1 && this.mIsDragging) {
            this.mTouchUpX = motionEvent.getX();
            onStopTrackingTouch();
            if (trackTouchEvent(motionEvent) && (onFontLinearLayoutClickListener = this.mOnFontLinearLayoutClickListener) != null) {
                onFontLinearLayoutClickListener.onStopTrackingTouch(this, this.mScale);
            }
        }
        return true;
    }

    public void setOnFontLinearLayoutClickListener(OnFontLinearLayoutClickListener onFontLinearLayoutClickListener) {
        this.mOnFontLinearLayoutClickListener = onFontLinearLayoutClickListener;
    }
}
