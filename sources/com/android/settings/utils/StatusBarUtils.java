package com.android.settings.utils;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;
import com.android.settings.R;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
public class StatusBarUtils {
    public static final boolean IS_CUST_SINGLE_SIM;
    public static final boolean IS_FOLD;
    public static final boolean IS_LM_CR;
    public static final boolean IS_MX_TELCEL;
    public static final boolean IS_NOTCH;
    public static final boolean IS_SUPPORT_HIGH_PRIORITY;
    public static final boolean IS_SUPPORT_LED;

    static {
        IS_NOTCH = SystemProperties.getInt("ro.miui.notch", 0) == 1;
        IS_FOLD = SystemProperties.getInt("persist.sys.muiltdisplay_type", 0) == 2;
        IS_CUST_SINGLE_SIM = SystemProperties.getInt("ro.miui.singlesim", 0) == 1;
        IS_MX_TELCEL = "mx_telcel".equals(SystemProperties.get("ro.miui.customized.region", ""));
        IS_LM_CR = "lm_cr".equals(SystemProperties.get("ro.miui.customized.region", ""));
        IS_SUPPORT_HIGH_PRIORITY = Build.VERSION.SDK_INT < 26;
        IS_SUPPORT_LED = FeatureParser.getBoolean("support_led_light", true);
    }

    public static int getNotificationShadeShortcut(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "status_bar_notification_shade_shortcut", 1);
    }

    public static int getNotificationStyle(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "status_bar_notification_style", miui.os.Build.IS_INTERNATIONAL_BUILD ? 1 : 0);
    }

    public static int getUserAggregate(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "user_aggregate", 0);
    }

    public static boolean isCompactQuickSettings(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "qs_compact_layout", 0) == 1;
    }

    public static boolean isControlPanelSwitchSide(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "control_panel_switch_side", 0) == 1;
    }

    public static boolean isExpandableUnderLockscreen(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "expandable_under_lock_screen", 1) == 1;
    }

    public static boolean isForceUseControlPanel(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "force_use_control_panel", 0) == 1;
    }

    public static boolean isMiSmartHub(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "mi_smart_hub_visible", 1) == 1;
    }

    public static boolean isMiSmartHubVisible(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "mi_smart_hub_visible", 10) / 10 == 0;
    }

    public static boolean isMiSmartPlay(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "mi_smart_play_visible", 1) == 1;
    }

    public static boolean isMiuiOptimizationOff(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "miui_optimization", 1) == 0;
    }

    public static boolean isNotificationUseAppIcon(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "notification_use_app_icon", 1) == 1;
    }

    public static boolean isShowFoldFooterIcons(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "fold_footer_icons", -1) > 0;
    }

    public static boolean isShowLTEFor4G(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "status_bar_show_lte_for_4g", 0) == 1;
    }

    public static boolean isUseControlPanel(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), "use_control_panel", context.getResources().getInteger(R.integer.use_control_panel_setting_default), 0) == 1;
    }

    public static boolean isUserAggregate(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "user_aggregate", 0) > 0;
    }

    public static boolean isUserFold(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "user_fold", 0) > 0;
    }

    public static void setCompactQuickSettings(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "qs_compact_layout", z ? 1 : 0);
    }

    public static void setControlPanelSwitchSide(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "control_panel_switch_side", z ? 1 : 0);
    }

    public static void setExpandableUnderLockscreen(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "expandable_under_lock_screen", i);
    }

    public static void setMiSmartHub(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "mi_smart_hub_visible", i);
    }

    public static void setMiSmartPlay(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "mi_smart_play_visible", i);
    }

    public static void setNotificationShadeShortcut(Context context, int i) {
        Settings.Secure.putInt(context.getContentResolver(), "status_bar_notification_shade_shortcut", i);
    }

    public static void setNotificationStyle(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "status_bar_notification_style", i);
    }

    public static void setNotificationUseAppIcon(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "notification_use_app_icon", z ? 1 : 0);
    }

    public static void setShowFoldFooterIcons(Context context, boolean z) {
        Settings.Global.putInt(context.getContentResolver(), "fold_footer_icons", z ? 1 : -1);
    }

    public static void setShowLTEFor4G(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "status_bar_show_lte_for_4g", z ? 1 : 0);
    }

    public static void setUseControlPanel(Context context, int i) {
        Settings.System.putIntForUser(context.getContentResolver(), "use_control_panel", i, 0);
    }

    public static void setUserAggregate(Context context, int i) {
        Settings.Global.putInt(context.getContentResolver(), "user_aggregate", i);
    }

    public static void setUserFold(Context context, boolean z) {
        Settings.Global.putInt(context.getContentResolver(), "user_fold", z ? 1 : -1);
    }
}
