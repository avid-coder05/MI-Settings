package com.miui.privacypolicy;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import miui.os.Build;
import miui.provider.Weather;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class PrivacyManager {
    private static void checkThreadPermission(String str) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }
        Log.w("Privacy_PrivacyManager", str);
        throw new IllegalStateException(str);
    }

    public static synchronized int privacyAgree(Context context, String str, String str2) {
        synchronized (PrivacyManager.class) {
            checkThreadPermission("can not request privacy agree in main thread!");
            if (Build.IS_INTERNATIONAL_BUILD) {
                return -4;
            }
            return PrivacyAgreeManager.handlePrivacyAgreeTask(context.getApplicationContext(), str, str2, String.valueOf(System.currentTimeMillis()), null, null);
        }
    }

    public static synchronized int privacyRevoke(Context context, String str, String str2) {
        synchronized (PrivacyManager.class) {
            checkThreadPermission("can not request privacy revoke in main thread!");
            if (Build.IS_INTERNATIONAL_BUILD) {
                return -4;
            }
            return PrivacyRevokeManager.handlePrivacyRevokeTask(context.getApplicationContext(), str, str2, null, null);
        }
    }

    public static synchronized String requestPrivacyUpdate(Context context, String str, String str2) {
        synchronized (PrivacyManager.class) {
            checkThreadPermission("can not request privacy update in main thread!");
            if (Build.IS_INTERNATIONAL_BUILD) {
                return String.valueOf(-4);
            }
            return requestPrivacyUpdate(context, str, str2, null, null);
        }
    }

    public static synchronized String requestPrivacyUpdate(Context context, String str, String str2, String str3, String str4) {
        synchronized (PrivacyManager.class) {
            checkThreadPermission("can not request privacy update in main thread!");
            if (Build.IS_INTERNATIONAL_BUILD) {
                return String.valueOf(-4);
            } else if (!FileUtils.isVersionFileExit(context.getApplicationContext(), str)) {
                int handlePrivacyQueryTask = PrivacyQueryManager.handlePrivacyQueryTask(context.getApplicationContext(), str, str3);
                if (handlePrivacyQueryTask == 1) {
                    handlePrivacyQueryTask = -7;
                }
                return String.valueOf(handlePrivacyQueryTask);
            } else if (!FileUtils.isAgreeErrorFileExit(context.getApplicationContext(), str)) {
                return PrivacyUpdateManager.handlePrivacyUpdateTask(context.getApplicationContext(), str, str3);
            } else {
                if (System.currentTimeMillis() - SharePreferenceUtils.getLong(context.getApplicationContext(), str + "_privacy_update_time", 0L) < 86400000) {
                    return String.valueOf(-5);
                }
                SharePreferenceUtils.putLong(context.getApplicationContext(), str + "_privacy_update_time", System.currentTimeMillis());
                String valueOf = String.valueOf(System.currentTimeMillis());
                String readData = FileUtils.readData(context.getApplicationContext(), "privacy_agree_error", str);
                if (!TextUtils.isEmpty(readData)) {
                    try {
                        JSONObject jSONObject = new JSONObject(readData);
                        str2 = jSONObject.optString("idContent", str2);
                        str = jSONObject.optString("policyName", str);
                        valueOf = jSONObject.optString(Weather.WeatherBaseColumns.TIMESTAMP, valueOf);
                    } catch (Exception e) {
                        Log.e("Privacy_PrivacyManager", "parse last jsonObject error, ", e);
                    }
                }
                int handlePrivacyAgreeTask = PrivacyAgreeManager.handlePrivacyAgreeTask(context.getApplicationContext(), str, str2, valueOf, str3, str4);
                if (handlePrivacyAgreeTask == 1) {
                    handlePrivacyAgreeTask = -6;
                }
                return String.valueOf(handlePrivacyAgreeTask);
            }
        }
    }
}
