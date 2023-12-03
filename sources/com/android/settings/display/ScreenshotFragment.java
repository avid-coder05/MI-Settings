package com.android.settings.display;

import android.os.Bundle;
import android.provider.MiuiSettings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

/* loaded from: classes.dex */
public class ScreenshotFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private CheckBoxPreference mScreenshotSound;
    private CheckBoxPreference mThreeGesture;

    private boolean hasScreenshotSoundEnabled() {
        return MiuiSettings.System.getBooleanForUser(getActivity().getContentResolver(), "has_screenshot_sound", true, 0);
    }

    private boolean isThreeGestureScreenshotEnabled() {
        return MiuiSettings.System.getBooleanForUser(getActivity().getContentResolver(), "three_gesture_screenshot", false, 0);
    }

    private void setHasScreenshotSoundEnabled(boolean z) {
        MiuiSettings.System.putBooleanForUser(getActivity().getContentResolver(), "has_screenshot_sound", z, 0);
    }

    private void setIsThreeGestureScreenshotEnabled(boolean z) {
        MiuiSettings.System.putBooleanForUser(getActivity().getContentResolver(), "three_gesture_screenshot", z, 0);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return ScreenshotFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.screenshot_settings);
        this.mThreeGesture = (CheckBoxPreference) findPreference("three_gesture");
        this.mScreenshotSound = (CheckBoxPreference) findPreference("screenshot_sound");
        this.mThreeGesture.setOnPreferenceChangeListener(this);
        this.mThreeGesture.setChecked(isThreeGestureScreenshotEnabled());
        this.mScreenshotSound.setOnPreferenceChangeListener(this);
        this.mScreenshotSound.setChecked(hasScreenshotSoundEnabled());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mThreeGesture) {
            setIsThreeGestureScreenshotEnabled(((Boolean) obj).booleanValue());
            return true;
        } else if (preference == this.mScreenshotSound) {
            setHasScreenshotSoundEnabled(((Boolean) obj).booleanValue());
            return true;
        } else {
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        getActionBar().setTitle(R.string.screenshot);
    }
}
