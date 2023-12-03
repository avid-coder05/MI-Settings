package com.android.settings.wifi;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settings.RegionUtils;
import com.android.settings.wifi.linkturbo.LinkTurboClient;
import com.android.settingslib.Utils;
import miui.os.Build;

/* loaded from: classes2.dex */
public class MiuiWifiAssistFeatureSupport {
    private static final boolean IS_INTERNATIONAL_BUILD = Build.IS_INTERNATIONAL_BUILD;
    private static final boolean IS_STABLE_VERSION = Build.IS_STABLE_VERSION;
    private static final boolean IS_JP_KDDI_VERSION = RegionUtils.IS_JP_KDDI;

    public static int getWifiWakeupStatus(Context context) {
        if (context == null || Utils.isWifiOnly(context)) {
            return 3;
        }
        return (!IS_STABLE_VERSION || IS_JP_KDDI_VERSION) ? 0 : 3;
    }

    public static boolean isLinkTurbAvailable(Context context) {
        return LinkTurboClient.isLinkTurboSupported(context);
    }

    public static boolean isTrafficPriorityAvailable() {
        return SystemProperties.getBoolean("sys.net.support.netprio", false);
    }

    public static boolean isWifiAssistAvailable(Context context) {
        boolean isWifiAssistantAvailable = isWifiAssistantAvailable(context);
        boolean isTrafficPriorityAvailable = isTrafficPriorityAvailable();
        boolean isWifiWakeupAvailable = isWifiWakeupAvailable(context);
        boolean isLinkTurbAvailable = isLinkTurbAvailable(context);
        Log.e("MiuiWifiAssistFeatureSupport", "Judge whether the Wifi Assist Fragment is available { CanWifiAssistant : " + isWifiAssistantAvailable + " , CanTraffice : " + isTrafficPriorityAvailable + " , CanWakeup : " + isWifiWakeupAvailable + " , CanMultiNetwork : " + isLinkTurbAvailable + " }");
        if (isWifiAssistantAvailable || isTrafficPriorityAvailable || isWifiWakeupAvailable || isLinkTurbAvailable) {
            Log.e("MiuiWifiAssistFeatureSupport", "Wifi Assist Fragment is available!");
            return true;
        }
        Log.e("MiuiWifiAssistFeatureSupport", "Wifi Assist Function is null, So the fragment should be hidden.");
        return false;
    }

    public static boolean isWifiAssistantAvailable(Context context) {
        if (context == null || Utils.isWifiOnly(context)) {
            return false;
        }
        return !IS_INTERNATIONAL_BUILD;
    }

    public static boolean isWifiWakeupAvailable(Context context) {
        return getWifiWakeupStatus(context) == 0;
    }
}
