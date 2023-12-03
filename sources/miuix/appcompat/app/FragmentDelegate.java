package miuix.appcompat.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.fragment.app.FragmentActivity;
import java.lang.ref.WeakReference;
import miuix.appcompat.R$bool;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;
import miuix.appcompat.R$styleable;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.app.widget.ActionBarView;
import miuix.appcompat.internal.util.LayoutUIUtils;
import miuix.appcompat.internal.view.SimpleWindowCallback;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.internal.util.AttributeResolver;
import miuix.view.SearchActionMode;

/* loaded from: classes5.dex */
public class FragmentDelegate extends ActionBarDelegateImpl {
    private int mExtraPaddingLevel;
    private int mExtraThemeRes;
    private androidx.fragment.app.Fragment mFragment;
    private byte mInvalidateMenuFlags;
    private Runnable mInvalidateMenuRunnable;
    private MenuBuilder mMenu;
    private View mSubDecor;
    private Context mThemedContext;
    private final Window.Callback mWindowCallback;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class InvalidateMenuRunnable implements Runnable {
        private WeakReference<FragmentDelegate> mRefs;

        InvalidateMenuRunnable(FragmentDelegate fragmentDelegate) {
            this.mRefs = null;
            this.mRefs = new WeakReference<>(fragmentDelegate);
        }

        @Override // java.lang.Runnable
        public void run() {
            WeakReference<FragmentDelegate> weakReference = this.mRefs;
            FragmentDelegate fragmentDelegate = weakReference == null ? null : weakReference.get();
            if (fragmentDelegate == null) {
                return;
            }
            boolean z = true;
            if ((fragmentDelegate.mInvalidateMenuFlags & 1) == 1) {
                fragmentDelegate.mMenu = null;
            }
            if (fragmentDelegate.mMenu == null) {
                fragmentDelegate.mMenu = fragmentDelegate.createMenu();
                z = fragmentDelegate.onCreatePanelMenu(0, fragmentDelegate.mMenu);
            }
            if (z) {
                z = fragmentDelegate.onPreparePanel(0, null, fragmentDelegate.mMenu);
            }
            if (z) {
                fragmentDelegate.setMenu(fragmentDelegate.mMenu);
            } else {
                fragmentDelegate.setMenu(null);
                fragmentDelegate.mMenu = null;
            }
            FragmentDelegate.access$172(fragmentDelegate, -18);
        }
    }

    public FragmentDelegate(androidx.fragment.app.Fragment fragment) {
        super((AppCompatActivity) fragment.getActivity());
        this.mExtraPaddingLevel = 0;
        this.mWindowCallback = new SimpleWindowCallback() { // from class: miuix.appcompat.app.FragmentDelegate.1
            @Override // android.view.Window.Callback
            public void onActionModeFinished(ActionMode actionMode) {
                ((IFragment) FragmentDelegate.this.mFragment).onActionModeFinished(actionMode);
            }

            @Override // android.view.Window.Callback
            public void onActionModeStarted(ActionMode actionMode) {
                ((IFragment) FragmentDelegate.this.mFragment).onActionModeStarted(actionMode);
            }

            @Override // android.view.Window.Callback
            public boolean onMenuItemSelected(int i, MenuItem menuItem) {
                return FragmentDelegate.this.onMenuItemSelected(i, menuItem);
            }

            @Override // android.view.Window.Callback
            public void onPanelClosed(int i, Menu menu) {
                if (FragmentDelegate.this.getActivity() != null) {
                    FragmentDelegate.this.getActivity().onPanelClosed(i, menu);
                }
            }

            @Override // android.view.Window.Callback
            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
                return FragmentDelegate.this.onWindowStartingActionMode(callback);
            }
        };
        this.mFragment = fragment;
    }

    static /* synthetic */ byte access$172(FragmentDelegate fragmentDelegate, int i) {
        byte b = (byte) (i & fragmentDelegate.mInvalidateMenuFlags);
        fragmentDelegate.mInvalidateMenuFlags = b;
        return b;
    }

    private Runnable getInvalidateMenuRunnable() {
        if (this.mInvalidateMenuRunnable == null) {
            this.mInvalidateMenuRunnable = new InvalidateMenuRunnable(this);
        }
        return this.mInvalidateMenuRunnable;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public ActionBar createActionBar() {
        if (this.mFragment.isAdded()) {
            return new ActionBarImpl(this.mFragment);
        }
        return null;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public Context getThemedContext() {
        if (this.mThemedContext == null) {
            this.mThemedContext = this.mActivity;
            if (this.mExtraThemeRes != 0) {
                this.mThemedContext = new ContextThemeWrapper(this.mThemedContext, this.mExtraThemeRes);
            }
        }
        return this.mThemedContext;
    }

    public View getView() {
        return this.mSubDecor;
    }

    final void installSubDecor(Context context, ViewGroup viewGroup, LayoutInflater layoutInflater) {
        boolean z;
        if (this.mSubDecorInstalled) {
            if (this.mSubDecor.getParent() == null || !(this.mSubDecor.getParent() instanceof ViewGroup)) {
                return;
            }
            ViewGroup viewGroup2 = (ViewGroup) this.mSubDecor.getParent();
            if (viewGroup2.getChildCount() == 0) {
                viewGroup2.endViewTransition(this.mSubDecor);
                return;
            }
            return;
        }
        FragmentActivity activity = this.mFragment.getActivity();
        boolean z2 = activity instanceof AppCompatActivity;
        if (z2) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
            setExtraHorizontalPaddingLevel(appCompatActivity.getExtraHorizontalPaddingLevel());
            appCompatActivity.setExtraHorizontalPaddingEnable(false);
        }
        this.mSubDecorInstalled = true;
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) layoutInflater.inflate(R$layout.miuix_appcompat_screen_action_bar, viewGroup, false);
        actionBarOverlayLayout.setCallback(this.mWindowCallback);
        actionBarOverlayLayout.setRootSubDecor(false);
        actionBarOverlayLayout.setOverlayMode(this.mOverlayActionBar);
        actionBarOverlayLayout.setTranslucentStatus(getTranslucentStatus());
        if (this.mExtraThemeRes != 0) {
            actionBarOverlayLayout.setBackground(AttributeResolver.resolveDrawable(context, 16842836));
        }
        if (z2) {
            actionBarOverlayLayout.onFloatingModeChanged(((AppCompatActivity) activity).isInFloatingWindowMode());
        }
        ActionBarView actionBarView = (ActionBarView) actionBarOverlayLayout.findViewById(R$id.action_bar);
        this.mActionBarView = actionBarView;
        actionBarView.setWindowCallback(this.mWindowCallback);
        if (this.mFeatureIndeterminateProgress) {
            this.mActionBarView.initIndeterminateProgress();
        }
        if (isImmersionMenuEnabled()) {
            this.mActionBarView.initImmersionMore(this.mImmersionLayoutResourceId, this);
        }
        boolean equals = "splitActionBarWhenNarrow".equals(getUiOptionsFromMetadata());
        if (equals) {
            z = context.getResources().getBoolean(R$bool.abc_split_action_bar_is_narrow);
        } else {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(R$styleable.Window);
            boolean z3 = obtainStyledAttributes.getBoolean(R$styleable.Window_windowSplitActionBar, false);
            obtainStyledAttributes.recycle();
            z = z3;
        }
        if (z) {
            addSplitActionBar(z, equals, actionBarOverlayLayout);
        }
        updateOptionsMenu(1);
        invalidateOptionsMenu();
        this.mSubDecor = actionBarOverlayLayout;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void invalidateOptionsMenu() {
        FragmentActivity activity = this.mFragment.getActivity();
        if (activity != null) {
            byte b = this.mInvalidateMenuFlags;
            if ((b & 16) == 0) {
                this.mInvalidateMenuFlags = (byte) (b | 16);
                activity.getWindow().getDecorView().post(getInvalidateMenuRunnable());
            }
        }
    }

    public void onActionModeFinished(ActionMode actionMode) {
        this.mActionMode = null;
    }

    public void onActionModeStarted(ActionMode actionMode) {
        this.mActionMode = actionMode;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onConfigurationChanged(Configuration configuration) {
        FragmentActivity activity;
        super.onConfigurationChanged(configuration);
        View view = this.mSubDecor;
        if (view == null || !(view instanceof ActionBarOverlayLayout) || (activity = this.mFragment.getActivity()) == null || !(activity instanceof AppCompatActivity)) {
            return;
        }
        ((ActionBarOverlayLayout) this.mSubDecor).onFloatingModeChanged(((AppCompatActivity) activity).isInFloatingWindowMode());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    protected boolean onCreateImmersionMenu(MenuBuilder menuBuilder) {
        androidx.fragment.app.Fragment fragment = this.mFragment;
        if (fragment instanceof IFragment) {
            return ((IFragment) fragment).onCreateOptionsMenu(menuBuilder);
        }
        return false;
    }

    public boolean onCreatePanelMenu(int i, Menu menu) {
        if (i == 0) {
            return ((IFragment) this.mFragment).onCreatePanelMenu(i, menu);
        }
        return false;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        TypedArray obtainStyledAttributes = getThemedContext().obtainStyledAttributes(R$styleable.Window);
        int i = R$styleable.Window_windowActionBar;
        if (!obtainStyledAttributes.hasValue(i)) {
            obtainStyledAttributes.recycle();
            throw new IllegalStateException("You need to use a miui theme (or descendant) with this fragment.");
        }
        if (obtainStyledAttributes.getBoolean(i, false)) {
            requestWindowFeature(8);
        }
        if (obtainStyledAttributes.getBoolean(R$styleable.Window_windowActionBarOverlay, false)) {
            requestWindowFeature(9);
        }
        setTranslucentStatus(obtainStyledAttributes.getInt(R$styleable.Window_windowTranslucentStatus, 0));
        setImmersionMenuEnabled(obtainStyledAttributes.getBoolean(R$styleable.Window_immersionMenuEnabled, false));
        this.mImmersionLayoutResourceId = obtainStyledAttributes.getResourceId(R$styleable.Window_immersionMenuLayout, 0);
        obtainStyledAttributes.recycle();
        LayoutInflater cloneInContext = layoutInflater.cloneInContext(getThemedContext());
        if (this.mHasActionBar) {
            installSubDecor(getThemedContext(), viewGroup, cloneInContext);
            ViewGroup viewGroup2 = (ViewGroup) this.mSubDecor.findViewById(16908290);
            View onInflateView = ((IFragment) this.mFragment).onInflateView(cloneInContext, viewGroup2, bundle);
            if (onInflateView != null && onInflateView.getParent() != viewGroup2) {
                if (onInflateView.getParent() != null) {
                    ((ViewGroup) onInflateView.getParent()).removeView(onInflateView);
                }
                viewGroup2.removeAllViews();
                viewGroup2.addView(onInflateView);
            }
        } else {
            this.mSubDecor = ((IFragment) this.mFragment).onInflateView(cloneInContext, viewGroup, bundle);
        }
        return this.mSubDecor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDestroyView() {
        this.mSubDecor = null;
        this.mSubDecorInstalled = false;
        this.mActionBar = null;
        this.mActionBarView = null;
        this.mInvalidateMenuRunnable = null;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        if (i == 0) {
            return this.mFragment.onOptionsItemSelected(menuItem);
        }
        if (i == 6) {
            return this.mFragment.onContextItemSelected(menuItem);
        }
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder.Callback
    public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
        return onMenuItemSelected(0, menuItem);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    protected boolean onPrepareImmersionMenu(MenuBuilder menuBuilder) {
        androidx.fragment.app.Fragment fragment = this.mFragment;
        if (fragment instanceof IFragment) {
            fragment.onPrepareOptionsMenu(menuBuilder);
            return true;
        }
        return false;
    }

    public boolean onPreparePanel(int i, View view, Menu menu) {
        if (i == 0) {
            ((IFragment) this.mFragment).onPreparePanel(i, null, menu);
            return true;
        }
        return false;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        if (getActionBar() != null) {
            return ((ActionBarImpl) getActionBar()).startActionMode(callback);
        }
        return null;
    }

    public void setExtraHorizontalPaddingLevel(int i) {
        if (!LayoutUIUtils.isLevelValid(i) || this.mExtraPaddingLevel == i) {
            return;
        }
        this.mExtraPaddingLevel = i;
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setExtraHorizontalPaddingLevel(i);
        }
    }

    public void setExtraThemeRes(int i) {
        this.mExtraThemeRes = i;
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (callback instanceof SearchActionMode.Callback) {
            addContentMask((ActionBarOverlayLayout) this.mSubDecor);
        }
        return this.mSubDecor.startActionMode(callback);
    }

    public void updateOptionsMenu(int i) {
        this.mInvalidateMenuFlags = (byte) ((i & 1) | this.mInvalidateMenuFlags);
    }
}
