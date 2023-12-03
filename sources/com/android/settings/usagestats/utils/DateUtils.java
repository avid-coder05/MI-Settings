package com.android.settings.usagestats.utils;

import com.android.settings.usagestats.model.DayInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* loaded from: classes2.dex */
public class DateUtils {
    public static int COUNT_DAYS_OF_WEEK = 7;
    public static int COUNT_HOURS_OF_DAY = 24;
    public static long INTERVAL_DAY = 0;
    public static long INTERVAL_HOUR = 0;
    public static long INTERVAL_MINUTE = 60000;
    public static long INTERVAL_WEEK = 0;
    public static int NUMBER_OF_DAYS_MONTH = 30;
    private static String TAG = "LR-DateUtils";

    static {
        long j = 60000 * 60;
        INTERVAL_HOUR = j;
        long j2 = 24 * j;
        INTERVAL_DAY = j2;
        INTERVAL_WEEK = 7 * j2;
    }

    public static List<DayInfo> daysOfMonth() {
        Calendar calendar = Calendar.getInstance();
        long j = today();
        ArrayList arrayList = new ArrayList(NUMBER_OF_DAYS_MONTH);
        int i = 0;
        while (true) {
            if (i >= NUMBER_OF_DAYS_MONTH) {
                CommonUtils.log(TAG, "Today is " + long2Date(j));
                return arrayList;
            }
            arrayList.add(new DayInfo(calendar, j - (INTERVAL_DAY * ((long) ((r5 - i) - 1)))));
            i++;
        }
    }

    public static List<DayInfo> daysOfWeek(boolean z) {
        Calendar calendar = Calendar.getInstance();
        int i = COUNT_DAYS_OF_WEEK;
        if (!z) {
            i = (calendar.get(7) - calendar.getFirstDayOfWeek()) + 1;
        }
        long j = today();
        ArrayList arrayList = new ArrayList(i);
        for (int i2 = 0; i2 < i; i2++) {
            arrayList.add(new DayInfo(calendar, j - (INTERVAL_DAY * ((long) ((i - i2) - 1)))));
        }
        return arrayList;
    }

    public static boolean isInMidNight() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = today();
        return currentTimeMillis > j && currentTimeMillis - j <= INTERVAL_MINUTE * 2;
    }

    public static boolean isInSameDay(long j, long j2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        int i = calendar.get(6);
        int i2 = calendar.get(5);
        int i3 = calendar.get(7);
        calendar.setTimeInMillis(j2);
        return i == calendar.get(6) && i2 == calendar.get(5) && i3 == calendar.get(7);
    }

    public static boolean isWeekdayToday() {
        int i = Calendar.getInstance().get(7);
        return (i == 1 || i == 7) ? false : true;
    }

    public static String long2Date(long j) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(j));
    }

    public static long today() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - INTERVAL_DAY;
        try {
            Date date = new Date(currentTimeMillis);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = simpleDateFormat.format(date);
            Date parse = simpleDateFormat.parse(format);
            j = parse.getTime();
            CommonUtils.log(TAG, "Today is:" + format + ", date is:" + parse + ", todayInMillis=" + j + ", curr=" + currentTimeMillis);
            return j;
        } catch (Exception unused) {
            CommonUtils.logE(TAG, "Opps! Fail to get what date is today.");
            return j;
        }
    }
}
