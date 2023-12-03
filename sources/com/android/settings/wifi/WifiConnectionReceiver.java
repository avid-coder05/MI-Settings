package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import com.android.settings.MiuiUtils;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;

/* loaded from: classes2.dex */
public class WifiConnectionReceiver extends BroadcastReceiver {
    private static boolean mIsRemind = true;
    private static boolean mMobileConnected;
    private static boolean mWifiConnected;

    private WifiConfiguration getBestConfiguration(Context context, boolean z) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        MiuiUtils miuiUtils = MiuiUtils.getInstance();
        WifiConfiguration wifiConfiguration = null;
        if (configuredNetworks != null && scanResults != null) {
            int i = Integer.MIN_VALUE;
            for (WifiConfiguration wifiConfiguration2 : configuredNetworks) {
                if (!z || wifiConfiguration == null || wifiConfiguration.status == 1 || wifiConfiguration2.status != 1) {
                    Iterator<ScanResult> it = scanResults.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            ScanResult next = it.next();
                            if (miuiUtils.isTheSameWifi(context, wifiConfiguration2, next)) {
                                if (!z || wifiConfiguration == null || wifiConfiguration.status != 1 || wifiConfiguration2.status == 1) {
                                    int i2 = next.level;
                                    if (i2 > i) {
                                        wifiConfiguration = wifiConfiguration2;
                                        i = i2;
                                    }
                                } else {
                                    i = next.level;
                                    wifiConfiguration = wifiConfiguration2;
                                }
                                scanResults.remove(next);
                            }
                        }
                    }
                }
            }
        }
        return wifiConfiguration;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (Build.IS_CM_CUSTOMIZATION) {
            String action = intent.getAction();
            if ("miui.intent.action.CONNECTIVITY_CHANGED".equals(action)) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                if (networkInfo != null) {
                    if (networkInfo.getType() != 1) {
                        if (networkInfo.getType() == 0) {
                            boolean z = mMobileConnected;
                            boolean isConnected = networkInfo.isConnected();
                            mMobileConnected = isConnected;
                            if (z || !isConnected) {
                                return;
                            }
                            mIsRemind = true;
                            return;
                        }
                        return;
                    }
                    boolean z2 = mWifiConnected;
                    boolean isConnected2 = networkInfo.isConnected();
                    mWifiConnected = isConnected2;
                    if (!z2 || isConnected2) {
                        return;
                    }
                    mIsRemind = true;
                    if (WifiConnectionDialog.isRemind(context)) {
                        TelephonyManager.from(context).setDataEnabled(false);
                        if (((WifiManager) context.getSystemService("wifi")).isWifiEnabled()) {
                            return;
                        }
                        mIsRemind = false;
                        Intent intent2 = new Intent("miui.intent.action.SELECT_WIFI_AP");
                        intent2.putExtra("extra_best_ap", (Parcelable) null);
                        intent2.addFlags(268468224);
                        context.startActivity(intent2);
                    }
                }
            } else if (!"miui.intent.action.SWITCH_TO_WIFI".equals(action) || !WifiConnectionDialog.isRemind(context)) {
                if ("miui.intent.action.SELECT_WIFI_AP".equals(action) && mIsRemind && WifiConnectionDialog.isRemind(context)) {
                    mIsRemind = false;
                    android.net.wifi.WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
                    if (connectionInfo == null || connectionInfo.getNetworkId() == -1) {
                        Intent intent3 = new Intent("miui.intent.action.SELECT_WIFI_AP");
                        intent3.putExtra("extra_best_ap", getBestConfiguration(context, true));
                        intent3.addFlags(268468224);
                        context.startActivity(intent3);
                    }
                }
            } else if (mIsRemind || (WifiConnectionDialog.isWifiAutoConnectAsk(context) && WifiConnectionDialog.isRemindExpired(context))) {
                android.net.wifi.WifiInfo connectionInfo2 = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
                if (connectionInfo2 != null && connectionInfo2.getNetworkId() != -1) {
                    mIsRemind = false;
                    return;
                }
                WifiConfiguration bestConfiguration = getBestConfiguration(context, false);
                if (bestConfiguration != null) {
                    mIsRemind = false;
                    Intent intent4 = new Intent("miui.intent.action.SWITCH_TO_WIFI");
                    intent4.putExtra("extra_best_ap", bestConfiguration);
                    intent4.addFlags(268468224);
                    context.startActivity(intent4);
                }
            }
        }
    }
}
