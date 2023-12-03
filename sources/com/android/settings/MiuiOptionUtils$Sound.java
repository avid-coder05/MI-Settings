package com.android.settings;

import android.content.Context;
import android.net.Uri;
import android.provider.MiuiSettings;
import miui.util.AudioManagerHelper;

/* loaded from: classes.dex */
public class MiuiOptionUtils$Sound {
    public static int touchSilentState(Context context, int i) {
        int i2 = MiuiSettings.SilenceMode.getZenMode(context) == 4 ? 1 : 0;
        if (i == -1 || i == i2) {
            return i2;
        }
        MiuiSettings.SilenceMode.setSilenceMode(context, i != 0 ? 4 : 0, (Uri) null);
        return i;
    }

    public static int touchVibrateState(Context context, int i) {
        boolean isVibrateEnabled = AudioManagerHelper.isVibrateEnabled(context);
        if (i == -1 || i == isVibrateEnabled) {
            return isVibrateEnabled ? 1 : 0;
        }
        AudioManagerHelper.toggleVibrateSetting(context);
        return i;
    }
}
