package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.appcompat.R$bool;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$styleable;
import miuix.appcompat.app.ActionBarTransitionListener;
import miuix.appcompat.internal.view.menu.action.ActionMenuPresenter;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;
import miuix.internal.util.DeviceHelper;
import miuix.internal.util.ViewUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public abstract class AbsActionBarView extends ViewGroup {
    protected ActionMenuPresenter mActionMenuPresenter;
    protected TransitionListener mAnimConfigListener;
    protected AnimConfig mCollapseAnimHideConfig;
    protected AnimConfig mCollapseAnimShowConfig;
    private int mExpandState;
    protected AnimConfig mHideProcessConfig;
    int mInnerExpandState;
    float mLastProcess;
    protected ActionMenuView mMenuView;
    protected AnimConfig mMovableAnimConfig;
    private boolean mResizable;
    protected AnimConfig mShowProcessConfig;
    protected boolean mSplitActionBar;
    protected ActionBarContainer mSplitView;
    protected boolean mSplitWhenNarrow;
    protected int mSubtitlePaddingV;
    protected int mTitleMaxHeight;
    protected int mTitleMinHeight;
    protected int mTitlePaddingV;
    ActionBarTransitionListener mTransitionListener;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes5.dex */
    public static class CollapseView {
        private float mAlpha;
        private List<View> mViews = new ArrayList();
        private boolean mIsAcceptAlphaChange = true;

        public void animTo(float f, int i, int i2, AnimConfig animConfig) {
            AnimState animState = new AnimState("to");
            ViewProperty viewProperty = ViewProperty.ALPHA;
            if (!this.mIsAcceptAlphaChange) {
                f = this.mAlpha;
            }
            AnimState add = animState.add(viewProperty, f).add(ViewProperty.TRANSLATION_X, i).add(ViewProperty.TRANSLATION_Y, i2);
            Iterator<View> it = this.mViews.iterator();
            while (it.hasNext()) {
                Folme.useAt(it.next()).state().to(add, animConfig);
            }
        }

        public void attachViews(View view) {
            if (this.mViews.contains(view)) {
                return;
            }
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: miuix.appcompat.internal.app.widget.AbsActionBarView.CollapseView.1
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View view2) {
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View view2) {
                    Iterator it = CollapseView.this.mViews.iterator();
                    while (it.hasNext()) {
                        Folme.clean((View) it.next());
                    }
                }
            });
            this.mViews.add(view);
        }

        public void onHide() {
            Iterator<View> it = this.mViews.iterator();
            while (it.hasNext()) {
                it.next().setEnabled(false);
            }
        }

        public void onShow() {
            Iterator<View> it = this.mViews.iterator();
            while (it.hasNext()) {
                it.next().setEnabled(true);
            }
        }

        public void setAcceptAlphaChange(boolean z) {
            this.mIsAcceptAlphaChange = z;
        }

        public void setAlpha(float f) {
            this.mAlpha = f;
            Iterator<View> it = this.mViews.iterator();
            while (it.hasNext()) {
                Folme.useAt(it.next()).state().setTo(ViewProperty.ALPHA, Float.valueOf(f));
            }
        }

        public void setAnimFrom(float f, int i, int i2) {
            AnimState animState = new AnimState("from");
            ViewProperty viewProperty = ViewProperty.ALPHA;
            if (!this.mIsAcceptAlphaChange) {
                f = this.mAlpha;
            }
            AnimState add = animState.add(viewProperty, f).add(ViewProperty.TRANSLATION_X, i).add(ViewProperty.TRANSLATION_Y, i2);
            Iterator<View> it = this.mViews.iterator();
            while (it.hasNext()) {
                Folme.useAt(it.next()).state().setTo(add);
            }
        }

        public void setVisibility(int i) {
            Iterator<View> it = this.mViews.iterator();
            while (it.hasNext()) {
                it.next().setVisibility(i);
            }
        }
    }

    AbsActionBarView(Context context) {
        this(context, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbsActionBarView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbsActionBarView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mInnerExpandState = 1;
        this.mExpandState = 1;
        this.mResizable = true;
        this.mLastProcess = 0.0f;
        this.mAnimConfigListener = new TransitionListener() { // from class: miuix.appcompat.internal.app.widget.AbsActionBarView.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                super.onBegin(obj);
                ActionBarTransitionListener actionBarTransitionListener = AbsActionBarView.this.mTransitionListener;
                if (actionBarTransitionListener != null) {
                    actionBarTransitionListener.onTransitionBegin(obj);
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                super.onComplete(obj);
                ActionBarTransitionListener actionBarTransitionListener = AbsActionBarView.this.mTransitionListener;
                if (actionBarTransitionListener != null) {
                    actionBarTransitionListener.onTransitionComplete(obj);
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
                ActionBarTransitionListener actionBarTransitionListener = AbsActionBarView.this.mTransitionListener;
                if (actionBarTransitionListener != null) {
                    actionBarTransitionListener.onTransitionUpdate(obj, collection);
                }
            }
        };
        this.mTitlePaddingV = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_bar_title_collapse_padding_vertical);
        this.mSubtitlePaddingV = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_bar_subtitle_collapse_padding_vertical);
        this.mCollapseAnimShowConfig = new AnimConfig().setEase(-2, 1.0f, 0.3f);
        this.mShowProcessConfig = new AnimConfig().setEase(-2, 1.0f, 0.3f).addListeners(this.mAnimConfigListener);
        this.mCollapseAnimHideConfig = new AnimConfig().setEase(-2, 1.0f, 0.15f);
        this.mHideProcessConfig = new AnimConfig().setEase(-2, 1.0f, 0.15f).addListeners(this.mAnimConfigListener);
        this.mMovableAnimConfig = new AnimConfig().setEase(-2, 1.0f, 0.6f);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ActionBar, 16843470, 0);
        int i2 = obtainStyledAttributes.getInt(R$styleable.ActionBar_expandState, 1);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.ActionBar_resizable, true);
        obtainStyledAttributes.recycle();
        if (i2 == 0 || (getContext().getResources().getConfiguration().orientation == 2 && !DeviceHelper.isTablet(getContext()))) {
            this.mInnerExpandState = 0;
            this.mExpandState = 0;
        } else {
            this.mInnerExpandState = 1;
            this.mExpandState = 1;
        }
        this.mResizable = z;
    }

    private boolean isRestricted() {
        return getContext().getResources().getConfiguration().orientation == 2 && !DeviceHelper.isTablet(getContext());
    }

    private void setTitleMaxHeight(int i) {
        this.mTitleMaxHeight = i;
        requestLayout();
    }

    private void setTitleMinHeight(int i) {
        this.mTitleMinHeight = i;
        requestLayout();
    }

    int getActionBarStyle() {
        return 16843470;
    }

    public ActionBarTransitionListener getActionBarTransitionListener() {
        return this.mTransitionListener;
    }

    public ActionMenuView getActionMenuView() {
        return this.mMenuView;
    }

    public int getAnimatedVisibility() {
        return getVisibility();
    }

    public int getExpandState() {
        return this.mExpandState;
    }

    public ActionMenuView getMenuView() {
        return this.mMenuView;
    }

    public boolean hideOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.hideOverflowMenu(false);
    }

    public boolean isOverflowMenuShowing() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowReserved() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowReserved();
    }

    public boolean isResizable() {
        return this.mResizable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int measureChildView(View view, int i, int i2, int i3) {
        view.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), i2);
        return Math.max(0, (i - view.getMeasuredWidth()) - i3);
    }

    protected void onAnimatedExpandStateChanged(int i, int i2) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(null, R$styleable.ActionBar, getActionBarStyle(), 0);
        setTitleMinHeight(obtainStyledAttributes.getLayoutDimension(R$styleable.ActionBar_android_height, 0));
        setTitleMaxHeight(obtainStyledAttributes.getLayoutDimension(R$styleable.ActionBar_android_maxHeight, 0));
        obtainStyledAttributes.recycle();
        if (this.mSplitWhenNarrow) {
            setSplitActionBar(getContext().getResources().getBoolean(R$bool.abc_split_action_bar_is_narrow));
        }
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.onConfigurationChanged(configuration);
        }
        if (getContext().getResources().getConfiguration().orientation != 2 || DeviceHelper.isTablet(getContext())) {
            return;
        }
        setExpandState(0);
    }

    protected abstract void onExpandStateChanged(int i, int i2);

    /* JADX INFO: Access modifiers changed from: protected */
    public int positionChild(View view, int i, int i2, int i3) {
        return positionChild(view, i, i2, i3, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int positionChild(View view, int i, int i2, int i3, boolean z) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i4 = i2 + ((i3 - measuredHeight) / 2);
        if (!z) {
            i4 = (this.mTitleMinHeight - measuredHeight) / 2;
        }
        int i5 = i4;
        ViewUtils.layoutChildView(this, view, i, i5, i + measuredWidth, i5 + measuredHeight);
        return measuredWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int positionChildInverse(View view, int i, int i2, int i3) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i4 = (this.mTitleMinHeight - measuredHeight) / 2;
        ViewUtils.layoutChildView(this, view, i - measuredWidth, i4, i, i4 + measuredHeight);
        return measuredWidth;
    }

    public void postShowOverflowMenu() {
        post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.AbsActionBarView.2
            @Override // java.lang.Runnable
            public void run() {
                AbsActionBarView.this.showOverflowMenu();
            }
        });
    }

    public void setActionBarTransitionListener(ActionBarTransitionListener actionBarTransitionListener) {
        this.mTransitionListener = actionBarTransitionListener;
    }

    public void setExpandState(int i) {
        setExpandState(i, false, false);
    }

    public void setExpandState(int i, boolean z, boolean z2) {
        int i2;
        if ((!isRestricted() || z2 || i == 0) && this.mResizable && (i2 = this.mInnerExpandState) != i) {
            if (z) {
                onAnimatedExpandStateChanged(i2, i);
                return;
            }
            this.mInnerExpandState = i;
            if (i == 0) {
                this.mExpandState = 0;
            } else if (i == 1) {
                this.mExpandState = 1;
            }
            onExpandStateChanged(i2, i);
            requestLayout();
        }
    }

    public void setResizable(boolean z) {
        this.mResizable = z;
    }

    public void setSplitActionBar(boolean z) {
        this.mSplitActionBar = z;
    }

    public void setSplitView(ActionBarContainer actionBarContainer) {
        this.mSplitView = actionBarContainer;
    }

    public void setSplitWhenNarrow(boolean z) {
        this.mSplitWhenNarrow = z;
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        if (i != getVisibility()) {
            super.setVisibility(i);
        }
    }

    public boolean showOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.showOverflowMenu();
    }
}
