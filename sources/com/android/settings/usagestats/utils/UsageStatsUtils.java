package com.android.settings.usagestats.utils;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.ArrayMap;
import com.android.settings.usagestats.model.AppUsageListFloorData;
import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.model.AppValueData;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.DayInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class UsageStatsUtils {
    private static UsageStatsManager MANAGER;

    private static void aggreOneAppWeekDayData(HashMap<String, ArrayList<AppValueData>> hashMap, List<String> list, AppUsageListFloorData appUsageListFloorData) {
        AppUsageStats value;
        if (hashMap == null) {
            hashMap = new HashMap<>();
        }
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            hashMap.put(it.next(), new ArrayList<>());
        }
        List<DayAppUsageStats> dayAppUsageStatsWeekList = appUsageListFloorData.getDayAppUsageStatsWeekList();
        int i = 0;
        for (DayAppUsageStats dayAppUsageStats : dayAppUsageStatsWeekList) {
            i++;
            DayInfo dayInfo = dayAppUsageStats.getDayInfo();
            for (Map.Entry<String, AppUsageStats> entry : dayAppUsageStats.getAppUsageStatsMap().entrySet()) {
                if (entry != null && (value = entry.getValue()) != null) {
                    AppValueData appValueData = new AppValueData();
                    appValueData.setDayInfo(dayInfo);
                    appValueData.setPackageName(entry.getKey());
                    appValueData.setValue(value.getTotalForegroundTime());
                    ArrayList<AppValueData> arrayList = hashMap.get(entry.getKey());
                    if (arrayList != null) {
                        arrayList.add(appValueData);
                    } else {
                        ArrayList<AppValueData> arrayList2 = new ArrayList<>();
                        arrayList2.add(appValueData);
                        hashMap.put(entry.getKey(), arrayList2);
                    }
                }
            }
            for (Map.Entry<String, ArrayList<AppValueData>> entry2 : hashMap.entrySet()) {
                if (entry2.getValue().size() < i) {
                    AppValueData appValueData2 = new AppValueData();
                    appValueData2.setValue(0L);
                    appValueData2.setPackageName(entry2.getKey());
                    appValueData2.setDayInfo(dayInfo);
                    entry2.getValue().add(appValueData2);
                }
            }
        }
    }

    public static void dealAppUsageWeekList(HashMap<String, ArrayList<AppValueData>> hashMap, AppUsageListFloorData appUsageListFloorData, Map<String, AppValueData> map) {
        ArrayList arrayList = new ArrayList();
        if (map == null) {
            map = new HashMap<>();
        }
        List<DayAppUsageStats> dayAppUsageStatsWeekList = appUsageListFloorData.getDayAppUsageStatsWeekList();
        if (!dayAppUsageStatsWeekList.isEmpty()) {
            Iterator<DayAppUsageStats> it = dayAppUsageStatsWeekList.iterator();
            while (it.hasNext()) {
                extractData(map, it.next(), arrayList);
            }
        }
        if (map.values() == null || map.values().size() <= 0) {
            return;
        }
        aggreOneAppWeekDayData(hashMap, arrayList, appUsageListFloorData);
    }

    private static void extractData(Map<String, AppValueData> map, DayAppUsageStats dayAppUsageStats, List<String> list) {
        ArrayMap<String, AppUsageStats> appUsageStatsMap = dayAppUsageStats.getAppUsageStatsMap();
        if (appUsageStatsMap == null || appUsageStatsMap.entrySet() == null) {
            return;
        }
        for (Map.Entry<String, AppUsageStats> entry : appUsageStatsMap.entrySet()) {
            String key = entry.getKey();
            list.add(key);
            if (entry.getValue() != null) {
                long totalForegroundTime = entry.getValue().getTotalForegroundTime();
                if (map.containsKey(key)) {
                    AppValueData appValueData = map.get(key);
                    appValueData.setValue(appValueData.getValue() + totalForegroundTime);
                } else {
                    AppValueData appValueData2 = new AppValueData();
                    appValueData2.setPackageName(key);
                    appValueData2.setValue(totalForegroundTime);
                    map.put(key, appValueData2);
                }
            }
        }
    }

    private static void filterUsageStats(List<UsageStats> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Iterator<UsageStats> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().getLastTimeUsed() <= 0) {
                it.remove();
            }
        }
    }

    public static UsageEvents getEventStats(Context context, long j, long j2) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        if (usageStatsManager != null) {
            return usageStatsManager.queryEvents(j, j2);
        }
        CommonUtils.logE("LR-UsageStatsUtils", "getEventStats()......manager is null!");
        return null;
    }

    public static List<UsageStats> getUsageStats(Context context, int i, long j, long j2) {
        List<UsageStats> arrayList = new ArrayList<>();
        if (MANAGER == null) {
            MANAGER = (UsageStatsManager) context.getSystemService("usagestats");
        }
        UsageStatsManager usageStatsManager = MANAGER;
        if (usageStatsManager != null) {
            arrayList = usageStatsManager.queryUsageStats(i, j, j2);
        } else {
            CommonUtils.logE("LR-UsageStatsUtils", "getUsageStats()......manager is null!");
        }
        filterUsageStats(arrayList);
        return arrayList;
    }

    public static List<UsageStats> getUsageStats(Context context, long j, long j2) {
        return getUsageStats(context, 0, j, j2);
    }
}
