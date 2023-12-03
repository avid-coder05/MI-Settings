package com.android.settings.usagestats.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.DayInfo;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.usagestats.utils.DateUtils;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes2.dex */
public class DeviceUsageController {
    private static String TAG = "LR-DeviceUsageController";
    private static Intent serviceIntent;

    public static void broadCastUsageMonitor(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.settings.SCHEDULE_DEVICE_USAGE_MONITOR");
        intent.setPackage("com.android.settings");
        context.sendBroadcast(intent);
    }

    public static boolean ensureServiceRunning(final Context context) {
        CommonUtils.log(TAG, "IMPORTANT: ensureServiceRunning().....");
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.usagestats.controller.DeviceUsageController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DeviceUsageController.lambda$ensureServiceRunning$1(context);
            }
        });
        return true;
    }

    public static int getLimitedTimeCommon(Context context, boolean z) {
        return Settings.System.getInt(context.getContentResolver(), z ? "key_stat_limited_time_weekday" : "key_stat_limited_time_weekend", 0);
    }

    public static int getLimitedTimeToday(Context context) {
        int i = Settings.System.getLong(context.getContentResolver(), "key_stat_today", 0L) == DateUtils.today() ? Settings.System.getInt(context.getContentResolver(), "key_stat_limited_time_today", 0) : 0;
        return i <= 0 ? getLimitedTimeCommon(context, DateUtils.isWeekdayToday()) : i;
    }

    public static boolean getMonitorStatus(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "key_stat_monitor_enable", 0) != 0;
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.settings.SCHEDULE_DEVICE_USAGE_MONITOR");
        intent.setPackage("com.android.settings");
        return PendingIntent.getBroadcast(context, 1, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
    }

    private static Intent getTargetIntent(Context context) {
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, DeviceUsageMonitorService.class);
        }
        return serviceIntent;
    }

    public static long getTodayNotifyTime(Context context) {
        return Settings.System.getLong(context.getContentResolver(), "key_stat_today_notify_time", 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$ensureServiceRunning$1(Context context) {
        boolean monitorStatus = getMonitorStatus(context);
        boolean checkServiceRunning = CommonUtils.checkServiceRunning(context, DeviceUsageMonitorService.class);
        DayAppUsageStats dayAppUsageStats = new DayAppUsageStats(new DayInfo(null, DateUtils.today()));
        AppInfoUtils.loadUsageByInterval(context, dayAppUsageStats);
        boolean z = getLimitedTimeToday(context) - dayAppUsageStats.getTotalUsageTimeInMinute() <= 0;
        if ((!monitorStatus && checkServiceRunning) || (monitorStatus && checkServiceRunning && z)) {
            CommonUtils.logE(TAG, "IMPORTANT: Stop Monitor.....");
            stopMonitor(context);
        }
        if (monitorStatus && !checkServiceRunning && !z) {
            CommonUtils.logE(TAG, "IMPORTANT: Start Monitor.....");
            startMonitor(context);
        }
        CommonUtils.log(TAG, "IMPORTANT: isSet=" + monitorStatus + ",isRunning=" + checkServiceRunning + ",isFinished=" + z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$prolongMonitor$0(Context context, int i) {
        Intent targetIntent = getTargetIntent(context);
        DayAppUsageStats dayAppUsageStats = new DayAppUsageStats(new DayInfo(null, DateUtils.today()));
        AppInfoUtils.loadUsageByInterval(context, dayAppUsageStats);
        setLimitedTimeToday(context, Math.max(getLimitedTimeToday(context), dayAppUsageStats.getTotalUsageTimeInMinute()) + i);
        context.startService(targetIntent);
        CommonUtils.logE(TAG, "prolong DeviceUsageMonitorService..........");
        MiStatInterfaceUtils.trackEvent("prolongMonitor");
        OneTrackInterfaceUtils.track("prolongMonitor", null);
    }

    public static void prolongMonitor(final Context context, final int i) {
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.usagestats.controller.DeviceUsageController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DeviceUsageController.lambda$prolongMonitor$0(context, i);
            }
        });
    }

    private static void registerNextAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM);
        if (alarmManager != null) {
            boolean monitorStatus = getMonitorStatus(context);
            PendingIntent pendingIntent = getPendingIntent(context);
            alarmManager.cancel(pendingIntent);
            if (!monitorStatus) {
                CommonUtils.logE(TAG, "registerNextAlarm()....Cancel!");
                return;
            }
            alarmManager.set(1, DateUtils.today() + DateUtils.INTERVAL_DAY, pendingIntent);
            CommonUtils.logE(TAG, "registerNextAlarm()....Set!");
        }
    }

    public static void setLimitedTimeCommon(Context context, int i, boolean z) {
        Settings.System.putInt(context.getContentResolver(), z ? "key_stat_limited_time_weekday" : "key_stat_limited_time_weekend", i);
    }

    public static void setLimitedTimeToday(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "key_stat_limited_time_today", i);
        Settings.System.putLong(context.getContentResolver(), "key_stat_today", DateUtils.today());
    }

    public static void setMonitorStatus(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "key_stat_monitor_enable", z ? 1 : 0);
        MiStatInterfaceUtils.trackSwitchEvent("enableDeviceMonitor", z);
        OneTrackInterfaceUtils.trackSwitchEvent("enableDeviceMonitor", z);
    }

    public static void setTodayNotifyTime(Context context, long j) {
        Settings.System.putLong(context.getContentResolver(), "key_stat_today_notify_time", j);
    }

    public static void startMonitor(Context context) {
        context.startService(getTargetIntent(context));
        CommonUtils.logE(TAG, "start DeviceUsageMonitorService..........");
    }

    public static void stopMonitor(Context context) {
        context.stopService(getTargetIntent(context));
        CommonUtils.logE(TAG, "stop DeviceUsageMonitorService..........");
        registerNextAlarm(context);
        setLimitedTimeToday(context, 0);
    }
}
