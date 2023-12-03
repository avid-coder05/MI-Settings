package com.android.settings.privacy;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Base64;
import android.util.Log;
import com.android.settings.privacy.PrivacyReflectUtils;
import com.android.settings.privacy.nonce.NonceFactory;
import com.xiaomi.accountsdk.request.AccessDeniedException;
import com.xiaomi.accountsdk.request.AuthenticationFailureException;
import com.xiaomi.accountsdk.request.SimpleRequest;
import com.xiaomi.accountsdk.utils.CloudCoder;
import com.xiaomi.passport.servicetoken.IServiceTokenUtil;
import com.xiaomi.passport.servicetoken.ServiceTokenResult;
import com.xiaomi.passport.servicetoken.ServiceTokenUtilFacade;
import com.xiaomi.security.devicecredential.SecurityDeviceCredentialManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import miui.accounts.ExtraAccountManager;
import miui.os.Build;
import miui.telephony.CloudTelephonyManager;
import miui.telephony.exception.IllegalDeviceException;
import miui.util.HashUtils;
import miui.yellowpage.Tag;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class PrivacyNetUtils {
    private static byte[] getBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes();
        }
    }

    private static String getExtraString(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            if (isMultiSimEnabled()) {
                jSONObject.put("imei1sha2", EncryptUtil.SHA256(getImei(0)));
                jSONObject.put("imei2sha2", EncryptUtil.SHA256(getImei(1)));
            } else {
                jSONObject.put("imei1sha2", EncryptUtil.SHA256(getImei()));
                jSONObject.put("imei2sha2", "");
            }
            int multiSimCount = CloudTelephonyManager.getMultiSimCount();
            for (int i = 0; i < multiSimCount; i++) {
                if (CloudTelephonyManager.isSimInserted(context, i)) {
                    jSONObject.put("sim" + (i + 1) + "sign", CloudCoder.hashDeviceInfo(CloudTelephonyManager.getSimId(context, i)));
                } else {
                    jSONObject.put("sim" + (i + 1) + "sign", "");
                }
            }
            return jSONObject.toString();
        } catch (Exception e) {
            Log.e("PrivacyNetUtils", " getExtraString :  ", e);
            return "";
        }
    }

    public static String getFidNonceSign(String str) {
        try {
            return Base64.encodeToString(SecurityDeviceCredentialManager.sign(1, str.getBytes("UTF-8"), true), 10);
        } catch (Exception e) {
            Log.e("PrivacyNetUtils", " getFidNonceSign :  ", e);
            return "";
        }
    }

    public static String getFidNonceValue(Context context, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("appModule", str);
            if (isMultiSimEnabled()) {
                jSONObject.put("imei1sign", getMd5Digest(getImei(0)));
                jSONObject.put("imei2sign", getMd5Digest(getImei(1)));
            } else {
                jSONObject.put("imei1sign", getMd5Digest(getImei()));
                jSONObject.put("imei2sign", "");
            }
            jSONObject.put("macsign", getMd5Digest(getMacAddress(context)));
            jSONObject.put("devName", SystemProperties.get("ro.product.device", ""));
            jSONObject.put("region", Build.getRegion());
            jSONObject.put("language", Locale.getDefault().toString());
            jSONObject.put("miuiVer", Build.VERSION.INCREMENTAL);
            jSONObject.put("extra", getExtraString(context));
            jSONObject.put("nonce", NonceFactory.generateNonce());
            return jSONObject.toString();
        } catch (Exception e) {
            Log.e("PrivacyNetUtils", " getFidNonceValue :  ", e);
            return "";
        }
    }

    public static String getImei() {
        return PrivacyReflectUtils.ReflAgent.getClass("miui.telephony.TelephonyManager").callStatic("getDefault", null, new Object[0]).setResultToSelf().call("getImei", null, new Object[0]).stringResult();
    }

    public static String getImei(int i) {
        return PrivacyReflectUtils.ReflAgent.getClass("miui.telephony.TelephonyManager").callStatic("getDefault", null, new Object[0]).setResultToSelf().call("getImeiForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i)).stringResult();
    }

    public static String getMacAddress(Context context) {
        WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        return connectionInfo != null ? connectionInfo.getMacAddress() : "";
    }

    public static String getMd5Digest(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
            messageDigest.update(getBytes(str));
            return String.format("%1$032X", new BigInteger(1, messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isMultiSimEnabled() {
        return PrivacyReflectUtils.ReflAgent.getClass("miui.telephony.TelephonyManager").callStatic("getDefault", null, new Object[0]).setResultToSelf().call("isMultiSimEnabled", null, new Object[0]).booleanResult();
    }

    public static boolean isXiaomiAccountLogin(Context context) {
        return ExtraAccountManager.getXiaomiAccount(context) != null;
    }

    public static boolean post(Context context, String str, String str2) {
        ServiceTokenResult serviceTokenResult;
        boolean isXiaomiAccountLogin = isXiaomiAccountLogin(context);
        IServiceTokenUtil iServiceTokenUtil = null;
        ServiceTokenResult serviceTokenResult2 = null;
        iServiceTokenUtil = null;
        try {
            try {
                HashMap hashMap = new HashMap();
                String fidNonceValue = getFidNonceValue(context, str2);
                hashMap.put("fidNonce", Base64.encodeToString(fidNonceValue.getBytes("UTF-8"), 10));
                hashMap.put("fidNonceSign", getFidNonceSign(fidNonceValue));
                HashMap hashMap2 = new HashMap();
                hashMap2.put("deviceId", CloudCoder.hashDeviceInfo(CloudTelephonyManager.blockingGetDeviceId(context)));
                try {
                    if (SecurityDeviceCredentialManager.isThisDeviceSupported()) {
                        hashMap2.put("fid", SecurityDeviceCredentialManager.getSecurityDeviceId());
                    }
                } catch (Exception e) {
                    Log.e("PrivacyNetUtils", "get fid error  ", e);
                }
                if (isXiaomiAccountLogin) {
                    IServiceTokenUtil buildMiuiServiceTokenUtil = ServiceTokenUtilFacade.getInstance().buildMiuiServiceTokenUtil();
                    try {
                        serviceTokenResult2 = buildMiuiServiceTokenUtil.getServiceToken(context, "app_auth").get();
                        hashMap2.put("serviceToken", serviceTokenResult2.serviceToken);
                        hashMap2.put("app_auth_ph", serviceTokenResult2.ph);
                        hashMap2.put("cUserId", serviceTokenResult2.cUserId);
                        serviceTokenResult = serviceTokenResult2;
                        iServiceTokenUtil = buildMiuiServiceTokenUtil;
                    } catch (AuthenticationFailureException e2) {
                        e = e2;
                        serviceTokenResult = serviceTokenResult2;
                        iServiceTokenUtil = buildMiuiServiceTokenUtil;
                        Log.e("PrivacyNetUtils", " post AuthenticationFailureException:  ", e);
                        if (isXiaomiAccountLogin) {
                            try {
                                iServiceTokenUtil.invalidateServiceToken(context, serviceTokenResult);
                            } catch (Exception unused) {
                            }
                        }
                        return false;
                    }
                } else {
                    serviceTokenResult = null;
                }
                try {
                    SimpleRequest.StringContent postAsString = SimpleRequest.postAsString(str, hashMap, hashMap2, true);
                    if (postAsString != null) {
                        Log.d("PrivacyNetUtils", " response :  " + postAsString.toString());
                        if (postAsString.getHttpCode() == 200) {
                            JSONObject jSONObject = new JSONObject(postAsString.getBody());
                            int optInt = jSONObject.optInt(Tag.TagWebService.CommonResult.RESULT_CODE, -10000);
                            jSONObject.optString("description");
                            return optInt == 0;
                        }
                    }
                } catch (AuthenticationFailureException e3) {
                    e = e3;
                    Log.e("PrivacyNetUtils", " post AuthenticationFailureException:  ", e);
                    if (isXiaomiAccountLogin && iServiceTokenUtil != null && serviceTokenResult != null) {
                        iServiceTokenUtil.invalidateServiceToken(context, serviceTokenResult);
                    }
                    return false;
                }
            } catch (AuthenticationFailureException e4) {
                e = e4;
                serviceTokenResult = null;
            }
        } catch (AccessDeniedException e5) {
            Log.e("PrivacyNetUtils", " post AccessDeniedException:  ", e5);
        } catch (IOException e6) {
            Log.e("PrivacyNetUtils", " post IOException:  ", e6);
        } catch (IllegalDeviceException e7) {
            Log.e("PrivacyNetUtils", " post IllegalDeviceException:  ", e7);
        } catch (JSONException e8) {
            Log.e("PrivacyNetUtils", " post JSONException:  ", e8);
        } catch (Exception e9) {
            Log.e("PrivacyNetUtils", " post Exception:  ", e9);
        }
        return false;
    }
}
