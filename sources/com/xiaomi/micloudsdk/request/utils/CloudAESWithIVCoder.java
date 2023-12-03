package com.xiaomi.micloudsdk.request.utils;

import android.util.Base64;
import android.util.Log;
import com.xiaomi.micloudsdk.exception.CipherException;
import com.xiaomi.micloudsdk.utils.AESWithIVCoder;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class CloudAESWithIVCoder extends AESWithIVCoder {
    public CloudAESWithIVCoder(String str) {
        super(str);
    }

    public static byte[] encryptHMACSha1(byte[] bArr, byte[] bArr2) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
            mac.update(bArr);
            return mac.doFinal();
        } catch (InvalidKeyException e) {
            Log.e("CloudAESWithIVCoder", "encryptHMACSha1 error", e);
            return null;
        } catch (NoSuchAlgorithmException e2) {
            Log.e("CloudAESWithIVCoder", "encryptHMACSha1 error", e2);
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.xiaomi.micloudsdk.utils.AESWithIVCoder, com.xiaomi.micloudsdk.utils.CryptCoder
    public String decrypt(String str) throws CipherException {
        checkThreadIdThrow();
        try {
            JSONObject jSONObject = new JSONObject(str);
            String string = jSONObject.getString("D");
            Object[] split = jSONObject.getString("S").split(":");
            if (split.length == 2) {
                Object obj = split[0];
                String str2 = split[1];
                String encodeToString = Base64.encodeToString(encryptHMACSha1(String.format("%s:%s", obj, new String(string)).getBytes("UTF-8"), Base64.decode(this.aesKey, 0)), 11);
                if (str2.equals(encodeToString)) {
                    return super.decrypt(string);
                }
                throw new CipherException("server hash " + str2 + " and local hash " + encodeToString + " dismatch");
            }
            throw new SecurityException("not 2 columns in security info");
        } catch (UnsupportedEncodingException e) {
            Log.e("CloudAESWithIVCoder", "decrypt error", e);
            throw new CipherException("decrypt error", e);
        } catch (IllegalArgumentException e2) {
            Log.e("CloudAESWithIVCoder", "decrypt error", e2);
            throw new CipherException("decrypt error", e2);
        } catch (NullPointerException e3) {
            Log.e("CloudAESWithIVCoder", "decrypt error", e3);
            throw new CipherException("decrypt error", e3);
        } catch (JSONException e4) {
            Log.e("CloudAESWithIVCoder", "decrypt error", e4);
            throw new CipherException("decrypt error", e4);
        }
    }
}
