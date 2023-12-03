package com.android.settings.cloudbackup;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.MiuiConfiguration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.JobDispatcher;
import com.android.settings.MiuiUtils;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settings.display.LargeFontUtils;
import com.android.settings.display.PaperModeSunTimeService;
import com.android.settings.display.PaperModeTimeModeUtil;
import com.android.settings.display.util.PaperConstants;
import miui.util.FeatureParser;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class DisplaySettingsCloudBackupHelper {
    private static void restoreDarkMode(Context context, JSONObject jSONObject) {
        if (jSONObject.has("CKDarkMode")) {
            boolean optBoolean = jSONObject.optBoolean("CKDarkMode");
            if (MiuiUtils.isDeviceProvisioned(context)) {
                DarkModeTimeModeUtil.setDarkModeEnable(context, optBoolean, false);
            }
        }
        if (jSONObject.has("CKDarkModeWallpaperEnable")) {
            DarkModeTimeModeUtil.setDarkWallpaperModeEnable(context, jSONObject.optBoolean("CKDarkModeWallpaperEnable"));
        }
        if (jSONObject.has("CKDarkModeStartTime")) {
            DarkModeTimeModeUtil.setDarkModeStartTime(context, jSONObject.optInt("CKDarkModeStartTime"));
        }
        if (jSONObject.has("CKDarkModeEndTime")) {
            DarkModeTimeModeUtil.setDarkModeEndTime(context, jSONObject.optInt("CKDarkModeEndTime"));
        }
        if (jSONObject.has("CKDarkModeTimeEnable")) {
            DarkModeTimeModeUtil.setDarkModeTimeEnablePrefsOnly(context, jSONObject.optBoolean("CKDarkModeTimeEnable"));
        }
        if (DarkModeTimeModeUtil.isDarkModeTimeEnable(context) && MiuiUtils.isDeviceProvisioned(context)) {
            DarkModeTimeModeUtil.startDarkModeAutoTime(context, true, true);
        }
        if (jSONObject.has("CKDarkModeContrastEnable")) {
            DarkModeTimeModeUtil.setDarkModeContrastEnable(context, jSONObject.optBoolean("CKDarkModeContrastEnable"));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        int optInt;
        ContentResolver contentResolver = context.getContentResolver();
        if (MiuiSettings.ScreenEffect.isScreenPaperModeSupported) {
            if (jSONObject.has("CKPaperModeSchedulerType")) {
                Settings.System.putInt(contentResolver, "paper_mode_scheduler_type", jSONObject.optInt("CKPaperModeSchedulerType"));
                if (jSONObject.optInt("CKPaperModeSchedulerType") == 1) {
                    JobDispatcher.scheduleJob(context, 44009);
                    context.startService(new Intent(context, PaperModeSunTimeService.class));
                }
            }
            if (MiuiUtils.supportPaperEyeCare()) {
                if (jSONObject.has("CKPaperModeType")) {
                    Settings.System.putInt(contentResolver, "screen_mode_type", jSONObject.optInt("CKPaperModeType"));
                }
                if (jSONObject.has("CKPaperTextureLevel")) {
                    Settings.System.putInt(contentResolver, "screen_paper_texture_level", jSONObject.optInt("CKPaperTextureLevel"));
                }
                if (jSONObject.has("CKPaperTextureEyecareLevel")) {
                    Settings.System.putInt(contentResolver, "screen_texture_eyecare_level", jSONObject.optInt("CKPaperTextureEyecareLevel"));
                }
                if (jSONObject.has("CKPaperColorType")) {
                    Settings.System.putInt(contentResolver, "screen_texture_color_type", jSONObject.optInt("CKPaperColorType"));
                }
            }
            if (jSONObject.has("CKClassicTempLevel")) {
                Settings.System.putInt(contentResolver, "screen_paper_mode_level", jSONObject.optInt("CKClassicTempLevel"));
            }
            if (MiuiUtils.supportSmartEyeCare() && jSONObject.has("CKPaperAutoAdjust")) {
                Settings.System.putInt(contentResolver, "screen_auto_adjust", jSONObject.optInt("CKPaperAutoAdjust"));
            }
            if (jSONObject.has("CKPaperModeTimeEnabled")) {
                MiuiSettings.System.putBoolean(contentResolver, "screen_paper_mode_time_enabled", jSONObject.optBoolean("CKPaperModeTimeEnabled"));
            }
            if (jSONObject.has("CKPaperModeTimeStart") && jSONObject.optInt("CKPaperModeTimeStart") >= 0 && jSONObject.optInt("CKPaperModeTimeStart") <= 1440) {
                PaperModeTimeModeUtil.setPaperModeStartTime(context, jSONObject.optInt("CKPaperModeTimeStart"));
            }
            if (jSONObject.has("CKPaperModeTimeEnd") && jSONObject.optInt("CKPaperModeTimeEnd") >= 0 && jSONObject.optInt("CKPaperModeTimeEnd") <= 1440) {
                PaperModeTimeModeUtil.setPaperModeEndTime(context, jSONObject.optInt("CKPaperModeTimeEnd"));
            }
            if (jSONObject.has("CKEyecareCache") && jSONObject.optInt("CKEyecareCache") >= 0) {
                SystemProperties.set("persist.sys.eyecare_cache", String.valueOf(jSONObject.optInt("CKEyecareCache")));
            }
            if (jSONObject.has("CKPaperMode") && (jSONObject.optInt("CKPaperMode") == 1 || jSONObject.optInt("CKPaperMode") == 2)) {
                Settings.System.putInt(contentResolver, "screen_paper_mode", jSONObject.optInt("CKPaperMode"));
            }
            boolean isPaperModeTimeEnable = PaperModeTimeModeUtil.isPaperModeTimeEnable(context);
            int paperModeSchedulerType = PaperModeTimeModeUtil.getPaperModeSchedulerType(context);
            if (!isPaperModeTimeEnable) {
                paperModeSchedulerType = 0;
            }
            PaperModeTimeModeUtil.startPaperModeAutoTime(context, paperModeSchedulerType);
            if (jSONObject.has("CKPaperModeEnabled")) {
                MiuiSettings.System.putBoolean(contentResolver, "screen_paper_mode_enabled", jSONObject.optBoolean("CKPaperModeEnabled"));
            }
        }
        if (jSONObject.has("CKScreenEffectMode") && (jSONObject.optInt("CKScreenEffectMode") == 1 || jSONObject.optInt("CKScreenEffectMode") == 2 || jSONObject.optInt("CKScreenEffectMode") == 3)) {
            Settings.System.putInt(contentResolver, "screen_optimize_mode", jSONObject.optInt("CKScreenEffectMode"));
        }
        if (jSONObject.has("CKScreenEffectLevel") && jSONObject.optInt("CKScreenEffectLevel") > 0) {
            Settings.System.putInt(contentResolver, "screen_color_level", jSONObject.optInt("CKScreenEffectLevel"));
        }
        if (jSONObject.has("CKScreenBrightnessMode") && (jSONObject.optInt("CKScreenBrightnessMode") == 1 || jSONObject.optInt("CKScreenBrightnessMode") == 0)) {
            Settings.System.putInt(contentResolver, "screen_brightness_mode", jSONObject.optInt("CKScreenBrightnessMode"));
        }
        if (FeatureParser.getBoolean("support_screen_effect", false)) {
            if (jSONObject.has("CKDisplayPrefer") && (jSONObject.optInt("CKDisplayPrefer") == 1 || jSONObject.optInt("CKDisplayPrefer") == 2 || jSONObject.optInt("CKDisplayPrefer") == 3)) {
                SystemProperties.set("persist.sys.display_prefer", String.valueOf(jSONObject.optInt("CKDisplayPrefer")));
            }
            if (jSONObject.has("CKLtmEnable")) {
                SystemProperties.set("persist.sys.ltm_enable", String.valueOf(jSONObject.optBoolean("CKLtmEnable")));
            }
            if (jSONObject.has("CKGamutMode") && jSONObject.optInt("CKGamutMode") >= 0) {
                SystemProperties.set("persist.sys.gamut_mode", String.valueOf(jSONObject.optInt("CKGamutMode")));
            }
            if (jSONObject.has("CKDisplayCe")) {
                SystemProperties.set("persist.sys.display_ce", jSONObject.optString("CKDisplayCe"));
            }
        }
        if (jSONObject.has("CKFontSize") && MiuiUtils.isDeviceProvisioned(context)) {
            LargeFontUtils.sendUiModeChangeMessage(context, jSONObject.optInt("CKFontSize"));
        }
        if (jSONObject.has("CKFontWeight") && (optInt = jSONObject.optInt("CKFontWeight")) >= 0 && optInt <= 100) {
            LargeFontUtils.setFontWeight(context, optInt);
            Bundle bundle = new Bundle();
            bundle.putInt("key_var_font_scale", LargeFontUtils.getFontWeight(context));
            MiuiConfiguration.sendThemeConfigurationChangeMsg(536870912L, bundle);
        }
        if (jSONObject.has("CKAccelerometerRotation") && (jSONObject.optInt("CKAccelerometerRotation") == 0 || jSONObject.optInt("CKAccelerometerRotation") == 1 || jSONObject.optInt("CKAccelerometerRotation") == 2)) {
            Settings.System.putInt(contentResolver, "accelerometer_rotation", jSONObject.optInt("CKAccelerometerRotation"));
        }
        restoreDarkMode(context, jSONObject);
    }

    private static void saveDarkMode(Context context, JSONObject jSONObject) throws JSONException {
        jSONObject.put("CKDarkMode", DarkModeTimeModeUtil.isDarkModeEnable(context));
        jSONObject.put("CKDarkModeWallpaperEnable", DarkModeTimeModeUtil.isDarkWallpaperModeEnable(context));
        jSONObject.put("CKDarkModeTimeEnable", DarkModeTimeModeUtil.isDarkModeTimeEnable(context));
        jSONObject.put("CKDarkModeStartTime", DarkModeTimeModeUtil.getDarkModeStartTime(context));
        jSONObject.put("CKDarkModeEndTime", DarkModeTimeModeUtil.getDarkModeEndTime(context));
        jSONObject.put("CKDarkModeContrastEnable", DarkModeTimeModeUtil.isDarkModeContrastEnable(context));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        JSONObject jSONObject = new JSONObject();
        ContentResolver contentResolver = context.getContentResolver();
        try {
            if (MiuiSettings.ScreenEffect.isScreenPaperModeSupported) {
                jSONObject.put("CKPaperModeEnabled", MiuiUtils.isPaperModeEnable(context));
                jSONObject.put("CKPaperModeTimeEnabled", PaperModeTimeModeUtil.isPaperModeTimeEnable(context));
                jSONObject.put("CKPaperModeTimeStart", PaperModeTimeModeUtil.getPaperModeStartTime(context));
                jSONObject.put("CKPaperModeTimeEnd", PaperModeTimeModeUtil.getPaperModeEndTime(context));
                jSONObject.put("CKEyecareCache", SystemProperties.getInt("persist.sys.eyecare_cache", MiuiSettings.ScreenEffect.DEFAULT_PAPER_MODE_LEVEL));
                jSONObject.put("CKPaperMode", Settings.System.getInt(contentResolver, "screen_paper_mode", 1));
                jSONObject.put("CKPaperModeType", Settings.System.getInt(contentResolver, "screen_mode_type", 0));
                jSONObject.put("CKClassicTempLevel", Settings.System.getInt(contentResolver, "screen_paper_mode_level", MiuiSettings.ScreenEffect.DEFAULT_PAPER_MODE_LEVEL));
                if (MiuiUtils.supportPaperEyeCare()) {
                    jSONObject.put("CKPaperTextureLevel", Settings.System.getInt(contentResolver, "screen_paper_texture_level", (int) PaperConstants.DEFAULT_TEXTURE_MODE_LEVEL));
                    jSONObject.put("CKPaperTextureEyecareLevel", Settings.System.getInt(contentResolver, "screen_texture_eyecare_level", PaperConstants.DEFAULT_TEXTURE_EYECARE_LEVEL));
                    jSONObject.put("CKPaperColorType", Settings.System.getInt(contentResolver, "screen_texture_color_type", 0));
                }
                if (MiuiUtils.supportSmartEyeCare()) {
                    jSONObject.put("CKPaperAutoAdjust", Settings.System.getInt(contentResolver, "screen_auto_adjust", 1));
                }
            }
            jSONObject.put("CKScreenEffectMode", Settings.System.getInt(contentResolver, "screen_optimize_mode", MiuiSettings.ScreenEffect.DEFAULT_SCREEN_OPTIMIZE_MODE));
            jSONObject.put("CKScreenEffectLevel", Settings.System.getInt(contentResolver, "screen_color_level", 2));
            jSONObject.put("CKScreenBrightnessMode", Settings.System.getInt(contentResolver, "screen_brightness_mode", 1));
            if (PaperModeTimeModeUtil.isPaperModeTimeEnable(context)) {
                jSONObject.put("CKPaperModeSchedulerType", Settings.System.getInt(contentResolver, "paper_mode_scheduler_type", 2));
            }
            jSONObject.put("CKDisplayPrefer", SystemProperties.get("persist.sys.display_prefer"));
            jSONObject.put("CKLtmEnable", SystemProperties.get("persist.sys.ltm_enable"));
            jSONObject.put("CKGamutMode", SystemProperties.get("persist.sys.gamut_mode"));
            jSONObject.put("CKDisplayCe", SystemProperties.get("persist.sys.display_ce"));
            jSONObject.put("CKFontSize", Resources.getSystem().getConfiguration().uiMode & 15);
            jSONObject.put("CKFontWeight", LargeFontUtils.getFontWeight(context));
            jSONObject.put("CKAccelerometerRotation", Settings.System.getInt(contentResolver, "accelerometer_rotation", 0));
            saveDarkMode(context, jSONObject);
        } catch (JSONException e) {
            Log.e("DisplayCloudBackupHelper", "build json error: ", e);
            CloudBackupException.trackException();
        }
        return jSONObject;
    }
}
