package com.xiaomi.micloudsdk.request.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.micloudsdk.exception.CloudServerException;
import com.xiaomi.micloudsdk.utils.JsonUtils;
import com.xiaomi.micloudsdk.utils.MiCloudConstants;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import micloud.compat.independent.utils.RelocationCacheCompat;
import miui.cloud.common.XLogger;
import miui.vip.VipService;
import miui.yellowpage.Tag;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"NewApi"})
/* loaded from: classes2.dex */
public class CloudRelocationUtils {
    private static volatile Object sNeedInitHostList;
    private static volatile boolean sNeedUpdateHostList;
    private static UpdateStatus sUpdateStatus;
    private static final int[] SERVER_RETRY_INTERVALS = {VipService.VIP_SERVICE_FAILURE, 2000, 5000, 10000};
    private static final String URL_RELOCATION_QUERY = MiCloudConstants.URL.URL_RELOCATION_BASE + "/mic/relocation/v3/user/record";
    private static volatile Map<String, Object> sHostsCacheInner = new HashMap();
    private static Object sUpdateMiCloudHostsLock = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum UpdateStatus {
        UPDATING,
        SUCCESS,
        FAILED
    }

    public static String checkRedirect(String str, int i) throws CloudServerException {
        if (i < 15) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.getInt(Tag.TagWebService.CommonResult.RESULT_CODE) == 308) {
                    if (jSONObject.getJSONObject("data").optBoolean("isPermanent")) {
                        setNeedUpdateHostsList(true);
                    }
                    return jSONObject.getJSONObject("data").getString("redirectUrl");
                } else if (jSONObject.getInt(Tag.TagWebService.CommonResult.RESULT_CODE) != 503) {
                    if (jSONObject.getInt(Tag.TagWebService.CommonResult.RESULT_CODE) != 10034) {
                        return null;
                    }
                    throw new CloudServerException(503, 10034, jSONObject.getJSONObject("data").getInt("retryAfter"));
                } else {
                    throw new CloudServerException(503, 503, jSONObject.getJSONObject("data").getInt("retryAfter"));
                }
            } catch (JSONException e) {
                Log.e("Micloud", "JSONException in checkRedirect():" + str, e);
                return null;
            }
        }
        throw new CloudServerException(503, 10034, 10);
    }

    private static Map<String, Object> getHostCache() {
        return new HashMap(sHostsCacheInner);
    }

    private static String getHostWithScheme(String str) {
        if (Log.isLoggable("Micloud", 3)) {
            Log.d("Micloud", "Enter getHost key=" + str);
        }
        Object obj = getHostCache().get(str);
        String str2 = (obj == null || !(obj instanceof String)) ? null : (String) obj;
        if (!TextUtils.isEmpty(str2)) {
            if (Log.isLoggable("Micloud", 3)) {
                Log.d("Micloud", "Hit host cache directly return host = " + str2);
            }
            return str2;
        }
        String cachedHostList = RelocationCacheCompat.getCachedHostList(RequestContext.getContext());
        if (TextUtils.isEmpty(cachedHostList)) {
            if (Log.isLoggable("Micloud", 3)) {
                Log.d("Micloud", "Hosts in SystemSettings/sp = null, return null");
            }
            return null;
        }
        try {
            updateHostCache(JsonUtils.jsonToMap(new JSONObject(cachedHostList)));
            Object obj2 = getHostCache().get(str);
            if (obj2 != null && (obj2 instanceof String)) {
                str2 = (String) obj2;
            }
            if (Log.isLoggable("Micloud", 3)) {
                Log.d("Micloud", "find host in SystemSettings/sp return host = " + str2);
            }
            return str2;
        } catch (JSONException e) {
            Log.e("Micloud", "JSONException in getHost, return null", e);
            return null;
        }
    }

    private static boolean needUpdateHostListAndInitIfNeeded() {
        if (sNeedInitHostList == null) {
            synchronized (sUpdateMiCloudHostsLock) {
                if (sNeedInitHostList == null) {
                    sNeedInitHostList = new Object();
                    sNeedUpdateHostList = TextUtils.isEmpty(RelocationCacheCompat.getCachedHostList(RequestContext.getContext()));
                }
            }
        }
        return sNeedUpdateHostList;
    }

    private static void setNeedUpdateHostsList(boolean z) {
        sNeedUpdateHostList = z;
    }

    private static void updateHostCache(Map<String, Object> map) {
        sHostsCacheInner = new HashMap(map);
    }

    /* JADX WARN: Code restructure failed: missing block: B:125:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x017b, code lost:
    
        r0 = com.xiaomi.micloudsdk.request.utils.CloudRelocationUtils.sUpdateMiCloudHostsLock;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x017d, code lost:
    
        monitor-enter(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x017e, code lost:
    
        com.xiaomi.micloudsdk.request.utils.CloudRelocationUtils.sUpdateStatus = com.xiaomi.micloudsdk.request.utils.CloudRelocationUtils.UpdateStatus.SUCCESS;
        com.xiaomi.micloudsdk.request.utils.CloudRelocationUtils.sUpdateMiCloudHostsLock.notifyAll();
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0187, code lost:
    
        monitor-exit(r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void updateMiCloudHosts(boolean r7) throws com.xiaomi.micloudsdk.exception.CloudServerException {
        /*
            Method dump skipped, instructions count: 584
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.micloudsdk.request.utils.CloudRelocationUtils.updateMiCloudHosts(boolean):void");
    }

    public static String updateRequestHost(String str, boolean z) throws CloudServerException {
        updateMiCloudHosts(z);
        try {
            XLogger.logi("Micloud", "Original URL: " + str + ". ");
            URL url = new URL(str);
            String hostWithScheme = getHostWithScheme(url.getHost());
            if (!TextUtils.isEmpty(hostWithScheme)) {
                XLogger.logi("Micloud", "New URL: " + hostWithScheme + ". ");
                URL url2 = new URL(hostWithScheme);
                str = new URL(url2.getProtocol(), url2.getHost(), url.getFile()).toString();
            }
        } catch (MalformedURLException e) {
            XLogger.error("Micloud", "MalformedURLException in updateHost %s", e);
        }
        XLogger.logi("Micloud", "Final URL: " + str + ". ");
        return str;
    }
}
