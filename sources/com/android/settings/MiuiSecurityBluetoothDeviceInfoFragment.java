package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.password.ChooseLockSettingsHelper;
import miui.bluetooth.ble.MiBleProfile;
import miui.bluetooth.ble.MiBleUnlockProfile;
import miui.provider.ExtraTelephony;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiSecurityBluetoothDeviceInfoFragment extends SettingsPreferenceFragment {
    private static final String TAG = MiuiSecurityBluetoothDeviceInfoFragment.class.getSimpleName();
    private ImageView mBluetoothDeviceConfirmed;
    private ImageView mBluetoothDeviceDefault;
    private TextView mDeviceStatus;
    private MiBleUnlockProfile mUnlockProfile;
    private MiuiLockPatternUtils mLockPatternUtils = null;
    private boolean mIsConnected = false;
    private String mDeviceMajorClass = "";
    private String mDeviceMinorClass = "";
    private String mDeviceType = "";
    private MiBleUnlockProfile.OnUnlockStateChangeListener mUnlockListener = new MiBleUnlockProfile.OnUnlockStateChangeListener() { // from class: com.android.settings.MiuiSecurityBluetoothDeviceInfoFragment.1
        @Override // miui.bluetooth.ble.MiBleUnlockProfile.OnUnlockStateChangeListener
        public void onUnlocked(byte b) {
            Log.d(MiuiSecurityBluetoothDeviceInfoFragment.TAG, "onUnlocked: " + ((int) b));
            if (MiuiSecurityBluetoothDeviceInfoFragment.this.isAdded()) {
                if (b == 2) {
                    if (MiuiSecurityBluetoothDeviceInfoFragment.this.mBluetoothDeviceConfirmed.getVisibility() != 0) {
                        MiuiSecurityBluetoothDeviceInfoFragment.this.mBluetoothDeviceConfirmed.setVisibility(0);
                    }
                } else if (MiuiSecurityBluetoothDeviceInfoFragment.this.mBluetoothDeviceConfirmed.getVisibility() != 8) {
                    MiuiSecurityBluetoothDeviceInfoFragment.this.mBluetoothDeviceConfirmed.setVisibility(8);
                }
                if (!MiuiSecurityBluetoothDeviceInfoFragment.this.mIsConnected) {
                    MiuiSecurityBluetoothDeviceInfoFragment.this.mDeviceStatus.setText(R.string.bluetooth_unlock_state_disconnected);
                } else if (b == 2) {
                    MiuiSecurityBluetoothDeviceInfoFragment.this.mDeviceStatus.setText(R.string.bluetooth_unlock_state_ok);
                } else if (b == 1) {
                    MiuiSecurityBluetoothDeviceInfoFragment.this.mDeviceStatus.setText(R.string.bluetooth_unlock_state_too_far);
                } else {
                    MiuiSecurityBluetoothDeviceInfoFragment.this.mDeviceStatus.setText(R.string.bluetooth_unlock_state_key_error);
                }
            }
        }
    };
    AsyncTask<Void, Void, Boolean> mSetUnlockLevelTask = null;

    private void cancelRuningSetUnlockLevelTask() {
        AsyncTask<Void, Void, Boolean> asyncTask = this.mSetUnlockLevelTask;
        if (asyncTask != null) {
            if (asyncTask.getStatus() == AsyncTask.Status.RUNNING || this.mSetUnlockLevelTask.getStatus() == AsyncTask.Status.PENDING) {
                this.mSetUnlockLevelTask.cancel(true);
            }
        }
    }

    private void loadDeviceInfo(String str) {
        String str2 = "";
        try {
            str2 = Settings.Global.getString(getContext().getContentResolver(), "com.xiaomi.bluetooth.UNLOCK_DEVICE");
            if (TextUtils.isEmpty(str2) || str2.indexOf(str) == -1) {
                str2 = Settings.Global.getString(getContext().getContentResolver(), "com.xiaomi.bluetooth.UNLOCK_DEVICE_DIRECT");
                if (TextUtils.isEmpty(str2)) {
                    return;
                }
                if (str2.indexOf(str) == -1) {
                    return;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "get Device type failed " + e);
        }
        String[] split = str2.split("\\,");
        if (split == null || split.length != 4) {
            return;
        }
        this.mDeviceMajorClass = split[2];
        this.mDeviceMinorClass = split[3];
        this.mDeviceType = split[1];
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiSecurityBluetoothDeviceInfoFragment.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 100 && i2 == -1) {
            getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock.disable"));
            this.mLockPatternUtils.setBluetoothUnlockEnabled(false);
            this.mLockPatternUtils.setBluetoothAddressToUnlock("");
            this.mLockPatternUtils.setBluetoothNameToUnlock("");
            this.mLockPatternUtils.setBluetoothKeyToUnlock("");
            finish();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MiuiLockPatternUtils miuiLockPatternUtils = new MiuiLockPatternUtils(getActivity());
        this.mLockPatternUtils = miuiLockPatternUtils;
        String bluetoothAddressToUnlock = miuiLockPatternUtils.getBluetoothAddressToUnlock();
        if (TextUtils.isEmpty(bluetoothAddressToUnlock)) {
            return;
        }
        this.mUnlockProfile = new MiBleUnlockProfile(getActivity(), bluetoothAddressToUnlock, new MiBleProfile.IProfileStateChangeCallback() { // from class: com.android.settings.MiuiSecurityBluetoothDeviceInfoFragment.2
            @Override // miui.bluetooth.ble.MiBleProfile.IProfileStateChangeCallback
            public void onState(int i) {
                Log.d(MiuiSecurityBluetoothDeviceInfoFragment.TAG, "onConnectionState: " + i);
                if (i != 4) {
                    MiuiSecurityBluetoothDeviceInfoFragment.this.mIsConnected = false;
                    return;
                }
                MiuiSecurityBluetoothDeviceInfoFragment.this.mUnlockProfile.registerUnlockListener(MiuiSecurityBluetoothDeviceInfoFragment.this.mUnlockListener);
                MiuiSecurityBluetoothDeviceInfoFragment.this.mIsConnected = true;
            }
        });
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.security_bluetooth_device_info, (ViewGroup) null);
        Button button = (Button) inflate.findViewById(R.id.delete_this_device);
        TextView textView = (TextView) inflate.findViewById(R.id.device_name_value_id);
        this.mBluetoothDeviceDefault = (ImageView) inflate.findViewById(R.id.bluetooth_device_default);
        this.mBluetoothDeviceConfirmed = (ImageView) inflate.findViewById(R.id.bluetooth_device_confirmed);
        String bluetoothNameToUnlock = this.mLockPatternUtils.getBluetoothNameToUnlock();
        String bluetoothAddressToUnlock = this.mLockPatternUtils.getBluetoothAddressToUnlock();
        loadDeviceInfo(bluetoothAddressToUnlock);
        String str = TAG;
        Log.e(str, "device info " + this.mDeviceType + this.mDeviceMajorClass + this.mDeviceMinorClass);
        if (TextUtils.isEmpty(this.mDeviceMajorClass) || !"1".equals(this.mDeviceMajorClass)) {
            if (TextUtils.isEmpty(bluetoothNameToUnlock)) {
                bluetoothNameToUnlock = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothAddressToUnlock).getAddress();
            }
            if ("MI Band 2".equals(bluetoothNameToUnlock) || "Mi Band 3".equals(bluetoothNameToUnlock)) {
                this.mBluetoothDeviceDefault.setImageResource(R.drawable.bluetooth_device_unlock_default_for_miband2);
                this.mBluetoothDeviceConfirmed.setImageResource(R.drawable.bluetooth_device_unlock_confirmed_for_miband2);
            } else if (bluetoothNameToUnlock != null && bluetoothNameToUnlock.startsWith("Amazfit Watch")) {
                this.mBluetoothDeviceDefault.setImageResource(R.drawable.bluetooth_device_unlock_default_for_huami_watch);
                this.mBluetoothDeviceConfirmed.setImageResource(R.drawable.bluetooth_device_unlock_confirmed_for_huami_watch);
            }
        } else {
            Log.d(str, "" + this.mDeviceMajorClass + this.mDeviceMinorClass);
            if ("1".equals(this.mDeviceMinorClass) || "2".equals(this.mDeviceMinorClass)) {
                this.mBluetoothDeviceDefault.setImageResource(R.drawable.unlock_01_ungranted);
                this.mBluetoothDeviceConfirmed.setImageResource(R.drawable.unlock_01_granted);
            } else if (ExtraTelephony.Phonelist.TYPE_VIP.equals(this.mDeviceMinorClass)) {
                this.mBluetoothDeviceDefault.setImageResource(R.drawable.unlock_03_ungranted);
                this.mBluetoothDeviceConfirmed.setImageResource(R.drawable.unlock_03_granted);
            } else if (ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK.equals(this.mDeviceMinorClass)) {
                ImageView imageView = this.mBluetoothDeviceDefault;
                int i = R.drawable.unlock_04_granted;
                imageView.setImageResource(i);
                this.mBluetoothDeviceConfirmed.setImageResource(i);
            }
        }
        if (TextUtils.isEmpty(bluetoothNameToUnlock)) {
            textView.setText(String.format("%s", bluetoothAddressToUnlock));
        } else {
            textView.setText(String.format("%s(%s)", bluetoothNameToUnlock, bluetoothAddressToUnlock));
        }
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiSecurityBluetoothDeviceInfoFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiSecurityBluetoothDeviceInfoFragment.3.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        if (i2 == -1) {
                            new ChooseLockSettingsHelper.Builder(MiuiSecurityBluetoothDeviceInfoFragment.this.getActivity(), MiuiSecurityBluetoothDeviceInfoFragment.this).setRequestCode(100).build().launch();
                        }
                        dialogInterface.dismiss();
                    }
                };
                AlertDialog create = new AlertDialog.Builder(MiuiSecurityBluetoothDeviceInfoFragment.this.getActivity()).setMessage(R.string.bluetooth_unlock_delete_device_confirm_msg).setNegativeButton(17039360, onClickListener).setPositiveButton(17039370, onClickListener).setCancelable(true).create();
                create.setCanceledOnTouchOutside(false);
                create.show();
            }
        });
        this.mDeviceStatus = (TextView) inflate.findViewById(R.id.device_status);
        super.onCreateView(layoutInflater, viewGroup, bundle);
        return inflate;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        cancelRuningSetUnlockLevelTask();
        MiBleUnlockProfile miBleUnlockProfile = this.mUnlockProfile;
        if (miBleUnlockProfile != null) {
            miBleUnlockProfile.unregisterUnlockListener();
            this.mUnlockProfile.disconnect();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        MiBleUnlockProfile miBleUnlockProfile = this.mUnlockProfile;
        if (miBleUnlockProfile != null) {
            miBleUnlockProfile.connect();
        }
    }
}
