package com.android.settings.usagestats.controller;

import android.app.PendingIntent;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.usagestats.TimeoverActivity;
import com.android.settings.usagestats.utils.AppLimitStateUtils;
import com.android.settings.usagestats.utils.AppUsageStatsFactory;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.usagestats.utils.DateUtils;
import java.util.concurrent.TimeUnit;

/* loaded from: classes2.dex */
public class AppUsageController {
    public static int NOTIFICATION_LAST_FORCE_WARN_TIME = 1;
    public static int NOTIFICATION_SHOW_TIME = 15;
    private static String TAG = "LR-AppUsageController";

    private static Intent getRegisterServiceIntent(Context context, String str, int i) {
        Intent intent = new Intent(context, AppLimitService.class);
        intent.putExtra("limitTime", i);
        intent.putExtra("registerTime", AppLimitStateUtils.getAppRegisterTime(context, str));
        intent.putExtra("pkgName", str);
        return intent;
    }

    private static void interceptor(Context context, UsageStatsManager usageStatsManager, String str, int i) {
        long appRegisterTime = AppLimitStateUtils.getAppRegisterTime(context, str);
        if (appRegisterTime != 0) {
            int loadTodayTotalTimeForPackage = (int) (AppUsageStatsFactory.loadTodayTotalTimeForPackage(context, str, appRegisterTime, System.currentTimeMillis()) / DateUtils.INTERVAL_MINUTE);
            Log.d(TAG, "interceptor: usageTime=" + loadTodayTotalTimeForPackage);
            i -= loadTodayTotalTimeForPackage;
        }
        Intent registerServiceIntent = getRegisterServiceIntent(context, str, i);
        if (i <= NOTIFICATION_SHOW_TIME) {
            unregisterAppUsageObserver(context, str);
        } else {
            int hashCode = str.hashCode() >> 1;
            usageStatsManager.registerAppUsageObserver(hashCode, new String[]{str}, (i - NOTIFICATION_SHOW_TIME) * 60, TimeUnit.SECONDS, PendingIntent.getService(context, hashCode, registerServiceIntent, MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP));
        }
        if (i > NOTIFICATION_LAST_FORCE_WARN_TIME) {
            int hashCode2 = str.hashCode() >> 2;
            registerServiceIntent.putExtra("ensureForeGround", true);
            usageStatsManager.registerAppUsageObserver(hashCode2, new String[]{str}, (i - NOTIFICATION_LAST_FORCE_WARN_TIME) * 60, TimeUnit.SECONDS, PendingIntent.getService(context, hashCode2, registerServiceIntent, MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP));
        }
        context.startService(registerServiceIntent);
    }

    public static void registerAppUsageObserver(Context context, String str, long j) {
        if (j <= 0 || TextUtils.isEmpty(str)) {
            CommonUtils.logE(TAG, "Opps! The limited time should >= 1 minute");
            return;
        }
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        if (usageStatsManager == null) {
            CommonUtils.logE(TAG, "Opps! manager is null!");
            return;
        }
        interceptor(context, usageStatsManager, str, (int) j);
        Intent intent = new Intent(context, TimeoverActivity.class);
        intent.putExtra("pkgName", str);
        intent.putExtra("theEnd", true);
        int hashCode = str.hashCode();
        usageStatsManager.registerAppUsageObserver(hashCode, new String[]{str}, 60 * j, TimeUnit.SECONDS, PendingIntent.getActivity(context, hashCode, intent, MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP));
        AppLimitStateUtils.setTodayRegisterTime(context);
        CommonUtils.log(TAG, "[Registered] pkg=" + str + ", limitTime=" + j + "min");
    }

    public static void removeUnregisterApp(Context context, String str) {
        Intent intent = new Intent(context, AppLimitService.class);
        intent.putExtra("pkgName", str);
        intent.putExtra("remove", true);
        context.startService(intent);
    }

    public static void suspendApp(Context context, String str, boolean z) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            CommonUtils.logE(TAG, "[Suspended] failed since pm is null!");
            return;
        }
        packageManager.setPackagesSuspended(new String[]{str}, z, null, null, "!miui_Suspended!");
        if (z) {
            try {
                CommonUtils.forceStopPackage(context, str);
                Log.d(TAG, "suspendApp: cancel process");
            } catch (Exception e) {
                Log.e(TAG, "suspendApp: ", e);
            }
        }
        AppLimitStateUtils.addSuspendApp(context, str, z);
        CommonUtils.log(TAG, "[Suspended] pkg=" + str + ", suspended=" + z);
    }

    public static void unregisterAppUsageObserver(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        if (usageStatsManager == null) {
            CommonUtils.logE(TAG, "Opps! unregister manager is null!");
            return;
        }
        int hashCode = str.hashCode();
        usageStatsManager.unregisterAppUsageObserver(hashCode >> 2);
        usageStatsManager.unregisterAppUsageObserver(hashCode >> 1);
        usageStatsManager.unregisterAppUsageObserver(hashCode);
        CommonUtils.log(TAG, "[UNRegistered] pkg=" + str);
    }
}
