package com.android.settings.applications.specialaccess;

import android.miui.AppOpsUtils;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public class SpecialAccessSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.special_access);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SpecialAccessSettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 351;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.special_access;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar == null || TextUtils.isEmpty(getPreferenceScreen().getTitle())) {
            return;
        }
        appCompatActionBar.setTitle(getPreferenceScreen().getTitle());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public void updatePreferenceStates() {
        PreferenceScreen preferenceScreen;
        Preference findPreference;
        super.updatePreferenceStates();
        if (!AppOpsUtils.isXOptMode() || (findPreference = (preferenceScreen = getPreferenceScreen()).findPreference("manage_device_oaid")) == null) {
            return;
        }
        preferenceScreen.removePreference(findPreference);
    }
}
