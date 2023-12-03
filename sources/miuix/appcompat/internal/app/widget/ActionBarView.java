package miuix.appcompat.internal.app.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.CollapsibleActionView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import java.lang.ref.WeakReference;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.IStateStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$bool;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;
import miuix.appcompat.R$string;
import miuix.appcompat.R$styleable;
import miuix.appcompat.app.ActionBarDelegateImpl;
import miuix.appcompat.app.ActionBarTransitionListener;
import miuix.appcompat.internal.app.widget.AbsActionBarView;
import miuix.appcompat.internal.app.widget.actionbar.CollapseTitle;
import miuix.appcompat.internal.app.widget.actionbar.ExpandTitle;
import miuix.appcompat.internal.util.ActionBarViewFactory;
import miuix.appcompat.internal.view.ActionBarPolicy;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.MenuItemImpl;
import miuix.appcompat.internal.view.menu.MenuPresenter;
import miuix.appcompat.internal.view.menu.SubMenuBuilder;
import miuix.appcompat.internal.view.menu.action.ActionMenuItem;
import miuix.appcompat.internal.view.menu.action.ActionMenuPresenter;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.DeviceHelper;
import miuix.internal.util.ViewUtils;
import miuix.springback.view.SpringBackLayout;
import miuix.view.ActionModeAnimationListener;

/* loaded from: classes5.dex */
public class ActionBarView extends AbsActionBarView implements ActionModeAnimationListener {
    protected TransitionListener mAlphaListener;
    private ActionBar.OnNavigationListener mCallback;
    protected TransitionListener mCollapseAnimHideConfigListener;
    protected TransitionListener mCollapseAnimShowConfigListener;
    private FrameLayout mCollapseContainer;
    private AbsActionBarView.CollapseView mCollapseController;
    private FrameLayout mCollapseCustomContainer;
    int mCollapseHeight;
    private int mCollapseSubtitleStyleRes;
    private SpringBackLayout mCollapseTabContainer;
    private int mCollapseTabHeight;
    private ScrollingTabContainerView mCollapseTabs;
    private CollapseTitle mCollapseTitle;
    private boolean mCollapseTitleShowable;
    private int mCollapseTitleStyleRes;
    private Context mContext;
    private View mCustomNavView;
    private final TextWatcher mCustomTitleWatcher;
    private int mDisplayOptions;
    private View mEndView;
    private int mExpandSubtitlePaddingBottom;
    private int mExpandTabTopPadding;
    private ScrollingTabContainerView mExpandTabs;
    private ExpandTitle mExpandTitle;
    private int mExpandTitlePaddingBottom;
    View mExpandedActionView;
    private final View.OnClickListener mExpandedActionViewUpListener;
    private HomeView mExpandedHomeLayout;
    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    private Drawable mHomeAsUpIndicatorDrawable;
    private int mHomeAsUpIndicatorResId;
    private HomeView mHomeLayout;
    private final int mHomeResId;
    private Drawable mIcon;
    private int mIconLogoInitIndicator;
    private View mImmersionView;
    private boolean mInActionMode;
    private boolean mInActionModeAnimating;
    private boolean mInSearchMode;
    private boolean mIncludeTabs;
    private ProgressBar mIndeterminateProgressView;
    private boolean mIsCollapsed;
    private boolean mIsTitleHidding;
    private boolean mIsTitleShowing;
    private int mItemPadding;
    private LinearLayout mListNavLayout;
    private Drawable mLogo;
    private ActionMenuItem mLogoNavItem;
    private FrameLayout mMovableContainer;
    private AbsActionBarView.CollapseView mMovableController;
    private SpringBackLayout mMovableTabContainer;
    private int mMovableTabHeight;
    private final AdapterView.OnItemSelectedListener mNavItemSelectedListener;
    private int mNavigationMode;
    private boolean mNonTouchScrolling;
    private MenuBuilder mOptionsMenu;
    private int mPendingHeight;
    private Runnable mPostScroll;
    private Scroller mPostScroller;
    private int mProgressBarPadding;
    private ProgressBar mProgressView;
    private ScrollingTabContainerView mSecondaryCollapseTabs;
    private ScrollingTabContainerView mSecondaryExpandTabs;
    private Spinner mSpinner;
    private SpinnerAdapter mSpinnerAdapter;
    private View mStartView;
    private IStateStyle mStateChangeAnimStateStyle;
    private CharSequence mSubtitle;
    private boolean mTempResizable;
    private CharSequence mTitle;
    private boolean mTitleCenter;
    private final View.OnClickListener mTitleClickListener;
    private ActionMenuItem mTitleNavItem;
    private View mTitleUpView;
    private boolean mTouchScrolling;
    private int mTransitionTarget;
    private int mUncollapsePaddingH;
    private int mUncollapseTabPaddingH;
    private final View.OnClickListener mUpClickListener;
    private boolean mUserTitle;
    Window.Callback mWindowCallback;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class ExpandedActionViewMenuPresenter implements MenuPresenter {
        MenuItemImpl mCurrentExpandedItem;
        MenuBuilder mMenu;

        private ExpandedActionViewMenuPresenter() {
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public boolean collapseItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
            View view = ActionBarView.this.mExpandedActionView;
            if (view instanceof CollapsibleActionView) {
                ((CollapsibleActionView) view).onActionViewCollapsed();
            }
            ActionBarView actionBarView = ActionBarView.this;
            actionBarView.removeView(actionBarView.mExpandedActionView);
            ActionBarView actionBarView2 = ActionBarView.this;
            actionBarView2.removeView(actionBarView2.mExpandedHomeLayout);
            ActionBarView actionBarView3 = ActionBarView.this;
            actionBarView3.mExpandedActionView = null;
            if ((actionBarView3.mDisplayOptions & 2) != 0) {
                ActionBarView.this.mHomeLayout.setVisibility(0);
            }
            if ((ActionBarView.this.mDisplayOptions & 8) != 0) {
                if (ActionBarView.this.mCollapseTitle == null) {
                    ActionBarView.this.initTitle();
                } else {
                    ActionBarView.this.setTitleVisibility(true);
                }
            }
            if (ActionBarView.this.mCollapseTabs != null && ActionBarView.this.mNavigationMode == 2) {
                ActionBarView.this.mCollapseTabs.setVisibility(0);
            }
            if (ActionBarView.this.mExpandTabs != null && ActionBarView.this.mNavigationMode == 2) {
                ActionBarView.this.mExpandTabs.setVisibility(0);
            }
            if (ActionBarView.this.mSecondaryCollapseTabs != null && ActionBarView.this.mNavigationMode == 2) {
                ActionBarView.this.mSecondaryCollapseTabs.setVisibility(0);
            }
            if (ActionBarView.this.mSecondaryExpandTabs != null && ActionBarView.this.mNavigationMode == 2) {
                ActionBarView.this.mSecondaryExpandTabs.setVisibility(0);
            }
            if (ActionBarView.this.mSpinner != null && ActionBarView.this.mNavigationMode == 1) {
                ActionBarView.this.mSpinner.setVisibility(0);
            }
            if (ActionBarView.this.mCustomNavView != null && (ActionBarView.this.mDisplayOptions & 16) != 0) {
                ActionBarView.this.mCustomNavView.setVisibility(0);
            }
            ActionBarView.this.mExpandedHomeLayout.setIcon(null);
            this.mCurrentExpandedItem = null;
            ActionBarView.this.requestLayout();
            menuItemImpl.setActionViewExpanded(false);
            return true;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public boolean expandItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
            ActionBarView.this.mExpandedActionView = menuItemImpl.getActionView();
            ActionBarView.this.initExpandedHomeLayout();
            ActionBarView.this.mExpandedHomeLayout.setIcon(ActionBarView.this.getIcon().getConstantState().newDrawable(ActionBarView.this.getResources()));
            this.mCurrentExpandedItem = menuItemImpl;
            ViewParent parent = ActionBarView.this.mExpandedActionView.getParent();
            ActionBarView actionBarView = ActionBarView.this;
            if (parent != actionBarView) {
                actionBarView.addView(actionBarView.mExpandedActionView);
            }
            ViewParent parent2 = ActionBarView.this.mExpandedHomeLayout.getParent();
            ActionBarView actionBarView2 = ActionBarView.this;
            if (parent2 != actionBarView2) {
                actionBarView2.addView(actionBarView2.mExpandedHomeLayout);
            }
            if (ActionBarView.this.mHomeLayout != null) {
                ActionBarView.this.mHomeLayout.setVisibility(8);
            }
            if (ActionBarView.this.mCollapseTitle != null) {
                ActionBarView.this.setTitleVisibility(false);
            }
            if (ActionBarView.this.mCollapseTabs != null) {
                ActionBarView.this.mCollapseTabs.setVisibility(8);
            }
            if (ActionBarView.this.mExpandTabs != null) {
                ActionBarView.this.mExpandTabs.setVisibility(8);
            }
            if (ActionBarView.this.mSecondaryCollapseTabs != null) {
                ActionBarView.this.mSecondaryCollapseTabs.setVisibility(8);
            }
            if (ActionBarView.this.mSecondaryExpandTabs != null) {
                ActionBarView.this.mSecondaryExpandTabs.setVisibility(8);
            }
            if (ActionBarView.this.mSpinner != null) {
                ActionBarView.this.mSpinner.setVisibility(8);
            }
            if (ActionBarView.this.mCustomNavView != null) {
                ActionBarView.this.mCustomNavView.setVisibility(8);
            }
            ActionBarView.this.requestLayout();
            menuItemImpl.setActionViewExpanded(true);
            View view = ActionBarView.this.mExpandedActionView;
            if (view instanceof CollapsibleActionView) {
                ((CollapsibleActionView) view).onActionViewExpanded();
            }
            return true;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public boolean flagActionItems() {
            return false;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public int getId() {
            return 0;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public void initForMenu(Context context, MenuBuilder menuBuilder) {
            MenuItemImpl menuItemImpl;
            MenuBuilder menuBuilder2 = this.mMenu;
            if (menuBuilder2 != null && (menuItemImpl = this.mCurrentExpandedItem) != null) {
                menuBuilder2.collapseItemActionView(menuItemImpl);
            }
            this.mMenu = menuBuilder;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public void onRestoreInstanceState(Parcelable parcelable) {
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public Parcelable onSaveInstanceState() {
            return null;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
            return false;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter
        public void updateMenuView(boolean z) {
            if (this.mCurrentExpandedItem != null) {
                MenuBuilder menuBuilder = this.mMenu;
                boolean z2 = false;
                if (menuBuilder != null) {
                    int size = menuBuilder.size();
                    int i = 0;
                    while (true) {
                        if (i >= size) {
                            break;
                        } else if (this.mMenu.getItem(i) == this.mCurrentExpandedItem) {
                            z2 = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                }
                if (z2) {
                    return;
                }
                collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class HomeView extends FrameLayout {
        private Drawable mDefaultUpIndicator;
        private ImageView mIconView;
        private int mUpIndicatorRes;
        private ImageView mUpView;
        private int mUpWidth;

        public HomeView(Context context) {
            this(context, null);
        }

        public HomeView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override // android.view.View
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            CharSequence contentDescription = getContentDescription();
            if (TextUtils.isEmpty(contentDescription)) {
                return true;
            }
            accessibilityEvent.getText().add(contentDescription);
            return true;
        }

        public int getStartOffset() {
            return 0;
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            int i = this.mUpIndicatorRes;
            if (i != 0) {
                setUpIndicator(i);
            }
        }

        @Override // android.view.View
        protected void onFinishInflate() {
            super.onFinishInflate();
            this.mUpView = (ImageView) findViewById(R$id.up);
            this.mIconView = (ImageView) findViewById(R$id.home);
            ImageView imageView = this.mUpView;
            if (imageView != null) {
                this.mDefaultUpIndicator = imageView.getDrawable();
                Folme.useAt(this.mUpView).hover().setFeedbackRadius(60.0f);
                Folme.useAt(this.mUpView).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this.mUpView, new AnimConfig[0]);
            }
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int i6 = (i4 - i2) / 2;
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
            if (this.mUpView.getVisibility() != 8) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mUpView.getLayoutParams();
                int measuredHeight = this.mUpView.getMeasuredHeight();
                int measuredWidth = this.mUpView.getMeasuredWidth();
                int i7 = i6 - (measuredHeight / 2);
                ViewUtils.layoutChildView(this, this.mUpView, 0, i7, measuredWidth, i7 + measuredHeight);
                i5 = layoutParams.leftMargin + measuredWidth + layoutParams.rightMargin;
                if (isLayoutRtl) {
                    i3 -= i5;
                } else {
                    i += i5;
                }
            } else {
                i5 = 0;
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mIconView.getLayoutParams();
            int measuredHeight2 = this.mIconView.getMeasuredHeight();
            int measuredWidth2 = this.mIconView.getMeasuredWidth();
            int max = i5 + Math.max(layoutParams2.getMarginStart(), ((i3 - i) / 2) - (measuredWidth2 / 2));
            int max2 = Math.max(layoutParams2.topMargin, i6 - (measuredHeight2 / 2));
            ViewUtils.layoutChildView(this, this.mIconView, max, max2, max + measuredWidth2, max2 + measuredHeight2);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            measureChildWithMargins(this.mUpView, i, 0, i2, 0);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mUpView.getLayoutParams();
            this.mUpWidth = layoutParams.leftMargin + this.mUpView.getMeasuredWidth() + layoutParams.rightMargin;
            int i3 = this.mUpView.getVisibility() == 8 ? 0 : this.mUpWidth;
            int measuredHeight = layoutParams.bottomMargin + layoutParams.topMargin + this.mUpView.getMeasuredHeight();
            measureChildWithMargins(this.mIconView, i, i3, i2, 0);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mIconView.getLayoutParams();
            int measuredWidth = i3 + layoutParams2.leftMargin + this.mIconView.getMeasuredWidth() + layoutParams2.rightMargin;
            int max = Math.max(measuredHeight, layoutParams2.topMargin + this.mIconView.getMeasuredHeight() + layoutParams2.bottomMargin);
            int mode = View.MeasureSpec.getMode(i);
            int mode2 = View.MeasureSpec.getMode(i2);
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            if (mode == Integer.MIN_VALUE) {
                measuredWidth = Math.min(measuredWidth, size);
            } else if (mode == 1073741824) {
                measuredWidth = size;
            }
            if (mode2 == Integer.MIN_VALUE) {
                max = Math.min(max, size2);
            } else if (mode2 == 1073741824) {
                max = size2;
            }
            setMeasuredDimension(measuredWidth, max);
        }

        public void setIcon(Drawable drawable) {
            this.mIconView.setImageDrawable(drawable);
        }

        public void setUp(boolean z) {
            this.mUpView.setVisibility(z ? 0 : 8);
        }

        public void setUpIndicator(int i) {
            this.mUpIndicatorRes = i;
            this.mUpView.setImageDrawable(i != 0 ? getResources().getDrawable(i) : null);
        }

        public void setUpIndicator(Drawable drawable) {
            ImageView imageView = this.mUpView;
            if (drawable == null) {
                drawable = this.mDefaultUpIndicator;
            }
            imageView.setImageDrawable(drawable);
            this.mUpIndicatorRes = 0;
        }
    }

    /* loaded from: classes5.dex */
    private static class InnerTransitionListener extends TransitionListener {
        private int mNewState;
        private WeakReference<ActionBarView> mRef;

        public InnerTransitionListener(ActionBarView actionBarView, int i) {
            this.mRef = new WeakReference<>(actionBarView);
            this.mNewState = i;
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onBegin(Object obj) {
            super.onBegin(obj);
            ActionBarView actionBarView = this.mRef.get();
            if (actionBarView == null) {
                return;
            }
            actionBarView.mTempResizable = actionBarView.isResizable();
            actionBarView.setResizable(true);
            actionBarView.setExpandState(2);
            actionBarView.mCollapseController.setVisibility(4);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            super.onComplete(obj);
            ActionBarView actionBarView = this.mRef.get();
            if (actionBarView == null) {
                return;
            }
            actionBarView.setExpandState(this.mNewState);
            actionBarView.setResizable(actionBarView.mTempResizable);
            if (actionBarView.mInSearchMode) {
                actionBarView.mCollapseController.setVisibility(4);
            } else {
                actionBarView.mCollapseController.setVisibility(0);
            }
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
            ActionBarView actionBarView;
            super.onUpdate(obj, collection);
            UpdateInfo findByName = UpdateInfo.findByName(collection, "actionbar_state_change");
            if (findByName == null || (actionBarView = this.mRef.get()) == null) {
                return;
            }
            actionBarView.mPendingHeight = findByName.getIntValue();
            actionBarView.requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.ClassLoaderCreator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return Build.VERSION.SDK_INT >= 24 ? new SavedState(parcel, classLoader) : new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int expandState;
        int expandedMenuItemId;
        boolean isOverflowOpen;

        SavedState(Parcel parcel) {
            super(parcel);
            this.expandedMenuItemId = parcel.readInt();
            this.isOverflowOpen = parcel.readInt() != 0;
            this.expandState = parcel.readInt();
        }

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.expandedMenuItemId = parcel.readInt();
            this.isOverflowOpen = parcel.readInt() != 0;
            this.expandState = parcel.readInt();
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.expandedMenuItemId);
            parcel.writeInt(this.isOverflowOpen ? 1 : 0);
            parcel.writeInt(this.expandState);
        }
    }

    public ActionBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDisplayOptions = -1;
        this.mCollapseTitleShowable = true;
        this.mCollapseAnimShowConfigListener = new TransitionListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.1
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                super.onComplete(obj);
                if (ActionBarView.this.mCollapseController != null) {
                    ActionBarView.this.mCollapseController.onShow();
                }
            }
        };
        this.mCollapseAnimHideConfigListener = new TransitionListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.2
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                super.onComplete(obj);
                if (ActionBarView.this.mCollapseController != null) {
                    ActionBarView.this.mCollapseController.onHide();
                }
            }
        };
        this.mAlphaListener = new TransitionListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.3
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
                if (ActionBarView.this.mMovableTabContainer.getAlpha() == 0.0f) {
                    ActionBarView.this.mMovableTabContainer.setVisibility(8);
                } else if (ActionBarView.this.mMovableTabContainer.getVisibility() == 8) {
                    ActionBarView.this.mMovableTabContainer.setVisibility(0);
                }
            }
        };
        this.mNavItemSelectedListener = new AdapterView.OnItemSelectedListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.4
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (ActionBarView.this.mCallback != null) {
                    ActionBarView.this.mCallback.onNavigationItemSelected(i, j);
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
        this.mExpandedActionViewUpListener = new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MenuItemImpl menuItemImpl = ActionBarView.this.mExpandedMenuPresenter.mCurrentExpandedItem;
                if (menuItemImpl != null) {
                    menuItemImpl.collapseActionView();
                }
            }
        };
        this.mUpClickListener = new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ActionBarView actionBarView = ActionBarView.this;
                actionBarView.mWindowCallback.onMenuItemSelected(0, actionBarView.mLogoNavItem);
            }
        };
        this.mTitleClickListener = new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ActionBarView actionBarView = ActionBarView.this;
                actionBarView.mWindowCallback.onMenuItemSelected(0, actionBarView.mTitleNavItem);
            }
        };
        this.mCustomTitleWatcher = new TextWatcher() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.8
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                ActionBarView.this.mExpandTitle.setTitle(charSequence);
            }
        };
        this.mIsTitleHidding = false;
        this.mIsTitleShowing = false;
        this.mTransitionTarget = 0;
        this.mCollapseController = new AbsActionBarView.CollapseView();
        this.mMovableController = new AbsActionBarView.CollapseView();
        this.mTouchScrolling = false;
        this.mNonTouchScrolling = false;
        this.mInActionMode = false;
        this.mInSearchMode = false;
        this.mInActionModeAnimating = false;
        this.mStateChangeAnimStateStyle = null;
        this.mPostScroll = new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.10
            @Override // java.lang.Runnable
            public void run() {
                if (ActionBarView.this.mPostScroller.computeScrollOffset()) {
                    ActionBarView actionBarView = ActionBarView.this;
                    int currY = actionBarView.mPostScroller.getCurrY();
                    ActionBarView actionBarView2 = ActionBarView.this;
                    actionBarView.mPendingHeight = (currY - actionBarView2.mCollapseHeight) + actionBarView2.mCollapseTabHeight;
                    ActionBarView.this.requestLayout();
                    if (!ActionBarView.this.mPostScroller.isFinished()) {
                        ActionBarView.this.postOnAnimation(this);
                        return;
                    }
                    int currY2 = ActionBarView.this.mPostScroller.getCurrY();
                    ActionBarView actionBarView3 = ActionBarView.this;
                    if (currY2 == actionBarView3.mCollapseHeight) {
                        actionBarView3.setExpandState(0);
                        return;
                    }
                    int currY3 = actionBarView3.mPostScroller.getCurrY();
                    ActionBarView actionBarView4 = ActionBarView.this;
                    if (currY3 == actionBarView4.mCollapseHeight + actionBarView4.mMovableContainer.getMeasuredHeight()) {
                        ActionBarView.this.setExpandState(1);
                    }
                }
            }
        };
        this.mMovableAnimConfig.addListeners(this.mAlphaListener);
        this.mContext = context;
        this.mPostScroller = new Scroller(context);
        this.mInActionMode = false;
        this.mInSearchMode = false;
        this.mUncollapsePaddingH = context.getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_title_horizontal_padding);
        this.mUncollapseTabPaddingH = context.getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_title_tab_horizontal_padding);
        this.mExpandTabTopPadding = context.getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_title_top_padding);
        this.mExpandTitlePaddingBottom = context.getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_title_bottom_padding);
        this.mExpandSubtitlePaddingBottom = context.getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_subtitle_bottom_padding);
        this.mCollapseAnimShowConfig.addListeners(this.mCollapseAnimShowConfigListener);
        this.mCollapseAnimHideConfig.addListeners(this.mCollapseAnimHideConfigListener);
        FrameLayout frameLayout = new FrameLayout(context);
        this.mCollapseContainer = frameLayout;
        frameLayout.setId(R$id.action_bar_collapse_container);
        this.mCollapseContainer.setForegroundGravity(17);
        this.mCollapseContainer.setVisibility(0);
        this.mCollapseContainer.setAlpha(this.mInnerExpandState == 0 ? 1.0f : 0.0f);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.mMovableContainer = frameLayout2;
        frameLayout2.setId(R$id.action_bar_movable_container);
        FrameLayout frameLayout3 = this.mMovableContainer;
        int i = this.mUncollapsePaddingH;
        frameLayout3.setPaddingRelative(i, this.mExpandTabTopPadding, i, this.mExpandTitlePaddingBottom);
        this.mMovableContainer.setVisibility(0);
        this.mMovableContainer.setAlpha(this.mInnerExpandState == 0 ? 0.0f : 1.0f);
        SpringBackLayout springBackLayout = new SpringBackLayout(context);
        this.mCollapseTabContainer = springBackLayout;
        springBackLayout.setId(R$id.action_bar_collapse_tab_container);
        this.mCollapseTabContainer.setScrollOrientation(1);
        this.mCollapseTabContainer.setVisibility(0);
        SpringBackLayout springBackLayout2 = new SpringBackLayout(context);
        this.mMovableTabContainer = springBackLayout2;
        springBackLayout2.setId(R$id.action_bar_movable_tab_container);
        this.mMovableTabContainer.setScrollOrientation(1);
        this.mMovableTabContainer.setVisibility(0);
        this.mCollapseController.attachViews(this.mCollapseContainer);
        this.mMovableController.attachViews(this.mMovableContainer);
        this.mCollapseController.attachViews(this.mCollapseTabContainer);
        this.mMovableController.attachViews(this.mMovableTabContainer);
        setBackgroundResource(0);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ActionBar, 16843470, 0);
        this.mNavigationMode = obtainStyledAttributes.getInt(R$styleable.ActionBar_android_navigationMode, 0);
        this.mTitle = obtainStyledAttributes.getText(R$styleable.ActionBar_android_title);
        this.mSubtitle = obtainStyledAttributes.getText(R$styleable.ActionBar_android_subtitle);
        this.mTitleCenter = obtainStyledAttributes.getBoolean(R$styleable.ActionBar_titleCenter, false);
        this.mLogo = obtainStyledAttributes.getDrawable(R$styleable.ActionBar_android_logo);
        this.mIcon = obtainStyledAttributes.getDrawable(R$styleable.ActionBar_android_icon);
        LayoutInflater from = LayoutInflater.from(context);
        this.mHomeResId = obtainStyledAttributes.getResourceId(R$styleable.ActionBar_android_homeLayout, R$layout.miuix_appcompat_action_bar_home);
        this.mCollapseTitleStyleRes = obtainStyledAttributes.getResourceId(R$styleable.ActionBar_android_titleTextStyle, 0);
        this.mCollapseSubtitleStyleRes = obtainStyledAttributes.getResourceId(R$styleable.ActionBar_android_subtitleTextStyle, 0);
        this.mProgressBarPadding = obtainStyledAttributes.getDimensionPixelOffset(R$styleable.ActionBar_android_progressBarPadding, 0);
        this.mItemPadding = obtainStyledAttributes.getDimensionPixelOffset(R$styleable.ActionBar_android_itemPadding, 0);
        setDisplayOptions(obtainStyledAttributes.getInt(R$styleable.ActionBar_android_displayOptions, 0));
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.ActionBar_android_customNavigationLayout, 0);
        if (resourceId != 0) {
            this.mCustomNavView = from.inflate(resourceId, (ViewGroup) this, false);
            this.mNavigationMode = 0;
        }
        this.mTitleMinHeight = obtainStyledAttributes.getLayoutDimension(R$styleable.ActionBar_android_height, 0);
        this.mTitleMaxHeight = obtainStyledAttributes.getLayoutDimension(R$styleable.ActionBar_android_maxHeight, 0);
        obtainStyledAttributes.recycle();
        this.mLogoNavItem = new ActionMenuItem(context, 0, 16908332, 0, 0, this.mTitle);
        this.mTitleNavItem = new ActionMenuItem(context, 0, 16908310, 0, 0, this.mTitle);
        post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ActionBarView.this.lambda$new$0();
            }
        });
    }

    private void addTabsContainer(ScrollingTabContainerView scrollingTabContainerView, ScrollingTabContainerView scrollingTabContainerView2, ScrollingTabContainerView scrollingTabContainerView3, ScrollingTabContainerView scrollingTabContainerView4) {
        this.mCollapseTabs = scrollingTabContainerView;
        this.mExpandTabs = scrollingTabContainerView2;
        this.mSecondaryCollapseTabs = scrollingTabContainerView3;
        this.mSecondaryExpandTabs = scrollingTabContainerView4;
        if (this.mCollapseContainer.getChildCount() == 0 || ((this.mDisplayOptions & 8) != 0 && TextUtils.isEmpty(this.mTitle))) {
            this.mCollapseContainer.removeAllViews();
            ScrollingTabContainerView scrollingTabContainerView5 = this.mCollapseTabs;
            if (scrollingTabContainerView5 != null) {
                scrollingTabContainerView5.setVisibility(0);
                this.mCollapseContainer.addView(this.mCollapseTabs);
            }
            this.mMovableContainer.removeAllViews();
            ScrollingTabContainerView scrollingTabContainerView6 = this.mExpandTabs;
            if (scrollingTabContainerView6 != null) {
                scrollingTabContainerView6.setVisibility(0);
                this.mMovableContainer.addView(this.mExpandTabs);
            }
            this.mCollapseTabContainer.removeAllViews();
            this.mMovableTabContainer.removeAllViews();
        } else if (this.mCollapseContainer.getChildCount() == 1) {
            ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(this.mContext);
            View childAt = this.mCollapseContainer.getChildAt(0);
            if (actionBarPolicy.isTightTitle() || (childAt instanceof ScrollingTabContainerView)) {
                this.mCollapseContainer.removeAllViews();
                ScrollingTabContainerView scrollingTabContainerView7 = this.mCollapseTabs;
                if (scrollingTabContainerView7 != null) {
                    this.mCollapseContainer.addView(scrollingTabContainerView7);
                }
                this.mMovableContainer.removeAllViews();
                ScrollingTabContainerView scrollingTabContainerView8 = this.mExpandTabs;
                if (scrollingTabContainerView8 != null) {
                    this.mMovableContainer.addView(scrollingTabContainerView8);
                }
            } else {
                this.mCollapseTabContainer.removeAllViews();
                ScrollingTabContainerView scrollingTabContainerView9 = this.mSecondaryCollapseTabs;
                if (scrollingTabContainerView9 != null) {
                    this.mCollapseTabContainer.addView(scrollingTabContainerView9);
                    this.mCollapseTabContainer.setTarget(this.mSecondaryCollapseTabs);
                }
                this.mMovableTabContainer.removeAllViews();
                ScrollingTabContainerView scrollingTabContainerView10 = this.mSecondaryExpandTabs;
                if (scrollingTabContainerView10 != null) {
                    this.mMovableTabContainer.addView(scrollingTabContainerView10);
                    this.mMovableTabContainer.setTarget(this.mSecondaryExpandTabs);
                }
                if (this.mCollapseTabContainer.getParent() == null) {
                    addView(this.mCollapseTabContainer, new FrameLayout.LayoutParams(-1, -2));
                }
                if (this.mMovableTabContainer.getParent() == null) {
                    addView(this.mMovableTabContainer, new FrameLayout.LayoutParams(-1, -2));
                }
            }
        }
        safeAddView(this, this.mCollapseContainer);
        safeAddView(this, this.mMovableContainer);
        ViewGroup.LayoutParams layoutParams = this.mCollapseTabs.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = -2;
            layoutParams.height = -1;
        }
        ViewGroup.LayoutParams layoutParams2 = this.mExpandTabs.getLayoutParams();
        if (layoutParams2 != null) {
            layoutParams2.width = -2;
            layoutParams2.height = -2;
        }
        ViewGroup.LayoutParams layoutParams3 = this.mSecondaryCollapseTabs.getLayoutParams();
        if (layoutParams3 != null) {
            layoutParams3.width = -2;
            layoutParams3.height = -1;
        }
        ViewGroup.LayoutParams layoutParams4 = this.mSecondaryExpandTabs.getLayoutParams();
        if (layoutParams4 != null) {
            layoutParams4.width = -2;
            layoutParams4.height = -1;
        }
        updateTightTitle();
    }

    private void addTitleView(View view, View view2) {
        if (this.mNavigationMode == 2 && this.mCollapseContainer.getChildCount() == 1 && (this.mCollapseContainer.getChildAt(0) instanceof ScrollingTabContainerView)) {
            ScrollingTabContainerView scrollingTabContainerView = this.mSecondaryCollapseTabs;
            if (scrollingTabContainerView != null) {
                safeAddView(this.mCollapseTabContainer, scrollingTabContainerView);
                this.mCollapseTabContainer.setTarget(this.mSecondaryCollapseTabs);
            }
            ScrollingTabContainerView scrollingTabContainerView2 = this.mSecondaryExpandTabs;
            if (scrollingTabContainerView2 != null) {
                safeAddView(this.mMovableTabContainer, scrollingTabContainerView2);
                this.mMovableTabContainer.setTarget(this.mSecondaryExpandTabs);
            }
            this.mCollapseContainer.removeAllViews();
            this.mMovableContainer.removeAllViews();
        }
        safeAddView(this.mCollapseContainer, view);
        safeAddView(this.mMovableContainer, view2);
    }

    private void addedCustomView() {
        FrameLayout frameLayout = (FrameLayout) this.mCustomNavView.findViewById(R$id.action_bar_expand_container);
        TextView customTitleView = getCustomTitleView(frameLayout);
        if (customTitleView != null) {
            freeCollapseContainer();
            this.mCollapseCustomContainer = frameLayout;
            this.mCollapseController.attachViews(frameLayout);
            this.mExpandTitle.setTitle(customTitleView.getText());
            this.mExpandTitle.setTitleVisibility(0);
            this.mExpandTitle.setVisibility(0);
            this.mExpandTitle.setSubTitleVisibility(8);
            this.mMovableContainer.addView(this.mExpandTitle.getLayout());
            customTitleView.addTextChangedListener(this.mCustomTitleWatcher);
        }
    }

    private void animateLayoutWithProcess(float f) {
        float min = 1.0f - Math.min(1.0f, f * 3.0f);
        int i = this.mInnerExpandState;
        if (i == 2 && this.mInActionModeAnimating && getExpandState() == 0 && isResizable()) {
            i = 1;
        }
        if (i != 2) {
            if (i == 1) {
                this.mCollapseController.animTo(0.0f, 0, 20, this.mCollapseAnimHideConfig);
                this.mMovableController.animTo(this.mInActionMode ? 0.0f : 1.0f, 0, 0, this.mMovableAnimConfig);
                this.mTransitionTarget = 20;
                return;
            } else if (i == 0) {
                this.mCollapseController.animTo(this.mInActionMode ? 0.0f : 1.0f, 0, 0, this.mCollapseAnimShowConfig);
                this.mMovableController.animTo(0.0f, 0, 0, this.mMovableAnimConfig);
                this.mTransitionTarget = 0;
                return;
            } else {
                return;
            }
        }
        if (min > 0.0f) {
            if (!this.mIsTitleShowing) {
                this.mIsTitleShowing = true;
                this.mIsTitleHidding = false;
                this.mCollapseController.animTo(0.0f, 0, 20, this.mCollapseAnimHideConfig);
                if (this.mTransitionListener != null) {
                    Folme.useValue("target", 0).setFlags(1L).setup(1).setTo("expand", Integer.valueOf(this.mTransitionTarget)).to("expand", 20, this.mHideProcessConfig);
                }
            }
        } else if (!this.mIsTitleHidding) {
            this.mIsTitleHidding = true;
            this.mIsTitleShowing = false;
            this.mCollapseController.animTo(this.mInActionMode ? 0.0f : 1.0f, 0, 0, this.mCollapseAnimShowConfig);
            if (this.mTransitionListener != null) {
                Folme.useValue("target", 0).setFlags(1L).setup(0).setTo("collapse", Integer.valueOf(this.mTransitionTarget)).to("collapse", 0, this.mShowProcessConfig);
            }
        }
        AbsActionBarView.CollapseView collapseView = this.mMovableController;
        if (this.mInActionMode) {
            min = 0.0f;
        }
        collapseView.animTo(min, 0, 0, this.mMovableAnimConfig);
    }

    private boolean canExpandTitleBeShown() {
        if (this.mCollapseTitle == null || TextUtils.isEmpty(this.mTitle)) {
            return false;
        }
        return (!isResizable() && getExpandState() == 0) || this.mCollapseTitle.canTitleBeShown(this.mTitle.toString());
    }

    private void clipViewBounds(View view, int i, int i2, int i3, int i4) {
        Rect rect = new Rect();
        rect.set(i, i2, i3, i4);
        view.setClipBounds(rect);
    }

    private int computeTitleCenterLayoutStart(View view) {
        int width = (getWidth() - view.getMeasuredWidth()) / 2;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        return width - (layoutParams instanceof LinearLayout.LayoutParams ? 0 + ((LinearLayout.LayoutParams) layoutParams).getMarginStart() : 0);
    }

    private void configPresenters(MenuBuilder menuBuilder) {
        if (menuBuilder != null) {
            menuBuilder.addMenuPresenter(this.mActionMenuPresenter);
            menuBuilder.addMenuPresenter(this.mExpandedMenuPresenter);
        } else {
            this.mActionMenuPresenter.initForMenu(this.mContext, null);
            this.mExpandedMenuPresenter.initForMenu(this.mContext, null);
        }
        this.mActionMenuPresenter.updateMenuView(true);
        this.mExpandedMenuPresenter.updateMenuView(true);
    }

    private boolean freeCollapseContainer() {
        if (this.mCollapseContainer.getChildCount() == 1 && (this.mCollapseContainer.getChildAt(0) instanceof ScrollingTabContainerView)) {
            this.mCollapseContainer.removeAllViews();
            this.mCollapseTabContainer.removeAllViews();
            ScrollingTabContainerView scrollingTabContainerView = this.mSecondaryCollapseTabs;
            if (scrollingTabContainerView != null) {
                this.mCollapseTabContainer.addView(scrollingTabContainerView);
                this.mCollapseTabContainer.setTarget(this.mSecondaryCollapseTabs);
            }
            this.mMovableTabContainer.removeAllViews();
            ScrollingTabContainerView scrollingTabContainerView2 = this.mSecondaryExpandTabs;
            if (scrollingTabContainerView2 != null) {
                this.mMovableTabContainer.addView(scrollingTabContainerView2);
                this.mMovableTabContainer.setTarget(this.mSecondaryExpandTabs);
            }
        }
        this.mMovableContainer.removeAllViews();
        return true;
    }

    private ProgressBar getCircularProgressBar() {
        ProgressBar progressBar = this.mIndeterminateProgressView;
        if (progressBar != null) {
            progressBar.setVisibility(4);
        }
        return progressBar;
    }

    private TextView getCustomTitleView(View view) {
        if (view != null) {
            return (TextView) view.findViewById(16908310);
        }
        return null;
    }

    private ProgressBar getHorizontalProgressBar() {
        ProgressBar progressBar = this.mProgressView;
        if (progressBar != null) {
            progressBar.setVisibility(4);
        }
        return progressBar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Drawable getIcon() {
        if ((this.mIconLogoInitIndicator & 1) != 1) {
            Context context = this.mContext;
            if (context instanceof Activity) {
                try {
                    this.mIcon = context.getPackageManager().getActivityIcon(((Activity) this.mContext).getComponentName());
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("ActionBarView", "Activity component name not found!", e);
                }
            }
            if (this.mIcon == null) {
                this.mIcon = this.mContext.getApplicationInfo().loadIcon(this.mContext.getPackageManager());
            }
            this.mIconLogoInitIndicator |= 1;
        }
        return this.mIcon;
    }

    private Drawable getLogo() {
        if ((this.mIconLogoInitIndicator & 2) != 2) {
            if (Build.VERSION.SDK_INT >= 9) {
                Context context = this.mContext;
                if (context instanceof Activity) {
                    try {
                        this.mLogo = context.getPackageManager().getActivityLogo(((Activity) this.mContext).getComponentName());
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e("ActionBarView", "Activity component name not found!", e);
                    }
                }
                if (this.mLogo == null) {
                    this.mLogo = this.mContext.getApplicationInfo().loadLogo(this.mContext.getPackageManager());
                }
            }
            this.mIconLogoInitIndicator |= 2;
        }
        return this.mLogo;
    }

    private boolean hasTitle() {
        return !((this.mDisplayOptions & 8) == 0 || TextUtils.isEmpty(this.mTitle)) || getNavigationMode() == 2;
    }

    private void hideProgressBars(ProgressBar progressBar, ProgressBar progressBar2) {
        if (progressBar2 != null && progressBar2.getVisibility() == 0) {
            progressBar2.setVisibility(4);
        }
        if (progressBar == null || progressBar.getVisibility() != 0) {
            return;
        }
        progressBar.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initExpandedHomeLayout() {
        if (this.mExpandedHomeLayout == null) {
            HomeView homeView = (HomeView) LayoutInflater.from(this.mContext).inflate(this.mHomeResId, (ViewGroup) this, false);
            this.mExpandedHomeLayout = homeView;
            homeView.setUp(true);
            this.mExpandedHomeLayout.setOnClickListener(this.mExpandedActionViewUpListener);
        }
    }

    private void initHomeLayout() {
        if (this.mHomeLayout == null) {
            HomeView homeView = (HomeView) LayoutInflater.from(this.mContext).inflate(this.mHomeResId, (ViewGroup) this, false);
            this.mHomeLayout = homeView;
            homeView.setOnClickListener(this.mUpClickListener);
            this.mHomeLayout.setClickable(true);
            this.mHomeLayout.setFocusable(true);
            int i = this.mHomeAsUpIndicatorResId;
            if (i != 0) {
                this.mHomeLayout.setUpIndicator(i);
                this.mHomeAsUpIndicatorResId = 0;
            }
            Drawable drawable = this.mHomeAsUpIndicatorDrawable;
            if (drawable != null) {
                this.mHomeLayout.setUpIndicator(drawable);
                this.mHomeAsUpIndicatorDrawable = null;
            }
            addView(this.mHomeLayout);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initTitle() {
        if (this.mTitleUpView == null) {
            View generateTitleUpView = ActionBarViewFactory.generateTitleUpView(getContext(), null);
            this.mTitleUpView = generateTitleUpView;
            generateTitleUpView.setOnClickListener(this.mUpClickListener);
            Folme.useAt(this.mTitleUpView).hover().setFeedbackRadius(60.0f);
            Folme.useAt(this.mTitleUpView).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this.mTitleUpView, new AnimConfig[0]);
        }
        addView(this.mTitleUpView);
        if (this.mCollapseTitle == null) {
            this.mCollapseTitle = ActionBarViewFactory.generateCollapseTitle(getContext(), this.mCollapseTitleStyleRes, this.mCollapseSubtitleStyleRes);
            this.mExpandTitle = ActionBarViewFactory.generateExpandTitle(getContext());
            int i = this.mDisplayOptions;
            boolean z = (i & 4) != 0;
            boolean z2 = (i & 2) != 0;
            this.mTitleUpView.setVisibility(!z2 ? z ? 0 : 4 : 8);
            this.mTitleUpView.setEnabled(z && !z2);
            this.mCollapseTitle.setEnabled(z && !z2);
            this.mExpandTitle.setEnabled(z && !z2);
            this.mCollapseTitle.setTitle(this.mTitle);
            this.mCollapseTitle.setSubTitle(this.mSubtitle);
            this.mExpandTitle.setTitle(this.mTitle);
            this.mExpandTitle.setSubTitle(this.mSubtitle);
            post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ActionBarView.this.lambda$initTitle$3();
                }
            });
            if (this.mSubtitle != null) {
                this.mCollapseTitle.setSubTitleVisibility(0);
                this.mExpandTitle.setSubTitleVisibility(0);
            }
            updateTightTitle();
        }
        addTitleView(this.mCollapseTitle.getLayout(), this.mExpandTitle.getLayout());
        post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarView$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ActionBarView.this.lambda$initTitle$4();
            }
        });
        if (this.mExpandedActionView != null || (TextUtils.isEmpty(this.mTitle) && TextUtils.isEmpty(this.mSubtitle))) {
            setTitleVisibility(false);
        }
        safeAddView(this, this.mCollapseContainer);
        safeAddView(this, this.mMovableContainer);
    }

    private boolean isShowTitle() {
        return this.mCollapseContainer.getChildCount() > 0 || !(this.mCustomNavView == null || this.mCollapseCustomContainer == null);
    }

    private boolean isSimpleCustomNavView() {
        View view = this.mCustomNavView;
        if (view == null || view.getVisibility() != 0) {
            return true;
        }
        ViewGroup.LayoutParams layoutParams = this.mCustomNavView.getLayoutParams();
        ActionBar.LayoutParams layoutParams2 = layoutParams instanceof ActionBar.LayoutParams ? (ActionBar.LayoutParams) layoutParams : null;
        return layoutParams2 != null && normalizeHorizontalGravity(layoutParams2.gravity, ViewUtils.isLayoutRtl(this)) == 8388613;
    }

    private boolean isTitleCenter() {
        HomeView homeView;
        return this.mTitleCenter && isSimpleCustomNavView() && ((homeView = this.mHomeLayout) == null || homeView.getVisibility() == 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initTitle$3() {
        CollapseTitle collapseTitle = this.mCollapseTitle;
        if (collapseTitle != null) {
            collapseTitle.setOnClickListener(this.mTitleClickListener);
            if (this.mSubtitle != null) {
                CollapseTitle collapseTitle2 = this.mCollapseTitle;
                collapseTitle2.setSubTitleTextSize(collapseTitle2.getSubtitleAdjustSize());
            }
        }
        ExpandTitle expandTitle = this.mExpandTitle;
        if (expandTitle != null) {
            expandTitle.setOnClickListener(this.mTitleClickListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initTitle$4() {
        CollapseTitle collapseTitle = this.mCollapseTitle;
        if (collapseTitle != null) {
            Rect hitRect = collapseTitle.getHitRect();
            hitRect.left -= AttributeResolver.resolveDimensionPixelSize(getContext(), R$attr.actionBarPaddingStart);
            setTouchDelegate(new TouchDelegate(hitRect, this.mCollapseTitle.getLayout()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        int i = this.mInnerExpandState;
        if (i == 0) {
            this.mCollapseController.setAnimFrom(1.0f, 0, 0);
            this.mMovableController.setAnimFrom(0.0f, 0, 0);
        } else if (i == 1) {
            this.mCollapseController.setAnimFrom(0.0f, 0, 20);
            this.mMovableController.setAnimFrom(1.0f, 0, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSubtitle$2() {
        CollapseTitle collapseTitle = this.mCollapseTitle;
        if (collapseTitle != null) {
            collapseTitle.setSubTitleTextSize(collapseTitle.getSubtitleAdjustSize());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setTitleImpl$1() {
        CollapseTitle collapseTitle = this.mCollapseTitle;
        if (collapseTitle != null) {
            collapseTitle.setSubTitleTextSize(collapseTitle.getSubtitleAdjustSize());
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x001b, code lost:
    
        if (r4 != false) goto L8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:?, code lost:
    
        return 8388613;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:?, code lost:
    
        return 8388611;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0012, code lost:
    
        if (r4 != false) goto L7;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int normalizeHorizontalGravity(int r3, boolean r4) {
        /*
            r2 = this;
            r2 = 8388615(0x800007, float:1.1754953E-38)
            r2 = r2 & r3
            r3 = 8388608(0x800000, float:1.1754944E-38)
            r3 = r3 & r2
            if (r3 != 0) goto L1e
            r3 = 3
            r0 = 8388613(0x800005, float:1.175495E-38)
            r1 = 8388611(0x800003, float:1.1754948E-38)
            if (r2 != r3) goto L18
            if (r4 == 0) goto L16
        L14:
            r2 = r0
            goto L1e
        L16:
            r2 = r1
            goto L1e
        L18:
            r3 = 5
            if (r2 != r3) goto L1e
            if (r4 == 0) goto L14
            goto L16
        L1e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.appcompat.internal.app.widget.ActionBarView.normalizeHorizontalGravity(int, boolean):int");
    }

    /* JADX WARN: Code restructure failed: missing block: B:110:0x01de, code lost:
    
        if (r3 == (-1)) goto L111;
     */
    /* JADX WARN: Removed duplicated region for block: B:100:0x01b0  */
    /* JADX WARN: Removed duplicated region for block: B:101:0x01bf  */
    /* JADX WARN: Removed duplicated region for block: B:104:0x01ce  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x01de  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x01eb  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x01f4  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x01ff  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x0205  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x0225  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x0240  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x0247  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x024a  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x0250  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x025d  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x013f  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x019f  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x01a4  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01a7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void onLayoutCollapseViews(boolean r18, int r19, int r20, int r21, int r22, int r23) {
        /*
            Method dump skipped, instructions count: 714
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.appcompat.internal.app.widget.ActionBarView.onLayoutCollapseViews(boolean, int, int, int, int, int):void");
    }

    private void safeAddView(ViewGroup viewGroup, View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        if (viewGroup != null) {
            viewGroup.addView(view);
        }
    }

    private void setTitleImpl(CharSequence charSequence) {
        this.mTitle = charSequence;
        CollapseTitle collapseTitle = this.mCollapseTitle;
        if (collapseTitle != null) {
            collapseTitle.setTitleVisibility(0);
            this.mCollapseTitle.setTitle(charSequence);
            this.mExpandTitle.setTitle(charSequence);
            setTitleVisibility((this.mExpandedActionView != null || (this.mDisplayOptions & 8) == 0 || (TextUtils.isEmpty(this.mTitle) && TextUtils.isEmpty(this.mSubtitle))) ? false : true);
            if (!TextUtils.isEmpty(this.mSubtitle)) {
                this.mCollapseTitle.setSubTitleVisibility(0);
                post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarView$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActionBarView.this.lambda$setTitleImpl$1();
                    }
                });
            }
        }
        ActionMenuItem actionMenuItem = this.mLogoNavItem;
        if (actionMenuItem != null) {
            actionMenuItem.setTitle(charSequence);
        }
        ActionMenuItem actionMenuItem2 = this.mTitleNavItem;
        if (actionMenuItem2 != null) {
            actionMenuItem2.setTitle(charSequence);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTitleVisibility(boolean z) {
        CollapseTitle collapseTitle = this.mCollapseTitle;
        if (collapseTitle != null) {
            collapseTitle.setVisibility(z ? 0 : 8);
        }
        ExpandTitle expandTitle = this.mExpandTitle;
        if (expandTitle != null) {
            expandTitle.setVisibility(z ? 0 : 8);
        }
        if (this.mTitleUpView != null && (getDisplayOptions() & 32) == 0) {
            if (z) {
                int i = this.mDisplayOptions;
                boolean z2 = (i & 4) != 0;
                this.mTitleUpView.setVisibility((i & 2) != 0 ? 8 : z2 ? 0 : 4);
            } else {
                this.mTitleUpView.setVisibility(8);
            }
        }
        int i2 = TextUtils.isEmpty(this.mSubtitle) ? this.mExpandTitlePaddingBottom : this.mExpandSubtitlePaddingBottom;
        FrameLayout frameLayout = this.mMovableContainer;
        frameLayout.setPaddingRelative(frameLayout.getPaddingStart(), this.mMovableContainer.getPaddingTop(), this.mMovableContainer.getPaddingEnd(), i2);
    }

    private void showProgressBars(ProgressBar progressBar, ProgressBar progressBar2) {
        if (progressBar2 != null && progressBar2.getVisibility() == 4) {
            progressBar2.setVisibility(0);
        }
        if (progressBar == null || progressBar.getProgress() >= 10000) {
            return;
        }
        progressBar.setVisibility(0);
    }

    private void updateProgressBars(int i) {
        ProgressBar circularProgressBar = getCircularProgressBar();
        ProgressBar horizontalProgressBar = getHorizontalProgressBar();
        if (i == -1) {
            if (horizontalProgressBar != null) {
                horizontalProgressBar.setVisibility((horizontalProgressBar.isIndeterminate() || horizontalProgressBar.getProgress() < 10000) ? 0 : 4);
            }
            if (circularProgressBar != null) {
                circularProgressBar.setVisibility(0);
            }
        } else if (i == -2) {
            if (horizontalProgressBar != null) {
                horizontalProgressBar.setVisibility(8);
            }
            if (circularProgressBar != null) {
                circularProgressBar.setVisibility(8);
            }
        } else if (i == -3) {
            horizontalProgressBar.setIndeterminate(true);
        } else if (i == -4) {
            horizontalProgressBar.setIndeterminate(false);
        } else if (i < 0 || i > 10000) {
        } else {
            horizontalProgressBar.setProgress(i + 0);
            if (i < 10000) {
                showProgressBars(horizontalProgressBar, circularProgressBar);
            } else {
                hideProgressBars(horizontalProgressBar, circularProgressBar);
            }
        }
    }

    private void updateTightTitle() {
        boolean z = isTightTitleWithEmbeddedTabs() && TextUtils.isEmpty(this.mTitle);
        int i = (z || !this.mCollapseTitleShowable) ? 8 : 0;
        CollapseTitle collapseTitle = this.mCollapseTitle;
        if (collapseTitle != null) {
            collapseTitle.setTitleVisibility(i);
        }
        int i2 = (z || !this.mCollapseTitleShowable || TextUtils.isEmpty(this.mSubtitle)) ? 8 : 0;
        CollapseTitle collapseTitle2 = this.mCollapseTitle;
        if (collapseTitle2 != null) {
            collapseTitle2.setSubTitleVisibility(i2);
        }
    }

    private void updateTitleCenter() {
        CollapseTitle collapseTitle = this.mCollapseTitle;
        if (collapseTitle != null) {
            collapseTitle.updateTitleCenter(isTitleCenter());
        }
    }

    public void collapseActionView() {
        ExpandedActionViewMenuPresenter expandedActionViewMenuPresenter = this.mExpandedMenuPresenter;
        MenuItemImpl menuItemImpl = expandedActionViewMenuPresenter == null ? null : expandedActionViewMenuPresenter.mCurrentExpandedItem;
        if (menuItemImpl != null) {
            menuItemImpl.collapseActionView();
        }
    }

    protected ActionMenuPresenter createActionMenuPresenter(MenuPresenter.Callback callback) {
        ViewParent parent = getParent();
        while (true) {
            View view = (View) parent;
            if (view instanceof ActionBarOverlayLayout) {
                ActionMenuPresenter actionMenuPresenter = new ActionMenuPresenter(this.mContext, (ActionBarOverlayLayout) view, R$layout.miuix_appcompat_action_menu_layout, R$layout.miuix_appcompat_action_menu_item_layout, R$layout.miuix_appcompat_action_bar_expanded_menu_layout, R$layout.miuix_appcompat_action_bar_list_menu_item_layout);
                actionMenuPresenter.setCallback(callback);
                actionMenuPresenter.setId(R$id.action_menu_presenter);
                return actionMenuPresenter;
            } else if (!(view.getParent() instanceof View)) {
                throw new IllegalStateException("ActionBarOverlayLayout not found");
            } else {
                parent = view.getParent();
            }
        }
    }

    protected ExpandedActionViewMenuPresenter createExpandedActionViewMenuPresenter() {
        return new ExpandedActionViewMenuPresenter();
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ActionBar.LayoutParams(8388627);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new ActionBar.LayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams == null ? generateDefaultLayoutParams() : layoutParams;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ ActionBarTransitionListener getActionBarTransitionListener() {
        return super.getActionBarTransitionListener();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ ActionMenuView getActionMenuView() {
        return super.getActionMenuView();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ int getAnimatedVisibility() {
        return super.getAnimatedVisibility();
    }

    public View getCustomNavigationView() {
        return this.mCustomNavView;
    }

    public int getDisplayOptions() {
        return this.mDisplayOptions;
    }

    public SpinnerAdapter getDropdownAdapter() {
        return this.mSpinnerAdapter;
    }

    public int getDropdownSelectedPosition() {
        return this.mSpinner.getSelectedItemPosition();
    }

    public View getEndView() {
        return this.mEndView;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ int getExpandState() {
        return super.getExpandState();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ ActionMenuView getMenuView() {
        return super.getMenuView();
    }

    public int getNavigationMode() {
        return this.mNavigationMode;
    }

    public View getStartView() {
        return this.mStartView;
    }

    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public boolean hasExpandedActionView() {
        ExpandedActionViewMenuPresenter expandedActionViewMenuPresenter = this.mExpandedMenuPresenter;
        return (expandedActionViewMenuPresenter == null || expandedActionViewMenuPresenter.mCurrentExpandedItem == null) ? false : true;
    }

    public boolean hideImmersionMore() {
        View view = this.mImmersionView;
        if (view != null) {
            view.setVisibility(8);
            return true;
        }
        return false;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ boolean hideOverflowMenu() {
        return super.hideOverflowMenu();
    }

    public void initImmersionMore(int i, final ActionBarDelegateImpl actionBarDelegateImpl) {
        if (i <= 0) {
            Log.w("ActionBarView", "Try to initialize invalid layout for immersion more button: " + i);
            return;
        }
        int i2 = this.mDisplayOptions;
        if ((i2 & 16) != 0) {
            Log.d("ActionBarView", "Don't show immersion menu button for custom action bar");
        } else if (i2 == 0) {
            Log.d("ActionBarView", "Don't show immersion menu button for null display option");
        } else {
            View inflate = LayoutInflater.from(getContext()).inflate(i, (ViewGroup) this, false);
            this.mImmersionView = inflate;
            addView(inflate);
            final View findViewById = this.mImmersionView.findViewById(R$id.more);
            if (findViewById != null) {
                findViewById.setOnClickListener(new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarView.9
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        actionBarDelegateImpl.showImmersionMenu(findViewById, ActionBarView.this);
                    }
                });
                Folme.useAt(findViewById).hover().setFeedbackRadius(60.0f);
                Folme.useAt(findViewById).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(findViewById, new AnimConfig[0]);
            }
        }
    }

    public void initIndeterminateProgress() {
        ProgressBar progressBar = new ProgressBar(this.mContext, null, R$attr.actionBarIndeterminateProgressStyle);
        this.mIndeterminateProgressView = progressBar;
        progressBar.setId(R$id.progress_circular);
        this.mIndeterminateProgressView.setVisibility(8);
        this.mIndeterminateProgressView.setIndeterminate(true);
        addView(this.mIndeterminateProgressView);
    }

    public boolean isCollapsed() {
        return this.mIsCollapsed;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ boolean isOverflowMenuShowing() {
        return super.isOverflowMenuShowing();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ boolean isOverflowReserved() {
        return super.isOverflowReserved();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ boolean isResizable() {
        return super.isResizable();
    }

    public boolean isSplitActionBar() {
        return this.mSplitActionBar;
    }

    public boolean isTightTitleWithEmbeddedTabs() {
        return this.mIncludeTabs && ActionBarPolicy.get(this.mContext).isTightTitle();
    }

    public void onActionModeEnd(boolean z) {
        this.mInActionMode = false;
        this.mInSearchMode = false;
        if (getExpandState() == 0) {
            this.mCollapseController.setAlpha(1.0f);
            this.mMovableController.setAlpha(0.0f);
        } else if (getExpandState() == 1) {
            this.mCollapseController.setAlpha(0.0f);
            this.mMovableController.setAlpha(1.0f);
        }
        View view = this.mStartView;
        if (view != null) {
            view.setAlpha(1.0f);
        }
        View view2 = this.mEndView;
        if (view2 != null) {
            view2.setAlpha(1.0f);
        }
        View view3 = this.mTitleUpView;
        if (view3 != null) {
            view3.setAlpha(1.0f);
        }
        if (z) {
            this.mMovableController.setAcceptAlphaChange(true);
            this.mCollapseController.setAcceptAlphaChange(true);
        }
    }

    public void onActionModeStart(boolean z, boolean z2) {
        this.mInActionMode = true;
        this.mInSearchMode = z;
        this.mCollapseController.setAlpha(0.0f);
        this.mMovableController.setAlpha(0.0f);
        View view = this.mStartView;
        if (view != null) {
            view.setAlpha(0.0f);
        }
        View view2 = this.mEndView;
        if (view2 != null) {
            view2.setAlpha(0.0f);
        }
        View view3 = this.mTitleUpView;
        if (view3 != null) {
            view3.setAlpha(0.0f);
        }
        if (z2) {
            this.mMovableController.setAcceptAlphaChange(false);
            this.mCollapseController.setAcceptAlphaChange(false);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    protected void onAnimatedExpandStateChanged(int i, int i2) {
        IStateStyle iStateStyle = this.mStateChangeAnimStateStyle;
        if (iStateStyle != null) {
            iStateStyle.cancel();
        }
        if (i == 1) {
            this.mPendingHeight = this.mMovableContainer.getMeasuredHeight() + this.mMovableTabHeight;
        } else if (i == 0) {
            this.mPendingHeight = 0;
        }
        AnimConfig addListeners = new AnimConfig().addListeners(new InnerTransitionListener(this, i2));
        int measuredHeight = i2 == 1 ? this.mMovableContainer.getMeasuredHeight() : 0;
        if (i2 == 1) {
            this.mCollapseContainer.setVisibility(4);
            this.mCollapseTabContainer.setVisibility(4);
        } else if (i2 == 0) {
            this.mCollapseContainer.setVisibility(0);
            this.mCollapseTabContainer.setVisibility(0);
        }
        this.mStateChangeAnimStateStyle = Folme.useValue(new Object[0]).setFlags(1L).setTo("actionbar_state_change", Integer.valueOf(this.mPendingHeight)).to("actionbar_state_change", Integer.valueOf(measuredHeight), addListeners);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView, android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        ViewGroup.LayoutParams layoutParams;
        ViewGroup.LayoutParams layoutParams2;
        ViewGroup.LayoutParams layoutParams3;
        ViewGroup.LayoutParams layoutParams4;
        super.onConfigurationChanged(configuration);
        this.mCollapseTitleShowable = true;
        updateTightTitle();
        if ((getDisplayOptions() & 8) != 0) {
            this.mCollapseTitle.onConfigurationChanged(configuration);
            this.mExpandTitle.onConfigurationChanged(configuration);
        }
        this.mUncollapsePaddingH = getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_title_horizontal_padding);
        this.mMovableContainer.setPaddingRelative(this.mUncollapsePaddingH, getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_title_top_padding), this.mUncollapsePaddingH, TextUtils.isEmpty(this.mSubtitle) ? this.mExpandTitlePaddingBottom : this.mExpandSubtitlePaddingBottom);
        setPaddingRelative(AttributeResolver.resolveDimensionPixelSize(getContext(), R$attr.actionBarPaddingStart), getPaddingTop(), AttributeResolver.resolveDimensionPixelSize(getContext(), R$attr.actionBarPaddingEnd), getPaddingBottom());
        ScrollingTabContainerView scrollingTabContainerView = this.mCollapseTabs;
        if (scrollingTabContainerView != null && this.mIncludeTabs && (layoutParams4 = scrollingTabContainerView.getLayoutParams()) != null) {
            layoutParams4.width = -2;
            layoutParams4.height = -1;
        }
        ScrollingTabContainerView scrollingTabContainerView2 = this.mExpandTabs;
        if (scrollingTabContainerView2 != null && this.mIncludeTabs && (layoutParams3 = scrollingTabContainerView2.getLayoutParams()) != null) {
            layoutParams3.width = -2;
            layoutParams3.height = -2;
        }
        ScrollingTabContainerView scrollingTabContainerView3 = this.mSecondaryCollapseTabs;
        if (scrollingTabContainerView3 != null && this.mIncludeTabs && (layoutParams2 = scrollingTabContainerView3.getLayoutParams()) != null) {
            layoutParams2.width = -2;
            layoutParams2.height = -1;
        }
        ScrollingTabContainerView scrollingTabContainerView4 = this.mSecondaryExpandTabs;
        if (scrollingTabContainerView4 == null || !this.mIncludeTabs || (layoutParams = scrollingTabContainerView4.getLayoutParams()) == null) {
            return;
        }
        layoutParams.width = -2;
        layoutParams.height = -1;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.hideOverflowMenu(false);
            this.mActionMenuPresenter.hideSubMenus();
        }
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    protected void onExpandStateChanged(int i, int i2) {
        if (i == 2) {
            this.mPendingHeight = 0;
            if (!this.mPostScroller.isFinished()) {
                this.mPostScroller.forceFinished(true);
            }
        }
        if (i2 != 0) {
            this.mMovableContainer.setVisibility(0);
            this.mMovableTabContainer.setVisibility(0);
        }
        if (i2 != 0) {
            this.mPendingHeight = (getHeight() - this.mCollapseHeight) + this.mCollapseTabHeight;
            return;
        }
        this.mMovableContainer.setVisibility(8);
        this.mMovableTabContainer.setVisibility(8);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mInActionModeAnimating) {
            return;
        }
        int measuredHeight = this.mCollapseContainer.getMeasuredHeight();
        View view = this.mCustomNavView;
        if (view != null && view.getParent() != null) {
            measuredHeight = this.mCustomNavView.getMeasuredHeight();
        }
        int i5 = measuredHeight;
        int i6 = 0;
        int measuredHeight2 = this.mCollapseTabContainer.getParent() == null ? 0 : this.mCollapseTabContainer.getMeasuredHeight();
        int measuredHeight3 = this.mMovableContainer.getMeasuredHeight();
        int measuredHeight4 = this.mMovableTabContainer.getParent() == null ? 0 : this.mMovableTabContainer.getMeasuredHeight();
        int i7 = this.mInnerExpandState;
        if (i7 == 2) {
            i6 = this.mPendingHeight;
        } else if (i7 == 1) {
            i6 = measuredHeight3 + measuredHeight4;
        }
        int i8 = (i4 - i2) - measuredHeight4;
        int i9 = i8 - i6;
        float f = ((measuredHeight3 + measuredHeight4) - i6) / measuredHeight3;
        ActionBarTransitionListener actionBarTransitionListener = this.mTransitionListener;
        if (actionBarTransitionListener != null) {
            actionBarTransitionListener.onActionBarMove(this.mLastProcess - f, f);
        }
        onLayoutCollapseViews(z, i, 0, i3, i5, measuredHeight2);
        onLayoutExpandViews(z, i, i9, i3, i8, measuredHeight4, f);
        animateLayoutWithProcess(f);
        this.mLastProcess = f;
    }

    protected void onLayoutExpandViews(boolean z, int i, int i2, int i3, int i4, int i5, float f) {
        int i6;
        int i7;
        if (hasTitle()) {
            if (this.mInnerExpandState == 2) {
                int i8 = f < 1.0f ? 0 : 4;
                FrameLayout frameLayout = this.mMovableContainer;
                if (frameLayout != null && frameLayout.getVisibility() != i8) {
                    this.mMovableContainer.setVisibility(i8);
                }
            }
            int i9 = 1.0f - Math.min(1.0f, 3.0f * f) <= 0.0f ? this.mCollapseTabHeight : 0;
            FrameLayout frameLayout2 = this.mMovableContainer;
            int measuredHeight = (frameLayout2 == null || frameLayout2.getVisibility() != 0) ? 0 : this.mMovableContainer.getMeasuredHeight();
            SpringBackLayout springBackLayout = this.mMovableTabContainer;
            int measuredHeight2 = (springBackLayout == null || springBackLayout.getParent() == null || this.mMovableTabContainer.getVisibility() != 0) ? 0 : this.mMovableTabContainer.getMeasuredHeight();
            int i10 = (((i2 + measuredHeight) + measuredHeight2) - i4) + i9;
            FrameLayout frameLayout3 = this.mMovableContainer;
            ScrollingTabContainerView scrollingTabContainerView = null;
            if (frameLayout3 != null && frameLayout3.getVisibility() == 0 && this.mInnerExpandState != 0) {
                this.mMovableContainer.layout(i, i4 - measuredHeight, i3, i4);
                ScrollingTabContainerView scrollingTabContainerView2 = (this.mMovableContainer.getChildCount() == 1 && (this.mMovableContainer.getChildAt(0) instanceof ScrollingTabContainerView)) ? (ScrollingTabContainerView) this.mMovableContainer.getChildAt(0) : null;
                if (scrollingTabContainerView2 != null) {
                    int i11 = this.mUncollapsePaddingH;
                    if (ViewUtils.isLayoutRtl(this)) {
                        i11 = (i3 - this.mUncollapsePaddingH) - scrollingTabContainerView2.getMeasuredWidth();
                    }
                    scrollingTabContainerView2.layout(i11, this.mExpandTabTopPadding, scrollingTabContainerView2.getMeasuredWidth() + i11, scrollingTabContainerView2.getMeasuredHeight() + this.mExpandTabTopPadding);
                }
                clipViewBounds(this.mMovableContainer, i, i10, i3, measuredHeight + measuredHeight2);
            }
            SpringBackLayout springBackLayout2 = this.mMovableTabContainer;
            if (springBackLayout2 == null || springBackLayout2.getChildCount() == 0 || this.mInnerExpandState == 0) {
                return;
            }
            SpringBackLayout springBackLayout3 = this.mMovableTabContainer;
            int i12 = i4 + i5;
            ViewUtils.layoutChildView(this, springBackLayout3, i + this.mUncollapseTabPaddingH, i12 - springBackLayout3.getMeasuredHeight(), i3 - this.mUncollapseTabPaddingH, i12);
            if (this.mMovableTabContainer.getChildCount() == 1 && (this.mMovableTabContainer.getChildAt(0) instanceof ScrollingTabContainerView)) {
                scrollingTabContainerView = (ScrollingTabContainerView) this.mMovableTabContainer.getChildAt(0);
            }
            ScrollingTabContainerView scrollingTabContainerView3 = scrollingTabContainerView;
            if (scrollingTabContainerView3 != null) {
                int measuredWidth = scrollingTabContainerView3.getMeasuredWidth();
                if (ViewUtils.isLayoutRtl(this)) {
                    i7 = (i3 - (this.mUncollapseTabPaddingH * 2)) - scrollingTabContainerView3.getMeasuredWidth();
                    i6 = i3 - (this.mUncollapseTabPaddingH * 2);
                } else {
                    i6 = measuredWidth;
                    i7 = 0;
                }
                scrollingTabContainerView3.layout(i7, 0, i6, scrollingTabContainerView3.getMeasuredHeight());
            }
            clipViewBounds(this.mMovableTabContainer, i, i10 - (measuredHeight - measuredHeight2), i3, measuredHeight + measuredHeight2);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:105:0x01d2  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x01e1  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x01e4  */
    /* JADX WARN: Removed duplicated region for block: B:111:0x01e8  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x01ed  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x01f9  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x01fc  */
    /* JADX WARN: Removed duplicated region for block: B:183:0x0380  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x0399  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onMeasure(int r22, int r23) {
        /*
            Method dump skipped, instructions count: 1004
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.appcompat.internal.app.widget.ActionBarView.onMeasure(int, int):void");
    }

    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3, int[] iArr2) {
        if (i2 <= 0 || getHeight() <= this.mCollapseHeight) {
            return;
        }
        int height = getHeight() - i2;
        int i4 = this.mPendingHeight;
        if (height >= this.mCollapseHeight) {
            this.mPendingHeight = i4 - i2;
            iArr[1] = iArr[1] + i2;
        } else {
            this.mPendingHeight = 0;
            iArr[1] = iArr[1] + ((getHeight() - this.mCollapseHeight) - this.mCollapseTabHeight);
        }
        int i5 = this.mPendingHeight;
        if (i5 != i4) {
            iArr2[1] = i4 - i5;
            requestLayout();
        }
    }

    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr, int[] iArr2) {
        int measuredHeight = this.mMovableContainer.getMeasuredHeight() + this.mMovableTabHeight;
        int i6 = (this.mCollapseHeight - this.mCollapseTabHeight) + measuredHeight;
        int height = getHeight();
        if (i4 >= 0 || height >= i6) {
            return;
        }
        int i7 = this.mPendingHeight;
        if (height - i4 <= i6) {
            this.mPendingHeight = i7 - i4;
            iArr[1] = iArr[1] + i4;
        } else {
            this.mPendingHeight = measuredHeight;
            iArr[1] = iArr[1] + (-(i6 - height));
        }
        int i8 = this.mPendingHeight;
        if (i8 != i7) {
            iArr2[1] = i7 - i8;
            requestLayout();
        }
    }

    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
        if (i2 == 0) {
            this.mTouchScrolling = true;
        } else {
            this.mNonTouchScrolling = true;
        }
        if (!this.mPostScroller.isFinished()) {
            this.mPostScroller.forceFinished(true);
        }
        setExpandState(2);
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        MenuBuilder menuBuilder;
        MenuItem findItem;
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        int i = savedState.expandedMenuItemId;
        if (i != 0 && this.mExpandedMenuPresenter != null && (menuBuilder = this.mOptionsMenu) != null && (findItem = menuBuilder.findItem(i)) != null) {
            findItem.expandActionView();
        }
        if (savedState.isOverflowOpen) {
            postShowOverflowMenu();
        }
        setExpandState(savedState.expandState);
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        MenuItemImpl menuItemImpl;
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        ExpandedActionViewMenuPresenter expandedActionViewMenuPresenter = this.mExpandedMenuPresenter;
        if (expandedActionViewMenuPresenter == null || (menuItemImpl = expandedActionViewMenuPresenter.mCurrentExpandedItem) == null) {
            savedState.expandedMenuItemId = 0;
        } else {
            savedState.expandedMenuItemId = menuItemImpl.getItemId();
        }
        savedState.isOverflowOpen = isOverflowMenuShowing();
        int i = this.mInnerExpandState;
        if (i == 2) {
            savedState.expandState = 0;
        } else {
            savedState.expandState = i;
        }
        return savedState;
    }

    @Override // miuix.view.ActionModeAnimationListener
    public void onStart(boolean z) {
        this.mInActionModeAnimating = true;
    }

    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        return (getContext().getResources().getConfiguration().orientation != 2 || DeviceHelper.isTablet(getContext())) && this.mExpandedActionView == null && isShowTitle() && isResizable();
    }

    @Override // miuix.view.ActionModeAnimationListener
    public void onStop(boolean z) {
        this.mInActionModeAnimating = false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0010, code lost:
    
        if (r3.mNonTouchScrolling == false) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onStopNestedScroll(android.view.View r4, int r5) {
        /*
            r3 = this;
            android.widget.FrameLayout r4 = r3.mMovableContainer
            int r4 = r4.getMeasuredHeight()
            boolean r5 = r3.mTouchScrolling
            r0 = 1
            r1 = 0
            if (r5 == 0) goto L13
            r3.mTouchScrolling = r1
            boolean r5 = r3.mNonTouchScrolling
            if (r5 != 0) goto L1b
            goto L19
        L13:
            boolean r5 = r3.mNonTouchScrolling
            if (r5 == 0) goto L1b
            r3.mNonTouchScrolling = r1
        L19:
            r5 = r0
            goto L1c
        L1b:
            r5 = r1
        L1c:
            if (r5 == 0) goto L64
            int r5 = r3.mPendingHeight
            if (r5 != 0) goto L26
            r3.setExpandState(r1)
            return
        L26:
            int r2 = r3.mMovableTabHeight
            int r2 = r2 + r4
            if (r5 != r2) goto L2f
            r3.setExpandState(r0)
            return
        L2f:
            int r5 = r3.getHeight()
            int r0 = r3.mCollapseHeight
            int r2 = r3.mMovableTabHeight
            int r2 = r2 + r4
            int r2 = r2 / 2
            int r0 = r0 + r2
            if (r5 <= r0) goto L4f
            android.widget.Scroller r5 = r3.mPostScroller
            int r0 = r3.getHeight()
            int r2 = r3.mCollapseHeight
            int r2 = r2 + r4
            int r4 = r3.getHeight()
            int r2 = r2 - r4
            r5.startScroll(r1, r0, r1, r2)
            goto L5f
        L4f:
            android.widget.Scroller r4 = r3.mPostScroller
            int r5 = r3.getHeight()
            int r0 = r3.mCollapseHeight
            int r2 = r3.getHeight()
            int r0 = r0 - r2
            r4.startScroll(r1, r5, r1, r0)
        L5f:
            java.lang.Runnable r4 = r3.mPostScroll
            r3.postOnAnimation(r4)
        L64:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.appcompat.internal.app.widget.ActionBarView.onStopNestedScroll(android.view.View, int):void");
    }

    @Override // miuix.view.ActionModeAnimationListener
    public void onUpdate(boolean z, float f) {
        if (!z) {
            f = 1.0f - f;
        }
        animateLayoutWithProcess(f);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void postShowOverflowMenu() {
        super.postShowOverflowMenu();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setActionBarTransitionListener(ActionBarTransitionListener actionBarTransitionListener) {
        super.setActionBarTransitionListener(actionBarTransitionListener);
    }

    public void setCallback(ActionBar.OnNavigationListener onNavigationListener) {
        this.mCallback = onNavigationListener;
    }

    public void setCollapsable(boolean z) {
    }

    public void setCustomNavigationView(View view) {
        boolean z = (this.mDisplayOptions & 16) != 0;
        View view2 = this.mCustomNavView;
        if (view2 != null && z) {
            removeView(view2);
        }
        this.mCustomNavView = view;
        if (view == null || !z) {
            this.mCollapseController.attachViews(this.mCollapseContainer);
            return;
        }
        addView(view);
        addedCustomView();
    }

    public void setDisplayOptions(int i) {
        View view;
        int i2 = this.mDisplayOptions;
        int i3 = i2 != -1 ? i ^ i2 : -1;
        this.mDisplayOptions = i;
        if ((i3 & 31) != 0) {
            boolean z = false;
            boolean z2 = (i & 2) != 0;
            if (z2) {
                initHomeLayout();
                this.mHomeLayout.setVisibility(this.mExpandedActionView == null ? 0 : 8);
                if ((i3 & 4) != 0) {
                    boolean z3 = (i & 4) != 0;
                    this.mHomeLayout.setUp(z3);
                    if (z3) {
                        setHomeButtonEnabled(true);
                    }
                }
                if ((i3 & 1) != 0) {
                    Drawable logo = getLogo();
                    boolean z4 = (logo == null || (i & 1) == 0) ? false : true;
                    HomeView homeView = this.mHomeLayout;
                    if (!z4) {
                        logo = getIcon();
                    }
                    homeView.setIcon(logo);
                }
            } else {
                HomeView homeView2 = this.mHomeLayout;
                if (homeView2 != null) {
                    removeView(homeView2);
                }
            }
            if ((i3 & 8) != 0) {
                if ((i & 8) != 0) {
                    if (getNavigationMode() == 2) {
                        safeAddView(this, this.mCollapseTabContainer);
                        safeAddView(this, this.mMovableTabContainer);
                        ScrollingTabContainerView scrollingTabContainerView = this.mSecondaryCollapseTabs;
                        if (scrollingTabContainerView != null) {
                            safeAddView(this.mCollapseTabContainer, scrollingTabContainerView);
                            this.mCollapseTabContainer.setTarget(this.mSecondaryCollapseTabs);
                        }
                        ScrollingTabContainerView scrollingTabContainerView2 = this.mSecondaryExpandTabs;
                        if (scrollingTabContainerView2 != null) {
                            safeAddView(this.mMovableTabContainer, scrollingTabContainerView2);
                            this.mMovableTabContainer.setTarget(this.mSecondaryExpandTabs);
                        }
                        this.mCollapseContainer.removeAllViews();
                        this.mMovableContainer.removeAllViews();
                    }
                    initTitle();
                } else {
                    CollapseTitle collapseTitle = this.mCollapseTitle;
                    if (collapseTitle != null) {
                        this.mCollapseContainer.removeView(collapseTitle.getLayout());
                    }
                    ExpandTitle expandTitle = this.mExpandTitle;
                    if (expandTitle != null) {
                        this.mMovableContainer.removeView(expandTitle.getLayout());
                    }
                    removeView(this.mTitleUpView);
                    this.mCollapseTitle = null;
                    this.mExpandTitle = null;
                    this.mTitleUpView = null;
                    if (getNavigationMode() == 2) {
                        removeView(this.mCollapseTabContainer);
                        removeView(this.mMovableTabContainer);
                        this.mCollapseTabContainer.removeAllViews();
                        this.mMovableTabContainer.removeAllViews();
                        ScrollingTabContainerView scrollingTabContainerView3 = this.mCollapseTabs;
                        if (scrollingTabContainerView3 != null) {
                            safeAddView(this.mCollapseContainer, scrollingTabContainerView3);
                        }
                        ScrollingTabContainerView scrollingTabContainerView4 = this.mExpandTabs;
                        if (scrollingTabContainerView4 != null) {
                            safeAddView(this.mMovableContainer, scrollingTabContainerView4);
                        }
                    }
                }
            }
            CollapseTitle collapseTitle2 = this.mCollapseTitle;
            if (collapseTitle2 != null && (i3 & 6) != 0) {
                boolean z5 = (this.mDisplayOptions & 4) != 0;
                if (collapseTitle2.getVisibility() == 0 || (getDisplayOptions() & 32) != 0) {
                    this.mTitleUpView.setVisibility(z2 ? 8 : z5 ? 0 : 4);
                }
                this.mCollapseTitle.setEnabled(!z2 && z5);
                ExpandTitle expandTitle2 = this.mExpandTitle;
                if (!z2 && z5) {
                    z = true;
                }
                expandTitle2.setEnabled(z);
            }
            if ((i3 & 16) != 0 && (view = this.mCustomNavView) != null) {
                if ((i & 16) != 0) {
                    safeAddView(this, view);
                    addedCustomView();
                } else {
                    removeView(view);
                }
            }
            requestLayout();
        } else {
            invalidate();
        }
        HomeView homeView3 = this.mHomeLayout;
        if (homeView3 != null) {
            if (!homeView3.isEnabled()) {
                this.mHomeLayout.setContentDescription(null);
            } else if ((i & 4) != 0) {
                this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R$string.abc_action_bar_up_description));
            } else {
                this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R$string.abc_action_bar_home_description));
            }
        }
    }

    public void setDropdownAdapter(SpinnerAdapter spinnerAdapter) {
        this.mSpinnerAdapter = spinnerAdapter;
        Spinner spinner = this.mSpinner;
        if (spinner != null) {
            spinner.setAdapter(spinnerAdapter);
        }
    }

    public void setDropdownSelectedPosition(int i) {
        this.mSpinner.setSelection(i);
    }

    public void setEmbeddedTabView(ScrollingTabContainerView scrollingTabContainerView, ScrollingTabContainerView scrollingTabContainerView2, ScrollingTabContainerView scrollingTabContainerView3, ScrollingTabContainerView scrollingTabContainerView4) {
        boolean z = scrollingTabContainerView != null;
        this.mIncludeTabs = z;
        if (z && this.mNavigationMode == 2) {
            addTabsContainer(scrollingTabContainerView, scrollingTabContainerView2, scrollingTabContainerView3, scrollingTabContainerView4);
        }
    }

    public void setEndView(View view) {
        View view2 = this.mEndView;
        if (view2 != null) {
            removeView(view2);
        }
        this.mEndView = view;
        if (view != null) {
            addView(view);
            Folme.useAt(this.mEndView).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setAlpha(0.6f, new ITouchStyle.TouchType[0]).handleTouchOf(view, new AnimConfig[0]);
            Folme.useAt(this.mEndView).hover().setFeedbackRadius(60.0f);
            Folme.useAt(this.mEndView).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this.mEndView, new AnimConfig[0]);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setExpandState(int i) {
        super.setExpandState(i);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setExpandState(int i, boolean z, boolean z2) {
        super.setExpandState(i, z, z2);
    }

    public void setHomeAsUpIndicator(int i) {
        HomeView homeView = this.mHomeLayout;
        if (homeView != null) {
            homeView.setUpIndicator(i);
            return;
        }
        this.mHomeAsUpIndicatorDrawable = null;
        this.mHomeAsUpIndicatorResId = i;
    }

    public void setHomeAsUpIndicator(Drawable drawable) {
        HomeView homeView = this.mHomeLayout;
        if (homeView != null) {
            homeView.setUpIndicator(drawable);
            return;
        }
        this.mHomeAsUpIndicatorDrawable = drawable;
        this.mHomeAsUpIndicatorResId = 0;
    }

    public void setHomeButtonEnabled(boolean z) {
        HomeView homeView = this.mHomeLayout;
        if (homeView != null) {
            homeView.setEnabled(z);
            this.mHomeLayout.setFocusable(z);
            if (!z) {
                this.mHomeLayout.setContentDescription(null);
            } else if ((this.mDisplayOptions & 4) != 0) {
                this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R$string.abc_action_bar_up_description));
            } else {
                this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R$string.abc_action_bar_home_description));
            }
        }
    }

    public void setIcon(int i) {
        setIcon(this.mContext.getResources().getDrawable(i));
    }

    public void setIcon(Drawable drawable) {
        HomeView homeView;
        this.mIcon = drawable;
        this.mIconLogoInitIndicator |= 1;
        if (drawable != null && (((this.mDisplayOptions & 1) == 0 || getLogo() == null) && (homeView = this.mHomeLayout) != null)) {
            homeView.setIcon(drawable);
        }
        if (this.mExpandedActionView != null) {
            this.mExpandedHomeLayout.setIcon(this.mIcon.getConstantState().newDrawable(getResources()));
        }
    }

    public void setLogo(int i) {
        setLogo(this.mContext.getResources().getDrawable(i));
    }

    public void setLogo(Drawable drawable) {
        HomeView homeView;
        this.mLogo = drawable;
        this.mIconLogoInitIndicator |= 2;
        if (drawable == null || (this.mDisplayOptions & 1) == 0 || (homeView = this.mHomeLayout) == null) {
            return;
        }
        homeView.setIcon(drawable);
    }

    public void setMenu(Menu menu, MenuPresenter.Callback callback) {
        ActionMenuView actionMenuView;
        ViewGroup viewGroup;
        MenuBuilder menuBuilder = this.mOptionsMenu;
        if (menu == menuBuilder) {
            return;
        }
        if (this.mSplitActionBar || menuBuilder != null) {
            if (menuBuilder != null) {
                menuBuilder.removeMenuPresenter(this.mActionMenuPresenter);
                this.mOptionsMenu.removeMenuPresenter(this.mExpandedMenuPresenter);
            }
            MenuBuilder menuBuilder2 = (MenuBuilder) menu;
            this.mOptionsMenu = menuBuilder2;
            ActionMenuView actionMenuView2 = this.mMenuView;
            if (actionMenuView2 != null && (viewGroup = (ViewGroup) actionMenuView2.getParent()) != null) {
                viewGroup.removeView(this.mMenuView);
            }
            if (this.mActionMenuPresenter == null) {
                this.mActionMenuPresenter = createActionMenuPresenter(callback);
                this.mExpandedMenuPresenter = createExpandedActionViewMenuPresenter();
            }
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -1);
            if (this.mSplitActionBar) {
                this.mActionMenuPresenter.setExpandedActionViewsExclusive(false);
                this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
                layoutParams.width = -1;
                layoutParams.height = -2;
                layoutParams.gravity = DeviceHelper.isTablet(getContext()) ? 17 : 80;
                configPresenters(menuBuilder2);
                actionMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
                if (this.mSplitView != null) {
                    ViewGroup viewGroup2 = (ViewGroup) actionMenuView.getParent();
                    if (viewGroup2 != null && viewGroup2 != this.mSplitView) {
                        viewGroup2.removeView(actionMenuView);
                    }
                    actionMenuView.setVisibility(getAnimatedVisibility());
                    this.mSplitView.addView(actionMenuView, 1, layoutParams);
                    View findViewById = actionMenuView.findViewById(R$id.expanded_menu);
                    if (findViewById != null) {
                        findViewById.requestLayout();
                    }
                } else {
                    actionMenuView.setLayoutParams(layoutParams);
                }
            } else {
                this.mActionMenuPresenter.setExpandedActionViewsExclusive(getResources().getBoolean(R$bool.abc_action_bar_expanded_action_views_exclusive));
                configPresenters(menuBuilder2);
                actionMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
                ViewGroup viewGroup3 = (ViewGroup) actionMenuView.getParent();
                if (viewGroup3 != null && viewGroup3 != this) {
                    viewGroup3.removeView(actionMenuView);
                }
                addView(actionMenuView, layoutParams);
            }
            this.mMenuView = actionMenuView;
        }
    }

    public void setNavigationMode(int i) {
        ScrollingTabContainerView scrollingTabContainerView;
        ScrollingTabContainerView scrollingTabContainerView2;
        LinearLayout linearLayout;
        int i2 = this.mNavigationMode;
        if (i != i2) {
            if (i2 == 1 && (linearLayout = this.mListNavLayout) != null) {
                removeView(linearLayout);
            }
            if (i == 1) {
                throw new UnsupportedOperationException("MIUIX Deleted");
            }
            if (i == 2 && (scrollingTabContainerView = this.mCollapseTabs) != null && (scrollingTabContainerView2 = this.mExpandTabs) != null && this.mIncludeTabs) {
                addTabsContainer(scrollingTabContainerView, scrollingTabContainerView2, this.mSecondaryCollapseTabs, this.mSecondaryExpandTabs);
            }
            this.mNavigationMode = i;
            requestLayout();
        }
    }

    public void setProgress(int i) {
        updateProgressBars(i + 0);
    }

    public void setProgressBarIndeterminate(boolean z) {
        updateProgressBars(z ? -3 : -4);
    }

    public void setProgressBarIndeterminateVisibility(boolean z) {
        updateProgressBars(z ? -1 : -2);
    }

    public void setProgressBarVisibility(boolean z) {
        updateProgressBars(z ? -1 : -2);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setResizable(boolean z) {
        super.setResizable(z);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public void setSplitActionBar(boolean z) {
        if (this.mSplitActionBar != z) {
            ActionMenuView actionMenuView = this.mMenuView;
            if (actionMenuView != null) {
                ViewGroup viewGroup = (ViewGroup) actionMenuView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(this.mMenuView);
                }
                if (z) {
                    ActionBarContainer actionBarContainer = this.mSplitView;
                    if (actionBarContainer != null) {
                        actionBarContainer.addView(this.mMenuView);
                    }
                    this.mMenuView.getLayoutParams().width = -1;
                } else {
                    addView(this.mMenuView);
                    this.mMenuView.getLayoutParams().width = -2;
                }
                this.mMenuView.requestLayout();
            }
            ActionBarContainer actionBarContainer2 = this.mSplitView;
            if (actionBarContainer2 != null) {
                actionBarContainer2.setVisibility(z ? 0 : 8);
            }
            ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
            if (actionMenuPresenter != null) {
                if (z) {
                    actionMenuPresenter.setExpandedActionViewsExclusive(false);
                    this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
                } else {
                    actionMenuPresenter.setExpandedActionViewsExclusive(getResources().getBoolean(R$bool.abc_action_bar_expanded_action_views_exclusive));
                }
            }
            super.setSplitActionBar(z);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setSplitView(ActionBarContainer actionBarContainer) {
        super.setSplitView(actionBarContainer);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setSplitWhenNarrow(boolean z) {
        super.setSplitWhenNarrow(z);
    }

    public void setStartView(View view) {
        View view2 = this.mStartView;
        if (view2 != null) {
            removeView(view2);
        }
        this.mStartView = view;
        if (view != null) {
            addView(view);
            Folme.useAt(view).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setAlpha(0.6f, new ITouchStyle.TouchType[0]).handleTouchOf(view, new AnimConfig[0]);
            Folme.useAt(this.mStartView).hover().setFeedbackRadius(60.0f);
            Folme.useAt(this.mStartView).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this.mStartView, new AnimConfig[0]);
        }
    }

    public void setSubtitle(CharSequence charSequence) {
        this.mSubtitle = charSequence;
        CollapseTitle collapseTitle = this.mCollapseTitle;
        if (collapseTitle != null) {
            collapseTitle.setSubTitle(charSequence);
            this.mExpandTitle.setSubTitle(charSequence);
            boolean z = false;
            this.mCollapseTitle.setSubTitleVisibility(charSequence != null ? 0 : 8);
            this.mExpandTitle.setSubTitleVisibility(charSequence != null ? 0 : 8);
            if (this.mExpandedActionView == null && (this.mDisplayOptions & 8) != 0 && (!TextUtils.isEmpty(this.mTitle) || !TextUtils.isEmpty(this.mSubtitle))) {
                z = true;
            }
            setTitleVisibility(z);
            post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ActionBarView.this.lambda$setSubtitle$2();
                }
            });
        }
    }

    public void setTitle(CharSequence charSequence) {
        this.mUserTitle = true;
        setTitleImpl(charSequence);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView, android.view.View
    public /* bridge */ /* synthetic */ void setVisibility(int i) {
        super.setVisibility(i);
    }

    public void setWindowCallback(Window.Callback callback) {
        this.mWindowCallback = callback;
    }

    public void setWindowTitle(CharSequence charSequence) {
        if (this.mUserTitle) {
            return;
        }
        setTitleImpl(charSequence);
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public boolean showImmersionMore() {
        View view = this.mImmersionView;
        if (view != null) {
            view.setVisibility(0);
            return true;
        }
        return false;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ boolean showOverflowMenu() {
        return super.showOverflowMenu();
    }
}
