package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/* loaded from: classes.dex */
public abstract class DeviceListPreferenceFragment extends RestrictedDashboardFragment implements BluetoothCallback {
    BluetoothAdapter mBluetoothAdapter;
    protected boolean mContinueDiscovery;
    PreferenceGroup mDeviceListGroup;
    final HashMap<CachedBluetoothDevice, BluetoothDevicePreference> mDevicePreferenceMap;
    protected BluetoothDeviceFilter.Filter mFilter;
    LocalBluetoothAdapter mLocalAdapter;
    LocalBluetoothManager mLocalManager;
    boolean mScanEnabled;
    BluetoothDevice mSelectedDevice;
    final List<BluetoothDevice> mSelectedList;
    boolean mShowDevicesWithoutNames;
    final HashMap<String, MiuiOnLineBluetoothDevicePreference> mTempDevicePreferenceMap;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeviceListPreferenceFragment(String str) {
        super(str);
        this.mDevicePreferenceMap = new HashMap<>();
        this.mSelectedList = new ArrayList();
        this.mTempDevicePreferenceMap = new HashMap<>();
        this.mFilter = BluetoothDeviceFilter.ALL_FILTER;
    }

    public static boolean headSetFeatureIsEnable(Context context) {
        String string;
        String string2;
        if (context == null) {
            Log.d("DeviceListPreferenceFragment", "context is null! ");
            return false;
        } else if (Log.isLoggable("MiuiFCServiceTest", 2)) {
            Log.d("DeviceListPreferenceFragment", "open cloud debug model! ");
            return true;
        } else {
            try {
                string = Settings.Global.getString(context.getContentResolver(), "mi_tws_hs_feature_enable");
                string2 = Settings.Global.getString(context.getContentResolver(), "mi_tws_deviceid_list_hs_feature_enable");
            } catch (Exception e) {
                Log.e("DeviceListPreferenceFragment", "headset Cloud Data get faied " + e);
            }
            if (!"true".equals(string) && !isEnableOfDeviceIdList(string2)) {
                Log.d("DeviceListPreferenceFragment", "TWS cloud data switch not enable! ");
                return false;
            }
            Log.d("DeviceListPreferenceFragment", "TWS cloud data switch is enable! ");
            return true;
        }
    }

    public static boolean headSetMoreDetailEnable(Context context) {
        String string;
        String string2;
        if (context == null) {
            Log.d("DeviceListPreferenceFragment", "context is null! ");
            return false;
        } else if (Log.isLoggable("MiuiFCServiceTest", 2)) {
            Log.d("DeviceListPreferenceFragment", "open cloud debug model! ");
            return true;
        } else {
            try {
                string = Settings.Global.getString(context.getContentResolver(), "mi_tws01_hs_feature_enable");
                string2 = Settings.Global.getString(context.getContentResolver(), "mi_tws01_deviceid_list_hs_feature_enable");
            } catch (Exception e) {
                Log.e("DeviceListPreferenceFragment", "headset Cloud Data get faied " + e);
            }
            if (!"true".equals(string) && !isEnableOfDeviceIdList(string2)) {
                Log.d("DeviceListPreferenceFragment", "TWS01 cloud data switch not enable! ");
                return false;
            }
            Log.d("DeviceListPreferenceFragment", "TWS01 cloud data switch is enable! ");
            return true;
        }
    }

    private boolean isBleAudioDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice != null) {
            try {
                if (cachedBluetoothDevice.isLeDevice()) {
                    return true;
                }
            } catch (Exception e) {
                Log.i("DeviceListPreferenceFragment", " isBleAudioDevice Exception " + e);
                return false;
            }
        }
        String string = Settings.Global.getString(getPrefContext().getContentResolver(), "three_mac_for_ble_f");
        String address = cachedBluetoothDevice != null ? cachedBluetoothDevice.getAddress() : "00:00:00:00:00:00";
        Log.i("DeviceListPreferenceFragment", "value is " + string + " myMac is " + address);
        if (string == null || !string.contains(address)) {
            return false;
        }
        return (string.indexOf(address) / 18) % 3 != 0;
    }

    public static boolean isEnableOfDeviceIdList(String str) {
        List asList;
        new Vector();
        if (TextUtils.isEmpty(str) || (asList = Arrays.asList(str.split("\\,"))) == null || asList.size() <= 0) {
            Log.d("DeviceListPreferenceFragment", " deviceId list switch is disable!");
            return false;
        }
        Log.d("DeviceListPreferenceFragment", " deviceId list switch is enable!");
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addCachedDevices() {
        Iterator<CachedBluetoothDevice> it = this.mLocalManager.getCachedDeviceManager().getCachedDevicesCopy().iterator();
        while (it.hasNext()) {
            onDeviceAdded(it.next());
        }
    }

    public void addDeviceCategory(PreferenceGroup preferenceGroup, int i, BluetoothDeviceFilter.Filter filter, boolean z) {
        cacheRemoveAllPrefs(preferenceGroup);
        preferenceGroup.setTitle(i);
        this.mDeviceListGroup = preferenceGroup;
        if (z) {
            setFilter(BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER);
            addCachedDevices();
        }
        setFilter(filter);
        preferenceGroup.setEnabled(true);
        removeCachedPrefs(preferenceGroup);
    }

    public void checkReCreateOnLineDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        if (headSetFeatureIsEnable(getActivity().getApplicationContext())) {
            MiuiOnLineBluetoothDevicePreference remove = this.mTempDevicePreferenceMap.remove(cachedBluetoothDevice.getAddress());
            PreferenceGroup preferenceGroup = this.mDeviceListGroup;
            if (preferenceGroup == null || remove == null) {
                return;
            }
            preferenceGroup.removePreference(remove);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createDevicePreference(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mDeviceListGroup == null) {
            Log.w("DeviceListPreferenceFragment", "Trying to create a device preference before the list group/category exists!");
        } else if (isBleAudioDevice(cachedBluetoothDevice)) {
            Log.w("DeviceListPreferenceFragment", "two le devices are not disaplay");
        } else {
            String address = cachedBluetoothDevice.getDevice().getAddress();
            Preference cachedPreference = getCachedPreference(address);
            if (cachedPreference != null && !(cachedPreference instanceof BluetoothDevicePreference)) {
                Log.d("DeviceListPreferenceFragment", "Not BluetoothDevicePreference!");
                cachedPreference = null;
            }
            BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) cachedPreference;
            if (bluetoothDevicePreference == null) {
                bluetoothDevicePreference = new BluetoothDevicePreference(getPrefContext(), cachedBluetoothDevice, this.mShowDevicesWithoutNames, 2);
                bluetoothDevicePreference.setKey(address);
                bluetoothDevicePreference.hideSecondTarget(true);
                this.mDeviceListGroup.addPreference(bluetoothDevicePreference);
            } else {
                this.mDeviceListGroup.addPreference(bluetoothDevicePreference);
                bluetoothDevicePreference.setCachedDevice(cachedBluetoothDevice);
                bluetoothDevicePreference.rebind();
            }
            initDevicePreference(bluetoothDevicePreference);
            this.mDevicePreferenceMap.put(cachedBluetoothDevice, bluetoothDevicePreference);
        }
    }

    void createDevicePreference(String str, String str2, String str3, BluetoothClass bluetoothClass, List<LocalBluetoothProfile> list) {
        if (this.mDeviceListGroup == null) {
            Log.w("DeviceListPreferenceFragment", "Trying to create a device preference before the list group/category exists!");
            return;
        }
        MiuiOnLineBluetoothDevicePreference miuiOnLineBluetoothDevicePreference = (MiuiOnLineBluetoothDevicePreference) getCachedPreference(str);
        if (miuiOnLineBluetoothDevicePreference == null) {
            miuiOnLineBluetoothDevicePreference = new MiuiOnLineBluetoothDevicePreference(getPrefContext(), str, str2, str3, bluetoothClass, list, this);
            miuiOnLineBluetoothDevicePreference.setKey(str);
            miuiOnLineBluetoothDevicePreference.hideSecondTarget(true);
            this.mDeviceListGroup.addPreference(miuiOnLineBluetoothDevicePreference);
            Log.d("DeviceListPreferenceFragment", "create createDevicePreference finish");
        } else {
            this.mDeviceListGroup.addPreference(miuiOnLineBluetoothDevicePreference);
            miuiOnLineBluetoothDevicePreference.rebind();
        }
        initDevicePreference(miuiOnLineBluetoothDevicePreference);
        this.mTempDevicePreferenceMap.put(str, miuiOnLineBluetoothDevicePreference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disableScanning() {
        if (this.mScanEnabled) {
            stopScanning();
            this.mScanEnabled = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableScanning() {
        if (this.mScanEnabled) {
            return;
        }
        startScanning();
        this.mScanEnabled = true;
    }

    public void getDeviceFromOnLineBluetooth(String str, String str2, String str3, BluetoothClass bluetoothClass, List<LocalBluetoothProfile> list) {
        if (headSetFeatureIsEnable(getActivity().getApplicationContext())) {
            HashMap<CachedBluetoothDevice, BluetoothDevicePreference> hashMap = this.mDevicePreferenceMap;
            if (hashMap != null) {
                Iterator<CachedBluetoothDevice> it = hashMap.keySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getAddress().equals(str)) {
                        return;
                    }
                }
            }
            HashMap<String, MiuiOnLineBluetoothDevicePreference> hashMap2 = this.mTempDevicePreferenceMap;
            if (hashMap2 != null && hashMap2.size() != 0) {
                Iterator<String> it2 = this.mTempDevicePreferenceMap.keySet().iterator();
                while (it2.hasNext()) {
                    if (it2.next().equals(str)) {
                        return;
                    }
                }
            }
            if (this.mBluetoothAdapter.getState() != 12) {
                return;
            }
            createDevicePreference(str, str2, str3, bluetoothClass, list);
        }
    }

    public abstract String getDeviceListKey();

    /* JADX INFO: Access modifiers changed from: protected */
    public void initDevicePreference(BluetoothDevicePreference bluetoothDevicePreference) {
    }

    protected void initDevicePreference(MiuiOnLineBluetoothDevicePreference miuiOnLineBluetoothDevicePreference) {
    }

    abstract void initPreferencesFromPreferenceScreen();

    public void onBluetoothStateChanged(int i) {
        if (i == 10) {
            updateProgressUi(false);
        }
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mLocalManager = localBtManager;
        if (localBtManager == null) {
            Log.e("DeviceListPreferenceFragment", "Bluetooth is not supported on this device");
            return;
        }
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mLocalAdapter = this.mLocalManager.getBluetoothAdapter();
        this.mShowDevicesWithoutNames = SystemProperties.getBoolean("persist.bluetooth.showdeviceswithoutnames", false);
        initPreferencesFromPreferenceScreen();
        if (TextUtils.isEmpty(getDeviceListKey())) {
            return;
        }
        this.mDeviceListGroup = (PreferenceGroup) findPreference(getDeviceListKey());
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        LocalBluetoothAdapter localBluetoothAdapter;
        if (this.mContinueDiscovery && (localBluetoothAdapter = this.mLocalAdapter) != null) {
            localBluetoothAdapter.stopScanning();
        }
        super.onDestroy();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mDevicePreferenceMap.get(cachedBluetoothDevice) == null && this.mTempDevicePreferenceMap.get(cachedBluetoothDevice.getAddress()) == null && this.mBluetoothAdapter.getState() == 12 && this.mFilter.matches(cachedBluetoothDevice.getDevice())) {
            createDevicePreference(cachedBluetoothDevice);
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothDevicePreference remove = this.mDevicePreferenceMap.remove(cachedBluetoothDevice);
        if (remove != null) {
            this.mDeviceListGroup.removePreference(remove);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        bluetoothDevicePreference.onClicked();
    }

    void onDevicePreferenceClick(MiuiOnLineBluetoothDevicePreference miuiOnLineBluetoothDevicePreference) {
        miuiOnLineBluetoothDevicePreference.onClicked();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        LocalBluetoothAdapter localBluetoothAdapter = this.mLocalAdapter;
        if (localBluetoothAdapter != null && localBluetoothAdapter.isEnabled()) {
            Log.d("DeviceListPreferenceFragment", "set scan mode connectable");
            this.mLocalAdapter.setScanMode(21);
        }
        if (this.mLocalManager == null || isUiRestricted()) {
            return;
        }
        this.mLocalManager.setForegroundActivity(null);
        if (this.mContinueDiscovery) {
            return;
        }
        this.mLocalAdapter.stopScanning();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("bt_scan".equals(preference.getKey())) {
            startScanning();
            return true;
        } else if (!(preference instanceof BluetoothDevicePreference)) {
            if (preference instanceof MiuiOnLineBluetoothDevicePreference) {
                onDevicePreferenceClick((MiuiOnLineBluetoothDevicePreference) preference);
                return true;
            }
            return super.onPreferenceTreeClick(preference);
        } else {
            BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) preference;
            BluetoothDevice device = bluetoothDevicePreference.getCachedDevice().getDevice();
            this.mSelectedDevice = device;
            this.mSelectedList.add(device);
            onDevicePreferenceClick(bluetoothDevicePreference);
            return true;
        }
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        LocalBluetoothAdapter localBluetoothAdapter = this.mLocalAdapter;
        if (localBluetoothAdapter != null && localBluetoothAdapter.isEnabled()) {
            Log.d("DeviceListPreferenceFragment", "set scan mode connectable and discoverable");
            this.mLocalAdapter.setScanMode(23);
        }
        if (this.mLocalManager == null || isUiRestricted()) {
            return;
        }
        this.mLocalManager.setForegroundActivity(getActivity());
        this.mContinueDiscovery = false;
    }

    public void onScanningStateChanged(boolean z) {
        if (!z && this.mScanEnabled) {
            startScanning();
        }
        updateProgressUi(z);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mLocalManager == null || isUiRestricted()) {
            return;
        }
        updateProgressUi(this.mLocalAdapter.isDiscovering());
        getListView().setItemAnimator(null);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mLocalManager != null) {
            isUiRestricted();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeAllDevices() {
        this.mDevicePreferenceMap.clear();
        PreferenceGroup preferenceGroup = this.mDeviceListGroup;
        if (preferenceGroup == null) {
            return;
        }
        for (int preferenceCount = preferenceGroup.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
            Preference preference = this.mDeviceListGroup.getPreference(preferenceCount);
            if (preference.getOrder() >= 0 && preference.getOrder() != 500) {
                this.mDeviceListGroup.removePreference(preference);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setFilter(int i) {
        this.mFilter = BluetoothDeviceFilter.getFilter(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setFilter(BluetoothDeviceFilter.Filter filter) {
        this.mFilter = filter;
    }

    void startScanning() {
        if (this.mBluetoothAdapter.isDiscovering()) {
            return;
        }
        this.mBluetoothAdapter.startDiscovery();
    }

    void stopScanning() {
        if (this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateFooterPreference(Preference preference) {
        preference.setTitle(getString(R.string.bluetooth_footer_mac_message, BidiFormatter.getInstance().unicodeWrap(this.mBluetoothAdapter.getAddress())));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateProgressUi(boolean z) {
        PreferenceGroup preferenceGroup = this.mDeviceListGroup;
        if (preferenceGroup instanceof BluetoothProgressCategory) {
            ((BluetoothProgressCategory) preferenceGroup).setProgress(z);
        }
    }
}
