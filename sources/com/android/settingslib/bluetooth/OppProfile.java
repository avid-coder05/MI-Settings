package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import com.android.settingslib.R$string;

/* loaded from: classes2.dex */
final class OppProfile implements LocalBluetoothProfile {
    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean accessProfileEnabled() {
        return false;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionPolicy(BluetoothDevice bluetoothDevice) {
        return 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        return 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getNameResource(BluetoothDevice bluetoothDevice) {
        return R$string.bluetooth_profile_opp;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getOrdinal() {
        return 2;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getProfileId() {
        return 20;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getSummaryResourceForDevice(BluetoothDevice bluetoothDevice) {
        return 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isEnabled(BluetoothDevice bluetoothDevice) {
        return false;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isProfileReady() {
        return true;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        return false;
    }

    public String toString() {
        return "OPP";
    }
}
