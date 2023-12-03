package com.android.settingslib.bluetooth;

import android.app.ActivityThread;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import miui.provider.ExtraContacts;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes2.dex */
public class CachedBluetoothDeviceManager {
    private static BluetoothAdapter sAdapter;
    private final LocalBluetoothManager mBtManager;
    @VisibleForTesting
    final List<CachedBluetoothDevice> mCachedDevices;
    @VisibleForTesting
    final Map<Long, CachedBluetoothDevice> mCachedDevicesMapForHearingAids;
    private Context mContext;
    private final Handler mHandler;
    @VisibleForTesting
    HearingAidDeviceManager mHearingAidDeviceManager;
    @VisibleForTesting
    final List<CachedBluetoothDevice> mHearingAidDevicesNotAddedInCache;
    private List<String> mCachedBRAddress = new ArrayList();
    private List<String> mCachedLE1Address = new ArrayList();
    private List<String> mCachedLE2Address = new ArrayList();
    private Map<String, String> mLeAudioAddressMap = Collections.synchronizedMap(new HashMap());
    private int mWaitForAutoBondRetry = 0;

    /* loaded from: classes2.dex */
    class AutoBondHandler extends Handler {
        AutoBondHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            boolean z;
            if (message.what != 1) {
                return;
            }
            int i = message.arg1;
            int i2 = message.arg2;
            Log.d("CachedBluetoothDeviceManager", "MESSAGE_AUTO_BOND index = " + i + ",type = " + i2);
            String str = (String) CachedBluetoothDeviceManager.this.mCachedBRAddress.get(i);
            String str2 = (String) CachedBluetoothDeviceManager.this.mCachedLE1Address.get(i);
            String str3 = (String) CachedBluetoothDeviceManager.this.mCachedLE2Address.get(i);
            CachedBluetoothDeviceManager.access$308(CachedBluetoothDeviceManager.this);
            Log.d("CachedBluetoothDeviceManager", "brAddr = " + str + ",bleAddr1 = " + str2 + ",bleAddr2 = " + str3);
            if (i2 == 1) {
                boolean isAllProfileConnected = CachedBluetoothDeviceManager.this.isAllProfileConnected(str2);
                boolean isAllProfileConnected2 = CachedBluetoothDeviceManager.this.isAllProfileConnected(str3);
                Log.d("CachedBluetoothDeviceManager", "isLe1Connected = " + isAllProfileConnected + ",isLe2Connected = " + isAllProfileConnected2);
                if ((isAllProfileConnected && isAllProfileConnected2) || CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry == 5) {
                    Log.d("CachedBluetoothDeviceManager", "Auto Bond BR device");
                    CachedBluetoothDeviceManager.this.createBond(str, 1);
                    CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry = 0;
                    z = true;
                }
                z = false;
            } else if (i2 == 2) {
                boolean isAllProfileConnected3 = CachedBluetoothDeviceManager.this.isAllProfileConnected(str);
                Log.d("CachedBluetoothDeviceManager", "isBrConnected = " + isAllProfileConnected3);
                if (isAllProfileConnected3 || CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry == 5) {
                    Log.d("CachedBluetoothDeviceManager", "Auto Bond LE1 device");
                    CachedBluetoothDeviceManager.this.createBond(str2, 2);
                    CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry = 0;
                    z = true;
                }
                z = false;
            } else if (i2 == 3) {
                boolean isAllProfileConnected4 = CachedBluetoothDeviceManager.this.isAllProfileConnected(str);
                Log.d("CachedBluetoothDeviceManager", "isBrConnected = " + isAllProfileConnected4);
                if (isAllProfileConnected4 || CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry == 5) {
                    Log.d("CachedBluetoothDeviceManager", "Auto Bond LE2 device");
                    CachedBluetoothDeviceManager.this.createBond(str3, 2);
                    CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry = 0;
                    z = true;
                }
                z = false;
            } else if (i2 == 4) {
                boolean isAllProfileConnected5 = CachedBluetoothDeviceManager.this.isAllProfileConnected(str2);
                Log.d("CachedBluetoothDeviceManager", "isLe1Connected = " + isAllProfileConnected5);
                if (isAllProfileConnected5 || CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry == 5) {
                    Log.d("CachedBluetoothDeviceManager", "Auto Bond BR device LE1");
                    CachedBluetoothDeviceManager.this.createBond(str, 1);
                    CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry = 0;
                    z = true;
                }
                z = false;
            } else {
                if (i2 == 5) {
                    boolean isAllProfileConnected6 = CachedBluetoothDeviceManager.this.isAllProfileConnected(str3);
                    Log.d("CachedBluetoothDeviceManager", "isLe2Connected = " + isAllProfileConnected6);
                    if (isAllProfileConnected6 || CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry == 5) {
                        Log.d("CachedBluetoothDeviceManager", "Auto Bond BR device LE2");
                        CachedBluetoothDeviceManager.this.createBond(str, 1);
                        CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry = 0;
                        z = true;
                    }
                }
                z = false;
            }
            if (z || CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry >= 5) {
                CachedBluetoothDeviceManager.this.mWaitForAutoBondRetry = 0;
                return;
            }
            Log.d("CachedBluetoothDeviceManager", "Retry sendAutoBondMsg");
            CachedBluetoothDeviceManager.this.mHandler.sendMessageDelayed(CachedBluetoothDeviceManager.this.mHandler.obtainMessage(1, i, i2), 1000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CachedBluetoothDeviceManager(Context context, LocalBluetoothManager localBluetoothManager) {
        ArrayList arrayList = new ArrayList();
        this.mCachedDevices = arrayList;
        this.mHearingAidDevicesNotAddedInCache = new ArrayList();
        this.mCachedDevicesMapForHearingAids = new HashMap();
        this.mContext = context;
        this.mBtManager = localBluetoothManager;
        this.mHearingAidDeviceManager = new HearingAidDeviceManager(localBluetoothManager, arrayList);
        sAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mHandler = new AutoBondHandler(Looper.getMainLooper());
    }

    static /* synthetic */ int access$308(CachedBluetoothDeviceManager cachedBluetoothDeviceManager) {
        int i = cachedBluetoothDeviceManager.mWaitForAutoBondRetry;
        cachedBluetoothDeviceManager.mWaitForAutoBondRetry = i + 1;
        return i;
    }

    private void clearNonBondedSubDevices() {
        for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
            CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(size);
            CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
            if (subDevice != null && subDevice.getDevice().getBondState() == 10) {
                cachedBluetoothDevice.setSubDevice(null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$clearNonBondedDevices$0(CachedBluetoothDevice cachedBluetoothDevice) {
        return cachedBluetoothDevice.getBondState() == 10;
    }

    public CachedBluetoothDevice addDevice(BluetoothDevice bluetoothDevice) {
        CachedBluetoothDevice findDevice;
        LocalBluetoothProfileManager profileManager = this.mBtManager.getProfileManager();
        synchronized (this) {
            findDevice = findDevice(bluetoothDevice);
            if (findDevice == null) {
                findDevice = new CachedBluetoothDevice(this.mContext, profileManager, bluetoothDevice);
                this.mHearingAidDeviceManager.initHearingAidDeviceIfNeeded(findDevice);
                if (!this.mHearingAidDeviceManager.setSubDeviceIfNeeded(findDevice)) {
                    this.mCachedDevices.add(findDevice);
                    this.mBtManager.getEventManager().dispatchDeviceAdded(findDevice);
                }
            }
        }
        return findDevice;
    }

    public CachedBluetoothDevice addDevice(BluetoothDevice bluetoothDevice, short s) {
        CachedBluetoothDevice cachedBluetoothDevice = new CachedBluetoothDevice(this.mContext, this.mBtManager.getProfileManager(), bluetoothDevice);
        cachedBluetoothDevice.setRssi(s);
        this.mHearingAidDeviceManager.initHearingAidDeviceIfNeeded(cachedBluetoothDevice);
        synchronized (this) {
            if (!this.mHearingAidDeviceManager.setSubDeviceIfNeeded(cachedBluetoothDevice)) {
                this.mCachedDevices.add(cachedBluetoothDevice);
                this.mBtManager.getEventManager().dispatchDeviceAdded(cachedBluetoothDevice);
            }
        }
        return cachedBluetoothDevice;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean addDevicetoSharedPreferences(CachedBluetoothDevice cachedBluetoothDevice) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("hearing_aid_device_map", 0);
        if (sharedPreferences.contains(cachedBluetoothDevice.getAddress())) {
            return false;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong(cachedBluetoothDevice.getAddress(), cachedBluetoothDevice.getHiSyncId());
        edit.apply();
        return true;
    }

    public synchronized void checkAutoBondIfNeed(BluetoothDevice bluetoothDevice, int i) {
        String currentPackageName = ActivityThread.currentPackageName();
        Log.d("CachedBluetoothDeviceManager", "callerPackageName = " + currentPackageName);
        if (currentPackageName != null && !currentPackageName.contains(YellowPageContract.Settings.DIRECTORY)) {
            Log.d("CachedBluetoothDeviceManager", "checkAutoBondIfNeed,return");
            return;
        }
        int indexOf = this.mCachedBRAddress.contains(bluetoothDevice.getAddress()) ? this.mCachedBRAddress.indexOf(bluetoothDevice.getAddress()) : this.mCachedLE1Address.contains(bluetoothDevice.getAddress()) ? this.mCachedLE1Address.indexOf(bluetoothDevice.getAddress()) : this.mCachedLE2Address.contains(bluetoothDevice.getAddress()) ? this.mCachedLE2Address.indexOf(bluetoothDevice.getAddress()) : -1;
        Log.d("CachedBluetoothDeviceManager", "checkAutoBondIfNeed index = " + indexOf);
        if (indexOf == -1 && i == 0) {
            Log.d("CachedBluetoothDeviceManager", "checkAutoBondIfNeed,keep aosp auto bond solution");
            bluetoothDevice.createBond(2);
        } else if (indexOf == -1) {
            Log.d("CachedBluetoothDeviceManager", "checkAutoBondIfNeed not find dumo device,return");
        } else {
            String str = this.mCachedBRAddress.get(indexOf);
            String str2 = this.mCachedLE1Address.get(indexOf);
            String str3 = this.mCachedLE2Address.get(indexOf);
            Log.d("CachedBluetoothDeviceManager", "checkAutoBondIfNeed brAddr = " + str + ",bleAddr1 = " + str2 + ",bleAddr2 = " + str3);
            if (i == 12) {
                if (bluetoothDevice.getAddress().equalsIgnoreCase(str)) {
                    Log.d("CachedBluetoothDeviceManager", "bleBondState1 = " + getBondState(str2) + ",bleBondState2 = " + getBondState(str3));
                } else if (!bluetoothDevice.getAddress().equalsIgnoreCase(str2)) {
                    bluetoothDevice.getAddress().equalsIgnoreCase(str3);
                }
            } else if (i != 0 && i != 11 && i == 10 && bluetoothDevice.getAddress().equalsIgnoreCase(str)) {
                Log.d("CachedBluetoothDeviceManager", "removeLeAudioAddress and set le audio status to false");
                sAdapter.getRemoteDevice(str).setLeAudioStatus(0);
            }
        }
    }

    public synchronized void clearAllDevices() {
        for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
            this.mCachedDevices.remove(size);
        }
    }

    public synchronized void clearNonBondedDevices() {
        clearNonBondedSubDevices();
        this.mCachedDevices.removeIf(new Predicate() { // from class: com.android.settingslib.bluetooth.CachedBluetoothDeviceManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$clearNonBondedDevices$0;
                lambda$clearNonBondedDevices$0 = CachedBluetoothDeviceManager.lambda$clearNonBondedDevices$0((CachedBluetoothDevice) obj);
                return lambda$clearNonBondedDevices$0;
            }
        });
        resetDumoDevices();
    }

    void clearSharedPreferences() {
        SharedPreferences.Editor edit = this.mContext.getSharedPreferences("hearing_aid_device_map", 0).edit();
        edit.clear();
        edit.apply();
    }

    public boolean createBond(String str, int i) {
        Log.d("CachedBluetoothDeviceManager", "createBond to " + str);
        BluetoothDevice remoteDevice = sAdapter.getRemoteDevice(str);
        if (remoteDevice != null) {
            return remoteDevice.createBond(i);
        }
        return false;
    }

    public synchronized CachedBluetoothDevice findDevice(BluetoothDevice bluetoothDevice) {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
            if (cachedBluetoothDevice.getDevice().equals(bluetoothDevice)) {
                return cachedBluetoothDevice;
            }
            CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
            if (subDevice != null && subDevice.getDevice().equals(bluetoothDevice)) {
                return subDevice;
            }
        }
        return null;
    }

    public int getBondState(String str) {
        CachedBluetoothDevice findDevice = findDevice(sAdapter.getRemoteDevice(str));
        if (findDevice != null) {
            return findDevice.getBondState();
        }
        return 10;
    }

    public synchronized Collection<CachedBluetoothDevice> getCachedDevicesCopy() {
        return new ArrayList(this.mCachedDevices);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getHisyncIdFromSharedPreferences(BluetoothDevice bluetoothDevice) {
        long j = this.mContext.getSharedPreferences("hearing_aid_device_map", 0).getLong(bluetoothDevice.getAddress(), 0L);
        Log.d("CachedBluetoothDeviceManager", "getHisyncIdFromSharedPreferences " + j);
        return j;
    }

    public String getName(BluetoothDevice bluetoothDevice) {
        CachedBluetoothDevice findDevice = findDevice(bluetoothDevice);
        if (findDevice == null || findDevice.getName() == null) {
            String alias = bluetoothDevice.getAlias();
            return alias != null ? alias : bluetoothDevice.getAddress();
        }
        return findDevice.getName();
    }

    public synchronized String getSubDeviceSummary(CachedBluetoothDevice cachedBluetoothDevice) {
        CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
        if (subDevice == null || !subDevice.isConnected()) {
            return null;
        }
        return subDevice.getConnectionSummary();
    }

    public boolean isAllProfileConnected(String str) {
        CachedBluetoothDevice findDevice = findDevice(sAdapter.getRemoteDevice(str));
        if (findDevice != null) {
            return findDevice.isAllProfileConnected();
        }
        return false;
    }

    public synchronized boolean isSubDevice(BluetoothDevice bluetoothDevice) {
        CachedBluetoothDevice subDevice;
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
            if (!cachedBluetoothDevice.getDevice().equals(bluetoothDevice) && (subDevice = cachedBluetoothDevice.getSubDevice()) != null && subDevice.getDevice().equals(bluetoothDevice)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void onBluetoothStateChanged(int i) {
        if (i == 13) {
            for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
                CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(size);
                CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
                if (subDevice != null && subDevice.getBondState() != 12) {
                    cachedBluetoothDevice.setSubDevice(null);
                }
                if (cachedBluetoothDevice.getBondState() != 12) {
                    cachedBluetoothDevice.setJustDiscovered(false);
                    this.mCachedDevices.remove(size);
                }
                cachedBluetoothDevice.mTwspBatteryState = -1;
                cachedBluetoothDevice.mTwspBatteryLevel = -1;
            }
        }
    }

    public void onDeviceNameUpdated(BluetoothDevice bluetoothDevice) {
        CachedBluetoothDevice findDevice = findDevice(bluetoothDevice);
        if (findDevice != null) {
            findDevice.refreshName();
        }
    }

    public synchronized void onDeviceUnpaired(CachedBluetoothDevice cachedBluetoothDevice) {
        CachedBluetoothDevice findMainDevice = this.mHearingAidDeviceManager.findMainDevice(cachedBluetoothDevice);
        CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
        clearSharedPreferences();
        if (subDevice != null) {
            subDevice.unpair();
            cachedBluetoothDevice.setSubDevice(null);
        } else if (findMainDevice != null) {
            findMainDevice.unpair();
            findMainDevice.setSubDevice(null);
        }
    }

    public synchronized boolean onProfileConnectionStateChangedIfProcessed(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        return this.mHearingAidDeviceManager.onProfileConnectionStateChangedIfProcessed(cachedBluetoothDevice, i);
    }

    public synchronized void onScanningStateChanged(boolean z) {
        if (z) {
            for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
                CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(size);
                cachedBluetoothDevice.setJustDiscovered(false);
                CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
                if (subDevice != null) {
                    subDevice.setJustDiscovered(false);
                }
            }
        }
    }

    public void removeDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice.getRssi() == 0) {
            this.mCachedDevices.remove(cachedBluetoothDevice);
        }
    }

    public void resetDumoDevices() {
        Log.d("CachedBluetoothDeviceManager", "reset dumo cached devices");
        this.mCachedBRAddress.clear();
        this.mCachedLE1Address.clear();
        this.mCachedLE2Address.clear();
    }

    public synchronized void setDumoGroupInfo(String str, String str2, String str3) {
        if (str != null && str2 != null && str3 != null) {
            if (!"00:00:00:00:00:00".equals(str) && !"00:00:00:00:00:00".equals(str2) && !"00:00:00:00:00:00".equals(str3) && !this.mCachedBRAddress.contains(str) && !this.mCachedLE1Address.contains(str2) && !this.mCachedLE2Address.contains(str3)) {
                Log.d("CachedBluetoothDeviceManager", "set dumo device to cache");
                this.mCachedBRAddress.add(str);
                this.mCachedLE1Address.add(str2);
                this.mCachedLE2Address.add(str3);
                this.mLeAudioAddressMap.put(str, str2 + ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION + str3);
            }
        }
    }

    public synchronized void updateHearingAidsDevices() {
        this.mHearingAidDeviceManager.updateHearingAidsDevices();
    }
}
