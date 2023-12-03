package com.android.settings.usagestats.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settings.usagestats.cache.DiskLruCacheUtils;
import com.android.settings.usagestats.cache.NewDiskLruCacheUtils;
import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.model.DayInfo;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class CacheUtils {
    private static String CACHE_TIME = "settings_cache_time";

    public static boolean buildResult(Context context, ArrayMap<String, AppUsageStats> arrayMap, long j) {
        String readFromFile = FileUtils.readFromFile(context, j);
        if (TextUtils.isEmpty(readFromFile)) {
            return false;
        }
        return AppInfoUtils.deserializeResult(readFromFile, arrayMap);
    }

    public static void clearIllegalData(Context context) {
        long j = Settings.System.getLong(context.getContentResolver(), CACHE_TIME, 0L);
        long currentTimeMillis = System.currentTimeMillis();
        if (DateUtils.isInSameDay(j, currentTimeMillis)) {
            return;
        }
        Settings.System.putLong(context.getContentResolver(), CACHE_TIME, currentTimeMillis);
        FileUtils.clearIllegalData(context, DateUtils.today() - (DateUtils.INTERVAL_DAY * 29));
    }

    private static String getLimitAppsDetail(Context context, JSONArray jSONArray) {
        try {
            JSONArray jSONArray2 = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                String string = jSONArray.getString(i);
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("packageName", string);
                jSONObject.put("weekDayTime", AppLimitStateUtils.getLimitTime(context, string, true));
                jSONObject.put("weekEndTime", AppLimitStateUtils.getLimitTime(context, string, false));
                jSONObject.put("registerTime", AppLimitStateUtils.getAppRegisterTime(context, string));
                jSONObject.put("prolongTime", AppLimitStateUtils.getProlongTime(context, string));
                jSONArray2.put(jSONObject);
            }
            return jSONArray2.toString();
        } catch (Exception e) {
            Log.e("CacheUtils", "moveLimitData: ", e);
            return "[]";
        }
    }

    public static boolean hasMoveComplete(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "settings_remove_complete", false);
    }

    public static boolean isJsonArrayEmpty(JSONArray jSONArray) {
        return jSONArray == null || jSONArray.length() <= 0;
    }

    public static boolean isListEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isMapEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$moveCache$0(Context context, long j) {
        moveLimitData(context);
        moveUsageData(context);
        NewDiskLruCacheUtils.getInstance(context).flush();
        NewDiskLruCacheUtils.getInstance(context).close();
        DiskLruCacheUtils.getInstance(context).close();
        MiuiSettings.System.putBoolean(context.getContentResolver(), "settings_remove_complete", true);
        CommonUtils.executeLoadService(context, true);
        AppLimitStateUtils.releaseSuspendList(context);
        Log.d("CacheUtils", "moveCache: duration=" + (System.currentTimeMillis() - j));
    }

    public static void moveCache(final Context context) {
        final long currentTimeMillis = System.currentTimeMillis();
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.usagestats.utils.CacheUtils$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CacheUtils.lambda$moveCache$0(context, currentTimeMillis);
            }
        });
    }

    private static void moveLimitData(Context context) {
        JSONArray limitAppJsonArray = AppLimitStateUtils.getLimitAppJsonArray(context);
        if (limitAppJsonArray == null || limitAppJsonArray.length() <= 0) {
            return;
        }
        NewDiskLruCacheUtils.getInstance(context).putString("app_limit_list", limitAppJsonArray.toString());
        NewDiskLruCacheUtils.getInstance(context).putString("app_limit_details", getLimitAppsDetail(context, limitAppJsonArray));
    }

    private static void moveUsageData(Context context) {
        Iterator<DayInfo> it = DateUtils.daysOfMonth().iterator();
        while (it.hasNext()) {
            String valueOf = String.valueOf(it.next().dayBeginningTime);
            if (TextUtils.isEmpty(NewDiskLruCacheUtils.getInstance(context).getString(valueOf))) {
                String string = DiskLruCacheUtils.getInstance(context).getString(valueOf);
                if (!TextUtils.isEmpty(string)) {
                    NewDiskLruCacheUtils.getInstance(context).putString(valueOf, string);
                }
            }
        }
    }

    public static void serializeResult(Context context, ArrayMap<String, AppUsageStats> arrayMap, long j) {
        if (isMapEmpty(arrayMap)) {
            return;
        }
        JSONArray aggregateToJOSNArray = AppInfoUtils.aggregateToJOSNArray(arrayMap);
        FileUtils.writeToFile(context, isJsonArrayEmpty(aggregateToJOSNArray) ? "" : aggregateToJOSNArray.toString(), j);
    }
}
