package com.android.wifitrackerlib;

import android.net.wifi.WifiManager;
import com.android.settingslib.wifi.WifiEntryUtils;

/* loaded from: classes2.dex */
public class WifiEntryUtilsStub {
    private static volatile IWifiEntryUtils mUtils;

    static {
        try {
            mUtils = (IWifiEntryUtils) WifiEntryUtils.class.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InstantiationException e3) {
            e3.printStackTrace();
        }
    }

    public static int miuiCalculateSignalLevel(int i, WifiManager wifiManager) {
        return mUtils != null ? mUtils.miuiCalculateSignalLevel(i, 5) : wifiManager.calculateSignalLevel(i);
    }
}
