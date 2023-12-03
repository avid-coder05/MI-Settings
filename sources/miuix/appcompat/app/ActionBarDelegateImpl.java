package miuix.appcompat.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import miuix.appcompat.R$id;
import miuix.appcompat.R$integer;
import miuix.appcompat.internal.app.widget.ActionBarContainer;
import miuix.appcompat.internal.app.widget.ActionBarContextView;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.app.widget.ActionBarView;
import miuix.appcompat.internal.view.menu.ImmersionMenuPopupWindow;
import miuix.appcompat.internal.view.menu.ImmersionMenuPopupWindowImpl;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.MenuPresenter;
import miuix.core.util.variable.WindowWrapper;

/* loaded from: classes5.dex */
public abstract class ActionBarDelegateImpl implements ActionBarDelegate, MenuPresenter.Callback, MenuBuilder.Callback {
    protected ActionBar mActionBar;
    protected ActionBarView mActionBarView;
    protected ActionMode mActionMode;
    final AppCompatActivity mActivity;
    protected boolean mFeatureIndeterminateProgress;
    protected boolean mFeatureProgress;
    boolean mHasActionBar;
    protected int mImmersionLayoutResourceId;
    private MenuBuilder mImmersionMenu;
    private boolean mImmersionMenuEnabled;
    protected MenuBuilder mMenu;
    private MenuInflater mMenuInflater;
    private ImmersionMenuPopupWindow mMenuPopupWindow;
    boolean mOverlayActionBar;
    protected boolean mSubDecorInstalled;
    private int mTranslucentStatus = 0;
    private boolean mHasAddSplitActionBar = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionBarDelegateImpl(AppCompatActivity appCompatActivity) {
        this.mActivity = appCompatActivity;
    }

    public void addContentMask(ActionBarOverlayLayout actionBarOverlayLayout) {
        if (actionBarOverlayLayout != null) {
            ViewStub viewStub = (ViewStub) actionBarOverlayLayout.findViewById(R$id.content_mask_vs);
            actionBarOverlayLayout.setContentMask(viewStub != null ? viewStub.inflate() : actionBarOverlayLayout.findViewById(R$id.content_mask));
        }
    }

    public void addSplitActionBar(boolean z, boolean z2, ActionBarOverlayLayout actionBarOverlayLayout) {
        if (this.mHasAddSplitActionBar) {
            return;
        }
        this.mHasAddSplitActionBar = true;
        ViewStub viewStub = (ViewStub) actionBarOverlayLayout.findViewById(R$id.split_action_bar_vs);
        ActionBarContainer actionBarContainer = viewStub != null ? (ActionBarContainer) viewStub.inflate() : (ActionBarContainer) actionBarOverlayLayout.findViewById(R$id.split_action_bar);
        if (actionBarContainer != null) {
            this.mActionBarView.setSplitView(actionBarContainer);
            this.mActionBarView.setSplitActionBar(z);
            this.mActionBarView.setSplitWhenNarrow(z2);
            actionBarOverlayLayout.setSplitActionBarView(actionBarContainer);
            addContentMask(actionBarOverlayLayout);
        }
        ActionBarContainer actionBarContainer2 = (ActionBarContainer) actionBarOverlayLayout.findViewById(R$id.action_bar_container);
        ViewStub viewStub2 = (ViewStub) actionBarOverlayLayout.findViewById(R$id.action_context_bar_vs);
        ActionBarContextView actionBarContextView = viewStub2 != null ? (ActionBarContextView) viewStub2.inflate() : (ActionBarContextView) actionBarOverlayLayout.findViewById(R$id.action_context_bar);
        if (actionBarContextView != null) {
            actionBarContainer2.setActionBarContextView(actionBarContextView);
            actionBarOverlayLayout.setActionBarContextView(actionBarContextView);
            if (actionBarContainer != null) {
                actionBarContextView.setSplitView(actionBarContainer);
                actionBarContextView.setSplitActionBar(z);
                actionBarContextView.setSplitWhenNarrow(z2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MenuBuilder createMenu() {
        MenuBuilder menuBuilder = new MenuBuilder(getActionBarThemedContext());
        menuBuilder.setCallback(this);
        return menuBuilder;
    }

    public void dismissImmersionMenu(boolean z) {
        ImmersionMenuPopupWindow immersionMenuPopupWindow = this.mMenuPopupWindow;
        if (immersionMenuPopupWindow != null) {
            immersionMenuPopupWindow.dismiss(z);
        }
    }

    public final ActionBar getActionBar() {
        if (!this.mHasActionBar && !this.mOverlayActionBar) {
            this.mActionBar = null;
        } else if (this.mActionBar == null) {
            this.mActionBar = createActionBar();
        }
        return this.mActionBar;
    }

    protected final Context getActionBarThemedContext() {
        AppCompatActivity appCompatActivity = this.mActivity;
        ActionBar actionBar = getActionBar();
        return actionBar != null ? actionBar.getThemedContext() : appCompatActivity;
    }

    public AppCompatActivity getActivity() {
        return this.mActivity;
    }

    public MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                this.mMenuInflater = new MenuInflater(actionBar.getThemedContext());
            } else {
                this.mMenuInflater = new MenuInflater(this.mActivity);
            }
        }
        return this.mMenuInflater;
    }

    public abstract Context getThemedContext();

    public int getTranslucentStatus() {
        return this.mTranslucentStatus;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getUiOptionsFromMetadata() {
        try {
            Bundle bundle = this.mActivity.getPackageManager().getActivityInfo(this.mActivity.getComponentName(), 128).metaData;
            if (bundle != null) {
                return bundle.getString("android.support.UI_OPTIONS");
            }
            return null;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("ActionBarDelegate", "getUiOptionsFromMetadata: Activity '" + this.mActivity.getClass().getSimpleName() + "' not in manifest");
            return null;
        }
    }

    public boolean isImmersionMenuEnabled() {
        return this.mImmersionMenuEnabled;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter.Callback
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        this.mActivity.closeOptionsMenu();
    }

    public void onConfigurationChanged(Configuration configuration) {
        ActionBarImpl actionBarImpl;
        if (this.mHasActionBar && this.mSubDecorInstalled && (actionBarImpl = (ActionBarImpl) getActionBar()) != null) {
            actionBarImpl.onConfigurationChanged(configuration);
        }
    }

    public void onCreate(Bundle bundle) {
    }

    protected abstract boolean onCreateImmersionMenu(MenuBuilder menuBuilder);

    public abstract /* synthetic */ boolean onMenuItemSelected(int i, MenuItem menuItem);

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder.Callback
    public void onMenuModeChange(MenuBuilder menuBuilder) {
        reopenMenu(menuBuilder, true);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter.Callback
    public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
        return false;
    }

    public void onPostResume() {
        ActionBarImpl actionBarImpl;
        if (this.mHasActionBar && this.mSubDecorInstalled && (actionBarImpl = (ActionBarImpl) getActionBar()) != null) {
            actionBarImpl.setShowHideAnimationEnabled(true);
        }
    }

    protected abstract boolean onPrepareImmersionMenu(MenuBuilder menuBuilder);

    public void onStop() {
        ActionBarImpl actionBarImpl;
        dismissImmersionMenu(false);
        if (this.mHasActionBar && this.mSubDecorInstalled && (actionBarImpl = (ActionBarImpl) getActionBar()) != null) {
            actionBarImpl.setShowHideAnimationEnabled(false);
        }
    }

    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return null;
    }

    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int i) {
        if (i == 0) {
            return onWindowStartingActionMode(callback);
        }
        return null;
    }

    protected void reopenMenu(MenuBuilder menuBuilder, boolean z) {
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView == null || !actionBarView.isOverflowReserved()) {
            menuBuilder.close();
        } else if (this.mActionBarView.isOverflowMenuShowing() && z) {
            this.mActionBarView.hideOverflowMenu();
        } else if (this.mActionBarView.getVisibility() == 0) {
            this.mActionBarView.showOverflowMenu();
        }
    }

    public boolean requestWindowFeature(int i) {
        if (i == 2) {
            this.mFeatureProgress = true;
            return true;
        } else if (i == 5) {
            this.mFeatureIndeterminateProgress = true;
            return true;
        } else if (i == 8) {
            this.mHasActionBar = true;
            return true;
        } else if (i != 9) {
            return this.mActivity.requestWindowFeature(i);
        } else {
            this.mOverlayActionBar = true;
            return true;
        }
    }

    public void setImmersionMenuEnabled(boolean z) {
        this.mImmersionMenuEnabled = z;
        if (this.mSubDecorInstalled && this.mHasActionBar) {
            if (!z) {
                this.mActionBarView.hideImmersionMore();
            } else if (!this.mActionBarView.showImmersionMore()) {
                this.mActionBarView.initImmersionMore(this.mImmersionLayoutResourceId, this);
            }
            invalidateOptionsMenu();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public void setMenu(MenuBuilder menuBuilder) {
        if (menuBuilder == this.mMenu) {
            return;
        }
        this.mMenu = menuBuilder;
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.setMenu(menuBuilder, this);
        }
    }

    public void setTranslucentStatus(int i) {
        int integer = this.mActivity.getResources().getInteger(R$integer.window_translucent_status);
        if (integer >= 0 && integer <= 2) {
            i = integer;
        }
        if (this.mTranslucentStatus == i || !WindowWrapper.setTranslucentStatus(this.mActivity.getWindow(), i)) {
            return;
        }
        this.mTranslucentStatus = i;
    }

    public void showImmersionMenu(View view, ViewGroup viewGroup) {
        if (!this.mImmersionMenuEnabled) {
            Log.w("ActionBarDelegate", "Try to show immersion menu when immersion menu disabled");
        } else if (view == null) {
            throw new IllegalArgumentException("You must specify a valid anchor view");
        } else {
            if (this.mImmersionMenu == null) {
                MenuBuilder createMenu = createMenu();
                this.mImmersionMenu = createMenu;
                onCreateImmersionMenu(createMenu);
            }
            if (onPrepareImmersionMenu(this.mImmersionMenu) && this.mImmersionMenu.hasVisibleItems()) {
                ImmersionMenuPopupWindow immersionMenuPopupWindow = this.mMenuPopupWindow;
                if (immersionMenuPopupWindow == null) {
                    this.mMenuPopupWindow = new ImmersionMenuPopupWindowImpl(this, this.mImmersionMenu);
                } else {
                    immersionMenuPopupWindow.update(this.mImmersionMenu);
                }
                if (this.mMenuPopupWindow.isShowing()) {
                    return;
                }
                this.mMenuPopupWindow.show(view, viewGroup);
            }
        }
    }
}
