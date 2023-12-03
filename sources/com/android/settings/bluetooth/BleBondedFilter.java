package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;

/* loaded from: classes.dex */
public class BleBondedFilter implements BluetoothDeviceFilter.Filter {
    @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter
    public boolean matches(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice.getBondState() == 12;
    }
}
