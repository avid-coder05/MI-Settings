package com.android.settings.usagestats.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationChannelGroup;
import android.app.usage.UsageStats;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.usagestats.DeviceTimeoverActivity;
import com.android.settings.usagestats.cache.DiskLruCacheUtils;
import com.android.settings.usagestats.model.DeviceUsageFloorData;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

/* loaded from: classes2.dex */
public class CommonUtils {
    public static boolean DEBUG = false;
    private static String TAG = "LR-CommonUtils";
    private static NotificationChannelGroup mAppTimerGroup;

    public static boolean checkServiceRunning(Context context, Class cls) {
        ComponentName componentName;
        String className;
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        if (activityManager == null) {
            logE(TAG, "Ops! Fail to get AM during check service running status.");
            return false;
        }
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (runningServiceInfo != null && (componentName = runningServiceInfo.service) != null && (className = componentName.getClassName()) != null && className.contains(cls.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public static void executeLoadService(Context context, boolean z) {
        try {
            Intent intent = new Intent();
            intent.setPackage("com.xiaomi.misettings");
            intent.putExtra("keyHasComplete", z);
            intent.setAction("miui.settings.action.LOAD_USAGE_DATA");
            context.startService(intent);
        } catch (Exception e) {
            Log.e(TAG, "executeLoadService: ", e);
        }
    }

    public static void forceStopPackage(Context context, String str) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
        if (activityManager != null) {
            activityManager.forceStopPackageAsUser(str, 0);
            activityManager.killBackgroundProcesses(str);
        }
    }

    public static NotificationChannelGroup getAppTimerNotiGroup(Context context) {
        if (mAppTimerGroup == null) {
            mAppTimerGroup = new NotificationChannelGroup("app_timer", context.getString(R.string.usage_state_app_timer));
        }
        return mAppTimerGroup;
    }

    public static long getAppVersionCode(Context context, String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 0);
                if (packageInfo == null) {
                    return 0L;
                }
                return packageInfo.getLongVersionCode();
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return 0L;
    }

    public static Intent getIntentTimerIntent() {
        Intent intent = new Intent();
        intent.setAction("miui.action.usagestas.MAIN");
        intent.setPackage("com.xiaomi.misettings");
        return intent;
    }

    public static Pair<Integer, Integer> getSystemDefaultExitAnim(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(null, new int[]{16842938, 16842939}, 16842926, 0);
        int resourceId = obtainStyledAttributes.getResourceId(0, -1);
        int resourceId2 = obtainStyledAttributes.getResourceId(1, -1);
        obtainStyledAttributes.recycle();
        return new Pair<>(Integer.valueOf(resourceId), Integer.valueOf(resourceId2));
    }

    public static String getTopPackageName(Context context) {
        String str;
        if (Build.VERSION.SDK_INT >= 21) {
            List<UsageStats> usageStats = UsageStatsUtils.getUsageStats(context, DateUtils.today(), System.currentTimeMillis());
            if (usageStats != null && usageStats.size() > 0) {
                TreeMap treeMap = new TreeMap();
                for (UsageStats usageStats2 : usageStats) {
                    treeMap.put(Long.valueOf(usageStats2.getLastTimeUsed()), usageStats2);
                }
                if (!treeMap.isEmpty()) {
                    str = ((UsageStats) treeMap.get(treeMap.lastKey())).getPackageName();
                }
            }
            str = "";
        } else {
            str = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses().get(0).processName;
        }
        Log.d(TAG, "Current App in foreground is: " + str);
        return str;
    }

    public static boolean hasIndependentTimer(Context context) {
        return MiuiUtils.canFindActivityStatic(context, getIntentTimerIntent());
    }

    public static boolean hasIndependentTimer(Context context, Intent intent) {
        return MiuiUtils.canFindActivityStatic(context, intent);
    }

    public static void insertEvent(Context context, long j) {
        if (j > 0 || isKeyguardLocked(context) || !DateUtils.isInMidNight()) {
            return;
        }
        logE(TAG, "=====InsertEvent======");
        Intent intent = new Intent(context, DeviceTimeoverActivity.class);
        intent.putExtra("finish", true);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static boolean isAppExist(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            return context.getPackageManager().getApplicationInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isKeyguardLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        if (keyguardManager != null) {
            return keyguardManager.isKeyguardLocked();
        }
        return false;
    }

    public static boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$preloadUsageStats$0(Context context) {
        Log.d(TAG, "preloadUsageStats: ready to init ... ");
        DeviceUsageFloorData.getDeviceUsageFloorData().setDeviceUsageWeekList(context);
        AppUsageStatsFactory.loadUsageMonth(context, true);
    }

    public static void log(String str, String str2) {
        if (DEBUG) {
            Log.d(str, str2);
        }
    }

    public static void logE(String str, String str2) {
        Log.e(str, str2);
    }

    public static void preloadUsageStats(final Context context) {
        if (!hasIndependentTimer(context)) {
            AsyncTask.execute(new Runnable() { // from class: com.android.settings.usagestats.utils.CommonUtils$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CommonUtils.lambda$preloadUsageStats$0(context);
                }
            });
        } else if (CacheUtils.hasMoveComplete(context)) {
            executeLoadService(context, false);
        }
    }

    public static int queryColor(Context context, String str, String str2) {
        int identifier;
        Resources resources = context.getResources();
        if (resources == null || (identifier = resources.getIdentifier(str, "color", str2)) == 0) {
            return 0;
        }
        return resources.getColor(identifier);
    }

    public static Set<String> queryPackageWithIcon(Context context) {
        ActivityInfo activityInfo;
        String str;
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        HashSet hashSet = new HashSet();
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            if (resolveInfo != null && (activityInfo = resolveInfo.activityInfo) != null && (str = activityInfo.packageName) != null) {
                hashSet.add(str);
            }
        }
        return hashSet;
    }

    public static void releasePreloadStats(Context context) {
        if (DiskLruCacheUtils.isInit()) {
            DiskLruCacheUtils.getInstance(context.getApplicationContext()).close();
        }
    }

    public static PackageInfo resolvePackageInfo(PackageManager packageManager, String str) {
        try {
            return packageManager.getPackageInfo(str, 0);
        } catch (Exception unused) {
            Log.d(TAG, "Fail to resolve PackageInfo for " + str);
            return null;
        }
    }
}
