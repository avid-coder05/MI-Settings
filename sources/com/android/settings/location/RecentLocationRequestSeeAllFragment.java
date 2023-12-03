package com.android.settings.location;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import miui.yellowpage.YellowPageContract;

@Deprecated
/* loaded from: classes.dex */
public class RecentLocationRequestSeeAllFragment extends DashboardFragment {
    private RecentLocationRequestSeeAllPreferenceController mController;
    private MenuItem mHideSystemMenu;
    private boolean mShowSystem = false;
    private MenuItem mShowSystemMenu;

    private void updateMenu() {
        this.mShowSystemMenu.setVisible(!this.mShowSystem);
        this.mHideSystemMenu.setVisible(this.mShowSystem);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "RecentLocationReqAll";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1325;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.location_recent_requests_see_all;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        int i = getArguments().getInt(YellowPageContract.Profile.DIRECTORY);
        RecentLocationRequestSeeAllPreferenceController recentLocationRequestSeeAllPreferenceController = (RecentLocationRequestSeeAllPreferenceController) use(RecentLocationRequestSeeAllPreferenceController.class);
        this.mController = recentLocationRequestSeeAllPreferenceController;
        recentLocationRequestSeeAllPreferenceController.init(this);
        if (i != 0) {
            this.mController.setProfileType(i);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        this.mShowSystemMenu = menu.add(0, 2, 0, R.string.menu_show_system);
        this.mHideSystemMenu = menu.add(0, 3, 0, R.string.menu_hide_system);
        updateMenu();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2 || itemId == 3) {
            this.mShowSystem = menuItem.getItemId() == 2;
            updateMenu();
            RecentLocationRequestSeeAllPreferenceController recentLocationRequestSeeAllPreferenceController = this.mController;
            if (recentLocationRequestSeeAllPreferenceController != null) {
                recentLocationRequestSeeAllPreferenceController.setShowSystem(this.mShowSystem);
            }
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
