package com.android.settings.stat.commonpreference;

import android.content.Context;

/* loaded from: classes2.dex */
public class PreferenceStatHelper {
    public static void tracePreferenceEvent(Context context) {
        new ScreenOptimizePreference().track(context);
        new FontSettingsPrefStat().track(context);
        new RefreshRatePrefStat().track(context);
    }
}
