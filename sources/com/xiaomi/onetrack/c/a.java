package com.xiaomi.onetrack.c;

import com.xiaomi.onetrack.util.p;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes2.dex */
public class a {
    private static KeyGenerator d;

    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            d = keyGenerator;
            keyGenerator.init(128);
        } catch (Exception e) {
            p.b(p.a("AES"), "AesUtil e", e);
        }
    }

    public static String a(String str) {
        try {
            char[] charArray = (str + b.a).toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                for (int i2 = 0; i2 < charArray.length - 1; i2++) {
                    if (charArray[i] < charArray[i2]) {
                        char c = charArray[i];
                        charArray[i] = charArray[i2];
                        charArray[i2] = c;
                    }
                }
            }
            return d.h(new String(charArray));
        } catch (Exception unused) {
            p.b("AES", "encodeFromSalt ");
            return "";
        }
    }

    public static byte[] a() {
        return d.generateKey().getEncoded();
    }

    public static byte[] a(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (Exception e) {
            p.b(p.a("AES"), "encrypt exception:", e);
            return null;
        }
    }

    public static byte[] b(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (Exception e) {
            p.b("AES", "decrypt exception:", e);
            p.b("AES", "content len=" + bArr.length + ", passwd len=" + bArr2.length);
            return null;
        }
    }
}
