package com.android.settings.emergency.util;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Settings;
import com.android.settings.emergency.service.LocationService;
import miui.os.Build;
import miui.process.ProcessManager;

/* loaded from: classes.dex */
public class Config {
    public static String getSosEmergencyContactNames(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "key_miui_sos_emergency_contacts_names");
        return string == null ? "" : string;
    }

    public static String getSosEmergencyContacts(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "key_miui_sos_emergency_contacts");
        return string == null ? "" : string;
    }

    public static boolean isInSosMode(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_is_in_miui_sos_mode", 0) == 1;
    }

    public static boolean isLockedApplication(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_is_locked_application", ProcessManager.isLockedApplication(context.getPackageName(), 0) ? 1 : 0) == 1;
    }

    public static boolean isPaEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "com_miui_warningcenter_pa_status", 0) == 1;
    }

    public static boolean isPaSupport(Context context) {
        return context.getPackageManager().queryIntentActivities(new Intent("miui.intent.action.WARNINGCENTER_POLICE_ASSIST"), 0).size() > 0;
    }

    public static boolean isSosCallLogConfirmed(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_sos_calllog_confirm", 0) == 1;
    }

    public static boolean isSosCallLogEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_miui_sos_call_log_enable", 0) == 1;
    }

    public static boolean isSosCallingConfirmed(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_sos_calling_confirm", 0) == 1;
    }

    public static boolean isSosCallingEnable(Context context) {
        return !Build.IS_INTERNATIONAL_BUILD && SystemProperties.getBoolean("ro.vendor.audio.sos", false) && Settings.Secure.getInt(context.getContentResolver(), "key_miui_sos_calling_enable", 0) == 1;
    }

    public static boolean isSosEmergencyAroundPhoto(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_miui_sos_emergency_around_photo", 0) == 1;
    }

    public static boolean isSosEmergencyAroundVoice(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_miui_sos_emergency_around_voice", 0) == 1;
    }

    public static boolean isSosEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_miui_sos_enable", 0) == 1;
    }

    public static boolean isSosPrivacyConfirmed(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_sos_privacy_confirm", 0) == 1;
    }

    public static void setApplicationLockedState(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_is_locked_application", z ? 1 : 0);
    }

    public static void setInSosModeState(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_is_in_miui_sos_mode", z ? 1 : 0);
    }

    public static void setSosCallLogConfirmed(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_sos_calllog_confirm", z ? 1 : 0);
    }

    public static void setSosCallLogEnable(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_miui_sos_call_log_enable", z ? 1 : 0);
    }

    public static void setSosCallingConfirmed(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_sos_calling_confirm", z ? 1 : 0);
    }

    public static void setSosCallingEnable(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_miui_sos_calling_enable", z ? 1 : 0);
    }

    public static void setSosEmergencyAroundPhoto(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_miui_sos_emergency_around_photo", z ? 1 : 0);
    }

    public static void setSosEmergencyAroundVoice(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_miui_sos_emergency_around_voice", z ? 1 : 0);
    }

    public static void setSosEmergencyContactNames(Context context, String str) {
        Settings.Secure.putString(context.getContentResolver(), "key_miui_sos_emergency_contacts_names", str);
    }

    public static void setSosEmergencyContacts(Context context, String str) {
        Settings.Secure.putString(context.getContentResolver(), "key_miui_sos_emergency_contacts", str);
    }

    public static void setSosEnable(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_miui_sos_enable", z ? 1 : 0);
        if (z || !isInSosMode(context)) {
            return;
        }
        context.stopService(new Intent(context, LocationService.class));
    }

    public static void setSosPrivacyConfirmed(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "key_sos_privacy_confirm", z ? 1 : 0);
    }
}
