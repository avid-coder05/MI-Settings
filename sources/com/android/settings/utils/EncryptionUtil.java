package com.android.settings.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import miui.util.HashUtils;

/* loaded from: classes2.dex */
public class EncryptionUtil {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String byteToBitIntStr(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return "";
        }
        char[] cArr = new char[(bArr.length * 3) - 1];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = bArr[i] & 255;
            int i3 = i * 3;
            char[] cArr2 = HEX_ARRAY;
            cArr[i3] = cArr2[i2 >>> 4];
            cArr[i3 + 1] = cArr2[i2 & 15];
            if (i != bArr.length - 1) {
                cArr[i3 + 2] = '-';
            }
        }
        return "(0x) " + new String(cArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v1 */
    /* JADX WARN: Type inference failed for: r12v3 */
    /* JADX WARN: Type inference failed for: r12v6 */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v2, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r4v4, types: [android.graphics.Bitmap] */
    public static Bitmap decodeImageResourceByKey(Context context, int i, String str) {
        ?? r12;
        InputStream inputStream = 0;
        InputStream inputStream2 = null;
        try {
            try {
                InputStream openRawResource = context.getResources().openRawResource(i);
                if (openRawResource == null) {
                    if (openRawResource != null) {
                        try {
                            openRawResource.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("EncryptionUtil", "is close exception");
                        }
                    }
                    return null;
                }
                try {
                    ArrayList arrayList = new ArrayList();
                    Log.d("EncryptionUtil", "before time " + System.currentTimeMillis());
                    while (true) {
                        int read = openRawResource.read();
                        if (read <= -1) {
                            break;
                        }
                        arrayList.add(Byte.valueOf((byte) read));
                    }
                    Log.d("EncryptionUtil", "after1 time " + System.currentTimeMillis() + " " + arrayList.size());
                    byte[] bArr = new byte[arrayList.size()];
                    Iterator it = arrayList.iterator();
                    int i2 = 0;
                    while (it.hasNext()) {
                        bArr[i2] = ((Byte) it.next()).byteValue();
                        i2++;
                    }
                    Log.d("EncryptionUtil", "after1 time " + System.currentTimeMillis() + " " + arrayList.size());
                    Log.d("EncryptionUtil", "after2 time " + System.currentTimeMillis() + " arr md5: " + byteToBitIntStr(getMd5(bArr)));
                    byte[] encryptDecoder = encryptDecoder(bArr, str);
                    if (encryptDecoder == null) {
                        Log.e("EncryptionUtil", "encryptDecoder get null");
                        try {
                            openRawResource.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            Log.e("EncryptionUtil", "is close exception");
                        }
                        return null;
                    }
                    Log.d("EncryptionUtil", "after encryptDecoder time " + System.currentTimeMillis() + " arr2 md5: " + byteToBitIntStr(getMd5(encryptDecoder)) + " arr2.len=" + encryptDecoder.length);
                    Bitmap decodeByteArray = BitmapFactory.decodeByteArray(encryptDecoder, 0, encryptDecoder.length);
                    StringBuilder sb = new StringBuilder();
                    sb.append("after3 time ");
                    sb.append(System.currentTimeMillis());
                    Log.d("EncryptionUtil", sb.toString());
                    try {
                        openRawResource.close();
                        return decodeByteArray;
                    } catch (IOException e3) {
                        e3.printStackTrace();
                        Log.e("EncryptionUtil", "is close exception");
                        return decodeByteArray;
                    }
                } catch (Exception e4) {
                    e = e4;
                    inputStream2 = openRawResource;
                    r12 = null;
                    e.printStackTrace();
                    if (inputStream2 != null) {
                        try {
                            inputStream2.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                            Log.e("EncryptionUtil", "is close exception");
                        }
                    }
                    inputStream = r12;
                    return inputStream;
                } catch (Throwable th) {
                    th = th;
                    inputStream = openRawResource;
                    if (inputStream != 0) {
                        try {
                            inputStream.close();
                        } catch (IOException e6) {
                            e6.printStackTrace();
                            Log.e("EncryptionUtil", "is close exception");
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e7) {
            e = e7;
            r12 = null;
        }
    }

    public static byte[] encryptDecoder(byte[] bArr, String str) {
        if (bArr == null || bArr.length == 0 || str == null || str.length() == 0 || str.length() > 32) {
            return null;
        }
        byte[] bArr2 = new byte[bArr.length];
        byte key = getKey(str.getBytes());
        int i = 0;
        int i2 = 0;
        for (byte b : bArr) {
            bArr2[i] = (byte) (((b ^ i2) ^ key) & 255);
            i++;
            i2++;
        }
        return bArr2;
    }

    public static byte getKey(byte[] bArr) {
        int i = 0;
        try {
            int length = bArr.length;
            byte b = 0;
            while (i < length) {
                try {
                    b = (byte) ((b ^ bArr[i]) & 255);
                    i++;
                } catch (Exception e) {
                    e = e;
                    i = b;
                    Log.e("EncryptionUtil", " exception " + e);
                    return i == 1 ? (byte) 1 : (byte) 0;
                }
            }
            return b;
        } catch (Exception e2) {
            e = e2;
        }
    }

    public static byte[] getMd5(byte[] bArr) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
            messageDigest.update(bArr, 0, bArr.length);
            return messageDigest.digest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
