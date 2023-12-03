package com.xiaomi.onetrack.c;

import android.os.Build;
import com.xiaomi.onetrack.util.p;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import miui.util.RSAUtils;

/* loaded from: classes2.dex */
public class e {
    private static RSAPublicKey a(String str) throws Exception {
        return (RSAPublicKey) (Build.VERSION.SDK_INT >= 28 ? KeyFactory.getInstance(RSAUtils.KEY_RSA) : KeyFactory.getInstance(RSAUtils.KEY_RSA, "BC")).generatePublic(new X509EncodedKeySpec(c.a(str)));
    }

    public static byte[] a(byte[] bArr) throws Exception {
        try {
            RSAPublicKey a = a("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCiH0r18h2G+lOzZz0mSZT9liZY\r6ibWUv/biAioduf0zuRbWUYGb3pHobyCOaw2LpVnlf8CeCYtbRJhxL9skOyoU1Qa\rwGtoJzvVR4GbCo1MBTmZ8XThMprr0unRfzsu9GNV4+twciOdS2cNJB7INcwAYBFQ\r9vKpgXFoEjWRhIgwMwIDAQAB\r");
            Cipher cipher = Cipher.getInstance(RSAUtils.CIPHER_RSA, "BC");
            cipher.init(1, a);
            return cipher.doFinal(bArr);
        } catch (Exception e) {
            p.b(p.a("RsaUtils"), "RsaUtils encrypt exception:", e);
            return null;
        }
    }
}
