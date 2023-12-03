package com.android.settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.utils.SettingsFeatures;

/* loaded from: classes.dex */
public class EdgeModeSettings extends SettingsPreferenceFragment {
    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return EdgeModeSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!SettingsFeatures.isSplitTablet(getContext())) {
            getActivity().setRequestedOrientation(1);
        }
        addPreferencesFromResource(R.xml.edge_mode_settings);
        getPreferenceScreen().removePreference(getPreferenceScreen().findPreference("edge_mode_clean"));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        int i = "edge_mode_photo".equals(key) ? 2 : "edge_mode_clean".equals(key) ? 1 : "edge_mode_back".equals(key) ? 0 : -1;
        if (i != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt("edge_mode_type", i);
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.EdgeModeGuideActivity");
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
