package com.android.settings.usagestats.model;

/* loaded from: classes2.dex */
public class AppUsageListFloorData extends UsageFloorData {
    private static AppUsageListFloorData floorData;
    private DayAppUsageStats mDayAppUsage;

    public AppUsageListFloorData(int i) {
        super(i);
    }

    public static synchronized AppUsageListFloorData getFloorData() {
        AppUsageListFloorData appUsageListFloorData;
        synchronized (AppUsageListFloorData.class) {
            if (floorData == null) {
                floorData = new AppUsageListFloorData(3);
            }
            appUsageListFloorData = floorData;
        }
        return appUsageListFloorData;
    }

    public DayAppUsageStats getmDayAppUsage() {
        return this.mDayAppUsage;
    }

    public void setmDayAppUsage(DayAppUsageStats dayAppUsageStats) {
        this.mDayAppUsage = dayAppUsageStats;
    }
}
