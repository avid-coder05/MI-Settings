package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$string;
import com.mediatek.bt.BluetoothLeAudioFactory;
import java.util.List;

/* loaded from: classes2.dex */
public class VolumeControlProfile implements LocalBluetoothProfile {
    private static boolean V = true;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothProfile mService;

    /* loaded from: classes2.dex */
    private final class VolumeControlServiceListener implements BluetoothProfile.ServiceListener {
        private VolumeControlServiceListener() {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (VolumeControlProfile.V) {
                Log.d("VolumeControlProfile", "Bluetooth service connected");
            }
            VolumeControlProfile.this.mService = bluetoothProfile;
            List<BluetoothDevice> connectedDevices = VolumeControlProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice remove = connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = VolumeControlProfile.this.mDeviceManager.findDevice(remove);
                if (findDevice == null) {
                    if (VolumeControlProfile.V) {
                        Log.d("VolumeControlProfile", "VolumeControlProfile found new device: " + remove);
                    }
                    findDevice = VolumeControlProfile.this.mDeviceManager.addDevice(remove);
                }
                findDevice.onProfileStateChanged(VolumeControlProfile.this, 2);
                findDevice.refresh();
            }
            VolumeControlProfile.this.mProfileManager.callServiceConnectedListeners();
            VolumeControlProfile.this.mIsProfileReady = true;
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            if (VolumeControlProfile.V) {
                Log.d("VolumeControlProfile", "Bluetooth service disconnected");
            }
            VolumeControlProfile.this.mProfileManager.callServiceDisconnectedListeners();
            VolumeControlProfile.this.mIsProfileReady = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VolumeControlProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new VolumeControlServiceListener(), BluetoothLeAudioFactory.getInstance().getVcProfileId());
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean accessProfileEnabled() {
        return true;
    }

    protected void finalize() {
        if (V) {
            Log.d("VolumeControlProfile", "finalize()");
        }
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothLeAudioFactory.getInstance().getVcProfileId(), this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("VolumeControlProfile", "Error cleaning up Volume Control proxy", th);
            }
        }
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionPolicy(BluetoothDevice bluetoothDevice) {
        if (this.mService == null || bluetoothDevice == null) {
            return 0;
        }
        return BluetoothLeAudioFactory.getInstance().getLeAudioConnectionPolicy(this.mService, bluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothProfile bluetoothProfile = this.mService;
        if (bluetoothProfile == null) {
            return 0;
        }
        return bluetoothProfile.getConnectionState(bluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return R$drawable.ic_bt_le_audio;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getNameResource(BluetoothDevice bluetoothDevice) {
        return R$string.bluetooth_profile_volume_control;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getOrdinal() {
        return 1;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getProfileId() {
        return BluetoothLeAudioFactory.getInstance().getVcProfileId();
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getSummaryResourceForDevice(BluetoothDevice bluetoothDevice) {
        int connectionStatus = getConnectionStatus(bluetoothDevice);
        return connectionStatus != 0 ? connectionStatus != 2 ? BluetoothUtils.getConnectionStateSummary(connectionStatus) : R$string.bluetooth_hearing_aid_profile_summary_connected : R$string.bluetooth_hearing_aid_profile_summary_use_for;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isEnabled(BluetoothDevice bluetoothDevice) {
        return (this.mService == null || bluetoothDevice == null || BluetoothLeAudioFactory.getInstance().getVcConnectionPolicy(this.mService, bluetoothDevice) <= 0) ? false : true;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isProfileReady() {
        return this.mIsProfileReady;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        if (this.mService == null || bluetoothDevice == null) {
            return false;
        }
        if (z) {
            if (BluetoothLeAudioFactory.getInstance().getVcConnectionPolicy(this.mService, bluetoothDevice) < 100) {
                return BluetoothLeAudioFactory.getInstance().setVcConnectionPolicy(this.mService, bluetoothDevice, 100);
            }
            return false;
        }
        return BluetoothLeAudioFactory.getInstance().setVcConnectionPolicy(this.mService, bluetoothDevice, 0);
    }

    public void setPreferred(BluetoothDevice bluetoothDevice, boolean z) {
        if (this.mService == null) {
            return;
        }
        if (!z) {
            BluetoothLeAudioFactory.getInstance().setVcPriority(this.mService, bluetoothDevice, 0);
        } else if (BluetoothLeAudioFactory.getInstance().getVcPriority(this.mService, bluetoothDevice) > 100) {
            BluetoothLeAudioFactory.getInstance().setVcPriority(this.mService, bluetoothDevice, 100);
        }
    }

    public String toString() {
        return "VolumeControl";
    }
}
