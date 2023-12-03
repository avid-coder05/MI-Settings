package com.android.settings.display;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.provider.SystemSettings$System;
import android.util.Log;
import com.android.settings.network.telephony.ToggleSubscriptionDialogActivity;
import miui.hardware.display.DisplayFeatureManager;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class DarkModeTimeModeUtil {
    public static final boolean SUPPORT_DARK_MODE_NOTIFY = FeatureParser.getBoolean("support_dark_mode_notify", false);

    public static int getDarkModeEndTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "dark_mode_time_end", 420);
    }

    public static int getDarkModeStartTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "dark_mode_time_start", 1140);
    }

    public static int getDarkModeTimeType(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "dark_mode_time_type", 2);
    }

    public static boolean isDarkModeAutoTimeEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "dark_mode_auto_time_enable", false);
    }

    public static boolean isDarkModeContrastEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "dark_mode_contrast_enable", false);
    }

    public static boolean isDarkModeEnable(Context context) {
        return ((UiModeManager) context.getSystemService(UiModeManager.class)).getNightMode() == 2;
    }

    public static boolean isDarkModeTimeEnable(Context context) {
        return Settings.System.getBoolean(context.getContentResolver(), "dark_mode_time_enable", false);
    }

    public static boolean isDarkWallpaperModeEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), SystemSettings$System.DARKEN_WALLPAPER_UNDER_DARK_MODE, true);
    }

    public static boolean isSmartDarkEnable() {
        return SystemProperties.getBoolean("debug.hwui.force_dark", false);
    }

    public static boolean isSunRiseSunSetMode(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "dark_mode_sun_time_mode_enable", false);
    }

    private static void sendDarkModeTimeModeCast(Context context, boolean z, boolean z2) {
        Log.d("DarkModeTimeModeUtil", "sendDarkModeTimeModeCast");
        Intent intent = new Intent("miui.action.intent.DARK_MODE_TIME_MODE");
        intent.putExtra(ToggleSubscriptionDialogActivity.ARG_enable, z);
        intent.putExtra("onlyRegisterAlarm", z2);
        context.sendBroadcast(intent);
    }

    public static void setDarkModeAutoTimeEnable(Context context, boolean z) {
        MiuiSettings.System.putBoolean(context.getContentResolver(), "dark_mode_auto_time_enable", z);
    }

    public static void setDarkModeContrastEnable(Context context, boolean z) {
        MiuiSettings.System.putBoolean(context.getContentResolver(), "dark_mode_contrast_enable", z);
    }

    public static void setDarkModeEnable(Context context, boolean z, boolean z2) {
        Log.d("DarkModeTimeModeUtil", "setDarkModeEnable: enable=" + z);
        Settings.Global.putInt(context.getContentResolver(), "uimode_timing", z2 ? 1 : 0);
        Settings.System.putIntForUser(context.getContentResolver(), "dark_mode_enable", z ? 1 : 0, 0);
        ((UiModeManager) context.getSystemService(UiModeManager.class)).setNightMode(z ? 2 : 1);
        if (SUPPORT_DARK_MODE_NOTIFY) {
            DisplayFeatureManager.getInstance().setScreenEffect(38, z ? 1 : 0);
        }
    }

    public static void setDarkModeEndTime(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "dark_mode_time_end", i);
    }

    public static void setDarkModeStartTime(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "dark_mode_time_start", i);
    }

    public static void setDarkModeTimeEnable(Context context, boolean z) {
        MiuiSettings.System.putBoolean(context.getContentResolver(), "dark_mode_time_enable", z);
    }

    public static void setDarkModeTimeEnablePrefsOnly(Context context, boolean z) {
        MiuiSettings.System.putBoolean(context.getContentResolver(), "dark_mode_time_enable", z);
    }

    public static void setDarkWallpaperModeEnable(Context context, boolean z) {
        MiuiSettings.System.putBoolean(context.getContentResolver(), SystemSettings$System.DARKEN_WALLPAPER_UNDER_DARK_MODE, z);
    }

    public static void setSunRiseSunSetMode(Context context, boolean z) {
        MiuiSettings.System.putBoolean(context.getContentResolver(), "dark_mode_sun_time_mode_enable", z);
    }

    public static void startDarkModeAutoTime(Context context, boolean z) {
        startDarkModeAutoTime(context, z, false);
    }

    public static void startDarkModeAutoTime(Context context, boolean z, boolean z2) {
        sendDarkModeTimeModeCast(context, z, z2);
    }
}
