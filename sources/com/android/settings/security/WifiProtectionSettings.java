package com.android.settings.security;

import android.os.Bundle;
import android.view.View;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

/* loaded from: classes2.dex */
public class WifiProtectionSettings extends SettingsPreferenceFragment {
    private CheckBoxPreference mCheckbox;
    private Preference mTip;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.wifi_protection_settings);
        this.mCheckbox = (CheckBoxPreference) findPreference("wifi_protection_checkbox");
        this.mTip = findPreference("wifi_protection_tip");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mCheckbox.setChecked(true);
        this.mCheckbox.setEnabled(false);
        this.mTip.setEnabled(false);
    }
}
