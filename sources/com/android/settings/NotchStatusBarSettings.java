package com.android.settings;

import android.app.MiuiStatusBarManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.utils.StatusBarUtils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miuix.preference.DropDownPreference;

/* loaded from: classes.dex */
public class NotchStatusBarSettings extends BaseSettingsPreferenceFragment {
    private String[] mBatteryEntries;
    private DropDownPreference mBatteryIndicator;
    private ValuePreference mCustomCarrier;
    private Preference mCutoutMode;
    private CheckBoxPreference mCutoutType;
    private boolean mHasMobileDataFeature;
    private PreferenceCategory mNotchCategory;
    private CheckBoxPreference mNotchForceBlack;
    private Preference mNotchStyleMode;
    private ListPreference mNotificationShadeShortcut;
    private CheckBoxPreference mNotificationUseAppIcon;
    private CheckBoxPreference mShowCarrierUnderKeyguard;
    private CheckBoxPreference mShowLTEFor4G;
    private CheckBoxPreference mShowNetworkSpeed;
    private CheckBoxPreference mShowNotificationIcon;

    private boolean isDripType() {
        return Settings.Global.getInt(getContentResolver(), "overlay_drip", 1) == 1;
    }

    private boolean isForceBlack() {
        return MiuiSettings.Global.getBoolean(getContentResolver(), "force_black");
    }

    private boolean isForceBlackV2() {
        return MiuiSettings.Global.getBoolean(getContentResolver(), "force_black_v2");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDripType(boolean z) {
        Settings.Global.putInt(getContentResolver(), "overlay_drip", z ? 1 : 0);
    }

    private void setForceBlack(boolean z) {
        MiuiSettings.Global.putBoolean(getContentResolver(), "force_black", z);
    }

    private void setupBatteryIndicator() {
        TypedArray obtainTypedArray;
        this.mBatteryIndicator = (DropDownPreference) findPreference("battery_indicator");
        String[] stringArray = getResources().getStringArray(R.array.battery_indicator_style_entries);
        this.mBatteryEntries = stringArray;
        if (StatusBarUtils.IS_NOTCH || StatusBarUtils.IS_FOLD) {
            String[] stringArray2 = getResources().getStringArray(R.array.notch_battery_indicator_style_entries);
            this.mBatteryEntries = stringArray2;
            int length = stringArray2.length;
            String[] strArr = new String[length];
            for (int i = 0; i < length; i++) {
                strArr[i] = "";
            }
            obtainTypedArray = getResources().obtainTypedArray(R.array.notch_battery_indicator_style_icons);
            this.mBatteryIndicator.setEntries(strArr);
            this.mBatteryIndicator.setEntryValues(getResources().getStringArray(R.array.notch_battery_indicator_style_values));
        } else {
            int length2 = stringArray.length;
            String[] strArr2 = new String[length2];
            for (int i2 = 0; i2 < length2; i2++) {
                strArr2[i2] = "";
            }
            obtainTypedArray = getResources().obtainTypedArray(R.array.battery_indicator_style_icons);
            this.mBatteryIndicator.setEntries(strArr2);
            this.mBatteryIndicator.setEntryValues(getResources().getStringArray(R.array.battery_indicator_style_values));
        }
        int length3 = obtainTypedArray.length();
        int[] iArr = new int[length3];
        for (int i3 = 0; i3 < length3; i3++) {
            iArr[i3] = obtainTypedArray.getResourceId(i3, 0);
        }
        obtainTypedArray.recycle();
        this.mBatteryIndicator.setEntryIcons(iArr);
        this.mBatteryIndicator.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.NotchStatusBarSettings.5
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Settings.System.putInt(NotchStatusBarSettings.this.getContentResolver(), "battery_indicator_style", com.android.settings.utils.Utils.parseInt((String) obj));
                NotchStatusBarSettings.this.updateBatteryIndicator();
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

    private void setupNotch() {
        Preference preference;
        this.mNotchCategory = (PreferenceCategory) getPreferenceScreen().findPreference("settings_notch");
        this.mNotchStyleMode = getPreferenceScreen().findPreference("notch_style_mode");
        this.mNotchForceBlack = (CheckBoxPreference) getPreferenceScreen().findPreference("notch_force_black");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("cutout_type");
        this.mCutoutType = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.NotchStatusBarSettings.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference2, Object obj) {
                NotchStatusBarSettings.this.setDripType(((Boolean) obj).booleanValue());
                return true;
            }
        });
        this.mCutoutMode = getPreferenceScreen().findPreference("cutout_mode");
        boolean z = StatusBarUtils.IS_NOTCH;
        if (!z || Build.VERSION.SDK_INT < 28 || "odin".equals(Build.DEVICE)) {
            this.mNotchCategory.removePreference(this.mNotchStyleMode);
            this.mNotchStyleMode = null;
        }
        if (!z || Build.VERSION.SDK_INT >= 28) {
            this.mNotchCategory.removePreference(this.mNotchForceBlack);
            this.mNotchForceBlack = null;
        }
        if (!com.android.settings.utils.Utils.supportOverlayRoundedCorner()) {
            this.mNotchCategory.removePreference(this.mCutoutType);
            this.mCutoutType = null;
        }
        if (!com.android.settings.utils.Utils.supportCutoutMode() && (preference = this.mCutoutMode) != null) {
            this.mNotchCategory.removePreference(preference);
            this.mCutoutMode = null;
        }
        if (!z || "odin".equals(Build.DEVICE)) {
            getPreferenceScreen().removePreference(this.mNotchCategory);
        }
    }

    private void setupNotificationShadeShortcut() {
        this.mNotificationShadeShortcut = (ListPreference) findPreference("notification_shade_shortcut");
        ((PreferenceCategory) findPreference("settings_status_bar")).removePreference(this.mNotificationShadeShortcut);
        this.mNotificationShadeShortcut = null;
    }

    private void setupNotificationUseAppIcon() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("notification_use_app_icon");
        this.mNotificationUseAppIcon = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.NotchStatusBarSettings.6
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setNotificationUseAppIcon(NotchStatusBarSettings.this.getActivity(), ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupShowCarrierUnderKeyguard() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("show_carrier_under_keyguard");
        this.mShowCarrierUnderKeyguard = checkBoxPreference;
        if (StatusBarUtils.IS_MX_TELCEL) {
            ((PreferenceCategory) findPreference("settings_status_bar")).removePreference(this.mShowCarrierUnderKeyguard);
            this.mShowCarrierUnderKeyguard = null;
            return;
        }
        if (!this.mHasMobileDataFeature) {
            checkBoxPreference.setTitle(getResources().getString(R.string.show_wifi_name_title));
        }
        this.mShowCarrierUnderKeyguard.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.NotchStatusBarSettings.4
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Settings.System.putInt(NotchStatusBarSettings.this.getContentResolver(), "status_bar_show_carrier_under_keyguard", ((Boolean) obj).booleanValue() ? 1 : 0);
                return true;
            }
        });
    }

    private void setupShowLTEFor4G() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("show_lte_for_4g");
        this.mShowLTEFor4G = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.NotchStatusBarSettings.7
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                StatusBarUtils.setShowLTEFor4G(NotchStatusBarSettings.this.getActivity(), ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupShowNetworkSpeed() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("show_network_speed");
        this.mShowNetworkSpeed = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.NotchStatusBarSettings.3
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                MiuiStatusBarManager.setShowNetworkSpeed(NotchStatusBarSettings.this.getActivity(), ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupShowNotificationIcon() {
        this.mShowNotificationIcon = (CheckBoxPreference) findPreference("show_notification_icon");
        if (!StatusBarUtils.isMiuiOptimizationOff(getActivity())) {
            this.mShowNotificationIcon.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.NotchStatusBarSettings.2
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    MiuiStatusBarManager.setShowNotificationIcon(NotchStatusBarSettings.this.getActivity(), ((Boolean) obj).booleanValue());
                    return true;
                }
            });
            return;
        }
        ((PreferenceCategory) findPreference("settings_status_bar")).removePreference(this.mShowNotificationIcon);
        this.mShowNotificationIcon = null;
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

    /* JADX WARN: Code restructure failed: missing block: B:13:0x004c, code lost:
    
        if (android.text.TextUtils.isEmpty(r4) != false) goto L14;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateCustomCarrier() {
        /*
            r6 = this;
            com.android.settingslib.miuisettings.preference.ValuePreference r0 = r6.mCustomCarrier
            if (r0 == 0) goto L67
            android.content.res.Resources r0 = r6.getResources()
            int r1 = com.android.settings.R.string.none
            java.lang.String r0 = r0.getString(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            boolean r2 = com.android.settings.utils.StatusBarUtils.IS_CUST_SINGLE_SIM
            if (r2 == 0) goto L19
            r2 = 1
            goto L21
        L19:
            miui.telephony.TelephonyManager r2 = miui.telephony.TelephonyManager.getDefault()
            int r2 = r2.getPhoneCount()
        L21:
            r3 = 0
        L22:
            if (r3 >= r2) goto L5e
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "status_bar_custom_carrier"
            r4.append(r5)
            r4.append(r3)
            java.lang.String r4 = r4.toString()
            miui.telephony.TelephonyManager r5 = miui.telephony.TelephonyManager.getDefault()
            boolean r5 = r5.hasIccCard(r3)
            if (r5 == 0) goto L4e
            android.content.ContentResolver r5 = r6.getContentResolver()
            java.lang.String r4 = android.provider.MiuiSettings.System.getString(r5, r4, r0)
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 == 0) goto L4f
        L4e:
            r4 = r0
        L4f:
            r1.append(r4)
            int r4 = r2 + (-1)
            if (r3 == r4) goto L5b
            java.lang.String r4 = " | "
            r1.append(r4)
        L5b:
            int r3 = r3 + 1
            goto L22
        L5e:
            com.android.settingslib.miuisettings.preference.ValuePreference r6 = r6.mCustomCarrier
            java.lang.String r0 = r1.toString()
            r6.setValue(r0)
        L67:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.NotchStatusBarSettings.updateCustomCarrier():void");
    }

    private void updateNotch() {
        if (this.mNotchStyleMode != null) {
            boolean z = isForceBlack() || isForceBlackV2();
            Preference preference = this.mCutoutMode;
            if (preference != null) {
                preference.setEnabled(!z);
            }
            CheckBoxPreference checkBoxPreference = this.mCutoutType;
            if (checkBoxPreference != null) {
                checkBoxPreference.setEnabled(!z);
            }
        }
        CheckBoxPreference checkBoxPreference2 = this.mNotchForceBlack;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setChecked(isForceBlack());
            Preference preference2 = this.mCutoutMode;
            if (preference2 != null) {
                preference2.setEnabled(!isForceBlack());
            }
            CheckBoxPreference checkBoxPreference3 = this.mCutoutType;
            if (checkBoxPreference3 != null) {
                checkBoxPreference3.setEnabled(true ^ isForceBlack());
            }
        }
        CheckBoxPreference checkBoxPreference4 = this.mCutoutType;
        if (checkBoxPreference4 != null) {
            checkBoxPreference4.setChecked(isDripType());
        }
    }

    private void updateNotificationUseAppIcon() {
        this.mNotificationUseAppIcon.setChecked(StatusBarUtils.isNotificationUseAppIcon(getActivity()));
    }

    private void updateShowCarrierUnderKeyguard() {
        CheckBoxPreference checkBoxPreference = this.mShowCarrierUnderKeyguard;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(Settings.System.getInt(getContentResolver(), "status_bar_show_carrier_under_keyguard", 1) == 1);
        }
    }

    private void updateShowLTEFor4G() {
        this.mShowLTEFor4G.setChecked(StatusBarUtils.isShowLTEFor4G(getActivity()));
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

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiStatusBarSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.notch_status_bar_settings);
        getPreferenceScreen().setTitle(R.string.status_bar_settings_status_bar);
        this.mHasMobileDataFeature = ((TelephonyManager) getSystemService("phone")).isDataCapable();
        setupNotch();
        setupShowNotificationIcon();
        setupShowNetworkSpeed();
        setupShowCarrierUnderKeyguard();
        setupCustomCarrier();
        setupBatteryIndicator();
        setupNotificationShadeShortcut();
        setupNotificationUseAppIcon();
        setupShowLTEFor4G();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == this.mCustomCarrier) {
            startFragment(this, "com.android.settings.CarrierNameSettings", 0, (Bundle) null, this.mHasMobileDataFeature ? R.string.custom_carrier_title : R.string.custom_wifi_name_title);
        } else {
            CheckBoxPreference checkBoxPreference = this.mNotchForceBlack;
            if (preference == checkBoxPreference) {
                setForceBlack(checkBoxPreference.isChecked());
                Preference preference2 = this.mCutoutMode;
                if (preference2 != null) {
                    preference2.setEnabled(!isForceBlack());
                }
                CheckBoxPreference checkBoxPreference2 = this.mCutoutType;
                if (checkBoxPreference2 != null) {
                    checkBoxPreference2.setEnabled(!isForceBlack());
                }
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateNotch();
        updateShowNotificationIcon();
        updateShowNetworkSpeed();
        updateShowCarrierUnderKeyguard();
        updateCustomCarrier();
        updateBatteryIndicator();
        updateNotificationUseAppIcon();
        updateShowLTEFor4G();
    }
}
