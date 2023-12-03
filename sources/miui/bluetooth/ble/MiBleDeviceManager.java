package miui.bluetooth.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.util.List;
import java.util.UUID;
import miui.bluetooth.ble.IMiBleDeviceManager;
import miui.bluetooth.ble.IScanDeviceCallback;

/* loaded from: classes3.dex */
public class MiBleDeviceManager {
    public static final String ACTION_BLE_EVENT = "miui.bluetooth.BLE_EVENT";
    public static final int BLE_EVENT_AWAKE = 5;
    public static final int BLE_EVENT_CLICK = 1;
    public static final int BLE_EVENT_CUSTOM = 256;
    public static final int BLE_EVENT_DOUBLE_CLICK = 2;
    public static final int BLE_EVENT_LINK_LOSS = 6;
    public static final int BLE_EVENT_LONG_CLICK = 3;
    public static final int BLE_EVENT_PHONE_HANGUP = 7;
    public static final int BLE_EVENT_PHONE_IGNORE = 8;
    public static final int BLE_EVENT_SLEEP = 4;
    public static final int BLE_EVENT_UNKNOWN = 0;
    public static final String BLE_IMMEDIATE_ALERT_PERMISSION = "miui.permission.BLE_IMMEDIATE_ALERT";
    private static final boolean DBG = true;
    public static final String EXTRA_DEVICE = "miui.bluetooth.extras.DEVICE";
    public static final String EXTRA_EVENT = "miui.bluetooth.extras.EVENT";
    public static final String EXTRA_EVENT_DATA = "miui.bluetooth.extras.EVENT_DATA";
    public static final int SERVICE_VERSION_UNKNOWN = -1;
    public static final String SETTING_BIND_DEVICE = "device_type";
    public static final String SETTING_IMMEDIATE_ALERT_ALARM_ENABLED = "alert_alarm_enabled";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_DELAYED = "alert_incall_delayed";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_ENABLED = "alert_incall_enabled";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_IN_CONTACTS_ENABLED = "alert_incall_enabled_in_contacts";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_NO_CONTACTS_ENABLED = "alert_incall_enabled_no_contacts";
    public static final String SETTING_IMMEDIATE_ALERT_SMS_ENABLED = "alert_sms_enabled";
    public static final String SETTING_IMMEDIATE_ALERT_SMS_IN_CONTACTS_ENABLED = "alert_sms_enabled_in_contacts";
    public static final String SETTING_IMMEDIATE_ALERT_SMS_NO_CONTACTS_ENABLED = "alert_sms_enabled_no_contacts";
    private static final String TAG = "MiBleDeviceManager";
    public static final int TYPE_MI_BAND = 1;
    public static final int TYPE_MI_KEY = 69;
    public static final int TYPE_MI_VR_CONTROLLER = 2;
    public static final int TYPE_UNKNOWN = 0;
    private ParcelUuid mClientId;
    private Context mContext;
    private MiBleDeviceManagerListener mListener;
    private IMiBleDeviceManager mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: miui.bluetooth.ble.MiBleDeviceManager.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MiBleDeviceManager.this.setServie(IMiBleDeviceManager.Stub.asInterface(iBinder));
            if (MiBleDeviceManager.this.mListener != null) {
                MiBleDeviceManager.this.mListener.onInit(MiBleDeviceManager.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            MiBleDeviceManager.this.setServie(null);
            if (MiBleDeviceManager.this.mListener != null) {
                MiBleDeviceManager.this.mListener.onDestroy();
            }
        }
    };
    private IBinder mToken;

    /* loaded from: classes3.dex */
    public interface MiBleDeviceManagerListener {
        void onDestroy();

        void onInit(MiBleDeviceManager miBleDeviceManager);
    }

    private MiBleDeviceManager(Context context, MiBleDeviceManagerListener miBleDeviceManagerListener) {
        if (context == null || context.getApplicationContext() == null) {
            throw new IllegalArgumentException("context not valid");
        }
        this.mClientId = new ParcelUuid(UUID.randomUUID());
        this.mToken = new Binder();
        this.mContext = context.getApplicationContext();
        this.mListener = miBleDeviceManagerListener;
    }

    private boolean checkReady() {
        if (this.mService == null) {
            Log.w(TAG, "Manager is not ready");
            return false;
        }
        return true;
    }

    public static MiBleDeviceManager createManager(Context context, MiBleDeviceManagerListener miBleDeviceManagerListener) {
        MiBleDeviceManager miBleDeviceManager = new MiBleDeviceManager(context, miBleDeviceManagerListener);
        miBleDeviceManager.init();
        return miBleDeviceManager;
    }

    public static String getSettingKeyForEvent(int i) {
        return String.format("miui_ble_event_%d", Integer.valueOf(i));
    }

    private void init() {
        Intent intent = new Intent("miui.bluetooth.mible.DeviceManagerService");
        intent.setComponent(new ComponentName("com.xiaomi.bluetooth", "com.android.bluetooth.ble.app.MiBleDeviceManagerService"));
        boolean bindService = this.mContext.bindService(intent, this.mServiceConnection, 1);
        if (!bindService) {
            intent.setComponent(new ComponentName("com.android.bluetooth", "com.android.bluetooth.ble.app.MiBleDeviceManagerService"));
            bindService = this.mContext.bindService(intent, this.mServiceConnection, 1);
        }
        if (bindService) {
            return;
        }
        Log.e(TAG, "bind manager service error: " + intent);
        if (this.mListener != null) {
            new Handler().post(new Runnable() { // from class: miui.bluetooth.ble.MiBleDeviceManager.2
                @Override // java.lang.Runnable
                public void run() {
                    MiBleDeviceManager.this.mListener.onDestroy();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setServie(IMiBleDeviceManager iMiBleDeviceManager) {
        this.mService = iMiBleDeviceManager;
    }

    public boolean bindDevice(String str) {
        if (checkReady()) {
            try {
                return this.mService.setSettingInteger(str, SETTING_BIND_DEVICE, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean bindDevice(String str, byte[] bArr) {
        if (checkReady()) {
            try {
                return this.mService.setToken(str, bArr);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void close() {
        try {
            this.mContext.unbindService(this.mServiceConnection);
        } catch (Exception e) {
            Log.e(TAG, "Close manager service error", e);
        }
    }

    public List<String> getBoundDevices() {
        if (checkReady()) {
            try {
                return this.mService.getBoundDevices();
            } catch (RemoteException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public int getDeviceType(String str) {
        if (checkReady()) {
            try {
                return this.mService.getDeviceType(str);
            } catch (RemoteException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public String getRegisterAppForBleEvent(String str, int i) {
        if (checkReady()) {
            try {
                return this.mService.getRegisterAppForBleEvent(str, i);
            } catch (RemoteException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public ScanResult getScanResult(String str) {
        if (checkReady()) {
            try {
                return this.mService.getScanResult(str);
            } catch (RemoteException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public int getServiceVersion() {
        if (checkReady()) {
            try {
                return this.mService.getServiceVersion();
            } catch (RemoteException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public boolean getSettingsBoolean(String str, String str2) {
        if (checkReady()) {
            try {
                return this.mService.getSettingInteger(str, str2) != 0;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getSettingsInteger(String str, String str2) {
        if (checkReady()) {
            try {
                return this.mService.getSettingInteger(str, str2);
            } catch (RemoteException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public String getSettingsString(String str, String str2) {
        if (checkReady()) {
            try {
                return this.mService.getSettingString(str, str2);
            } catch (RemoteException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public boolean registerAppForBleEvent(String str, int i) {
        if (checkReady()) {
            try {
                return this.mService.setSettingString(str, getSettingKeyForEvent(i), this.mContext.getPackageName());
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean registerBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) {
        if (iBleEventCallback != null && checkReady()) {
            try {
                return this.mService.registerBleEventListener(str, i, iBleEventCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean setSettings(String str, String str2, int i) {
        if (checkReady()) {
            try {
                return this.mService.setSettingInteger(str, str2, i);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean setSettings(String str, String str2, String str3) {
        if (checkReady()) {
            try {
                return this.mService.setSettingString(str, str2, str3);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean setSettings(String str, String str2, boolean z) {
        if (checkReady()) {
            try {
                return this.mService.setSettingInteger(str, str2, z ? 1 : 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean startScanDevice(final int i, final BluetoothAdapter.LeScanCallback leScanCallback) {
        if (checkReady()) {
            try {
                this.mService.startScanDevice(this.mToken, this.mClientId, i, new IScanDeviceCallback.Stub() { // from class: miui.bluetooth.ble.MiBleDeviceManager.3
                    @Override // miui.bluetooth.ble.IScanDeviceCallback
                    public void onScanDevice(int i2, BluetoothDevice bluetoothDevice, int i3, byte[] bArr) throws RemoteException {
                        if (i2 == i) {
                            leScanCallback.onLeScan(bluetoothDevice, i3, bArr);
                        }
                    }
                });
                return false;
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void stopScanDevice() {
        if (checkReady()) {
            try {
                this.mService.stopScanDevice(this.mClientId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean unbindDevice(String str) {
        if (checkReady()) {
            try {
                return this.mService.deleteSettings(str);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean unregisterAppForBleEvent(String str, int i) {
        if (checkReady()) {
            try {
                return this.mService.setSettingString(str, getSettingKeyForEvent(i), null);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean unregisterBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) {
        if (checkReady()) {
            try {
                return this.mService.unregisterBleEventListener(str, i, iBleEventCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
