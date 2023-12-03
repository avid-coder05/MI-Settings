package com.android.settings.bootloader;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.xiaomi.accountsdk.account.data.ExtendedAuthToken;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import miui.accounts.ExtraAccountManager;
import miui.cloud.common.XDeviceInfo;
import miui.telephony.SubscriptionManager;
import miui.util.FeatureParser;
import miuix.core.util.IOUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class Utils {

    /* loaded from: classes.dex */
    public static class AccountExcepiton extends Exception {
        public AccountExcepiton(String str) {
            super(str);
        }
    }

    /* loaded from: classes.dex */
    public static class RetType {
        public int retCode = 3;
        public String retMsg = null;
    }

    public static void addAccount(Context context, AccountManagerCallback<Bundle> accountManagerCallback) {
        AccountManager accountManager = AccountManager.get(context);
        if (accountManagerCallback != null) {
            accountManager.addAccount("com.xiaomi", null, null, null, (Activity) context, accountManagerCallback, null);
        }
    }

    public static String binToHex(byte[] bArr) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            int i2 = (bArr[i] >> 4) & 15;
            int i3 = bArr[i] & 15;
            sb.append(cArr[i2]);
            sb.append(cArr[i3]);
        }
        return sb.toString();
    }

    public static String encodeGetParamsToUrl(String str, Map<String, String> map) {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                arrayList.add(URLEncoder.encode(entry.getKey(), "utf-8") + "=" + URLEncoder.encode(entry.getValue(), "utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str + "?" + TextUtils.join("&", arrayList.toArray(new String[0]));
    }

    public static Account getAccount(Context context) {
        Account[] accountsByType = AccountManager.get(context).getAccountsByType("com.xiaomi");
        if (accountsByType.length > 0) {
            return accountsByType[0];
        }
        return null;
    }

    public static String getAccountName(Context context) {
        Account account = getAccount(context);
        if (account != null) {
            return account.name;
        }
        return null;
    }

    public static ExtendedAuthToken getAuthToken(Context context) throws AccountExcepiton {
        Account account = getAccount(context);
        if (account != null) {
            try {
                String string = AccountManager.get(context).getAuthToken(account, "micloudfind", true, null, null).getResult(30000L, TimeUnit.MILLISECONDS).getString("authtoken");
                if (TextUtils.isEmpty(string)) {
                    return null;
                }
                return ExtendedAuthToken.parse(string);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        throw new AccountExcepiton("not found xiaomi account");
    }

    public static String getDeviceId(Context context) {
        XDeviceInfo syncGet = XDeviceInfo.syncGet(context);
        if (syncGet != null) {
            return syncGet.deviceId;
        }
        return null;
    }

    public static String getEncryptedAccountName(Context context) throws AccountExcepiton {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(context);
        if (account != null) {
            return accountManager.getUserData(account, ExtraAccountManager.KEY_ENCRYPTED_USER_ID);
        }
        throw new AccountExcepiton("not found xiaomi account");
    }

    public static String getHardwareIdFromLocal() {
        BufferedReader bufferedReader;
        String str = SystemProperties.get("ro.boot.cpuid", "");
        if (TextUtils.isEmpty(str)) {
            BufferedReader bufferedReader2 = null;
            try {
                bufferedReader = new BufferedReader(new FileReader("/proc/serial_num"), 256);
            } catch (Exception unused) {
            } catch (Throwable th) {
                th = th;
            }
            try {
                str = bufferedReader.readLine();
                IOUtils.closeQuietly((Reader) bufferedReader);
            } catch (Exception unused2) {
                bufferedReader2 = bufferedReader;
                IOUtils.closeQuietly((Reader) bufferedReader2);
                return str;
            } catch (Throwable th2) {
                th = th2;
                bufferedReader2 = bufferedReader;
                IOUtils.closeQuietly((Reader) bufferedReader2);
                throw th;
            }
        }
        return str;
    }

    private static String getHash(String str, String str2) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(str2);
            messageDigest.update(str.getBytes());
            byte[] digest = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                String hexString = Integer.toHexString(b & 255);
                if (hexString.length() == 1) {
                    sb.append("0");
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getImsi(Context context) {
        int defaultDataSlotId = SubscriptionManager.getDefault().getDefaultDataSlotId();
        if (defaultDataSlotId < 0 || defaultDataSlotId > 1) {
            return null;
        }
        String subscriberId = ((TelephonyManager) context.getSystemService("phone")).getSubscriberId(SubscriptionManager.getDefault().getSubscriptionIdForSlot(defaultDataSlotId));
        if (TextUtils.isEmpty(subscriberId)) {
            return null;
        }
        return getHash(subscriberId + "2jkkewm2OPMBEz7yhl1nZ995OMjOKr6q7gm1Dl0T3EwxmycEIcwr8W3tQIwPLqhm", "SHA-256");
    }

    public static String getModDevice() {
        String str = SystemProperties.get("ro.product.mod_device", "");
        return TextUtils.isEmpty(str) ? Build.DEVICE : str;
    }

    public static int getSimState(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        int phoneCount = telephonyManager.getPhoneCount();
        for (int i = 0; i < phoneCount; i++) {
            if (telephonyManager.getSimState(i) == 5) {
                return 3;
            }
        }
        return 1;
    }

    public static void invalidateAuthToken(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        if (accountManager != null) {
            accountManager.invalidateAuthToken("com.xiaomi", "micloudfind");
        }
    }

    public static boolean isAccountLogined(Context context) {
        return getAccount(context) != null;
    }

    public static boolean isChineseLocale() {
        return Locale.CHINESE.getLanguage().equals(Locale.getDefault().getLanguage());
    }

    public static boolean isMobileConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.getType() == 0;
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean needSimCard() {
        return (FeatureParser.getBoolean("is_pad", false) && SystemProperties.getBoolean("ro.radio.noril", false)) ? false : true;
    }
}
