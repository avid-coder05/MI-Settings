package com.android.settingslib.wifi;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.android.wifitrackerlib.WifiEntry;

/* loaded from: classes2.dex */
public class Wifi6ApiCompatible {
    public static boolean isHe8ssCapableAp(WifiInfo wifiInfo) {
        try {
            return ((Boolean) Class.forName("android.net.wifi.WifiInfo").getDeclaredMethod("isHe8ssCapableAp", null).invoke(wifiInfo, null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isHe8ssCapableAp(WifiEntry wifiEntry) {
        try {
            return ((Boolean) Class.forName("com.android.wifitrackerlib.WifiEntry").getDeclaredMethod("isHe8ssCapableAp", null).invoke(wifiEntry, null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isVht8ssCapableDevice(WifiManager wifiManager) {
        try {
            return ((Boolean) Class.forName("android.net.wifi.WifiManager").getDeclaredMethod("isVht8ssCapableDevice", null).invoke(wifiManager, null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isVhtMax8SpatialStreamsSupported(WifiInfo wifiInfo) {
        try {
            return ((Boolean) Class.forName("android.net.wifi.WifiInfo").getDeclaredMethod("isVhtMax8SpatialStreamsSupported", null).invoke(wifiInfo, null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isVhtMax8SpatialStreamsSupported(WifiEntry wifiEntry) {
        try {
            return ((Boolean) Class.forName("com.android.wifitrackerlib.WifiEntry").getDeclaredMethod("isVhtMax8SpatialStreamsSupported", null).invoke(wifiEntry, null)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }
}
