package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import com.android.settings.MiuiAirplaneModeEnabler;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.WirelessUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.os.Build;

/* loaded from: classes2.dex */
public class MiuiWifiEnabler {
    private Context mContext;
    private SettingsPreferenceFragment mFragment;
    private final IntentFilter mIntentFilter;
    private CheckBoxPreference mPreference;
    private boolean mStateMachineEvent;
    private final WifiManager mWifiManager;
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.MiuiWifiEnabler.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                MiuiWifiEnabler.this.handleWifiStateChanged(intent.getIntExtra("wifi_state", 4));
            } else if ("android.net.wifi.supplicant.STATE_CHANGE".equals(action)) {
                if (MiuiWifiEnabler.this.mConnected.get()) {
                    return;
                }
                MiuiWifiEnabler.this.handleStateChanged(android.net.wifi.WifiInfo.getDetailedStateOf((SupplicantState) intent.getParcelableExtra("newState")));
            } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                MiuiWifiEnabler.this.mConnected.set(networkInfo.isConnected());
                MiuiWifiEnabler.this.handleStateChanged(networkInfo.getDetailedState());
            }
        }
    };

    public MiuiWifiEnabler(SettingsPreferenceFragment settingsPreferenceFragment, CheckBoxPreference checkBoxPreference) {
        this.mFragment = settingsPreferenceFragment;
        FragmentActivity activity = settingsPreferenceFragment.getActivity();
        this.mContext = activity;
        this.mWifiManager = (WifiManager) activity.getSystemService("wifi");
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        setPreference(checkBoxPreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStateChanged(NetworkInfo.DetailedState detailedState) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWifiStateChanged(int i) {
        if (i == 0) {
            this.mPreference.setEnabled(false);
        } else if (i == 1) {
            setChecked(false);
            this.mPreference.setEnabled(true);
        } else if (i == 2) {
            this.mPreference.setEnabled(false);
        } else if (i != 3) {
            setChecked(false);
            this.mPreference.setEnabled(true);
        } else {
            setChecked(true);
            this.mPreference.setEnabled(true);
        }
        this.mFragment.invalidateOptionsMenu();
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
        int wifiApState = this.mWifiManager.getWifiApState();
        if (z && !MiuiUtils.getInstance().getWifiStaSapConcurrency(this.mContext) && (wifiApState == 12 || wifiApState == 13)) {
            ((ConnectivityManager) this.mContext.getSystemService("connectivity")).stopTethering(0);
        }
        if (!this.mWifiManager.setWifiEnabled(z)) {
            this.mPreference.setEnabled(true);
            Toast.makeText(this.mContext, R.string.wifi_error, 0).show();
        } else if (Build.IS_CM_CUSTOMIZATION && MiuiAirplaneModeEnabler.isAirplaneModeOn(this.mContext)) {
            this.mPreference.setChecked(false);
        }
    }

    public void pause() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public void resume(Context context) {
        this.mContext = context;
        context.registerReceiver(this.mReceiver, this.mIntentFilter);
    }

    public void setPreference(CheckBoxPreference checkBoxPreference) {
        this.mPreference = checkBoxPreference;
        int wifiState = this.mWifiManager.getWifiState();
        boolean z = wifiState == 3;
        boolean z2 = wifiState == 1;
        this.mPreference.setChecked(z);
        this.mPreference.setEnabled(z || z2);
    }
}
