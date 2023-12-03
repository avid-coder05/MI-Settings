package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;

/* loaded from: classes.dex */
public class PrivacySettingsPreferenceFactory {
    private static PrivacySettingsEnterprisePreference createPrivacySettingsEnterprisePreference(Context context) {
        return new PrivacySettingsEnterprisePreference(context);
    }

    private static PrivacySettingsFinancedPreference createPrivacySettingsFinancedPreference(Context context) {
        return new PrivacySettingsFinancedPreference(context);
    }

    public static PrivacySettingsPreference createPrivacySettingsPreference(Context context) {
        return isFinancedDevice(context) ? createPrivacySettingsFinancedPreference(context) : createPrivacySettingsEnterprisePreference(context);
    }

    private static boolean isFinancedDevice(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        return devicePolicyManager.isDeviceManaged() && devicePolicyManager.getDeviceOwnerType(devicePolicyManager.getDeviceOwnerComponentOnAnyUser()) == 1;
    }
}
