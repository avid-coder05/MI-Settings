package com.android.settings.usagestats.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.settings.R;
import com.android.settings.Settings;
import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.DayInfo;
import java.util.List;
import java.util.Set;
import miui.vip.VipService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class AppInfoUtils {
    public static JSONArray aggregateToJOSNArray(ArrayMap<String, AppUsageStats> arrayMap) {
        Set<String> keySet = arrayMap.keySet();
        JSONArray jSONArray = new JSONArray();
        for (String str : keySet) {
            AppUsageStats appUsageStats = arrayMap.get(str);
            if (appUsageStats != null) {
                try {
                    jSONArray.put(aggregateToJson(str, appUsageStats));
                } catch (JSONException e) {
                    Log.e("AppInfoUtils", "serializeResult: ", e);
                }
            }
        }
        return jSONArray;
    }

    private static JSONObject aggregateToJson(String str, AppUsageStats appUsageStats) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("totalForeGroundTime", appUsageStats.getTotalForegroundTime());
        jSONObject.put("lastUsageTime", appUsageStats.getLastUsageTime());
        jSONObject.put("packageName", str);
        return jSONObject;
    }

    public static void clearAppTimerSP(Context context) {
        getAppTimerSP(context).edit().clear().commit();
    }

    public static boolean deserializeResult(String str, ArrayMap<String, AppUsageStats> arrayMap) {
        AppUsageStats appUsageStats;
        try {
            JSONArray jSONArray = new JSONArray(str);
            int length = jSONArray.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    JSONObject jSONObject = (JSONObject) jSONArray.opt(i);
                    String optString = jSONObject.optString("packageName");
                    long optLong = jSONObject.optLong("lastUsageTime");
                    long optLong2 = jSONObject.optLong("totalForeGroundTime");
                    if (!TextUtils.isEmpty(optString) && optLong2 > 0) {
                        if (arrayMap.containsKey(optString)) {
                            appUsageStats = arrayMap.get(optString);
                            appUsageStats.addForegroundTime(optLong2);
                        } else {
                            appUsageStats = new AppUsageStats(optString);
                            appUsageStats.setTotalForegroundTime(optLong2);
                            arrayMap.put(optString, appUsageStats);
                        }
                        appUsageStats.setLastUsageTime(optLong);
                    }
                }
                return true;
            }
        } catch (JSONException e) {
            Log.e("AppInfoUtils", "rebuildResult: ", e);
        }
        return false;
    }

    public static String formatTime(Context context, long j) {
        if (j < 60000) {
            return context.getString(R.string.usage_state_less_one_minute);
        }
        int i = (int) (j / 3600000);
        int i2 = (int) ((j - (((i * VipService.VIP_SERVICE_FAILURE) * 60) * 60)) / 60000);
        return (i == 0 || i2 == 0) ? i != 0 ? context.getResources().getQuantityString(R.plurals.usage_state_hour, i, Integer.valueOf(i)) : i2 != 0 ? context.getResources().getQuantityString(R.plurals.usage_state_minute, i2, Integer.valueOf(i2)) : "" : context.getString(R.string.usage_state_hour_minute, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static Drawable getAppLaunchIcon(Context context, String str) {
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            try {
                return packageManager.getApplicationInfo(str, 128).loadIcon(packageManager);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static String getAppName(Context context, String str) {
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            try {
                return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 128)).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static SharedPreferences getAppTimerSP(Context context) {
        return context.getSharedPreferences("apptimer", 0);
    }

    public static long getCacheTime(Context context) {
        return getAppTimerSP(context).getLong("timeStamp", DateUtils.today());
    }

    public static int getColor(Context context, int i) {
        return context.getResources().getColor(i, null);
    }

    public static float getTextBaseLine(Paint paint, float f) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float f2 = fontMetrics.bottom;
        return (f + ((f2 - fontMetrics.top) / 2.0f)) - f2;
    }

    public static float getTextHeight(float f) {
        Paint paint = new Paint();
        paint.setTextSize(f);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.bottom - fontMetrics.top;
    }

    public static float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.bottom - fontMetrics.top;
    }

    private static boolean jumpToByAction(Context context) {
        Intent intent = new Intent();
        intent.setPackage("com.miui.notification");
        intent.setAction("android.settings.ALL_APPS_NOTIFICATION_SETTINGS");
        if (!(context instanceof Activity)) {
            intent.addFlags(268435456);
        }
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            Log.d("AppInfoUtils", "jumpToByAction: false");
            return false;
        }
        context.startActivity(intent);
        Log.d("AppInfoUtils", "jumpToByAction: success");
        return true;
    }

    private static void jumpToNotificationByClass(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, Settings.NotificationAppListSettingsActivity.class);
        intent.putExtra(":settings:show_fragment_title_resid", R.string.usage_state_notification);
        if (!(context instanceof Activity)) {
            intent.addFlags(268435456);
        }
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            Log.d("AppInfoUtils", "jumpToNotificationByClass: fail");
            return;
        }
        context.startActivity(intent);
        Log.d("AppInfoUtils", "jumpToNotificationByClass: success");
    }

    public static void jumpToNotificationManager(Context context) {
        if (jumpToByAction(context)) {
            return;
        }
        jumpToNotificationByClass(context);
    }

    public static void loadUsageByInterval(Context context, DayAppUsageStats dayAppUsageStats) {
        if (context == null) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long j = DateUtils.today();
        DayAppUsageStats dayAppUsageStats2 = dayAppUsageStats == null ? new DayAppUsageStats(new DayInfo(null, j)) : dayAppUsageStats;
        List<Long> timeList = AppUsageStatsFactory.getTimeList(context, currentTimeMillis, j, false);
        dayAppUsageStats2.getAppUsageStatsMap().clear();
        int size = timeList.size();
        int i = 0;
        long j2 = j;
        while (i < size) {
            long longValue = timeList.get(i).longValue();
            AppUsageStatsFactory.loadUsageByEndTime(context, dayAppUsageStats2, longValue, j2);
            i++;
            j2 = longValue;
        }
        AppUsageStatsFactory.loadUsageByEndTime(context, dayAppUsageStats2, currentTimeMillis, j2);
        AppUsageStatsFactory.filterUsageEventResult(context, j, currentTimeMillis, dayAppUsageStats2.getAppUsageStatsMap());
        dayAppUsageStats2.setTotalUsageTime(0L);
        dayAppUsageStats2.updateUsageStats();
        Slog.d("AppInfoUtils", "loadTodayData:duration=" + (System.currentTimeMillis() - currentTimeMillis));
    }

    public static void rebuildResult(Context context, DayAppUsageStats dayAppUsageStats) {
        Slog.d("AppInfoUtils", "rebuildResult....");
        String string = getAppTimerSP(context).getString("todayData", "");
        if (TextUtils.isEmpty(string)) {
            return;
        }
        deserializeResult(string, dayAppUsageStats.getAppUsageStatsMap());
    }

    public static void serializeResult(Context context, ArrayMap<String, AppUsageStats> arrayMap) {
        Slog.d("AppInfoUtils", "serializeResult....");
        if (arrayMap == null || arrayMap.size() <= 0) {
            return;
        }
        JSONArray aggregateToJOSNArray = aggregateToJOSNArray(arrayMap);
        SharedPreferences appTimerSP = getAppTimerSP(context);
        long currentTimeMillis = System.currentTimeMillis();
        appTimerSP.edit().putString("todayData", aggregateToJOSNArray.toString()).commit();
        Slog.d("AppInfoUtils", "duration=" + (System.currentTimeMillis() - currentTimeMillis));
    }

    public static void setCacheTime(Context context, long j) {
        getAppTimerSP(context).edit().putLong("timeStamp", j).commit();
    }
}
