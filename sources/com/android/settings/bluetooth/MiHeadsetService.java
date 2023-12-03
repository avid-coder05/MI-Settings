package com.android.settings.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class MiHeadsetService extends Service implements CachedBluetoothDevice.Callback {
    private static final int A2DP_PROFILE_CLOSE = 1;
    private static final int ALL_PROFILE_CLOSE = 0;
    public static final String EXTRA_DEVICE = "device";
    private static final int HFP_PROFILE_CLOSE = 2;
    public static final int MSG_FINISH_SERVICE = 101;
    public static final int MSG_INIT_SERVICE = 100;
    private static final String TAG = "MiHeadsetService";
    public static MiHeadsetService mInstance;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothHeadset mBluetoothHfp;
    public CachedBluetoothDevice mCachedDevice;
    private BluetoothDevice mDevice;
    private LocalBluetoothManager mManager;
    public LocalBluetoothProfileManager mProfileManager;
    private HandlerThread mThread;
    private final Object mBluetoothA2dpLock = new Object();
    private final Object mBluetoothHfpLock = new Object();
    public WeakReference<IMiHeadsetInterfaceImpl> sCallbackRef = null;
    private Intent mPendingIntent = null;
    public boolean mServiceInited = false;
    private MessageHandler mWorkHandler = null;
    private CachedBluetoothDeviceManager deviceManager = null;
    private BroadcastReceiver mBluetoothA2dpReceiver = null;
    private BluetoothProfile.ServiceListener mBluetoothA2dpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.bluetooth.MiHeadsetService.1
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            IMiHeadsetInterfaceImpl iMiHeadsetInterfaceImpl;
            Log.d(MiHeadsetService.TAG, "onA2dpServiceConnected()");
            synchronized (MiHeadsetService.this.mBluetoothA2dpLock) {
                try {
                    WeakReference<IMiHeadsetInterfaceImpl> weakReference = MiHeadsetService.this.sCallbackRef;
                    if (weakReference != null && (iMiHeadsetInterfaceImpl = weakReference.get()) != null) {
                        iMiHeadsetInterfaceImpl.a2dpconnected(i, bluetoothProfile);
                    }
                    MiHeadsetService.this.mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
                } catch (Exception e) {
                    Log.e(MiHeadsetService.TAG, "BluetoothA2dpServiceListener connected" + e);
                }
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            IMiHeadsetInterfaceImpl iMiHeadsetInterfaceImpl;
            Log.d(MiHeadsetService.TAG, "onA2dpServiceDisconnected()");
            synchronized (MiHeadsetService.this.mBluetoothA2dpLock) {
                MiHeadsetService.this.closeProfileProxy(1);
                try {
                    WeakReference<IMiHeadsetInterfaceImpl> weakReference = MiHeadsetService.this.sCallbackRef;
                    if (weakReference != null && (iMiHeadsetInterfaceImpl = weakReference.get()) != null) {
                        iMiHeadsetInterfaceImpl.a2dpdisconnected(i);
                    }
                    MiHeadsetService.this.mBluetoothA2dp = null;
                } catch (Exception e) {
                    Log.e(MiHeadsetService.TAG, "mBluetoothA2dpServiceListener disconnected " + e);
                }
            }
        }
    };
    private BluetoothProfile.ServiceListener mBluetoothHfpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.bluetooth.MiHeadsetService.2
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            IMiHeadsetInterfaceImpl iMiHeadsetInterfaceImpl;
            Log.d(MiHeadsetService.TAG, "onHfpServiceConnected()");
            synchronized (MiHeadsetService.this.mBluetoothHfpLock) {
                MiHeadsetService.this.mBluetoothHfp = (BluetoothHeadset) bluetoothProfile;
            }
            try {
                WeakReference<IMiHeadsetInterfaceImpl> weakReference = MiHeadsetService.this.sCallbackRef;
                if (weakReference == null || (iMiHeadsetInterfaceImpl = weakReference.get()) == null) {
                    return;
                }
                iMiHeadsetInterfaceImpl.hfpconnected(i, bluetoothProfile);
            } catch (Exception e) {
                Log.e(MiHeadsetService.TAG, "mBluetoothHfpServiceListener connected " + e);
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            IMiHeadsetInterfaceImpl iMiHeadsetInterfaceImpl;
            Log.d(MiHeadsetService.TAG, "onHfpServiceDisconnected()");
            synchronized (MiHeadsetService.this.mBluetoothHfpLock) {
                MiHeadsetService.this.closeProfileProxy(2);
            }
            try {
                WeakReference<IMiHeadsetInterfaceImpl> weakReference = MiHeadsetService.this.sCallbackRef;
                if (weakReference != null && (iMiHeadsetInterfaceImpl = weakReference.get()) != null) {
                    iMiHeadsetInterfaceImpl.hfpdisconnected(i);
                }
                MiHeadsetService.this.mBluetoothHfp = null;
            } catch (Exception e) {
                Log.e(MiHeadsetService.TAG, "mBluetoothHfpServiceListener disconnected " + e);
            }
        }
    };
    private MBinder mBinder = new MBinder();

    /* loaded from: classes.dex */
    public class MBinder extends Binder {
        public MBinder() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class MessageHandler extends Handler {
        private MessageHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Object obj;
            try {
                Log.e(MiHeadsetService.TAG, "handl message ");
                int i = message.what;
                Log.e(MiHeadsetService.TAG, "handl message " + i);
                if (i == 100 && (obj = message.obj) != null) {
                    MiHeadsetService.this.initService((Intent) obj);
                }
            } catch (Exception e) {
                Log.e(MiHeadsetService.TAG, "handleMessage error " + e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeProfileProxy(int i) {
        BluetoothHeadset bluetoothHeadset;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG, " " + i);
        if (defaultAdapter != null) {
            if (i == 0) {
                BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
                if (bluetoothA2dp != null) {
                    defaultAdapter.closeProfileProxy(2, bluetoothA2dp);
                    this.mBluetoothA2dp = null;
                }
                BluetoothHeadset bluetoothHeadset2 = this.mBluetoothHfp;
                if (bluetoothHeadset2 != null) {
                    defaultAdapter.closeProfileProxy(1, bluetoothHeadset2);
                    this.mBluetoothHfp = null;
                }
            } else if (i != 1) {
                if (i == 2 && (bluetoothHeadset = this.mBluetoothHfp) != null) {
                    defaultAdapter.closeProfileProxy(1, bluetoothHeadset);
                    this.mBluetoothHfp = null;
                }
            } else {
                BluetoothA2dp bluetoothA2dp2 = this.mBluetoothA2dp;
                if (bluetoothA2dp2 != null) {
                    defaultAdapter.closeProfileProxy(2, bluetoothA2dp2);
                    this.mBluetoothA2dp = null;
                }
            }
        }
    }

    private void createService() {
        try {
            Log.e(TAG, "createService here");
            if (this.mThread == null && this.mWorkHandler == null) {
                HandlerThread handlerThread = new HandlerThread("MiHeadsetServiceHandler");
                this.mThread = handlerThread;
                handlerThread.start();
                this.mWorkHandler = new MessageHandler(this.mThread.getLooper());
            }
            Intent intent = this.mPendingIntent;
            if (intent != null) {
                MessageHandler messageHandler = this.mWorkHandler;
                messageHandler.sendMessage(messageHandler.obtainMessage(100, intent));
            }
            IntentFilter intentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiHeadsetService.3
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent2) {
                    if (intent2.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                        int intExtra = intent2.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
                        if (intExtra == 10) {
                            MiHeadsetService.this.closeProfileProxy(0);
                        } else if (intExtra != 12) {
                        } else {
                            MiHeadsetService.this.getProfileProxy();
                        }
                    }
                }
            };
            this.mBluetoothA2dpReceiver = broadcastReceiver;
            registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            Log.e(TAG, "createService error " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getProfileProxy() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            return;
        }
        defaultAdapter.getProfileProxy(getApplicationContext().getApplicationContext(), this.mBluetoothA2dpServiceListener, 2);
        defaultAdapter.getProfileProxy(getApplicationContext().getApplicationContext(), this.mBluetoothHfpServiceListener, 1);
    }

    public static Object getService() {
        Log.e(TAG, "get the service " + mInstance);
        return mInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initService(Intent intent) {
        IMiHeadsetInterfaceImpl iMiHeadsetInterfaceImpl;
        try {
            Log.d(TAG, "onStart() " + intent);
            if (this.mPendingIntent != null) {
                this.mPendingIntent = null;
            }
            this.mDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            Log.d(TAG, "onStart() " + this.mDevice);
            LocalBluetoothManager localBtManager = Utils.getLocalBtManager(this);
            this.mManager = localBtManager;
            this.deviceManager = localBtManager.getCachedDeviceManager();
            this.mProfileManager = this.mManager.getProfileManager();
            Log.e(TAG, " " + this.mDevice + " " + this.mManager + " " + this.deviceManager + " " + this.mProfileManager);
            CachedBluetoothDevice findDevice = this.deviceManager.findDevice(this.mDevice);
            this.mCachedDevice = findDevice;
            if (findDevice == null) {
                CachedBluetoothDevice addDevice = this.deviceManager.addDevice(this.mDevice);
                this.mCachedDevice = addDevice;
                if (addDevice == null) {
                    Log.e(TAG, "cacheddevice is null error");
                    return;
                }
            }
            getProfileProxy();
            this.mCachedDevice.registerCallback(this);
            this.mServiceInited = true;
            WeakReference<IMiHeadsetInterfaceImpl> weakReference = this.sCallbackRef;
            if (weakReference == null || (iMiHeadsetInterfaceImpl = weakReference.get()) == null) {
                return;
            }
            iMiHeadsetInterfaceImpl.serviceInited();
        } catch (Exception e) {
            Log.e(TAG, "error initService " + e);
        }
    }

    public void connectProfile(BluetoothDevice bluetoothDevice, Object obj) {
        if (obj == null || bluetoothDevice == null) {
            return;
        }
        try {
            CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
            if (cachedBluetoothDevice == null || !cachedBluetoothDevice.getDevice().equals(bluetoothDevice)) {
                return;
            }
            this.mCachedDevice.connectProfile((LocalBluetoothProfile) obj);
        } catch (Exception e) {
            Log.e(TAG, "error connectProfile " + e);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        mInstance = this;
        return this.mBinder;
    }

    public void onCreate(Bundle bundle) {
        Log.d(TAG, "oncreate settings service interface");
        mInstance = this;
        createService();
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.d(TAG, "Destory ");
        HandlerThread handlerThread = this.mThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        BroadcastReceiver broadcastReceiver = this.mBluetoothA2dpReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            this.mBluetoothA2dpReceiver = null;
        }
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        IMiHeadsetInterfaceImpl iMiHeadsetInterfaceImpl;
        WeakReference<IMiHeadsetInterfaceImpl> weakReference = this.sCallbackRef;
        if (weakReference == null || (iMiHeadsetInterfaceImpl = weakReference.get()) == null) {
            return;
        }
        iMiHeadsetInterfaceImpl.onDeviceAttributesChanged();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.d(TAG, "onStartCommand service " + intent + " " + this.mWorkHandler);
        mInstance = this;
        MessageHandler messageHandler = this.mWorkHandler;
        if (messageHandler != null) {
            messageHandler.sendMessage(messageHandler.obtainMessage(100, intent));
            return 3;
        }
        this.mPendingIntent = intent;
        createService();
        return 3;
    }

    public void onStop() {
        Log.d(TAG, "onstop ");
        closeProfileProxy(0);
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return false;
    }

    public void setCallback(IMiHeadsetInterfaceImpl iMiHeadsetInterfaceImpl) {
        if (iMiHeadsetInterfaceImpl != null) {
            try {
                WeakReference<IMiHeadsetInterfaceImpl> weakReference = new WeakReference<>(iMiHeadsetInterfaceImpl);
                this.sCallbackRef = weakReference;
                IMiHeadsetInterfaceImpl iMiHeadsetInterfaceImpl2 = weakReference.get();
                if (this.mServiceInited) {
                    iMiHeadsetInterfaceImpl2.serviceInited();
                }
                BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
                if (bluetoothA2dp != null) {
                    iMiHeadsetInterfaceImpl2.a2dpconnected(2, bluetoothA2dp);
                }
                BluetoothHeadset bluetoothHeadset = this.mBluetoothHfp;
                if (bluetoothHeadset != null) {
                    iMiHeadsetInterfaceImpl2.hfpconnected(1, bluetoothHeadset);
                }
            } catch (Exception e) {
                Log.e(TAG, "error setCallback " + e);
            }
        }
    }
}
