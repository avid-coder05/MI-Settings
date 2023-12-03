package com.android.settings.compat;

import android.content.Context;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;

/* loaded from: classes.dex */
public class RestrictedLockUtilsCompat {
    public static RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled(Context context, int i, int i2) {
        return RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(context, i, i2);
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfMaximumTimeToLockIsSet(Context context) {
        return RestrictedLockUtilsInternal.checkIfMaximumTimeToLockIsSet(context);
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfPasswordQualityIsSet(Context context, int i) {
        return RestrictedLockUtilsInternal.checkIfPasswordQualityIsSet(context, i);
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced(Context context, String str, int i) {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, str, i);
    }

    public static RestrictedLockUtils.EnforcedAdmin getDeviceOwner(Context context) {
        return RestrictedLockUtilsInternal.getDeviceOwner(context);
    }
}
