package com.android.settings.bluetooth;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.MiuiSearchDrawable;
import com.android.settings.R;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class MiuiDevicePickerFragment extends DevicePickerFragment {
    private MiuiSearchDrawable mSearchIcon;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.DevicePickerFragment, com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initDevicePreference(BluetoothDevicePreference bluetoothDevicePreference) {
        super.initDevicePreference(bluetoothDevicePreference);
        bluetoothDevicePreference.setLayoutResource(R.layout.preference_bt_icon_no_widget);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DevicePickerFragment, com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initPreferencesFromPreferenceScreen() {
        super.initPreferencesFromPreferenceScreen();
        setHasOptionsMenu(true);
    }

    @Override // com.android.settings.bluetooth.DevicePickerFragment, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        this.mSearchIcon = new MiuiSearchDrawable(getActivity());
        super.onCreate(bundle);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        LocalBluetoothAdapter localBluetoothAdapter = this.mLocalAdapter;
        if (localBluetoothAdapter == null) {
            return;
        }
        boolean z = localBluetoothAdapter.getBluetoothState() == 12;
        MenuItem add = menu.add(0, 1, 0, R.string.bluetooth_search_for_devices);
        add.setIcon(this.mSearchIcon.getSearchIcon());
        add.setEnabled(z);
        add.setShowAsAction(1);
        if (z) {
            return;
        }
        updateProgressUi(false);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.mLocalAdapter.getBluetoothState() == 12) {
            if (this.mLocalAdapter.isDiscovering()) {
                disableScanning();
            } else {
                removeAllUnbondedDevices();
                addCachedDevices();
                enableScanning();
            }
        }
        return true;
    }

    @Override // com.android.settings.bluetooth.DevicePickerFragment, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
        invalidateOptionsMenu();
    }

    void removeAllUnbondedDevices() {
        Iterator<Map.Entry<CachedBluetoothDevice, BluetoothDevicePreference>> it = this.mDevicePreferenceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<CachedBluetoothDevice, BluetoothDevicePreference> next = it.next();
            if (BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER.matches(next.getKey().getDevice())) {
                it.remove();
                BluetoothDevicePreference value = next.getValue();
                if (value != null) {
                    this.mDeviceListGroup.removePreference(value);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void updateProgressUi(boolean z) {
        if (z) {
            this.mSearchIcon.playAnimation();
        } else {
            this.mSearchIcon.stopAnimation();
        }
    }
}
