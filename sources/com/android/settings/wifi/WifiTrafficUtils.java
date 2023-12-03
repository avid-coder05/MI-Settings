package com.android.settings.wifi;

import android.content.Context;
import android.provider.Settings;

/* loaded from: classes2.dex */
public class WifiTrafficUtils {
    public static int getLastSelectedTrafficPriority(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "user_network_priority_mode", 1);
    }

    public static int getTrafficPriority(Context context) {
        String string = Settings.System.getString(context.getContentResolver(), "cloud_network_priority_enabled");
        return Settings.System.getInt(context.getContentResolver(), "user_network_priority_enabled", "on".equals(string) ? 1 : "fast".equals(string) ? 2 : 0);
    }

    public static boolean isTrafficPrioritySupport() {
        return MiuiWifiAssistFeatureSupport.isTrafficPriorityAvailable();
    }

    public static void setLastSelectedTrafficPriority(Context context, int i) {
        if (i != 0) {
            Settings.System.putInt(context.getContentResolver(), "user_network_priority_mode", i);
        }
    }

    public static void setTrafficPriority(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "user_network_priority_enabled", i);
    }
}
