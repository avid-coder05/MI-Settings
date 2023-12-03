package com.android.settings.utils;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.MiuiKeyguardSettingsUtils;
import com.android.settings.R;
import com.android.settings.compat.AmbientDisplayConfigurationCompat;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
public class AodUtils {
    public static final String AOD_MODE;
    public static final int AOD_SHOW_STYLE_DEF;
    public static final boolean IS_PCMODE_ENABLED;
    private static final String[] KEYCODE_GOTO_ENABLE_DEVICE;

    static {
        AOD_MODE = Build.VERSION.SDK_INT >= 28 ? "doze_always_on" : "aod_mode";
        KEYCODE_GOTO_ENABLE_DEVICE = new String[]{"pyxis"};
        AOD_SHOW_STYLE_DEF = !isKeycodeGotoEnable() ? 1 : 0;
        IS_PCMODE_ENABLED = SystemProperties.getInt("persist.sys.miui.pcmode", 0) > 0;
    }

    public static boolean actionAvailable(Context context) {
        return new Intent("com.miui.aod.style_list").resolveActivity(context.getPackageManager()) != null;
    }

    public static int convertNotificationStatusToPrefIndex(Context context) {
        int keyguardNotificationStatus = MiuiKeyguardSettingsUtils.getKeyguardNotificationStatus(context, context.getContentResolver());
        String[] stringArray = context.getResources().getStringArray(R.array.keyguard_notification_status_values);
        for (int i = 0; i < stringArray.length; i++) {
            if (keyguardNotificationStatus == Integer.valueOf(stringArray[i]).intValue()) {
                return i;
            }
        }
        return 0;
    }

    public static String getNotificationWakeUpStyle(Context context) {
        String[] stringArray;
        int convertNotificationStatusToPrefIndex = convertNotificationStatusToPrefIndex(context);
        return (convertNotificationStatusToPrefIndex >= 0 && (stringArray = context.getResources().getStringArray(R.array.aod_notification_status_entries)) != null && stringArray.length > convertNotificationStatusToPrefIndex) ? stringArray[convertNotificationStatusToPrefIndex] : "";
    }

    public static boolean isAodAvailable(Context context) {
        boolean isAvailable = AmbientDisplayConfigurationCompat.isAvailable(context);
        Log.i("AodUtils", "isAodAvailable: " + isAvailable);
        return isAvailable;
    }

    public static boolean isAodEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "doze_always_on", 0, UserHandle.myUserId()) != 0;
    }

    public static boolean isKeycodeGotoEnable() {
        String str = Build.DEVICE;
        int i = 0;
        while (true) {
            String[] strArr = KEYCODE_GOTO_ENABLE_DEVICE;
            if (i >= strArr.length) {
                return FeatureParser.getBoolean("aod_support_keycode_goto_dismiss", false);
            }
            if (str.equals(strArr[i])) {
                return true;
            }
            i++;
        }
    }

    public static void registerAodStateObserver(Context context, ContentObserver contentObserver) {
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(AOD_MODE), false, contentObserver);
    }

    public static void setAodModeState(Context context, boolean z) {
        Log.i("AodUtils", "setAodModeState: " + z);
        Settings.Secure.putInt(context.getContentResolver(), AOD_MODE, z ? 1 : 0);
    }

    public static void setAodModeUserSet(Context context, boolean z) {
        Log.i("AodUtils", "setAodModeUserSet: " + z);
        Settings.Secure.putInt(context.getContentResolver(), "aod_mode_user_set", z ? 1 : 0);
    }

    public static boolean supportSettingSplit(Context context) {
        return KeyguardSettingsFaceUnlockUtils.isLargeScreen(context) || Build.DEVICE.equals("cetus");
    }

    public static void unregisterAodStateObserver(Context context, ContentObserver contentObserver) {
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }
}
