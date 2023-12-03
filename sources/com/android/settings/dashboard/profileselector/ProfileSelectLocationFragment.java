package com.android.settings.dashboard.profileselector;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.location.LocationPersonalSettings;
import com.android.settings.location.LocationSwitchBarController;
import com.android.settings.location.LocationWorkProfileSettings;
import com.android.settings.widget.SettingsMainSwitchBar;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class ProfileSelectLocationFragment extends ProfileSelectFragment {
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        Bundle bundle = new Bundle();
        bundle.putInt(YellowPageContract.Profile.DIRECTORY, 2);
        LocationWorkProfileSettings locationWorkProfileSettings = new LocationWorkProfileSettings();
        locationWorkProfileSettings.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt(YellowPageContract.Profile.DIRECTORY, 1);
        LocationPersonalSettings locationPersonalSettings = new LocationPersonalSettings();
        locationPersonalSettings.setArguments(bundle2);
        return new Fragment[]{locationPersonalSettings, locationWorkProfileSettings};
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
        switchBar.setTitle(getContext().getString(R.string.location_settings_primary_switch_title));
        new LocationSwitchBarController(settingsActivity, switchBar, getSettingsLifecycle());
        switchBar.show();
    }
}
