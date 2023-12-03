package com.android.settings.bluetooth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class MiuiAES {
    private static SecretKeySpec secretKey;

    public static byte[] decrypt(byte[] bArr, byte[] bArr2) {
        try {
            setKey(bArr2);
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(2, secretKey);
            return cipher.doFinal(bArr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] encrypt(byte[] bArr, byte[] bArr2) {
        try {
            setKey(bArr2);
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(1, secretKey);
            cipher.getOutputSize(bArr.length);
            return cipher.doFinal(bArr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setKey(byte[] bArr) {
        try {
            secretKey = new SecretKeySpec(bArr, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
