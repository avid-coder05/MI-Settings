package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.wifi.SlaveWifiUtils;

/* loaded from: classes2.dex */
public class MiuiSlaveWifiEnabler {
    private Context mContext;
    private SettingsPreferenceFragment mFragment;
    private final IntentFilter mIntentFilter;
    private CheckBoxPreference mPreference;
    private SlaveWifiUtils mSlaveWifiUtils;
    private boolean mStateMachineEvent;
    private final WifiManager mWifiManager;
    private boolean mIsNeededEnabled = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.MiuiSlaveWifiEnabler.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("MiuiSlaveWifiEnabler", "onReceive : " + action);
            if ("android.net.wifi.WIFI_SLAVE_STATE_CHANGED".equals(action)) {
                MiuiSlaveWifiEnabler.this.handleWifiStateChanged(intent.getIntExtra("wifi_state", 18));
            }
        }
    };
    private WifiManager.SoftApCallback mSoftApCallback = new WifiManager.SoftApCallback() { // from class: com.android.settings.wifi.MiuiSlaveWifiEnabler.2
        public void onStateChanged(int i, int i2) {
            if (i == 11 && MiuiSlaveWifiEnabler.this.mIsNeededEnabled) {
                MiuiSlaveWifiEnabler.this.mSlaveWifiUtils.setWifiSlaveEnabled(true);
                MiuiSlaveWifiEnabler.this.mIsNeededEnabled = false;
            }
        }
    };
    private final Handler mHandler = new Handler();

    public MiuiSlaveWifiEnabler(SettingsPreferenceFragment settingsPreferenceFragment, CheckBoxPreference checkBoxPreference, SlaveWifiUtils slaveWifiUtils) {
        this.mFragment = settingsPreferenceFragment;
        this.mContext = settingsPreferenceFragment.getActivity();
        this.mSlaveWifiUtils = slaveWifiUtils;
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_SLAVE_STATE_CHANGED");
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("miui.intent.action.wifi.WIFI_SLAVE_CONNECTION_FAILURE");
        setPreference(checkBoxPreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWifiStateChanged(int i) {
        if (i == 14) {
            this.mPreference.setEnabled(false);
        } else if (i == 16) {
            this.mPreference.setEnabled(false);
        } else if (i != 17) {
            setChecked(false);
            this.mPreference.setEnabled(true);
        } else {
            setChecked(true);
            this.mPreference.setEnabled(true);
        }
        this.mFragment.invalidateOptionsMenu();
    }

    private boolean mayDisableTethering(boolean z) {
        int wifiApState = this.mWifiManager.getWifiApState();
        return z && (wifiApState == 12 || wifiApState == 13);
    }

    private void setChecked(boolean z) {
        if (z != this.mPreference.isChecked()) {
            this.mStateMachineEvent = true;
            this.mPreference.setChecked(z);
            this.mStateMachineEvent = false;
        }
    }

    public void checkedChanged(boolean z) {
        if (this.mStateMachineEvent) {
            return;
        }
        if (z && !WirelessUtils.isRadioAllowed(this.mContext, "wifi")) {
            Toast.makeText(this.mContext, R.string.wifi_in_airplane_mode, 0).show();
            this.mPreference.setChecked(false);
            return;
        }
        this.mIsNeededEnabled = false;
        if (mayDisableTethering(z)) {
            ((ConnectivityManager) this.mContext.getSystemService("connectivity")).stopTethering(0);
            this.mIsNeededEnabled = true;
        } else if (this.mSlaveWifiUtils.setWifiSlaveEnabled(z)) {
        } else {
            this.mPreference.setEnabled(true);
            Toast.makeText(this.mContext, R.string.wifi_error, 0).show();
        }
    }

    public void setPreference(CheckBoxPreference checkBoxPreference) {
        this.mPreference = checkBoxPreference;
        int slaveWifiState = this.mSlaveWifiUtils.getSlaveWifiState();
        Log.d("MiuiSlaveWifiEnabler", "Slave wifi state : " + slaveWifiState);
        boolean z = true;
        boolean z2 = slaveWifiState == 17;
        boolean z3 = slaveWifiState == 15;
        this.mPreference.setChecked(z2);
        CheckBoxPreference checkBoxPreference2 = this.mPreference;
        if (!z2 && !z3) {
            z = false;
        }
        checkBoxPreference2.setEnabled(z);
    }

    public void start() {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        this.mWifiManager.registerSoftApCallback(new HandlerExecutor(this.mHandler), this.mSoftApCallback);
    }

    public void stop() {
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mWifiManager.unregisterSoftApCallback(this.mSoftApCallback);
    }
}
