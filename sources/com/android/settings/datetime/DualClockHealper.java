package com.android.settings.datetime;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.icu.text.TimeZoneNames;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.AodStylePreferenceController;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class DualClockHealper {
    private static boolean mDualClockMode;

    public static void dualTimeZoneInit(Context context) {
        int i = Settings.System.getInt(context.getContentResolver(), AodStylePreferenceController.AUTO_DUAL_CLOCK, -1);
        String id = TimeZone.getDefault().getID();
        String string = Settings.System.getString(context.getContentResolver(), AodStylePreferenceController.RESIDENT_TIMEZONE);
        if (i == -1) {
            if (TextUtils.isEmpty(string) || id.equals(string)) {
                Log.i("DualClockHealper", "no used, init dualTimeZone");
                Settings.System.putInt(context.getContentResolver(), AodStylePreferenceController.AUTO_DUAL_CLOCK, 1);
                if (TextUtils.isEmpty(string)) {
                    Settings.System.putString(context.getContentResolver(), AodStylePreferenceController.RESIDENT_TIMEZONE, id);
                }
            }
        }
    }

    public static Cursor getDualTimeZoneCursor(Context context) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"zone_id", "zone_displayname"});
        matrixCursor.addRow(new Object[]{getDualTimeZoneID(context), getDualZoneDisplayName(context)});
        return matrixCursor;
    }

    public static String getDualTimeZoneID(Context context) {
        String string = Settings.System.getString(context.getContentResolver(), AodStylePreferenceController.RESIDENT_TIMEZONE);
        if (TextUtils.isEmpty(string)) {
            Log.i("DualClockHealper", "init dual timeZone");
            Settings.System.putString(context.getContentResolver(), AodStylePreferenceController.RESIDENT_TIMEZONE, TimeZone.getDefault().getID());
            return TimeZone.getDefault().getID();
        }
        return string;
    }

    public static String getDualZoneDisplayName(Context context) {
        return getNamebyZone(getDualTimeZoneID(context));
    }

    public static String getNamebyZone(String str) {
        return (str == null || !str.equals("Asia/Shanghai")) ? TimeZoneNames.getInstance(Locale.getDefault()).getExemplarLocationName(str) : TimeZoneNames.getInstance(Locale.getDefault()).getDisplayName("Asia/Shanghai", TimeZoneNames.NameType.LONG_STANDARD, new Date().getTime());
    }

    public static Cursor getZoneInfoCursor(Context context) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"zone_id", "zone_displayname", "auto_time_zone"});
        String id = TimeZone.getDefault().getID();
        matrixCursor.addRow(new Object[]{id, getNamebyZone(id), Boolean.valueOf(Settings.Global.getInt(context.getContentResolver(), "auto_time_zone", 0) > 0)});
        return matrixCursor;
    }

    public static void initDualClockMode(Bundle bundle) {
        if (bundle == null) {
            mDualClockMode = false;
            return;
        }
        mDualClockMode = bundle.getBoolean("miui_launch", false);
        Log.i("DualClockHealper", "onCreate: mDualClockMode = " + mDualClockMode);
    }

    public static boolean isDualClockMode() {
        return mDualClockMode;
    }

    public static void saveTimeZone(Context context, String str, String str2) {
        Settings.System.putString(context.getContentResolver(), "resident_id", str);
        Settings.System.putString(context.getContentResolver(), AodStylePreferenceController.RESIDENT_TIMEZONE, str2);
    }
}
