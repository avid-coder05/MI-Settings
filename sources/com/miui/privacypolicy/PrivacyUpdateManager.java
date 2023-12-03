package com.miui.privacypolicy;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.privacypolicy.NetUtils;
import java.util.HashMap;
import java.util.Map;
import miui.provider.Weather;
import miui.yellowpage.Tag;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class PrivacyUpdateManager {
    private static boolean compareVersion(String str, String str2) {
        return TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || str.compareToIgnoreCase(str2) < 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String handlePrivacyUpdateTask(Context context, String str, String str2) {
        if (System.currentTimeMillis() - SharePreferenceUtils.getLong(context.getApplicationContext(), str + "_privacy_update_time", 0L) < 86400000) {
            String readData = FileUtils.readData(context, "privacy_version", str);
            String readData2 = FileUtils.readData(context, "privacy_update", str);
            if (!TextUtils.isEmpty(readData2)) {
                try {
                    if (compareVersion(readData, new JSONObject(readData2).optString("version"))) {
                        return readData2;
                    }
                } catch (Exception e) {
                    Log.e("Privacy_UpdateManager", "handlePrivacyUpdateTask parse temp version error, ", e);
                }
            }
            return String.valueOf(-5);
        }
        SharePreferenceUtils.putLong(context.getApplicationContext(), str + "_privacy_update_time", System.currentTimeMillis());
        HashMap hashMap = new HashMap();
        if (TextUtils.isEmpty(str2)) {
            str2 = context.getPackageName();
        }
        hashMap.put("pkg", str2);
        hashMap.put("policyName", str);
        hashMap.put(Weather.WeatherBaseColumns.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        JSONObject jSONObject = new JSONObject();
        try {
            for (Map.Entry entry : hashMap.entrySet()) {
                jSONObject.put((String) entry.getKey(), (String) entry.getValue());
            }
        } catch (Exception e2) {
            Log.e("Privacy_UpdateManager", "build jsonObject error, ", e2);
        }
        String request = NetUtils.request(hashMap, "https://data.sec.miui.com/privacy/get/v1", NetUtils.HttpMethod.POST, jSONObject);
        if (TextUtils.isEmpty(request)) {
            return String.valueOf(-2);
        }
        try {
            JSONObject jSONObject2 = new JSONObject(request);
            int optInt = jSONObject2.optInt(Tag.TagWebService.CommonResult.RESULT_CODE);
            String optString = jSONObject2.optString("message");
            if (optInt == 200 && "success".equals(optString)) {
                String optString2 = jSONObject2.optString("data");
                String optString3 = new JSONObject(optString2).optString("version");
                if (compareVersion(FileUtils.readData(context, "privacy_version", str), optString3)) {
                    FileUtils.saveData(optString2, context, "privacy_update", str);
                    FileUtils.saveData(optString3, context, "privacy_temp_update_version", str);
                    return optString2;
                }
            }
        } catch (Exception e3) {
            Log.e("Privacy_UpdateManager", "handlePrivacyRevokeTask error, ", e3);
        }
        return String.valueOf(-3);
    }
}
