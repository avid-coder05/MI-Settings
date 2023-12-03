package com.android.settings;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import miui.util.CoderUtils;

/* loaded from: classes.dex */
public class AESUtil {
    public static String encrypt(String str, String str2) throws Exception {
        if (str2 != null) {
            if (str2.length() == 16) {
                SecretKeySpec secretKeySpec = new SecretKeySpec(str2.getBytes(), "AES");
                Cipher cipher = Cipher.getInstance(CoderUtils.AES_ALGORITHM);
                cipher.init(1, secretKeySpec, new IvParameterSpec("0102030405060708".getBytes()));
                return Base64.encodeToString(cipher.doFinal(str.getBytes()), 2);
            }
            throw new Exception("AES ENCRYPT : sKey's length is not 16");
        }
        throw new Exception("AES ENCRYPT : sKey is null");
    }

    public static String getDefaultAESKeyPlaintext() {
        return "20nr1aobv2xi8ax4";
    }
}
