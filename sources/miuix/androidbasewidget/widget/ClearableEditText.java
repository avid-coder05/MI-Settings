package miuix.androidbasewidget.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import androidx.appcompat.widget.ViewUtils;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import java.lang.ref.WeakReference;
import java.util.List;
import miuix.androidbasewidget.R$attr;
import miuix.androidbasewidget.R$string;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes5.dex */
public class ClearableEditText extends EditText {
    private static final int[] EMPTY_STATE_SET = {16842921};
    private AccessHelper mAccessHelper;
    private Drawable mDrawable;
    private boolean mPressed;
    private boolean mShown;
    private ShowWidgetTextWatcher mTextWatcher;

    /* loaded from: classes5.dex */
    private class AccessHelper extends ExploreByTouchHelper {
        private final View forView;
        private final Rect mTempParentBounds;

        public AccessHelper(View view) {
            super(view);
            this.mTempParentBounds = new Rect();
            this.forView = view;
        }

        private void getChildRect(Rect rect) {
            this.forView.getLocalVisibleRect(this.mTempParentBounds);
            int intrinsicWidth = ClearableEditText.this.mDrawable == null ? 0 : ClearableEditText.this.mDrawable.getIntrinsicWidth();
            if (ViewUtils.isLayoutRtl(ClearableEditText.this)) {
                rect.right -= (ClearableEditText.this.getWidth() - intrinsicWidth) - (ClearableEditText.this.getPaddingLeft() * 2);
            } else {
                rect.left += (ClearableEditText.this.getWidth() - (ClearableEditText.this.getPaddingRight() * 2)) - intrinsicWidth;
            }
        }

        private CharSequence getDescription() {
            return ClearableEditText.this.getResources().getString(R$string.clearable_edittext_clear_description);
        }

        private boolean isVirtualView(float f, float f2) {
            int intrinsicWidth = ClearableEditText.this.mDrawable == null ? 0 : ClearableEditText.this.mDrawable.getIntrinsicWidth();
            return ViewUtils.isLayoutRtl(ClearableEditText.this) ? f < ((float) (intrinsicWidth + (ClearableEditText.this.getPaddingLeft() * 2))) : f > ((float) ((ClearableEditText.this.getWidth() - (ClearableEditText.this.getPaddingRight() * 2)) - intrinsicWidth));
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected int getVirtualViewAt(float f, float f2) {
            return (ClearableEditText.this.mShown && isVirtualView(f, f2)) ? 0 : Integer.MIN_VALUE;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void getVisibleVirtualViews(List list) {
            if (ClearableEditText.this.mShown) {
                list.add(0);
            }
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            if (i != Integer.MIN_VALUE && i2 == 16) {
                ClearableEditText.this.onClearButtonClick();
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateEventForHost(AccessibilityEvent accessibilityEvent) {
            super.onPopulateEventForHost(accessibilityEvent);
            if (accessibilityEvent.getEventType() == 2048 && !ClearableEditText.this.mShown && this.forView.isFocused()) {
                this.forView.sendAccessibilityEvent(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.setContentDescription(getDescription());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateNodeForHost(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onPopulateNodeForHost(accessibilityNodeInfoCompat);
            if (ClearableEditText.this.mShown) {
                accessibilityNodeInfoCompat.setClassName(ClearableEditText.class.getName());
            }
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setContentDescription(getDescription());
            accessibilityNodeInfoCompat.addAction(16);
            accessibilityNodeInfoCompat.setClassName(Button.class.getName());
            getChildRect(this.mTempParentBounds);
            accessibilityNodeInfoCompat.setBoundsInParent(this.mTempParentBounds);
            accessibilityNodeInfoCompat.setClickable(true);
        }
    }

    /* loaded from: classes5.dex */
    static class ShowWidgetTextWatcher implements TextWatcher {
        WeakReference<ClearableEditText> mRef;

        ShowWidgetTextWatcher(ClearableEditText clearableEditText) {
            this.mRef = new WeakReference<>(clearableEditText);
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            ClearableEditText clearableEditText = this.mRef.get();
            if (clearableEditText == null) {
                return;
            }
            if (clearableEditText.mShown != (editable.length() > 0)) {
                clearableEditText.mShown = !clearableEditText.mShown;
                clearableEditText.refreshDrawableState();
                if (clearableEditText.mAccessHelper != null) {
                    clearableEditText.mAccessHelper.invalidateRoot();
                }
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    public ClearableEditText(Context context) {
        this(context, null);
    }

    public ClearableEditText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.clearableEditTextStyle);
    }

    public ClearableEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTextWatcher = new ShowWidgetTextWatcher(this);
        this.mDrawable = getCompoundDrawablesRelative()[2];
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 19) {
            AccessHelper accessHelper = new AccessHelper(this);
            this.mAccessHelper = accessHelper;
            ViewCompat.setAccessibilityDelegate(this, accessHelper);
        }
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
        if (i2 >= 29) {
            setForceDarkAllowed(false);
        }
        Editable text = getText();
        if (text != null) {
            if (this.mShown != (text.length() > 0)) {
                this.mShown = !this.mShown;
                refreshDrawableState();
            }
        }
    }

    private boolean dispatchWidgetTouchEvent(MotionEvent motionEvent) {
        if (this.mShown) {
            Drawable drawable = this.mDrawable;
            int intrinsicWidth = drawable == null ? 0 : drawable.getIntrinsicWidth();
            if (ViewUtils.isLayoutRtl(this)) {
                if (motionEvent.getX() < intrinsicWidth + getPaddingLeft()) {
                    return onButtonTouchEvent(motionEvent);
                }
            } else if (motionEvent.getX() > (getWidth() - getPaddingRight()) - intrinsicWidth) {
                return onButtonTouchEvent(motionEvent);
            }
        }
        this.mPressed = false;
        return false;
    }

    private boolean onButtonTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action == 3 && this.mPressed) {
                    this.mPressed = false;
                }
            } else if (isEnabled() && this.mPressed) {
                onClearButtonClick();
                this.mPressed = false;
                return true;
            }
        } else if (isEnabled() && this.mShown) {
            this.mPressed = true;
        }
        return this.mPressed;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClearButtonClick() {
        setText("");
        HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_TAP_LIGHT);
    }

    @Override // android.view.View
    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        AccessHelper accessHelper;
        if (Build.VERSION.SDK_INT < 19 || (accessHelper = this.mAccessHelper) == null || !this.mShown || !accessHelper.dispatchHoverEvent(motionEvent)) {
            return super.dispatchHoverEvent(motionEvent);
        }
        return true;
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return dispatchWidgetTouchEvent(motionEvent) || super.dispatchTouchEvent(motionEvent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.AppCompatEditText, android.widget.TextView, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mDrawable != null) {
            this.mDrawable.setState(getDrawableState());
            invalidate();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addTextChangedListener(this.mTextWatcher);
    }

    @Override // android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (!this.mShown) {
            android.widget.EditText.mergeDrawableStates(onCreateDrawableState, EMPTY_STATE_SET);
        }
        return onCreateDrawableState;
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeTextChangedListener(this.mTextWatcher);
    }

    @Override // android.widget.TextView, android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        super.onRestoreInstanceState(parcelable);
        Editable text = getText();
        if (text != null) {
            if (this.mShown != (text.length() > 0)) {
                this.mShown = !this.mShown;
                refreshDrawableState();
            }
        }
    }

    @Override // android.widget.TextView
    public void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        if (drawable != null || drawable2 != null || drawable3 != null || drawable4 != null) {
            throw new IllegalStateException("ClearableEditText can only set drawables by setCompoundDrawablesRelative()");
        }
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
    }

    @Override // android.widget.TextView
    public void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        this.mDrawable = drawable3;
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
    }

    @Override // android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mDrawable;
    }
}
