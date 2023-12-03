package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.ActionBarTransitionListener;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.IFragment;
import miuix.appcompat.internal.view.ActionBarPolicy;
import miuix.appcompat.internal.view.ActionModeImpl;
import miuix.appcompat.internal.view.EditActionModeImpl;
import miuix.appcompat.internal.view.SearchActionModeImpl;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;
import miuix.appcompat.internal.view.menu.action.PhoneActionMenuView;
import miuix.view.SearchActionMode;

/* loaded from: classes5.dex */
public class ActionBarImpl extends ActionBar {
    private static ActionBar.TabListener sTabListenerWrapper = new ActionBar.TabListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.1
        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            TabImpl tabImpl = (TabImpl) tab;
            if (tabImpl.mInternalCallback != null) {
                tabImpl.mInternalCallback.onTabReselected(tab, fragmentTransaction);
            }
            if (tabImpl.mCallback != null) {
                tabImpl.mCallback.onTabReselected(tab, fragmentTransaction);
            }
        }

        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            TabImpl tabImpl = (TabImpl) tab;
            if (tabImpl.mInternalCallback != null) {
                tabImpl.mInternalCallback.onTabSelected(tab, fragmentTransaction);
            }
            if (tabImpl.mCallback != null) {
                tabImpl.mCallback.onTabSelected(tab, fragmentTransaction);
            }
        }

        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            TabImpl tabImpl = (TabImpl) tab;
            if (tabImpl.mInternalCallback != null) {
                tabImpl.mInternalCallback.onTabUnselected(tab, fragmentTransaction);
            }
            if (tabImpl.mCallback != null) {
                tabImpl.mCallback.onTabUnselected(tab, fragmentTransaction);
            }
        }
    };
    ActionMode mActionMode;
    private ActionModeView mActionModeView;
    private ActionBarView mActionView;
    private ScrollingTabContainerView mCollapseTabScrollView;
    private IStateStyle mContainerAnim;
    private ActionBarContainer mContainerView;
    private View mContentMask;
    private View.OnClickListener mContentMaskOnClickListenr;
    private Context mContext;
    private int mContextDisplayMode;
    private ActionBarContextView mContextView;
    private int mCurrentAccessibilityImportant;
    private int mCurrentExpandState;
    private boolean mCurrentResizable;
    private boolean mDisplayHomeAsUpSet;
    private ScrollingTabContainerView mExpandTabScrollView;
    private ScrollingTabContainerView mExpanedTabScrollView;
    private FragmentManager mFragmentManager;
    private boolean mHiddenByApp;
    private boolean mHiddenBySystem;
    private ActionBarOverlayLayout mOverlayLayout;
    private SearchActionModeView mSearchActionModeView;
    private TabImpl mSelectedTab;
    private boolean mShowHideAnimationEnabled;
    private boolean mShowingForMode;
    private PhoneActionMenuView mSplitMenuView;
    private ActionBarContainer mSplitView;
    private IStateStyle mSpliterAnim;
    private ScrollingTabContainerView mTabScrollView;
    private Context mThemedContext;
    private ActionBarViewPagerController mViewPagerController;
    private ArrayList<TabImpl> mTabs = new ArrayList<>();
    private int mSavedTabPosition = -1;
    private ArrayList<ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList<>();
    private int mCurWindowVisibility = 0;
    private boolean mNowShowing = true;
    private ActionModeImpl.ActionModeCallback mActionModeCallback = new ActionModeImpl.ActionModeCallback() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.2
        @Override // miuix.appcompat.internal.view.ActionModeImpl.ActionModeCallback
        public void onActionModeFinish(ActionMode actionMode) {
            ActionBarImpl.this.animateToMode(false);
            ActionBarImpl.this.mActionMode = null;
        }
    };

    /* loaded from: classes5.dex */
    public class TabImpl extends ActionBar.Tab {
        private ActionBar.TabListener mCallback;
        private CharSequence mContentDesc;
        private View mCustomView;
        private Drawable mIcon;
        private ActionBar.TabListener mInternalCallback;
        private CharSequence mText;
        private int mPosition = -1;
        public boolean mWithAnim = true;

        public TabImpl() {
        }

        public ActionBar.TabListener getCallback() {
            return ActionBarImpl.sTabListenerWrapper;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public CharSequence getContentDescription() {
            return this.mContentDesc;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public View getCustomView() {
            return this.mCustomView;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public Drawable getIcon() {
            return this.mIcon;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public int getPosition() {
            return this.mPosition;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public CharSequence getText() {
            return this.mText;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public void select() {
            ActionBarImpl.this.selectTab(this);
        }

        public ActionBar.Tab setInternalTabListener(ActionBar.TabListener tabListener) {
            this.mInternalCallback = tabListener;
            return this;
        }

        public void setPosition(int i) {
            this.mPosition = i;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public ActionBar.Tab setText(CharSequence charSequence) {
            this.mText = charSequence;
            if (this.mPosition >= 0) {
                ActionBarImpl.this.mTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mExpanedTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mCollapseTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mCollapseTabScrollView.updateTab(this.mPosition);
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class ViewHideTransitionListener extends TransitionListener {
        private WeakReference<View> mRef;

        public ViewHideTransitionListener(View view) {
            this.mRef = new WeakReference<>(view);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj, UpdateInfo updateInfo) {
            View view = this.mRef.get();
            if (view != null) {
                view.setVisibility(8);
            }
        }
    }

    public ActionBarImpl(Fragment fragment) {
        this.mContext = ((IFragment) fragment).getThemedContext();
        this.mFragmentManager = fragment.getChildFragmentManager();
        init((ViewGroup) fragment.getView());
        FragmentActivity activity = fragment.getActivity();
        setTitle(activity != null ? activity.getTitle() : null);
    }

    public ActionBarImpl(AppCompatActivity appCompatActivity, ViewGroup viewGroup) {
        this.mContext = appCompatActivity;
        this.mFragmentManager = appCompatActivity.getSupportFragmentManager();
        init(viewGroup);
        setTitle(appCompatActivity.getTitle());
    }

    private static boolean checkShowingFlags(boolean z, boolean z2, boolean z3) {
        if (z3) {
            return true;
        }
        return (z || z2) ? false : true;
    }

    private void cleanupTabs() {
        if (this.mSelectedTab != null) {
            selectTab(null);
        }
        this.mTabs.clear();
        ScrollingTabContainerView scrollingTabContainerView = this.mTabScrollView;
        if (scrollingTabContainerView != null) {
            scrollingTabContainerView.removeAllTabs();
        }
        ScrollingTabContainerView scrollingTabContainerView2 = this.mExpanedTabScrollView;
        if (scrollingTabContainerView2 != null) {
            scrollingTabContainerView2.removeAllTabs();
        }
        ScrollingTabContainerView scrollingTabContainerView3 = this.mCollapseTabScrollView;
        if (scrollingTabContainerView3 != null) {
            scrollingTabContainerView3.removeAllTabs();
        }
        ScrollingTabContainerView scrollingTabContainerView4 = this.mExpandTabScrollView;
        if (scrollingTabContainerView4 != null) {
            scrollingTabContainerView4.removeAllTabs();
        }
        this.mSavedTabPosition = -1;
    }

    private void configureTab(ActionBar.Tab tab, int i) {
        TabImpl tabImpl = (TabImpl) tab;
        if (tabImpl.getCallback() == null) {
            throw new IllegalStateException("Action Bar Tab must have a Callback");
        }
        tabImpl.setPosition(i);
        this.mTabs.add(i, tabImpl);
        int size = this.mTabs.size();
        while (true) {
            i++;
            if (i >= size) {
                return;
            }
            this.mTabs.get(i).setPosition(i);
        }
    }

    private ActionMode createActionMode(ActionMode.Callback callback) {
        return callback instanceof SearchActionMode.Callback ? new SearchActionModeImpl(this.mContext, callback) : new EditActionModeImpl(this.mContext, callback);
    }

    private void doHide(boolean z) {
        doHide(z, null);
    }

    private void doHide(boolean z, AnimState animState) {
        AnimState animState2;
        IStateStyle iStateStyle = this.mContainerAnim;
        AnimState animState3 = null;
        if (iStateStyle != null) {
            animState2 = iStateStyle.getCurrentState();
            this.mContainerAnim.cancel();
        } else {
            animState2 = null;
        }
        boolean z2 = isShowHideAnimationEnabled() || z;
        if (z2) {
            this.mContainerAnim = startContainerViewAnimation(false, "HideActionBar", animState2, animState);
        } else {
            this.mContainerView.setTranslationY(-r8.getHeight());
            this.mContainerView.setAlpha(0.0f);
            this.mContainerView.setVisibility(8);
        }
        if (this.mSplitView != null) {
            IStateStyle iStateStyle2 = this.mSpliterAnim;
            if (iStateStyle2 != null) {
                animState3 = iStateStyle2.getCurrentState();
                this.mSpliterAnim.cancel();
            }
            if (z2) {
                this.mSpliterAnim = startSplitViewAnimation(false, "SpliterHide", animState3);
            } else {
                this.mSplitView.setTranslationY(getSplitHeight());
                this.mSplitView.setAlpha(0.0f);
                this.mSplitView.setVisibility(8);
            }
            updateContentMaskVisibility(false);
        }
    }

    private void doShow(boolean z) {
        doShow(z, null);
    }

    private void doShow(boolean z, AnimState animState) {
        AnimState animState2;
        View childAt;
        IStateStyle iStateStyle = this.mContainerAnim;
        AnimState animState3 = null;
        if (iStateStyle != null) {
            animState2 = iStateStyle.getCurrentState();
            this.mContainerAnim.cancel();
        } else {
            animState2 = null;
        }
        boolean z2 = isShowHideAnimationEnabled() || z;
        this.mContainerView.setVisibility(this.mActionMode instanceof SearchActionMode ? 8 : 0);
        if (z2) {
            this.mContainerAnim = startContainerViewAnimation(true, "ShowActionBar", animState2, animState);
        } else {
            this.mContainerView.setTranslationY(0.0f);
            this.mContainerView.setAlpha(1.0f);
        }
        if (this.mSplitView != null) {
            IStateStyle iStateStyle2 = this.mSpliterAnim;
            if (iStateStyle2 != null) {
                animState3 = iStateStyle2.getCurrentState();
                this.mSpliterAnim.cancel();
            }
            this.mSplitView.setVisibility(0);
            if (z2) {
                this.mSpliterAnim = startSplitViewAnimation(true, "SpliterShow", animState3);
                if (this.mActionView.isSplitActionBar() && this.mSplitView.getChildCount() > 0 && (childAt = this.mSplitView.getChildAt(0)) != null && (childAt instanceof PhoneActionMenuView) && (!((PhoneActionMenuView) childAt).isOverflowMenuShowing()) != false) {
                    ((ActionMenuView) childAt).startLayoutAnimation();
                }
            } else {
                this.mSplitView.setTranslationY(0.0f);
                this.mSplitView.setAlpha(1.0f);
            }
            updateContentMaskVisibility(true);
        }
    }

    private void ensureTabsExist() {
        if (this.mTabScrollView != null) {
            return;
        }
        CollapseTabContainer collapseTabContainer = new CollapseTabContainer(this.mContext);
        ExpandTabContainer expandTabContainer = new ExpandTabContainer(this.mContext);
        SecondaryCollapseTabContainer secondaryCollapseTabContainer = new SecondaryCollapseTabContainer(this.mContext);
        SecondaryExpandTabContainer secondaryExpandTabContainer = new SecondaryExpandTabContainer(this.mContext);
        collapseTabContainer.setVisibility(0);
        expandTabContainer.setVisibility(0);
        secondaryCollapseTabContainer.setVisibility(0);
        secondaryExpandTabContainer.setVisibility(0);
        this.mActionView.setEmbeddedTabView(collapseTabContainer, expandTabContainer, secondaryCollapseTabContainer, secondaryExpandTabContainer);
        collapseTabContainer.setEmbeded(true);
        this.mTabScrollView = collapseTabContainer;
        this.mExpanedTabScrollView = expandTabContainer;
        this.mCollapseTabScrollView = secondaryCollapseTabContainer;
        this.mExpandTabScrollView = secondaryExpandTabContainer;
    }

    private int getBlurOptioons() {
        int displayOptions = getDisplayOptions();
        int i = MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON;
        boolean z = (displayOptions & MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON) != 0;
        boolean z2 = (getDisplayOptions() & MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION) != 0;
        if (!z) {
            i = 0;
        }
        return i | (z2 ? 16384 : 0);
    }

    private int getSplitHeight() {
        View childAt;
        int height = this.mSplitView.getHeight();
        if (this.mSplitView.getChildCount() == 1 && (childAt = this.mSplitView.getChildAt(0)) != null && (childAt instanceof PhoneActionMenuView)) {
            PhoneActionMenuView phoneActionMenuView = (PhoneActionMenuView) childAt;
            return !phoneActionMenuView.isOverflowMenuShowing() ? phoneActionMenuView.getCollapsedHeight() : height;
        }
        return height;
    }

    private void setHasEmbeddedTabs(boolean z) {
        this.mContainerView.setTabContainer(null);
        this.mActionView.setEmbeddedTabView(this.mTabScrollView, this.mExpanedTabScrollView, this.mCollapseTabScrollView, this.mExpandTabScrollView);
        boolean z2 = getNavigationMode() == 2;
        ScrollingTabContainerView scrollingTabContainerView = this.mTabScrollView;
        if (scrollingTabContainerView != null) {
            if (z2) {
                scrollingTabContainerView.setVisibility(0);
            } else {
                scrollingTabContainerView.setVisibility(8);
            }
            this.mTabScrollView.setEmbeded(true);
        }
        ScrollingTabContainerView scrollingTabContainerView2 = this.mExpanedTabScrollView;
        if (scrollingTabContainerView2 != null) {
            if (z2) {
                scrollingTabContainerView2.setVisibility(0);
            } else {
                scrollingTabContainerView2.setVisibility(8);
            }
            this.mExpanedTabScrollView.setEmbeded(true);
        }
        ScrollingTabContainerView scrollingTabContainerView3 = this.mCollapseTabScrollView;
        if (scrollingTabContainerView3 != null) {
            if (z2) {
                scrollingTabContainerView3.setVisibility(0);
            } else {
                scrollingTabContainerView3.setVisibility(8);
            }
            this.mCollapseTabScrollView.setEmbeded(true);
        }
        ScrollingTabContainerView scrollingTabContainerView4 = this.mExpandTabScrollView;
        if (scrollingTabContainerView4 != null) {
            if (z2) {
                scrollingTabContainerView4.setVisibility(0);
            } else {
                scrollingTabContainerView4.setVisibility(8);
            }
            this.mExpandTabScrollView.setEmbeded(true);
        }
        this.mActionView.setCollapsable(false);
    }

    private IStateStyle startContainerViewAnimation(boolean z, String str, AnimState animState, AnimState animState2) {
        int height = this.mContainerView.getHeight();
        if (z) {
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(EaseManager.getStyle(-2, 0.9f, 0.25f));
            if (animState2 == null) {
                animState2 = new AnimState(str).add(ViewProperty.TRANSLATION_Y, 0.0d).add(ViewProperty.ALPHA, 1.0d);
            }
            IStateStyle state = Folme.useAt(this.mContainerView).state();
            if (animState != null) {
                animState.setTag(str);
                state = state.setTo(animState);
            }
            return state.to(animState2, animConfig);
        }
        AnimConfig animConfig2 = new AnimConfig();
        animConfig2.setEase(EaseManager.getStyle(-2, 1.0f, 0.35f));
        animConfig2.addListeners(new ViewHideTransitionListener(this.mContainerView));
        if (animState2 == null) {
            animState2 = new AnimState(str).add(ViewProperty.TRANSLATION_Y, (double) ((-height) - 100)).add(ViewProperty.ALPHA, 0.0d);
        }
        IStateStyle state2 = Folme.useAt(this.mContainerView).state();
        if (animState != null) {
            animState.setTag(str);
            state2 = state2.setTo(animState);
        }
        return state2.to(animState2, animConfig2);
    }

    private IStateStyle startSplitViewAnimation(boolean z, String str, AnimState animState) {
        int splitHeight = getSplitHeight();
        if (z) {
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(EaseManager.getStyle(-2, 0.9f, 0.25f));
            AnimState add = new AnimState(str).add(ViewProperty.TRANSLATION_Y, 0.0d).add(ViewProperty.ALPHA, 1.0d);
            IStateStyle state = Folme.useAt(this.mSplitView).state();
            if (animState != null) {
                animState.setTag(str);
                state = state.setTo(animState);
            }
            return state.to(add, animConfig);
        }
        AnimConfig animConfig2 = new AnimConfig();
        animConfig2.setEase(EaseManager.getStyle(-2, 1.0f, 0.35f));
        animConfig2.addListeners(new ViewHideTransitionListener(this.mSplitView));
        AnimState add2 = new AnimState(str).add(ViewProperty.TRANSLATION_Y, splitHeight + 100).add(ViewProperty.ALPHA, 0.0d);
        IStateStyle state2 = Folme.useAt(this.mSplitView).state();
        if (animState != null) {
            animState.setTag(str);
            state2 = state2.setTo(animState);
        }
        return state2.to(add2, animConfig2);
    }

    private void updateContentMaskVisibility(boolean z) {
        if (this.mSplitView.getChildCount() == 2 && (this.mSplitView.getChildAt(1) instanceof PhoneActionMenuView)) {
            PhoneActionMenuView phoneActionMenuView = (PhoneActionMenuView) this.mSplitView.getChildAt(1);
            this.mSplitMenuView = phoneActionMenuView;
            if (!phoneActionMenuView.isOverflowMenuShowing() || this.mContentMask == null) {
                return;
            }
            if (z) {
                this.mOverlayLayout.getContentMaskAnimator(this.mContentMaskOnClickListenr).show().start();
            } else {
                this.mOverlayLayout.getContentMaskAnimator(null).hide().start();
            }
        }
    }

    private void updateVisibility(boolean z) {
        updateVisibility(z, null);
    }

    private void updateVisibility(boolean z, AnimState animState) {
        if (checkShowingFlags(this.mHiddenByApp, this.mHiddenBySystem, this.mShowingForMode)) {
            if (this.mNowShowing) {
                return;
            }
            this.mNowShowing = true;
            doShow(z, animState);
        } else if (this.mNowShowing) {
            this.mNowShowing = false;
            doHide(z, animState);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public int addFragmentTab(String str, ActionBar.Tab tab, int i, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        return this.mViewPagerController.addFragmentTab(str, tab, i, cls, bundle, z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public int addFragmentTab(String str, ActionBar.Tab tab, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        return this.mViewPagerController.addFragmentTab(str, tab, cls, bundle, z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addOnFragmentViewPagerChangeListener(ActionBar.FragmentViewPagerChangeListener fragmentViewPagerChangeListener) {
        this.mViewPagerController.addOnFragmentViewPagerChangeListener(fragmentViewPagerChangeListener);
    }

    void animateToMode(boolean z) {
        if (z) {
            showForActionMode();
        } else {
            hideForActionMode();
        }
        this.mActionModeView.animateToVisibility(z);
        if (this.mTabScrollView == null || this.mActionView.isTightTitleWithEmbeddedTabs() || !this.mActionView.isCollapsed()) {
            return;
        }
        this.mTabScrollView.setEnabled(!z);
        this.mExpanedTabScrollView.setEnabled(!z);
        this.mCollapseTabScrollView.setEnabled(!z);
        this.mExpandTabScrollView.setEnabled(!z);
    }

    public ActionModeView createActionModeView(ActionMode.Callback callback) {
        if (!(callback instanceof SearchActionMode.Callback)) {
            ActionBarContextView actionBarContextView = this.mContextView;
            if (actionBarContextView != null) {
                return actionBarContextView;
            }
            throw new IllegalStateException("not set windowSplitActionBar true in activity style!");
        }
        if (this.mSearchActionModeView == null) {
            this.mSearchActionModeView = createSearchActionModeView();
        }
        Rect baseInnerInsets = this.mOverlayLayout.getBaseInnerInsets();
        if (baseInnerInsets != null) {
            this.mSearchActionModeView.setStatusBarPaddingTop(baseInnerInsets.top);
        }
        if (this.mOverlayLayout != this.mSearchActionModeView.getParent()) {
            this.mOverlayLayout.addView(this.mSearchActionModeView);
        }
        this.mSearchActionModeView.addAnimationListener(this.mActionView);
        return this.mSearchActionModeView;
    }

    public SearchActionModeView createSearchActionModeView() {
        SearchActionModeView searchActionModeView = (SearchActionModeView) LayoutInflater.from(getThemedContext()).inflate(R$layout.miuix_appcompat_search_action_mode_view, (ViewGroup) this.mOverlayLayout, false);
        searchActionModeView.setOnBackClickListener(new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ActionMode actionMode = ActionBarImpl.this.mActionMode;
                if (actionMode != null) {
                    actionMode.finish();
                }
            }
        });
        return searchActionModeView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionBarOverlayLayout getActionBarOverlayLayout() {
        return this.mOverlayLayout;
    }

    @Override // androidx.appcompat.app.ActionBar
    public View getCustomView() {
        return this.mActionView.getCustomNavigationView();
    }

    @Override // androidx.appcompat.app.ActionBar
    public int getDisplayOptions() {
        return this.mActionView.getDisplayOptions();
    }

    @Override // miuix.appcompat.app.ActionBar
    public View getEndView() {
        return this.mActionView.getEndView();
    }

    public int getExpandState() {
        return this.mActionView.getExpandState();
    }

    @Override // miuix.appcompat.app.ActionBar
    public int getFragmentTabCount() {
        return this.mViewPagerController.getFragmentTabCount();
    }

    public int getNavigationMode() {
        return this.mActionView.getNavigationMode();
    }

    public int getSelectedNavigationIndex() {
        TabImpl tabImpl;
        int navigationMode = this.mActionView.getNavigationMode();
        if (navigationMode != 1) {
            if (navigationMode == 2 && (tabImpl = this.mSelectedTab) != null) {
                return tabImpl.getPosition();
            }
            return -1;
        }
        return this.mActionView.getDropdownSelectedPosition();
    }

    public int getTabCount() {
        return this.mTabs.size();
    }

    @Override // androidx.appcompat.app.ActionBar
    public Context getThemedContext() {
        if (this.mThemedContext == null) {
            TypedValue typedValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(16843671, typedValue, true);
            int i = typedValue.resourceId;
            if (i != 0) {
                this.mThemedContext = new ContextThemeWrapper(this.mContext, i);
            } else {
                this.mThemedContext = this.mContext;
            }
        }
        return this.mThemedContext;
    }

    @Override // androidx.appcompat.app.ActionBar
    public void hide() {
        hide(null);
    }

    public void hide(AnimState animState) {
        if (this.mHiddenByApp) {
            return;
        }
        this.mHiddenByApp = true;
        updateVisibility(false, animState);
    }

    void hideForActionMode() {
        if (this.mShowingForMode) {
            this.mShowingForMode = false;
            this.mActionView.onActionModeEnd((getDisplayOptions() & MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON) != 0);
            updateVisibility(false);
            setResizable(true);
            ActionModeView actionModeView = this.mActionModeView;
            if (actionModeView instanceof SearchActionModeView) {
                setExpandState(this.mCurrentExpandState, true);
                setResizable(this.mCurrentResizable);
            } else {
                this.mCurrentExpandState = ((ActionBarContextView) actionModeView).getExpandState();
                this.mCurrentResizable = ((ActionBarContextView) this.mActionModeView).isResizable();
                setExpandState(this.mCurrentExpandState);
                setResizable(this.mCurrentResizable);
            }
            this.mActionView.setImportantForAccessibility(this.mCurrentAccessibilityImportant);
        }
    }

    protected void init(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) viewGroup;
        this.mOverlayLayout = actionBarOverlayLayout;
        actionBarOverlayLayout.setActionBar(this);
        this.mActionView = (ActionBarView) viewGroup.findViewById(R$id.action_bar);
        this.mContextView = (ActionBarContextView) viewGroup.findViewById(R$id.action_context_bar);
        this.mContainerView = (ActionBarContainer) viewGroup.findViewById(R$id.action_bar_container);
        this.mSplitView = (ActionBarContainer) viewGroup.findViewById(R$id.split_action_bar);
        View findViewById = viewGroup.findViewById(R$id.content_mask);
        this.mContentMask = findViewById;
        if (findViewById != null) {
            this.mContentMaskOnClickListenr = new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (ActionBarImpl.this.mSplitMenuView == null || !ActionBarImpl.this.mSplitMenuView.isOverflowMenuShowing()) {
                        return;
                    }
                    ActionBarImpl.this.mSplitMenuView.getPresenter().hideOverflowMenu(true);
                }
            };
        }
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null && this.mContextView == null && this.mContainerView == null) {
            throw new IllegalStateException(ActionBarImpl.class.getSimpleName() + " can only be used with a compatible window decor layout");
        }
        this.mContextDisplayMode = actionBarView.isSplitActionBar() ? 1 : 0;
        Object[] objArr = (this.mActionView.getDisplayOptions() & 4) != 0 ? 1 : null;
        if (objArr != null) {
            this.mDisplayHomeAsUpSet = true;
        }
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(this.mContext);
        setHomeButtonEnabled(actionBarPolicy.enableHomeButtonByDefault() || objArr != null);
        setHasEmbeddedTabs(actionBarPolicy.hasEmbeddedTabs());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void internalAddTab(ActionBar.Tab tab) {
        internalAddTab(tab, getTabCount() == 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void internalAddTab(ActionBar.Tab tab, int i) {
        internalAddTab(tab, i, i == getTabCount());
    }

    void internalAddTab(ActionBar.Tab tab, int i, boolean z) {
        ensureTabsExist();
        this.mTabScrollView.addTab(tab, i, z);
        this.mExpanedTabScrollView.addTab(tab, i, z);
        this.mCollapseTabScrollView.addTab(tab, i, z);
        this.mExpandTabScrollView.addTab(tab, i, z);
        configureTab(tab, i);
        if (z) {
            selectTab(tab);
        }
    }

    void internalAddTab(ActionBar.Tab tab, boolean z) {
        ensureTabsExist();
        this.mTabScrollView.addTab(tab, z);
        this.mExpanedTabScrollView.addTab(tab, z);
        this.mCollapseTabScrollView.addTab(tab, z);
        this.mExpandTabScrollView.addTab(tab, z);
        configureTab(tab, this.mTabs.size());
        if (z) {
            selectTab(tab);
        }
    }

    void internalRemoveAllTabs() {
        cleanupTabs();
    }

    public boolean isFragmentViewPagerMode() {
        return this.mViewPagerController != null;
    }

    public boolean isResizable() {
        return this.mActionView.isResizable();
    }

    boolean isShowHideAnimationEnabled() {
        return this.mShowHideAnimationEnabled;
    }

    @Override // androidx.appcompat.app.ActionBar
    public boolean isShowing() {
        return this.mNowShowing;
    }

    @Override // androidx.appcompat.app.ActionBar
    public ActionBar.Tab newTab() {
        return new TabImpl();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void onConfigurationChanged(Configuration configuration) {
        setHasEmbeddedTabs(ActionBarPolicy.get(this.mContext).hasEmbeddedTabs());
    }

    public void onFloatingModeChanged(boolean z) {
        this.mContainerView.setIsMiuixFloating(z);
        SearchActionModeView searchActionModeView = this.mSearchActionModeView;
        if (searchActionModeView != null) {
            searchActionModeView.onFloatingModeChanged(z);
        }
    }

    public void removeAllTabs() {
        if (isFragmentViewPagerMode()) {
            throw new IllegalStateException("Cannot add tab directly in fragment view pager mode!\n Please using addFragmentTab().");
        }
        internalRemoveAllTabs();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void selectTab(ActionBar.Tab tab) {
        selectTab(tab, true);
    }

    public void selectTab(ActionBar.Tab tab, boolean z) {
        if (getNavigationMode() != 2) {
            this.mSavedTabPosition = tab != null ? tab.getPosition() : -1;
            return;
        }
        FragmentTransaction disallowAddToBackStack = this.mFragmentManager.beginTransaction().disallowAddToBackStack();
        TabImpl tabImpl = this.mSelectedTab;
        if (tabImpl != tab) {
            this.mTabScrollView.setTabSelected(tab != null ? tab.getPosition() : -1, z);
            this.mExpanedTabScrollView.setTabSelected(tab != null ? tab.getPosition() : -1, z);
            this.mCollapseTabScrollView.setTabSelected(tab != null ? tab.getPosition() : -1, z);
            this.mExpandTabScrollView.setTabSelected(tab != null ? tab.getPosition() : -1, z);
            TabImpl tabImpl2 = this.mSelectedTab;
            if (tabImpl2 != null) {
                tabImpl2.getCallback().onTabUnselected(this.mSelectedTab, disallowAddToBackStack);
            }
            TabImpl tabImpl3 = (TabImpl) tab;
            this.mSelectedTab = tabImpl3;
            if (tabImpl3 != null) {
                tabImpl3.mWithAnim = z;
                tabImpl3.getCallback().onTabSelected(this.mSelectedTab, disallowAddToBackStack);
            }
        } else if (tabImpl != null) {
            tabImpl.getCallback().onTabReselected(this.mSelectedTab, disallowAddToBackStack);
            this.mTabScrollView.animateToTab(tab.getPosition());
            this.mExpanedTabScrollView.animateToTab(tab.getPosition());
            this.mCollapseTabScrollView.animateToTab(tab.getPosition());
            this.mExpandTabScrollView.animateToTab(tab.getPosition());
        }
        if (disallowAddToBackStack.isEmpty()) {
            return;
        }
        disallowAddToBackStack.commit();
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setActionBarTransitionListener(ActionBarTransitionListener actionBarTransitionListener) {
        this.mActionView.setActionBarTransitionListener(actionBarTransitionListener);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setBackgroundDrawable(Drawable drawable) {
        boolean z = (getDisplayOptions() & MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON) != 0;
        ActionBarContainer actionBarContainer = this.mContainerView;
        if (z) {
            drawable = null;
        }
        actionBarContainer.setPrimaryBackground(drawable);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setCustomView(int i) {
        setCustomView(LayoutInflater.from(getThemedContext()).inflate(i, (ViewGroup) this.mActionView, false));
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setCustomView(View view) {
        this.mActionView.setCustomNavigationView(view);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setCustomView(View view, ActionBar.LayoutParams layoutParams) {
        view.setLayoutParams(layoutParams);
        this.mActionView.setCustomNavigationView(view);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayHomeAsUpEnabled(boolean z) {
        setDisplayOptions(z ? getBlurOptioons() | 4 : 0, getBlurOptioons() | 4);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayOptions(int i) {
        ActionBarContainer actionBarContainer;
        if ((i & 4) != 0) {
            this.mDisplayHomeAsUpSet = true;
        }
        this.mActionView.setDisplayOptions(i);
        int displayOptions = this.mActionView.getDisplayOptions();
        ActionBarContainer actionBarContainer2 = this.mContainerView;
        if (actionBarContainer2 != null) {
            actionBarContainer2.setBlurBackground((displayOptions & MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON) != 0);
        }
        if ((i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION) != 0 && (actionBarContainer = this.mSplitView) != null) {
            actionBarContainer.setBlurBackground(true);
            return;
        }
        ActionBarContainer actionBarContainer3 = this.mSplitView;
        if (actionBarContainer3 != null) {
            actionBarContainer3.setBlurBackground(false);
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayOptions(int i, int i2) {
        ActionBarContainer actionBarContainer;
        int displayOptions = this.mActionView.getDisplayOptions();
        if ((i2 & 4) != 0) {
            this.mDisplayHomeAsUpSet = true;
        }
        this.mActionView.setDisplayOptions(((~i2) & displayOptions) | (i & i2));
        int displayOptions2 = this.mActionView.getDisplayOptions();
        ActionBarContainer actionBarContainer2 = this.mContainerView;
        if (actionBarContainer2 != null) {
            actionBarContainer2.setBlurBackground((displayOptions2 & MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON) != 0);
        }
        if ((i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION) != 0 && (actionBarContainer = this.mSplitView) != null) {
            actionBarContainer.setBlurBackground(true);
            return;
        }
        ActionBarContainer actionBarContainer3 = this.mSplitView;
        if (actionBarContainer3 != null) {
            actionBarContainer3.setBlurBackground(false);
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayShowCustomEnabled(boolean z) {
        setDisplayOptions(z ? getBlurOptioons() | 16 : 0, getBlurOptioons() | 16);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayShowTitleEnabled(boolean z) {
        setDisplayOptions(z ? getBlurOptioons() | 8 : 0, getBlurOptioons() | 8);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setEndView(View view) {
        this.mActionView.setEndView(view);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setExpandState(int i) {
        this.mActionView.setExpandState(i);
    }

    public void setExpandState(int i, boolean z) {
        this.mActionView.setExpandState(i, z, false);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setFragmentViewPagerMode(FragmentActivity fragmentActivity, boolean z) {
        if (isFragmentViewPagerMode()) {
            return;
        }
        removeAllTabs();
        setNavigationMode(2);
        this.mViewPagerController = new ActionBarViewPagerController(this, this.mFragmentManager, fragmentActivity.getLifecycle(), z);
        addOnFragmentViewPagerChangeListener(this.mTabScrollView);
        addOnFragmentViewPagerChangeListener(this.mExpanedTabScrollView);
        addOnFragmentViewPagerChangeListener(this.mCollapseTabScrollView);
        addOnFragmentViewPagerChangeListener(this.mExpandTabScrollView);
        ActionBarContainer actionBarContainer = this.mSplitView;
        if (actionBarContainer != null) {
            addOnFragmentViewPagerChangeListener(actionBarContainer);
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setHomeButtonEnabled(boolean z) {
        this.mActionView.setHomeButtonEnabled(z);
    }

    public void setNavigationMode(int i) {
        if (this.mActionView.getNavigationMode() == 2) {
            this.mSavedTabPosition = getSelectedNavigationIndex();
            selectTab(null);
            this.mTabScrollView.setVisibility(8);
            this.mExpanedTabScrollView.setVisibility(8);
            this.mCollapseTabScrollView.setVisibility(8);
            this.mExpandTabScrollView.setVisibility(8);
        }
        this.mActionView.setNavigationMode(i);
        if (i == 2) {
            ensureTabsExist();
            this.mTabScrollView.setVisibility(0);
            this.mExpanedTabScrollView.setVisibility(0);
            this.mCollapseTabScrollView.setVisibility(0);
            this.mExpandTabScrollView.setVisibility(0);
            int i2 = this.mSavedTabPosition;
            if (i2 != -1) {
                setSelectedNavigationItem(i2);
                this.mSavedTabPosition = -1;
            }
        }
        this.mActionView.setCollapsable(false);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setResizable(boolean z) {
        this.mActionView.setResizable(z);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setSelectedNavigationItem(int i) {
        int navigationMode = this.mActionView.getNavigationMode();
        if (navigationMode == 1) {
            this.mActionView.setDropdownSelectedPosition(i);
        } else if (navigationMode != 2) {
            throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode");
        } else {
            selectTab(this.mTabs.get(i));
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setShowHideAnimationEnabled(boolean z) {
        this.mShowHideAnimationEnabled = z;
        if (z) {
            return;
        }
        if (isShowing()) {
            doShow(false);
        } else {
            doHide(false);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setStartView(View view) {
        this.mActionView.setStartView(view);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setSubtitle(CharSequence charSequence) {
        this.mActionView.setSubtitle(charSequence);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setTitle(int i) {
        setTitle(this.mContext.getString(i));
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setTitle(CharSequence charSequence) {
        this.mActionView.setTitle(charSequence);
    }

    void showForActionMode() {
        if (this.mShowingForMode) {
            return;
        }
        this.mShowingForMode = true;
        updateVisibility(false);
        this.mCurrentExpandState = getExpandState();
        this.mCurrentResizable = isResizable();
        ActionModeView actionModeView = this.mActionModeView;
        if (actionModeView instanceof SearchActionModeView) {
            setExpandState(0, true);
            setResizable(false);
        } else {
            ((ActionBarContextView) actionModeView).setExpandState(this.mCurrentExpandState);
            ((ActionBarContextView) this.mActionModeView).setResizable(this.mCurrentResizable);
        }
        this.mCurrentAccessibilityImportant = this.mActionView.getImportantForAccessibility();
        this.mActionView.setImportantForAccessibility(4);
        this.mActionView.onActionModeStart(this.mActionModeView instanceof SearchActionModeView, (getDisplayOptions() & MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON) != 0);
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
        ActionMode createActionMode = createActionMode(callback);
        ActionModeView actionModeView = this.mActionModeView;
        if (((actionModeView instanceof SearchActionModeView) && (createActionMode instanceof SearchActionModeImpl)) || ((actionModeView instanceof ActionBarContextView) && (createActionMode instanceof EditActionModeImpl))) {
            actionModeView.closeMode();
            this.mActionModeView.killMode();
        }
        ActionModeView createActionModeView = createActionModeView(callback);
        this.mActionModeView = createActionModeView;
        if (createActionModeView != null) {
            if (createActionMode instanceof ActionModeImpl) {
                ActionModeImpl actionModeImpl = (ActionModeImpl) createActionMode;
                actionModeImpl.setActionModeView(createActionModeView);
                actionModeImpl.setActionModeCallback(this.mActionModeCallback);
                if (actionModeImpl.dispatchOnCreate()) {
                    createActionMode.invalidate();
                    this.mActionModeView.initForMode(createActionMode);
                    animateToMode(true);
                    ActionBarContainer actionBarContainer = this.mSplitView;
                    if (actionBarContainer != null && this.mContextDisplayMode == 1 && actionBarContainer.getVisibility() != 0) {
                        this.mSplitView.setVisibility(0);
                    }
                    ActionModeView actionModeView2 = this.mActionModeView;
                    if (actionModeView2 instanceof ActionBarContextView) {
                        ((ActionBarContextView) actionModeView2).sendAccessibilityEvent(32);
                    }
                    this.mActionMode = createActionMode;
                    return createActionMode;
                }
                return null;
            }
            return null;
        }
        throw new IllegalStateException("not set windowSplitActionBar true in activity style!");
    }
}
