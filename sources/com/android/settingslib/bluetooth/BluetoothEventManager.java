package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;
import com.android.settingslib.R$string;
import com.mediatek.bt.BluetoothLeAudioFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import miui.payment.PaymentManager;
import miui.telephony.TelephonyManager;

/* loaded from: classes2.dex */
public class BluetoothEventManager {
    private static final boolean DEBUG = Log.isLoggable("BluetoothEventManager", 3);
    private final BroadcastReceiver mBroadcastReceiver;
    private final Context mContext;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private final android.os.Handler mHandler;
    private final LocalBluetoothAdapter mLocalAdapter;
    private final BroadcastReceiver mProfileBroadcastReceiver;
    private final android.os.Handler mReceiverHandler;
    private final UserHandle mUserHandle;
    private final Collection<BluetoothCallback> mCallbacks = new CopyOnWriteArrayList();
    private final IntentFilter mAdapterIntentFilter = new IntentFilter();
    private final IntentFilter mProfileIntentFilter = new IntentFilter();
    private final Map<String, Handler> mHandlerMap = new HashMap();

    /* loaded from: classes2.dex */
    private class AclStateChangedHandler implements Handler {
        private AclStateChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            int i;
            if (bluetoothDevice == null) {
                Log.w("BluetoothEventManager", "AclStateChangedHandler: device is null");
            } else if (BluetoothEventManager.this.mDeviceManager.isSubDevice(bluetoothDevice)) {
            } else {
                String action = intent.getAction();
                if (action == null) {
                    Log.w("BluetoothEventManager", "AclStateChangedHandler: action is null");
                    return;
                }
                CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    Log.w("BluetoothEventManager", "AclStateChangedHandler: activeDevice is null");
                    return;
                }
                if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                    i = 2;
                } else if (!action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                    Log.w("BluetoothEventManager", "ActiveDeviceChangedHandler: unknown action " + action);
                    return;
                } else {
                    i = 0;
                }
                BluetoothEventManager.this.dispatchAclStateChanged(findDevice, i);
            }
        }
    }

    /* loaded from: classes2.dex */
    private class ActiveDeviceChangedHandler implements Handler {
        private ActiveDeviceChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            String action = intent.getAction();
            if (action == null) {
                Log.w("BluetoothEventManager", "ActiveDeviceChangedHandler: action is null");
                return;
            }
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            int i = 2;
            if (!action.equals("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED")) {
                if (action.equals("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED")) {
                    if (bluetoothDevice != null && bluetoothDevice.getType() == 2 && bluetoothDevice.isDualModeDevice()) {
                        Log.d("BluetoothEventManager", "Fake HFP active device broadcast,return");
                        return;
                    }
                    i = 1;
                } else if (action.equals("android.bluetooth.hearingaid.profile.action.ACTIVE_DEVICE_CHANGED")) {
                    i = 21;
                } else if (!action.equals(BluetoothLeAudioFactory.getInstance().getLeAudioActiveDeviceChangedAction())) {
                    Log.w("BluetoothEventManager", "ActiveDeviceChangedHandler: unknown action " + action);
                    return;
                } else {
                    i = BluetoothLeAudioFactory.getInstance().getLeAudioProfileId();
                }
            }
            BluetoothEventManager.this.dispatchActiveDeviceChanged(findDevice, i);
        }
    }

    /* loaded from: classes2.dex */
    private class AdapterStateChangedHandler implements Handler {
        private AdapterStateChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            if (BluetoothEventManager.this.mHandler != null) {
                BluetoothEventManager.this.mHandler.sendMessage(BluetoothEventManager.this.mHandler.obtainMessage(1, Integer.valueOf(intExtra)));
            }
        }
    }

    /* loaded from: classes2.dex */
    private class AudioModeChangedHandler implements Handler {
        private AudioModeChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            if (intent.getAction() == null) {
                Log.w("BluetoothEventManager", "AudioModeChangedHandler() action is null");
            } else {
                BluetoothEventManager.this.dispatchAudioModeChanged();
            }
        }
    }

    /* loaded from: classes2.dex */
    private class BatteryLevelChangedHandler implements Handler {
        private BatteryLevelChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                findDevice.refresh();
            }
        }
    }

    /* loaded from: classes2.dex */
    private class BluetoothBroadcastReceiver extends BroadcastReceiver {
        private BluetoothBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            Handler handler = (Handler) BluetoothEventManager.this.mHandlerMap.get(action);
            if (handler != null) {
                handler.onReceive(context, intent, bluetoothDevice);
            }
        }
    }

    /* loaded from: classes2.dex */
    private class BondStateChangedHandler implements Handler {
        private BondStateChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            if (bluetoothDevice == null) {
                Log.e("BluetoothEventManager", "ACTION_BOND_STATE_CHANGED with no EXTRA_DEVICE");
                return;
            }
            int intExtra = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
            if (BluetoothEventManager.this.mHandler != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(PaymentManager.KEY_INTENT, intent);
                bundle.putParcelable("device", bluetoothDevice);
                Message message = new Message();
                message.obj = context;
                message.what = 3;
                message.arg1 = intExtra;
                message.setData(bundle);
                BluetoothEventManager.this.mHandler.sendMessage(message);
            }
            BluetoothEventManager.this.mDeviceManager.checkAutoBondIfNeed(bluetoothDevice, intExtra);
        }
    }

    /* loaded from: classes2.dex */
    private class ClassChangedHandler implements Handler {
        private ClassChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            if (BluetoothEventManager.this.mHandler != null) {
                Message obtainMessage = BluetoothEventManager.this.mHandler.obtainMessage(5);
                obtainMessage.obj = bluetoothDevice;
                BluetoothEventManager.this.mHandler.sendMessage(obtainMessage);
            }
        }
    }

    /* loaded from: classes2.dex */
    private class ConnectionStateChangedHandler implements Handler {
        private ConnectionStateChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.dispatchConnectionStateChanged(BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice), intent.getIntExtra("android.bluetooth.adapter.extra.CONNECTION_STATE", Integer.MIN_VALUE));
        }
    }

    /* loaded from: classes2.dex */
    private class DeviceFoundHandler implements Handler {
        private DeviceFoundHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            short shortExtra = intent.getShortExtra("android.bluetooth.device.extra.RSSI", Short.MIN_VALUE);
            intent.getStringExtra("android.bluetooth.device.extra.NAME");
            if (BluetoothEventManager.this.mHandler != null) {
                Bundle bundle = new Bundle();
                bundle.putShort("rssi", shortExtra);
                Message message = new Message();
                message.obj = bluetoothDevice;
                message.what = 2;
                message.setData(bundle);
                BluetoothEventManager.this.mHandler.sendMessage(message);
            }
        }
    }

    /* loaded from: classes2.dex */
    private class DumoInfoHandler implements Handler {
        private DumoInfoHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            String stringExtra = intent.getStringExtra("android.bluetooth.device.extra.DEVICE_BR");
            String stringExtra2 = intent.getStringExtra("android.bluetooth.device.extra.DEVICE_LE1");
            String stringExtra3 = intent.getStringExtra("android.bluetooth.device.extra.DEVICE_LE2");
            Log.d("BluetoothEventManager", "onDumoGroupInfo brStr = " + stringExtra + " le1Str = " + stringExtra2 + " le2Str = " + stringExtra3);
            BluetoothEventManager.this.mDeviceManager.setDumoGroupInfo(stringExtra, stringExtra2, stringExtra3);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface Handler {
        void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice);
    }

    /* loaded from: classes2.dex */
    private class LeaduioConfigChangedHandler implements Handler {
        private LeaduioConfigChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            Log.d("BluetoothEventManager", "LeaduioConfigChangedHandler");
            if (BluetoothLeAudioFactory.getInstance().isLeAudioProfileSupported(context) && BluetoothLeAudioFactory.getInstance().getLeAudioConfChangedAction().equals(intent.getAction())) {
                BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                intent.getIntExtra(BluetoothLeAudioFactory.getInstance().getLeAudioGroupIdExtra(), -1);
                Log.d("BluetoothEventManager", "LeaduioConfigChangedHandler: " + bluetoothDevice2);
                Toast.makeText(context, "only can support one kit", 0).show();
            }
        }
    }

    /* loaded from: classes2.dex */
    private class NameChangedHandler implements Handler {
        private NameChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.mDeviceManager.onDeviceNameUpdated(bluetoothDevice);
        }
    }

    /* loaded from: classes2.dex */
    private class ScanningStateChangedHandler implements Handler {
        private final boolean mStarted;

        ScanningStateChangedHandler(boolean z) {
            this.mStarted = z;
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            if (BluetoothEventManager.this.mHandler != null) {
                Message obtainMessage = BluetoothEventManager.this.mHandler.obtainMessage(4);
                obtainMessage.obj = Boolean.valueOf(this.mStarted);
                BluetoothEventManager.this.mHandler.sendMessage(obtainMessage);
            }
        }
    }

    /* loaded from: classes2.dex */
    private class SetMemberAvailableHandler implements Handler {
        private SetMemberAvailableHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            Log.d("BluetoothEventManager", "setMemberAvailable device = " + bluetoothDevice);
            BluetoothEventManager.this.mDeviceManager.checkAutoBondIfNeed(bluetoothDevice, 0);
        }
    }

    /* loaded from: classes2.dex */
    private class UuidChangedHandler implements Handler {
        private UuidChangedHandler() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                findDevice.onUuidChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothEventManager(LocalBluetoothAdapter localBluetoothAdapter, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, Context context, android.os.Handler handler, UserHandle userHandle) {
        this.mBroadcastReceiver = new BluetoothBroadcastReceiver();
        this.mProfileBroadcastReceiver = new BluetoothBroadcastReceiver();
        this.mLocalAdapter = localBluetoothAdapter;
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mContext = context;
        this.mUserHandle = userHandle;
        this.mReceiverHandler = handler;
        addHandler("android.bluetooth.adapter.action.STATE_CHANGED", new AdapterStateChangedHandler());
        addHandler("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED", new ConnectionStateChangedHandler());
        addHandler("android.bluetooth.adapter.action.DISCOVERY_STARTED", new ScanningStateChangedHandler(true));
        addHandler("android.bluetooth.adapter.action.DISCOVERY_FINISHED", new ScanningStateChangedHandler(false));
        addHandler("android.bluetooth.device.action.FOUND", new DeviceFoundHandler());
        addHandler("android.bluetooth.device.action.NAME_CHANGED", new NameChangedHandler());
        addHandler("android.bluetooth.device.action.ALIAS_CHANGED", new NameChangedHandler());
        addHandler("com.android.bluetooth.btservice.action.DUAL_MODE_INFO", new DumoInfoHandler());
        if (BluetoothLeAudioFactory.getInstance().isLeAudioProfileSupported(context)) {
            addHandler(BluetoothLeAudioFactory.getInstance().getLeAudioSetMemberAvailableAction(), new SetMemberAvailableHandler());
        }
        addHandler("android.bluetooth.device.action.BOND_STATE_CHANGED", new BondStateChangedHandler());
        addHandler("android.bluetooth.device.action.CLASS_CHANGED", new ClassChangedHandler());
        addHandler("android.bluetooth.device.action.UUID", new UuidChangedHandler());
        addHandler("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED", new BatteryLevelChangedHandler());
        addHandler("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        addHandler("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        addHandler("android.bluetooth.hearingaid.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        if (BluetoothLeAudioFactory.getInstance().isLeAudioProfileSupported(context)) {
            addHandler(BluetoothLeAudioFactory.getInstance().getLeAudioActiveDeviceChangedAction(), new ActiveDeviceChangedHandler());
        }
        addHandler("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED", new AudioModeChangedHandler());
        addHandler(TelephonyManager.ACTION_PHONE_STATE_CHANGED, new AudioModeChangedHandler());
        addHandler("android.bluetooth.device.action.ACL_CONNECTED", new AclStateChangedHandler());
        addHandler("android.bluetooth.device.action.ACL_DISCONNECTED", new AclStateChangedHandler());
        if (BluetoothLeAudioFactory.getInstance().isLeAudioProfileSupported(context)) {
            addHandler(BluetoothLeAudioFactory.getInstance().getLeAudioConfChangedAction(), new LeaduioConfigChangedHandler());
        }
        registerAdapterIntentReceiver();
        this.mHandler = new android.os.Handler(context.getMainLooper()) { // from class: com.android.settingslib.bluetooth.BluetoothEventManager.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                int i = message.what;
                if (i == 1) {
                    BluetoothEventManager.this.handleAdapterStateChanged(((Integer) message.obj).intValue());
                } else if (i == 2) {
                    BluetoothEventManager.this.handleDeviceFound(message.getData().getShort("rssi"), (BluetoothDevice) message.obj);
                } else if (i == 3) {
                    Bundle data = message.getData();
                    BluetoothEventManager.this.handleBondStateChanged(message.arg1, (BluetoothDevice) data.getParcelable("device"), (Intent) data.getParcelable(PaymentManager.KEY_INTENT), (Context) message.obj);
                } else if (i == 4) {
                    BluetoothEventManager.this.handleDiscoveryStateChanged(((Boolean) message.obj).booleanValue());
                } else if (i != 5) {
                } else {
                    BluetoothEventManager.this.handleClassChanged((BluetoothDevice) message.obj);
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchAclStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onAclConnectionStateChanged(cachedBluetoothDevice, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchAudioModeChanged() {
        Iterator<CachedBluetoothDevice> it = this.mDeviceManager.getCachedDevicesCopy().iterator();
        while (it.hasNext()) {
            it.next().onAudioModeChanged();
        }
        Iterator<BluetoothCallback> it2 = this.mCallbacks.iterator();
        while (it2.hasNext()) {
            it2.next().onAudioModeChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onConnectionStateChanged(cachedBluetoothDevice, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAdapterStateChanged(int i) {
        this.mLocalAdapter.setBluetoothStateInt(i);
        Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onBluetoothStateChanged(i);
        }
        this.mDeviceManager.onBluetoothStateChanged(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBondStateChanged(int i, BluetoothDevice bluetoothDevice, Intent intent, Context context) {
        try {
            CachedBluetoothDevice findDevice = this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice == null) {
                Log.w("BluetoothEventManager", "Got bonding state changed for " + bluetoothDevice + ", but we have no record of that device.");
                findDevice = this.mDeviceManager.addDevice(bluetoothDevice);
            }
            Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                it.next().onDeviceBondStateChanged(findDevice, i);
            }
            findDevice.onBondingStateChanged(i);
            if (i == 10) {
                if (findDevice.getHiSyncId() != 0) {
                    this.mDeviceManager.onDeviceUnpaired(findDevice);
                }
                showUnbondMessage(context, findDevice.getName(), intent.getIntExtra("android.bluetooth.device.extra.REASON", Integer.MIN_VALUE));
            }
            this.mDeviceManager.checkAutoBondIfNeed(bluetoothDevice, i);
        } catch (Exception e) {
            Log.e("BluetoothEventManager", "handleBondStateChanged Exception:" + e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleClassChanged(BluetoothDevice bluetoothDevice) {
        try {
            if (BluetoothDeviceFilter.BONDED_DEVICE_FILTER.matches(bluetoothDevice)) {
                Log.d("BluetoothEventManager", "a bonded device: " + bluetoothDevice + " bt class changed, do nothing.");
                return;
            }
            CachedBluetoothDevice findDevice = this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                for (BluetoothCallback bluetoothCallback : this.mCallbacks) {
                    bluetoothCallback.onDeviceDeleted(findDevice);
                    bluetoothCallback.onDeviceAdded(findDevice);
                }
                findDevice.refresh();
            }
        } catch (Exception e) {
            Log.e("BluetoothEventManager", "handleClassChanged Exception:" + e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDeviceFound(short s, BluetoothDevice bluetoothDevice) {
        try {
            CachedBluetoothDevice findDevice = this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice == null) {
                findDevice = this.mDeviceManager.addDevice(bluetoothDevice, s);
                Log.d("BluetoothEventManager", "DeviceFoundHandler created new CachedBluetoothDevice: " + findDevice + " rssi: " + ((int) s));
            } else if (findDevice.getBondState() != 12 || findDevice.getDevice().isConnected()) {
                Log.d("BluetoothEventManager", "DeviceFoundHandler found existing CachedBluetoothDevice:" + findDevice);
            } else {
                dispatchDeviceAdded(findDevice);
                Log.d("BluetoothEventManager", "DeviceFoundHandler found bonded and not connected device:" + findDevice);
            }
            findDevice.setRssi(s);
            findDevice.setJustDiscovered(true);
        } catch (Exception e) {
            Log.e("BluetoothEventManager", "handleDeviceFound E: " + e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDiscoveryStateChanged(boolean z) {
        try {
            Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                it.next().onScanningStateChanged(z);
            }
            this.mDeviceManager.onScanningStateChanged(z);
        } catch (Exception e) {
            Log.e("BluetoothEventManager", "handleDiscoveryStateChanged Exception:" + e.toString());
        }
    }

    private void registerIntentReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        UserHandle userHandle = this.mUserHandle;
        if (userHandle == null) {
            this.mContext.registerReceiver(broadcastReceiver, intentFilter, null, this.mReceiverHandler);
        } else {
            this.mContext.registerReceiverAsUser(broadcastReceiver, userHandle, intentFilter, null, this.mReceiverHandler);
        }
    }

    private void showUnbondMessage(Context context, String str, int i) {
        int i2;
        switch (i) {
            case 1:
                i2 = R$string.bluetooth_pairing_pin_error_message;
                break;
            case 2:
                i2 = R$string.bluetooth_pairing_rejected_error_message;
                break;
            case 3:
            default:
                Log.w("BluetoothEventManager", "showUnbondMessage: Not displaying any message for reason: " + i);
                return;
            case 4:
                i2 = R$string.bluetooth_pairing_device_down_error_message;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                i2 = R$string.bluetooth_pairing_error_message;
                break;
        }
        BluetoothUtils.showError(context, str, i2);
    }

    void addHandler(String str, Handler handler) {
        this.mHandlerMap.put(str, handler);
        this.mAdapterIntentFilter.addAction(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addProfileHandler(String str, Handler handler) {
        this.mHandlerMap.put(str, handler);
        this.mProfileIntentFilter.addAction(str);
    }

    void dispatchActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        for (CachedBluetoothDevice cachedBluetoothDevice2 : this.mDeviceManager.getCachedDevicesCopy()) {
            cachedBluetoothDevice2.onActiveDeviceChanged(Objects.equals(cachedBluetoothDevice2, cachedBluetoothDevice), i);
        }
        Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onActiveDeviceChanged(cachedBluetoothDevice, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onDeviceAdded(cachedBluetoothDevice);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchDeviceRemoved(CachedBluetoothDevice cachedBluetoothDevice) {
        Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onDeviceDeleted(cachedBluetoothDevice);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchHearingAidAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onHearingAidAdded(cachedBluetoothDevice);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchHearingAidRemoved(CachedBluetoothDevice cachedBluetoothDevice) {
        Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onHearingAidDeleted(cachedBluetoothDevice);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        Iterator<BluetoothCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onProfileConnectionStateChanged(cachedBluetoothDevice, i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean readPairedDevices() {
        Set<BluetoothDevice> bondedDevices = this.mLocalAdapter.getBondedDevices();
        boolean z = false;
        if (bondedDevices == null) {
            return false;
        }
        for (BluetoothDevice bluetoothDevice : bondedDevices) {
            if (this.mDeviceManager.findDevice(bluetoothDevice) == null) {
                this.mDeviceManager.addDevice(bluetoothDevice);
                z = true;
            }
        }
        return z;
    }

    void registerAdapterIntentReceiver() {
        registerIntentReceiver(this.mBroadcastReceiver, this.mAdapterIntentFilter);
    }

    public void registerCallback(BluetoothCallback bluetoothCallback) {
        this.mCallbacks.add(bluetoothCallback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerProfileIntentReceiver() {
        registerIntentReceiver(this.mProfileBroadcastReceiver, this.mProfileIntentFilter);
    }

    public void unregisterCallback(BluetoothCallback bluetoothCallback) {
        this.mCallbacks.remove(bluetoothCallback);
    }
}
