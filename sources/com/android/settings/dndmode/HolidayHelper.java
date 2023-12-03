package com.android.settings.dndmode;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import java.util.Calendar;
import miui.provider.ExtraCalendarContracts;

/* loaded from: classes.dex */
public class HolidayHelper {
    public static boolean isHolidayDataInvalid(Context context) {
        Cursor query = context.getContentResolver().query(ContentUris.withAppendedId(ExtraCalendarContracts.HolidayContracts.HOLIDAY_CONTENT_URI, Calendar.getInstance().getTimeInMillis()), null, null, null, null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    if (query.getInt(2) == 3) {
                        return true;
                    }
                }
                return false;
            } finally {
                query.close();
            }
        }
        return false;
    }

    public static boolean isWeekEnd(Calendar calendar) {
        return calendar.get(7) == 7 || calendar.get(7) == 1;
    }
}
