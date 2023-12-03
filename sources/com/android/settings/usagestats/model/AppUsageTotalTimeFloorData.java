package com.android.settings.usagestats.model;

import java.util.List;

/* loaded from: classes2.dex */
public class AppUsageTotalTimeFloorData extends UsageFloorData {
    private List<DayAppUsageStats> mMouthAppUsageStatsList;

    public AppUsageTotalTimeFloorData(int i) {
        super(i);
    }

    public List<DayAppUsageStats> getmMouthAppUsageStatsList() {
        return this.mMouthAppUsageStatsList;
    }

    public synchronized void setmMouthAppUsageStatsList(List<DayAppUsageStats> list) {
        this.mMouthAppUsageStatsList = list;
    }
}
