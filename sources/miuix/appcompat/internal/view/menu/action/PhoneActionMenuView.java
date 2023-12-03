package miuix.appcompat.internal.view.menu.action;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$dimen;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.view.menu.ExpandedMenuBlurView;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;
import miuix.blurdrawable.widget.BlurBackgroundView;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.DeviceHelper;
import miuix.internal.util.ViewUtils;
import miuix.view.animation.CubicEaseOutInterpolator;

/* loaded from: classes5.dex */
public class PhoneActionMenuView extends ActionMenuView {
    private static final int[] ATTRS = {16842964, R$attr.expandBackground, R$attr.splitActionBarOverlayHeight};
    private int mActionCount;
    private Rect mBackgroundPadding;
    private View mBackgroundView;
    private BlurBackgroundView mBlurBackgroundView;
    private Drawable mCollapseBackground;
    private Context mContext;
    private Drawable mExpandBackground;
    private ExpandedMenuBlurView mExpandedMenuBlurView;
    private boolean mIsShowBlurBackground;
    private int mMaxActionButtonWidth;
    private int mMenuItemGap;
    private int mMenuItemHeight;
    private int mMenuItemWidth;
    private Drawable mOverflowBackgroundBackup;
    private OverflowMenuState mOverflowMenuState;
    private View mOverflowMenuView;
    private OverflowMenuViewAnimator mOverflowMenuViewAnimator;
    private int mSplitActionBarOverlayHeight;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public enum OverflowMenuState {
        Collapsed,
        Expanding,
        Expanded,
        Collapsing
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class OverflowMenuViewAnimator implements Animator.AnimatorListener, View.OnClickListener {
        private AnimatorSet mCollapseAnimator;
        private AnimatorSet mExpandAnimator;
        private ActionBarOverlayLayout overlayLayout;

        private OverflowMenuViewAnimator() {
        }

        private void setContentViewImportantForAccessibility(boolean z) {
            if (z) {
                this.overlayLayout.getContentView().setImportantForAccessibility(0);
            } else {
                this.overlayLayout.getContentView().setImportantForAccessibility(4);
            }
        }

        public void ensureAnimators(ActionBarOverlayLayout actionBarOverlayLayout) {
            this.overlayLayout = actionBarOverlayLayout;
            if (this.mExpandAnimator == null) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(ObjectAnimator.ofFloat(PhoneActionMenuView.this, "Value", 1.0f, 0.0f), actionBarOverlayLayout.getContentMaskAnimator(this).show());
                animatorSet.setDuration(PhoneActionMenuView.this.getResources().getInteger(17694720));
                animatorSet.setInterpolator(new CubicEaseOutInterpolator());
                animatorSet.addListener(this);
                this.mExpandAnimator = animatorSet;
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(ObjectAnimator.ofFloat(PhoneActionMenuView.this, "Value", 0.0f, 1.0f), actionBarOverlayLayout.getContentMaskAnimator(null).hide());
                animatorSet2.setDuration(PhoneActionMenuView.this.getResources().getInteger(17694720));
                animatorSet2.setInterpolator(new CubicEaseOutInterpolator());
                animatorSet2.addListener(this);
                this.mCollapseAnimator = animatorSet2;
                if (DeviceHelper.isFeatureWholeAnim()) {
                    return;
                }
                this.mExpandAnimator.setDuration(0L);
                this.mCollapseAnimator.setDuration(0L);
            }
        }

        public void hide(ActionBarOverlayLayout actionBarOverlayLayout) {
            ensureAnimators(actionBarOverlayLayout);
            this.mCollapseAnimator.cancel();
            this.mExpandAnimator.cancel();
            this.mCollapseAnimator.start();
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            if (PhoneActionMenuView.this.mOverflowMenuState == OverflowMenuState.Expanding || PhoneActionMenuView.this.mOverflowMenuState == OverflowMenuState.Expanded) {
                PhoneActionMenuView.this.setValue(0.0f);
                setContentViewImportantForAccessibility(false);
            } else if (PhoneActionMenuView.this.mOverflowMenuState == OverflowMenuState.Collapsing || PhoneActionMenuView.this.mOverflowMenuState == OverflowMenuState.Collapsed) {
                PhoneActionMenuView.this.setValue(1.0f);
                setContentViewImportantForAccessibility(true);
            }
            PhoneActionMenuView.this.postInvalidateOnAnimation();
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            if (PhoneActionMenuView.this.mOverflowMenuView != null) {
                if (PhoneActionMenuView.this.mOverflowMenuView.getTranslationY() == 0.0f) {
                    PhoneActionMenuView.this.mOverflowMenuState = OverflowMenuState.Expanded;
                    setContentViewImportantForAccessibility(false);
                } else if (PhoneActionMenuView.this.mOverflowMenuView.getTranslationY() == PhoneActionMenuView.this.getMeasuredHeight()) {
                    PhoneActionMenuView.this.mOverflowMenuState = OverflowMenuState.Collapsed;
                    setContentViewImportantForAccessibility(true);
                    if (!PhoneActionMenuView.this.mIsShowBlurBackground) {
                        PhoneActionMenuView.this.mBackgroundView.setBackground(PhoneActionMenuView.this.mCollapseBackground);
                    }
                }
            }
            PhoneActionMenuView.this.postInvalidateOnAnimation();
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (PhoneActionMenuView.this.mOverflowMenuState == OverflowMenuState.Expanded) {
                PhoneActionMenuView.this.getPresenter().hideOverflowMenu(true);
            }
        }

        public void reverse() {
            if (Build.VERSION.SDK_INT < 26) {
                ArrayList<Animator> childAnimations = this.mExpandAnimator.isRunning() ? this.mExpandAnimator.getChildAnimations() : null;
                if (this.mCollapseAnimator.isRunning()) {
                    childAnimations = this.mCollapseAnimator.getChildAnimations();
                }
                if (childAnimations == null) {
                    return;
                }
                Iterator<Animator> it = childAnimations.iterator();
                while (it.hasNext()) {
                    ((ValueAnimator) it.next()).reverse();
                }
                return;
            }
            try {
                Method declaredMethod = Class.forName("android.animation.AnimatorSet").getDeclaredMethod("reverse", new Class[0]);
                if (this.mExpandAnimator.isRunning()) {
                    declaredMethod.invoke(this.mExpandAnimator, new Object[0]);
                }
                if (this.mCollapseAnimator.isRunning()) {
                    declaredMethod.invoke(this.mCollapseAnimator, new Object[0]);
                }
            } catch (Exception e) {
                Log.e("PhoneActionMenuView", "reverse: ", e);
            }
        }

        public void show(ActionBarOverlayLayout actionBarOverlayLayout) {
            ensureAnimators(actionBarOverlayLayout);
            this.mCollapseAnimator.cancel();
            this.mExpandAnimator.cancel();
            this.mExpandAnimator.start();
        }
    }

    public PhoneActionMenuView(Context context) {
        this(context, null);
    }

    public PhoneActionMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOverflowMenuState = OverflowMenuState.Collapsed;
        this.mMaxActionButtonWidth = 0;
        this.mMenuItemGap = 0;
        this.mActionCount = 0;
        super.setBackground(null);
        this.mContext = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ATTRS);
        this.mCollapseBackground = obtainStyledAttributes.getDrawable(0);
        this.mExpandBackground = obtainStyledAttributes.getDrawable(1);
        this.mSplitActionBarOverlayHeight = obtainStyledAttributes.getDimensionPixelSize(2, 0);
        obtainStyledAttributes.recycle();
        extractBackground();
        View view = new View(context);
        this.mBackgroundView = view;
        addView(view);
        addBlurView(context);
        setChildrenDrawingOrderEnabled(true);
        this.mMaxActionButtonWidth = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_button_max_width);
        this.mMenuItemGap = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_button_gap);
    }

    private void addBlurView(Context context) {
        this.mBlurBackgroundView = new BlurBackgroundView(context);
        this.mBlurBackgroundView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.mBlurBackgroundView, 0);
        if (this.mIsShowBlurBackground) {
            setBlurBackground(true);
        }
    }

    private void clearBackground() {
        this.mBackgroundView.setBackground(null);
        setBackground(null);
    }

    private void extractBackground() {
        if (this.mBackgroundPadding == null) {
            this.mBackgroundPadding = new Rect();
        }
        Drawable drawable = this.mOverflowMenuView == null ? this.mCollapseBackground : this.mExpandBackground;
        if (drawable == null) {
            this.mBackgroundPadding.setEmpty();
        } else {
            drawable.getPadding(this.mBackgroundPadding);
        }
    }

    private int getActionMenuItemCount() {
        int childCount = getChildCount();
        if (indexOfChild(this.mOverflowMenuView) != -1) {
            childCount--;
        }
        if (indexOfChild(this.mBackgroundView) != -1) {
            childCount--;
        }
        return indexOfChild(this.mBlurBackgroundView) != -1 ? childCount - 1 : childCount;
    }

    private OverflowMenuViewAnimator getOverflowMenuViewAnimator() {
        if (this.mOverflowMenuViewAnimator == null) {
            this.mOverflowMenuViewAnimator = new OverflowMenuViewAnimator();
        }
        return this.mOverflowMenuViewAnimator;
    }

    private boolean isNotActionMenuItemChild(View view) {
        return view == this.mOverflowMenuView || view == this.mBackgroundView || view == this.mBlurBackgroundView;
    }

    private void resetBackground() {
        this.mBackgroundView.setBackground(this.mOverflowMenuState == OverflowMenuState.Collapsed ? this.mCollapseBackground : this.mExpandBackground);
        extractBackground();
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, miuix.appcompat.internal.view.menu.MenuView
    public boolean filterLeftoverView(int i) {
        ActionMenuView.LayoutParams layoutParams;
        View childAt = getChildAt(i);
        return (!isNotActionMenuItemChild(childAt) && ((layoutParams = (ActionMenuView.LayoutParams) childAt.getLayoutParams()) == null || !layoutParams.isOverflowButton)) && super.filterLeftoverView(i);
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int i, int i2) {
        int indexOfChild = indexOfChild(this.mOverflowMenuView);
        int indexOfChild2 = indexOfChild(this.mBackgroundView);
        if (i2 == 0) {
            if (indexOfChild != -1) {
                return indexOfChild;
            }
            if (indexOfChild2 != -1) {
                return indexOfChild2;
            }
        } else if (i2 == 1 && indexOfChild != -1 && indexOfChild2 != -1) {
            return indexOfChild2;
        }
        int i3 = 0;
        while (i3 < i) {
            if (i3 != indexOfChild && i3 != indexOfChild2) {
                int i4 = i3 < indexOfChild ? i3 + 1 : i3;
                if (i3 < indexOfChild2) {
                    i4++;
                }
                if (i4 == i2) {
                    return i3;
                }
            }
            i3++;
        }
        return super.getChildDrawingOrder(i, i2);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    public int getCollapsedHeight() {
        int i = this.mMenuItemHeight;
        if (i == 0) {
            return 0;
        }
        return (i + this.mBackgroundPadding.top) - this.mSplitActionBarOverlayHeight;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, miuix.appcompat.internal.view.menu.MenuView
    public boolean hasBackgroundView() {
        return getChildAt(0) == this.mBackgroundView || getChildAt(1) == this.mBackgroundView;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, miuix.appcompat.internal.view.menu.MenuView
    public boolean hasBlurBackgroundView() {
        return getChildAt(0) == this.mBlurBackgroundView || getChildAt(1) == this.mBlurBackgroundView;
    }

    public boolean hideOverflowMenu(ActionBarOverlayLayout actionBarOverlayLayout) {
        OverflowMenuState overflowMenuState = this.mOverflowMenuState;
        OverflowMenuState overflowMenuState2 = OverflowMenuState.Collapsing;
        if (overflowMenuState == overflowMenuState2 || overflowMenuState == OverflowMenuState.Collapsed) {
            return false;
        }
        OverflowMenuViewAnimator overflowMenuViewAnimator = getOverflowMenuViewAnimator();
        if (overflowMenuState == OverflowMenuState.Expanded) {
            this.mOverflowMenuState = overflowMenuState2;
            overflowMenuViewAnimator.hide(actionBarOverlayLayout);
            return true;
        } else if (overflowMenuState == OverflowMenuState.Expanding) {
            overflowMenuViewAnimator.reverse();
            return true;
        } else {
            return true;
        }
    }

    public boolean isOverflowMenuShowing() {
        OverflowMenuState overflowMenuState = this.mOverflowMenuState;
        return overflowMenuState == OverflowMenuState.Expanding || overflowMenuState == OverflowMenuState.Expanded;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mMaxActionButtonWidth = this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_button_max_width);
        this.mMenuItemGap = this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_button_gap);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6 = i3 - i;
        int i7 = i4 - i2;
        View view = this.mOverflowMenuView;
        if (view != null) {
            int measuredHeight = view.getMeasuredHeight();
            ViewUtils.layoutChildView(this, this.mOverflowMenuView, 0, 0, i6, measuredHeight);
            i5 = measuredHeight - this.mBackgroundPadding.top;
        } else {
            i5 = 0;
        }
        int i8 = i5;
        ViewUtils.layoutChildView(this, this.mBackgroundView, 0, i8, i6, i7);
        ViewUtils.layoutChildView(this, this.mBlurBackgroundView, 0, i8, i6, i7);
        int childCount = getChildCount();
        int i9 = (i6 - this.mMenuItemWidth) >> 1;
        for (int i10 = 0; i10 < childCount; i10++) {
            View childAt = getChildAt(i10);
            if (!isNotActionMenuItemChild(childAt)) {
                ViewUtils.layoutChildView(this, childAt, i9, i5, i9 + childAt.getMeasuredWidth(), i7);
                i9 += childAt.getMeasuredWidth() + this.mMenuItemGap;
            }
        }
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int childCount = getChildCount();
        int actionMenuItemCount = getActionMenuItemCount();
        this.mActionCount = actionMenuItemCount;
        if (childCount == 0 || actionMenuItemCount == 0) {
            this.mMenuItemHeight = 0;
            setMeasuredDimension(0, 0);
            return;
        }
        int size = View.MeasureSpec.getSize(i);
        int min = Math.min(size / this.mActionCount, this.mMaxActionButtonWidth);
        this.mMaxActionButtonWidth = min;
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(min, Integer.MIN_VALUE);
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (!isNotActionMenuItemChild(childAt)) {
                measureChildWithMargins(childAt, makeMeasureSpec, 0, i2, 0);
                i3 += Math.min(childAt.getMeasuredWidth(), this.mMaxActionButtonWidth);
                i4 = Math.max(i4, childAt.getMeasuredHeight());
            }
        }
        int i6 = this.mMenuItemGap;
        int i7 = this.mActionCount;
        if ((i6 * (i7 - 1)) + i3 > size) {
            this.mMenuItemGap = 0;
        }
        int i8 = i3 + (this.mMenuItemGap * (i7 - 1));
        this.mMenuItemWidth = i8;
        this.mMenuItemHeight = i4;
        View view = this.mOverflowMenuView;
        if (view != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            marginLayoutParams.topMargin = MiuixUIUtils.getStatusBarHeight(this.mContext);
            marginLayoutParams.bottomMargin = this.mMenuItemHeight;
            measureChildWithMargins(this.mOverflowMenuView, i, 0, i2, 0);
            Math.max(i8, this.mOverflowMenuView.getMeasuredWidth());
            i4 += this.mOverflowMenuView.getMeasuredHeight();
            OverflowMenuState overflowMenuState = this.mOverflowMenuState;
            if (overflowMenuState == OverflowMenuState.Expanded) {
                this.mOverflowMenuView.setTranslationY(0.0f);
            } else if (overflowMenuState == OverflowMenuState.Collapsed) {
                this.mOverflowMenuView.setTranslationY(i4);
            }
        }
        if (this.mOverflowMenuView == null) {
            i4 += this.mBackgroundPadding.top;
        }
        if (!this.mIsShowBlurBackground) {
            this.mBackgroundView.setBackground(this.mOverflowMenuState == OverflowMenuState.Collapsed ? this.mCollapseBackground : this.mExpandBackground);
        }
        setMeasuredDimension(size, i4);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
        if (DeviceHelper.isFeatureWholeAnim()) {
            setAlpha(computeAlpha(f, z, z2));
        }
        float computeTranslationY = computeTranslationY(f, z, z2);
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (!isNotActionMenuItemChild(childAt)) {
                childAt.setTranslationY(computeTranslationY);
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float y = motionEvent.getY();
        View view = this.mOverflowMenuView;
        return y > (view == null ? 0.0f : view.getTranslationY()) || super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        if (!this.mIsShowBlurBackground) {
            super.setAlpha(f);
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (!(getChildAt(i) instanceof BlurBackgroundView)) {
                getChildAt(i).setAlpha(f);
            }
        }
    }

    @Override // android.view.View
    public void setBackground(Drawable drawable) {
        if (this.mCollapseBackground != drawable) {
            this.mCollapseBackground = drawable;
            extractBackground();
        }
    }

    public boolean setBlurBackground(boolean z) {
        boolean blurBackground = this.mBlurBackgroundView.setBlurBackground(z);
        if (blurBackground) {
            this.mIsShowBlurBackground = z;
            ExpandedMenuBlurView expandedMenuBlurView = this.mExpandedMenuBlurView;
            if (expandedMenuBlurView != null) {
                expandedMenuBlurView.setBlurBackground(z);
            }
            if (this.mIsShowBlurBackground) {
                this.mBackgroundView.setAlpha(0.0f);
                ExpandedMenuBlurView expandedMenuBlurView2 = this.mExpandedMenuBlurView;
                if (expandedMenuBlurView2 != null && expandedMenuBlurView2.getChildCount() > 1) {
                    this.mOverflowBackgroundBackup = this.mExpandedMenuBlurView.getChildAt(1).getBackground();
                    this.mExpandedMenuBlurView.getChildAt(1).setBackground(null);
                }
                setBackground(null);
            } else {
                this.mBackgroundView.setAlpha(1.0f);
                ExpandedMenuBlurView expandedMenuBlurView3 = this.mExpandedMenuBlurView;
                if (expandedMenuBlurView3 != null && expandedMenuBlurView3.getChildCount() > 1 && this.mOverflowBackgroundBackup != null) {
                    this.mExpandedMenuBlurView.getChildAt(1).setBackground(this.mOverflowBackgroundBackup);
                }
            }
        }
        return blurBackground;
    }

    public void setOverflowMenuView(View view) {
        ExpandedMenuBlurView expandedMenuBlurView = this.mExpandedMenuBlurView;
        if (((expandedMenuBlurView == null || expandedMenuBlurView.getChildCount() <= 1) ? null : this.mExpandedMenuBlurView.getChildAt(1)) != view) {
            View view2 = this.mOverflowMenuView;
            if (view2 != null) {
                if (view2.getAnimation() != null) {
                    this.mOverflowMenuView.clearAnimation();
                }
                ExpandedMenuBlurView expandedMenuBlurView2 = this.mExpandedMenuBlurView;
                if (expandedMenuBlurView2 != null) {
                    expandedMenuBlurView2.removeAllViews();
                    removeView(this.mExpandedMenuBlurView);
                    this.mExpandedMenuBlurView = null;
                }
            }
            if (view != null) {
                if (this.mExpandedMenuBlurView == null) {
                    this.mExpandedMenuBlurView = new ExpandedMenuBlurView(this.mContext);
                }
                this.mExpandedMenuBlurView.addView(view);
                addView(this.mExpandedMenuBlurView);
            }
            this.mOverflowMenuView = this.mExpandedMenuBlurView;
            setBlurBackground(this.mIsShowBlurBackground);
            extractBackground();
        }
    }

    public void setValue(float f) {
        View view = this.mOverflowMenuView;
        if (view == null) {
            return;
        }
        view.setTranslationY(f * getMeasuredHeight());
    }

    public boolean showOverflowMenu(ActionBarOverlayLayout actionBarOverlayLayout) {
        OverflowMenuState overflowMenuState = this.mOverflowMenuState;
        OverflowMenuState overflowMenuState2 = OverflowMenuState.Expanding;
        if (overflowMenuState == overflowMenuState2 || overflowMenuState == OverflowMenuState.Expanded || this.mOverflowMenuView == null) {
            return false;
        }
        if (!this.mIsShowBlurBackground) {
            this.mBackgroundView.setBackground(this.mExpandBackground);
        }
        OverflowMenuViewAnimator overflowMenuViewAnimator = getOverflowMenuViewAnimator();
        if (overflowMenuState == OverflowMenuState.Collapsed) {
            this.mOverflowMenuState = overflowMenuState2;
            overflowMenuViewAnimator.show(actionBarOverlayLayout);
        } else if (overflowMenuState == OverflowMenuState.Collapsing) {
            overflowMenuViewAnimator.reverse();
        }
        postInvalidateOnAnimation();
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        return true;
    }

    public void updateBackground(boolean z) {
        this.mIsShowBlurBackground = z;
        if (z) {
            clearBackground();
        } else {
            resetBackground();
        }
        setBlurBackground(z);
    }
}
