package miui.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes4.dex */
public class HashUtils {
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";

    private HashUtils() {
    }

    public static String getHash(File file, String str) {
        try {
            return getHash(new BufferedInputStream(new FileInputStream(file)), str);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getHash(InputStream inputStream, String str) {
        try {
            try {
                try {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance(str);
                        byte[] bArr = new byte[8192];
                        while (true) {
                            int read = inputStream.read(bArr);
                            if (read <= 0) {
                                String hexString = toHexString(messageDigest.digest());
                                try {
                                    inputStream.close();
                                    return hexString;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return hexString;
                                }
                            }
                            messageDigest.update(bArr, 0, read);
                        }
                    } catch (NoSuchAlgorithmException e2) {
                        e2.printStackTrace();
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        return null;
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    return null;
                }
            } catch (IOException e4) {
                e4.printStackTrace();
                return null;
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static String getHash(String str, String str2) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(str2);
            messageDigest.update(str.getBytes());
            return toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMD5(File file) {
        return getHash(file, MD5);
    }

    public static String getMD5(InputStream inputStream) {
        return getHash(inputStream, MD5);
    }

    public static String getMD5(String str) {
        return getHash(str, MD5);
    }

    public static String getSHA1(File file) {
        return getHash(file, SHA1);
    }

    public static String getSHA1(InputStream inputStream) {
        return getHash(inputStream, SHA1);
    }

    public static String getSHA1(String str) {
        return getHash(str, SHA1);
    }

    public static String toHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            sb.append(String.format("%02x", Integer.valueOf(b & 255)));
        }
        return sb.toString();
    }
}
