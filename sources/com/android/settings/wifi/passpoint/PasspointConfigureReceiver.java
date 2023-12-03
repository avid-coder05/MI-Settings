package com.android.settings.wifi.passpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/* loaded from: classes2.dex */
public class PasspointConfigureReceiver extends BroadcastReceiver {
    private static boolean mIsRegistered;
    private static PasspointConfigureReceiver mPasspointConfigureReceiver;

    private void disablePasspointWifiReceiver(Context context) {
        if (mIsRegistered) {
            context.getApplicationContext().unregisterReceiver(this);
            mIsRegistered = false;
        }
    }

    public static void enablePasspointWifiReceiver(Context context) {
        if (mPasspointConfigureReceiver == null) {
            mPasspointConfigureReceiver = new PasspointConfigureReceiver();
        }
        if (mIsRegistered) {
            return;
        }
        mIsRegistered = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.getApplicationContext().registerReceiver(mPasspointConfigureReceiver, intentFilter);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        WifiInfo connectionInfo;
        String action = intent.getAction();
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
            if (wifiManager.getWifiState() == 1) {
                MiuiPasspointR1Utils.removeAllUnregisteredConfig(context);
                disablePasspointWifiReceiver(context);
            }
        } else if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            if ("android.settings.wifi.PASSPOINT_LOGIN_RESULT".equals(action)) {
                String stringExtra = intent.getStringExtra("friendly_name");
                int intExtra = intent.getIntExtra("result", 1);
                String str = (stringExtra == null || !stringExtra.toLowerCase().contains("exands")) ? "" : "exands.com";
                if (str.length() != 0) {
                    MiuiPasspointR1Utils.saveRegisterState(context, str, intExtra == 0);
                    if (intExtra == 1) {
                        MiuiPasspointR1Utils.removePasspointConfig(context, str);
                    }
                }
            }
        } else {
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            if (networkInfo == null || !NetworkInfo.State.CONNECTED.equals(networkInfo.getState()) || (connectionInfo = wifiManager.getConnectionInfo()) == null || !connectionInfo.isPasspointAp() || connectionInfo.isOsuAp()) {
                return;
            }
            String passpointProviderFriendlyName = connectionInfo.getPasspointProviderFriendlyName();
            String passpointFqdn = connectionInfo.getPasspointFqdn();
            if (MiuiPasspointR1Utils.getRegisterState(context, passpointFqdn)) {
                context.sendBroadcast(new Intent("com.miui.wifi.passpoint.action.PASSPOINT_CONNECTED"));
            } else {
                MiuiPasspointR1Utils.gotoLoginActivity(context, passpointFqdn, passpointProviderFriendlyName);
            }
        }
    }
}
