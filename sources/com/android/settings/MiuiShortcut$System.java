package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import android.provider.Settings;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public final class MiuiShortcut$System {
    public static boolean hasKnockFeature(Context context) {
        return MiuiSettings.Global.getBoolean(context.getContentResolver(), "support_knock");
    }

    public static boolean hasSmartHome(Context context) {
        return context.getPackageManager().queryIntentActivities(new Intent().setClassName("com.miui.smarthomeplus", "com.miui.smarthomeplus.UWBEntryActivity"), 0).size() > 0;
    }

    public static boolean hasVoiceAssist(Context context) {
        return MiuiUtils.includeXiaoAi(context);
    }

    public static boolean isFullScreenStatus(Context context) {
        return MiuiSettings.Global.getBoolean(context.getContentResolver(), "force_fsg_nav_bar");
    }

    public static boolean isSupportNewVersionKeySettings(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "support_gesture_shortcut_settings", 0, 0) == 1;
    }

    public static boolean shouldShowAiButton() {
        return FeatureParser.getBoolean("support_ai_task", false);
    }

    public static boolean supportFpNavCenterToHome() {
        return FeatureParser.getBoolean("support_tap_fingerprint_sensor_to_home", false);
    }

    public static boolean supportPartialScreenShot() {
        return FeatureParser.getBoolean("is_support_partial_screenshot", true);
    }
}
