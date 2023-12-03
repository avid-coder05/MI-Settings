package com.android.settings.usagestats.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import com.android.settings.usagestats.TimeoverActivity;
import com.android.settings.usagestats.UsageAppDetailActivity;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.utils.AppLimitStateUtils;
import com.android.settings.usagestats.utils.AppUsageStatsFactory;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.usagestats.utils.DateUtils;
import java.util.List;
import java.util.Map;
import miui.app.constants.ThemeManagerConstants;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;
import miui.process.ProcessManager;

/* loaded from: classes2.dex */
public class AppLimitService extends Service {
    private int currentPkgUsageTime;
    private IForegroundInfoListener.Stub mAppObserver = new IForegroundInfoListener.Stub() { // from class: com.android.settings.usagestats.controller.AppLimitService.1
        public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
            Log.d("AppLimitService", "onForegroundInfoChanged: " + foregroundInfo.mForegroundPackageName);
            if (UserHandle.getUserId(foregroundInfo.mForegroundUid) != 0) {
                return;
            }
            String str = foregroundInfo.mForegroundPackageName;
            AppLimitService.this.mForegroundPkgName = str;
            if (AppLimitService.this.mLimitApps != null && AppLimitService.this.mLimitApps.containsKey(str)) {
                AppLimitService.this.updateNotification(str, false);
                return;
            }
            AppLimitService appLimitService = AppLimitService.this;
            appLimitService.updateNotification(appLimitService.mForegroundPkgName, true);
        }
    };
    private Map<String, Long> mAppRegisterTime;
    private String mForegroundPkgName;
    private Map<String, Integer> mLimitApps;
    private NotificationManager notificationManager;

    private void backUpData(String str, int i, long j) {
        AppLimitStateUtils.openTimeLimit(getApplicationContext(), str);
        AppLimitStateUtils.setAppRegisterTime(getApplicationContext(), str, j);
        AppLimitStateUtils.setLimitTime(getApplicationContext(), str, i, DateUtils.isWeekdayToday());
    }

    private Notification buildNotification(String str, int i) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "com.android.settings.appLimit");
        builder.setContentTitle(getString(R.string.usage_app_limit_reach_title, new Object[]{AppInfoUtils.getAppName(getApplicationContext(), str)}));
        builder.setContentText(getString(R.string.usage_app_limit_reach_summay, new Object[]{getResources().getQuantityString(R.plurals.usage_state_minute, i, Integer.valueOf(i))}));
        builder.setSmallIcon(R.drawable.ic_app_timer);
        builder.setContentIntent(getNotificationIntent(str));
        builder.setLargeIcon(getLargeIcon(str));
        builder.setWhen(System.currentTimeMillis());
        builder.setShowWhen(true);
        builder.addAction(R.drawable.notification_action_reboot_icon, getString(R.string.usage_app_limit_prelong), getProlongIntent(str, i));
        Bundle bundle = new Bundle();
        bundle.putBoolean("miui.showAction", true);
        builder.setExtras(bundle);
        return builder.build();
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        notificationManager.createNotificationChannelGroup(CommonUtils.getAppTimerNotiGroup(getApplicationContext()));
        NotificationChannel notificationChannel = new NotificationChannel("com.android.settings.appLimit", getString(R.string.usage_app_limit_title), 4);
        notificationChannel.setSound(null, null);
        notificationChannel.enableVibration(false);
        notificationChannel.enableLights(false);
        notificationChannel.setGroup("app_timer");
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private Icon getLargeIcon(String str) {
        return Icon.createWithResource(this, R.drawable.ic_app_timer_noti);
    }

    private PendingIntent getNotificationIntent(String str) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isWeek", false);
        bundle.putString("packageName", str);
        bundle.putBoolean("fromNotification", true);
        Intent intent = new Intent(this, UsageAppDetailActivity.class);
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_NO_SCREENSHOT);
        intent.putExtras(bundle);
        return PendingIntent.getActivity(getApplicationContext(), 102, intent, MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
    }

    private PendingIntent getProlongIntent(String str, int i) {
        Intent intent = new Intent("miui.intent.action.settings.SCHEDULE_PROLONG_LIMIT_TIME");
        intent.setPackage("com.android.settings");
        intent.putExtra("pkgName", str);
        intent.putExtra("remainTime", i);
        intent.putExtra("showNotificationTime", System.currentTimeMillis());
        return PendingIntent.getBroadcast(getApplicationContext(), 101, intent, 201326592);
    }

    private int getRemainTime(String str) {
        ensureMapNonNull();
        Integer num = this.mLimitApps.get(str);
        Long l = this.mAppRegisterTime.get(str);
        if (this.currentPkgUsageTime == 0 && l.longValue() != 0) {
            this.currentPkgUsageTime = (int) (AppUsageStatsFactory.loadTodayTotalTimeForPackage(getApplicationContext(), str, l.longValue(), System.currentTimeMillis()) / DateUtils.INTERVAL_MINUTE);
        }
        if (num.intValue() < this.currentPkgUsageTime) {
            int intValue = num.intValue();
            int i = AppUsageController.NOTIFICATION_SHOW_TIME;
            return intValue < i ? num.intValue() : i;
        }
        return num.intValue() - this.currentPkgUsageTime;
    }

    private void init() {
        ensureMapNonNull();
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.usagestats.controller.AppLimitService.2
            @Override // java.lang.Runnable
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                List<String> limitAppList = AppLimitStateUtils.getLimitAppList(AppLimitService.this.getApplicationContext());
                if (limitAppList != null && !limitAppList.isEmpty()) {
                    boolean isWeekdayToday = DateUtils.isWeekdayToday();
                    for (String str : limitAppList) {
                        AppLimitService.this.mLimitApps.put(str, Integer.valueOf(AppLimitStateUtils.getLimitTime(AppLimitService.this.getApplicationContext(), str, isWeekdayToday)));
                        AppLimitService.this.mAppRegisterTime.put(str, Long.valueOf(AppLimitStateUtils.getAppRegisterTime(AppLimitService.this.getApplicationContext(), str)));
                    }
                }
                AppLimitService.this.getForeGroundPackage(true);
                Log.d("AppLimitService", "init: duration=" + (System.currentTimeMillis() - currentTimeMillis));
            }
        });
    }

    private void resolveIntent(Intent intent) {
        if (intent.getBooleanExtra("removeAll", false)) {
            this.mLimitApps.clear();
            this.mAppRegisterTime.clear();
            this.currentPkgUsageTime = 0;
            return;
        }
        String stringExtra = intent.getStringExtra("pkgName");
        int intExtra = intent.getIntExtra("limitTime", 0);
        long longExtra = intent.getLongExtra("registerTime", 0L);
        boolean booleanExtra = intent.getBooleanExtra("remove", false);
        boolean booleanExtra2 = intent.getBooleanExtra("ensureForeGround", false);
        if (booleanExtra) {
            this.mLimitApps.remove(stringExtra);
            this.mAppRegisterTime.remove(stringExtra);
            AppLimitStateUtils.cancelTimeLimit(getApplicationContext(), stringExtra);
            AppLimitStateUtils.clearLimitTime(getApplicationContext(), stringExtra);
            return;
        }
        this.mLimitApps.put(stringExtra, Integer.valueOf(intExtra));
        this.mAppRegisterTime.put(stringExtra, Long.valueOf(longExtra));
        backUpData(stringExtra, intExtra, longExtra);
        if (TextUtils.equals(stringExtra, this.mForegroundPkgName)) {
            updateNotification(stringExtra, false);
        } else if (!booleanExtra2 || CommonUtils.isKeyguardLocked(getApplicationContext())) {
        } else {
            Log.d("AppLimitService", "resolveIntent: ==ensureForeground==" + this.mForegroundPkgName);
            getForeGroundPackage(false);
        }
    }

    private boolean shouldShowNotification(String str) {
        ensureMapNonNull();
        Integer num = this.mLimitApps.get(str);
        Long l = this.mAppRegisterTime.get(str);
        if (l.longValue() != 0) {
            long currentTimeMillis = System.currentTimeMillis();
            Log.d("AppLimitService", "shouldShowNotification: currentTime=" + currentTimeMillis);
            this.currentPkgUsageTime = (int) (AppUsageStatsFactory.loadTodayTotalTimeForPackage(getApplicationContext(), str, l.longValue(), currentTimeMillis) / DateUtils.INTERVAL_MINUTE);
        } else {
            this.currentPkgUsageTime = 0;
        }
        Log.d("AppLimitService", "shouldShowNotification: limitTime=" + num + ",registerTime=" + l + ",usageTime=" + this.currentPkgUsageTime);
        if (num.intValue() - this.currentPkgUsageTime > 0) {
            return num.intValue() - this.currentPkgUsageTime <= AppUsageController.NOTIFICATION_SHOW_TIME;
        }
        Intent intent = new Intent(getApplicationContext(), TimeoverActivity.class);
        intent.putExtra("pkgName", str);
        intent.putExtra("theEnd", true);
        intent.addFlags(268435456);
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_NO_SCREENSHOT);
        startActivity(intent);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotification(String str, boolean z) {
        if (this.notificationManager == null) {
            Log.e("AppLimitService", "updateNotification: notification is null");
        } else if (z || !shouldShowNotification(str)) {
            Log.d("AppLimitService", "updateNotification: hide notification");
            this.notificationManager.cancel(65670);
        } else {
            Log.d("AppLimitService", "updateNotification: show notification");
            this.notificationManager.notify(65670, buildNotification(str, getRemainTime(str)));
        }
    }

    public void ensureMapNonNull() {
        if (this.mLimitApps == null) {
            this.mLimitApps = new ArrayMap();
        }
        if (this.mAppRegisterTime == null) {
            this.mAppRegisterTime = new ArrayMap();
        }
    }

    public void getForeGroundPackage(boolean z) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.android.settings.usagestats.controller.AppLimitService.3
            @Override // java.lang.Runnable
            public void run() {
                String topPackageName = CommonUtils.getTopPackageName(AppLimitService.this.getApplicationContext());
                if (AppLimitStateUtils.UNABLE_LIMIT_APPS.contains(topPackageName)) {
                    return;
                }
                AppLimitService.this.mForegroundPkgName = topPackageName;
                AppLimitService.this.ensureMapNonNull();
                if (AppLimitService.this.mLimitApps.containsKey(AppLimitService.this.mForegroundPkgName)) {
                    AppLimitService appLimitService = AppLimitService.this;
                    appLimitService.updateNotification(appLimitService.mForegroundPkgName, false);
                }
            }
        }, z ? 1500L : 0L);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.d("AppLimitService", "onCreate: ====create====");
        NotificationManager notificationManager = (NotificationManager) getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        this.notificationManager = notificationManager;
        if (notificationManager == null) {
            Log.e("AppLimitService", "[FATAL] Fail to get NotificationManager!");
        } else {
            createNotificationChannel(notificationManager);
        }
        ProcessManager.registerForegroundInfoListener(this.mAppObserver);
        init();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Map<String, Integer> map = this.mLimitApps;
        if (map != null) {
            map.clear();
        }
        Map<String, Long> map2 = this.mAppRegisterTime;
        if (map2 != null) {
            map2.clear();
        }
        ProcessManager.unregisterForegroundInfoListener(this.mAppObserver);
        Map<String, Integer> map3 = this.mLimitApps;
        if (map3 != null) {
            map3.clear();
        }
        Map<String, Long> map4 = this.mAppRegisterTime;
        if (map4 != null) {
            map4.clear();
        }
        Log.d("AppLimitService", "onDestroy: ");
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.d("AppLimitService", "onStartCommand: ====start====");
        ensureMapNonNull();
        if (intent != null && intent.hasExtra("pkgName")) {
            resolveIntent(intent);
            return 1;
        } else if (this.mLimitApps.containsKey(this.mForegroundPkgName)) {
            updateNotification(this.mForegroundPkgName, false);
            return 1;
        } else {
            return 1;
        }
    }
}
