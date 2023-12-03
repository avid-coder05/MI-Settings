package miui.util;

import android.util.Base64;
import android.view.MiuiWindowManager$LayoutParams;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import miui.provider.ExtraTelephony;
import miui.provider.MiCloudSmsCmd;

/* loaded from: classes4.dex */
public class CoderUtils {
    public static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String[] hexDigits = {"0", "1", "2", ExtraTelephony.Phonelist.TYPE_VIP, ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK, ExtraTelephony.Phonelist.TYPE_CLOUDS_WHITE, ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK, ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_WHITE, "8", "9", "a", "b", "c", MiCloudSmsCmd.TYPE_DISCARD_TOKEN, "e", "f"};

    public static final String base64AesEncode(String str, String str2) {
        byte[] bytes;
        if (str != null && str.length() != 0 && (bytes = str2.getBytes()) != null && bytes.length == 16) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
            try {
                Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
                cipher.init(1, secretKeySpec, new IvParameterSpec("0102030405060708".getBytes()));
                return new String(encodeBase64(cipher.doFinal(str.getBytes())));
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException unused) {
            }
        }
        return null;
    }

    public static final String base6AesDecode(String str, String str2) {
        byte[] bytes;
        if (str != null && str.length() != 0 && (bytes = str2.getBytes()) != null && bytes.length == 16) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
            try {
                Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
                cipher.init(2, secretKeySpec, new IvParameterSpec("0102030405060708".getBytes()));
                byte[] decodeBase64Bytes = decodeBase64Bytes(str);
                if (decodeBase64Bytes == null) {
                    return null;
                }
                return new String(cipher.doFinal(decodeBase64Bytes));
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException unused) {
            }
        }
        return null;
    }

    private static String byteArrayToString(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            stringBuffer.append(byteToHexString(b));
        }
        return stringBuffer.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:0:?, code lost:
    
        r3 = r3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String byteToHexString(byte r3) {
        /*
            if (r3 >= 0) goto L4
            int r3 = r3 + 256
        L4:
            int r0 = r3 / 16
            int r3 = r3 % 16
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String[] r2 = miui.util.CoderUtils.hexDigits
            r0 = r2[r0]
            r1.append(r0)
            r3 = r2[r3]
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.util.CoderUtils.byteToHexString(byte):java.lang.String");
    }

    public static final String decodeBase64(String str) {
        return new String(Base64.decode(str.getBytes(), 0));
    }

    public static final byte[] decodeBase64Bytes(String str) {
        return Base64.decode(str.getBytes(), 0);
    }

    public static final byte[] encodeBase64(String str) {
        return Base64.encode(str.getBytes(), 2);
    }

    public static final byte[] encodeBase64(byte[] bArr) {
        return Base64.encode(bArr, 2);
    }

    public static final byte[] encodeBase64Bytes(String str) {
        return Base64.encode(str.getBytes(), 2);
    }

    public static final String encodeMD5(File file) {
        FileInputStream fileInputStream;
        byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
        try {
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
                    while (true) {
                        int read = fileInputStream.read(bArr);
                        if (read > 0) {
                            messageDigest.update(bArr, 0, read);
                        } else {
                            try {
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    fileInputStream.close();
                    return byteArrayToString(messageDigest.digest());
                } catch (IOException e2) {
                    e2.printStackTrace();
                    try {
                        fileInputStream.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                    return null;
                } catch (NoSuchAlgorithmException e4) {
                    e4.printStackTrace();
                    try {
                        fileInputStream.close();
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                    return null;
                }
            } catch (FileNotFoundException e6) {
                e6.printStackTrace();
                return null;
            }
        } catch (Throwable th) {
            try {
                fileInputStream.close();
            } catch (IOException e7) {
                e7.printStackTrace();
            }
            throw th;
        }
    }

    public static final String encodeMD5(String str) {
        if (str != null && str.length() != 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
                messageDigest.update(str.getBytes());
                return byteArrayToString(messageDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static final String encodeSHA(String str) {
        if (str != null && str.length() != 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(str.getBytes());
                return byteArrayToString(messageDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static final byte[] encodeSHABytes(String str) {
        if (str != null && str.length() != 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(str.getBytes());
                return messageDigest.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
