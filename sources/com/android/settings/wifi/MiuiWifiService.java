package com.android.settings.wifi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.net.wifi.MiuiWifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.android.settings.utils.LogUtil;
import com.android.settings.wifi.passpoint.MiuiPasspointR1Utils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import miui.provider.Wifi;

/* loaded from: classes2.dex */
public class MiuiWifiService extends Service {
    public static Uri DEFAULT_VALIDATE_URI = Uri.parse("http://clients3.google.com/generate_204");
    private final IBinder mBinder = new LocalBinder();
    private MiuiWifiManager mMiuiWifiManager;
    private WifiConfigurationManager mWifiConfigurationManager;
    private WifiManager mWifiManager;
    private Handler mWorkHandler;
    private HandlerThread mWorkThread;

    /* loaded from: classes2.dex */
    public class LocalBinder extends Binder {
        public LocalBinder() {
        }
    }

    /* loaded from: classes2.dex */
    private class WorkThread extends Handler {
        public WorkThread(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                MiuiWifiService.this.restoreWifiConfigurations((List) message.obj);
                return;
            }
            if (i == 1) {
                WifiConfiguration wifiConfiguration = (WifiConfiguration) message.obj;
                if (wifiConfiguration != null) {
                    AutoConnectUtils autoConnectUtils = AutoConnectUtils.getInstance(MiuiWifiService.this);
                    String str = wifiConfiguration.SSID;
                    if (str != null && !autoConnectUtils.isAutoConnect(str)) {
                        autoConnectUtils.enableAutoConnect(MiuiWifiService.this, wifiConfiguration.SSID, true);
                    }
                    String str2 = wifiConfiguration.SSID;
                    if (str2 != null) {
                        autoConnectUtils.removeNoSecretWifi(MiuiWifiService.this, str2);
                    }
                    MiuiWifiService.this.mWifiConfigurationManager.deleteWifiConfiguration((WifiConfiguration) message.obj);
                }
            } else if (i == 2) {
                MiuiWifiService.this.handleWifiAutoConnect((String) message.obj, message.arg1 == 1);
            } else if (i != 5) {
            } else {
                Context applicationContext = MiuiWifiService.this.getApplicationContext();
                applicationContext.getContentResolver().delete(Wifi.SyncState.CONTENT_URI, null, null);
                applicationContext.getContentResolver().delete(Wifi.CONTENT_URI, null, null);
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
                if (defaultSharedPreferences.getBoolean("DELETED_SYNCED_DATA", false)) {
                    return;
                }
                SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putBoolean("DELETED_SYNCED_DATA", false);
                edit.commit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWifiAutoConnect(String str, boolean z) {
        AutoConnectUtils autoConnectUtils = AutoConnectUtils.getInstance(this);
        if (autoConnectUtils.isAutoConnect(str) != z) {
            autoConnectUtils.enableAutoConnect(this, str, z);
        }
    }

    public static boolean isVaildConfig(WifiConfiguration wifiConfiguration) {
        BitSet bitSet;
        String str = wifiConfiguration.SSID;
        if (str == null || str.length() == 0 || (bitSet = wifiConfiguration.allowedKeyManagement) == null || bitSet.isEmpty()) {
            return false;
        }
        if (wifiConfiguration.allowedKeyManagement.cardinality() > 1) {
            if (wifiConfiguration.allowedKeyManagement.cardinality() > 4) {
                return false;
            }
            if (!wifiConfiguration.allowedKeyManagement.get(2) && !wifiConfiguration.allowedKeyManagement.get(3)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreWifiConfigurations(List<WifiConfiguration> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        WifiConfiguration wifiConfiguration = list.get(list.size() - 1);
        if (isVaildConfig(wifiConfiguration)) {
            if (wifiConfiguration.allowedKeyManagement.get(8)) {
                wifiConfiguration.requirePmf = true;
            }
            int addNetwork = this.mWifiManager.addNetwork(wifiConfiguration);
            if (addNetwork == -1) {
                Log.d("MiuiWifiService", wifiConfiguration.SSID + " save network failed");
            } else {
                this.mWifiManager.enableNetwork(addNetwork, false);
                LogUtil.logCloudSync("MiuiWifiService", wifiConfiguration.getKey() + " save network successfully!");
            }
        } else {
            Log.d("MiuiWifiService", wifiConfiguration.SSID + " is not a vaild config");
        }
        list.remove(wifiConfiguration);
        if (!list.isEmpty()) {
            this.mWorkHandler.sendMessageDelayed(this.mWorkHandler.obtainMessage(0, list), 5000L);
            LogUtil.logCloudSync("MiuiWifiService", "MSG_RESTORE_CONFIG next...");
        } else if (list.isEmpty()) {
            LogUtil.logCloudSync("MiuiWifiService", "seems MSG_RESTORE_CONFIG end");
            updateUnSavedAccessPoints();
        }
    }

    private void sendRestoreConfigMessage(List<WifiConfiguration> list) {
        if (list != null) {
            Message obtainMessage = this.mWorkHandler.obtainMessage(0, list);
            LogUtil.logCloudSync("MiuiWifiService", "sendRestoreConfigMessage start to restore wifi config");
            this.mWorkHandler.sendMessage(obtainMessage);
        }
    }

    private void sendWifiSyncMessage() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (defaultSharedPreferences.getBoolean("DELETED_SYNCED_DATA", false)) {
            SharedPreferences.Editor edit = defaultSharedPreferences.edit();
            edit.putBoolean("DELETED_SYNCED_DATA", true);
            edit.commit();
        }
        LogUtil.logCloudSync("MiuiWifiService", "sendWifiSyncMessage obtain restore wifi configurations");
        sendRestoreConfigMessage(this.mWifiConfigurationManager.getRestoreWifiConfigurations());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateConnectedWifi  reason: merged with bridge method [inline-methods] */
    public void lambda$onStartCommand$0() {
        android.net.wifi.WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return;
        }
        String bssid = connectionInfo.getBSSID();
        int networkId = connectionInfo.getNetworkId();
        List<WifiConfiguration> configuredNetworks = this.mWifiManager.getConfiguredNetworks();
        if (bssid == null || configuredNetworks == null) {
            return;
        }
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (networkId == wifiConfiguration.networkId) {
                wifiConfiguration.BSSID = bssid;
                this.mWifiConfigurationManager.addOrUpdateWifiConfiguration(wifiConfiguration);
                return;
            }
        }
    }

    private void updateUnSavedAccessPoints() {
        List<String> unSavedAccessPoints = this.mWifiConfigurationManager.getUnSavedAccessPoints();
        LogUtil.logCloudSync("MiuiWifiService", "updateUnSavedAccessPoints observedAps: " + unSavedAccessPoints);
        try {
            this.mMiuiWifiManager.setObservedAccessPionts(unSavedAccessPoints);
        } catch (RuntimeException e) {
            int size = unSavedAccessPoints.size();
            Log.e("MiuiWifiService", e.toString() + " - observedAps.size()=" + size);
            if (size <= 500) {
                Log.e("MiuiWifiService", "Bug - Observed APs are not set to the FWK.");
                return;
            }
            Log.v("MiuiWifiService", "Bug - Max observedAps=500");
            this.mMiuiWifiManager.setObservedAccessPionts(new ArrayList(unSavedAccessPoints.subList(0, 500)));
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mWifiConfigurationManager = WifiConfigurationManager.getInstance(this);
        this.mMiuiWifiManager = (MiuiWifiManager) getApplicationContext().getSystemService("MiuiWifiService");
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        HandlerThread handlerThread = new HandlerThread("WorkHandlerThread");
        this.mWorkThread = handlerThread;
        handlerThread.start();
        this.mWorkHandler = new WorkThread(this.mWorkThread.getLooper());
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mWorkHandler.removeMessages(0);
        this.mWorkHandler.removeMessages(1);
        this.mWorkHandler.removeMessages(5);
        this.mWorkThread.quit();
        super.onDestroy();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            String action = intent.getAction();
            LogUtil.logCloudSync("MiuiWifiService", "Received " + action);
            if ("miui.intent.action.UPDATE_CURRENT_WIFI_CONFIGURATION".equals(action)) {
                ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.wifi.MiuiWifiService$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MiuiWifiService.this.lambda$onStartCommand$0();
                    }
                });
            } else if ("miui.intent.action.WIFI_SYNC".equals(action)) {
                sendWifiSyncMessage();
            } else if ("miui.intent.action.RESTORE_WIFI_CONFIGURATIONS".equals(action)) {
                sendRestoreConfigMessage(intent.getParcelableArrayListExtra("wifiConfiguration"));
            } else if ("android.net.wifi.observed_accesspionts_changed".equals(action)) {
                sendRestoreConfigMessage(WifiConfigurationManager.getInstance(this).getRestoreWifiConfigurations());
            } else if ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(action)) {
                if (intent.getIntExtra("changeReason", 2) == 1) {
                    this.mWorkHandler.sendMessage(this.mWorkHandler.obtainMessage(1, intent.getParcelableExtra("wifiConfiguration")));
                }
            } else if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
                updateUnSavedAccessPoints();
                MiuiPasspointR1Utils.removeAllUnregisteredConfig(getApplicationContext());
            } else if ("miui.intent.action.UPDATE_SSID_AUTO_CONNECT".equals(action)) {
                String stringExtra = intent.getStringExtra("extra.EXTRA_AUTO_CONNECT_SSID");
                boolean booleanExtra = intent.getBooleanExtra("extra.EXTRA_AUTO_CONNECT_ENABLED", true);
                Handler handler = this.mWorkHandler;
                handler.sendMessage(handler.obtainMessage(2, booleanExtra ? 1 : 0, 0, stringExtra));
            } else if ("miui.intent.action.FORCE_SELECT_WIFI".equals(action)) {
                ((ConnectivityManager) getSystemService("connectivity")).setAcceptUnvalidated((Network) intent.getParcelableExtra("miui.intent.extra.NETWORK"), true, false);
            } else if ("miui.intent.action.DELETE_WIFI_SYNCED_DATE".equals(action)) {
                this.mWorkHandler.sendEmptyMessage(5);
            }
        }
        return 2;
    }
}
