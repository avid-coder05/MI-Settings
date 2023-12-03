package com.android.settings;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.password.ChooseLockSettingsHelper;
import miui.bluetooth.ble.MiBleProfile;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiSecurityBluetoothSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, FragmentResultCallBack {
    private ChooseLockSettingsHelper.Builder mBuilder;
    private MiuiLockPatternUtils mLockPatternUtils = null;

    private Preference buildPreference(String str, int i) {
        return buildPreference(str, getResources().getString(i));
    }

    private Preference buildPreference(String str, String str2) {
        Preference preference = new Preference(getPrefContext());
        preference.setKey(str);
        preference.setTitle(str2);
        return preference;
    }

    private void startBleDevicePickerActivity(int i) {
        Intent intent = new Intent(MiBleProfile.ACTION_SELECT_DEVICE);
        intent.putExtra(MiBleProfile.EXTRA_MIBLE_PROPERTY, 1);
        intent.addCategory("android.intent.category.DEFAULT");
        startActivityForResult(intent, i);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiSecurityBluetoothSettingsFragment.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        BluetoothDevice bluetoothDevice;
        super.onActivityResult(i, i2, intent);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("unlock_device_by_bluetooth");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("bluetooth_devices");
        if (i == 100 && i2 == -1) {
            if (this.mLockPatternUtils.getBluetoothUnlockEnabled()) {
                getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock.disable"));
                this.mLockPatternUtils.setBluetoothUnlockEnabled(false);
                checkBoxPreference.setChecked(false);
                preferenceCategory.setEnabled(false);
            } else if (TextUtils.isEmpty(this.mLockPatternUtils.getBluetoothAddressToUnlock())) {
                startBleDevicePickerActivity(102);
            } else {
                this.mLockPatternUtils.setBluetoothUnlockEnabled(true);
                checkBoxPreference.setChecked(true);
                preferenceCategory.setEnabled(true);
                getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock"));
            }
        } else if (i == 101 && i2 == -1) {
            startBleDevicePickerActivity(103);
        } else if ((i != 102 && i != 103) || i2 != -1) {
            if (i == 104 && i2 == -1) {
                this.mLockPatternUtils.setBluetoothUnlockEnabled(true);
                checkBoxPreference.setChecked(true);
                preferenceCategory.setEnabled(true);
            }
        } else if (intent == null || (bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE")) == null || TextUtils.isEmpty(bluetoothDevice.getAddress())) {
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("device_address", bluetoothDevice.getAddress());
            String stringExtra = intent.getStringExtra("DEVICE_TYPE");
            String stringExtra2 = intent.getStringExtra("DEVICE_TYPE_MAJOR");
            String stringExtra3 = intent.getStringExtra("DEVICE_TYPE_MINOR");
            Log.d("MiuiSecurityBluetoothSettingsFragment", "device info = " + stringExtra + stringExtra2 + stringExtra3);
            bundle.putString("DEVICE_TYPE", stringExtra);
            bundle.putString("DEVICE_TYPE_MAJOR", stringExtra2);
            bundle.putString("DEVICE_TYPE_MINOR", stringExtra3);
            startFragment(this, MiuiSecurityBluetoothMatchDeviceFragment.class.getName(), 104, bundle);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mLockPatternUtils = new MiuiLockPatternUtils(getActivity());
        this.mBuilder = new ChooseLockSettingsHelper.Builder(getActivity(), this);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        addPreferencesFromResource(R.xml.security_settings_bluetooth);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onFragmentResult(int i, Bundle bundle) {
        if (i == 100 && bundle != null && bundle.getInt("miui_security_fragment_result") == 0) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("unlock_device_by_bluetooth");
            PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("bluetooth_devices");
            if (this.mLockPatternUtils.getBluetoothUnlockEnabled()) {
                getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock.disable"));
                this.mLockPatternUtils.setBluetoothUnlockEnabled(false);
                checkBoxPreference.setChecked(false);
                preferenceCategory.setEnabled(false);
            } else if (TextUtils.isEmpty(this.mLockPatternUtils.getBluetoothAddressToUnlock())) {
                startBleDevicePickerActivity(102);
            } else {
                this.mLockPatternUtils.setBluetoothUnlockEnabled(true);
                checkBoxPreference.setChecked(true);
                preferenceCategory.setEnabled(true);
                getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock"));
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("unlock_device_by_bluetooth".equals(preference.getKey())) {
            this.mBuilder.setRequestCode(100).build().launch();
            return true;
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if ("matched_device".equals(key)) {
            startFragment(this, MiuiSecurityBluetoothDeviceInfoFragment.class.getName(), -1, null);
        } else if ("change_matched_device".equals(key)) {
            AlertDialog create = new AlertDialog.Builder(getActivity()).create();
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiSecurityBluetoothSettingsFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == -1) {
                        MiuiSecurityBluetoothSettingsFragment.this.mBuilder.setRequestCode(101).build().launch();
                    }
                    dialogInterface.dismiss();
                }
            };
            create.setCancelable(true);
            create.setCanceledOnTouchOutside(false);
            create.setMessage(getResources().getString(R.string.bluetooth_unlock_change_device_confirm_msg));
            create.setButton(-2, getResources().getString(17039360), onClickListener);
            create.setButton(-1, getResources().getString(17039370), onClickListener);
            create.show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("unlock_device_by_bluetooth");
        checkBoxPreference.setOnPreferenceChangeListener(this);
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("bluetooth_devices");
        preferenceCategory.removeAll();
        if (TextUtils.isEmpty(this.mLockPatternUtils.getBluetoothAddressToUnlock())) {
            preferenceCategory.addPreference(buildPreference("no_matched_device", R.string.bluetooth_unlock_no_matched_device_yet));
        } else {
            String bluetoothNameToUnlock = this.mLockPatternUtils.getBluetoothNameToUnlock();
            String bluetoothAddressToUnlock = this.mLockPatternUtils.getBluetoothAddressToUnlock();
            if (!TextUtils.isEmpty(bluetoothNameToUnlock)) {
                bluetoothAddressToUnlock = bluetoothNameToUnlock + "(" + bluetoothAddressToUnlock.substring(bluetoothAddressToUnlock.length() - 5, bluetoothAddressToUnlock.length() - 3) + bluetoothAddressToUnlock.substring(bluetoothAddressToUnlock.length() - 2) + ")";
            }
            preferenceCategory.addPreference(buildPreference("matched_device", bluetoothAddressToUnlock));
            preferenceCategory.addPreference(buildPreference("change_matched_device", R.string.bluetooth_unlock_change_matched_device));
        }
        if (this.mLockPatternUtils.getBluetoothUnlockEnabled()) {
            checkBoxPreference.setChecked(true);
            preferenceCategory.setEnabled(true);
            return;
        }
        checkBoxPreference.setChecked(false);
        preferenceCategory.setEnabled(false);
    }
}
