package com.android.settings.display;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.SubSettings;
import com.android.settings.network.telephony.ToggleSubscriptionDialogActivity;
import com.android.settings.search.provider.SettingsProvider;
import miui.provider.ExtraContacts;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class ScreenEnhanceEngineStatusCheck extends SubSettings {
    private static final boolean IS_DEVICE_SCREEN_ENHANCE_SUPPORT = FeatureParser.getBoolean("support_screen_enhance_engine", false);

    private static Bundle callPreference(Context context, String str, Bundle bundle) {
        try {
            return context.getContentResolver().call(Uri.parse("content://com.miui.securitycenter.remoteprovider"), "callPreference", str, bundle);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean getAiStatus(Context context) {
        int i = Settings.System.getInt(context.getContentResolver(), "screen_optimize_mode", -1);
        Log.e("StatusCheck", "screen effect mode is " + i);
        if (i == 3 || i == 4) {
            Settings.Global.putString(context.getContentResolver(), "screen_enhance_engine_gallery_ai_mode_status", "false");
            return false;
        }
        String string = Settings.Global.getString(context.getContentResolver(), "screen_enhance_engine_gallery_ai_mode_status");
        Log.e("StatusCheck", "get AI status value is " + string);
        return "true".equals(string);
    }

    public static boolean getMemcStatus(Context context) {
        boolean preferenceBoolean = getPreferenceBoolean(context, "pref_videobox_frc_status", false);
        Log.e("StatusCheck", "get pref_videobox_frc_status value is " + preferenceBoolean);
        return preferenceBoolean;
    }

    private static boolean getPreferenceBoolean(Context context, String str, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString(SettingsProvider.ARGS_KEY, str);
        bundle.putBoolean(ExtraContacts.DefaultAccount.NAME, z);
        Bundle callPreference = callPreference(context, "GET", bundle);
        return callPreference == null ? z : callPreference.getBoolean(str, z);
    }

    private static int getPreferenceInt(Context context, String str, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putString(SettingsProvider.ARGS_KEY, str);
        bundle.putInt(ExtraContacts.DefaultAccount.NAME, i);
        Bundle callPreference = callPreference(context, "GET", bundle);
        return callPreference == null ? i : callPreference.getInt(str, i);
    }

    public static boolean getS2hStatus(Context context) {
        int preferenceInt = getPreferenceInt(context, "pref_video_box_dispaly_style", -1);
        Log.e("StatusCheck", "get pref_video_box_dispaly_style value is " + preferenceInt);
        return preferenceInt == 2;
    }

    public static boolean getSrForImageStatus(Context context) {
        String string = Settings.Global.getString(context.getContentResolver(), "screen_enhance_engine_sr_for_image_status");
        Log.e("StatusCheck", "get SR for image status is " + string);
        return "true".equals(string);
    }

    public static boolean getSrForVideoStatus(Context context) {
        boolean preferenceBoolean = getPreferenceBoolean(context, "pref_video_division", false);
        StringBuilder sb = new StringBuilder();
        sb.append("SR for video is ");
        sb.append(preferenceBoolean ? ToggleSubscriptionDialogActivity.ARG_enable : "disable");
        Log.e("StatusCheck", sb.toString());
        return preferenceBoolean;
    }

    public static boolean isAiSupport(Context context) {
        boolean z;
        boolean z2;
        Bundle bundle;
        if (!IS_DEVICE_SCREEN_ENHANCE_SUPPORT) {
            Log.e("StatusCheck", "AI display not support, Device not support screen_enhance");
            return false;
        }
        boolean z3 = FeatureParser.getBoolean("support_AI_display", false);
        StringBuilder sb = new StringBuilder();
        sb.append("AI device is ");
        sb.append(z3 ? "support" : "not support");
        Log.e("StatusCheck", sb.toString());
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.miui.gallery", 128);
            String str = null;
            if (applicationInfo != null && (bundle = applicationInfo.metaData) != null) {
                str = bundle.getString("com.miui.gallery.SCREEN_AI_MODE");
                Log.e("StatusCheck", "isGallerySupportString is " + str);
            }
            z2 = "support".equals(str);
        } catch (PackageManager.NameNotFoundException e) {
            e = e;
            z = false;
        }
        try {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("AI gallery is ");
            sb2.append(z2 ? "support" : "not support");
            Log.e("StatusCheck", sb2.toString());
        } catch (PackageManager.NameNotFoundException e2) {
            z = z2;
            e = e2;
            e.printStackTrace();
            z2 = z;
            return !z3 ? false : false;
        }
        if (!z3 && z2) {
            return true;
        }
    }

    public static boolean isMemcSupport() {
        if (!IS_DEVICE_SCREEN_ENHANCE_SUPPORT) {
            Log.e("StatusCheck", "MEMC not support, Device not support screen_enhance");
            return false;
        }
        boolean z = "true".equals(SystemProperties.get("ro.vendor.media.video.frc.support", "false")) || "true".equals(SystemProperties.get("debug.config.media.video.frc.support", "false"));
        StringBuilder sb = new StringBuilder();
        sb.append("MEMC is ");
        sb.append(z ? "support" : "not support");
        Log.e("StatusCheck", sb.toString());
        return z;
    }

    public static boolean isS2hSupport() {
        if (!IS_DEVICE_SCREEN_ENHANCE_SUPPORT) {
            Log.e("StatusCheck", "SDR to HDR not support, Device not support screen_enhance");
            return false;
        }
        boolean equals = "2".equals(SystemProperties.get("ro.vendor.video_box.version", "0"));
        StringBuilder sb = new StringBuilder();
        sb.append("SDR to HDR is ");
        sb.append(equals ? "support" : "not support");
        Log.e("StatusCheck", sb.toString());
        return equals;
    }

    public static boolean isSrForImageSupport() {
        if (!IS_DEVICE_SCREEN_ENHANCE_SUPPORT) {
            Log.e("StatusCheck", "SR for image not support, Device not support screen_enhance");
            return false;
        }
        boolean z = FeatureParser.getBoolean("support_SR_for_image_display", false);
        StringBuilder sb = new StringBuilder();
        sb.append("SR for image is ");
        sb.append(z ? "support" : "not support");
        Log.e("StatusCheck", sb.toString());
        Log.e("StatusCheck", "SR for image support manual close!");
        return false;
    }

    public static boolean isSrForVideoSupport() {
        if (!IS_DEVICE_SCREEN_ENHANCE_SUPPORT) {
            Log.e("StatusCheck", "SR for video not support, Device not support screen_enhance");
            return false;
        }
        boolean equals = "true".equals(SystemProperties.get("debug.config.media.video.ais.support", "false"));
        StringBuilder sb = new StringBuilder();
        sb.append("SR for video is ");
        sb.append(equals ? "support" : "not support");
        Log.e("StatusCheck", sb.toString());
        return equals;
    }

    public static boolean setAiStatus(Context context, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("set AI ");
        sb.append(z ? ToggleSubscriptionDialogActivity.ARG_enable : "disable");
        Log.e("StatusCheck", sb.toString());
        String str = z ? "true" : "false";
        if (str.equals(Settings.Global.getString(context.getContentResolver(), "screen_enhance_engine_gallery_ai_mode_status"))) {
            return true;
        }
        Settings.Global.putString(context.getContentResolver(), "screen_enhance_engine_gallery_ai_mode_status", str);
        return str.equals(Settings.Global.getString(context.getContentResolver(), "screen_enhance_engine_gallery_ai_mode_status"));
    }

    public static boolean setMemcStatus(Context context, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("set MEMC ");
        sb.append(z ? ToggleSubscriptionDialogActivity.ARG_enable : "disable");
        Log.e("StatusCheck", sb.toString());
        if (z && isSrForVideoSupport() && getSrForVideoStatus(context) && !setSrForVideoStatus(context, false)) {
            Log.e("StatusCheck", "set SR for video disable!");
            return false;
        } else if (z == getPreferenceBoolean(context, "pref_videobox_frc_status", false)) {
            return true;
        } else {
            setPreferenceBoolean(context, "pref_videobox_frc_status", z);
            return z == getPreferenceBoolean(context, "pref_videobox_frc_status", false);
        }
    }

    private static void setPreferenceBoolean(Context context, String str, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString(SettingsProvider.ARGS_KEY, str);
        bundle.putBoolean("value", z);
        callPreference(context, "SET", bundle);
        context.getContentResolver().notifyChange(Uri.withAppendedPath(Uri.parse("content://com.miui.securitycenter.remoteprovider"), str), (ContentObserver) null, false);
    }

    private static void setPreferenceInt(Context context, String str, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putString(SettingsProvider.ARGS_KEY, str);
        bundle.putInt("value", i);
        callPreference(context, "SET", bundle);
    }

    public static boolean setS2hStatus(Context context, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("set S2H ");
        sb.append(z ? ToggleSubscriptionDialogActivity.ARG_enable : "disable");
        Log.e("StatusCheck", sb.toString());
        int i = z ? 2 : 0;
        if (i == getPreferenceInt(context, "pref_video_box_dispaly_style", -1)) {
            return true;
        }
        setPreferenceInt(context, "pref_video_box_dispaly_style", i);
        return i == getPreferenceInt(context, "pref_video_box_dispaly_style", -1);
    }

    public static boolean setSrForImageStatus(Context context, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("set SR for image ");
        sb.append(z ? ToggleSubscriptionDialogActivity.ARG_enable : "disable");
        Log.e("StatusCheck", sb.toString());
        String str = z ? "true" : "false";
        if (str.equals(Settings.Global.getString(context.getContentResolver(), "screen_enhance_engine_sr_for_image_status"))) {
            return true;
        }
        Settings.Global.putString(context.getContentResolver(), "screen_enhance_engine_sr_for_image_status", str);
        return str.equals(Settings.Global.getString(context.getContentResolver(), "screen_enhance_engine_sr_for_image_status"));
    }

    public static boolean setSrForVideoStatus(Context context, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("set SR for video ");
        sb.append(z ? ToggleSubscriptionDialogActivity.ARG_enable : "disable");
        Log.e("StatusCheck", sb.toString());
        if (z && isMemcSupport() && getMemcStatus(context) && !setMemcStatus(context, false)) {
            Log.e("StatusCheck", "set MEMC disable!");
            return false;
        } else if (z == getPreferenceBoolean(context, "pref_video_division", false)) {
            return true;
        } else {
            setPreferenceBoolean(context, "pref_video_division", z);
            return z == getPreferenceBoolean(context, "pref_video_division", false);
        }
    }
}
