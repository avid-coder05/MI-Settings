package com.android.settings;

import android.content.Context;
import android.provider.Settings;

/* loaded from: classes.dex */
public class MiuiAirplaneModeEnabler extends BaseEnabler {
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }
}
