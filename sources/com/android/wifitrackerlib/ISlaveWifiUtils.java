package com.android.wifitrackerlib;

import android.net.Network;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;

/* loaded from: classes2.dex */
public interface ISlaveWifiUtils {
    void connectToSlaveAp(int i);

    void connectToSlaveAp(WifiConfiguration wifiConfiguration);

    Network getSlaveWifiCurrentNetwork();

    WifiInfo getWifiSlaveConnectionInfo();

    boolean is24GHz(ScanResult scanResult);

    boolean is5GHz(ScanResult scanResult);

    boolean isSlaveWifiEnabled();
}
