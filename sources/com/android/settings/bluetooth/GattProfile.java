package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.milink.api.v1.type.DeviceType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.bluetooth.ble.MiBleDeviceManager;
import miui.bluetooth.ble.ScanRecord;
import miui.bluetooth.ble.ScanResult;

/* loaded from: classes.dex */
public class GattProfile extends BluetoothGattCallback implements LocalBluetoothProfile {
    private static final String TAG = GattProfile.class.getSimpleName();
    private static List<String> mBondDevices = new ArrayList();
    private BluetoothManager mBluetoothManager;
    private Context mContext;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private Handler mHandler;
    private final LocalBluetoothAdapter mLocalAdapter;
    private MiuiLockPatternUtils mLockPatternUtils;
    private MiBleDeviceManager mMiBleDeviceManager;
    private final LocalBluetoothProfileManager mProfileManager;
    private final Uri mUri;
    private ContentObserver mObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.bluetooth.GattProfile.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            GattProfile.this.getBondDevices();
            super.onChange(z);
        }
    };
    private HashMap<String, BluetoothGatt> mGatts = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public GattProfile(Context context, LocalBluetoothAdapter localBluetoothAdapter, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager, MiBleDeviceManager miBleDeviceManager) {
        this.mContext = context;
        this.mLocalAdapter = localBluetoothAdapter;
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        this.mBluetoothManager = (BluetoothManager) context.getSystemService(DeviceType.BLUETOOTH);
        this.mMiBleDeviceManager = miBleDeviceManager;
        this.mLockPatternUtils = new MiuiLockPatternUtils(this.mContext);
        Uri parse = Uri.parse("content://com.android.bluetooth.ble.settingsprovider/devices");
        this.mUri = parse;
        this.mHandler = new Handler();
        getBondDevices();
        try {
            this.mContext.getContentResolver().registerContentObserver(parse, false, this.mObserver);
        } catch (SecurityException e) {
            Log.e(TAG, "registerContentObserver failed: ", e);
        }
    }

    public static boolean isBond(BluetoothDevice bluetoothDevice) {
        return mBondDevices.contains(bluetoothDevice.getAddress());
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean accessProfileEnabled() {
        return true;
    }

    public void cleanup() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
        this.mObserver = null;
        Iterator<BluetoothGatt> it = this.mGatts.values().iterator();
        while (it.hasNext()) {
            it.next().close();
        }
        this.mGatts.clear();
    }

    public synchronized void getBondDevices() {
        final BluetoothDevice remoteDevice;
        final CachedBluetoothDevice findDevice;
        ScanRecord scanRecord;
        ArrayList arrayList = new ArrayList();
        try {
            String bluetoothAddressToUnlock = this.mLockPatternUtils.getBluetoothAddressToUnlock();
            if (!TextUtils.isEmpty(bluetoothAddressToUnlock)) {
                BluetoothDevice remoteDevice2 = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothAddressToUnlock);
                if (this.mDeviceManager.findDevice(remoteDevice2) == null) {
                    Log.w(TAG, "GattProfile get bluetooth unlock device: " + remoteDevice2);
                    this.mDeviceManager.addDevice(remoteDevice2).refresh();
                }
                arrayList.add(bluetoothAddressToUnlock);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        List<String> boundDevices = this.mMiBleDeviceManager.getBoundDevices();
        if (boundDevices != null) {
            for (String str : boundDevices) {
                BluetoothDevice remoteDevice3 = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(str);
                CachedBluetoothDevice findDevice2 = this.mDeviceManager.findDevice(remoteDevice3);
                if (findDevice2 == null) {
                    Log.w(TAG, "GattProfile found new device: " + remoteDevice3);
                    findDevice2 = this.mDeviceManager.addDevice(remoteDevice3);
                }
                String alias = remoteDevice3.getAlias();
                if (alias != null && !alias.equalsIgnoreCase(str)) {
                    findDevice2.setName(alias);
                    arrayList.add(remoteDevice3.getAddress());
                    findDevice2.refresh();
                }
                ScanResult scanResult = this.mMiBleDeviceManager.getScanResult(str);
                if (scanResult != null && (scanRecord = scanResult.getScanRecord()) != null) {
                    try {
                        String deviceName = scanRecord.getDeviceName();
                        if (!TextUtils.isEmpty(deviceName)) {
                            findDevice2.setName(deviceName);
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                arrayList.add(remoteDevice3.getAddress());
                findDevice2.refresh();
            }
        }
        for (String str2 : mBondDevices) {
            if (!arrayList.contains(str2) && (findDevice = this.mDeviceManager.findDevice((remoteDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(str2)))) != null) {
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.GattProfile.2
                    @Override // java.lang.Runnable
                    public void run() {
                        int connectionStatus = GattProfile.this.getConnectionStatus(remoteDevice);
                        if (connectionStatus == 0 || connectionStatus == 3) {
                            GattProfile.this.mDeviceManager.removeDevice(findDevice);
                        }
                    }
                }, 50L);
            }
        }
        mBondDevices = arrayList;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionPolicy(BluetoothDevice bluetoothDevice) {
        return 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        return this.mBluetoothManager.getConnectionState(bluetoothDevice, 7);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getNameResource(BluetoothDevice bluetoothDevice) {
        return 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getOrdinal() {
        return 7;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getProfileId() {
        return 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getSummaryResourceForDevice(BluetoothDevice bluetoothDevice) {
        return 0;
    }

    public boolean isBleDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice.getType() == 2 || isBond(bluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isEnabled(BluetoothDevice bluetoothDevice) {
        return false;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isProfileReady() {
        return true;
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        super.onCharacteristicChanged(bluetoothGatt, bluetoothGattCharacteristic);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        super.onCharacteristicRead(bluetoothGatt, bluetoothGattCharacteristic, i);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
        CachedBluetoothDevice findDevice = this.mDeviceManager.findDevice(bluetoothGatt.getDevice());
        if (findDevice == null) {
            return;
        }
        findDevice.onProfileStateChanged(this, i2);
        super.onConnectionStateChange(bluetoothGatt, i, i2);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        super.onDescriptorRead(bluetoothGatt, bluetoothGattDescriptor, i);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        super.onDescriptorWrite(bluetoothGatt, bluetoothGattDescriptor, i);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, int i, int i2) {
        super.onReadRemoteRssi(bluetoothGatt, i, i2);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onReliableWriteCompleted(BluetoothGatt bluetoothGatt, int i) {
        super.onReliableWriteCompleted(bluetoothGatt, i);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
        super.onServicesDiscovered(bluetoothGatt, i);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        return false;
    }
}
