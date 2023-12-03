package miuix.core.util;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes5.dex */
public class Utf8TextUtils {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class CharRange {
        int length;
        int start;

        CharRange() {
            this.start = -1;
            this.length = -1;
        }

        CharRange(int i, int i2) {
            this.start = -1;
            this.length = -1;
            this.start = i;
            this.length = i2;
        }

        int getEndIndex() {
            return this.start + this.length;
        }

        boolean isValid() {
            return this.start >= 0 && this.length > 0;
        }
    }

    private static CharRange findRange(byte[] bArr, int i, int i2) {
        CharRange charRange = new CharRange();
        if (isValidCharacter(bArr, i, i2)) {
            charRange.start = i;
            charRange.length = i2;
        }
        return charRange;
    }

    private static int getByteCount(byte b) {
        int i = 0;
        for (int i2 = 7; i2 >= 1 && (1 & ((byte) (b >> i2))) != 0; i2--) {
            i++;
        }
        return i;
    }

    private static CharRange getCharRangeAt(byte[] bArr, int i) {
        int byteCount = getByteCount(bArr[i]);
        return byteCount == 0 ? new CharRange(i, 1) : findRange(bArr, i, byteCount);
    }

    private static List<CharRange> getUtf8CharList(byte[] bArr) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (true) {
            if (i >= bArr.length) {
                break;
            }
            CharRange charRangeAt = getCharRangeAt(bArr, i);
            if (!charRangeAt.isValid()) {
                arrayList.clear();
                break;
            }
            arrayList.add(charRangeAt);
            i += charRangeAt.length;
        }
        return arrayList;
    }

    private static boolean isValidCharacter(byte[] bArr, int i, int i2) {
        if (i2 <= 1 || i2 > 6) {
            return false;
        }
        for (int i3 = 1; i3 < i2; i3++) {
            if (getByteCount(bArr[i + i3]) != 1) {
                return false;
            }
        }
        return true;
    }

    public static String truncateByte(String str, int i) {
        try {
            byte[] bytes = str.getBytes();
            List<CharRange> utf8CharList = getUtf8CharList(bytes);
            if (utf8CharList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                while (sb.toString().getBytes().length < i) {
                    sb.append(str.charAt(sb.length()));
                }
                if (sb.toString().getBytes().length > i) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                return sb.toString();
            }
            int length = bytes.length;
            int size = utf8CharList.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                CharRange charRange = utf8CharList.get(size);
                if (charRange.start >= i) {
                    size--;
                } else {
                    length = charRange.getEndIndex();
                    if (length > i) {
                        length = charRange.start;
                    }
                }
            }
            if (length < bytes.length) {
                byte[] bArr = new byte[length];
                System.arraycopy(bytes, 0, bArr, 0, length);
                return new String(bArr, "UTF-8");
            }
            return str;
        } catch (UnsupportedEncodingException e) {
            Log.w("Utf8TextUtils", "failed to get bytes of UTF-8 from " + str + ", " + e);
            return null;
        }
    }
}
