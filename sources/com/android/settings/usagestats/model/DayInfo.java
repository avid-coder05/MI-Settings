package com.android.settings.usagestats.model;

import java.io.Serializable;
import java.util.Calendar;

/* loaded from: classes2.dex */
public class DayInfo implements Serializable {
    public long dayBeginningTime;
    public int dayInMonth;
    public int dayInWeek;
    public int dayMonthInYear;

    public DayInfo(Calendar calendar, long j) {
        calendar = calendar == null ? Calendar.getInstance() : calendar;
        calendar.setTimeInMillis(j);
        this.dayBeginningTime = j;
        this.dayInWeek = calendar.get(7);
        this.dayInMonth = calendar.get(5);
        this.dayMonthInYear = calendar.get(2);
    }
}
