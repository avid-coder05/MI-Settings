package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.password.PasswordUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;

/* loaded from: classes.dex */
public class DevicePickerFragment extends DeviceListPreferenceFragment {
    PreferenceCategory mAvailableDevicesCategory;
    String mCallingAppPackageName;
    Context mContext;
    String mLaunchClass;
    String mLaunchPackage;
    private boolean mNeedAuth;
    private boolean mScanAllowed;

    public DevicePickerFragment() {
        super(null);
    }

    private void sendDevicePickedIntent(BluetoothDevice bluetoothDevice) {
        Log.w("DevicePickerFragment", "sendDevicePickedIntent");
        Intent intent = new Intent("android.bluetooth.devicepicker.action.DEVICE_SELECTED");
        intent.putExtra("android.bluetooth.device.extra.DEVICE", bluetoothDevice);
        String str = this.mLaunchPackage;
        if (str != null && this.mLaunchClass != null && TextUtils.equals(this.mCallingAppPackageName, str)) {
            intent.setClassName(this.mLaunchPackage, this.mLaunchClass);
        }
        this.mContext.sendBroadcast(intent, "android.permission.BLUETOOTH_CONNECT");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return null;
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public String getDeviceListKey() {
        return "bt_device_list";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DevicePickerFragment";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 25;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return DevicePickerFragment.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.device_picker;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initDevicePreference(BluetoothDevicePreference bluetoothDevicePreference) {
        super.initDevicePreference(bluetoothDevicePreference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initPreferencesFromPreferenceScreen() {
        addPreferencesFromResource(R.xml.device_picker);
        Intent intent = getActivity().getIntent();
        this.mNeedAuth = intent.getBooleanExtra("android.bluetooth.devicepicker.extra.NEED_AUTH", false);
        setFilter(intent.getIntExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 0));
        this.mLaunchPackage = intent.getStringExtra("android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE");
        this.mLaunchClass = intent.getStringExtra("android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS");
        this.mAvailableDevicesCategory = (PreferenceCategory) findPreference("bt_device_list");
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        super.onBluetoothStateChanged(i);
        if (i == 12) {
            enableScanning();
        }
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(getString(R.string.device_picker));
        this.mScanAllowed = !((UserManager) getSystemService("user")).hasUserRestriction("no_config_bluetooth");
        String callingAppPackageName = PasswordUtils.getCallingAppPackageName(getActivity().getActivityToken());
        this.mCallingAppPackageName = callingAppPackageName;
        if (!TextUtils.equals(callingAppPackageName, this.mLaunchPackage)) {
            Log.w("DevicePickerFragment", "sendDevicePickedIntent() launch package name is not equivalent to calling package name!");
        }
        this.mContext = getContext();
        this.mLocalAdapter.setName(MiuiBTUtils.isCustomizedOperator() ? MiuiBTUtils.getBluetoothName() : MiuiSettings.System.getDeviceName(getActivity()));
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mSelectedDevice == null) {
            sendDevicePickedIntent(null);
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (device.equals(this.mSelectedDevice)) {
            if (i == 12) {
                sendDevicePickedIntent(device);
                finish();
            } else if (i == 10) {
                enableScanning();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        disableScanning();
        LocalBluetoothPreferences.persistSelectedDeviceInPicker(getActivity(), this.mSelectedDevice.getAddress());
        if (bluetoothDevicePreference.getCachedDevice().getBondState() != 12 && this.mNeedAuth) {
            super.onDevicePreferenceClick(bluetoothDevicePreference);
            return;
        }
        sendDevicePickedIntent(this.mSelectedDevice);
        finish();
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
        updateProgressUi(z);
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mLocalManager == null || isUiRestricted()) {
            return;
        }
        this.mLocalManager.getEventManager().registerCallback(this);
        addCachedDevices();
        this.mSelectedDevice = null;
        if (this.mScanAllowed) {
            enableScanning();
            updateProgressUi(this.mLocalAdapter.isDiscovering());
        }
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        disableScanning();
        super.onStop();
        if (this.mLocalManager == null || isUiRestricted()) {
            return;
        }
        this.mLocalManager.getEventManager().unregisterCallback(this);
    }
}
