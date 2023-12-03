package com.android.settingslib.wifi;

import android.net.wifi.MiuiWifiManager;
import android.util.Log;
import com.android.wifitrackerlib.IWifiEntryUtils;

/* loaded from: classes2.dex */
public class WifiEntryUtils implements IWifiEntryUtils {
    private static final String TAG = "WifiEntryUtils";

    @Override // com.android.wifitrackerlib.IWifiEntryUtils
    public int miuiCalculateSignalLevel(int i, int i2) {
        try {
            return MiuiWifiManager.calculateSignalLevel(i, i2);
        } catch (Exception e) {
            Log.e(TAG, "calculateSignalLevel Exception:" + e);
            return -1;
        }
    }
}
