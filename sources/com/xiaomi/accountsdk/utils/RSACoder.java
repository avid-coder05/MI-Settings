package com.xiaomi.accountsdk.utils;

import android.text.TextUtils;
import com.xiaomi.accountsdk.account.exception.CryptoException;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/* loaded from: classes2.dex */
public class RSACoder {
    public static byte[] cipher(byte[] bArr, Key key, int i) throws CryptoException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            cipher.init(i, key);
            return cipher.doFinal(bArr);
        } catch (InvalidKeyException e) {
            throw new CryptoException(e.getCause());
        } catch (NoSuchAlgorithmException e2) {
            throw new CryptoException(e2.getCause());
        } catch (BadPaddingException e3) {
            throw new CryptoException(e3.getCause());
        } catch (IllegalBlockSizeException e4) {
            throw new CryptoException(e4.getCause());
        } catch (NoSuchPaddingException e5) {
            throw new CryptoException(e5.getCause());
        }
    }

    public static byte[] encrypt(byte[] bArr, Key key) throws CryptoException {
        try {
            return cipher(bArr, key, 1);
        } catch (CryptoException e) {
            throw new CryptoException("encrypt", e.getCause());
        }
    }

    public static PublicKey getCertificatePublicKey(String str) throws CryptoException {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalStateException("public key should not be empty");
        }
        try {
            return getCertificatePublicKey(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new CryptoException("getPublicKey", e.getCause());
        }
    }

    public static PublicKey getCertificatePublicKey(byte[] bArr) throws CryptoException {
        if (bArr != null) {
            try {
                return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr))).getPublicKey();
            } catch (CertificateException e) {
                throw new CryptoException("getPublicKey", e.getCause());
            }
        }
        throw new IllegalStateException("public key bytes should not be empty");
    }
}
