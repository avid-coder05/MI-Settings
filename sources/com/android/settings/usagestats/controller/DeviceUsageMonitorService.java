package com.android.settings.usagestats.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.android.settings.R;
import com.android.settings.usagestats.DeviceTimeoverActivity;
import com.android.settings.usagestats.UsageStatsTimeSetActivity;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.DayInfo;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.usagestats.utils.DateUtils;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes2.dex */
public class DeviceUsageMonitorService extends Service {
    private static PendingIntent targetPendingIntent;
    private final Handler handler = new Handler() { // from class: com.android.settings.usagestats.controller.DeviceUsageMonitorService.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Context applicationContext = DeviceUsageMonitorService.this.getApplicationContext();
            DeviceUsageMonitorService.this.limitedTime = DeviceUsageController.getLimitedTimeToday(applicationContext);
            int i = message.what;
            if (i == 111) {
                CommonUtils.log("LR-DeviceUsageMonitorService", "handleMessage(MSG_WHAT_MONITOR)");
                DeviceUsageMonitorService.this.monitor(applicationContext);
            } else if (i != 222) {
            } else {
                CommonUtils.log("LR-DeviceUsageMonitorService", "handleMessage(MSG_WHAT_MONITOR_TERMINAL)");
                removeMessages(111);
                if (DateUtils.isInSameDay(DeviceUsageController.getTodayNotifyTime(applicationContext), DateUtils.today())) {
                    DeviceUsageMonitorService.this.monitorByStrategy(applicationContext, 0);
                    return;
                }
                Intent intent = new Intent(applicationContext, DeviceTimeoverActivity.class);
                intent.addFlags(268435456);
                applicationContext.startActivity(intent);
            }
        }
    };
    private int limitedTime;
    private String notificationDes;
    private String notificationDesOver;
    private String notificationDesReset;
    private NotificationManager notificationManager;
    private String notificationTitle;

    private Notification buildNotification(Context context, int i) {
        return buildNotification(context, i, false);
    }

    private Notification buildNotification(Context context, int i, boolean z) {
        Notification.Builder builder = new Notification.Builder(context, "com.android.settings.usagestats_monitor");
        String format = i > 0 ? z ? String.format(this.notificationDesReset, formatNotificationDes(this.limitedTime), formatNotificationDes(i)) : String.format(this.notificationDes, formatNotificationDes(i)) : this.notificationDesOver;
        builder.setContentTitle(this.notificationTitle);
        builder.setContentText(format);
        builder.setContentIntent(getPendingIntent(context));
        builder.setSmallIcon(R.drawable.ic_app_timer);
        builder.setLargeIcon(Icon.createWithResource(context, R.drawable.ic_app_timer_noti));
        builder.setWhen(System.currentTimeMillis());
        builder.setShowWhen(true);
        return builder.build();
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        notificationManager.createNotificationChannelGroup(CommonUtils.getAppTimerNotiGroup(getApplicationContext()));
        NotificationChannel notificationChannel = new NotificationChannel("com.android.settings.usagestats_monitor", getString(R.string.usage_state_app_timer), 2);
        notificationChannel.setSound(null, null);
        notificationChannel.enableVibration(false);
        notificationChannel.enableLights(false);
        notificationChannel.setGroup("app_timer");
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private String formatNotificationDes(int i) {
        int i2 = i / 60;
        int i3 = i % 60;
        StringBuilder sb = new StringBuilder();
        if (i2 > 0) {
            sb.append(i2);
            sb.append(getResources().getQuantityString(R.plurals.usagestats_device_notification_des_hour, i2, Integer.valueOf(i2)));
        }
        if (i3 > 0) {
            sb.append(i3);
            sb.append(getResources().getQuantityString(R.plurals.usagestats_device_notification_des_min, i3, Integer.valueOf(i3)));
        }
        return sb.toString();
    }

    private PendingIntent getPendingIntent(Context context) {
        if (targetPendingIntent == null) {
            targetPendingIntent = PendingIntent.getActivity(context, 1, new Intent(context, UsageStatsTimeSetActivity.class), 0);
        }
        return targetPendingIntent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$monitor$0(Context context, DayAppUsageStats dayAppUsageStats, int i, int i2, int i3) {
        CommonUtils.insertEvent(context, dayAppUsageStats.getTotalUsageTime());
        CommonUtils.log("LR-DeviceUsageMonitorService", "limitedTime=" + i + "min,usedTime=" + i2 + "min");
        if (i3 <= 0) {
            this.handler.sendEmptyMessageDelayed(222, 1000L);
        } else {
            monitorByStrategy(context, i3);
        }
        if (i3 > 30) {
            updateNotification(context, DeviceUsageController.getLimitedTimeToday(context), false);
            CommonUtils.log("LR-DeviceUsageMonitorService", "monitor().....updateNotification since not reach the left.");
            return;
        }
        updateNotification(context, i3, true);
        CommonUtils.log("LR-DeviceUsageMonitorService", "monitor().....updateNotification since reset=" + i3 + "mins");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$monitor$1(final Context context, final int i) {
        final DayAppUsageStats dayAppUsageStats = new DayAppUsageStats(new DayInfo(null, DateUtils.today()));
        AppInfoUtils.loadUsageByInterval(context, dayAppUsageStats);
        final int totalUsageTimeInMinute = dayAppUsageStats.getTotalUsageTimeInMinute();
        final int i2 = i - totalUsageTimeInMinute;
        this.handler.post(new Runnable() { // from class: com.android.settings.usagestats.controller.DeviceUsageMonitorService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DeviceUsageMonitorService.this.lambda$monitor$0(context, dayAppUsageStats, i, totalUsageTimeInMinute, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void monitor(Context context) {
        monitor(context, this.limitedTime);
    }

    private void monitor(final Context context, final int i) {
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.usagestats.controller.DeviceUsageMonitorService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DeviceUsageMonitorService.this.lambda$monitor$1(context, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void monitorByStrategy(Context context, int i) {
        this.handler.sendEmptyMessageDelayed(111, (i > 60 ? i - 60 : CommonUtils.isKeyguardLocked(context) ? 5 : 1) * DateUtils.INTERVAL_MINUTE);
    }

    private void updateNotification(Context context, int i, boolean z) {
        startForeground(110329, buildNotification(context, i, z));
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        CommonUtils.log("LR-DeviceUsageMonitorService", "[DeviceUsageMonitorService]...onCreate");
        NotificationManager notificationManager = (NotificationManager) getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        this.notificationManager = notificationManager;
        if (notificationManager == null) {
            CommonUtils.logE("LR-DeviceUsageMonitorService", "[FATAL] Fail to get NotificationManager!");
        } else {
            createNotificationChannel(notificationManager);
        }
        Resources resources = getResources();
        this.notificationTitle = resources.getString(R.string.usagestats_device_notification_title);
        this.notificationDes = resources.getString(R.string.usagestats_device_notification_des);
        this.notificationDesReset = resources.getString(R.string.usagestats_device_notification_des_reset);
        this.notificationDesOver = resources.getString(R.string.usagestats_device_notification_des_over);
        this.limitedTime = 0;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        CommonUtils.log("LR-DeviceUsageMonitorService", "[DeviceUsageMonitorService]...onDestroy");
        stopForeground(true);
        DeviceUsageController.broadCastUsageMonitor(this);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        CommonUtils.log("LR-DeviceUsageMonitorService", "[DeviceUsageMonitorService]...onStartCommond");
        this.limitedTime = DeviceUsageController.getLimitedTimeToday(this);
        this.handler.removeMessages(111);
        if (this.limitedTime > 0) {
            Context applicationContext = getApplicationContext();
            startForeground(110329, buildNotification(applicationContext, this.limitedTime));
            monitor(applicationContext, this.limitedTime);
        } else {
            CommonUtils.logE("LR-DeviceUsageMonitorService", "[DeviceUsageMonitorService]... invalid extra for total limited time.");
        }
        return super.onStartCommand(intent, i, i2);
    }
}
