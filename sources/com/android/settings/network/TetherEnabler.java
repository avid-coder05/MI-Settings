package com.android.settings.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.TetheringManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.widget.SwitchWidgetController;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public class TetherEnabler implements SwitchWidgetController.OnSwitchChangeListener, DataSaverBackend.Listener, LifecycleObserver {
    private static final boolean DEBUG = Log.isLoggable("TetherEnabler", 3);
    private final BluetoothAdapter mBluetoothAdapter;
    private boolean mBluetoothEnableForTether;
    private final AtomicReference<BluetoothPan> mBluetoothPan;
    @VisibleForTesting
    boolean mBluetoothTetheringStoppedByUser;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private final String mEthernetRegex;
    @VisibleForTesting
    final List<OnTetherStateUpdateListener> mListeners;
    private final Handler mMainThreadHandler;
    @VisibleForTesting
    ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback;
    private final SwitchWidgetController mSwitchWidgetController;
    private final BroadcastReceiver mTetherChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.TetherEnabler.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals("android.net.wifi.WIFI_AP_STATE_CHANGED", action) ? TetherEnabler.this.handleWifiApStateChanged(intent.getIntExtra("wifi_state", 14)) : TextUtils.equals("android.bluetooth.adapter.action.STATE_CHANGED", action) ? TetherEnabler.this.handleBluetoothStateChanged(intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE)) : false) {
                TetherEnabler.this.updateState(null);
            }
        }
    };
    @VisibleForTesting
    TetheringManager.TetheringEventCallback mTetheringEventCallback;
    private final TetheringManager mTetheringManager;
    private final UserManager mUserManager;
    private final WifiManager mWifiManager;

    /* loaded from: classes.dex */
    private static final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        final WeakReference<TetherEnabler> mTetherEnabler;

        OnStartTetheringCallback(TetherEnabler tetherEnabler) {
            this.mTetherEnabler = new WeakReference<>(tetherEnabler);
        }

        private void update() {
            TetherEnabler tetherEnabler = this.mTetherEnabler.get();
            if (tetherEnabler != null) {
                tetherEnabler.updateState(null);
            }
        }

        public void onTetheringFailed() {
            update();
        }

        public void onTetheringStarted() {
            update();
        }
    }

    /* loaded from: classes.dex */
    public interface OnTetherStateUpdateListener {
        void onTetherStateUpdated(int i);
    }

    public TetherEnabler(Context context, SwitchWidgetController switchWidgetController, AtomicReference<BluetoothPan> atomicReference) {
        this.mContext = context;
        this.mSwitchWidgetController = switchWidgetController;
        DataSaverBackend dataSaverBackend = new DataSaverBackend(context);
        this.mDataSaverBackend = dataSaverBackend;
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mTetheringManager = (TetheringManager) context.getSystemService("tethering");
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothPan = atomicReference;
        this.mEthernetRegex = context.getString(17039958);
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
        this.mListeners = new ArrayList();
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleBluetoothStateChanged(int i) {
        if (i != Integer.MIN_VALUE && i != 10) {
            if (i != 12) {
                return false;
            }
            if (this.mBluetoothEnableForTether) {
                startTethering(2);
            }
        }
        this.mBluetoothEnableForTether = false;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleWifiApStateChanged(int i) {
        if (i == 11 || i == 13) {
            return true;
        }
        if (i != 14) {
            return false;
        }
        Log.e("TetherEnabler", "Wifi AP is failed!");
        return true;
    }

    public static boolean isTethering(int i, int i2) {
        return (i & (1 << i2)) != 0;
    }

    private void setSwitchCheckedInternal(boolean z) {
        try {
            this.mSwitchWidgetController.stopListening();
            this.mSwitchWidgetController.setChecked(z);
            this.mSwitchWidgetController.startListening();
        } catch (IllegalStateException unused) {
            Log.e("TetherEnabler", "failed to stop switch widget listener when set check internally");
        }
    }

    private void setSwitchEnabled(boolean z) {
        this.mSwitchWidgetController.setEnabled(z && !this.mDataSaverEnabled && this.mUserManager.isAdminUser());
    }

    public void addListener(OnTetherStateUpdateListener onTetherStateUpdateListener) {
        if (onTetherStateUpdateListener == null || this.mListeners.contains(onTetherStateUpdateListener)) {
            return;
        }
        onTetherStateUpdateListener.onTetherStateUpdated(getTetheringState(null));
        this.mListeners.add(onTetherStateUpdateListener);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @VisibleForTesting
    int getTetheringState(String[] strArr) {
        if (strArr == null) {
            strArr = this.mTetheringManager.getTetheredIfaces();
        }
        boolean isWifiApEnabled = this.mWifiManager.isWifiApEnabled();
        boolean z = isWifiApEnabled;
        if (!this.mBluetoothTetheringStoppedByUser) {
            BluetoothPan bluetoothPan = this.mBluetoothPan.get();
            BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
            z = isWifiApEnabled;
            if (bluetoothAdapter != null) {
                z = isWifiApEnabled;
                z = isWifiApEnabled;
                if (bluetoothAdapter.getState() == 12 && bluetoothPan != null) {
                    z = isWifiApEnabled;
                    if (bluetoothPan.isTetheringOn()) {
                        z = isWifiApEnabled | true;
                    }
                }
            }
        }
        String[] tetherableUsbRegexs = this.mTetheringManager.getTetherableUsbRegexs();
        int length = strArr.length;
        int i = 0;
        int i2 = z;
        while (i < length) {
            String str = strArr[i];
            int length2 = tetherableUsbRegexs.length;
            int i3 = 0;
            boolean z2 = i2;
            while (i3 < length2) {
                if (str.matches(tetherableUsbRegexs[i3])) {
                    z2 = (z2 ? 1 : 0) | true;
                }
                i3++;
                z2 = z2;
            }
            if (str.matches(this.mEthernetRegex)) {
                z2 = (z2 ? 1 : 0) | true;
            }
            i++;
            i2 = z2;
        }
        return i2;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
        setSwitchEnabled(true);
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mDataSaverBackend.addListener(this);
        this.mSwitchWidgetController.setListener(this);
        this.mSwitchWidgetController.startListening();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(this.mTetherChangeReceiver, intentFilter);
        this.mTetheringEventCallback = new TetheringManager.TetheringEventCallback() { // from class: com.android.settings.network.TetherEnabler.1
            public void onTetheredInterfacesChanged(List<String> list) {
                TetherEnabler.this.updateState((String[]) list.toArray(new String[list.size()]));
            }
        };
        this.mTetheringManager.registerTetheringEventCallback(new HandlerExecutor(this.mMainThreadHandler), this.mTetheringEventCallback);
        this.mOnStartTetheringCallback = new OnStartTetheringCallback(this);
        updateState(null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mBluetoothTetheringStoppedByUser = false;
        this.mDataSaverBackend.remListener(this);
        this.mSwitchWidgetController.stopListening();
        this.mContext.unregisterReceiver(this.mTetherChangeReceiver);
        this.mTetheringManager.unregisterTetheringEventCallback(this.mTetheringEventCallback);
        this.mTetheringEventCallback = null;
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        if (z) {
            startTethering(0);
        } else {
            stopTethering(1);
            stopTethering(0);
            stopTethering(2);
            stopTethering(5);
        }
        return true;
    }

    public void removeListener(OnTetherStateUpdateListener onTetherStateUpdateListener) {
        if (onTetherStateUpdateListener != null) {
            this.mListeners.remove(onTetherStateUpdateListener);
        }
    }

    public void startTethering(int i) {
        BluetoothAdapter bluetoothAdapter;
        if (i == 2) {
            this.mBluetoothTetheringStoppedByUser = false;
        }
        if (isTethering(getTetheringState(null), i)) {
            return;
        }
        if (i != 2 || (bluetoothAdapter = this.mBluetoothAdapter) == null || bluetoothAdapter.getState() != 10) {
            setSwitchEnabled(false);
            this.mConnectivityManager.startTethering(i, true, this.mOnStartTetheringCallback, this.mMainThreadHandler);
            return;
        }
        if (DEBUG) {
            Log.d("TetherEnabler", "Turn on bluetooth first.");
        }
        this.mBluetoothEnableForTether = true;
        this.mBluetoothAdapter.enable();
    }

    public void stopTethering(int i) {
        if (isTethering(getTetheringState(null), i)) {
            setSwitchEnabled(false);
            this.mConnectivityManager.stopTethering(i);
            if (i == 2) {
                this.mBluetoothTetheringStoppedByUser = true;
                updateState(null);
            }
        }
    }

    @VisibleForTesting
    void updateState(String[] strArr) {
        int tetheringState = getTetheringState(strArr);
        if (DEBUG) {
            Log.d("TetherEnabler", "updateState: " + tetheringState);
        }
        setSwitchCheckedInternal(tetheringState != 0);
        setSwitchEnabled(true);
        int size = this.mListeners.size();
        for (int i = 0; i < size; i++) {
            this.mListeners.get(i).onTetherStateUpdated(tetheringState);
        }
    }
}
