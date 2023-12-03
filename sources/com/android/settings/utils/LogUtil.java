package com.android.settings.utils;

import android.util.Log;

/* loaded from: classes2.dex */
public class LogUtil {
    public static void logCloudSync(String str, String str2) {
        if (Log.isLoggable("WifiCloudSync", 2)) {
            Log.d(str, str2);
        }
    }

    public static void logCost(String str, String str2, double d, double d2, Object obj) {
        Log.d(str, str2 + " (" + obj + ") takes " + (d2 - d) + "ms");
    }
}
