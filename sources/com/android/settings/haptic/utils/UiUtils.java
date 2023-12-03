package com.android.settings.haptic.utils;

import android.os.SystemProperties;

/* loaded from: classes.dex */
public final class UiUtils {
    public static boolean isSupportLinearMotorVibrate() {
        return "linear".equals(SystemProperties.get("sys.haptic.motor"));
    }
}
