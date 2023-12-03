package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

/* loaded from: classes2.dex */
public final class TelefonicaOperator extends BaseOperator {
    private ConnectivityManager cm;
    private WifiManager wm;

    public TelefonicaOperator(Context context) {
        super(context);
        this.cm = (ConnectivityManager) context.getSystemService("connectivity");
        this.wm = (WifiManager) this.mContext.getSystemService("wifi");
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void stopTethering() {
        ConnectivityManager connectivityManager;
        Log.i("BaseOperator", "ready to stop Tethering!");
        WifiManager wifiManager = this.wm;
        if (wifiManager == null || !wifiManager.isWifiApEnabled() || (connectivityManager = this.cm) == null) {
            return;
        }
        connectivityManager.stopTethering(0);
    }
}
