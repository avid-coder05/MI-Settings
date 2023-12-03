package com.android.settings.usagestats.model;

import android.content.Context;
import com.android.settings.usagestats.utils.AppUsageStatsFactory;
import com.android.settings.usagestats.utils.DeviceUsageStatsFactory;
import java.util.List;

/* loaded from: classes2.dex */
public class UsageFloorData {
    protected static List<DayAppUsageStats> dayAppUsageStatsWeekList;
    protected static DayDeviceUsageStats deviceOneDayStats;
    protected static List<DayDeviceUsageStats> deviceUsageWeekList;
    protected int floorType;
    private boolean isWeek;

    public UsageFloorData(int i) {
        this.floorType = i;
    }

    public static void initAll() {
        List<DayAppUsageStats> list = dayAppUsageStatsWeekList;
        if (list != null) {
            list.clear();
            dayAppUsageStatsWeekList = null;
        }
        deviceOneDayStats = null;
    }

    public static void setmDeviceOneDayStats(DayDeviceUsageStats dayDeviceUsageStats) {
        deviceOneDayStats = dayDeviceUsageStats;
    }

    public List<DayAppUsageStats> getDayAppUsageStatsWeekList() {
        return dayAppUsageStatsWeekList;
    }

    public DayDeviceUsageStats getDeviceOneDayStats() {
        return deviceOneDayStats;
    }

    public List<DayDeviceUsageStats> getDeviceUsageWeekList() {
        return deviceUsageWeekList;
    }

    public int getFloorType() {
        return this.floorType;
    }

    public boolean isWeek() {
        return this.isWeek;
    }

    public synchronized void setDayAppUsageStatsWeekList(Context context) {
        if (dayAppUsageStatsWeekList == null) {
            dayAppUsageStatsWeekList = AppUsageStatsFactory.loadUsageWeek(context, true);
        }
    }

    public void setDeviceOneDayStats(Context context) {
        if (deviceOneDayStats == null) {
            deviceOneDayStats = DeviceUsageStatsFactory.loadDeviceUsageToday(context);
        }
    }

    public void setDeviceUsageWeekList(Context context) {
        List<DayDeviceUsageStats> list = deviceUsageWeekList;
        if (list == null) {
            deviceUsageWeekList = DeviceUsageStatsFactory.loadDeviceUsageWeek(context);
        } else if (deviceOneDayStats != null) {
            int size = list.size() - 1;
            if (deviceUsageWeekList.get(size).getDayInfo().dayBeginningTime == deviceOneDayStats.getDayInfo().dayBeginningTime) {
                deviceUsageWeekList.remove(size);
                deviceUsageWeekList.add(deviceOneDayStats);
                return;
            }
            deviceUsageWeekList.clear();
            deviceUsageWeekList = DeviceUsageStatsFactory.loadDeviceUsageWeek(context);
        }
    }

    public void setWeek(boolean z) {
        this.isWeek = z;
    }
}
