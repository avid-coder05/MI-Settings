package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.ViewUtils;
import miuix.androidbasewidget.R$dimen;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;

/* loaded from: classes5.dex */
public class CheckedTextView extends AppCompatCheckedTextView {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private Drawable mCheckMarkDrawable;
    private int mCheckMarkMarginToText;
    private boolean mDrawCheckMark;

    public CheckedTextView(Context context) {
        this(context, null);
    }

    public CheckedTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16843720);
    }

    public CheckedTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDrawCheckMark = true;
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
        this.mCheckMarkMarginToText = (int) getContext().getResources().getDimension(R$dimen.miuix_appcompat_checked_text_view_addition_margin);
    }

    private void drawCheckMark(Canvas canvas) {
        int width;
        int scrollX;
        Drawable checkMarkDrawable = getCheckMarkDrawable();
        if (checkMarkDrawable != null) {
            int intrinsicWidth = checkMarkDrawable.getCurrent().getIntrinsicWidth();
            if (ViewUtils.isLayoutRtl(this)) {
                width = getPaddingStart();
                scrollX = getScrollX();
            } else {
                width = (getWidth() - getPaddingEnd()) - intrinsicWidth;
                scrollX = getScrollX();
            }
            int i = width + scrollX;
            int paddingTop = getPaddingTop();
            int paddingTop2 = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            int intrinsicHeight = checkMarkDrawable.getIntrinsicHeight();
            if (checkMarkDrawable.getCurrent() instanceof NinePatchDrawable) {
                intrinsicHeight = (getHeight() - paddingTop) - getPaddingBottom();
            } else {
                int gravity = getGravity() & 112;
                if (gravity == 16) {
                    paddingTop = getCheckMarkPositionY(getHeight(), intrinsicHeight, paddingTop2, paddingBottom);
                } else if (gravity == 80) {
                    paddingTop = getHeight() - intrinsicHeight;
                }
            }
            checkMarkDrawable.setBounds(i, paddingTop, intrinsicWidth + i, intrinsicHeight + paddingTop);
            checkMarkDrawable.draw(canvas);
        }
    }

    public static int getCheckMarkPositionY(int i, int i2, int i3, int i4) {
        return ((((i - i3) - i4) - i2) / 2) + i3;
    }

    private int getCheckWidth() {
        Drawable checkMarkDrawable = getCheckMarkDrawable();
        if (checkMarkDrawable == null) {
            return 0;
        }
        return checkMarkDrawable.getCurrent().getIntrinsicWidth();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.AppCompatCheckedTextView, android.widget.CheckedTextView, android.widget.TextView, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mCheckMarkDrawable != null) {
            this.mCheckMarkDrawable.setState(getDrawableState());
            invalidate();
        }
    }

    @Override // android.widget.CheckedTextView
    public Drawable getCheckMarkDrawable() {
        return this.mCheckMarkDrawable;
    }

    @Override // android.widget.CheckedTextView, android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mCheckMarkDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mCheckMarkMarginToText = (int) getContext().getResources().getDimension(R$dimen.miuix_appcompat_checked_text_view_addition_margin);
    }

    @Override // android.widget.CheckedTextView, android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (isChecked()) {
            android.widget.CheckedTextView.mergeDrawableStates(onCreateDrawableState, CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }

    @Override // android.widget.CheckedTextView, android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mDrawCheckMark) {
            drawCheckMark(canvas);
        }
        super.onDraw(canvas);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int checkWidth = getCheckWidth();
        if (checkWidth > 0) {
            checkWidth += this.mCheckMarkMarginToText;
            if (size - getPaddingEnd() < checkWidth * 2) {
                this.mDrawCheckMark = false;
                checkWidth = 0;
            } else {
                this.mDrawCheckMark = true;
            }
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(size - checkWidth, View.MeasureSpec.getMode(i)), i2);
        if (checkWidth == 0) {
            return;
        }
        setMeasuredDimension(getMeasuredWidth() + checkWidth, getMeasuredHeight());
    }

    @Override // android.widget.CheckedTextView
    public void setCheckMarkDrawable(Drawable drawable) {
        Drawable drawable2 = this.mCheckMarkDrawable;
        if (drawable2 != null) {
            drawable2.setCallback(null);
            unscheduleDrawable(this.mCheckMarkDrawable);
        }
        if (drawable != null) {
            drawable.setCallback(this);
            drawable.setVisible(getVisibility() == 0, false);
            drawable.setState(CHECKED_STATE_SET);
            setMinHeight(drawable.getIntrinsicHeight());
            drawable.setState(getDrawableState());
        }
        this.mCheckMarkDrawable = drawable;
    }

    @Override // android.widget.CheckedTextView, android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mCheckMarkDrawable;
    }
}
