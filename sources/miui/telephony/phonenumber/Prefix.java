package miui.telephony.phonenumber;

import android.text.TextUtils;

/* loaded from: classes4.dex */
public class Prefix {
    public static final String EMPTY = "";
    public static final String PREFIX_10193 = "10193";
    public static final String PREFIX_11808 = "11808";
    public static final String PREFIX_12520 = "12520";
    public static final String PREFIX_125831 = "125831";
    public static final String PREFIX_125832 = "125832";
    public static final String PREFIX_125833 = "125833";
    public static final String PREFIX_12593 = "12593";
    public static final String PREFIX_17900 = "17900";
    public static final String PREFIX_17901 = "17901";
    public static final String PREFIX_17908 = "17908";
    public static final String PREFIX_17909 = "17909";
    public static final String PREFIX_17911 = "17911";
    public static final String PREFIX_17950 = "17950";
    public static final String PREFIX_17951 = "17951";
    public static final String PREFIX_17960 = "17960";
    public static final String PREFIX_17961 = "17961";
    public static final String PREFIX_17968 = "17968";
    public static final String PREFIX_17969 = "17969";
    public static final String PREFIX_17990 = "17990";
    public static final String PREFIX_17991 = "17991";
    public static final String PREFIX_17995 = "17995";
    public static final String PREFIX_17996 = "17996";
    public static final String[] SMS_PREFIXES = new String[0];

    public static boolean isSmsPrefix(String str) {
        if (!TextUtils.isEmpty(str)) {
            for (String str2 : SMS_PREFIXES) {
                if (str2.equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String parse(StringBuffer stringBuffer, int i, int i2) {
        if (i2 <= 0) {
            return "";
        }
        switch (stringBuffer.charAt(i)) {
            case '1':
                if (i2 > 4) {
                    char charAt = stringBuffer.charAt(i + 1);
                    if (charAt != '7') {
                        switch (charAt) {
                            case '0':
                                if (stringBuffer.charAt(i + 2) == '1' && stringBuffer.charAt(i + 3) == '9' && stringBuffer.charAt(i + 4) == '3') {
                                    return PREFIX_10193;
                                }
                                break;
                            case '1':
                                if (stringBuffer.charAt(i + 2) == '8' && stringBuffer.charAt(i + 3) == '0' && stringBuffer.charAt(i + 4) == '8') {
                                    return PREFIX_11808;
                                }
                                break;
                            case '2':
                                if (stringBuffer.charAt(i + 2) == '5') {
                                    if (i2 > 5 && stringBuffer.charAt(i + 3) == '8' && stringBuffer.charAt(i + 4) == '3' && stringBuffer.charAt(i + 5) == '1') {
                                        return "125831";
                                    }
                                    if (i2 > 5 && stringBuffer.charAt(i + 3) == '8' && stringBuffer.charAt(i + 4) == '3' && stringBuffer.charAt(i + 5) == '2') {
                                        return "125832";
                                    }
                                    if (i2 > 5 && stringBuffer.charAt(i + 3) == '8' && stringBuffer.charAt(i + 4) == '3' && stringBuffer.charAt(i + 5) == '3') {
                                        return "125833";
                                    }
                                    if (stringBuffer.charAt(i + 3) == '9' && stringBuffer.charAt(i + 4) == '3') {
                                        return PREFIX_12593;
                                    }
                                }
                                break;
                        }
                    } else if (stringBuffer.charAt(i + 2) == '9') {
                        int i3 = i + 3;
                        if (stringBuffer.charAt(i3) == '0' && stringBuffer.charAt(i + 4) == '0') {
                            return PREFIX_17900;
                        }
                        if (stringBuffer.charAt(i3) == '0' && stringBuffer.charAt(i + 4) == '1') {
                            return PREFIX_17901;
                        }
                        if (stringBuffer.charAt(i3) == '0' && stringBuffer.charAt(i + 4) == '8') {
                            return PREFIX_17908;
                        }
                        if (stringBuffer.charAt(i3) == '0' && stringBuffer.charAt(i + 4) == '9') {
                            return PREFIX_17909;
                        }
                        if (stringBuffer.charAt(i3) == '1' && stringBuffer.charAt(i + 4) == '1') {
                            return PREFIX_17911;
                        }
                        if (stringBuffer.charAt(i3) == '5' && stringBuffer.charAt(i + 4) == '0') {
                            return PREFIX_17950;
                        }
                        if (stringBuffer.charAt(i3) == '5' && stringBuffer.charAt(i + 4) == '1') {
                            return PREFIX_17951;
                        }
                        if (stringBuffer.charAt(i3) == '6' && stringBuffer.charAt(i + 4) == '0') {
                            return PREFIX_17960;
                        }
                        if (stringBuffer.charAt(i3) == '6' && stringBuffer.charAt(i + 4) == '1') {
                            return PREFIX_17961;
                        }
                        if (stringBuffer.charAt(i3) == '6' && stringBuffer.charAt(i + 4) == '8') {
                            return PREFIX_17968;
                        }
                        if (stringBuffer.charAt(i3) == '6' && stringBuffer.charAt(i + 4) == '9') {
                            return PREFIX_17969;
                        }
                        if (stringBuffer.charAt(i3) == '9' && stringBuffer.charAt(i + 4) == '0') {
                            return PREFIX_17990;
                        }
                        if (stringBuffer.charAt(i3) == '9' && stringBuffer.charAt(i + 4) == '1') {
                            return PREFIX_17991;
                        }
                        if (stringBuffer.charAt(i3) == '9' && stringBuffer.charAt(i + 4) == '5') {
                            return PREFIX_17995;
                        }
                        if (stringBuffer.charAt(i3) == '9' && stringBuffer.charAt(i + 4) == '6') {
                            return PREFIX_17996;
                        }
                    }
                }
                break;
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
                if (i2 >= 10 && stringBuffer.charAt(i + 1) == '0') {
                    int i4 = i + 2;
                    if (stringBuffer.charAt(i4) >= '1' && stringBuffer.charAt(i4) <= '9') {
                        return stringBuffer.substring(i, i + 3);
                    }
                }
                break;
        }
        return "";
    }
}
