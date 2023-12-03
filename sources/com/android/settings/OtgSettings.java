package com.android.settings;

import android.os.Bundle;
import android.view.View;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import miui.util.IOtgSwitch;

/* loaded from: classes.dex */
public class OtgSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mOtgCheckbox;

    private void updateOtgStatus() {
        if (this.mOtgCheckbox != null) {
            this.mOtgCheckbox.setChecked(IOtgSwitch.getInstance().getOtgStatus() == 0);
            this.mOtgCheckbox.setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.otg_settings);
        this.mOtgCheckbox = (CheckBoxPreference) findPreference("otg_button");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("otg_button".equals(preference.getKey())) {
            IOtgSwitch.getInstance().setOtgEnabled(((Boolean) obj).booleanValue());
            return true;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateOtgStatus();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }
}
