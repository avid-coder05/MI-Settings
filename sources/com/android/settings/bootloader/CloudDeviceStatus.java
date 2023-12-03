package com.android.settings.bootloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.AESUtil;
import com.android.settings.bootloader.Utils;
import com.xiaomi.accountsdk.account.data.ExtendedAuthToken;
import com.xiaomi.security.devicecredential.SecurityDeviceCredentialManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import miui.cloud.net.XHttpClient;
import miui.os.Build;
import miui.yellowpage.Tag;
import org.json.JSONObject;

/* loaded from: classes.dex */
class CloudDeviceStatus {
    private static final String HOST;
    private static final Uri SECURITY_CENTER_CONTENT_URI;
    private static final Uri SECURITY_CENTER_GET_SERINUM_URI;
    private static int sHeartbeatCount;
    private static Utils.RetType sResult;

    static {
        HOST = Build.IS_INTERNATIONAL_BUILD ? "https://unlock.update.intl.miui.com" : "https://unlock.update.miui.com";
        Uri parse = Uri.parse("content://com.miui.securitycenter.provider");
        SECURITY_CENTER_CONTENT_URI = parse;
        SECURITY_CENTER_GET_SERINUM_URI = Uri.withAppendedPath(parse, "getserinum");
        sResult = new Utils.RetType();
        sHeartbeatCount = 0;
    }

    private static boolean analysisResponse(XHttpClient.HttpResponse httpResponse, Context context) {
        JSONObject jSONObject;
        String optString;
        int optInt;
        Log.d("CloudDeviceStatus", "stateCode: " + httpResponse.stateCode);
        Log.d("CloudDeviceStatus", "content: " + httpResponse.content);
        Utils.RetType retType = sResult;
        retType.retCode = 2;
        Object obj = httpResponse.content;
        if (obj == null) {
            if (httpResponse.error != null) {
                retType.retCode = 3;
                Log.e("CloudDeviceStatus", "error: " + httpResponse.error.toString());
            } else {
                retType.retCode = 2;
            }
            return false;
        }
        try {
            jSONObject = (JSONObject) obj;
            optString = jSONObject.optString("result");
            optInt = jSONObject.optInt(Tag.TagWebService.CommonResult.RESULT_CODE, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"ok".equals(optString) && optInt != 0) {
            Utils.RetType retType2 = sResult;
            retType2.retCode = 4;
            retType2.retMsg = String.valueOf(optInt);
            if (Utils.isChineseLocale()) {
                sResult.retMsg = jSONObject.optString("descCN");
            } else {
                sResult.retMsg = jSONObject.optString("descEN");
            }
            if (optInt == 401) {
                sResult.retCode = 401;
                Utils.invalidateAuthToken(context);
            } else if (optInt == 20086) {
                forceReload();
            }
            return false;
        }
        sResult.retCode = 0;
        sHeartbeatCount = jSONObject.optInt(Tag.TagPhone.MARKED_COUNT, 0);
        return true;
    }

    public static Utils.RetType bindAccountWithDevice(Context context) throws Utils.AccountExcepiton {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        String accountName = Utils.getAccountName(context);
        if (accountName == null) {
            Utils.RetType retType = sResult;
            retType.retCode = 1;
            return retType;
        }
        if (Utils.needSimCard()) {
            if (!Utils.isNetworkConnected(context)) {
                Utils.RetType retType2 = sResult;
                retType2.retCode = 2;
                return retType2;
            } else if (!Utils.isMobileConnected(context)) {
                Utils.RetType retType3 = sResult;
                retType3.retCode = 11;
                return retType3;
            } else {
                String imsi = Utils.getImsi(context);
                if (TextUtils.isEmpty(imsi)) {
                    Utils.RetType retType4 = sResult;
                    retType4.retCode = 7;
                    return retType4;
                }
                hashMap.put("imsi1", imsi);
            }
        }
        hashMap.put("userId", accountName);
        hashMap.put("device", Utils.getModDevice());
        hashMap.put("rom_version", Build.VERSION.INCREMENTAL);
        hashMap.put("heartbeat_mode", String.valueOf(2));
        hashMap.put("cloudsp_devId", Utils.getDeviceId(context));
        hashMap.put("cloudsp_cpuId", getHardwardId(context));
        hashMap.put("cloudsp_product", android.os.Build.DEVICE);
        hashMap.put("cloudsp_userId", accountName);
        int i = 0;
        try {
            String securityDeviceId = SecurityDeviceCredentialManager.getSecurityDeviceId();
            hashMap.put("cloudsp_fid", securityDeviceId);
            hashMap.put("cloudsp_nonce", getNonce(context, securityDeviceId));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CloudDeviceStatus", "get paramter error: fid");
            i = 20090;
        }
        byte[] signData = getSignData(hashMap);
        if (signData == null) {
            i = 20091;
            hashMap.put("cloudp_sign", "");
        } else {
            hashMap.put("cloudp_sign", Utils.binToHex(signData).toLowerCase());
        }
        JSONObject jSONObject = new JSONObject(hashMap);
        try {
            jSONObject.put("error_code", i);
        } catch (Exception unused) {
        }
        HashMap hashMap3 = new HashMap();
        hashMap3.put(Tag.TagYellowPage.YID, "miui_sec_android");
        hashMap3.put("data", jSONObject.toString());
        hashMap3.put("sign", getHMacSign("/v1/unlock/applyBind", jSONObject.toString()));
        getCookie(context, hashMap2);
        try {
            String defaultAESKeyPlaintext = AESUtil.getDefaultAESKeyPlaintext();
            Log.i("CloudDeviceStatus", "args: " + AESUtil.encrypt(jSONObject.toString(), defaultAESKeyPlaintext));
            Log.i("CloudDeviceStatus", "headers: " + AESUtil.encrypt(hashMap2.toString(), defaultAESKeyPlaintext));
        } catch (Exception e2) {
            Log.e("CloudDeviceStatus", "encrypt error:" + e2.getMessage());
        }
        try {
            analysisResponse(new XHttpClient().syncPost(HOST + "/v1/unlock/applyBind", hashMap2, hashMap3), context);
        } catch (Exception unused2) {
            sResult.retCode = 2;
            Log.d("CloudDeviceStatus", "post server error!");
        }
        return sResult;
    }

    private static void forceReload() {
        try {
            SecurityDeviceCredentialManager.forceReload();
        } catch (Exception unused) {
        }
    }

    private static void getCookie(Context context, Map<String, List<String>> map) throws Utils.AccountExcepiton {
        try {
            ExtendedAuthToken authToken = Utils.getAuthToken(context);
            String encryptedAccountName = Utils.getEncryptedAccountName(context);
            ArrayList arrayList = new ArrayList();
            arrayList.add("serviceToken=" + authToken.authToken + ";cUserId=" + encryptedAccountName);
            map.put("Cookie", arrayList);
        } catch (Utils.AccountExcepiton e) {
            throw e;
        }
    }

    private static String getHMacSign(String str, String str2) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec("10f29ff413c89c8de02349cb3eb9a5f510f29ff413c89c8de02349cb3eb9a5f5".getBytes(), mac.getAlgorithm()));
            return Utils.binToHex(mac.doFinal(("POST\n" + str + "\ndata=" + str2 + "&sid=miui_sec_android").getBytes())).toLowerCase();
        } catch (Exception unused) {
            return null;
        }
    }

    private static String getHardwardId(Context context) {
        String hardwareIdFromLocal = Utils.getHardwareIdFromLocal();
        if (TextUtils.isEmpty(hardwareIdFromLocal)) {
            Cursor cursor = null;
            try {
                try {
                    try {
                        cursor = context.getContentResolver().query(SECURITY_CENTER_GET_SERINUM_URI, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            hardwareIdFromLocal = cursor.getString(cursor.getColumnIndex("seriNum"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th) {
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        if (hardwareIdFromLocal.toLowerCase().startsWith("0x")) {
            try {
                return new BigInteger(hardwareIdFromLocal.substring(2), 16).toString();
            } catch (Exception e4) {
                e4.printStackTrace();
                return hardwareIdFromLocal;
            }
        }
        return hardwareIdFromLocal;
    }

    private static String getNonce(Context context, String str) {
        String accountName = Utils.getAccountName(context);
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        hashMap.put("cloudsp_devId", Utils.getDeviceId(context));
        hashMap.put("cloudsp_fid", str);
        if (accountName != null) {
            hashMap.put("userId", accountName);
            hashMap.put("cloudsp_userId", accountName);
        }
        try {
            XHttpClient.HttpResponse syncGet = new XHttpClient().syncGet(Utils.encodeGetParamsToUrl(HOST + "/v1/micloud/nonce", hashMap), hashMap2);
            return analysisResponse(syncGet, context) ? ((JSONObject) syncGet.content).optJSONObject("data").optString("nonce") : "";
        } catch (Exception unused) {
            return "";
        }
    }

    private static byte[] getSignData(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("POST&");
        sb.append("/mic/binding/v1/identified/device/account");
        for (Map.Entry entry : new TreeMap(map).entrySet()) {
            if (((String) entry.getKey()).startsWith("cloudsp_")) {
                sb.append("&" + ((String) entry.getKey()) + "=" + ((String) entry.getValue()));
            }
        }
        try {
            return SecurityDeviceCredentialManager.signWithDeviceCredential(sb.toString().getBytes("UTF-8"), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getsignDataForHeartbeat(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (Map.Entry entry : new TreeMap(map).entrySet()) {
            if (z) {
                z = false;
            } else {
                sb.append("&");
            }
            sb.append(((String) entry.getKey()) + "=" + ((String) entry.getValue()));
        }
        try {
            return SecurityDeviceCredentialManager.signWithDeviceCredential(sb.toString().getBytes("UTF-8"), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int sendHeartbeat(Context context) {
        String str;
        try {
            str = SecurityDeviceCredentialManager.getSecurityDeviceId();
        } catch (Exception unused) {
            str = null;
        }
        if (TextUtils.isEmpty(str)) {
            return -20090;
        }
        String accountName = Utils.getAccountName(context);
        if (accountName == null) {
            return -1;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("cpuId", getHardwardId(context));
        hashMap.put("fid", str);
        hashMap.put("product", android.os.Build.DEVICE);
        hashMap.put("uid", accountName);
        byte[] bArr = getsignDataForHeartbeat(hashMap);
        if (bArr == null) {
            return -20091;
        }
        hashMap.put("tzSign", Utils.binToHex(bArr).toLowerCase());
        JSONObject jSONObject = new JSONObject(hashMap);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(Tag.TagYellowPage.YID, "miui_sec_android");
        hashMap2.put("data", jSONObject.toString());
        Log.d("CloudDeviceStatus", "data: " + jSONObject.toString());
        hashMap2.put("sign", getHMacSign("/v1/unlock/deviceHeartbeat", jSONObject.toString()));
        try {
            if (analysisResponse(new XHttpClient().syncPost(HOST + "/v1/unlock/deviceHeartbeat", (Map<String, List<String>>) null, hashMap2), context)) {
                return sHeartbeatCount;
            }
            return -1;
        } catch (Exception unused2) {
            return -2;
        }
    }
}
