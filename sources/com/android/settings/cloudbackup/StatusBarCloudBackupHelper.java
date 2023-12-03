package com.android.settings.cloudbackup;

import android.app.MiuiStatusBarManager;
import android.content.Context;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.utils.StatusBarUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class StatusBarCloudBackupHelper {
    private static int getBatteryIndicatorStyle(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "battery_indicator_style", 1);
    }

    private static String getCustomCarrier(Context context, int i) {
        if (i < 0 || i > 2) {
            return "";
        }
        return MiuiSettings.System.getString(context.getContentResolver(), "status_bar_custom_carrier" + i, "");
    }

    public static boolean getFsgNavBar(Context context) {
        return MiuiSettings.Global.getBoolean(context.getContentResolver(), "force_fsg_nav_bar");
    }

    private static String getQuickSettingsOrder(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "sysui_qs_tiles");
    }

    public static String getScreenKeyOrder(Context context) {
        return Settings.System.getString(context.getContentResolver(), "screen_key_order");
    }

    private static int getShowCarrierStyle(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "status_bar_show_custom_carrier", MiuiSettings.System.getShowCustomCarrierDefault());
    }

    private static int getShowCarrierUnderKeyguard(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "status_bar_show_carrier_under_keyguard", 1);
    }

    private static int getStatusBarStyle(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "status_bar_style_type", 0);
    }

    private static boolean isProvisioned(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        if (jSONObject.has("CKNotificationFold")) {
            StatusBarUtils.setUserFold(context, jSONObject.optBoolean("CKNotificationFold"));
        }
        if (jSONObject.has("CKNotificationAggregate")) {
            StatusBarUtils.setUserAggregate(context, jSONObject.optInt("CKNotificationAggregate"));
        }
        if (jSONObject.has("CKNotificationBucket")) {
            SystemProperties.set("persist.sys.notification_rank", jSONObject.optString("CKNotificationBucket"));
        }
        if (jSONObject.has("CKNotificationBucketVersion")) {
            SystemProperties.set("persist.sys.notification_ver", jSONObject.optString("CKNotificationBucketVersion"));
        }
        if (jSONObject.has("CKFoldFooterIcon")) {
            StatusBarUtils.setShowFoldFooterIcons(context, jSONObject.optBoolean("CKFoldFooterIcon"));
        }
        if (jSONObject.has("CKStatusBarStyle")) {
            Settings.System.putInt(context.getContentResolver(), "status_bar_style_type", jSONObject.optInt("CKStatusBarStyle"));
        }
        if (jSONObject.has("CKQuickSettingsOrder")) {
            Settings.Secure.putString(context.getContentResolver(), "sysui_qs_tiles", jSONObject.optString("CKQuickSettingsOrder"));
        }
        if (jSONObject.has("CKNotificationIcon")) {
            MiuiStatusBarManager.setShowNotificationIcon(context, jSONObject.optBoolean("CKNotificationIcon"));
        }
        if (jSONObject.has("CKNetworkSpeed")) {
            MiuiStatusBarManager.setShowNetworkSpeed(context, jSONObject.optBoolean("CKNetworkSpeed"));
        }
        if (jSONObject.has("CKShowCarrierStyle")) {
            setShowCarrierStyle(context, jSONObject.optInt("CKShowCarrierStyle"));
        } else if (jSONObject.has("CKShowCarrier")) {
            MiuiStatusBarManager.setShowCarrier(context, jSONObject.optBoolean("CKShowCarrier"));
        }
        if (jSONObject.has("CKShowCarrierUnderKeyguard")) {
            setShowCarrierUnderKeygurad(context, jSONObject.optInt("CKShowCarrierUnderKeyguard"));
        }
        setCustomCarrier(context, jSONObject.optString("CKCustomCarrierSim0"), 0);
        setCustomCarrier(context, jSONObject.optString("CKCustomCarrierSim1"), 1);
        if (jSONObject.has("CKBatteryIndicatorStyle")) {
            Settings.System.putInt(context.getContentResolver(), "battery_indicator_style", jSONObject.optInt("CKBatteryIndicatorStyle"));
        }
        if (jSONObject.has("CKCollapseToggle")) {
            MiuiStatusBarManager.setCollapseAfterClicked(context, jSONObject.optBoolean("CKCollapseToggle"));
        }
        if (jSONObject.has("CKKeyguardExpand")) {
            MiuiStatusBarManager.setExpandableUnderKeyguard(context, jSONObject.optBoolean("CKKeyguardExpand"));
        }
        if (jSONObject.has("CKShadeShortcut")) {
            StatusBarUtils.setNotificationShadeShortcut(context, jSONObject.optInt("CKShadeShortcut"));
        }
        if (jSONObject.has("CKNotificationStyle")) {
            StatusBarUtils.setNotificationStyle(context, jSONObject.optInt("CKNotificationStyle"));
        }
        if (jSONObject.has("CKUseControlPanel")) {
            StatusBarUtils.setUseControlPanel(context, jSONObject.optBoolean("CKUseControlPanel") ? 1 : 0);
        }
        if (jSONObject.has("CKLSShowNotifications")) {
            Settings.Secure.putInt(context.getContentResolver(), "lock_screen_show_notifications", jSONObject.optInt("CKLSShowNotifications"));
        }
        if (jSONObject.has("CKLSAllowPrivateNotifications")) {
            Settings.Secure.putInt(context.getContentResolver(), "lock_screen_allow_private_notifications", jSONObject.optInt("CKLSAllowPrivateNotifications"));
        }
        if (isProvisioned(context) && jSONObject.has("CKForceFsgNavBar")) {
            MiuiSettings.Global.putBoolean(context.getContentResolver(), "force_fsg_nav_bar", jSONObject.optBoolean("CKForceFsgNavBar"));
        }
        if (jSONObject.has("CKScreenKeyOrder")) {
            Settings.System.putString(context.getContentResolver(), "screen_key_order", jSONObject.optString("CKScreenKeyOrder"));
        }
        if (jSONObject.has("CKShowMistakeTouchToast")) {
            Settings.Global.putInt(context.getContentResolver(), "show_mistake_touch_toast", jSONObject.optInt("CKShowMistakeTouchToast"));
        }
        if (isProvisioned(context) && jSONObject.has("CKHideGestureLine")) {
            Settings.Global.putInt(context.getContentResolver(), "hide_gesture_line", jSONObject.optInt("CKHideGestureLine"));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("CKNotificationFold", StatusBarUtils.isUserFold(context));
            jSONObject.put("CKNotificationAggregate", StatusBarUtils.getUserAggregate(context));
            jSONObject.put("CKNotificationBucket", SystemProperties.get("persist.sys.notification_rank", ""));
            jSONObject.put("CKNotificationBucketVersion", SystemProperties.get("persist.sys.notification_ver", ""));
            jSONObject.put("CKFoldFooterIcon", StatusBarUtils.isShowFoldFooterIcons(context));
            jSONObject.put("CKStatusBarStyle", getStatusBarStyle(context));
            jSONObject.put("CKQuickSettingsOrder", getQuickSettingsOrder(context));
            jSONObject.put("CKNotificationIcon", MiuiStatusBarManager.isShowNotificationIcon(context));
            jSONObject.put("CKNetworkSpeed", MiuiStatusBarManager.isShowNetworkSpeed(context));
            jSONObject.put("CKShowCarrierStyle", getShowCarrierStyle(context));
            jSONObject.put("CKShowCarrierUnderKeyguard", getShowCarrierUnderKeyguard(context));
            jSONObject.put("CKCustomCarrierSim0", getCustomCarrier(context, 0));
            jSONObject.put("CKCustomCarrierSim1", getCustomCarrier(context, 1));
            jSONObject.put("CKBatteryIndicatorStyle", getBatteryIndicatorStyle(context));
            jSONObject.put("CKCollapseToggle", MiuiStatusBarManager.isCollapseAfterClicked(context));
            jSONObject.put("CKKeyguardExpand", MiuiStatusBarManager.isExpandableUnderKeyguard(context));
            jSONObject.put("CKShadeShortcut", StatusBarUtils.getNotificationShadeShortcut(context));
            jSONObject.put("CKNotificationStyle", StatusBarUtils.getNotificationStyle(context));
            jSONObject.put("CKUseControlPanel", StatusBarUtils.isUseControlPanel(context));
            jSONObject.put("CKForceFsgNavBar", getFsgNavBar(context));
            jSONObject.put("CKScreenKeyOrder", getScreenKeyOrder(context));
            int i = Settings.Secure.getInt(context.getContentResolver(), "lock_screen_show_notifications", 0);
            if (i != 0) {
                jSONObject.put("CKLSShowNotifications", i);
            }
            int i2 = Settings.Secure.getInt(context.getContentResolver(), "lock_screen_allow_private_notifications", 0);
            if (i2 != 0) {
                jSONObject.put("CKLSAllowPrivateNotifications", i2);
            }
            int i3 = Settings.Global.getInt(context.getContentResolver(), "show_mistake_touch_toast", -1);
            if (i3 != -1) {
                jSONObject.put("CKShowMistakeTouchToast", i3);
            }
            int i4 = Settings.Global.getInt(context.getContentResolver(), "hide_gesture_line", -1);
            if (i4 != -1) {
                jSONObject.put("CKHideGestureLine", i4);
            }
        } catch (JSONException unused) {
            Log.e("StatusBarCloudBackupHelper", "Build JSON failed. ");
        }
        return jSONObject;
    }

    private static void setCustomCarrier(Context context, String str, int i) {
        if (i < 0 || i > 2) {
            return;
        }
        MiuiSettings.System.putString(context.getContentResolver(), "status_bar_custom_carrier" + i, str);
    }

    private static void setShowCarrierStyle(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "status_bar_show_custom_carrier", i);
    }

    private static void setShowCarrierUnderKeygurad(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "status_bar_show_carrier_under_keyguard", i);
    }
}
