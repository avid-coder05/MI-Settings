package com.xiaomi.accountsdk.utils;

import com.xiaomi.accountsdk.account.XMPassport;
import com.xiaomi.accountsdk.hasheddeviceidlib.DeviceIdHasher;

/* loaded from: classes2.dex */
public class CloudCoder {
    private static final Integer INT_0;
    private static final String URL_REMOTE_DECRYPT;
    private static final String URL_REMOTE_ENCRYPT;

    static {
        StringBuilder sb = new StringBuilder();
        String str = XMPassport.URL_ACCOUNT_SAFE_API_BASE;
        sb.append(str);
        sb.append("/user/getSecurityToken");
        URL_REMOTE_ENCRYPT = sb.toString();
        URL_REMOTE_DECRYPT = str + "/user/getPlanText";
        INT_0 = 0;
    }

    public static String hashDeviceInfo(String str) {
        return DeviceIdHasher.hashDeviceInfo(str, 8);
    }
}
