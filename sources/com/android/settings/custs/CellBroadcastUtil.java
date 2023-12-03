package com.android.settings.custs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

/* loaded from: classes.dex */
public final class CellBroadcastUtil {
    private static boolean deviceIsProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    public static boolean isPkgInstalled(PackageManager packageManager, String str) {
        try {
            packageManager.getPackageInfo(str, 1);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("CellBroadcastUtil", str + " not found");
            return false;
        }
    }

    public static boolean nccBroadcastEnabled(PackageManager packageManager) {
        try {
            return packageManager.getApplicationEnabledSetting("com.mediatek.cellbroadcastreceiver") == 1;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    private static void setApplicationSetting(String str, int i, PackageManager packageManager) {
        try {
            packageManager.setApplicationEnabledSetting(str, i, 0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void setCellbroadcastEnabledSetting(Context context) {
        PackageManager packageManager;
        if (deviceIsProvisioned(context) && (packageManager = context.getPackageManager()) != null && isPkgInstalled(packageManager, "com.android.cellbroadcastreceiver")) {
            setApplicationSetting("com.android.cellbroadcastreceiver", 1, packageManager);
        }
    }
}
