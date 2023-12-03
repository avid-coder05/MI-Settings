package com.android.settings;

import android.app.MiuiStatusBarManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.notification.NotificationSettingsHelper;
import com.android.settings.smarthome.SmartHomePreferenceManager;
import com.android.settings.utils.StatusBarUtils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.os.Build;
import miui.telephony.TelephonyManager;
import miuix.preference.DropDownPreference;

/* loaded from: classes.dex */
public class MiuiStatusBarSettings extends BaseSettingsPreferenceFragment {
    private String[] mBatteryEntries;
    private DropDownPreference mBatteryIndicator;
    private ValuePreference mCustomCarrier;
    private CheckBoxPreference mExpandableUnderKeyguard;
    private CheckBoxPreference mExpandableUnderLockscreen;
    private boolean mHasMobileDataFeature;
    private PreferenceScreen mManageNotification;
    private ValuePreference mNotificationFold;
    private ListPreference mNotificationShadeShortcut;
    private ListPreference mNotificationStyle;
    private CheckBoxPreference mShowCarrierUnderKeyguard;
    private CheckBoxPreference mShowNetworkSpeed;
    private CheckBoxPreference mShowNotificationIcon;
    private SmartHomePreferenceManager mSmartHomePreferenceManager;
    private CheckBoxPreference mToggleCollapseAfterClicked;
    private CheckBoxPreference mUseControlPanel;

    private void setupBatteryIndicator() {
        TypedArray obtainTypedArray;
        this.mBatteryIndicator = (DropDownPreference) findPreference("battery_indicator");
        this.mBatteryEntries = getResources().getStringArray(R.array.battery_indicator_style_entries);
        if (StatusBarUtils.IS_NOTCH && !com.android.settings.utils.Utils.isHole(getResources())) {
            if (com.android.settings.utils.Utils.isDrip(getResources()) || com.android.settings.utils.Utils.isNarrowNotch(getResources())) {
                this.mBatteryEntries = getResources().getStringArray(R.array.notch_battery_indicator_style_entries);
            } else {
                this.mBatteryEntries = getResources().getStringArray(R.array.wide_notch_battery_indicator_style_entries);
            }
            int length = this.mBatteryEntries.length;
            String[] strArr = new String[length];
            for (int i = 0; i < length; i++) {
                strArr[i] = "";
            }
            obtainTypedArray = getResources().obtainTypedArray(R.array.notch_battery_indicator_style_icons);
            this.mBatteryIndicator.setEntries(strArr);
            this.mBatteryIndicator.setEntryValues(getResources().getStringArray(R.array.notch_battery_indicator_style_values));
        } else if (StatusBarUtils.IS_FOLD) {
            String[] stringArray = getResources().getStringArray(R.array.notch_battery_indicator_style_entries);
            this.mBatteryEntries = stringArray;
            int length2 = stringArray.length;
            String[] strArr2 = new String[length2];
            for (int i2 = 0; i2 < length2; i2++) {
                strArr2[i2] = "";
            }
            obtainTypedArray = getResources().obtainTypedArray(R.array.notch_battery_indicator_style_icons);
            this.mBatteryIndicator.setEntries(strArr2);
            this.mBatteryIndicator.setEntryValues(getResources().getStringArray(R.array.notch_battery_indicator_style_values));
        } else {
            int length3 = this.mBatteryEntries.length;
            String[] strArr3 = new String[length3];
            for (int i3 = 0; i3 < length3; i3++) {
                strArr3[i3] = "";
            }
            obtainTypedArray = getResources().obtainTypedArray(R.array.battery_indicator_style_icons);
            this.mBatteryIndicator.setEntries(strArr3);
            this.mBatteryIndicator.setEntryValues(getResources().getStringArray(R.array.battery_indicator_style_values));
        }
        int length4 = obtainTypedArray.length();
        int[] iArr = new int[length4];
        for (int i4 = 0; i4 < length4; i4++) {
            iArr[i4] = obtainTypedArray.getResourceId(i4, 0);
        }
        obtainTypedArray.recycle();
        this.mBatteryIndicator.setEntryIcons(iArr);
        this.mBatteryIndicator.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiStatusBarSettings.7
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Settings.System.putInt(MiuiStatusBarSettings.this.getContentResolver(), "battery_indicator_style", com.android.settings.utils.Utils.parseInt((String) obj));
                MiuiStatusBarSettings.this.updateBatteryIndicator();
                return true;
            }
        });
    }

    private void setupCustomCarrier() {
        ValuePreference valuePreference = (ValuePreference) findPreference("custom_carrier");
        this.mCustomCarrier = valuePreference;
        if (StatusBarUtils.IS_MX_TELCEL || StatusBarUtils.IS_LM_CR) {
            ((PreferenceCategory) findPreference("settings_status_bar")).removePreference(this.mCustomCarrier);
            this.mCustomCarrier = null;
            return;
        }
        valuePreference.setShowRightArrow(true);
        if (this.mHasMobileDataFeature) {
            return;
        }
        this.mCustomCarrier.setTitle(getResources().getString(R.string.custom_wifi_name_title));
    }

    private void setupExpandableUnderKeyguard() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("expandable_under_keyguard");
        this.mExpandableUnderKeyguard = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiStatusBarSettings.9
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                MiuiStatusBarManager.setExpandableUnderKeyguard(MiuiStatusBarSettings.this.getActivity(), ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupExpandableUnderLockscreen() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("expandable_under_lock_screen");
        this.mExpandableUnderLockscreen = checkBoxPreference;
        checkBoxPreference.setEnabled(StatusBarUtils.isUseControlPanel(getContext()));
        this.mExpandableUnderLockscreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiStatusBarSettings.6
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setExpandableUnderLockscreen(MiuiStatusBarSettings.this.getContext(), ((Boolean) obj).booleanValue() ? 1 : 0);
                return true;
            }
        });
    }

    private void setupManageNotification() {
        PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("manage_notification");
        this.mManageNotification = preferenceScreen;
        preferenceScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.MiuiStatusBarSettings.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                Intent preferManageEntranceIntent = NotificationSettingsHelper.getPreferManageEntranceIntent(MiuiStatusBarSettings.this.getContext());
                if (preferManageEntranceIntent != null) {
                    MiuiStatusBarSettings.this.startActivity(preferManageEntranceIntent);
                    return true;
                }
                return false;
            }
        });
    }

    private void setupNotificationFold() {
        ValuePreference valuePreference = (ValuePreference) findPreference("notification_fold");
        this.mNotificationFold = valuePreference;
        if (!Build.IS_INTERNATIONAL_BUILD) {
            valuePreference.setShowRightArrow(true);
            return;
        }
        getPreferenceScreen().removePreference(this.mNotificationFold);
        this.mNotificationFold = null;
    }

    private void setupNotificationShadeShortcut() {
        this.mNotificationShadeShortcut = (ListPreference) findPreference("notification_shade_shortcut");
        ((PreferenceCategory) findPreference("settings_status_bar")).removePreference(this.mNotificationShadeShortcut);
        this.mNotificationShadeShortcut = null;
    }

    private void setupNotificationStyle() {
        ListPreference listPreference = (ListPreference) findPreference("notification_style");
        this.mNotificationStyle = listPreference;
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiStatusBarSettings.10
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setNotificationStyle(MiuiStatusBarSettings.this.getActivity(), com.android.settings.utils.Utils.parseInt((String) obj));
                MiuiStatusBarSettings.this.updateNotificationStyle();
                return true;
            }
        });
    }

    private void setupShowCarrierUnderKeyguard() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("show_carrier_under_keyguard");
        this.mShowCarrierUnderKeyguard = checkBoxPreference;
        if (StatusBarUtils.IS_MX_TELCEL || StatusBarUtils.IS_LM_CR) {
            ((PreferenceCategory) findPreference("settings_status_bar")).removePreference(this.mShowCarrierUnderKeyguard);
            this.mShowCarrierUnderKeyguard = null;
            return;
        }
        if (!this.mHasMobileDataFeature) {
            checkBoxPreference.setTitle(getResources().getString(R.string.show_wifi_name_title));
        }
        this.mShowCarrierUnderKeyguard.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiStatusBarSettings.4
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Settings.System.putInt(MiuiStatusBarSettings.this.getContentResolver(), "status_bar_show_carrier_under_keyguard", ((Boolean) obj).booleanValue() ? 1 : 0);
                return true;
            }
        });
    }

    private void setupShowNetworkSpeed() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("show_network_speed");
        this.mShowNetworkSpeed = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiStatusBarSettings.3
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                MiuiStatusBarManager.setShowNetworkSpeed(MiuiStatusBarSettings.this.getActivity(), ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupShowNotificationIcon() {
        this.mShowNotificationIcon = (CheckBoxPreference) findPreference("show_notification_icon");
        if (!StatusBarUtils.isMiuiOptimizationOff(getActivity())) {
            this.mShowNotificationIcon.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiStatusBarSettings.2
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    MiuiStatusBarManager.setShowNotificationIcon(MiuiStatusBarSettings.this.getActivity(), ((Boolean) obj).booleanValue());
                    return true;
                }
            });
            return;
        }
        ((PreferenceCategory) findPreference("settings_status_bar")).removePreference(this.mShowNotificationIcon);
        this.mShowNotificationIcon = null;
    }

    private void setupToggleCollapseAfterClicked() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("toggle_collapse_after_clicked");
        this.mToggleCollapseAfterClicked = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiStatusBarSettings.8
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                MiuiStatusBarManager.setCollapseAfterClicked(MiuiStatusBarSettings.this.getActivity(), ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupUseControlPanel() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("use_control_panel");
        this.mUseControlPanel = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiStatusBarSettings.5
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Boolean bool = (Boolean) obj;
                StatusBarUtils.setUseControlPanel(MiuiStatusBarSettings.this.getContext(), bool.booleanValue() ? 1 : 0);
                MiuiStatusBarSettings.this.mExpandableUnderLockscreen.setEnabled(bool.booleanValue());
                MiuiStatusBarSettings.this.mSmartHomePreferenceManager.setEnabled(bool.booleanValue());
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBatteryIndicator() {
        int findIndexOfValue;
        int i = Settings.System.getInt(getContentResolver(), "battery_indicator_style", 1);
        if (i < 0 || (findIndexOfValue = this.mBatteryIndicator.findIndexOfValue(String.valueOf(i))) < 0 || findIndexOfValue >= this.mBatteryEntries.length) {
            return;
        }
        this.mBatteryIndicator.setValueIndex(findIndexOfValue);
        this.mBatteryIndicator.setSummary(this.mBatteryEntries[findIndexOfValue]);
    }

    private void updateCustomCarrier() {
        if (this.mCustomCarrier != null) {
            String string = getResources().getString(R.string.none);
            StringBuilder sb = new StringBuilder();
            int phoneCount = StatusBarUtils.IS_CUST_SINGLE_SIM ? 1 : TelephonyManager.getDefault().getPhoneCount();
            for (int i = 0; i < phoneCount; i++) {
                sb.append(TelephonyManager.getDefault().hasIccCard(i) ? MiuiSettings.System.getString(getContentResolver(), "status_bar_custom_carrier" + i, string) : string);
                if (i != phoneCount - 1) {
                    sb.append(" | ");
                }
            }
            this.mCustomCarrier.setValue(sb.toString());
        }
    }

    private void updateExpandableUnderKeyguard() {
        this.mExpandableUnderKeyguard.setChecked(MiuiStatusBarManager.isExpandableUnderKeyguard(getActivity()));
    }

    private void updateExpandableUnderLockscreen() {
        CheckBoxPreference checkBoxPreference = this.mExpandableUnderLockscreen;
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(StatusBarUtils.isUseControlPanel(getContext()));
            this.mExpandableUnderLockscreen.setChecked(StatusBarUtils.isExpandableUnderLockscreen(getContext()));
        }
    }

    private void updateNotificationFold() {
        if (this.mNotificationFold != null) {
            this.mNotificationFold.setValue(StatusBarUtils.isUserFold(getActivity()) ? R.string.on : R.string.off);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotificationStyle() {
        int notificationStyle = StatusBarUtils.getNotificationStyle(getActivity());
        if (notificationStyle < 0 || notificationStyle >= this.mNotificationStyle.getEntries().length) {
            return;
        }
        this.mNotificationStyle.setValueIndex(notificationStyle);
        ListPreference listPreference = this.mNotificationStyle;
        listPreference.setSummary(listPreference.getEntries()[notificationStyle]);
    }

    private void updateShowCarrierUnderKeyguard() {
        CheckBoxPreference checkBoxPreference = this.mShowCarrierUnderKeyguard;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(Settings.System.getInt(getContentResolver(), "status_bar_show_carrier_under_keyguard", 1) == 1);
        }
    }

    private void updateShowNetworkSpeed() {
        this.mShowNetworkSpeed.setChecked(MiuiStatusBarManager.isShowNetworkSpeed(getActivity()));
    }

    private void updateShowNotificationIcon() {
        CheckBoxPreference checkBoxPreference = this.mShowNotificationIcon;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(MiuiStatusBarManager.isShowNotificationIcon(getActivity()));
        }
    }

    private void updateToggleCollapseAfterClicked() {
        this.mToggleCollapseAfterClicked.setChecked(MiuiStatusBarManager.isCollapseAfterClicked(getActivity()));
    }

    private void updateUseControlPanel() {
        CheckBoxPreference checkBoxPreference = this.mUseControlPanel;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(StatusBarUtils.isUseControlPanel(getContext()));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiStatusBarSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.status_bar_settings);
        this.mHasMobileDataFeature = ((android.telephony.TelephonyManager) getSystemService("phone")).isDataCapable();
        setupNotificationFold();
        setupManageNotification();
        setupShowNotificationIcon();
        setupShowNetworkSpeed();
        setupShowCarrierUnderKeyguard();
        setupExpandableUnderLockscreen();
        setupUseControlPanel();
        setupCustomCarrier();
        setupBatteryIndicator();
        setupToggleCollapseAfterClicked();
        setupExpandableUnderKeyguard();
        setupNotificationShadeShortcut();
        setupNotificationStyle();
        SmartHomePreferenceManager smartHomePreferenceManager = new SmartHomePreferenceManager(getContext(), false);
        this.mSmartHomePreferenceManager = smartHomePreferenceManager;
        smartHomePreferenceManager.onCreate((DropDownPreference) findPreference("smart_home"));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mSmartHomePreferenceManager.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == this.mNotificationFold) {
            startFragment(this, "com.miui.notification.management.fragment.FoldSettingsFragment", 0, null);
        } else if (preference == this.mCustomCarrier) {
            startFragment(this, "com.android.settings.CarrierNameSettings", 0, (Bundle) null, this.mHasMobileDataFeature ? R.string.custom_carrier_title : R.string.custom_wifi_name_title);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateNotificationFold();
        updateShowNotificationIcon();
        updateShowNetworkSpeed();
        updateShowCarrierUnderKeyguard();
        updateUseControlPanel();
        updateExpandableUnderLockscreen();
        updateCustomCarrier();
        updateBatteryIndicator();
        updateToggleCollapseAfterClicked();
        updateExpandableUnderKeyguard();
        updateNotificationStyle();
        this.mSmartHomePreferenceManager.onResume();
    }
}
