package com.android.settings.cloudbackup;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.settings.AodStylePreferenceController;
import com.android.settings.MiuiUtils;
import miui.util.HandyModeUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class AdvancedSettingsCloudBackupHelper {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (jSONObject.has("CKAutoTime")) {
            Settings.Global.putInt(contentResolver, "auto_time", jSONObject.optInt("CKAutoTime"));
        }
        if (jSONObject.has("CKTimeHour24")) {
            try {
                Settings.System.putString(contentResolver, "time_12_24", jSONObject.optString("CKTimeHour24"));
                context.sendBroadcast(new Intent("android.intent.action.TIME_SET"));
            } catch (Exception unused) {
                Log.w("AdvancedBackup", "restore time_12_24 failed");
                CloudBackupException.trackException();
            }
        }
        if (jSONObject.has("CKAutoZone")) {
            Settings.Global.putInt(contentResolver, "auto_time_zone", jSONObject.optInt("CKAutoZone"));
        }
        if (jSONObject.has("CKThreeGestureScreenshot")) {
            MiuiSettings.System.putBoolean(contentResolver, "three_gesture_screenshot", jSONObject.optBoolean("CKThreeGestureScreenshot"));
        }
        if (jSONObject.has("CKScreenshotSound")) {
            MiuiSettings.System.putBoolean(contentResolver, "has_screenshot_sound", jSONObject.optBoolean("CKScreenshotSound"));
        }
        if (jSONObject.has("CKHandyModeEnterDirect")) {
            HandyModeUtils.getInstance(context).setHandyModeStateToSettings(jSONObject.optInt("CKHandyModeEnterDirect") != 0);
        }
        if (jSONObject.has("CKHandyModeSize")) {
            HandyModeUtils.getInstance(context).setSize((float) jSONObject.optDouble("CKHandyModeSize"));
        }
        if (jSONObject.has("CKBattaryIndicator")) {
            Settings.System.putInt(contentResolver, "battery_indicator_style", jSONObject.optInt("CKBattaryIndicator"));
        }
        int color = context.getResources().getColor(285605890);
        int integer = context.getResources().getInteger(285933599);
        Settings.System.putInt(contentResolver, "breathing_light_color", jSONObject.optInt("CKBreathingLightColor", color));
        int optInt = jSONObject.optInt("CKBreathingLightFreq", integer);
        if (optInt < 0) {
            optInt = integer;
        }
        Settings.System.putInt(contentResolver, "breathing_light_freq", optInt);
        Settings.System.putInt(contentResolver, "call_breathing_light_color", jSONObject.optInt("CKCallBreathingLightColor", color));
        int optInt2 = jSONObject.optInt("CKCallBreathingLightFreq", integer);
        if (optInt2 < 0) {
            optInt2 = integer;
        }
        Settings.System.putInt(contentResolver, "call_breathing_light_freq", optInt2);
        Settings.System.putInt(contentResolver, "mms_breathing_light_color", jSONObject.optInt("CKMMSBreathingLightColor", color));
        int optInt3 = jSONObject.optInt("CKMMSBreathingLightFreq", integer);
        if (optInt3 >= 0) {
            integer = optInt3;
        }
        Settings.System.putInt(contentResolver, "mms_breathing_light_freq", integer);
        if (jSONObject.has("CKNotificationLightPulse")) {
            Settings.System.putInt(contentResolver, "notification_light_pulse", jSONObject.optInt("CKNotificationLightPulse"));
        }
        if (jSONObject.has("CKBatteryLight")) {
            Settings.Secure.putInt(contentResolver, "battery_light_turn_on", jSONObject.optInt("CKBatteryLight"));
        }
        if (jSONObject.has("CKAutoDualClock")) {
            Settings.System.putInt(contentResolver, AodStylePreferenceController.AUTO_DUAL_CLOCK, jSONObject.optInt("CKAutoDualClock"));
        }
        if (jSONObject.has("CKDualClockResidentId")) {
            Settings.System.putString(contentResolver, "resident_id", jSONObject.optString("CKDualClockResidentId"));
        }
        if (jSONObject.has("CKDualClockResidentTimezone")) {
            Settings.System.putString(contentResolver, AodStylePreferenceController.RESIDENT_TIMEZONE, jSONObject.optString("CKDualClockResidentTimezone"));
        }
        if (jSONObject.has("CKShowImeWithHardKeyboard")) {
            Settings.Secure.putInt(contentResolver, "show_ime_with_hard_keyboard", jSONObject.optInt("CKShowImeWithHardKeyboard"));
        }
        if (jSONObject.has("CKDefaultInputMethod")) {
            String optString = jSONObject.optString("CKDefaultInputMethod");
            if (MiuiUtils.enabledInputMethod(context, optString) && MiuiUtils.isInputMethodSupported(context, optString)) {
                Settings.Secure.putString(contentResolver, "default_input_method", optString);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        JSONObject jSONObject = new JSONObject();
        ContentResolver contentResolver = context.getContentResolver();
        try {
            jSONObject.put("CKAutoTime", Settings.Global.getInt(contentResolver, "auto_time", 0));
            jSONObject.put("CKTimeHour24", DateFormat.is24HourFormat(context) ? "24" : "12");
            jSONObject.put("CKAutoZone", Settings.Global.getInt(contentResolver, "auto_time_zone", 0));
            jSONObject.put("CKThreeGestureScreenshot", MiuiSettings.System.getBoolean(contentResolver, "three_gesture_screenshot", false));
            jSONObject.put("CKScreenshotSound", MiuiSettings.System.getBoolean(contentResolver, "has_screenshot_sound", true));
            jSONObject.put("CKHandyModeEnterDirect", HandyModeUtils.getInstance(context).isEnable() ? 1 : 0);
            jSONObject.put("CKHandyModeSize", HandyModeUtils.getInstance(context).getSize());
            jSONObject.put("CKBreathingLightColor", Settings.System.getString(contentResolver, "breathing_light_color"));
            jSONObject.put("CKBreathingLightFreq", Settings.System.getString(contentResolver, "breathing_light_freq"));
            jSONObject.put("CKCallBreathingLightColor", Settings.System.getString(contentResolver, "call_breathing_light_color"));
            jSONObject.put("CKCallBreathingLightFreq", Settings.System.getString(contentResolver, "call_breathing_light_freq"));
            jSONObject.put("CKMMSBreathingLightColor", Settings.System.getString(contentResolver, "mms_breathing_light_color"));
            jSONObject.put("CKMMSBreathingLightFreq", Settings.System.getString(contentResolver, "mms_breathing_light_freq"));
            jSONObject.put("CKNotificationLightPulse", Settings.System.getString(contentResolver, "notification_light_pulse"));
            jSONObject.put("CKBatteryLight", Settings.Secure.getInt(contentResolver, "battery_light_turn_on", 1));
            jSONObject.put("CKAutoDualClock", Settings.System.getInt(contentResolver, AodStylePreferenceController.AUTO_DUAL_CLOCK, 0));
            jSONObject.put("CKDualClockResidentId", Settings.System.getString(contentResolver, "resident_id"));
            jSONObject.put("CKDualClockResidentTimezone", Settings.System.getString(contentResolver, AodStylePreferenceController.RESIDENT_TIMEZONE));
            jSONObject.put("CKShowImeWithHardKeyboard", Settings.Secure.getInt(contentResolver, "show_ime_with_hard_keyboard", 0));
            jSONObject.put("CKDefaultInputMethod", Settings.Secure.getString(contentResolver, "default_input_method"));
        } catch (JSONException unused) {
            Log.e("AdvancedBackup", "Build JSON failed. ");
            CloudBackupException.trackException();
        }
        return jSONObject;
    }
}
