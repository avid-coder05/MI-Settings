package miui.bluetooth.ble;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import java.util.UUID;
import miui.bluetooth.ble.IBluetoothMiBle;
import miui.bluetooth.ble.IBluetoothMiBleCallback;
import miui.bluetooth.ble.IBluetoothMiBlePropertyCallback;

/* loaded from: classes3.dex */
public class MiBleProfile {
    public static final String ACTION_MIBLE_SERVICE = "miui.bluetooth.mible.Service";
    public static final String ACTION_SELECT_DEVICE = "miui.bluetooth.action.PICK_DEVICE";
    protected static final boolean DBG = true;
    public static final String EXTRA_MIBLE_PROPERTY = "miui.bluetooth.extra.MIBLE_PROPERTY";
    private static final int MSG_PROPERTY = 2;
    private static final int MSG_STATUS = 1;
    public static final int PROPERTY_ALARM_CLOCK = 106;
    public static final int PROPERTY_ALERT_NOTIFICATION = 8;
    public static final int PROPERTY_BATTERY = 6;
    public static final int PROPERTY_DEVICE_CONTROL = 2;
    public static final int PROPERTY_DEVICE_INFO = 101;
    public static final int PROPERTY_FIRMWARE = 3;
    public static final int PROPERTY_IMMEDIATE_ALERT = 5;
    public static final int PROPERTY_LINK_LOSS = 7;
    public static final int PROPERTY_MI_BAND_EVENT = 108;
    public static final int PROPERTY_MI_KEY = 107;
    @Deprecated
    public static final int PROPERTY_PAY = 4;
    public static final int PROPERTY_SPORT_ACTIVITIES = 104;
    public static final int PROPERTY_SPORT_STEPS = 103;
    public static final int PROPERTY_THEME_COLOR = 105;
    public static final int PROPERTY_UNDEFINED = 0;
    @Deprecated
    public static final int PROPERTY_UNLOCK = 1;
    public static final int PROPERTY_USER_INFO = 102;
    public static final int SERVICE_VERSION_UNKNOWN = -1;
    public static final int STATUS_CONNECTED = 2;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_DISCONNECTING = 3;
    public static final int STATUS_ERROR = -1;
    public static final int STATUS_READY = 4;
    protected static final String TAG = "MiBleProfile";
    protected IProfileStateChangeCallback mCallback;
    protected final ParcelUuid mClientId;
    private boolean mConnectWhenBind;
    protected Context mContext;
    protected String mDevice;
    private Handler mHandler;
    private int mProfileState;
    private IBluetoothMiBlePropertyCallback mPropertyCallback;
    private SparseArray<IPropertyNotifyCallback> mPropertyCallbacks;
    protected IBluetoothMiBle mService;
    private IBluetoothMiBleCallback mServiceCallback;
    private ServiceConnection mServiceConnection;
    private final IBinder mToken;

    /* loaded from: classes3.dex */
    public interface IProfileStateChangeCallback {
        void onState(int i);
    }

    /* loaded from: classes3.dex */
    public interface IPropertyNotifyCallback {
        void notifyProperty(int i, byte[] bArr);
    }

    public MiBleProfile(Context context, String str) {
        this(context, str, null);
    }

    public MiBleProfile(Context context, String str, IProfileStateChangeCallback iProfileStateChangeCallback) {
        this.mClientId = new ParcelUuid(UUID.randomUUID());
        this.mToken = new Binder();
        this.mConnectWhenBind = false;
        this.mPropertyCallbacks = new SparseArray<>();
        this.mProfileState = 0;
        this.mServiceConnection = new ServiceConnection() { // from class: miui.bluetooth.ble.MiBleProfile.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(MiBleProfile.TAG, "onServiceConnected()");
                MiBleProfile.this.mService = IBluetoothMiBle.Stub.asInterface(iBinder);
                MiBleProfile.this.mHandler.sendMessage(MiBleProfile.this.mHandler.obtainMessage(1, 2, 0));
                try {
                    MiBleProfile miBleProfile = MiBleProfile.this;
                    IBluetoothMiBle iBluetoothMiBle = miBleProfile.mService;
                    IBinder iBinder2 = miBleProfile.mToken;
                    MiBleProfile miBleProfile2 = MiBleProfile.this;
                    iBluetoothMiBle.registerClient(iBinder2, miBleProfile2.mDevice, miBleProfile2.mClientId, miBleProfile2.mServiceCallback);
                } catch (RemoteException e) {
                    Log.e(MiBleProfile.TAG, "onServiceConnected", e);
                    MiBleProfile.this.mHandler.sendMessage(MiBleProfile.this.mHandler.obtainMessage(1, -1, 0));
                }
                if (MiBleProfile.this.mConnectWhenBind) {
                    MiBleProfile.this.mConnectWhenBind = false;
                    MiBleProfile.this.connect();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                MiBleProfile miBleProfile = MiBleProfile.this;
                miBleProfile.mService = null;
                miBleProfile.mProfileState = 0;
                MiBleProfile.this.mHandler.sendMessage(MiBleProfile.this.mHandler.obtainMessage(1, 0, 0));
            }
        };
        this.mServiceCallback = new IBluetoothMiBleCallback.Stub() { // from class: miui.bluetooth.ble.MiBleProfile.2
            @Override // miui.bluetooth.ble.IBluetoothMiBleCallback
            public void onConnectionState(ParcelUuid parcelUuid, int i) throws RemoteException {
                Log.d(MiBleProfile.TAG, "onConnectionState() sate=" + i);
                if (MiBleProfile.this.mClientId.equals(parcelUuid)) {
                    MiBleProfile.this.mProfileState = i;
                    MiBleProfile.this.mHandler.sendMessage(MiBleProfile.this.mHandler.obtainMessage(1, i, 0));
                }
            }
        };
        this.mPropertyCallback = new IBluetoothMiBlePropertyCallback.Stub() { // from class: miui.bluetooth.ble.MiBleProfile.3
            @Override // miui.bluetooth.ble.IBluetoothMiBlePropertyCallback
            public void notifyProperty(ParcelUuid parcelUuid, int i, byte[] bArr) throws RemoteException {
                Log.d(MiBleProfile.TAG, "mPropertyCallback() property=" + i);
                if (MiBleProfile.this.mClientId.equals(parcelUuid)) {
                    Message obtainMessage = MiBleProfile.this.mHandler.obtainMessage(2);
                    obtainMessage.arg1 = i;
                    obtainMessage.obj = bArr;
                    MiBleProfile.this.mHandler.sendMessage(obtainMessage);
                }
            }
        };
        this.mDevice = str;
        this.mContext = context;
        this.mCallback = iProfileStateChangeCallback;
        try {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "prepare looper failed", e);
        }
        this.mHandler = new Handler(new Handler.Callback() { // from class: miui.bluetooth.ble.MiBleProfile.4
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    IProfileStateChangeCallback iProfileStateChangeCallback2 = MiBleProfile.this.mCallback;
                    if (iProfileStateChangeCallback2 != null) {
                        iProfileStateChangeCallback2.onState(message.arg1);
                    }
                    return true;
                } else if (i == 2) {
                    int i2 = message.arg1;
                    IPropertyNotifyCallback iPropertyNotifyCallback = (IPropertyNotifyCallback) MiBleProfile.this.mPropertyCallbacks.get(i2);
                    if (iPropertyNotifyCallback != null) {
                        iPropertyNotifyCallback.notifyProperty(i2, (byte[]) message.obj);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void connect() {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle != null) {
            try {
                iBluetoothMiBle.connect(this.mDevice, this.mClientId);
                return;
            } catch (RemoteException e) {
                Log.w(TAG, "connect: ", e);
                Handler handler = this.mHandler;
                handler.sendMessage(handler.obtainMessage(1, -1, 0));
                return;
            }
        }
        this.mConnectWhenBind = true;
        Intent intent = new Intent(ACTION_MIBLE_SERVICE);
        intent.setClassName("com.xiaomi.bluetooth", "com.android.bluetooth.ble.BluetoothMiBleService");
        intent.setPackage("com.xiaomi.bluetooth");
        boolean bindService = this.mContext.bindService(intent, this.mServiceConnection, 1);
        if (!bindService) {
            Intent intent2 = new Intent(ACTION_MIBLE_SERVICE);
            intent2.setClassName("com.android.bluetooth", "com.android.bluetooth.ble.BluetoothMiBleService");
            intent2.setPackage("com.android.bluetooth");
            bindService = this.mContext.bindService(intent2, this.mServiceConnection, 1);
            intent = intent2;
        }
        if (bindService) {
            return;
        }
        Log.e(TAG, "connect: bind service error" + intent.toString());
        Handler handler2 = this.mHandler;
        handler2.sendMessage(handler2.obtainMessage(1, -1, 0));
    }

    public void disconnect() {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle == null) {
            return;
        }
        try {
            iBluetoothMiBle.unregisterClient(this.mToken, this.mDevice, this.mClientId);
            this.mContext.unbindService(this.mServiceConnection);
        } catch (RemoteException e) {
            Log.w(TAG, "disconnect: ", e);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(1, -1, 0));
        } catch (IllegalArgumentException e2) {
            Log.w(TAG, "disconnect: ", e2);
            Handler handler2 = this.mHandler;
            handler2.sendMessage(handler2.obtainMessage(1, -1, 0));
        }
    }

    public String getDeviceAddress() {
        return this.mDevice;
    }

    public byte[] getProperty(int i) {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle != null) {
            try {
                return iBluetoothMiBle.getProperty(this.mDevice, this.mClientId, i);
            } catch (RemoteException e) {
                Log.e(TAG, "getProperty: ", e);
                return null;
            }
        }
        return null;
    }

    public int getRssi() {
        if (isReady()) {
            try {
                return this.mService.getRssi(this.mDevice, this.mClientId);
            } catch (RemoteException e) {
                Log.e(TAG, "getRssi: ", e);
                return Integer.MIN_VALUE;
            }
        }
        return Integer.MIN_VALUE;
    }

    public int getServiceVersion() {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle != null) {
            try {
                return iBluetoothMiBle.getServiceVersion();
            } catch (RemoteException e) {
                Log.e(TAG, "getServiceVersion", e);
                return -1;
            }
        }
        return -1;
    }

    public boolean isReady() {
        return this.mProfileState == 4;
    }

    public boolean isSupportProperty(int i) {
        try {
            IBluetoothMiBle iBluetoothMiBle = this.mService;
            if (iBluetoothMiBle != null) {
                return iBluetoothMiBle.supportProperty(this.mDevice, i);
            }
            return false;
        } catch (RemoteException e) {
            Log.e(TAG, "isSupportProperty: ", e);
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0021  */
    /* JADX WARN: Removed duplicated region for block: B:12:0x0027 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean registerPropertyNotifyCallback(int r5, miui.bluetooth.ble.MiBleProfile.IPropertyNotifyCallback r6) {
        /*
            r4 = this;
            android.util.SparseArray<miui.bluetooth.ble.MiBleProfile$IPropertyNotifyCallback> r0 = r4.mPropertyCallbacks
            r0.put(r5, r6)
            miui.bluetooth.ble.IBluetoothMiBle r6 = r4.mService
            r0 = 0
            if (r6 == 0) goto L1e
            java.lang.String r1 = r4.mDevice     // Catch: android.os.RemoteException -> L15
            android.os.ParcelUuid r2 = r4.mClientId     // Catch: android.os.RemoteException -> L15
            miui.bluetooth.ble.IBluetoothMiBlePropertyCallback r3 = r4.mPropertyCallback     // Catch: android.os.RemoteException -> L15
            boolean r6 = r6.registerPropertyCallback(r1, r2, r5, r3)     // Catch: android.os.RemoteException -> L15
            goto L1f
        L15:
            r6 = move-exception
            java.lang.String r1 = "MiBleProfile"
            java.lang.String r2 = "registerPropertyNotifyCallback: "
            android.util.Log.e(r1, r2, r6)
        L1e:
            r6 = r0
        L1f:
            if (r6 != 0) goto L27
            android.util.SparseArray<miui.bluetooth.ble.MiBleProfile$IPropertyNotifyCallback> r4 = r4.mPropertyCallbacks
            r4.remove(r5)
            return r0
        L27:
            r4 = 1
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.bluetooth.ble.MiBleProfile.registerPropertyNotifyCallback(int, miui.bluetooth.ble.MiBleProfile$IPropertyNotifyCallback):boolean");
    }

    public void setProfileStateChangeCallback(IProfileStateChangeCallback iProfileStateChangeCallback) {
        this.mCallback = iProfileStateChangeCallback;
    }

    public boolean setProperty(int i, byte[] bArr) {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle != null) {
            try {
                return iBluetoothMiBle.setProperty(this.mDevice, this.mClientId, i, bArr);
            } catch (RemoteException e) {
                Log.e(TAG, "setProperty: ", e);
                return false;
            }
        }
        return false;
    }

    public boolean unregisterPropertyNotifyCallback(int i) {
        this.mPropertyCallbacks.remove(i);
        if (this.mPropertyCallbacks.get(i) == null) {
            try {
                IBluetoothMiBle iBluetoothMiBle = this.mService;
                if (iBluetoothMiBle != null) {
                    return iBluetoothMiBle.unregisterPropertyCallback(this.mDevice, this.mClientId, i, this.mPropertyCallback);
                }
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, "unregisterPropertyNotifyCallback: ", e);
                return false;
            }
        }
        return true;
    }
}
