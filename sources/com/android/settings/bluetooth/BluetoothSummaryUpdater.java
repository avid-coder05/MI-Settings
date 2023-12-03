package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.widget.SummaryUpdater;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import java.util.Set;

/* loaded from: classes.dex */
public final class BluetoothSummaryUpdater extends SummaryUpdater implements BluetoothCallback {
    private final BluetoothAdapter mBluetoothAdapter;

    String getConnectedDeviceSummary() {
        Set<BluetoothDevice> bondedDevices = this.mBluetoothAdapter.getBondedDevices();
        if (bondedDevices == null) {
            Log.e("BluetoothSummaryUpdater", "getConnectedDeviceSummary, bonded devices are null");
            return this.mContext.getString(R.string.bluetooth_disabled);
        } else if (bondedDevices.isEmpty()) {
            Log.e("BluetoothSummaryUpdater", "getConnectedDeviceSummary, no bonded devices");
            return this.mContext.getString(R.string.disconnected);
        } else {
            String str = null;
            int i = 0;
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                if (bluetoothDevice.isConnected()) {
                    str = bluetoothDevice.getName();
                    i++;
                    if (i > 1) {
                        break;
                    }
                }
            }
            if (str != null) {
                return i > 1 ? this.mContext.getString(R.string.bluetooth_connected_multiple_devices_summary) : this.mContext.getString(R.string.bluetooth_connected_summary, str);
            }
            Log.e("BluetoothSummaryUpdater", "getConnectedDeviceSummary, deviceName is null, numBondedDevices=" + bondedDevices.size());
            for (BluetoothDevice bluetoothDevice2 : bondedDevices) {
                Log.e("BluetoothSummaryUpdater", "getConnectedDeviceSummary, device=" + bluetoothDevice2.getName() + "[" + bluetoothDevice2.getAddress() + "], isConnected=" + bluetoothDevice2.isConnected());
            }
            return this.mContext.getString(R.string.disconnected);
        }
    }

    @Override // com.android.settings.widget.SummaryUpdater
    public String getSummary() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return this.mContext.getString(R.string.bluetooth_disabled);
        }
        int connectionState = this.mBluetoothAdapter.getConnectionState();
        return connectionState != 1 ? connectionState != 2 ? connectionState != 3 ? this.mContext.getString(R.string.disconnected) : this.mContext.getString(R.string.bluetooth_disconnecting) : getConnectedDeviceSummary() : this.mContext.getString(R.string.bluetooth_connecting);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        notifyChangeIfNeeded();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        notifyChangeIfNeeded();
    }
}
