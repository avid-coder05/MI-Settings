package com.android.settings.controlcenter;

import android.app.MiuiStatusBarManager;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.BaseSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.utils.StatusBarUtils;
import miuix.preference.DropDownPreference;

/* loaded from: classes.dex */
public class ControlCenterSettings extends BaseSettingsPreferenceFragment {
    private CheckBoxPreference mCollapseAfterClicked;
    private CheckBoxPreference mControlPanelSwitchSide;
    private DropDownPreference mQuickSettingsLayout;
    private ControlCenterStylePreference mUseControlPanel;

    private void setupCollapseAfterClicked() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("collapse_after_clicked");
        this.mCollapseAfterClicked = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.controlcenter.ControlCenterSettings.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                MiuiStatusBarManager.setCollapseAfterClicked(ControlCenterSettings.this.getContext(), ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupControlPanelSwitchSide() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("control_panel_switch_side");
        this.mControlPanelSwitchSide = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.controlcenter.ControlCenterSettings.3
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setControlPanelSwitchSide(ControlCenterSettings.this.getContext(), ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupQuickSettingsLayout() {
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("quick_settings_layout");
        this.mQuickSettingsLayout = dropDownPreference;
        dropDownPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.controlcenter.ControlCenterSettings.4
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setCompactQuickSettings(ControlCenterSettings.this.getContext(), "1".equals((String) obj));
                return true;
            }
        });
    }

    private void setupUseControlPanel() {
        ControlCenterStylePreference controlCenterStylePreference = (ControlCenterStylePreference) findPreference("control_center_style");
        this.mUseControlPanel = controlCenterStylePreference;
        controlCenterStylePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.controlcenter.ControlCenterSettings.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setUseControlPanel(ControlCenterSettings.this.getContext(), ((Boolean) obj).booleanValue() ? 1 : 0);
                ControlCenterSettings.this.updateControlPanelSwitchSideEnabled();
                ControlCenterSettings.this.updateQuickSettingsLayoutEnabled();
                return true;
            }
        });
    }

    private void updateCollapseAfterClicked() {
        this.mCollapseAfterClicked.setChecked(MiuiStatusBarManager.isCollapseAfterClicked(getContext()));
    }

    private void updateControlPanelSwitchSide() {
        updateControlPanelSwitchSideEnabled();
        this.mControlPanelSwitchSide.setChecked(StatusBarUtils.isControlPanelSwitchSide(getContext()));
    }

    private void updateQuickSettingsLayout() {
        updateQuickSettingsLayoutEnabled();
        this.mQuickSettingsLayout.setValue(String.valueOf(StatusBarUtils.isCompactQuickSettings(getContext()) ? 1 : 0));
    }

    private void updateUseControlPanel() {
        ControlCenterStylePreference controlCenterStylePreference = this.mUseControlPanel;
        if (controlCenterStylePreference != null) {
            controlCenterStylePreference.setChecked(StatusBarUtils.isUseControlPanel(getContext()));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return getClass().getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.control_center_settings);
        getPreferenceScreen().setTitle(R.string.status_bar_title);
        setupUseControlPanel();
        setupCollapseAfterClicked();
        setupControlPanelSwitchSide();
        setupQuickSettingsLayout();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateUseControlPanel();
        updateCollapseAfterClicked();
        updateControlPanelSwitchSide();
        updateQuickSettingsLayout();
    }

    void updateControlPanelSwitchSideEnabled() {
        this.mControlPanelSwitchSide.setEnabled(StatusBarUtils.isUseControlPanel(getContext()));
    }

    void updateQuickSettingsLayoutEnabled() {
        this.mQuickSettingsLayout.setEnabled(!StatusBarUtils.isUseControlPanel(getContext()));
    }
}
