package miuix.pickerwidget.date;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.MiuiWindowManager$LayoutParams;
import java.util.TimeZone;
import miuix.core.util.Pools;
import miuix.pickerwidget.R$string;

/* loaded from: classes5.dex */
public class DateUtils {
    private static final Pools.Pool<Calendar> CALENDAR_POOL = Pools.createSoftReferencePool(new Pools.Manager<Calendar>() { // from class: miuix.pickerwidget.date.DateUtils.1
        @Override // miuix.core.util.Pools.Manager
        public Calendar createInstance() {
            return new Calendar();
        }
    }, 1);

    public static String formatDateTime(Context context, long j, int i) {
        StringBuilder acquire = Pools.getStringBuilderPool().acquire();
        String sb = formatDateTime(context, acquire, j, i, null).toString();
        Pools.getStringBuilderPool().release(acquire);
        return sb;
    }

    public static StringBuilder formatDateTime(Context context, StringBuilder sb, long j, int i, TimeZone timeZone) {
        if ((i & 16) == 0 && (i & 32) == 0) {
            i |= DateFormat.is24HourFormat(context) ? 32 : 16;
        }
        String string = context.getString(getFormatResId(i));
        StringBuilder acquire = Pools.getStringBuilderPool().acquire();
        Calendar acquire2 = CALENDAR_POOL.acquire();
        acquire2.setTimeZone(timeZone);
        acquire2.setTimeInMillis(j);
        int length = string.length();
        for (int i2 = 0; i2 < length; i2++) {
            char charAt = string.charAt(i2);
            if (charAt == 'D') {
                acquire.append(context.getString(getDatePatternResId(i)));
            } else if (charAt == 'T') {
                acquire.append(context.getString(getTimePatternResId(acquire2, i)));
            } else if (charAt != 'W') {
                acquire.append(charAt);
            } else {
                acquire.append(context.getString(getWeekdayPatternResId(i)));
            }
        }
        acquire2.format(context, sb, acquire);
        Pools.getStringBuilderPool().release(acquire);
        CALENDAR_POOL.release(acquire2);
        return sb;
    }

    private static int getDatePatternResId(int i) {
        if ((i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON) == 32768) {
            if ((i & 512) == 512) {
                return (i & 256) == 256 ? (i & 128) == 128 ? R$string.fmt_date_numeric_year_month_day : R$string.fmt_date_numeric_year_month : R$string.fmt_date_numeric_year;
            } else if ((i & 256) == 256) {
                return (i & 128) == 128 ? R$string.fmt_date_numeric_month_day : R$string.fmt_date_numeric_month;
            } else if ((i & 128) == 128) {
                return R$string.fmt_date_numeric_day;
            } else {
                throw new IllegalArgumentException("no any time date");
            }
        } else if ((i & 4096) == 4096) {
            if ((i & 512) == 512) {
                return (i & 256) == 256 ? (i & 128) == 128 ? R$string.fmt_date_short_year_month_day : R$string.fmt_date_short_year_month : R$string.fmt_date_year;
            } else if ((i & 256) == 256) {
                return (i & 128) == 128 ? R$string.fmt_date_short_month_day : R$string.fmt_date_short_month;
            } else if ((i & 128) == 128) {
                return R$string.fmt_date_day;
            } else {
                throw new IllegalArgumentException("no any time date");
            }
        } else if ((i & 512) == 512) {
            return (i & 256) == 256 ? (i & 128) == 128 ? R$string.fmt_date_long_year_month_day : R$string.fmt_date_long_year_month : R$string.fmt_date_year;
        } else if ((i & 256) == 256) {
            return (i & 128) == 128 ? R$string.fmt_date_long_month_day : R$string.fmt_date_long_month;
        } else if ((i & 128) == 128) {
            return R$string.fmt_date_day;
        } else {
            throw new IllegalArgumentException("no any time date");
        }
    }

    private static int getFormatResId(int i) {
        return (i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE) == 1024 ? (i & 896) != 0 ? (i & 15) != 0 ? (i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD) == 2048 ? R$string.fmt_weekday_date_time_timezone : R$string.fmt_weekday_date_time : (i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD) == 2048 ? R$string.fmt_weekday_date_timezone : R$string.fmt_weekday_date : (i & 15) != 0 ? (i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD) == 2048 ? R$string.fmt_weekday_time_timezone : R$string.fmt_weekday_time : (i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD) == 2048 ? R$string.fmt_weekday_timezone : R$string.fmt_weekday : (i & 896) != 0 ? (i & 15) != 0 ? (i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD) == 2048 ? R$string.fmt_date_time_timezone : R$string.fmt_date_time : (i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD) == 2048 ? R$string.fmt_date_timezone : R$string.fmt_date : (i & 15) != 0 ? (i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD) == 2048 ? R$string.fmt_time_timezone : R$string.fmt_time : (i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD) == 2048 ? R$string.fmt_timezone : R$string.empty;
    }

    private static int getTimePatternResId(Calendar calendar, int i) {
        if ((i & MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION) == 16384 && (((i & 1) != 1 || calendar.get(22) == 0) && (i & 14) != 0)) {
            i &= -2;
            if (((i & 2) != 2 || calendar.get(21) == 0) && (i & 12) != 0) {
                i &= -3;
                if (calendar.get(20) == 0 && (i & 8) != 0) {
                    i &= -5;
                }
            }
        }
        if ((i & 8) == 8) {
            return (i & 16) == 16 ? (i & 64) == 64 ? (i & 4) == 4 ? (i & 2) == 2 ? (i & 1) == 1 ? R$string.fmt_time_12hour_minute_second_millis : R$string.fmt_time_12hour_minute_second : R$string.fmt_time_12hour_minute : R$string.fmt_time_12hour : (i & 4) == 4 ? (i & 2) == 2 ? (i & 1) == 1 ? R$string.fmt_time_12hour_minute_second_millis_pm : R$string.fmt_time_12hour_minute_second_pm : R$string.fmt_time_12hour_minute_pm : R$string.fmt_time_12hour_pm : (i & 4) == 4 ? (i & 2) == 2 ? (i & 1) == 1 ? R$string.fmt_time_24hour_minute_second_millis : R$string.fmt_time_24hour_minute_second : R$string.fmt_time_24hour_minute : R$string.fmt_time_24hour;
        } else if ((i & 4) == 4) {
            return (i & 2) == 2 ? (i & 1) == 1 ? R$string.fmt_time_minute_second_millis : R$string.fmt_time_minute_second : R$string.fmt_time_minute;
        } else if ((i & 2) == 2) {
            return (i & 1) == 1 ? R$string.fmt_time_second_millis : R$string.fmt_time_second;
        } else if ((i & 1) == 1) {
            return R$string.fmt_time_millis;
        } else {
            throw new IllegalArgumentException("no any time date");
        }
    }

    private static int getWeekdayPatternResId(int i) {
        return (i & 8192) == 8192 ? R$string.fmt_weekday_short : R$string.fmt_weekday_long;
    }
}
