package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.CheckBoxPreference;

/* loaded from: classes.dex */
public final class MiuiHeadsetPreferenceConfig {
    private CheckBoxPreference mAutoAck;
    private BluetoothDevice mDevice;
    private CheckBoxPreference mInearTest;

    public MiuiHeadsetPreferenceConfig(BluetoothDevice bluetoothDevice, CheckBoxPreference checkBoxPreference, CheckBoxPreference checkBoxPreference2) {
        this.mDevice = bluetoothDevice;
        this.mInearTest = checkBoxPreference;
        this.mAutoAck = checkBoxPreference2;
    }

    public void clearSharedPreferencesConfig(Activity activity) {
        if (activity != null) {
            try {
                SharedPreferences.Editor edit = activity.getSharedPreferences("sharedpreConfig", 0).edit();
                if (edit != null) {
                    edit.clear();
                    edit.commit();
                }
            } catch (Exception e) {
                Log.e("MiuiHeadsetPreferenceConfig", "error" + e);
            }
        }
    }

    public void initPreferenceConfig(Activity activity) {
        BluetoothDevice bluetoothDevice;
        if (activity != null) {
            try {
                SharedPreferences sharedPreferences = activity.getSharedPreferences("sharedpreConfig", 0);
                if (sharedPreferences == null || (bluetoothDevice = this.mDevice) == null) {
                    return;
                }
                String address = bluetoothDevice.getAddress();
                String str = address + "AutoAckModePref";
                String str2 = address + "InEarTestPref";
                if (sharedPreferences.contains(str) && this.mAutoAck != null) {
                    this.mAutoAck.setChecked(Boolean.valueOf(sharedPreferences.getBoolean(str, false)).booleanValue());
                }
                if (!sharedPreferences.contains(str2) || this.mInearTest == null) {
                    return;
                }
                this.mInearTest.setChecked(Boolean.valueOf(sharedPreferences.getBoolean(str2, false)).booleanValue());
            } catch (Exception e) {
                Log.e("MiuiHeadsetPreferenceConfig", "error " + e);
            }
        }
    }

    public void setSharedPref(Boolean bool, String str, Activity activity) {
        SharedPreferences.Editor edit;
        try {
            BluetoothDevice bluetoothDevice = this.mDevice;
            if (bluetoothDevice == null || str == null || activity == null) {
                return;
            }
            String str2 = bluetoothDevice.getAddress() + str;
            SharedPreferences sharedPreferences = activity.getSharedPreferences("sharedpreConfig", 0);
            if (sharedPreferences == null || (edit = sharedPreferences.edit()) == null) {
                return;
            }
            edit.putBoolean(str2, bool.booleanValue());
            edit.apply();
        } catch (Exception e) {
            Log.e("MiuiHeadsetPreferenceConfig", "error" + e);
        }
    }
}
