package com.android.settings.usagestats.model;

import android.util.ArrayMap;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.usagestats.utils.DateUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class DayDeviceUsageStats {
    private DayInfo dayInfo;
    private List<Integer> unlockList = new ArrayList();
    private List<Integer> notificationList = new ArrayList();
    private ArrayMap<String, AppNotificationStats> appNotificationStatsMap = new ArrayMap<>();
    private int totalUnlock = 0;
    private int totalNotification = 0;
    private int totalViewedNotification = 0;

    /* loaded from: classes2.dex */
    public static class AppNotificationStats extends AppUsageInfo {
        private long lastReceivedEventTime;
        private int receivedNotificationCount;
        private int viewedNotificationCount;

        public int getReceivedNotificationCount() {
            return this.receivedNotificationCount;
        }

        public int getViewedNotificationCount() {
            return this.viewedNotificationCount;
        }
    }

    public DayDeviceUsageStats(DayInfo dayInfo) {
        this.dayInfo = dayInfo;
        init();
    }

    private int calIndex(long j) {
        long j2 = this.dayInfo.dayBeginningTime;
        if (j < j2 || j > DateUtils.INTERVAL_DAY + j2) {
            CommonUtils.logE("LR-DayDeviceUsageStats", "addUnlock()... incorrect time:" + j);
            return -1;
        }
        int i = (int) ((j - j2) / DateUtils.INTERVAL_HOUR);
        if (i < 0 || i >= DateUtils.COUNT_HOURS_OF_DAY) {
            return -1;
        }
        return i;
    }

    private void ensureNotifications() {
        int i;
        ArrayMap<String, AppNotificationStats> arrayMap = this.appNotificationStatsMap;
        int i2 = 0;
        if (arrayMap != null) {
            Iterator<String> it = arrayMap.keySet().iterator();
            i = 0;
            while (it.hasNext()) {
                AppNotificationStats appNotificationStats = this.appNotificationStatsMap.get(it.next());
                i2 += appNotificationStats.getReceivedNotificationCount();
                i += appNotificationStats.getViewedNotificationCount();
            }
        } else {
            i = 0;
        }
        if (this.totalNotification != i2) {
            CommonUtils.logE("LR-DayDeviceUsageStats", "This stat has incorrect data. totalNotification=" + this.totalNotification + ",total=" + i2);
        }
        this.totalViewedNotification = i;
    }

    private void init() {
        for (int i = 0; i < DateUtils.COUNT_HOURS_OF_DAY; i++) {
            this.unlockList.add(0);
            this.notificationList.add(0);
        }
    }

    public void addUnlock(long j) {
        int calIndex = calIndex(j);
        if (calIndex < 0 || calIndex >= this.unlockList.size()) {
            return;
        }
        List<Integer> list = this.unlockList;
        list.set(calIndex, Integer.valueOf(list.get(calIndex).intValue() + 1));
        this.totalUnlock++;
    }

    public ArrayMap<String, AppNotificationStats> getAppNotificationStatsMap() {
        return this.appNotificationStatsMap;
    }

    public DayInfo getDayInfo() {
        return this.dayInfo;
    }

    public List<Integer> getNotificationList() {
        return this.notificationList;
    }

    public int getTotalNotification() {
        return this.totalNotification;
    }

    public int getTotalUnlock() {
        return this.totalUnlock;
    }

    public List<Integer> getUnlockList() {
        return this.unlockList;
    }

    public void setReceivedNotificationCountMap(ArrayMap<String, AppNotificationStats> arrayMap) {
        this.appNotificationStatsMap = arrayMap;
        ensureNotifications();
    }
}
