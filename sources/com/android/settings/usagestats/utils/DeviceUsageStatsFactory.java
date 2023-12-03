package com.android.settings.usagestats.utils;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.util.ArrayMap;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settings.usagestats.model.DayDeviceUsageStats;
import com.android.settings.usagestats.model.DayInfo;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.Constants;
import miui.content.res.ThemeResources;

/* loaded from: classes2.dex */
public class DeviceUsageStatsFactory {
    private static final List<String> NOTIFICATION_FILTER_CHANNEL_LIST = new ArrayList<String>() { // from class: com.android.settings.usagestats.utils.DeviceUsageStatsFactory.1
        {
            add("securitycenter_resident_notification");
            add("Mms_Default");
        }
    };
    private static final List<String> NOISY_NOTIFICATION_FILTER_PKG_LIST = new ArrayList<String>() { // from class: com.android.settings.usagestats.utils.DeviceUsageStatsFactory.2
        {
            add("com.android.mms");
            add("com.mi.health");
            add("com.xiaomi.market");
            add(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
            add("com.miui.gallery");
        }
    };
    private static final List<String> NOTIFICATION_FILTER_PKG_LIST = new ArrayList<String>() { // from class: com.android.settings.usagestats.utils.DeviceUsageStatsFactory.3
        {
            add(ThemeResources.FRAMEWORK_PACKAGE);
            add(ThemeResources.SYSTEMUI_NAME);
            add(Constants.XMSF_PACKAGE_NAME);
            add("com.miui.systemAdSolution");
            add("com.android.providers.downloads");
        }
    };

    private static boolean analysis(Context context, UsageEvents usageEvents, DayDeviceUsageStats dayDeviceUsageStats) {
        if (usageEvents == null || dayDeviceUsageStats == null) {
            CommonUtils.logE("DeviceUsageStatsFactory", "analysis()...... return since events = null or ret is null.");
            return false;
        }
        ArrayMap<String, DayDeviceUsageStats.AppNotificationStats> arrayMap = new ArrayMap<>();
        context.getPackageManager();
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            if (usageEvents.getNextEvent(event) && event.getEventType() == 18) {
                dayDeviceUsageStats.addUnlock(event.getTimeStamp());
            }
        }
        dayDeviceUsageStats.setReceivedNotificationCountMap(arrayMap);
        return true;
    }

    private static boolean loadDeviceUsage(Context context, long j, long j2, DayDeviceUsageStats dayDeviceUsageStats) {
        return analysis(context, UsageStatsUtils.getEventStats(context, j, j2), dayDeviceUsageStats);
    }

    public static DayDeviceUsageStats loadDeviceUsageToday(Context context) {
        long j = DateUtils.today();
        DayDeviceUsageStats dayDeviceUsageStats = new DayDeviceUsageStats(new DayInfo(null, j));
        if (!loadDeviceUsage(context, j, System.currentTimeMillis(), dayDeviceUsageStats)) {
            CommonUtils.logE("DeviceUsageStatsFactory", "Ops! Fail to load device usage today:" + DateUtils.long2Date(j));
        }
        return dayDeviceUsageStats;
    }

    public static List<DayDeviceUsageStats> loadDeviceUsageWeek(Context context) {
        ArrayList arrayList = new ArrayList();
        List<DayInfo> daysOfWeek = DateUtils.daysOfWeek(true);
        int size = daysOfWeek.size() - 1;
        for (int i = 0; i < size; i++) {
            DayInfo dayInfo = daysOfWeek.get(i);
            DayDeviceUsageStats dayDeviceUsageStats = new DayDeviceUsageStats(dayInfo);
            long j = dayInfo.dayBeginningTime;
            if (loadDeviceUsage(context, j, DateUtils.INTERVAL_DAY + j, dayDeviceUsageStats)) {
                arrayList.add(dayDeviceUsageStats);
            } else {
                CommonUtils.logE("DeviceUsageStatsFactory", "Ops! Fail to load day device usage for:" + DateUtils.long2Date(dayInfo.dayBeginningTime));
            }
        }
        arrayList.add(loadDeviceUsageToday(context));
        return arrayList;
    }
}
