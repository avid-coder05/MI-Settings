package com.android.settings.wifi.linkturbo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.msim.MSimUtils;
import java.util.Calendar;
import java.util.Date;
import miui.telephony.TelephonyManager;
import miuix.util.Log;

/* loaded from: classes2.dex */
public class LinkTurboUtils {
    public static int getLinkTurboOptions(Context context) {
        if (context != null) {
            return Settings.System.getInt(context.getContentResolver(), "link_turbo_option", 0);
        }
        return 0;
    }

    public static Date getTimesMonthmorning() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(1), calendar.get(2), calendar.get(5), 0, 0, 0);
        calendar.set(5, calendar.getActualMinimum(5));
        Log.d("LinkTurboUtils", "getTimesMonthmorning()" + calendar.getTime().toLocaleString());
        return calendar.getTime();
    }

    public static Date getTimesmorning() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(13, 0);
        calendar.set(12, 0);
        calendar.set(14, 0);
        Log.d("LinkTurboUtils", "getTimesmorning()" + calendar.getTime().toLocaleString());
        return calendar.getTime();
    }

    public static int getUid(Context context, int i, String str) {
        try {
            return context.getPackageManager().getPackageUidAsUser(str, i);
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        }
    }

    public static boolean hasInternetAccess(Context context, String str) {
        return context.getPackageManager().checkPermission("android.permission.INTERNET", str) == 0;
    }

    private static boolean isAirPlaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    public static boolean isTabletDeviceWithVolteCapability() {
        return "clover".equals(Build.DEVICE);
    }

    public static void setLinkTurboOptions(Context context, int i) {
        if (context != null) {
            Settings.System.putInt(context.getContentResolver(), "link_turbo_option", i);
        }
    }

    public static boolean shouldHideSmartDualSimButton(Context context) {
        if (context == null || !context.getResources().getBoolean(R.bool.config_show_smart_sim)) {
            return true;
        }
        boolean isAirPlaneModeOn = isAirPlaneModeOn(context);
        TelephonyManager.getDefault();
        return isAirPlaneModeOn || (TelephonyManager.isCustSingleSimDevice() || isTabletDeviceWithVolteCapability() || miui.os.Build.IS_INTERNATIONAL_BUILD) || (!new MSimUtils().hasDualSim(context) || !MSimUtils.isViceSlotActivated()) || (MSimUtils.isSmartDualSimSwitchSupported() ^ true);
    }
}
