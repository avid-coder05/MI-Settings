package com.android.settings.usagestats.model;

import android.util.ArrayMap;
import com.android.settings.usagestats.utils.DateUtils;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class DayAppUsageStats {
    private DayInfo dayInfo;
    private ArrayMap<String, AppUsageStats> appUsageStatsMap = new ArrayMap<>();
    private long totalUsageTime = 0;
    private ArrayMap<Integer, Long> categoryUsageStatsMap = new ArrayMap<>();

    public DayAppUsageStats(DayInfo dayInfo) {
        this.dayInfo = dayInfo;
    }

    private void statCategoryUsage(int i, long j) {
        if (this.categoryUsageStatsMap.containsKey(Integer.valueOf(i))) {
            j += this.categoryUsageStatsMap.get(Integer.valueOf(i)).longValue();
        }
        this.categoryUsageStatsMap.put(Integer.valueOf(i), Long.valueOf(j));
    }

    public ArrayMap<String, AppUsageStats> getAppUsageStatsMap() {
        return this.appUsageStatsMap;
    }

    public DayInfo getDayInfo() {
        return this.dayInfo;
    }

    public long getTotalUsageTime() {
        return this.totalUsageTime;
    }

    public int getTotalUsageTimeInMinute() {
        return (int) (this.totalUsageTime / DateUtils.INTERVAL_MINUTE);
    }

    public void setAppUsageStatsMap(ArrayMap<String, AppUsageStats> arrayMap) {
        this.appUsageStatsMap = arrayMap;
        updateUsageStats();
    }

    public void setTotalUsageTime(long j) {
        this.totalUsageTime = j;
    }

    public void updateUsageStats() {
        ArrayMap<String, AppUsageStats> arrayMap = this.appUsageStatsMap;
        if (arrayMap != null) {
            Iterator<String> it = arrayMap.keySet().iterator();
            while (it.hasNext()) {
                AppUsageStats appUsageStats = this.appUsageStatsMap.get(it.next());
                this.totalUsageTime += appUsageStats.getTotalForegroundTime();
                statCategoryUsage(appUsageStats.getCategory(), appUsageStats.getTotalForegroundTime());
            }
        }
    }
}
