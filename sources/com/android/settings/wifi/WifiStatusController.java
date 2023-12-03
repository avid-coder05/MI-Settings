package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.R;
import com.android.settings.utils.TabletUtils;
import com.android.settings.wifi.WifiStatusController;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.AccessPoint;

/* loaded from: classes2.dex */
public class WifiStatusController extends BaseSettingsController {
    private Context mContext;
    private Handler mHandler;
    private boolean mHasRegister;
    private IntentFilter mIntentFilter;
    private volatile boolean mNetworkConnected;
    private BroadcastReceiver mReceiver;
    private String mStatusViewText;
    private boolean mVerbose;
    private volatile boolean mWifiEnabled;
    private WifiManager mWifiManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.wifi.WifiStatusController$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass1 extends Handler {
        AnonymousClass1(Looper looper) {
            super(looper);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleMessage$0() {
            String wifiState = WifiStatusController.this.getWifiState();
            if (WifiStatusController.this.mVerbose) {
                Log.d("WifiStatusController", "getWifiState complete.");
            }
            if (TextUtils.isEmpty(wifiState) || TextUtils.equals(WifiStatusController.this.mStatusViewText, wifiState)) {
                return;
            }
            WifiStatusController.this.mStatusViewText = wifiState;
            sendEmptyMessage(258);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i == 257) {
                if (WifiStatusController.this.mVerbose) {
                    Log.d("WifiStatusController", "do refresh state background.");
                }
                ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.wifi.WifiStatusController$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        WifiStatusController.AnonymousClass1.this.lambda$handleMessage$0();
                    }
                });
            } else if (i != 258) {
            } else {
                if (WifiStatusController.this.mVerbose) {
                    Log.d("WifiStatusController", "do refresh view.");
                }
                if (((BaseSettingsController) WifiStatusController.this).mStatusView != null) {
                    ((BaseSettingsController) WifiStatusController.this).mStatusView.setText(WifiStatusController.this.mStatusViewText);
                }
            }
        }
    }

    public WifiStatusController(Context context, TextView textView) {
        super(context, textView);
        this.mStatusViewText = "";
        this.mHandler = new AnonymousClass1(Looper.getMainLooper());
        this.mWifiEnabled = false;
        this.mNetworkConnected = false;
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.WifiStatusController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                    int intExtra = intent.getIntExtra("wifi_state", 4);
                    if ((intExtra == 3 && !WifiStatusController.this.mWifiEnabled) || (intExtra != 3 && WifiStatusController.this.mWifiEnabled)) {
                        WifiStatusController.this.updateStatus();
                    }
                    WifiStatusController.this.mWifiEnabled = intExtra == 3;
                } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    if (networkInfo == null) {
                        WifiStatusController.this.mNetworkConnected = false;
                        WifiStatusController.this.updateStatus();
                    } else if (networkInfo.isConnected() != WifiStatusController.this.mNetworkConnected) {
                        WifiStatusController.this.mNetworkConnected = networkInfo.isConnected();
                        WifiStatusController.this.updateStatus();
                    }
                }
            }
        };
        this.mContext = context;
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        this.mWifiManager = wifiManager;
        this.mVerbose = wifiManager.isVerboseLoggingEnabled();
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getWifiState() {
        if (this.mStatusView == null) {
            return "";
        }
        if (!this.mWifiManager.isWifiEnabled()) {
            if (this.mVerbose) {
                Log.d("WifiStatusController", "WiFi is off.");
            }
            return this.mContext.getString(R.string.wireless_off);
        } else if (!this.mNetworkConnected) {
            if (this.mVerbose) {
                Log.d("WifiStatusController", "WiFi is not connected.");
            }
            return this.mContext.getString(R.string.wireless_on);
        } else {
            android.net.wifi.WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
            if (connectionInfo == null || TextUtils.isEmpty(connectionInfo.getSSID()) || connectionInfo.getIpAddress() == 0) {
                return this.mContext.getString(R.string.wireless_on);
            }
            if (TabletUtils.IS_TABLET) {
                return this.mContext.getString(R.string.wireless_connected);
            }
            return connectionInfo.isPasspointAp() ? connectionInfo.getPasspointProviderFriendlyName() : AccessPoint.removeDoubleQuotes(connectionInfo.getSSID());
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void start() {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        this.mHasRegister = true;
        updateStatus();
    }

    @Override // com.android.settings.BaseSettingsController
    public void stop() {
        if (this.mHasRegister) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mHasRegister = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
        TextView textView = this.mStatusView;
        if (textView != null) {
            textView.setText(this.mStatusViewText);
        }
        if (this.mHandler.hasMessages(257)) {
            return;
        }
        this.mHandler.sendEmptyMessageDelayed(257, 100L);
    }
}
