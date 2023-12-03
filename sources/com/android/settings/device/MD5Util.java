package com.android.settings.device;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import miui.util.HashUtils;

/* loaded from: classes.dex */
public class MD5Util {
    private static final char[] strDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static char[] sTemp = new char[2];

    /* JADX WARN: Code restructure failed: missing block: B:0:?, code lost:
    
        r4 = r4;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String byteToArrayString(byte r4) {
        /*
            if (r4 >= 0) goto L4
            int r4 = r4 + 256
        L4:
            char[] r0 = com.android.settings.device.MD5Util.sTemp
            r1 = 0
            char[] r2 = com.android.settings.device.MD5Util.strDigits
            int r3 = r4 / 16
            char r3 = r2[r3]
            r0[r1] = r3
            r1 = 1
            int r4 = r4 % 16
            char r4 = r2[r4]
            r0[r1] = r4
            java.lang.String r4 = new java.lang.String
            r4.<init>(r0)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.device.MD5Util.byteToArrayString(byte):java.lang.String");
    }

    private static String byteToString(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            stringBuffer.append(byteToArrayString(b));
        }
        return stringBuffer.toString();
    }

    public static String encode(String str) {
        String str2 = null;
        try {
            String str3 = new String(str);
            try {
                return byteToString(MessageDigest.getInstance(HashUtils.MD5).digest(str.getBytes()));
            } catch (NoSuchAlgorithmException e) {
                e = e;
                str2 = str3;
                e.printStackTrace();
                return str2;
            }
        } catch (NoSuchAlgorithmException e2) {
            e = e2;
        }
    }
}
