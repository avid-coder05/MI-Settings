package com.android.settings.shoulderkey;

import android.app.ActionBar;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes2.dex */
public class ShoulderKeySettings extends SettingsPreferenceFragment {
    private ValuePreference mShoudlerKeyGameLightEffec;
    private ValuePreference mShoudlerKeyShortcut;
    private PreferenceCategory mShoulderKeyFeatures;
    private ValuePreference mShoulderKeySoundEffect;

    private void updateState() {
        this.mShoulderKeySoundEffect.setValue(Settings.System.getInt(getContentResolver(), "shoulderkey_sound_switch", 0) == 1 ? R.string.shoulder_key_switch_status_open : R.string.shoulder_key_switch_status_close);
        this.mShoudlerKeyGameLightEffec.setValue(Settings.System.getInt(getContentResolver(), "shoulderkey_game_light_switch", 0) == 1 ? R.string.shoulder_key_switch_status_open : R.string.shoulder_key_switch_status_close);
        if (SettingsFeatures.IS_SUPPORT_SHOULDER_KEY_MORE) {
            return;
        }
        this.mShoulderKeyFeatures.removePreference(this.mShoudlerKeyShortcut);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return ShoulderKeySettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.shoulderkey_settings);
        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("shoulder_key_features");
        this.mShoulderKeyFeatures = preferenceCategory;
        this.mShoulderKeySoundEffect = (ValuePreference) preferenceCategory.findPreference("shoulder_key_sound_effect");
        this.mShoudlerKeyGameLightEffec = (ValuePreference) this.mShoulderKeyFeatures.findPreference("shoulder_key_game_light_effect");
        this.mShoudlerKeyShortcut = (ValuePreference) this.mShoulderKeyFeatures.findPreference("shoulder_key_shortcut");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateState();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.shoulder_key_settings_title);
        }
    }
}
