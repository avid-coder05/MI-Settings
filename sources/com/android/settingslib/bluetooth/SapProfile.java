package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSap;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$string;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SapProfile implements LocalBluetoothProfile {
    static final ParcelUuid[] UUIDS = {BluetoothUuid.SAP};
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothSap mService;

    /* loaded from: classes2.dex */
    private final class SapServiceListener implements BluetoothProfile.ServiceListener {
        private SapServiceListener() {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            SapProfile.this.mService = (BluetoothSap) bluetoothProfile;
            List connectedDevices = SapProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = SapProfile.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    Log.w("SapProfile", "SapProfile found new device: " + bluetoothDevice);
                    findDevice = SapProfile.this.mDeviceManager.addDevice(bluetoothDevice);
                }
                findDevice.onProfileStateChanged(SapProfile.this, 2);
                findDevice.refresh();
            }
            SapProfile.this.mProfileManager.callServiceConnectedListeners();
            SapProfile.this.mIsProfileReady = true;
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            SapProfile.this.mProfileManager.callServiceDisconnectedListeners();
            SapProfile.this.mIsProfileReady = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SapProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new SapServiceListener(), 10);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean accessProfileEnabled() {
        return true;
    }

    protected void finalize() {
        Log.d("SapProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(10, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("SapProfile", "Error cleaning up SAP proxy", th);
            }
        }
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionPolicy(BluetoothDevice bluetoothDevice) {
        BluetoothSap bluetoothSap = this.mService;
        if (bluetoothSap == null) {
            return 0;
        }
        return bluetoothSap.getConnectionPolicy(bluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothSap bluetoothSap = this.mService;
        if (bluetoothSap == null) {
            return 0;
        }
        return bluetoothSap.getConnectionState(bluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return R$drawable.ic_bt_cellphone;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getNameResource(BluetoothDevice bluetoothDevice) {
        return R$string.bluetooth_profile_sap;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getOrdinal() {
        return 10;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getProfileId() {
        return 10;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getSummaryResourceForDevice(BluetoothDevice bluetoothDevice) {
        int connectionStatus = getConnectionStatus(bluetoothDevice);
        return connectionStatus != 0 ? connectionStatus != 2 ? BluetoothUtils.getConnectionStateSummary(connectionStatus) : R$string.bluetooth_sap_profile_summary_connected : R$string.bluetooth_sap_profile_summary_use_for;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isEnabled(BluetoothDevice bluetoothDevice) {
        BluetoothSap bluetoothSap = this.mService;
        return bluetoothSap != null && bluetoothSap.getConnectionPolicy(bluetoothDevice) > 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isProfileReady() {
        return this.mIsProfileReady;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothSap bluetoothSap = this.mService;
        if (bluetoothSap == null) {
            return false;
        }
        if (z) {
            if (bluetoothSap.getConnectionPolicy(bluetoothDevice) < 100) {
                return this.mService.setConnectionPolicy(bluetoothDevice, 100);
            }
            return false;
        }
        return bluetoothSap.setConnectionPolicy(bluetoothDevice, 0);
    }

    public String toString() {
        return "SAP";
    }
}
