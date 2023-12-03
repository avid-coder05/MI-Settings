package com.android.settings.utils;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.util.Log;

/* loaded from: classes2.dex */
public class MiuiSecurityUtils {
    private static DevicePolicyManager getDevicePolicyManager(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (devicePolicyManager == null) {
            Log.e("MiuiSecurityUtils", "Can't get DevicePolicyManagerService: is it running?", new IllegalStateException("Stack trace:"));
        }
        return devicePolicyManager;
    }

    public static int getRequestedMinimumPasswordLength(Context context, int i) {
        return getDevicePolicyManager(context).getPasswordMinimumLength(null, i);
    }

    public static int getRequestedPasswordMinimumLetters(Context context, int i) {
        return getDevicePolicyManager(context).getPasswordMinimumLetters(null, i);
    }

    public static int getRequestedPasswordMinimumLowerCase(Context context, int i) {
        return getDevicePolicyManager(context).getPasswordMinimumLowerCase(null, i);
    }

    public static int getRequestedPasswordMinimumNonLetter(Context context, int i) {
        return getDevicePolicyManager(context).getPasswordMinimumNonLetter(null, i);
    }

    public static int getRequestedPasswordMinimumNumeric(Context context, int i) {
        return getDevicePolicyManager(context).getPasswordMinimumNumeric(null, i);
    }

    public static int getRequestedPasswordMinimumSymbols(Context context, int i) {
        return getDevicePolicyManager(context).getPasswordMinimumSymbols(null, i);
    }

    public static int getRequestedPasswordMinimumUpperCase(Context context, int i) {
        return getDevicePolicyManager(context).getPasswordMinimumUpperCase(null, i);
    }

    public static int getRequestedPasswordQuality(Context context, int i) {
        return getDevicePolicyManager(context).getPasswordQuality(null, i);
    }
}
