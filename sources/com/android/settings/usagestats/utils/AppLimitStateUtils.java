package com.android.settings.usagestats.utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settings.usagestats.controller.AppLimitService;
import com.android.settings.usagestats.controller.AppUsageController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miui.accounts.ExtraAccountManager;
import miui.app.constants.ThemeManagerConstants;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.content.res.ThemeResources;
import org.json.JSONArray;
import org.json.JSONException;

/* loaded from: classes2.dex */
public class AppLimitStateUtils {
    public static final List<String> UNABLE_LIMIT_APPS = new ArrayList<String>() { // from class: com.android.settings.usagestats.utils.AppLimitStateUtils.1
        {
            add("com.android.phone");
            add("com.android.mms");
            add(ContactsSyncInfoProvider.AUTHORITY);
            add("com.android.settings");
            add(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
            add("com.android.camera");
            add("com.miui.weather2");
            add("com.android.thememanager");
            add("com.android.stk");
            add(ThemeResources.SYSTEMUI_NAME);
            add("com.miui.home");
            add("com.mi.android.globallauncher");
            add("com.android.updater");
            add("com.miui.powerkeeper");
            add("com.miui.backup");
            add("com.miui.cloudbackup");
            add(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
            add("com.miui.voiceassist");
            add("com.miui.bugreport");
        }
    };

    public static void addSuspendApp(Context context, String str, boolean z) {
        List<String> suspendList = getSuspendList(context);
        suspendList.remove(str);
        if (z) {
            suspendList.add(str);
        }
        getSuspendSP(context).edit().putString("suspend_list", listToString(suspendList)).commit();
    }

    public static void cancelCrossDayAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM);
        if (alarmManager != null) {
            Log.e("AppLimitStateUtils", "cancelCrossDayAlarm: ");
            if (CacheUtils.isListEmpty(getLimitAppList(context))) {
                alarmManager.cancel(getPendingIntent(context));
            }
        }
    }

    public static void cancelTimeLimit(Context context, String str) {
        List<String> limitAppList = getLimitAppList(context);
        if (limitAppList == null || !limitAppList.contains(str)) {
            return;
        }
        limitAppList.remove(str);
        if (limitAppList.size() <= 0) {
            stopLimitService(context);
            clearRemoteSp(context);
            cancelCrossDayAlarm(context);
        }
        getAppLimitSP(context).edit().putString("limit_app_list", listToString(limitAppList)).remove(str + "_register").remove(str + "_prolong").commit();
    }

    public static void clearLimitTime(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        getAppLimitSP(context).edit().remove(str + "_weekday").remove(str + "_weekend").commit();
    }

    private static void clearProlongTime(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        getAppLimitSP(context).edit().remove(str + "_prolong").commit();
    }

    private static void clearRemoteSp(Context context) {
        context.getSharedPreferences("miui_AppLimit_remote", 0).edit().clear().commit();
    }

    private static SharedPreferences getAppLimitSP(Context context) {
        return "com.android.settings".equals(getProcessName(context)) ? context.getSharedPreferences("miui_AppLimit", 0) : context.getSharedPreferences("miui_AppLimit_remote", 0);
    }

    public static long getAppRegisterTime(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return 0L;
        }
        return getAppLimitSP(context).getLong(str + "_register", 0L);
    }

    private static String getKey(String str, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(z ? "_weekday" : "_weekend");
        return sb.toString();
    }

    public static JSONArray getLimitAppJsonArray(Context context) {
        try {
            return new JSONArray(getAppLimitSP(context).getString("limit_app_list", "[]"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getLimitAppList(Context context) {
        return jsonToList(getAppLimitSP(context).getString("limit_app_list", "[]"));
    }

    public static int getLimitTime(Context context, String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return 120;
        }
        return getAppLimitSP(context).getInt(getKey(str, z), 120);
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.settings.SCHEDULE_APP_LIMIT");
        intent.setPackage("com.android.settings");
        return PendingIntent.getBroadcast(context, 1, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
    }

    public static String getProcessName(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return null;
        }
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.pid == myPid) {
                return runningAppProcessInfo.processName;
            }
        }
        return null;
    }

    public static int getProlongTime(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        return getAppLimitSP(context).getInt(str + "_prolong", 0);
    }

    private static List<String> getSuspendList(Context context) {
        return jsonToList(getSuspendSP(context).getString("suspend_list", "[]"));
    }

    private static SharedPreferences getSuspendSP(Context context) {
        return context.getSharedPreferences("miui_Suspend", 0);
    }

    public static void initAllLimitApps(final Context context, final boolean z) {
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.usagestats.utils.AppLimitStateUtils$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AppLimitStateUtils.lambda$initAllLimitApps$0(context, z);
            }
        });
    }

    public static boolean isOpenTimeLimit(Context context, String str) {
        List<String> limitAppList = getLimitAppList(context);
        return limitAppList != null && limitAppList.contains(str);
    }

    private static List<String> jsonToList(String str) {
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONArray(str);
            int length = jSONArray.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    arrayList.add(jSONArray.optString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initAllLimitApps$0(Context context, boolean z) {
        List<String> limitAppList = getLimitAppList(context);
        if (CacheUtils.isListEmpty(limitAppList)) {
            Log.d("AppLimitStateUtils", "initAllLimitApps: no limit apps");
            return;
        }
        resetServiceLimitList(context);
        boolean isWeekdayToday = DateUtils.isWeekdayToday();
        long currentTimeMillis = System.currentTimeMillis();
        Log.d("AppLimitStateUtils", "initAllLimitApps: " + limitAppList.size() + ",isNewDay=" + z);
        boolean z2 = false;
        boolean z3 = false;
        for (String str : limitAppList) {
            boolean z4 = !DateUtils.isInSameDay(currentTimeMillis, getAppRegisterTime(context, str));
            if (z || z4) {
                clearProlongTime(context, str);
                AppUsageController.suspendApp(context, str, false);
                register(context, str, getLimitTime(context, str, isWeekdayToday));
                if (!z3) {
                    CommonUtils.insertEvent(context, 0L);
                    z3 = true;
                }
            } else {
                rebootRegister(context, str, isWeekdayToday);
            }
            z2 = z4;
        }
        if (z || z2) {
            releaseSuspendList(context);
        }
        registerCrossDayAlarm(context);
        Log.d("AppLimitStateUtils", "initAllLimitApps: registerLimitTime duration=" + (System.currentTimeMillis() - currentTimeMillis));
    }

    private static String listToString(List<String> list) {
        return (list == null || list.size() <= 0) ? "[]" : new JSONArray((Collection) list).toString();
    }

    public static void openTimeLimit(Context context, String str) {
        List<String> limitAppList = getLimitAppList(context);
        if (limitAppList.contains(str)) {
            return;
        }
        limitAppList.add(str);
        getAppLimitSP(context).edit().putString("limit_app_list", listToString(limitAppList)).commit();
    }

    public static void prolongLimitTime(Context context, String str, int i, long j) {
        setProlongTime(context, str);
        int prolongTime = (getProlongTime(context, str) + i) - ((int) (AppUsageStatsFactory.loadTodayTotalTimeForPackage(context, str, j, System.currentTimeMillis()) / DateUtils.INTERVAL_MINUTE));
        register(context, str, prolongTime);
        Log.d("AppLimitStateUtils", "prolongLimitTime: limitTime = " + prolongTime + ",currentRemainTime=" + i);
    }

    private static void rebootRegister(Context context, String str, boolean z) {
        Log.d("AppLimitStateUtils", "rebootRegister: ===reboot===");
        int limitTime = getLimitTime(context, str, z) + getProlongTime(context, str);
        int loadTodayTotalTimeForPackage = (int) (AppUsageStatsFactory.loadTodayTotalTimeForPackage(context, str, DateUtils.today(), System.currentTimeMillis()) / DateUtils.INTERVAL_MINUTE);
        Log.d("AppLimitStateUtils", "rebootRegister: usageTime=" + loadTodayTotalTimeForPackage + ",limitTime=" + limitTime);
        if (loadTodayTotalTimeForPackage < limitTime) {
            register(context, str, limitTime - loadTodayTotalTimeForPackage);
            return;
        }
        AppUsageController.suspendApp(context, str, true);
        Log.d("AppLimitStateUtils", "rebootRegister: suspendApp");
    }

    public static void register(Context context, String str, int i) {
        setAppRegisterTime(context, str, System.currentTimeMillis());
        if (i > 0) {
            AppUsageController.suspendApp(context, str, false);
            AppUsageController.registerAppUsageObserver(context, str, i);
        }
    }

    public static void registerCrossDayAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM);
        if (alarmManager != null) {
            Log.e("AppLimitStateUtils", "registerCrossDayAlarm: ");
            PendingIntent pendingIntent = getPendingIntent(context);
            alarmManager.cancel(pendingIntent);
            alarmManager.set(1, DateUtils.today() + DateUtils.INTERVAL_DAY, pendingIntent);
        }
    }

    public static void releaseSuspendList(Context context) {
        List<String> suspendList = getSuspendList(context);
        if (suspendList == null || suspendList.isEmpty()) {
            return;
        }
        Log.d("AppLimitStateUtils", "releaseSuspendList: release suspendList =" + suspendList.size());
        Iterator<String> it = suspendList.iterator();
        while (it.hasNext()) {
            AppUsageController.suspendApp(context, it.next(), false);
        }
    }

    private static void resetServiceLimitList(Context context) {
        Intent intent = new Intent(context, AppLimitService.class);
        intent.putExtra("removeAll", true);
        context.startService(intent);
    }

    public static void setAppRegisterTime(Context context, String str, long j) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (j == 0) {
            getAppLimitSP(context).edit().remove(str + "_register").commit();
            return;
        }
        getAppLimitSP(context).edit().putLong(str + "_register", j).commit();
    }

    public static void setLimitTime(Context context, String str, int i, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            getAppLimitSP(context).edit().putInt(getKey(str, z), i).commit();
        } catch (Exception e) {
            Log.e("AppLimitStateUtils", "setLimitTime: ", e);
        }
    }

    private static void setProlongTime(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        getAppLimitSP(context).edit().putInt(str + "_prolong", getProlongTime(context, str) + 30).commit();
    }

    public static void setTodayRegisterTime(Context context) {
        long j = Settings.System.getLong(context.getContentResolver(), "todayRegisterTime", 0L);
        long currentTimeMillis = System.currentTimeMillis();
        if (DateUtils.isInSameDay(currentTimeMillis, j)) {
            return;
        }
        Settings.System.putLong(context.getContentResolver(), "todayRegisterTime", currentTimeMillis);
    }

    private static void stopLimitService(Context context) {
        if (CommonUtils.checkServiceRunning(context, AppLimitService.class)) {
            context.stopService(new Intent(context, AppLimitService.class));
        }
    }

    public static void timeSetAction(Context context) {
        if (Settings.System.getInt(context.getContentResolver(), "time_set_by_settings", 0) != 1) {
            return;
        }
        Settings.System.putInt(context.getContentResolver(), "time_set_by_settings", 0);
        Log.d("AppLimitStateUtils", "timeSetAction: init app limit ??");
        initAllLimitApps(context, true);
    }
}
