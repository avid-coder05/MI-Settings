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
public class PrivacyRevokeManager {
    /* JADX INFO: Access modifiers changed from: protected */
    public static int handlePrivacyRevokeTask(Context context, String str, String str2, String str3, String str4) {
        HashMap hashMap = new HashMap();
        if (TextUtils.isEmpty(str3)) {
            str3 = context.getPackageName();
        }
        hashMap.put("pkg", str3);
        hashMap.put("policyName", str);
        hashMap.put("idContent", str2);
        hashMap.put("idStatus", "1");
        hashMap.put("miuiVersion", NetUtils.MIUI_VERSION_NAME);
        if (TextUtils.isEmpty(str4)) {
            str4 = PackageUtils.getAppVersionName(context);
        }
        hashMap.put("apkVersion", str4);
        String readData = FileUtils.readData(context, "privacy_version", str);
        if (TextUtils.isEmpty(readData)) {
            readData = "";
        }
        hashMap.put("policyVersion", readData);
        hashMap.put("language", Locale.getDefault().getLanguage());
        hashMap.put("region", Locale.getDefault().getCountry());
        hashMap.put(Weather.WeatherBaseColumns.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        JSONObject jSONObject = new JSONObject();
        try {
            for (Map.Entry entry : hashMap.entrySet()) {
                jSONObject.put((String) entry.getKey(), (String) entry.getValue());
            }
        } catch (Exception e) {
            Log.e("Privacy_RevokeManager", "build jsonObject error, ", e);
        }
        String request = NetUtils.request(hashMap, "https://data.sec.miui.com/privacy/revoke/v1", NetUtils.HttpMethod.POST, jSONObject);
        if (TextUtils.isEmpty(request)) {
            return -2;
        }
        try {
            JSONObject jSONObject2 = new JSONObject(request);
            int optInt = jSONObject2.optInt(Tag.TagWebService.CommonResult.RESULT_CODE);
            String optString = jSONObject2.optString("message");
            if (optInt == 200 && "success".equals(optString)) {
                FileUtils.deleteFile(context, "privacy_version", str);
                FileUtils.deleteFile(context, "privacy_update", str);
                FileUtils.deleteFile(context, "privacy_temp_update_version", str);
                FileUtils.deleteFile(context, "privacy_agree_error", str);
                SharePreferenceUtils.clear(context);
                return 1;
            }
            return -3;
        } catch (Exception e2) {
            Log.e("Privacy_RevokeManager", "handlePrivacyRevokeTask error, ", e2);
            return -3;
        }
    }
}
