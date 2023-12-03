package com.android.settings.cloudbackup;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import com.android.settings.MiuiUtils;
import miui.hardware.display.DisplayFeatureManager;
import miui.os.Build;
import miui.os.DeviceFeature;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class AccessibilityCloudBackupHelper {
    public static boolean isServiceInstalled(Context context, String str) {
        AccessibilityManager accessibilityManager;
        AccessibilityServiceInfo installedServiceInfoWithComponentName;
        if (TextUtils.isEmpty(str) || (installedServiceInfoWithComponentName = (accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility")).getInstalledServiceInfoWithComponentName(ComponentName.unflattenFromString(str))) == null) {
            return false;
        }
        return accessibilityManager.getInstalledAccessibilityServiceList().contains(installedServiceInfoWithComponentName);
    }

    private static void notifyScreenEffectConflict(int i, int i2, boolean z) {
        if (z && i2 == 0) {
            return;
        }
        if ((z || i2 != 1) && DeviceFeature.SCREEN_EFFECT_CONFLICT && i != i2) {
            DisplayFeatureManager.getInstance().setScreenEffect(15, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (jSONObject.has("CKCaptioningEnabled")) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_enabled", jSONObject.optInt("CKCaptioningEnabled"));
        }
        if (jSONObject.has("CKCaptioningLocale")) {
            Settings.Secure.putString(contentResolver, "accessibility_captioning_locale", jSONObject.optString("CKCaptioningLocale"));
        }
        if (jSONObject.has("CKCaptioningFontScale")) {
            Settings.Secure.putString(contentResolver, "accessibility_captioning_font_scale", jSONObject.optString("CKCaptioningFontScale"));
        }
        if (jSONObject.has("CKCaptioningPreset")) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_preset", jSONObject.optInt("CKCaptioningPreset"));
        }
        if (jSONObject.has("CKCaptioningTypeface")) {
            Settings.Secure.putString(contentResolver, "accessibility_captioning_typeface", jSONObject.optString("CKCaptioningTypeface"));
        }
        if (jSONObject.has("CKCaptioningForegroundColor")) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_foreground_color", jSONObject.optInt("CKCaptioningForegroundColor"));
        }
        if (jSONObject.has("CKCaptioningEdgeType")) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_edge_type", jSONObject.optInt("CKCaptioningEdgeType"));
        }
        if (jSONObject.has("CKCaptioningEdgeColor")) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_edge_color", jSONObject.optInt("CKCaptioningEdgeColor"));
        }
        if (jSONObject.has("CKCaptioningBackgroundColor")) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_background_color", jSONObject.optInt("CKCaptioningBackgroundColor"));
        }
        if (jSONObject.has("CKCaptioningWindowColor")) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_window_color", jSONObject.optInt("CKCaptioningWindowColor"));
        }
        if (jSONObject.has("CKDisplayMagnificationEnabled")) {
            Settings.Secure.putInt(contentResolver, "accessibility_display_magnification_enabled", jSONObject.optInt("CKDisplayMagnificationEnabled"));
        }
        if (jSONObject.has("CKIncallPowerButtonBehavior")) {
            Settings.Secure.putInt(contentResolver, "incall_power_button_behavior", jSONObject.optInt("CKIncallPowerButtonBehavior"));
        }
        if (jSONObject.has("CKSpeakPassword")) {
            Settings.Secure.putInt(contentResolver, "speak_password", jSONObject.optInt("CKSpeakPassword"));
        }
        if (jSONObject.has("CKTtsDefaultSynth")) {
            Settings.Secure.putString(contentResolver, "tts_default_synth", jSONObject.optString("CKTtsDefaultSynth"));
        }
        if (jSONObject.has("CKTtsDefaultRate")) {
            Settings.Secure.putInt(contentResolver, "tts_default_rate", jSONObject.optInt("CKTtsDefaultRate"));
        }
        if (jSONObject.has("CKLongPressTimeout")) {
            Settings.Secure.putInt(contentResolver, "long_press_timeout", jSONObject.optInt("CKLongPressTimeout"));
        }
        if (jSONObject.has("CKDisplayInversionEnabled")) {
            int i = Settings.Secure.getInt(contentResolver, "accessibility_display_inversion_enabled", 0);
            int optInt = jSONObject.optInt("CKDisplayInversionEnabled");
            notifyScreenEffectConflict(i, optInt, true);
            Settings.Secure.putInt(contentResolver, "accessibility_display_inversion_enabled", optInt);
            notifyScreenEffectConflict(i, optInt, false);
        }
        if (jSONObject.has("CKDisplayDaltonizerEnabled")) {
            int i2 = Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer_enabled", 0);
            int optInt2 = jSONObject.optInt("CKDisplayDaltonizerEnabled");
            notifyScreenEffectConflict(i2, optInt2, true);
            Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", optInt2);
            notifyScreenEffectConflict(i2, optInt2, false);
        }
        if (jSONObject.has("CKDisplayDaltonizer")) {
            Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer", jSONObject.optInt("CKDisplayDaltonizer"));
        }
        if (jSONObject.has("CKLargePointerIcon")) {
            Settings.Secure.putInt(contentResolver, "accessibility_large_pointer_icon", jSONObject.optInt("CKLargePointerIcon"));
        }
        if (jSONObject.has("CKWindowAnimationScale") && MiuiUtils.isHuanjiInProgress(context)) {
            Settings.Global.putString(contentResolver, "window_animation_scale", jSONObject.optString("CKWindowAnimationScale"));
        }
        if (jSONObject.has("CKAnimatorDurationScale") && MiuiUtils.isHuanjiInProgress(context)) {
            Settings.Global.putString(contentResolver, "animator_duration_scale", jSONObject.optString("CKAnimatorDurationScale"));
        }
        if (jSONObject.has("CKTransitionAnimationScale") && MiuiUtils.isHuanjiInProgress(context)) {
            Settings.Global.putString(contentResolver, "transition_animation_scale", jSONObject.optString("CKTransitionAnimationScale"));
        }
        if (jSONObject.has("CKMasterMono")) {
            Settings.System.putIntForUser(contentResolver, "master_mono", jSONObject.optInt("CKMasterMono"), -2);
        }
        if (jSONObject.has("CKHighTextContrast") && MiuiUtils.isHuanjiInProgress(context)) {
            Settings.Secure.putInt(contentResolver, "high_text_contrast_enabled", jSONObject.optInt("CKHighTextContrast"));
        }
        if (Build.IS_TABLET || !jSONObject.has("CKAutoSpeaker")) {
            return;
        }
        MiuiSettings.Secure.putBoolean(contentResolver, "enable_auto_speaker", jSONObject.optBoolean("CKAutoSpeaker"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        JSONObject jSONObject = new JSONObject();
        ContentResolver contentResolver = context.getContentResolver();
        try {
            jSONObject.put("CKCaptioningEnabled", Settings.Secure.getString(contentResolver, "accessibility_captioning_enabled"));
            jSONObject.put("CKCaptioningLocale", Settings.Secure.getString(contentResolver, "accessibility_captioning_locale"));
            jSONObject.put("CKCaptioningFontScale", Settings.Secure.getString(contentResolver, "accessibility_captioning_font_scale"));
            jSONObject.put("CKCaptioningPreset", Settings.Secure.getString(contentResolver, "accessibility_captioning_preset"));
            jSONObject.put("CKCaptioningTypeface", Settings.Secure.getString(contentResolver, "accessibility_captioning_typeface"));
            jSONObject.put("CKCaptioningForegroundColor", Settings.Secure.getString(contentResolver, "accessibility_captioning_foreground_color"));
            jSONObject.put("CKCaptioningEdgeType", Settings.Secure.getString(contentResolver, "accessibility_captioning_edge_type"));
            jSONObject.put("CKCaptioningEdgeColor", Settings.Secure.getString(contentResolver, "accessibility_captioning_edge_color"));
            jSONObject.put("CKCaptioningBackgroundColor", Settings.Secure.getString(contentResolver, "accessibility_captioning_background_color"));
            jSONObject.put("CKCaptioningWindowColor", Settings.Secure.getString(contentResolver, "accessibility_captioning_window_color"));
            jSONObject.put("CKDisplayMagnificationEnabled", Settings.Secure.getString(contentResolver, "accessibility_display_magnification_enabled"));
            jSONObject.put("CKIncallPowerButtonBehavior", Settings.Secure.getString(contentResolver, "incall_power_button_behavior"));
            jSONObject.put("CKSpeakPassword", Settings.Secure.getString(contentResolver, "speak_password"));
            jSONObject.put("CKTtsDefaultSynth", Settings.Secure.getString(contentResolver, "tts_default_synth"));
            jSONObject.put("CKTtsDefaultRate", Settings.Secure.getString(contentResolver, "tts_default_rate"));
            jSONObject.put("CKLongPressTimeout", Settings.Secure.getString(contentResolver, "long_press_timeout"));
            jSONObject.put("CKDisplayInversionEnabled", Settings.Secure.getString(contentResolver, "accessibility_display_inversion_enabled"));
            jSONObject.put("CKDisplayDaltonizerEnabled", Settings.Secure.getString(contentResolver, "accessibility_display_daltonizer_enabled"));
            jSONObject.put("CKDisplayDaltonizer", Settings.Secure.getString(contentResolver, "accessibility_display_daltonizer"));
            jSONObject.put("CKLargePointerIcon", Settings.Secure.getInt(contentResolver, "accessibility_large_pointer_icon", 0));
            jSONObject.put("CKWindowAnimationScale", Settings.Global.getString(contentResolver, "window_animation_scale"));
            jSONObject.put("CKAnimatorDurationScale", Settings.Global.getString(contentResolver, "animator_duration_scale"));
            jSONObject.put("CKTransitionAnimationScale", Settings.Global.getString(contentResolver, "transition_animation_scale"));
            jSONObject.put("CKMasterMono", Settings.System.getIntForUser(contentResolver, "master_mono", 0, -2));
            jSONObject.put("CKHighTextContrast", Settings.Secure.getInt(contentResolver, "high_text_contrast_enabled", 0));
            if (!Build.IS_TABLET) {
                jSONObject.put("CKAutoSpeaker", MiuiSettings.Secure.getBoolean(contentResolver, "enable_auto_speaker", true));
            }
        } catch (JSONException unused) {
            Log.e("AccessibilityCloudBackupHelper", "Build JSON failed. ");
            CloudBackupException.trackException();
        }
        return jSONObject;
    }
}
