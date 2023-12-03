package com.android.settings;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

/* loaded from: classes.dex */
public class MiuiOptionUtils$Wifi {
    public static int touchHotspotState(Context context, int i) {
        Context applicationContext = context.getApplicationContext();
        WifiManager wifiManager = (WifiManager) applicationContext.getSystemService("wifi");
        ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext.getSystemService("connectivity");
        int i2 = wifiManager.getWifiApState() == 13 ? 1 : 0;
        if (i == -1 || i == i2) {
            return i2;
        }
        if (i != 0) {
            connectivityManager.startTethering(0, false, new ConnectivityManager.OnStartTetheringCallback() { // from class: com.android.settings.MiuiOptionUtils$Wifi.1
                public void onTetheringFailed() {
                }

                public void onTetheringStarted() {
                }
            });
        } else {
            connectivityManager.stopTethering(0);
        }
        return i;
    }

    public static int touchWLANState(Context context, int i) {
        int i2;
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        int wifiState = wifiManager.getWifiState();
        if (wifiState == 1) {
            i2 = 0;
        } else if (wifiState != 3) {
            return -1;
        } else {
            i2 = 1;
        }
        if (i == -1 || i == i2) {
            return i2;
        }
        if (i != 0) {
            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.setWifiEnabled(false);
        }
        return i;
    }
}
