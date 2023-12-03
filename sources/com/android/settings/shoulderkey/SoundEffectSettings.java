package com.android.settings.shoulderkey;

import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.provider.Weather;
import miuix.visual.check.VisualCheckGroup;

/* loaded from: classes2.dex */
public class SoundEffectSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, VisualCheckGroup.OnCheckedChangeListener {
    private ShoulderKeySoundPreference mShoulderKeySoundEffectOptions;
    private CheckBoxPreference mShoulderKeySoundSwitch;

    private void updateState() {
        int i = Settings.System.getInt(getContentResolver(), "shoulderkey_sound_switch", 0);
        this.mShoulderKeySoundSwitch.setChecked(i == 1);
        if (i == 0) {
            getPreferenceScreen().removePreference(this.mShoulderKeySoundEffectOptions);
            return;
        }
        getPreferenceScreen().addPreference(this.mShoulderKeySoundEffectOptions);
        this.mShoulderKeySoundEffectOptions.setCheckBoxCheckedType(Settings.System.getString(getContentResolver(), "shoulderkey_sound_type"));
    }

    @Override // miuix.visual.check.VisualCheckGroup.OnCheckedChangeListener
    public void onCheckedChanged(VisualCheckGroup visualCheckGroup, int i) {
        String str = i == R.id.sound_classic ? "classic" : i == R.id.sound_bullet ? "bullet" : i == R.id.sound_current ? "current" : i == R.id.sound_wind ? Weather.WeatherBaseColumns.WIND : null;
        if (str != null) {
            Settings.System.putString(getContentResolver(), "shoulderkey_sound_type", str);
            SoundPoolUtil.play(str, false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SoundPoolUtil.init(getActivity().getApplicationContext());
        addPreferencesFromResource(R.xml.shoulderkey_sound);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("shoulder_key_sound_switch");
        this.mShoulderKeySoundSwitch = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        ShoulderKeySoundPreference shoulderKeySoundPreference = (ShoulderKeySoundPreference) findPreference("shoulder_key_sound_effect");
        this.mShoulderKeySoundEffectOptions = shoulderKeySoundPreference;
        shoulderKeySoundPreference.setOnCheckedChangeListener(this);
        updateState();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        SoundPoolUtil.release();
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("shoulder_key_sound_switch".equals(preference.getKey())) {
            Settings.System.putInt(getContentResolver(), "shoulderkey_sound_switch", ((Boolean) obj).booleanValue() ? 1 : 0);
            updateState();
            return true;
        }
        return true;
    }
}
