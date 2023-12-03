package com.android.settings.cloudbackup;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.MiuiKeyguardSettingsUtils;
import miui.keyguard.clock.KeyguardClockController;
import miui.util.FeatureParser;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class LockScreenSettingsCloudBackupHelper {
    private static boolean isEllipticProximity(Context context) {
        return Build.VERSION.SDK_INT >= 28 ? SystemProperties.getBoolean("ro.vendor.audio.us.proximity", false) : SystemProperties.getBoolean("ro.audio.us.proximity", false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        ContentResolver contentResolver = context.getContentResolver();
        if (jSONObject.has("CKLeftFunctionEnabled")) {
            MiuiSettings.System.putBoolean(contentResolver, "keyguard_left_function_enabled", jSONObject.optBoolean("CKLeftFunctionEnabled"));
        }
        if (jSONObject.has("CKLeftFunction")) {
            MiuiSettings.System.putString(contentResolver, "keyguard_left_function_chooser", jSONObject.optString("CKLeftFunction"));
        }
        if (jSONObject.has("CKRightFunctionEnabled")) {
            MiuiSettings.System.putBoolean(contentResolver, "keyguard_right_function_enabled", jSONObject.optBoolean("CKRightFunctionEnabled"));
        }
        if (jSONObject.has("CKRightFunction")) {
            MiuiSettings.System.putString(contentResolver, "keyguard_right_function_chooser", jSONObject.optString("CKRightFunction"));
        }
        if (jSONObject.has("CKLockScreenMagazineStatus")) {
            MiuiSettings.Secure.putBoolean(contentResolver, "lock_screen_magazine_status", jSONObject.optBoolean("CKLockScreenMagazineStatus"));
        }
        if (jSONObject.has("CKScreenTimeout")) {
            Settings.System.putLong(contentResolver, "screen_off_timeout", jSONObject.optLong("CKScreenTimeout"));
        }
        if (jSONObject.has("CKWakeForNotifications")) {
            Settings.System.putInt(contentResolver, "wakeup_for_keyguard_notification", jSONObject.optBoolean("CKWakeForNotifications") ? 1 : 0);
        }
        if (jSONObject.has("CKNotifications")) {
            MiuiKeyguardSettingsUtils.putKeyguardNotificationStatus(context, contentResolver, jSONObject.optInt("CKNotifications"));
        }
        if (jSONObject.has("CKVolumeKeyWakeScreen")) {
            Settings.System.putInt(contentResolver, "volumekey_wake_screen", jSONObject.optInt("CKVolumeKeyWakeScreen"));
        }
        if (jSONObject.has("CKGestureWakeup")) {
            MiuiSettings.System.putBoolean(contentResolver, "gesture_wakeup", jSONObject.optBoolean("CKGestureWakeup"));
        }
        if (jSONObject.has("CKPickupWakeup")) {
            MiuiSettings.System.putBoolean(contentResolver, "pick_up_gesture_wakeup_mode", jSONObject.optBoolean("CKPickupWakeup"));
        }
        if (jSONObject.has("CKVolumeKeyLaunchCamera")) {
            Settings.System.putInt(contentResolver, "volumekey_launch_camera", jSONObject.optInt("CKVolumeKeyLaunchCamera"));
        }
        if (jSONObject.has("CKShowLunarCalendar")) {
            Settings.System.putInt(contentResolver, "show_lunar_calendar", jSONObject.optInt("CKShowLunarCalendar"));
        }
        if (jSONObject.has("CKKeyguardClockPosition")) {
            Settings.System.putInt(contentResolver, KeyguardClockController.SELECTED_KEYGUARD_CLOCK_POSITION, jSONObject.optInt("CKKeyguardClockPosition"));
        }
        if (jSONObject.has("CKOwnerInfoEnabled")) {
            new LockPatternUtils(context).setOwnerInfoEnabled(jSONObject.optBoolean("CKOwnerInfoEnabled"), UserHandle.myUserId());
        }
        if (jSONObject.has("CKOwnerInfoContent")) {
            new LockPatternUtils(context).setOwnerInfo(jSONObject.optString("CKOwnerInfoContent"), UserHandle.myUserId());
        }
        if (jSONObject.has("CKSmartCoverMode") && FeatureParser.getBoolean("support_hall_sensor", false)) {
            SystemProperties.set("persist.sys.smartcover_mode", jSONObject.optString("CKSmartCoverMode"));
        }
        if (jSONObject.has("CKPocketMode") && context.getPackageManager().hasSystemFeature("android.hardware.sensor.proximity") && !isEllipticProximity(context)) {
            Settings.Global.putInt(contentResolver, "enable_screen_on_proximity_sensor", jSONObject.optInt("CKPocketMode"));
        }
        if (jSONObject.has("CKShowChargingInNonLockScreen")) {
            Settings.System.putInt(contentResolver, "show_charging_in_non_lockscreen", jSONObject.optInt("CKShowChargingInNonLockScreen"));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("CKLeftFunctionEnabled", MiuiSettings.System.getBoolean(contentResolver, "keyguard_left_function_enabled", true));
            jSONObject.put("CKLeftFunction", MiuiSettings.System.getString(contentResolver, "keyguard_left_function_chooser"));
            jSONObject.put("CKRightFunctionEnabled", MiuiSettings.System.getBoolean(contentResolver, "keyguard_right_function_enabled", true));
            jSONObject.put("CKRightFunction", MiuiSettings.System.getString(contentResolver, "keyguard_right_function_chooser"));
            jSONObject.put("CKLockScreenMagazineStatus", MiuiSettings.Secure.getBoolean(contentResolver, "lock_screen_magazine_status", false));
            jSONObject.put("CKScreenTimeout", Settings.System.getLong(contentResolver, "screen_off_timeout", 30000L));
            jSONObject.put("CKWakeForNotifications", MiuiKeyguardSettingsUtils.isWakeupForNotification(context, contentResolver));
            jSONObject.put("CKNotifications", MiuiKeyguardSettingsUtils.getKeyguardNotificationStatus(context, contentResolver));
            jSONObject.put("CKVolumeKeyWakeScreen", Settings.System.getInt(contentResolver, "volumekey_wake_screen", 0));
            jSONObject.put("CKGestureWakeup", MiuiSettings.System.getBoolean(contentResolver, "gesture_wakeup", false));
            jSONObject.put("CKPickupWakeup", MiuiSettings.System.getBoolean(contentResolver, "pick_up_gesture_wakeup_mode", false));
            jSONObject.put("CKVolumeKeyLaunchCamera", Settings.System.getInt(contentResolver, "volumekey_launch_camera", 1));
            jSONObject.put("CKShowLunarCalendar", Settings.System.getInt(contentResolver, "show_lunar_calendar", 0));
            jSONObject.put("CKKeyguardClockPosition", Settings.System.getInt(contentResolver, KeyguardClockController.SELECTED_KEYGUARD_CLOCK_POSITION, 0));
            jSONObject.put("CKOwnerInfoEnabled", new LockPatternUtils(context).isOwnerInfoEnabled(UserHandle.myUserId()));
            jSONObject.put("CKOwnerInfoContent", new LockPatternUtils(context).getOwnerInfo(UserHandle.myUserId()));
            jSONObject.put("CKSmartCoverMode", SystemProperties.getInt("persist.sys.smartcover_mode", -1));
            jSONObject.put("CKPocketMode", Settings.Global.getInt(contentResolver, "enable_screen_on_proximity_sensor", -1));
            jSONObject.put("CKShowChargingInNonLockScreen", Settings.System.getInt(contentResolver, "show_charging_in_non_lockscreen", 1));
        } catch (JSONException e) {
            Log.e("LockScreenSettingsCloudBackupHelper", "build json error: ", e);
            CloudBackupException.trackException();
        }
        return jSONObject;
    }
}
