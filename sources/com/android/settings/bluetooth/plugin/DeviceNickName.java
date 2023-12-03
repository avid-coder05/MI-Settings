package com.android.settings.bluetooth.plugin;

import android.os.SystemProperties;
import android.util.Log;

/* loaded from: classes.dex */
public class DeviceNickName {
    public static boolean getSurpportSplit() {
        try {
            return ((Boolean) Class.forName("com.xiaomi.device_feature." + SystemProperties.get("ro.product.device").replaceAll("[^(0-9a-zA-Z)]", "")).getDeclaredField("ISSUPPORTSPLIT").get(null)).booleanValue();
        } catch (Exception e) {
            Log.w("DeviceNickName", "getSurpportSplit error:" + e);
            return false;
        }
    }
}
