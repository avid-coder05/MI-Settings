package com.android.settings.usagestats.utils;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.android.settings.usagestats.DeviceTimeoverActivity;
import com.android.settings.usagestats.TimeoverActivity;
import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.DayInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import miui.accounts.ExtraAccountManager;
import miui.content.res.ThemeResources;

/* loaded from: classes2.dex */
public class AppUsageStatsFactory {
    private static ConcurrentMap<String, ArrayMap<String, AppUsageStats>> mInnerStats = new ConcurrentHashMap(30);
    private static final List<String> APP_STAT_NEEDED_LIST = new ArrayList<String>() { // from class: com.android.settings.usagestats.utils.AppUsageStatsFactory.1
        {
            add("com.miui.cloudbackup");
            add(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
            add("com.miui.powerkeeper");
            add("com.miui.bugreport");
            add("com.miui.backup");
            add("com.miui.userguide");
            add("com.miui.home");
            add("com.tencent.qqlivexiaomi");
        }
    };

    private static boolean aggregate(List<UsageEvents.Event> list, long j, long j2, AppUsageStats appUsageStats) {
        String str;
        long timeStamp;
        int i = 0;
        if (list == null || list.isEmpty() || appUsageStats == null) {
            CommonUtils.logE("LR-AppUsageStatsFactory", "aggregate()......Fail since invalid params.");
            return false;
        }
        int i2 = 1;
        int i3 = j2 - j > DateUtils.INTERVAL_HOUR ? 0 : 1;
        String pkgName = appUsageStats.getPkgName();
        long j3 = 0;
        while (i < list.size()) {
            UsageEvents.Event event = list.get(i);
            if (!TextUtils.equals(event.getPackageName(), pkgName)) {
                CommonUtils.logE("LR-AppUsageStatsFactory", "Ops! Fail to aggregate due to different package. event.pkgName=" + event.getPackageName() + ", stat.pkgName=" + pkgName);
            }
            if (!doEventFiltered(event)) {
                int eventType = event.getEventType();
                if (eventType == i2) {
                    j3 = event.getTimeStamp();
                    appUsageStats.increaseForegroundCount();
                } else if (eventType != 2) {
                    CommonUtils.logE("LR-AppUsageStatsFactory", "Ops! Invalid eventType for aggregate. pkgName=" + appUsageStats.getPkgName() + ", eventType=" + eventType + ",start=" + j3);
                } else if (j3 > 0 || i <= 0) {
                    if (j3 <= 0) {
                        str = "min";
                        timeStamp = handleCrossUsage(pkgName, j, event.getTimeStamp(), i3);
                    } else {
                        str = "min";
                        timeStamp = event.getTimeStamp() - j3;
                    }
                    if (timeStamp <= 0) {
                        CommonUtils.logE("LR-AppUsageStatsFactory", "aggregate()...Skip this aggregate, diff is invalid! diff=" + timeStamp);
                    } else {
                        appUsageStats.addForegroundTime(timeStamp);
                        CommonUtils.log("LR-AppUsageStatsFactory", "aggregate()...diff=" + (timeStamp / 60000) + str);
                        appUsageStats.updateLastUsageTime(event.getTimeStamp());
                    }
                    j3 = 0;
                } else {
                    CommonUtils.logE("LR-AppUsageStatsFactory", "aggregate()...start <= 0, This is not the first MOVE_TO_BACKGROUND." + pkgName);
                }
            }
            i++;
            i2 = 1;
        }
        if (j3 > 0) {
            CommonUtils.log("LR-AppUsageStatsFactory", "aggregate()...Last event is MOVE_TO_FOREGROUND, we guess it is still been used. pkgName=" + pkgName);
            long j4 = j2 - j3;
            if (System.currentTimeMillis() <= j2) {
                j4 = System.currentTimeMillis() - j3;
                CommonUtils.logE("LR-AppUsageStatsFactory", "aggregate()... Should not go here!");
            }
            if (j4 > DateUtils.INTERVAL_HOUR * 3 || j4 < 0) {
                CommonUtils.logE("LR-AppUsageStatsFactory", "aggregate()... the gap is invalid and we treat it as a mis-event");
                return true;
            }
            appUsageStats.addForegroundTime(j4);
            appUsageStats.setLastUsageTime(Math.min(j2, System.currentTimeMillis()));
            CommonUtils.log("LR-AppUsageStatsFactory", "aggregate()...gap=" + (j4 / 60000) + "min");
            return true;
        }
        return true;
    }

    private static void aggregateEventByPackage(UsageEvents usageEvents, ArrayMap<String, List<UsageEvents.Event>> arrayMap) {
        if (usageEvents == null || arrayMap == null) {
            CommonUtils.logE("LR-AppUsageStatsFactory", "aggregateEventByPackage()......return since invalid params.");
            return;
        }
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            if (usageEvents.getNextEvent(event) && valid(event)) {
                boolean containsKey = arrayMap.containsKey(event.getPackageName());
                List<UsageEvents.Event> arrayList = containsKey ? arrayMap.get(event.getPackageName()) : new ArrayList<>();
                if (!containsKey) {
                    arrayMap.put(event.getPackageName(), arrayList);
                }
                arrayList.add(event);
            }
        }
    }

    private static void aggregateUsageEvent(Context context, UsageEvents usageEvents, ArrayMap<String, AppUsageStats> arrayMap) {
        if (usageEvents == null || arrayMap == null) {
            CommonUtils.logE("LR-AppUsageStatsFactory", "aggregateUsageEvent()......return since invalid params.");
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            if (usageEvents.getNextEvent(event)) {
                checkAndAggregate(packageManager, event, arrayMap);
            }
        }
    }

    private static void aggregateUsageStats(Context context, List<UsageStats> list, UsageEvents usageEvents, long j, long j2, ArrayMap<String, AppUsageStats> arrayMap) {
        if (list == null || usageEvents == null || arrayMap == null) {
            CommonUtils.logE("LR-AppUsageStatsFactory", "aggregateUsageStats()......return since invalid params.");
            return;
        }
        aggregateUsageEvent(context, usageEvents, arrayMap);
        for (UsageStats usageStats : list) {
            String packageName = usageStats.getPackageName();
            if (usageStats.getLastTimeUsed() < j || usageStats.getLastTimeUsed() > j2) {
                CommonUtils.log("LR-AppUsageStatsFactory", "Wow! We filter out it since out of the range. pkgName=" + usageStats.getPackageName() + ", lastTimeUsed=" + DateUtils.long2Date(usageStats.getLastTimeUsed()));
            } else {
                AppUsageStats appUsageStats = arrayMap.get(packageName);
                if (appUsageStats != null) {
                    appUsageStats.updateStats(usageStats.getLastTimeUsed(), usageStats.getTotalTimeInForeground());
                } else {
                    CommonUtils.log("LR-AppUsageStatsFactory", "aggregateUsageStats()......Skip " + packageName + ", no move to foreground event found!");
                }
            }
        }
    }

    private static void aggregateUsageStatsByEvent(Context context, UsageEvents usageEvents, long j, long j2, ArrayMap<String, AppUsageStats> arrayMap) {
        AppUsageStats appUsageStats;
        String str;
        if (usageEvents == null || arrayMap == null) {
            CommonUtils.logE("LR-AppUsageStatsFactory", "aggregateUsageStatsByEvent()......return since invalid params.");
            return;
        }
        CommonUtils.log("LR-AppUsageStatsFactory", "aggregateUsageStatsByEvent().......start=" + DateUtils.long2Date(j) + "(" + j + ")， end=" + DateUtils.long2Date(j2) + "(" + j2 + ")");
        PackageManager packageManager = context.getPackageManager();
        ArrayMap arrayMap2 = new ArrayMap();
        aggregateEventByPackage(usageEvents, arrayMap2);
        for (String str2 : arrayMap2.keySet()) {
            AppUsageStats appUsageStats2 = new AppUsageStats(str2);
            if (aggregate((List) arrayMap2.get(str2), j, j2, appUsageStats2)) {
                PackageInfo resolvePackageInfo = CommonUtils.resolvePackageInfo(packageManager, str2);
                if (resolvePackageInfo == null) {
                    CommonUtils.logE("LR-AppUsageStatsFactory", "Fail to load package info for pkg:" + str2);
                } else {
                    CharSequence applicationLabel = packageManager.getApplicationLabel(resolvePackageInfo.applicationInfo);
                    if (applicationLabel != null) {
                        str = applicationLabel.toString();
                        appUsageStats = appUsageStats2;
                    } else {
                        appUsageStats = appUsageStats2;
                        str = str2;
                    }
                    appUsageStats.setAppName(str);
                    appUsageStats.setPackageInfo(resolvePackageInfo);
                    arrayMap.put(str2, appUsageStats);
                }
            }
        }
    }

    private static boolean checkAndAggregate(PackageManager packageManager, UsageEvents.Event event, ArrayMap<String, AppUsageStats> arrayMap) {
        if (packageManager == null || event == null || event.getEventType() != 1) {
            return false;
        }
        String packageName = event.getPackageName();
        boolean containsKey = arrayMap.containsKey(packageName);
        AppUsageStats appUsageStats = containsKey ? arrayMap.get(packageName) : new AppUsageStats(packageName);
        if (!containsKey) {
            PackageInfo resolvePackageInfo = CommonUtils.resolvePackageInfo(packageManager, packageName);
            if (resolvePackageInfo == null) {
                CommonUtils.logE("LR-AppUsageStatsFactory", "Fail to load package info for pkg:" + packageName);
                return false;
            }
            CharSequence applicationLabel = packageManager.getApplicationLabel(resolvePackageInfo.applicationInfo);
            appUsageStats.setAppName(applicationLabel != null ? applicationLabel.toString() : packageName);
            appUsageStats.setPackageInfo(resolvePackageInfo);
            arrayMap.put(packageName, appUsageStats);
        }
        appUsageStats.increaseForegroundCount();
        return true;
    }

    private static boolean doEventFiltered(UsageEvents.Event event) {
        return TextUtils.equals(event.getPackageName(), "com.android.settings") && (event.getClassName().contains(TimeoverActivity.class.getSimpleName()) || event.getClassName().contains(DeviceTimeoverActivity.class.getSimpleName()));
    }

    private static synchronized void ensureInnerMapNonNull() {
        synchronized (AppUsageStatsFactory.class) {
            if (mInnerStats == null) {
                mInnerStats = new ConcurrentHashMap();
            }
        }
    }

    private static List<UsageEvents.Event> filterEventByPackage(UsageEvents usageEvents, String str) {
        ArrayList arrayList = new ArrayList();
        if (usageEvents == null || str == null) {
            CommonUtils.logE("LR-AppUsageStatsFactory", "filterEventByPackage()....invalid params!");
            return arrayList;
        }
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            if (usageEvents.getNextEvent(event) && valid(event) && TextUtils.equals(event.getPackageName(), str)) {
                arrayList.add(event);
            }
        }
        return arrayList;
    }

    public static void filterUsageEventResult(Context context, long j, long j2, ArrayMap<String, AppUsageStats> arrayMap) {
        if (context == null || arrayMap == null) {
            return;
        }
        Set<String> queryPackageWithIcon = CommonUtils.queryPackageWithIcon(context);
        int size = arrayMap.keySet().size();
        String[] strArr = new String[size];
        arrayMap.keySet().toArray(strArr);
        for (int i = 0; i < size; i++) {
            String str = strArr[i];
            AppUsageStats appUsageStats = arrayMap.get(str);
            if (appUsageStats != null) {
                if (appUsageStats.getLastUsageTime() < j || appUsageStats.getLastUsageTime() > j2) {
                    CommonUtils.logE("LR-AppUsageStatsFactory", "Wow! We filter out it again? pkgName=" + appUsageStats.getPkgName() + ", lastTimeUsed=" + DateUtils.long2Date(appUsageStats.getLastUsageTime()));
                    arrayMap.remove(str);
                } else {
                    String pkgName = appUsageStats.getPkgName();
                    if (!appUsageStats.isValid()) {
                        CommonUtils.logE("LR-AppUsageStatsFactory", "filterUsageEventResult()......Skip, invalid stats. pkgName=" + pkgName);
                        arrayMap.remove(str);
                    } else if ((!queryPackageWithIcon.contains(pkgName) && !APP_STAT_NEEDED_LIST.contains(pkgName)) || isSpecialApp(pkgName)) {
                        arrayMap.remove(str);
                        CommonUtils.logE("LR-AppUsageStatsFactory", "filterUsageEventResult()......Skip, filter out the stat for no icon on launcher / special, pkgName=" + pkgName);
                    }
                }
            }
        }
    }

    private static void getAllPackages(UsageEvents usageEvents, List<String> list) {
        if (usageEvents == null || list == null) {
            CommonUtils.logE("LR-AppUsageStatsFactory", "aggregateEventByPackage()......return since invalid params.");
            return;
        }
        list.clear();
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            if (usageEvents.getNextEvent(event) && valid(event) && !list.contains(event.getPackageName())) {
                list.add(event.getPackageName());
            }
        }
    }

    public static List<Long> getTimeList(Context context, long j, long j2, boolean z) {
        ArrayList arrayList = new ArrayList();
        if (z) {
            j2 = DateUtils.today();
            long cacheTime = AppInfoUtils.getCacheTime(context);
            if (cacheTime > j) {
                AppInfoUtils.clearAppTimerSP(context);
            } else {
                boolean isInSameDay = DateUtils.isInSameDay(cacheTime, j);
                if (isInSameDay) {
                    j2 = cacheTime;
                }
                if (!isInSameDay) {
                    AppInfoUtils.clearAppTimerSP(context);
                }
            }
        }
        int i = 0;
        while (i < DateUtils.COUNT_HOURS_OF_DAY) {
            i++;
            long j3 = (i * DateUtils.INTERVAL_HOUR) + j2;
            if (j3 > j) {
                break;
            }
            arrayList.add(Long.valueOf(j3));
        }
        return arrayList;
    }

    public static List<Long> getTimeList(Context context, long j, boolean z) {
        return getTimeList(context, j, 0L, z);
    }

    private static long handleCrossUsage(String str, long j, long j2, int i) {
        CommonUtils.logE("LR-AppUsageStatsFactory", "aggregate()...start <= 0, This may because a cross usage! This can only occur once:" + str);
        long j3 = j2 - j;
        if (j3 > (i != 0 ? i != 1 ? 0L : DateUtils.INTERVAL_HOUR : 4 * DateUtils.INTERVAL_HOUR)) {
            return 0L;
        }
        return j3;
    }

    private static boolean isSpecialApp(String str) {
        return ThemeResources.SYSTEMUI_NAME.equals(str) || "com.android.settings:remote".equals(str) || "com.android.nfc".equals(str) || "com.android.provision".equals(str);
    }

    private static List<DayAppUsageStats> loadDayUsages(Context context, List<DayInfo> list, boolean z) {
        CommonUtils.logE("LR-AppUsageStatsFactory", "loadDayUsages()......");
        ArrayList arrayList = new ArrayList();
        if (list == null) {
            CommonUtils.logE("LR-AppUsageStatsFactory", "No days info provided!");
            return arrayList;
        }
        int size = list.size() - 1;
        for (int i = 0; i < size - 1; i++) {
            DayInfo dayInfo = list.get(i);
            DayAppUsageStats dayAppUsageStats = new DayAppUsageStats(dayInfo);
            long j = dayInfo.dayBeginningTime;
            dayAppUsageStats.setAppUsageStatsMap(loadUsage(context, j, DateUtils.INTERVAL_DAY + j, z));
            arrayList.add(dayAppUsageStats);
        }
        arrayList.add(loadUsageYesterday(context));
        arrayList.add(new DayAppUsageStats(new DayInfo(null, DateUtils.today())));
        return arrayList;
    }

    public static long loadTodayTotalTimeForPackage(Context context, String str, long j, long j2) {
        long j3;
        Context context2;
        CommonUtils.logE("LR-AppUsageStatsFactory", "loadTodayTotalTimeForPackage()......pkgName=" + str);
        int i = (int) (((j2 - j) / DateUtils.INTERVAL_HOUR) + 1);
        long j4 = 0;
        int i2 = 0;
        while (i2 < i) {
            long j5 = DateUtils.INTERVAL_HOUR;
            long j6 = j + (i2 * j5);
            i2++;
            long j7 = j + (i2 * j5);
            if (j7 > j2) {
                context2 = context;
                j3 = j2;
            } else {
                j3 = j7;
                context2 = context;
            }
            UsageEvents eventStats = UsageStatsUtils.getEventStats(context2, j6, j3);
            AppUsageStats appUsageStats = new AppUsageStats(str);
            if (!aggregate(filterEventByPackage(eventStats, str), j6, j3, appUsageStats)) {
                CommonUtils.log("LR-AppUsageStatsFactory", "Ops! Fail to aggregate~");
            }
            j4 += appUsageStats.getTotalForegroundTime();
        }
        return j4;
    }

    private static ArrayMap<String, AppUsageStats> loadUsage(Context context, long j, long j2, boolean z) {
        CommonUtils.logE("LR-AppUsageStatsFactory", "loadUsage()......start=" + j + ", end=" + j2);
        if (z) {
            return loadUsageAccurately(context, j, j2);
        }
        ArrayMap<String, AppUsageStats> arrayMap = new ArrayMap<>();
        aggregateUsageStats(context, UsageStatsUtils.getUsageStats(context, j, j2), UsageStatsUtils.getEventStats(context, j, j2), j, j2, arrayMap);
        return arrayMap;
    }

    private static ArrayMap<String, AppUsageStats> loadUsageAccurately(Context context, long j, long j2) {
        CommonUtils.logE("LR-AppUsageStatsFactory", "loadUsageAccurately()......");
        ArrayMap<String, AppUsageStats> obtainFromMemory = obtainFromMemory(j);
        if (obtainFromMemory == null || obtainFromMemory.isEmpty()) {
            if (obtainFromMemory == null) {
                obtainFromMemory = new ArrayMap<>();
            }
            if (CacheUtils.buildResult(context, obtainFromMemory, j)) {
                filterUsageEventResult(context, j, j2, obtainFromMemory);
            } else {
                aggregateUsageStatsByEvent(context, UsageStatsUtils.getEventStats(context, j, j2), j, j2, obtainFromMemory);
                filterUsageEventResult(context, j, j2, obtainFromMemory);
                if (!DateUtils.isInSameDay(j, DateUtils.today())) {
                    CacheUtils.serializeResult(context, obtainFromMemory, j);
                }
            }
            mInnerStats.put(String.valueOf(j), obtainFromMemory);
            return obtainFromMemory;
        }
        return obtainFromMemory;
    }

    public static void loadUsageByEndTime(Context context, DayAppUsageStats dayAppUsageStats, long j, long j2) {
        if (j == j2) {
            return;
        }
        ArrayList<String> arrayList = new ArrayList();
        PackageManager packageManager = context.getPackageManager();
        getAllPackages(UsageStatsUtils.getEventStats(context, j2, j), arrayList);
        ArrayMap<String, AppUsageStats> appUsageStatsMap = dayAppUsageStats.getAppUsageStatsMap();
        for (String str : arrayList) {
            PackageInfo resolvePackageInfo = CommonUtils.resolvePackageInfo(packageManager, str);
            if (resolvePackageInfo != null) {
                long loadTodayTotalTimeForPackage = loadTodayTotalTimeForPackage(context, str, j2, j);
                AppUsageStats appUsageStats = appUsageStatsMap.get(str);
                if (appUsageStats != null) {
                    appUsageStats.addForegroundTime(loadTodayTotalTimeForPackage);
                } else {
                    appUsageStats = new AppUsageStats(str);
                    appUsageStats.setPackageInfo(resolvePackageInfo);
                    appUsageStats.setTotalForegroundTime(loadTodayTotalTimeForPackage);
                    appUsageStatsMap.put(str, appUsageStats);
                }
                appUsageStats.setLastUsageTime(j);
            }
        }
    }

    public static List<DayAppUsageStats> loadUsageMonth(Context context, boolean z) {
        CommonUtils.logE("LR-AppUsageStatsFactory", "loadUsageMonth()......");
        return loadDayUsages(context, DateUtils.daysOfMonth(), z);
    }

    public static DayAppUsageStats loadUsageToday(Context context, boolean z) {
        CommonUtils.logE("LR-AppUsageStatsFactory", "loadUsageToday()......");
        long j = DateUtils.today();
        DayAppUsageStats dayAppUsageStats = new DayAppUsageStats(new DayInfo(null, j));
        dayAppUsageStats.setAppUsageStatsMap(loadUsage(context, j, System.currentTimeMillis(), z));
        return dayAppUsageStats;
    }

    public static List<AppUsageStats> loadUsageToday(Context context, String str) {
        CommonUtils.logE("LR-AppUsageStatsFactory", "loadUsageToday()......pkgName=" + str);
        long j = DateUtils.today();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < DateUtils.COUNT_HOURS_OF_DAY) {
            long j2 = DateUtils.INTERVAL_HOUR;
            long j3 = j + (i * j2);
            i++;
            long j4 = j + (i * j2);
            UsageEvents eventStats = UsageStatsUtils.getEventStats(context, j3, j4);
            AppUsageStats appUsageStats = new AppUsageStats(str);
            if (!aggregate(filterEventByPackage(eventStats, str), j3, j4, appUsageStats)) {
                CommonUtils.log("LR-AppUsageStatsFactory", "Ops! Fail to aggregate~");
            }
            arrayList.add(appUsageStats);
        }
        return arrayList;
    }

    public static List<DayAppUsageStats> loadUsageWeek(Context context, boolean z) {
        CommonUtils.logE("LR-AppUsageStatsFactory", "loadUsageWeek()......");
        return loadDayUsages(context, DateUtils.daysOfWeek(true), z);
    }

    private static DayAppUsageStats loadUsageYesterday(Context context) {
        long j = DateUtils.today();
        long j2 = j - DateUtils.INTERVAL_DAY;
        DayAppUsageStats dayAppUsageStats = new DayAppUsageStats(new DayInfo(null, j2));
        ArrayMap<String, AppUsageStats> obtainFromMemory = obtainFromMemory(j2);
        if (obtainFromMemory != null && !obtainFromMemory.isEmpty()) {
            dayAppUsageStats.setAppUsageStatsMap(obtainFromMemory);
            return dayAppUsageStats;
        }
        boolean buildResult = CacheUtils.buildResult(context, dayAppUsageStats.getAppUsageStatsMap(), j2);
        if (!buildResult) {
            long j3 = j2;
            for (Long l : getTimeList(context, j, j2, false)) {
                loadUsageByEndTime(context, dayAppUsageStats, l.longValue(), j3);
                j3 = l.longValue();
            }
        }
        filterUsageEventResult(context, j2, j, dayAppUsageStats.getAppUsageStatsMap());
        dayAppUsageStats.setTotalUsageTime(0L);
        dayAppUsageStats.updateUsageStats();
        if (!buildResult) {
            CacheUtils.serializeResult(context, dayAppUsageStats.getAppUsageStatsMap(), j2);
        }
        mInnerStats.put(String.valueOf(j2), dayAppUsageStats.getAppUsageStatsMap());
        return dayAppUsageStats;
    }

    private static ArrayMap<String, AppUsageStats> obtainFromMemory(long j) {
        ensureInnerMapNonNull();
        if (mInnerStats.containsKey(String.valueOf(j))) {
            return mInnerStats.get(String.valueOf(j));
        }
        return null;
    }

    private static boolean valid(UsageEvents.Event event) {
        return event.getEventType() == 1 || event.getEventType() == 2;
    }
}
