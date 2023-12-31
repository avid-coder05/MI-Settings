package com.android.settingslib;

import android.content.Context;
import android.provider.Settings;

/* loaded from: classes2.dex */
public class WirelessUtils {
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public static boolean isRadioAllowed(Context context, String str) {
        if (isAirplaneModeOn(context)) {
            String string = Settings.Global.getString(context.getContentResolver(), "airplane_mode_toggleable_radios");
            return string != null && string.contains(str);
        }
        return true;
    }
}
