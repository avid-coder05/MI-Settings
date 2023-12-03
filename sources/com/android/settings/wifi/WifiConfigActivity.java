package com.android.settings.wifi;

import android.app.StatusBarManager;
import android.content.Intent;
import android.net.ScoredNetwork;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.android.settings.wifi.WifiDialog2;
import com.android.settingslib.wifi.SlaveWifiUtils;
import com.android.wifitrackerlib.StandardWifiEntry;
import com.android.wifitrackerlib.Utils;
import com.android.wifitrackerlib.WifiEntry;
import java.lang.ref.WeakReference;
import java.util.List;
import miui.app.constants.ThemeManagerConstants;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class WifiConfigActivity extends AppCompatActivity implements WifiDialog2.WifiDialog2Listener {
    private DetachWifiDialogListener dismissListener;
    private WifiDialog2 mDialog;
    private Boolean mIsSlave;
    private Handler mMainHandler;
    private WeakReference<WifiConfigActivity> mWifiConfigActivity;
    private WifiConfiguration mWifiConfiguration;
    private WifiManager mWifiManager;
    private WifiNetworkScoreCache mWifiNetworkScoreCache;
    private Handler mWorkHandler;
    private HandlerThread mWorkerThread;
    private WifiEntry wifiEntry;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiConfigActivity = new WeakReference<>(this);
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        ((StatusBarManager) getSystemService(ThemeManagerConstants.COMPONENT_CODE_STATUSBAR)).collapsePanels();
        this.mWifiConfiguration = (WifiConfiguration) getIntent().getParcelableExtra("wifi_config");
        this.mIsSlave = Boolean.valueOf(getIntent().getBooleanExtra("isSlave", false));
        this.mMainHandler = new Handler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread("WifiConfigActivity{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWorkHandler = new Handler(this.mWorkerThread.getLooper());
        WifiNetworkScoreCache wifiNetworkScoreCache = new WifiNetworkScoreCache(this, new WifiNetworkScoreCache.CacheListener(this.mWorkHandler) { // from class: com.android.settings.wifi.WifiConfigActivity.1
            public void networkCacheUpdated(List<ScoredNetwork> list) {
            }
        });
        this.mWifiNetworkScoreCache = wifiNetworkScoreCache;
        this.wifiEntry = new StandardWifiEntry(this, this.mMainHandler, wifiNetworkScoreCache, this.mWifiManager, false, this.mWifiConfiguration.getPrintableSsid(), Utils.getSecurityTypesFromWifiConfiguration(this.mWifiConfiguration));
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onForget(WifiDialog2 wifiDialog2) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("wifi_config");
        this.mIsSlave = Boolean.valueOf(intent.getBooleanExtra("isSlave", false));
        this.wifiEntry = new StandardWifiEntry(this, this.mMainHandler, this.mWifiNetworkScoreCache, this.mWifiManager, false, wifiConfiguration.getPrintableSsid(), Utils.getSecurityTypesFromWifiConfiguration(wifiConfiguration));
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.dismissListener = new DetachWifiDialogListener(this.mWifiConfigActivity.get());
        WifiDialog2 wifiDialog2 = new WifiDialog2(this.mWifiConfigActivity.get(), this.mWifiConfigActivity.get(), this.wifiEntry, 3, 0, false);
        this.mDialog = wifiDialog2;
        wifiDialog2.setOnDismissListener(this.dismissListener);
        this.mDialog.show();
        this.dismissListener.clearOnDetach(this.mDialog);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        setVisible(true);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        WifiDialog2 wifiDialog2 = this.mDialog;
        if (wifiDialog2 != null) {
            wifiDialog2.dismiss();
        }
        this.dismissListener = null;
        this.mWorkerThread.quit();
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        WifiConfiguration config = this.mDialog.getController().getConfig();
        config.hiddenSSID = this.mWifiConfiguration.hiddenSSID;
        if (this.mIsSlave.booleanValue()) {
            SlaveWifiUtils.getInstance(getApplicationContext()).connectToSlaveAp(config);
        } else {
            this.mWifiManager.connect(config, null);
        }
    }
}
