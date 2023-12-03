package com.miui.privacypolicy;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.privacypolicy.NetUtils;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import miui.provider.Weather;
import miui.yellowpage.Tag;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class PrivacyAgreeManager {
    /* JADX INFO: Access modifiers changed from: protected */
    public static int handlePrivacyAgreeTask(Context context, String str, String str2, String str3, String str4, String str5) {
        HashMap hashMap = new HashMap();
        if (TextUtils.isEmpty(str4)) {
            str4 = context.getPackageName();
        }
        hashMap.put("pkg", str4);
        hashMap.put("policyName", str);
        hashMap.put("idContent", str2);
        hashMap.put("miuiVersion", NetUtils.MIUI_VERSION_NAME);
        if (TextUtils.isEmpty(str5)) {
            str5 = PackageUtils.getAppVersionName(context);
        }
        hashMap.put("apkVersion", str5);
        String readData = FileUtils.readData(context, "privacy_temp_update_version", str);
        if (TextUtils.isEmpty(readData)) {
            readData = "";
        }
        hashMap.put("policyVersion", readData);
        hashMap.put("language", Locale.getDefault().getLanguage());
        hashMap.put("region", Locale.getDefault().getCountry());
        hashMap.put(Weather.WeatherBaseColumns.TIMESTAMP, str3);
        JSONObject jSONObject = new JSONObject();
        try {
            for (Map.Entry entry : hashMap.entrySet()) {
                jSONObject.put((String) entry.getKey(), (String) entry.getValue());
            }
        } catch (Exception e) {
            Log.e("Privacy_AgreeManager", "build jsonObject error, ", e);
        }
        String request = NetUtils.request(hashMap, "https://data.sec.miui.com/privacy/agree/v1", NetUtils.HttpMethod.POST, jSONObject);
        if (TextUtils.isEmpty(request)) {
            FileUtils.saveData(jSONObject.toString(), context, "privacy_agree_error", str);
            return -2;
        }
        try {
            JSONObject jSONObject2 = new JSONObject(request);
            int optInt = jSONObject2.optInt(Tag.TagWebService.CommonResult.RESULT_CODE);
            String optString = jSONObject2.optString("message");
            if (optInt == 200 && "success".equals(optString)) {
                String optString2 = new JSONObject(jSONObject2.optString("data")).optString("latestPolicyVersion");
                if (!TextUtils.isEmpty(optString2)) {
                    FileUtils.saveData(optString2, context, "privacy_version", str);
                    FileUtils.deleteFile(context, "privacy_agree_error", str);
                    FileUtils.deleteFile(context, "privacy_temp_update_version", str);
                    FileUtils.deleteFile(context, "privacy_update", str);
                    return 1;
                }
            }
        } catch (Exception e2) {
            Log.e("Privacy_AgreeManager", "handlePrivacyAgreeTask error, ", e2);
        }
        FileUtils.saveData(jSONObject.toString(), context, "privacy_agree_error", str);
        return -3;
    }
}
