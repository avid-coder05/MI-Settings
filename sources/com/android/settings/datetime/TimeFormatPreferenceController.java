package com.android.settings.datetime;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes.dex */
public class TimeFormatPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    static void set24Hour(Context context, Boolean bool) {
        Settings.System.putString(context.getContentResolver(), "time_12_24", bool == null ? null : bool.booleanValue() ? "24" : "12");
    }

    static void timeUpdated(Context context, Boolean bool) {
        Intent intent = new Intent("android.intent.action.TIME_SET");
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
        intent.putExtra("android.intent.extra.TIME_PREF_24_HOUR_FORMAT", bool == null ? 2 : bool.booleanValue());
        context.sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update24HourFormat(Context context, Boolean bool) {
        set24Hour(context, bool);
        timeUpdated(context, bool);
    }
}
