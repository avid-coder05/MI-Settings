package com.android.settings.compat;

import android.app.admin.DevicePolicyManager;
import android.app.admin.PasswordMetrics;

/* loaded from: classes.dex */
public class QualityCompat {
    public static int upgradeQuality(int i, DevicePolicyManager devicePolicyManager, int i2, int i3) {
        return Math.max(Math.max(i, devicePolicyManager.getPasswordQuality(null, i2)), PasswordMetrics.complexityLevelToMinQuality(i3));
    }
}
