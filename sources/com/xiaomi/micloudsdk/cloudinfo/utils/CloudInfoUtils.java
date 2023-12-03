package com.xiaomi.micloudsdk.cloudinfo.utils;

import android.os.Build;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.Locale;
import miui.cloud.os.SystemProperties;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.cloud.sync.VipInfo;
import miui.provider.Weather;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class CloudInfoUtils {
    private static String sUserAgent;

    private static String convertObsoleteLanguageCodeToNew(String str) {
        if (str == null) {
            return null;
        }
        return "iw".equals(str) ? "he" : "in".equals(str) ? "id" : "ji".equals(str) ? "yi" : str;
    }

    public static MiCloudStatusInfo.ItemInfo getItemInfo(MiCloudStatusInfo miCloudStatusInfo, JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        String optString = jSONObject.optString("Name");
        String optString2 = jSONObject.optString("LocalizedName");
        long optLong = jSONObject.optLong("Used");
        miCloudStatusInfo.getClass();
        return new MiCloudStatusInfo.ItemInfo(optString, optString2, optLong);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0037 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:12:0x0038  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static miui.cloud.sync.VipInfo getMiCloudMemberStatusInfo(java.lang.String r2, java.lang.String r3) throws com.xiaomi.opensdk.exception.UnretriableException, com.xiaomi.opensdk.exception.RetriableException, com.xiaomi.opensdk.exception.AuthenticationException {
        /*
            java.lang.String r2 = getUserAgent()
            android.util.ArrayMap r0 = new android.util.ArrayMap
            r0.<init>()
            java.lang.String r1 = "version"
            r0.put(r1, r2)
            java.lang.String r2 = "_locale"
            r0.put(r2, r3)
            java.lang.String r2 = com.xiaomi.micloudsdk.utils.MiCloudConstants.URL.URL_MICLOUD_MEMBER_STATUS_QUERY
            com.xiaomi.micloudsdk.request.utils.HttpUtils$HttpMethod r3 = com.xiaomi.micloudsdk.request.utils.HttpUtils.HttpMethod.GET
            r1 = 0
            org.json.JSONObject r2 = com.xiaomi.micloudsdk.request.utils.Request.requestServer(r2, r3, r0, r1)
            java.lang.String r3 = "code"
            int r3 = r2.getInt(r3)     // Catch: org.json.JSONException -> L2c
            if (r3 != 0) goto L34
            java.lang.String r3 = "data"
            org.json.JSONObject r2 = r2.getJSONObject(r3)     // Catch: org.json.JSONException -> L2c
            goto L35
        L2c:
            r2 = move-exception
            java.lang.String r3 = "CloudInfoUtils"
            java.lang.String r0 = "JSONException in getMiCloudMemberStatusInfo"
            android.util.Log.e(r3, r0, r2)
        L34:
            r2 = r1
        L35:
            if (r2 != 0) goto L38
            return r1
        L38:
            miui.cloud.sync.VipInfo r2 = getVipInfo(r2)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.micloudsdk.cloudinfo.utils.CloudInfoUtils.getMiCloudMemberStatusInfo(java.lang.String, java.lang.String):miui.cloud.sync.VipInfo");
    }

    public static MiCloudStatusInfo.QuotaInfo getQuotaInfo(MiCloudStatusInfo miCloudStatusInfo, JSONObject jSONObject) {
        long optLong = jSONObject.optLong("Total");
        long optLong2 = jSONObject.optLong("Used");
        String optString = jSONObject.optString("Warn");
        String optString2 = jSONObject.optString("YearlyPackageType");
        long optLong3 = jSONObject.optLong("YearlyPackageSize");
        long optLong4 = jSONObject.optLong("YearlyPackageCreateTime");
        long optLong5 = jSONObject.optLong("YearlyPackageExpireTime");
        miCloudStatusInfo.getClass();
        MiCloudStatusInfo.QuotaInfo quotaInfo = new MiCloudStatusInfo.QuotaInfo(optLong, optLong2, optString, optString2, optLong3, optLong4, optLong5);
        JSONArray optJSONArray = jSONObject.optJSONArray("ItemInfoList");
        if (optJSONArray != null) {
            for (int i = 0; i < optJSONArray.length(); i++) {
                MiCloudStatusInfo.ItemInfo itemInfo = getItemInfo(miCloudStatusInfo, optJSONArray.optJSONObject(i));
                if (itemInfo != null) {
                    quotaInfo.addItemInfo(itemInfo);
                }
            }
        }
        return quotaInfo;
    }

    public static String getUserAgent() {
        if (sUserAgent == null) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            String str = Build.MODEL;
            sb2.append(str);
            sb2.append("/");
            sb.append(sb2.toString());
            String str2 = SystemProperties.get("ro.product.mod_device");
            if (TextUtils.isEmpty(str2)) {
                sb.append(str);
            } else {
                sb.append(str2);
            }
            sb.append("; MIUI/");
            sb.append(Build.VERSION.INCREMENTAL);
            sb.append(" E/");
            sb.append(SystemProperties.get("ro.miui.ui.version.name"));
            sb.append(" B/");
            if (miui.os.Build.IS_ALPHA_BUILD) {
                sb.append("A");
            } else if (miui.os.Build.IS_DEVELOPMENT_VERSION) {
                sb.append("D");
            } else if (miui.os.Build.IS_STABLE_VERSION) {
                sb.append("S");
            } else {
                sb.append("null");
            }
            sb.append(" L/");
            Locale locale = Locale.getDefault();
            String language = locale.getLanguage();
            if (language != null) {
                sb.append(convertObsoleteLanguageCodeToNew(language));
                String country = locale.getCountry();
                if (country != null) {
                    sb.append("-");
                    sb.append(country.toUpperCase());
                }
            } else {
                sb.append("EN");
            }
            sb.append(" LO/");
            String region = miui.os.Build.getRegion();
            if (TextUtils.isEmpty(region)) {
                sb.append("null");
            } else {
                sb.append(region.toUpperCase());
            }
            sUserAgent = sb.toString();
        }
        return sUserAgent;
    }

    public static VipInfo getVipInfo(JSONObject jSONObject) {
        return new VipInfo(jSONObject.optString("vipName"), jSONObject.optString(Weather.AlertInfo.LEVEL));
    }

    public static JSONObject toJSONObject(MiCloudStatusInfo.ItemInfo itemInfo) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("Name", itemInfo.getName());
        jSONObject.put("Used", itemInfo.getUsed());
        jSONObject.put("LocalizedName", itemInfo.getLocalizedName());
        return jSONObject;
    }

    public static JSONObject toJSONObject(MiCloudStatusInfo.QuotaInfo quotaInfo) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("Total", quotaInfo.getTotal());
        jSONObject.put("Used", quotaInfo.getUsed());
        jSONObject.put("Warn", quotaInfo.getWarn());
        jSONObject.put("YearlyPackageType", quotaInfo.getYearlyPackageType());
        jSONObject.put("YearlyPackageSize", quotaInfo.getYearlyPackageSize());
        jSONObject.put("YearlyPackageCreateTime", quotaInfo.getYearlyPackageCreateTime());
        jSONObject.put("YearlyPackageExpireTime", quotaInfo.getYearlyPackageExpireTime());
        JSONArray jSONArray = new JSONArray();
        Iterator<MiCloudStatusInfo.ItemInfo> it = quotaInfo.getItemInfoList().iterator();
        while (it.hasNext()) {
            jSONArray.put(toJSONObject(it.next()));
        }
        jSONObject.put("ItemInfoList", jSONArray);
        return jSONObject;
    }
}
