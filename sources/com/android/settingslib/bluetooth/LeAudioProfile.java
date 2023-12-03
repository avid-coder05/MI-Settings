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
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class LeAudioProfile implements LocalBluetoothProfile {
    private static boolean V = true;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothProfile mService;

    /* loaded from: classes2.dex */
    private final class LeAudioServiceListener implements BluetoothProfile.ServiceListener {
        private LeAudioServiceListener() {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (LeAudioProfile.V) {
                Log.d("LeAudioProfile", "Bluetooth service connected");
            }
            LeAudioProfile.this.mService = bluetoothProfile;
            List<BluetoothDevice> connectedDevices = LeAudioProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice remove = connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = LeAudioProfile.this.mDeviceManager.findDevice(remove);
                if (findDevice == null) {
                    if (LeAudioProfile.V) {
                        Log.d("LeAudioProfile", "LeAudioProfile found new device: " + remove);
                    }
                    findDevice = LeAudioProfile.this.mDeviceManager.addDevice(remove);
                }
                findDevice.onProfileStateChanged(LeAudioProfile.this, 2);
                findDevice.refresh();
            }
            LeAudioProfile.this.mProfileManager.callServiceConnectedListeners();
            LeAudioProfile.this.mIsProfileReady = true;
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            if (LeAudioProfile.V) {
                Log.d("LeAudioProfile", "Bluetooth service disconnected");
            }
            LeAudioProfile.this.mProfileManager.callServiceDisconnectedListeners();
            LeAudioProfile.this.mIsProfileReady = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LeAudioProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new LeAudioServiceListener(), BluetoothLeAudioFactory.getInstance().getLeAudioProfileId());
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean accessProfileEnabled() {
        return true;
    }

    protected void finalize() {
        if (V) {
            Log.d("LeAudioProfile", "finalize()");
        }
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothLeAudioFactory.getInstance().getLeAudioProfileId(), this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("LeAudioProfile", "Error cleaning up LeAudio proxy", th);
            }
        }
    }

    public List<BluetoothDevice> getActiveDevices() {
        return this.mService == null ? new ArrayList() : BluetoothLeAudioFactory.getInstance().getLeAudioActiveDevices(this.mService);
    }

    public List<BluetoothDevice> getConnectedDevices() {
        BluetoothProfile bluetoothProfile = this.mService;
        return bluetoothProfile == null ? new ArrayList(0) : bluetoothProfile.getDevicesMatchingConnectionStates(new int[]{2, 1, 3});
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
        return R$string.bluetooth_profile_le_audio;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getOrdinal() {
        return 1;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getProfileId() {
        return BluetoothLeAudioFactory.getInstance().getLeAudioProfileId();
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getSummaryResourceForDevice(BluetoothDevice bluetoothDevice) {
        int connectionStatus = getConnectionStatus(bluetoothDevice);
        return connectionStatus != 0 ? connectionStatus != 2 ? BluetoothUtils.getConnectionStateSummary(connectionStatus) : R$string.bluetooth_le_audio_profile_summary_connected : R$string.bluetooth_le_audio_profile_summary_use_for;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isEnabled(BluetoothDevice bluetoothDevice) {
        return (this.mService == null || bluetoothDevice == null || BluetoothLeAudioFactory.getInstance().getLeAudioConnectionPolicy(this.mService, bluetoothDevice) <= 0) ? false : true;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isProfileReady() {
        return this.mIsProfileReady;
    }

    public boolean setActiveDevice(BluetoothDevice bluetoothDevice) {
        if (this.mService == null) {
            return false;
        }
        return BluetoothLeAudioFactory.getInstance().setLeAudioActiveDevice(this.mService, bluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        if (this.mService == null || bluetoothDevice == null) {
            return false;
        }
        if (z) {
            if (BluetoothLeAudioFactory.getInstance().getLeAudioConnectionPolicy(this.mService, bluetoothDevice) < 100) {
                return BluetoothLeAudioFactory.getInstance().setLeAudioConnectionPolicy(this.mService, bluetoothDevice, 100);
            }
            return false;
        }
        return BluetoothLeAudioFactory.getInstance().setLeAudioConnectionPolicy(this.mService, bluetoothDevice, 0);
    }

    public void setPreferred(BluetoothDevice bluetoothDevice, boolean z) {
        if (this.mService == null) {
            return;
        }
        if (!z) {
            BluetoothLeAudioFactory.getInstance().setLeAudioPriority(this.mService, bluetoothDevice, 0);
        } else if (BluetoothLeAudioFactory.getInstance().getLeAudioPriority(this.mService, bluetoothDevice) > 100) {
            BluetoothLeAudioFactory.getInstance().setLeAudioPriority(this.mService, bluetoothDevice, 100);
        }
    }

    public String toString() {
        return "LE_AUDIO";
    }
}
