package com.android.settings.popup;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;

/* loaded from: classes2.dex */
public class PopupSettings extends MiuiSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private Context mContext;
    private PopupCameraSoundChoosePreference mGridVIew;
    private PopupCameraLedChoosePreference mLedGridVIew;
    private CheckBoxPreference mPopupGesture;
    private PreferenceCategory mPopupGridviewSettings;
    private CheckBoxPreference mPopupLed;
    private CheckBoxPreference mPopupSound;

    private void gestureStateChange(boolean z) {
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "miui_popup_gesture_check", z ? 1 : 0, -2);
    }

    private void ledStateChange(boolean z) {
        this.mLedGridVIew.setEnabled(z);
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "miui_popup_led_check", z ? 1 : 0, -2);
    }

    private void soundStateChange(boolean z) {
        this.mGridVIew.setEnabled(z);
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "miui_popup_sound_check", z ? 1 : 0, -2);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return PopupSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.popupcamera_function_settings);
        getActivity().setTitle(R.string.popup_title);
        this.mContext = getActivity();
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("popup_voice_setting");
        this.mPopupGridviewSettings = preferenceCategory;
        this.mPopupSound = (CheckBoxPreference) preferenceCategory.findPreference("popup_voice_check");
        this.mPopupLed = (CheckBoxPreference) this.mPopupGridviewSettings.findPreference("popup_led_check");
        this.mPopupGesture = (CheckBoxPreference) this.mPopupGridviewSettings.findPreference("popup_gesture_check");
        this.mGridVIew = (PopupCameraSoundChoosePreference) this.mPopupGridviewSettings.findPreference("popup_voice_preference");
        this.mLedGridVIew = (PopupCameraLedChoosePreference) this.mPopupGridviewSettings.findPreference("popup_led_preference");
        this.mPopupSound.setOnPreferenceChangeListener(this);
        this.mPopupLed.setOnPreferenceChangeListener(this);
        this.mPopupGesture.setOnPreferenceChangeListener(this);
        if (!SettingsFeatures.isNeedShowColorLamp()) {
            this.mPopupGridviewSettings.removePreference(this.mLedGridVIew);
        }
        if ("cezanne".equals(Build.DEVICE)) {
            this.mPopupGridviewSettings.removePreference(this.mPopupGesture);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mGridVIew.onDestroy();
        this.mLedGridVIew.onDestroy();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("popup_voice_check".equals(preference.getKey())) {
            soundStateChange(((Boolean) obj).booleanValue());
            return true;
        } else if ("popup_led_check".equals(preference.getKey())) {
            ledStateChange(((Boolean) obj).booleanValue());
            return true;
        } else if ("popup_gesture_check".equals(preference.getKey())) {
            gestureStateChange(((Boolean) obj).booleanValue());
            return true;
        } else {
            return false;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        boolean z = Settings.System.getIntForUser(this.mContext.getContentResolver(), "miui_popup_sound_check", 1, -2) != 0;
        this.mPopupSound.setChecked(z);
        this.mGridVIew.setEnabled(z);
        boolean z2 = Settings.System.getIntForUser(this.mContext.getContentResolver(), "miui_popup_led_check", 1, -2) != 0;
        this.mPopupLed.setChecked(z2);
        this.mLedGridVIew.setEnabled(z2);
        this.mPopupGesture.setChecked(Settings.System.getIntForUser(this.mContext.getContentResolver(), "miui_popup_gesture_check", 0, -2) != 0);
    }
}
