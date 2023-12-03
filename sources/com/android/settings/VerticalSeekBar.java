package com.android.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.AbsSeekBar;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class VerticalSeekBar extends AbsSeekBar {
    private Rect mBounds;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private final Method mSetProgress;
    private Drawable mThumb;

    /* loaded from: classes.dex */
    public interface OnSeekBarChangeListener {
        void onProgressChanged(VerticalSeekBar verticalSeekBar, int i, boolean z);

        void onStartTrackingTouch(VerticalSeekBar verticalSeekBar);

        void onStopTrackingTouch(VerticalSeekBar verticalSeekBar);
    }

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBounds = new Rect();
        Method method = null;
        try {
            method = getClass().getSuperclass().getSuperclass().getDeclaredMethod("setProgress", Integer.TYPE, Boolean.TYPE);
            method.setAccessible(true);
        } catch (Exception unused) {
        }
        this.mSetProgress = method;
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void setProgressOnMove(float f) {
        OnSeekBarChangeListener onSeekBarChangeListener;
        int progress = getProgress();
        try {
            this.mSetProgress.invoke(this, Integer.valueOf((int) f), Boolean.TRUE);
        } catch (Exception unused) {
            setProgress((int) f);
        }
        if (progress == getProgress() || (onSeekBarChangeListener = this.mOnSeekBarChangeListener) == null) {
            return;
        }
        onSeekBarChangeListener.onProgressChanged(this, getProgress(), true);
    }

    private void trackTouchEvent(MotionEvent motionEvent) {
        Drawable drawable = this.mThumb;
        int intrinsicHeight = drawable != null ? drawable.getIntrinsicHeight() : 0;
        int height = getHeight();
        int i = ((height - intrinsicHeight) - ((AbsSeekBar) this).mPaddingBottom) - ((AbsSeekBar) this).mPaddingTop;
        int y = (int) motionEvent.getY();
        setProgressOnMove(Math.round(getMax() * (y > height - getPaddingBottom() ? 0.0f : y < getPaddingTop() ? 1.0f : (((height - ((AbsSeekBar) this).mPaddingBottom) - y) - (intrinsicHeight / 2)) / i)));
    }

    @Override // android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        KeyEvent keyEvent2;
        if (keyEvent.getAction() == 0) {
            switch (keyEvent.getKeyCode()) {
                case 19:
                    keyEvent2 = new KeyEvent(0, 22);
                    break;
                case 20:
                    keyEvent2 = new KeyEvent(0, 21);
                    break;
                case 21:
                    keyEvent2 = new KeyEvent(0, 20);
                    break;
                case 22:
                    keyEvent2 = new KeyEvent(0, 19);
                    break;
                default:
                    keyEvent2 = null;
                    break;
            }
            if (keyEvent2 != null) {
                return keyEvent2.dispatch(this, null, null);
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onDraw(Canvas canvas) {
        Rect rect = this.mBounds;
        int height = getHeight();
        int width = getWidth();
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            drawable.copyBounds(rect);
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int progress = height - (((height - intrinsicHeight) * getProgress()) / getMax());
            int i = ((AbsSeekBar) this).mPaddingLeft;
            drawable.setBounds(i, progress - intrinsicHeight, intrinsicWidth + i, progress);
            drawable.draw(canvas);
            drawable.setBounds(rect);
        }
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.copyBounds(rect);
            progressDrawable.setLevel(10000);
            int i2 = ((AbsSeekBar) this).mPaddingBottom;
            progressDrawable.setBounds(((AbsSeekBar) this).mPaddingLeft, (height - i2) - ((((height - i2) - ((AbsSeekBar) this).mPaddingTop) * getProgress()) / getMax()), width - ((AbsSeekBar) this).mPaddingRight, height - ((AbsSeekBar) this).mPaddingBottom);
            progressDrawable.draw(canvas);
            progressDrawable.setBounds(rect);
        }
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onMeasure(int i, int i2) {
        Drawable background = getBackground();
        if (background == null || background.getIntrinsicWidth() <= 0) {
            throw new IllegalStateException("No background!");
        }
        setMeasuredDimension(background.getIntrinsicWidth(), background.getIntrinsicHeight());
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i2, i, i3, i4);
    }

    void onStartVerticalTrackingTouch() {
        OnSeekBarChangeListener onSeekBarChangeListener = this.mOnSeekBarChangeListener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    void onStopVerticalTrackingTouch() {
        OnSeekBarChangeListener onSeekBarChangeListener = this.mOnSeekBarChangeListener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            int action = motionEvent.getAction();
            if (action == 0) {
                setPressed(true);
                onStartVerticalTrackingTouch();
                trackTouchEvent(motionEvent);
            } else if (action == 1) {
                trackTouchEvent(motionEvent);
                onStopVerticalTrackingTouch();
                setPressed(false);
            } else if (action == 2) {
                trackTouchEvent(motionEvent);
                attemptClaimDrag();
            } else if (action == 3) {
                onStopVerticalTrackingTouch();
                setPressed(false);
            }
            return true;
        }
        return false;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    @Override // android.widget.AbsSeekBar
    public void setThumb(Drawable drawable) {
        this.mThumb = drawable;
        super.setThumb(drawable);
    }
}
