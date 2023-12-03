package com.android.settings.compat;

import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;

/* loaded from: classes.dex */
public class AmbientDisplayConfigurationCompat {
    public static boolean isAvailable(Context context) {
        return new AmbientDisplayConfiguration(context).alwaysOnAvailable();
    }
}
