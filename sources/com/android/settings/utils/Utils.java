package com.android.settings.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.settings.R;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import miui.os.Build;
import miui.provider.ExtraContacts;
import miui.util.CustomizeUtil;

/* loaded from: classes2.dex */
public class Utils {
    public static final boolean IS_MIUI_LITE_VERSION = Build.IS_MIUI_LITE_VERSION;
    public static final boolean IS_NOT_SUPPORT_GESTURE_V3_DEVICE;
    public static Set<String> LOW_MEMORY_DEVICES;

    static {
        ArraySet arraySet = new ArraySet();
        LOW_MEMORY_DEVICES = arraySet;
        arraySet.add("pine");
        LOW_MEMORY_DEVICES.add("olive");
        LOW_MEMORY_DEVICES.add("olivelite");
        LOW_MEMORY_DEVICES.add("olivewood");
        IS_NOT_SUPPORT_GESTURE_V3_DEVICE = isNotSupportGestureV3Device();
    }

    public static String convertSetToString(HashSet<String> hashSet) {
        if (hashSet == null || hashSet.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = hashSet.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (!TextUtils.isEmpty(next)) {
                sb.append(next + ",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static HashSet<String> convertStringToSet(String str) {
        if (TextUtils.isEmpty(str)) {
            return new HashSet<>();
        }
        String[] split = str.split(",");
        HashSet<String> hashSet = new HashSet<>();
        for (String str2 : split) {
            if (!hashSet.contains(str2)) {
                hashSet.add(str2);
            }
        }
        return hashSet;
    }

    public static boolean isDrip(Resources resources) {
        return resources.getBoolean(R.bool.config_device_is_drip_type);
    }

    public static boolean isFold() {
        return SystemProperties.getInt("persist.sys.muiltdisplay_type", 0) == 2;
    }

    public static boolean isHole(Resources resources) {
        return resources.getBoolean(R.bool.config_device_is_hole_type);
    }

    public static boolean isNarrowNotch(Resources resources) {
        return resources.getBoolean(R.bool.config_device_is_narrow_notch_type);
    }

    private static final boolean isNotSupportGestureV3Device() {
        try {
            return ((Boolean) Class.forName("android.util.MiuiGestureUtils").getMethod("isNotSupportGestureV3Device", new Class[0]).invoke(null, new Object[0])).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isPad() {
        return "tablet".equals(SystemProperties.get("ro.build.characteristics", ExtraContacts.DefaultAccount.NAME));
    }

    public static boolean isRecentsWithinLauncher(Context context) {
        return isRecentsWithinLauncher(context, "com.miui.home") || isRecentsWithinLauncher(context, "com.mi.android.globallauncher");
    }

    private static boolean isRecentsWithinLauncher(Context context, String str) {
        PackageInfo packageInfo;
        ApplicationInfo applicationInfo;
        Bundle bundle;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 128);
        } catch (Exception e) {
            Log.e("Utils", "isRecentsWithinLauncher: getPackageInfo error.", e);
            packageInfo = null;
        }
        boolean z = false;
        if (packageInfo != null && (applicationInfo = packageInfo.applicationInfo) != null && (bundle = applicationInfo.metaData) != null) {
            z = bundle.getBoolean("supportRecents", false);
        }
        Log.e("Utils", "isRecentsWithinLauncher=" + z);
        return z;
    }

    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    public static boolean supportCutoutMode() {
        return CustomizeUtil.HAS_NOTCH && Build.VERSION.SDK_INT >= 28 && !"odin".equals(android.os.Build.DEVICE);
    }

    public static boolean supportOverlayRoundedCorner() {
        String str = android.os.Build.DEVICE;
        return "cepheus".equals(str) || "grus".equals(str) || "crux".equals(str);
    }

    public static boolean useMiuiHomeAsDefaultHome(Context context) {
        ActivityInfo activityInfo;
        String str;
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 786432);
        return resolveActivity == null || (activityInfo = resolveActivity.activityInfo) == null || (str = activityInfo.packageName) == null || "com.miui.home".equals(str) || "com.mi.android.globallauncher".equals(str);
    }
}
