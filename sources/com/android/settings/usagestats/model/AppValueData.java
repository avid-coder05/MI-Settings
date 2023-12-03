package com.android.settings.usagestats.model;

import java.io.Serializable;

/* loaded from: classes2.dex */
public class AppValueData implements Serializable, Comparable<AppValueData> {
    private DayInfo dayInfo;
    private String packageName;
    private Long value;

    @Override // java.lang.Comparable
    public int compareTo(AppValueData appValueData) {
        return appValueData.value.compareTo(this.value);
    }

    public DayInfo getDayInfo() {
        return this.dayInfo;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public long getValue() {
        return this.value.longValue();
    }

    public void setDayInfo(DayInfo dayInfo) {
        this.dayInfo = dayInfo;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public void setValue(long j) {
        this.value = Long.valueOf(j);
    }
}
