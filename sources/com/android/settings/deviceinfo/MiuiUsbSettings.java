package com.android.settings.deviceinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.os.Build;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class MiuiUsbSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mCharging;
    private boolean mChargingEnable;
    private String mChargingFunctionName;
    private boolean mMassStorageEnable;
    private String mMassStorageFunctionName;
    private CheckBoxPreference mMsd;
    private CheckBoxPreference mMtp;
    private String mMtpFunctionName;
    private CheckBoxPreference mPtp;
    private String mPtpFunctionName;
    private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() { // from class: com.android.settings.deviceinfo.MiuiUsbSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.hardware.usb.action.USB_STATE".equals(intent.getAction()) && !intent.getBooleanExtra(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, false)) {
                MiuiUsbSettings.this.finish();
                return;
            }
            MiuiUsbSettings miuiUsbSettings = MiuiUsbSettings.this;
            UsbManager unused = miuiUsbSettings.mUsbManager;
            miuiUsbSettings.updateToggles(MiCloudStatusInfo.QuotaInfo.WARN_NONE);
        }
    };
    private UsbManager mUsbManager;

    private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        addPreferencesFromResource(R.xml.usb_settings);
        PreferenceScreen preferenceScreen2 = getPreferenceScreen();
        this.mMtp = (CheckBoxPreference) preferenceScreen2.findPreference("usb_mtp");
        this.mPtp = (CheckBoxPreference) preferenceScreen2.findPreference("usb_ptp");
        this.mMsd = (CheckBoxPreference) preferenceScreen2.findPreference("usb_msd");
        this.mCharging = (CheckBoxPreference) preferenceScreen2.findPreference("usb_charging");
        this.mMtp.setOnPreferenceChangeListener(this);
        this.mPtp.setOnPreferenceChangeListener(this);
        this.mMsd.setOnPreferenceChangeListener(this);
        this.mCharging.setOnPreferenceChangeListener(this);
        this.mMtpFunctionName = getResources().getString(R.string.config_mtp_function_name);
        this.mPtpFunctionName = getResources().getString(R.string.config_ptp_function_name);
        this.mMassStorageFunctionName = getResources().getString(R.string.config_mass_storage_function_name);
        this.mChargingFunctionName = getResources().getString(R.string.config_charging_function_name);
        if (Build.IS_MITWO) {
            this.mMsd.setTitle(R.string.usb_msd_driver_installer_title);
            this.mMsd.setSummary(R.string.usb_msd_driver_installer_summary);
        }
        this.mMassStorageEnable = false;
        CheckBoxPreference checkBoxPreference = this.mMsd;
        if (checkBoxPreference != null) {
            preferenceScreen2.removePreference(checkBoxPreference);
            this.mMsd = null;
        }
        boolean z = Build.IS_CM_CUSTOMIZATION_TEST;
        this.mChargingEnable = z;
        CheckBoxPreference checkBoxPreference2 = this.mCharging;
        if (checkBoxPreference2 != null && !z) {
            preferenceScreen2.removePreference(checkBoxPreference2);
            this.mCharging = null;
        }
        return preferenceScreen2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateToggles(String str) {
        this.mMtp.setChecked(this.mMtpFunctionName.equals(str));
        this.mPtp.setChecked(this.mPtpFunctionName.equals(str));
        if (this.mMassStorageEnable) {
            this.mMsd.setChecked(this.mMassStorageFunctionName.equals(str));
        }
        if (this.mChargingEnable) {
            this.mCharging.setChecked(this.mChargingFunctionName.equals(str));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiUsbSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUsbManager = (UsbManager) getSystemService("usb");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mStateReceiver);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (Utils.isMonkeyRunning()) {
            return true;
        }
        if (preference == this.mMtp && ((Boolean) obj).booleanValue()) {
            MiuiUtils.setUsbCurrentFunction(getActivity(), this.mMtpFunctionName, true);
            updateToggles(this.mMtpFunctionName);
        } else if (preference == this.mPtp && ((Boolean) obj).booleanValue()) {
            MiuiUtils.setUsbCurrentFunction(getActivity(), this.mPtpFunctionName, true);
            updateToggles(this.mPtpFunctionName);
        } else if (preference == this.mMsd && this.mMassStorageEnable) {
            MiuiUtils.setUsbCurrentFunction(getActivity(), this.mMassStorageFunctionName, true);
            updateToggles(this.mMassStorageFunctionName);
        } else if (preference == this.mCharging && this.mChargingEnable) {
            MiuiUtils.setUsbCurrentFunction(getActivity(), this.mChargingFunctionName, true);
            updateToggles(this.mChargingFunctionName);
        } else {
            MiuiUtils.setUsbCurrentFunction(getActivity(), MiCloudStatusInfo.QuotaInfo.WARN_NONE, false);
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        CheckBoxPreference checkBoxPreference;
        if (Utils.isMonkeyRunning()) {
            return true;
        }
        CheckBoxPreference checkBoxPreference2 = this.mMtp;
        if ((preference != checkBoxPreference2 || !checkBoxPreference2.isChecked()) && ((preference != (checkBoxPreference = this.mPtp) || !checkBoxPreference.isChecked()) && ((preference != this.mMsd || !this.mMassStorageEnable) && (preference != this.mCharging || !this.mChargingEnable)))) {
            MiuiUtils.setUsbCurrentFunction(getActivity(), MiCloudStatusInfo.QuotaInfo.WARN_NONE, false);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        createPreferenceHierarchy();
        getActivity().registerReceiver(this.mStateReceiver, new IntentFilter("android.hardware.usb.action.USB_STATE"));
    }
}
