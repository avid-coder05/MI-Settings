package com.android.settings;

import android.app.MiuiStatusBarManager;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.smarthome.SmartHomePreferenceManager;
import com.android.settings.utils.StatusBarUtils;

/* loaded from: classes.dex */
public class LockScreenActionsSettings extends BaseSettingsPreferenceFragment {
    private CheckBoxPreference mLockScreenControlCenter;
    private CheckBoxPreference mLockScreenNotification;
    private CheckBoxPreference mLockScreenSmartHome;

    private void setupLockScreenControlCenter() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("lock_screen_control_center");
        this.mLockScreenControlCenter = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.LockScreenActionsSettings.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setExpandableUnderLockscreen(LockScreenActionsSettings.this.getContext(), ((Boolean) obj).booleanValue() ? 1 : 0);
                LockScreenActionsSettings.this.updateLockScreenSmartHome();
                return true;
            }
        });
    }

    private void setupLockScreenNotification() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("lock_screen_notification");
        this.mLockScreenNotification = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.LockScreenActionsSettings.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                MiuiStatusBarManager.setExpandableUnderKeyguard(LockScreenActionsSettings.this.getContext(), ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupLockScreenSmartHome() {
        this.mLockScreenSmartHome = (CheckBoxPreference) findPreference("lock_screen_smart_home");
        if (SmartHomePreferenceManager.isControlsSupported(getContext())) {
            this.mLockScreenSmartHome.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.LockScreenActionsSettings.3
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    SmartHomePreferenceManager.setExpandableUnderLockscreen(LockScreenActionsSettings.this.getContext(), ((Boolean) obj).booleanValue());
                    return true;
                }
            });
        } else {
            this.mLockScreenSmartHome.setVisible(false);
        }
    }

    private void updateLockScreenControlCenter() {
        CheckBoxPreference checkBoxPreference = this.mLockScreenControlCenter;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(StatusBarUtils.isExpandableUnderLockscreen(getContext()));
            this.mLockScreenControlCenter.setEnabled(StatusBarUtils.isUseControlPanel(getContext()));
        }
    }

    private void updateLockScreenNotification() {
        CheckBoxPreference checkBoxPreference = this.mLockScreenNotification;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(MiuiStatusBarManager.isExpandableUnderKeyguard(getContext()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLockScreenSmartHome() {
        CheckBoxPreference checkBoxPreference = this.mLockScreenSmartHome;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(SmartHomePreferenceManager.isExpandableUnderLockscreen(getContext()));
            this.mLockScreenSmartHome.setEnabled(StatusBarUtils.isUseControlPanel(getContext()) && StatusBarUtils.isExpandableUnderLockscreen(getContext()));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return getClass().getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.lock_screen_actions_settings);
        setupLockScreenNotification();
        setupLockScreenControlCenter();
        setupLockScreenSmartHome();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateLockScreenNotification();
        updateLockScreenControlCenter();
        updateLockScreenSmartHome();
    }
}
