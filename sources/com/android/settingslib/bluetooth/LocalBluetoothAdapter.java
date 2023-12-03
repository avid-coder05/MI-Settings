package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Process;
import android.util.Log;
import com.mi.mibridge.MiBridge;
import java.lang.reflect.Method;
import java.util.Set;
import miui.util.FeatureParser;

@Deprecated
/* loaded from: classes2.dex */
public class LocalBluetoothAdapter {
    private static LocalBluetoothAdapter sInstance;
    private final BluetoothAdapter mAdapter;
    private long mLastScan;
    private LocalBluetoothProfileManager mProfileManager;
    private int mState = Integer.MIN_VALUE;

    private LocalBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.mAdapter = bluetoothAdapter;
    }

    public static synchronized LocalBluetoothAdapter getInstance() {
        LocalBluetoothAdapter localBluetoothAdapter;
        BluetoothAdapter defaultAdapter;
        synchronized (LocalBluetoothAdapter.class) {
            if (sInstance == null && (defaultAdapter = BluetoothAdapter.getDefaultAdapter()) != null) {
                sInstance = new LocalBluetoothAdapter(defaultAdapter);
            }
            localBluetoothAdapter = sInstance;
        }
        return localBluetoothAdapter;
    }

    public synchronized int getBluetoothState() {
        syncBluetoothState();
        return this.mState;
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return this.mAdapter.getBondedDevices();
    }

    public BluetoothDevice getRemoteDevice(String str) {
        return this.mAdapter.getRemoteDevice(str);
    }

    public int getState() {
        return this.mAdapter.getState();
    }

    public boolean isDiscovering() {
        return this.mAdapter.isDiscovering();
    }

    public boolean isEnabled() {
        return this.mAdapter.isEnabled();
    }

    public boolean setBluetoothEnabled(boolean z) {
        if (FeatureParser.getBoolean("support_bluetooth_boost", false)) {
            String string = FeatureParser.getString("bluetooth_boost_value");
            if (string != null) {
                if (string.length() < 1) {
                    Log.e("LocalBluetoothAdapter", "setBluetoothEnabled: boost value error");
                } else {
                    if (string.length() > 2 && "0x".equalsIgnoreCase(string.substring(0, 2))) {
                        string = string.substring(2);
                    }
                    try {
                        Log.v("LocalBluetoothAdapter", "setBluetoothEnabled : boostValue = " + string);
                        Integer.parseInt(string, 16);
                    } catch (NumberFormatException unused) {
                        Log.e("LocalBluetoothAdapter", "setBluetoothEnabled: set boost number format exception");
                    }
                }
            }
        } else if (FeatureParser.getBoolean("support_bluetooth_mtk_boost", false)) {
            try {
                Method method = MiBridge.a;
                if (MiBridge.checkPermission("com.android.settingslib.bluetooth", Process.myUid())) {
                    int requestCpuHighFreq = MiBridge.requestCpuHighFreq(Process.myUid(), 2, 2000);
                    if (requestCpuHighFreq == 0) {
                        Log.d("LocalBluetoothAdapter", "Success ");
                    } else if (requestCpuHighFreq == -2) {
                        Log.d("LocalBluetoothAdapter", "No Permission ");
                    } else {
                        Log.d("LocalBluetoothAdapter", "Fail ");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        boolean enable = z ? this.mAdapter.enable() : this.mAdapter.disable();
        if (enable) {
            setBluetoothStateInt(z ? 11 : 13);
        } else {
            Log.v("LocalBluetoothAdapter", "setBluetoothEnabled call, manager didn't return success for enabled: " + z);
            syncBluetoothState();
        }
        return enable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBluetoothStateInt(int i) {
        LocalBluetoothProfileManager localBluetoothProfileManager;
        synchronized (this) {
            if (this.mState == i) {
                return;
            }
            this.mState = i;
            if (i != 12 || (localBluetoothProfileManager = this.mProfileManager) == null) {
                return;
            }
            localBluetoothProfileManager.setBluetoothStateOn();
        }
    }

    public void setName(String str) {
        this.mAdapter.setName(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProfileManager(LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mProfileManager = localBluetoothProfileManager;
    }

    public void setScanMode(int i) {
        this.mAdapter.setScanMode(i);
    }

    public void startScanning(boolean z) {
        if (this.mAdapter.isDiscovering()) {
            return;
        }
        if (!z) {
            if (this.mLastScan + 300000 > System.currentTimeMillis()) {
                return;
            }
            A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
            if (a2dpProfile != null && a2dpProfile.isA2dpPlaying()) {
                return;
            }
            A2dpSinkProfile a2dpSinkProfile = this.mProfileManager.getA2dpSinkProfile();
            if (a2dpSinkProfile != null && a2dpSinkProfile.isAudioPlaying()) {
                return;
            }
        }
        if (this.mAdapter.startDiscovery()) {
            this.mLastScan = System.currentTimeMillis();
        }
    }

    public void stopScanning() {
        if (this.mAdapter.isDiscovering()) {
            this.mAdapter.cancelDiscovery();
        }
    }

    boolean syncBluetoothState() {
        if (this.mAdapter.getState() != this.mState) {
            setBluetoothStateInt(this.mAdapter.getState());
            return true;
        }
        return false;
    }
}
