package com.android.settings.search.actionbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.SearchFeatureProvider;
import com.android.settings.search.SearchUpdater;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreateOptionsMenu;
import com.google.android.setupcompat.util.WizardManagerHelper;

/* loaded from: classes2.dex */
public class SearchMenuController implements LifecycleObserver, OnCreateOptionsMenu {
    public static final int MENU_SEARCH = 11;
    public static final String NEED_SEARCH_ICON_IN_ACTION_BAR = "need_search_icon_in_action_bar";
    private final Fragment mHost;
    private final int mPageId;

    private SearchMenuController(Fragment fragment, int i) {
        this.mHost = fragment;
        this.mPageId = i;
    }

    public static void init(InstrumentedFragment instrumentedFragment) {
        instrumentedFragment.getSettingsLifecycle().addObserver(new SearchMenuController(instrumentedFragment, instrumentedFragment.getMetricsCategory()));
    }

    public static void init(InstrumentedPreferenceFragment instrumentedPreferenceFragment) {
        instrumentedPreferenceFragment.getSettingsLifecycle().addObserver(new SearchMenuController(instrumentedPreferenceFragment, instrumentedPreferenceFragment.getMetricsCategory()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreateOptionsMenu$0(Activity activity, MenuItem menuItem) {
        Intent buildSearchIntent = FeatureFactory.getFactory(activity).getSearchFeatureProvider().buildSearchIntent(activity, this.mPageId);
        if (activity.getPackageManager().queryIntentActivities(buildSearchIntent, SearchUpdater.GOOGLE).isEmpty()) {
            return true;
        }
        FeatureFactory.getFactory(activity).getMetricsFeatureProvider().action(activity, 226, new Pair[0]);
        this.mHost.startActivityForResult(buildSearchIntent, SearchFeatureProvider.REQUEST_CODE);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreateOptionsMenu
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        final FragmentActivity activity = this.mHost.getActivity();
        String string = activity.getString(R.string.config_settingsintelligence_package_name);
        if (!WizardManagerHelper.isDeviceProvisioned(activity) || WizardManagerHelper.isAnySetupWizard(activity.getIntent()) || !Utils.isPackageEnabled(activity, string) || menu == null) {
            return;
        }
        Bundle arguments = this.mHost.getArguments();
        if ((arguments == null || arguments.getBoolean(NEED_SEARCH_ICON_IN_ACTION_BAR, true)) && menu.findItem(11) == null) {
            MenuItem add = menu.add(0, 11, 0, R.string.search_menu);
            add.setIcon(R.drawable.ic_search_24dp);
            add.setShowAsAction(2);
            add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.android.settings.search.actionbar.SearchMenuController$$ExternalSyntheticLambda0
                @Override // android.view.MenuItem.OnMenuItemClickListener
                public final boolean onMenuItemClick(MenuItem menuItem) {
                    boolean lambda$onCreateOptionsMenu$0;
                    lambda$onCreateOptionsMenu$0 = SearchMenuController.this.lambda$onCreateOptionsMenu$0(activity, menuItem);
                    return lambda$onCreateOptionsMenu$0;
                }
            });
        }
    }
}
