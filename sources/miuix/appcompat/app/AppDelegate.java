package miuix.appcompat.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.appcompat.view.WindowCallbackWrapper;
import miui.content.res.ThemeResources;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$bool;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;
import miuix.appcompat.R$styleable;
import miuix.appcompat.app.floatingactivity.FloatingActivitySwitcher;
import miuix.appcompat.app.floatingactivity.OnFloatingCallback;
import miuix.appcompat.app.floatingactivity.OnFloatingModeCallback;
import miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper;
import miuix.appcompat.app.floatingactivity.helper.FloatingHelperFactory;
import miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingActivitySwitcher;
import miuix.appcompat.internal.app.widget.ActionBarContainer;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.app.widget.ActionBarView;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.core.util.variable.WindowWrapper;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.DeviceHelper;
import miuix.view.SearchActionMode;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class AppDelegate extends ActionBarDelegateImpl {
    private ActionBarContainer mActionBarContainer;
    private ActivityCallback mActivityCallback;
    private final String mActivityIdentity;
    private AppCompatWindowCallback mAppCompatWindowCallback;
    private ViewGroup mContentParent;
    private OnFloatingModeCallback mFloatingModeCallback;
    private ViewGroup mFloatingRoot;
    private BaseFloatingActivityHelper mFloatingWindowHelper;
    private final Runnable mInvalidateMenuRunnable;
    private boolean mIsFloatingTheme;
    private boolean mIsFloatingWindow;
    private LayoutInflater mLayoutInflater;
    private ActionBarOverlayLayout mSubDecor;
    Window mWindow;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class AppCompatWindowCallback extends WindowCallbackWrapper {
        public AppCompatWindowCallback(Window.Callback callback) {
            super(callback);
        }

        @Override // android.view.Window.Callback
        public void onContentChanged() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppDelegate(AppCompatActivity appCompatActivity, ActivityCallback activityCallback, OnFloatingModeCallback onFloatingModeCallback) {
        super(appCompatActivity);
        this.mIsFloatingTheme = false;
        this.mIsFloatingWindow = false;
        this.mFloatingRoot = null;
        this.mInvalidateMenuRunnable = new Runnable() { // from class: miuix.appcompat.app.AppDelegate.1
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r0v1, types: [android.view.Menu, miuix.appcompat.internal.view.menu.MenuBuilder] */
            @Override // java.lang.Runnable
            public void run() {
                ?? createMenu = AppDelegate.this.createMenu();
                if (!AppDelegate.this.isImmersionMenuEnabled() && AppDelegate.this.mActivityCallback.onCreatePanelMenu(0, createMenu) && AppDelegate.this.mActivityCallback.onPreparePanel(0, null, createMenu)) {
                    AppDelegate.this.setMenu(createMenu);
                } else {
                    AppDelegate.this.setMenu(null);
                }
            }
        };
        this.mActivityIdentity = String.valueOf(SystemClock.elapsedRealtimeNanos());
        this.mActivityCallback = activityCallback;
        this.mFloatingModeCallback = onFloatingModeCallback;
    }

    private void attachToWindow(Window window) {
        if (this.mWindow != null) {
            throw new IllegalStateException("AppCompat has already installed itself into the Window");
        }
        Window.Callback callback = window.getCallback();
        if (callback instanceof AppCompatWindowCallback) {
            throw new IllegalStateException("AppCompat has already installed itself into the Window");
        }
        AppCompatWindowCallback appCompatWindowCallback = new AppCompatWindowCallback(callback);
        this.mAppCompatWindowCallback = appCompatWindowCallback;
        window.setCallback(appCompatWindowCallback);
        this.mWindow = window;
    }

    private void ensureWindow() {
        AppCompatActivity appCompatActivity;
        Window window = this.mWindow;
        if (window != null) {
            return;
        }
        if (window == null && (appCompatActivity = this.mActivity) != null) {
            attachToWindow(appCompatActivity.getWindow());
        }
        if (this.mWindow == null) {
            throw new IllegalStateException("We have not been given a Window");
        }
    }

    private int getDecorViewLayoutRes(Window window) {
        Context context = window.getContext();
        int i = AttributeResolver.resolveBoolean(context, R$attr.windowActionBar, false) ? AttributeResolver.resolveBoolean(context, R$attr.windowActionBarMovable, false) ? R$layout.miuix_appcompat_screen_action_bar_movable : R$layout.miuix_appcompat_screen_action_bar : R$layout.miuix_appcompat_screen_simple;
        int resolve = AttributeResolver.resolve(context, R$attr.startingWindowOverlay);
        if (resolve > 0 && isSystemProcess() && isWindowActionBarEnabled(context)) {
            i = resolve;
        }
        if (!window.isFloating() && (window.getCallback() instanceof Dialog)) {
            WindowWrapper.setTranslucentStatus(window, AttributeResolver.resolveInt(context, R$attr.windowTranslucentStatus, 0));
        }
        return i;
    }

    private void installSubDecor() {
        ActionBarOverlayLayout actionBarOverlayLayout;
        if (this.mSubDecorInstalled) {
            return;
        }
        ensureWindow();
        this.mSubDecorInstalled = true;
        Window window = this.mActivity.getWindow();
        this.mLayoutInflater = window.getLayoutInflater();
        TypedArray obtainStyledAttributes = this.mActivity.obtainStyledAttributes(R$styleable.Window);
        if (obtainStyledAttributes.getInt(R$styleable.Window_windowLayoutMode, 0) == 1) {
            this.mActivity.getWindow().setGravity(80);
        }
        int i = R$styleable.Window_windowActionBar;
        if (!obtainStyledAttributes.hasValue(i)) {
            obtainStyledAttributes.recycle();
            throw new IllegalStateException("You need to use a miui theme (or descendant) with this activity.");
        }
        if (obtainStyledAttributes.getBoolean(i, false)) {
            requestWindowFeature(8);
        }
        if (obtainStyledAttributes.getBoolean(R$styleable.Window_windowActionBarOverlay, false)) {
            requestWindowFeature(9);
        }
        this.mIsFloatingTheme = obtainStyledAttributes.getBoolean(R$styleable.Window_isMiuixFloatingTheme, false);
        this.mIsFloatingWindow = obtainStyledAttributes.getBoolean(R$styleable.Window_windowFloating, false);
        setTranslucentStatus(obtainStyledAttributes.getInt(R$styleable.Window_windowTranslucentStatus, 0));
        installToDecor(window);
        ActionBarOverlayLayout actionBarOverlayLayout2 = this.mSubDecor;
        if (actionBarOverlayLayout2 != null) {
            actionBarOverlayLayout2.setCallback(this.mActivity);
            this.mSubDecor.setTranslucentStatus(getTranslucentStatus());
        }
        if (this.mHasActionBar && (actionBarOverlayLayout = this.mSubDecor) != null) {
            this.mActionBarContainer = (ActionBarContainer) actionBarOverlayLayout.findViewById(R$id.action_bar_container);
            this.mSubDecor.setOverlayMode(this.mOverlayActionBar);
            ActionBarView actionBarView = (ActionBarView) this.mSubDecor.findViewById(R$id.action_bar);
            this.mActionBarView = actionBarView;
            actionBarView.setWindowCallback(this.mActivity);
            if (this.mFeatureIndeterminateProgress) {
                this.mActionBarView.initIndeterminateProgress();
            }
            this.mImmersionLayoutResourceId = obtainStyledAttributes.getResourceId(R$styleable.Window_immersionMenuLayout, 0);
            if (isImmersionMenuEnabled()) {
                this.mActionBarView.initImmersionMore(this.mImmersionLayoutResourceId, this);
            }
            if (this.mActionBarView.getCustomNavigationView() != null) {
                ActionBarView actionBarView2 = this.mActionBarView;
                actionBarView2.setDisplayOptions(actionBarView2.getDisplayOptions() | 16);
            }
            boolean equals = "splitActionBarWhenNarrow".equals(getUiOptionsFromMetadata());
            boolean z = equals ? this.mActivity.getResources().getBoolean(R$bool.abc_split_action_bar_is_narrow) : obtainStyledAttributes.getBoolean(R$styleable.Window_windowSplitActionBar, false);
            if (z) {
                addSplitActionBar(z, equals, this.mSubDecor);
            }
            this.mActivity.getWindow().getDecorView().post(this.mInvalidateMenuRunnable);
        }
        if (obtainStyledAttributes.getBoolean(R$styleable.Window_immersionMenuEnabled, false)) {
            setImmersionMenuEnabled(true);
        }
        obtainStyledAttributes.recycle();
    }

    private void installToDecor(Window window) {
        this.mFloatingWindowHelper = this.mIsFloatingTheme ? FloatingHelperFactory.get(this.mActivity) : null;
        this.mFloatingRoot = null;
        View inflate = View.inflate(this.mActivity, getDecorViewLayoutRes(window), null);
        ViewGroup viewGroup = inflate;
        if (this.mFloatingWindowHelper != null) {
            boolean shouldShowFloatingActivityTabletMode = shouldShowFloatingActivityTabletMode();
            this.mIsFloatingWindow = shouldShowFloatingActivityTabletMode;
            this.mFloatingWindowHelper.setFloatingWindowMode(shouldShowFloatingActivityTabletMode);
            ViewGroup replaceSubDecor = this.mFloatingWindowHelper.replaceSubDecor(inflate, this.mIsFloatingWindow);
            this.mFloatingRoot = replaceSubDecor;
            updateSystemUiStateInFloatingTheme(this.mIsFloatingWindow);
            viewGroup = replaceSubDecor;
        }
        View findViewById = viewGroup.findViewById(R$id.action_bar_overlay_layout);
        if (findViewById instanceof ActionBarOverlayLayout) {
            ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) findViewById;
            this.mSubDecor = actionBarOverlayLayout;
            ViewGroup viewGroup2 = (ViewGroup) actionBarOverlayLayout.findViewById(16908290);
            ViewGroup viewGroup3 = (ViewGroup) window.findViewById(16908290);
            if (viewGroup3 != null) {
                while (viewGroup3.getChildCount() > 0) {
                    View childAt = viewGroup3.getChildAt(0);
                    viewGroup3.removeViewAt(0);
                    viewGroup2.addView(childAt);
                }
                viewGroup3.setId(-1);
                viewGroup2.setId(16908290);
                if (viewGroup3 instanceof FrameLayout) {
                    ((FrameLayout) viewGroup3).setForeground(null);
                }
            }
        }
        window.setContentView(viewGroup);
        ActionBarOverlayLayout actionBarOverlayLayout2 = this.mSubDecor;
        if (actionBarOverlayLayout2 != null) {
            this.mContentParent = (ViewGroup) actionBarOverlayLayout2.findViewById(16908290);
        }
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.init(this.mFloatingRoot, shouldShowFloatingActivityTabletMode());
        }
    }

    private boolean isSystemProcess() {
        return ThemeResources.FRAMEWORK_PACKAGE.equals(getActivity().getApplicationContext().getApplicationInfo().packageName);
    }

    private static boolean isWindowActionBarEnabled(Context context) {
        return AttributeResolver.resolveBoolean(context, R$attr.windowActionBar, true);
    }

    private void notifyFloatWindowModeChanged(boolean z) {
        this.mFloatingModeCallback.onFloatingWindowModeChanged(z);
    }

    private void setFloatingWindowMode(boolean z, boolean z2, boolean z3) {
        if (this.mIsFloatingTheme) {
            if ((z3 || DeviceHelper.isTablet(this.mActivity)) && this.mIsFloatingWindow != z && this.mFloatingModeCallback.onFloatingWindowModeChanging(z)) {
                this.mIsFloatingWindow = z;
                this.mFloatingWindowHelper.setFloatingWindowMode(z);
                updateSystemUiStateInFloatingTheme(this.mIsFloatingWindow);
                if (this.mSubDecor != null) {
                    ViewGroup.LayoutParams floatingLayoutParam = this.mFloatingWindowHelper.getFloatingLayoutParam();
                    if (z) {
                        floatingLayoutParam.height = -2;
                        floatingLayoutParam.width = -2;
                    } else {
                        floatingLayoutParam.height = -1;
                        floatingLayoutParam.width = -1;
                    }
                    this.mSubDecor.setLayoutParams(floatingLayoutParam);
                    this.mSubDecor.requestLayout();
                }
                ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
                if (actionBarOverlayLayout != null) {
                    actionBarOverlayLayout.onFloatingModeChanged(z);
                }
                if (z2) {
                    notifyFloatWindowModeChanged(z);
                }
            }
        }
    }

    private boolean shouldShowFloatingActivityTabletMode() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        return baseFloatingActivityHelper != null && baseFloatingActivityHelper.isFloatingModeSupport();
    }

    private void updateSystemUiStateInFloatingTheme(boolean z) {
        Window window = this.mActivity.getWindow();
        window.setFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP, MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        if (Build.VERSION.SDK_INT < 30) {
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(!z ? systemUiVisibility | MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE : systemUiVisibility & (-1025));
        } else if (z) {
            window.setDecorFitsSystemWindows(false);
        } else {
            window.setDecorFitsSystemWindows(true);
        }
    }

    public void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        if (!this.mSubDecorInstalled) {
            installSubDecor();
        }
        ViewGroup viewGroup = this.mContentParent;
        if (viewGroup != null) {
            viewGroup.addView(view, layoutParams);
        }
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public ActionBar createActionBar() {
        if (!this.mSubDecorInstalled) {
            installSubDecor();
        }
        if (this.mSubDecor == null) {
            return null;
        }
        return new ActionBarImpl(this.mActivity, this.mSubDecor);
    }

    public void executeCloseEnterAnimation() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.executeCloseEnterAnimation();
        }
    }

    public void executeOpenEnterAnimation() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.executeOpenEnterAnimation();
        }
    }

    public void executeOpenExitAnimation() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.executeOpenExitAnimation();
        }
    }

    public String getActivityIdentity() {
        return this.mActivityIdentity;
    }

    public int getExtraHorizontalPaddingLevel() {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            return actionBarOverlayLayout.getExtraHorizontalPaddingLevel();
        }
        return 0;
    }

    public View getFloatingBrightPanel() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            return baseFloatingActivityHelper.getFloatingBrightPanel();
        }
        return null;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public Context getThemedContext() {
        return this.mActivity;
    }

    public void hideFloatingBrightPanel() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.hideFloatingBrightPanel();
        }
    }

    public void hideFloatingDimBackground() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.hideFloatingDimBackground();
        }
    }

    public void installFloatingSwitcher(boolean z, Bundle bundle) {
        if (z) {
            Intent intent = this.mActivity.getIntent();
            if (intent == null || !MultiAppFloatingActivitySwitcher.isFromMultiApp(intent)) {
                FloatingActivitySwitcher.install(this.mActivity, bundle);
            } else {
                MultiAppFloatingActivitySwitcher.install(this.mActivity, intent, bundle);
            }
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void invalidateOptionsMenu() {
        this.mInvalidateMenuRunnable.run();
    }

    public boolean isInFloatingWindowMode() {
        return shouldShowFloatingActivityTabletMode();
    }

    public void onActionModeFinished(ActionMode actionMode) {
        this.mActionMode = null;
    }

    public void onActionModeStarted(ActionMode actionMode) {
        this.mActionMode = actionMode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBackPressed() {
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
            return;
        }
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null && actionBarView.hasExpandedActionView()) {
            this.mActionBarView.collapseActionView();
            return;
        }
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper == null || !baseFloatingActivityHelper.onBackPressed()) {
            this.mActivityCallback.onBackPressed();
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setFloatingWindowMode(isInFloatingWindowMode(), true, DeviceHelper.isFoldDevice());
        this.mActivityCallback.onConfigurationChanged(configuration);
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onCreate(Bundle bundle) {
        ApplicationInfo applicationInfo;
        Bundle bundle2;
        Bundle bundle3;
        this.mActivityCallback.onCreate(bundle);
        installSubDecor();
        installFloatingSwitcher(this.mIsFloatingTheme, bundle);
        ActivityInfo activityInfo = null;
        try {
            applicationInfo = this.mActivity.getPackageManager().getApplicationInfo(this.mActivity.getPackageName(), 128);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }
        int i = (applicationInfo == null || (bundle3 = applicationInfo.metaData) == null) ? 0 : bundle3.getInt("miui.extra.window.padding.level", 0);
        try {
            activityInfo = this.mActivity.getPackageManager().getActivityInfo(this.mActivity.getComponentName(), 128);
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
        }
        if (activityInfo != null && (bundle2 = activityInfo.metaData) != null) {
            i = bundle2.getInt("miui.extra.window.padding.level", i);
        }
        int resolveInt = AttributeResolver.resolveInt(this.mActivity, R$attr.windowExtraPaddingHorizontal, i);
        boolean resolveBoolean = AttributeResolver.resolveBoolean(this.mActivity, R$attr.windowExtraPaddingHorizontalEnable, resolveInt != 0);
        setExtraHorizontalPaddingLevel(resolveInt);
        setExtraHorizontalPaddingEnable(resolveBoolean);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    protected boolean onCreateImmersionMenu(MenuBuilder menuBuilder) {
        return this.mActivity.onCreateOptionsMenu(menuBuilder);
    }

    public boolean onCreatePanelMenu(int i, Menu menu) {
        return i != 0 && this.mActivityCallback.onCreatePanelMenu(i, menu);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1, types: [miuix.appcompat.app.ActivityCallback] */
    /* JADX WARN: Type inference failed for: r1v2, types: [miuix.appcompat.app.ActivityCallback] */
    /* JADX WARN: Type inference failed for: r4v0, types: [miuix.appcompat.app.AppDelegate, miuix.appcompat.app.ActionBarDelegateImpl] */
    /* JADX WARN: Type inference failed for: r5v2, types: [miuix.appcompat.internal.view.menu.MenuBuilder] */
    /* JADX WARN: Type inference failed for: r5v3, types: [miuix.appcompat.internal.view.menu.MenuBuilder] */
    /* JADX WARN: Type inference failed for: r5v4, types: [android.view.Menu, miuix.appcompat.internal.view.menu.MenuBuilder] */
    /* JADX WARN: Type inference failed for: r5v5, types: [android.view.Menu, miuix.appcompat.internal.view.menu.MenuBuilder] */
    /* JADX WARN: Type inference failed for: r5v6 */
    /* JADX WARN: Type inference failed for: r5v7 */
    public View onCreatePanelView(int i) {
        if (i != 0) {
            return this.mActivityCallback.onCreatePanelView(i);
        }
        if (!isImmersionMenuEnabled()) {
            ?? r5 = this.mMenu;
            boolean z = true;
            r5 = r5;
            if (this.mActionMode == null) {
                if (r5 == 0) {
                    ?? createMenu = createMenu();
                    setMenu(createMenu);
                    createMenu.stopDispatchingItemsChanged();
                    z = this.mActivityCallback.onCreatePanelMenu(0, createMenu);
                    r5 = createMenu;
                }
                if (z) {
                    r5.stopDispatchingItemsChanged();
                    z = this.mActivityCallback.onPreparePanel(0, null, r5);
                }
            } else if (r5 == 0) {
                z = false;
            }
            if (z) {
                r5.startDispatchingItemsChanged();
            } else {
                setMenu(null);
            }
        }
        return null;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        if (this.mActivityCallback.onMenuItemSelected(i, menuItem)) {
            return true;
        }
        if (i == 0 && menuItem.getItemId() == 16908332 && getActionBar() != null && (getActionBar().getDisplayOptions() & 4) != 0) {
            if (!(this.mActivity.getParent() == null ? this.mActivity.onNavigateUp() : this.mActivity.getParent().onNavigateUpFromChild(this.mActivity))) {
                this.mActivity.finish();
            }
        }
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder.Callback
    public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
        return this.mActivity.onMenuItemSelected(0, menuItem);
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onPostResume() {
        this.mActivityCallback.onPostResume();
        ActionBarImpl actionBarImpl = (ActionBarImpl) getActionBar();
        if (actionBarImpl != null) {
            actionBarImpl.setShowHideAnimationEnabled(true);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    protected boolean onPrepareImmersionMenu(MenuBuilder menuBuilder) {
        return this.mActivity.onPrepareOptionsMenu(menuBuilder);
    }

    public boolean onPreparePanel(int i, View view, Menu menu) {
        return i != 0 && this.mActivityCallback.onPreparePanel(i, view, menu);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        SparseArray sparseParcelableArray;
        this.mActivityCallback.onRestoreInstanceState(bundle);
        if (this.mActionBarContainer == null || (sparseParcelableArray = bundle.getSparseParcelableArray("miuix:ActionBar")) == null) {
            return;
        }
        this.mActionBarContainer.restoreHierarchyState(sparseParcelableArray);
    }

    public void onSaveInstanceState(Bundle bundle) {
        this.mActivityCallback.onSaveInstanceState(bundle);
        if (bundle != null && this.mFloatingWindowHelper != null) {
            FloatingActivitySwitcher.onSaveInstanceState(this.mActivity, bundle);
            MultiAppFloatingActivitySwitcher.onSaveInstanceState(this.mActivity.getTaskId(), this.mActivity.getActivityIdentity(), bundle);
        }
        if (this.mActionBarContainer != null) {
            SparseArray<? extends Parcelable> sparseArray = new SparseArray<>();
            this.mActionBarContainer.saveHierarchyState(sparseArray);
            bundle.putSparseParcelableArray("miuix:ActionBar", sparseArray);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onStop() {
        this.mActivityCallback.onStop();
        dismissImmersionMenu(false);
        ActionBarImpl actionBarImpl = (ActionBarImpl) getActionBar();
        if (actionBarImpl != null) {
            actionBarImpl.setShowHideAnimationEnabled(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTitleChanged(CharSequence charSequence) {
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.setWindowTitle(charSequence);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return getActionBar() != null ? ((ActionBarImpl) getActionBar()).startActionMode(callback) : super.onWindowStartingActionMode(callback);
    }

    public void setContentView(int i) {
        if (!this.mSubDecorInstalled) {
            installSubDecor();
        }
        ViewGroup viewGroup = this.mContentParent;
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            this.mLayoutInflater.inflate(i, this.mContentParent);
        }
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }

    public void setContentView(View view) {
        setContentView(view, new ViewGroup.LayoutParams(-1, -1));
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        if (!this.mSubDecorInstalled) {
            installSubDecor();
        }
        ViewGroup viewGroup = this.mContentParent;
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            this.mContentParent.addView(view, layoutParams);
        }
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }

    public void setEnableSwipToDismiss(boolean z) {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.setEnableSwipToDismiss(z);
        }
    }

    public void setExtraHorizontalPaddingEnable(boolean z) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setExtraHorizontalPaddingEnable(z);
        }
    }

    public void setExtraHorizontalPaddingLevel(int i) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setExtraHorizontalPaddingLevel(i);
        }
    }

    public void setOnFloatingCallback(OnFloatingCallback onFloatingCallback) {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.setOnFloatingCallback(onFloatingCallback);
        }
    }

    public boolean shouldDelegateActivityFinish() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            return baseFloatingActivityHelper.delegateFinishFloatingActivityInternal();
        }
        return false;
    }

    public void showFloatingBrightPanel() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.showFloatingBrightPanel();
        }
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (callback instanceof SearchActionMode.Callback) {
            addContentMask(this.mSubDecor);
        }
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            return actionBarOverlayLayout.startActionMode(callback);
        }
        return null;
    }
}
