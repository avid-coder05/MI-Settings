package com.android.settings;

import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.smarthome.SmartHomePreferenceManager;
import com.android.settings.utils.StatusBarUtils;
import miuix.preference.DropDownPreference;

/* loaded from: classes.dex */
public class ExpandCardActionsSettings extends BaseSettingsPreferenceFragment {
    private CheckBoxPreference mExpandCardMiSmartHub;
    private CheckBoxPreference mExpandCardMiSmartPlay;
    private SmartHomePreferenceManager mSmartHomePreferenceManager;

    private void setupExpandCardMiSmartHub() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("expand_card_mi_smart_hub");
        this.mExpandCardMiSmartHub = checkBoxPreference;
        checkBoxPreference.setVisible(StatusBarUtils.isMiSmartHubVisible(getContext()));
        this.mExpandCardMiSmartHub.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.ExpandCardActionsSettings.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setMiSmartHub(ExpandCardActionsSettings.this.getContext(), ((Boolean) obj).booleanValue() ? 1 : 0);
                return true;
            }
        });
    }

    private void setupExpandCardMiSmartPlay() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("expand_card_mi_smart_play");
        this.mExpandCardMiSmartPlay = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.ExpandCardActionsSettings.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setMiSmartPlay(ExpandCardActionsSettings.this.getContext(), ((Boolean) obj).booleanValue() ? 1 : 0);
                return true;
            }
        });
    }

    private void setupSmartHome() {
        SmartHomePreferenceManager smartHomePreferenceManager = new SmartHomePreferenceManager(getContext(), false);
        this.mSmartHomePreferenceManager = smartHomePreferenceManager;
        smartHomePreferenceManager.onCreate((DropDownPreference) findPreference("smart_home"));
    }

    private void updateExpandCardMiSmartHub() {
        CheckBoxPreference checkBoxPreference = this.mExpandCardMiSmartHub;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(StatusBarUtils.isMiSmartHub(getContext()));
        }
    }

    private void updateExpandCardMiSmartPlay() {
        CheckBoxPreference checkBoxPreference = this.mExpandCardMiSmartPlay;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(StatusBarUtils.isMiSmartPlay(getContext()));
        }
    }

    private void updateSmartHome() {
        this.mSmartHomePreferenceManager.setVisible(StatusBarUtils.isUseControlPanel(getContext()));
        this.mSmartHomePreferenceManager.setListView(getListView());
        this.mSmartHomePreferenceManager.onResume();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return getClass().getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.expand_card_actions_settings);
        setupExpandCardMiSmartPlay();
        setupExpandCardMiSmartHub();
        setupSmartHome();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mSmartHomePreferenceManager.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateExpandCardMiSmartPlay();
        updateExpandCardMiSmartHub();
        updateSmartHome();
    }
}
