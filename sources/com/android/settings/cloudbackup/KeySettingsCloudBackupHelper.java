package com.android.settings.cloudbackup;

import android.content.ContentResolver;
import android.content.Context;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.MiuiUtils;
import miui.util.FeatureParser;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class KeySettingsCloudBackupHelper {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        ContentResolver contentResolver = context.getContentResolver();
        if (FeatureParser.getBoolean("support_screen_key_swap", false) && jSONObject.has("screen_key_handswap")) {
            SystemProperties.set("persist.sys.handswap", jSONObject.optString("screen_key_handswap"));
        }
        MiuiSettings.System.putBoolean(contentResolver, "screen_key_press_app_switch", jSONObject.optBoolean("screen_key_press_app_switch", true));
        MiuiSettings.System.putString(contentResolver, "long_press_menu_key_when_lock", jSONObject.optString("long_press_menu_key_when_lock"));
        MiuiSettings.System.putString(contentResolver, "double_click_power_key", jSONObject.optString("double_click_power_key"));
        MiuiSettings.System.putString(contentResolver, "three_gesture_down", jSONObject.optString("three_gesture_down"));
        MiuiSettings.System.putString(contentResolver, "long_press_home_key", jSONObject.optString("long_press_home_key"));
        MiuiSettings.System.putString(contentResolver, "long_press_menu_key", jSONObject.optString("long_press_menu_key"));
        MiuiSettings.System.putString(contentResolver, "long_press_back_key", jSONObject.optString("long_press_back_key"));
        MiuiSettings.System.putString(contentResolver, "key_combination_power_home", jSONObject.optString("key_combination_power_home"));
        MiuiSettings.System.putString(contentResolver, "key_combination_power_menu", jSONObject.optString("key_combination_power_menu"));
        MiuiSettings.System.putString(contentResolver, "key_combination_power_back", jSONObject.optString("key_combination_power_back"));
        if (FeatureParser.getBoolean("support_tap_fingerprint_sensor_to_home", false)) {
            Settings.System.putInt(contentResolver, "fingerprint_nav_center_action", jSONObject.optInt("pref_fingerprint_nav_center_to_home"));
            Settings.System.putInt(contentResolver, "single_key_use_enable", jSONObject.optInt("pref_single_key_use"));
        }
        if (FeatureParser.getBoolean("support_button_light", false)) {
            int optInt = jSONObject.optInt("screen_buttons_light_timeout", Integer.MIN_VALUE);
            if (optInt != Integer.MIN_VALUE) {
                Settings.System.putInt(contentResolver, "screen_buttons_timeout", optInt);
            }
            Settings.Secure.putInt(contentResolver, "screen_buttons_turn_on", jSONObject.optBoolean("screen_buttons_light_on") ? 1 : 0);
        }
        if (jSONObject.has("screen_buttons_auto_disable")) {
            MiuiSettings.System.putString(contentResolver, "auto_disable_screen_button", jSONObject.optString("screen_buttons_auto_disable"));
        }
        boolean optBoolean = jSONObject.optBoolean("keyguard_volume_wake");
        Settings.System.putInt(contentResolver, "volumekey_wake_screen", optBoolean ? 1 : 0);
        MiuiUtils.enableVolumKeyWakeUp(optBoolean);
        Settings.System.putInt(contentResolver, "volumekey_launch_camera", jSONObject.optBoolean("keyguard_volume_launch_camera") ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            ContentResolver contentResolver = context.getContentResolver();
            if (FeatureParser.getBoolean("support_screen_key_swap", false)) {
                jSONObject.put("screen_key_handswap", SystemProperties.get("persist.sys.handswap", "0"));
            }
            jSONObject.put("screen_key_press_app_switch", MiuiSettings.System.getBoolean(contentResolver, "screen_key_press_app_switch", true));
            jSONObject.put("long_press_menu_key_when_lock", MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, "long_press_menu_key_when_lock"));
            jSONObject.put("double_click_power_key", MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, "double_click_power_key"));
            jSONObject.put("three_gesture_down", MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, "three_gesture_down"));
            jSONObject.put("long_press_home_key", MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, "long_press_home_key"));
            jSONObject.put("long_press_menu_key", MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, "long_press_menu_key"));
            jSONObject.put("long_press_back_key", MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, "long_press_back_key"));
            jSONObject.put("key_combination_power_home", MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, "key_combination_power_home"));
            jSONObject.put("key_combination_power_menu", MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, "key_combination_power_menu"));
            jSONObject.put("key_combination_power_back", MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, "key_combination_power_back"));
            jSONObject.put("screen_buttons_light_timeout", Settings.System.getString(contentResolver, "screen_buttons_timeout"));
            jSONObject.put("pref_fingerprint_nav_center_to_home", Settings.System.getInt(contentResolver, "fingerprint_nav_center_action", 0));
            jSONObject.put("pref_single_key_use", Settings.System.getInt(contentResolver, "single_key_use_enable", 0));
            jSONObject.put("screen_buttons_light_on", Settings.Secure.getInt(contentResolver, "screen_buttons_turn_on", 1) == 1);
            jSONObject.put("screen_buttons_auto_disable", MiuiSettings.System.getString(contentResolver, "auto_disable_screen_button"));
            jSONObject.put("keyguard_volume_wake", Settings.System.getInt(contentResolver, "volumekey_wake_screen", 0) == 1);
            jSONObject.put("keyguard_volume_launch_camera", Settings.System.getInt(contentResolver, "volumekey_launch_camera", 1) == 1);
        } catch (JSONException e) {
            Log.e("KeySettingsCloudBackupHelper", "build json error: ", e);
            CloudBackupException.trackException();
        }
        return jSONObject;
    }
}
