package com.xiaomi.micloudsdk.utils;

import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import miui.util.HashUtils;

/* loaded from: classes2.dex */
public class DeviceIdHasher {
    public static String hashDeviceInfo(String str, int i) {
        if (str == null) {
            return null;
        }
        try {
            return Base64.encodeToString(MessageDigest.getInstance(HashUtils.SHA1).digest(str.getBytes()), i).substring(0, 16);
        } catch (NoSuchAlgorithmException unused) {
            throw new IllegalStateException("failed to init SHA1 digest");
        }
    }
}
