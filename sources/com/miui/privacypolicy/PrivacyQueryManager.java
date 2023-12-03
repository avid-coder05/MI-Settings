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
public class PrivacyQueryManager {
    /* JADX INFO: Access modifiers changed from: protected */
    public static int handlePrivacyQueryTask(Context context, String str, String str2) {
        if (System.currentTimeMillis() - SharePreferenceUtils.getLong(context, str + "_privacy_query_time", 0L) < 86400000) {
            return -5;
        }
        SharePreferenceUtils.putLong(context, str + "_privacy_query_time", System.currentTimeMillis());
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
        } catch (Exception e) {
            Log.e("Privacy_QueryManager", "build jsonObject error, ", e);
        }
        String request = NetUtils.request(hashMap, "https://data.sec.miui.com/privacy/latestVersion", NetUtils.HttpMethod.POST, jSONObject);
        if (TextUtils.isEmpty(request)) {
            return -2;
        }
        try {
            JSONObject jSONObject2 = new JSONObject(request);
            int optInt = jSONObject2.optInt(Tag.TagWebService.CommonResult.RESULT_CODE);
            String optString = jSONObject2.optString("message");
            if (optInt == 200 && "success".equals(optString)) {
                String optString2 = jSONObject2.optString("data");
                if (TextUtils.isEmpty(optString2)) {
                    return -3;
                }
                FileUtils.saveData(optString2, context, "privacy_version", str);
                return 1;
            }
            return -3;
        } catch (Exception e2) {
            Log.e("Privacy_QueryManager", "handlePrivacyAgreeTask error, ", e2);
            return -3;
        }
    }
}
