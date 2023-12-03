package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import com.android.settings.R;
import com.android.settings.datausage.DataSaverBackend;
import com.miui.enterprise.RestrictionsHelper;

/* loaded from: classes2.dex */
public class WifiApEnabler {
    private final Context mContext;
    private final DataSaverBackend mDataSaverBackend;
    private Handler mHandler;
    private final IntentFilter mIntentFilter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.WifiApEnabler.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                WifiApEnabler.this.enableWifiSwitch();
            }
        }
    };
    private WifiManager.SoftApCallback mSoftApCallback = new WifiManager.SoftApCallback() { // from class: com.android.settings.wifi.WifiApEnabler.2
        public void onStateChanged(int i, int i2) {
            WifiApEnabler.this.handleWifiApStateChanged(i, i2);
        }
    };
    private final CheckBoxPreference mSwitch;
    private final WifiManager mWifiManager;

    public WifiApEnabler(Context context, DataSaverBackend dataSaverBackend, CheckBoxPreference checkBoxPreference) {
        this.mContext = context;
        this.mDataSaverBackend = dataSaverBackend;
        this.mSwitch = checkBoxPreference;
        checkBoxPreference.setPersistent(false);
        this.mHandler = new Handler();
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mIntentFilter = new IntentFilter("android.intent.action.AIRPLANE_MODE");
        this.mHandler = new Handler();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableWifiSwitch() {
        boolean z = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0;
        boolean hasRestriction = RestrictionsHelper.hasRestriction(this.mContext, "disallow_tether");
        if (z || hasRestriction) {
            this.mSwitch.setEnabled(false);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("set enableWifiApSwitch to ");
        sb.append(!this.mDataSaverBackend.isDataSaverEnabled());
        sb.append(" in enableWifiSwitch()");
        Log.d("WifiApEnabler", sb.toString());
        this.mSwitch.setEnabled(!this.mDataSaverBackend.isDataSaverEnabled());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWifiApStateChanged(int i, int i2) {
        switch (i) {
            case 10:
                this.mSwitch.setChecked(false);
                this.mSwitch.setEnabled(false);
                return;
            case 11:
                this.mSwitch.setChecked(false);
                enableWifiSwitch();
                return;
            case 12:
                this.mSwitch.setEnabled(false);
                return;
            case 13:
                this.mSwitch.setChecked(true);
                this.mSwitch.setEnabled(!this.mDataSaverBackend.isDataSaverEnabled());
                updateConfigSummary(this.mWifiManager.getSoftApConfiguration());
                return;
            default:
                this.mSwitch.setChecked(false);
                if (i2 == 1) {
                    this.mSwitch.setSummary(R.string.wifi_sap_no_channel_error);
                } else {
                    this.mSwitch.setSummary(R.string.wifi_error);
                }
                enableWifiSwitch();
                return;
        }
    }

    public void pause() {
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mWifiManager.unregisterSoftApCallback(this.mSoftApCallback);
    }

    public void resume() {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        this.mWifiManager.registerSoftApCallback(new HandlerExecutor(this.mHandler), this.mSoftApCallback);
        enableWifiSwitch();
    }

    public void updateConfigSummary(SoftApConfiguration softApConfiguration) {
        if (softApConfiguration == null) {
            this.mSwitch.setSummary("AndroidAP");
        } else {
            this.mSwitch.setSummary(String.format(softApConfiguration.getSecurityType() == 0 ? this.mContext.getString(R.string.wifi_tether_enabled_nosecurity_summary) : this.mContext.getString(R.string.wifi_tether_enabled_summary), softApConfiguration.getSsid()));
        }
    }
}
