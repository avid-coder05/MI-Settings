package com.android.settings;

import android.content.Context;
import android.net.Uri;
import android.provider.MiuiSettings;

/* loaded from: classes.dex */
public class MiuiOptionUtils$DoNotDisturb {
    public static int touchDoNotDisturbState(Context context, int i) {
        int i2 = MiuiSettings.SilenceMode.getZenMode(context) == 1 ? 1 : 0;
        if (i == -1 || i == i2) {
            return i2;
        }
        MiuiSettings.SilenceMode.setSilenceMode(context, i != 0 ? 1 : 0, (Uri) null);
        return i;
    }
}
