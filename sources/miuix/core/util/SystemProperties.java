package miuix.core.util;

import androidx.annotation.Keep;

@Keep
@Deprecated
/* loaded from: classes5.dex */
public class SystemProperties {
    public static final int PROP_NAME_MAX = 31;
    public static final int PROP_VALUE_MAX = 91;
    private static Class classSystemProperties;
    private static boolean isSupportGet;
    private static boolean isSupportGetBoolean;
    private static boolean isSupportGetInt;
    private static boolean isSupportGetLong;
    private static boolean isSupportSet;

    static {
        try {
            classSystemProperties = Class.forName("android.os.SystemProperties");
        } catch (Exception unused) {
            classSystemProperties = null;
        }
        Class cls = classSystemProperties;
        if (cls != null) {
            boolean z = true;
            try {
                isSupportGet = cls.getMethod("get", String.class, String.class) != null;
            } catch (Exception unused2) {
                isSupportGet = false;
            }
            try {
                isSupportGetInt = classSystemProperties.getMethod("getInt", String.class, Integer.TYPE) != null;
            } catch (Exception unused3) {
                isSupportGetInt = false;
            }
            try {
                isSupportGetLong = classSystemProperties.getMethod("getLong", String.class, Long.TYPE) != null;
            } catch (Exception unused4) {
                isSupportGetLong = false;
            }
            try {
                isSupportGetBoolean = classSystemProperties.getMethod("getBoolean", String.class, Boolean.TYPE) != null;
            } catch (Exception unused5) {
                isSupportGetBoolean = false;
            }
            try {
                if (classSystemProperties.getMethod("set", String.class, String.class) == null) {
                    z = false;
                }
                isSupportSet = z;
            } catch (Exception unused6) {
                isSupportSet = false;
            }
        }
    }

    public static String get(String str) {
        return get(str, "");
    }

    public static String get(String str, String str2) {
        return isSupportGet ? android.os.SystemProperties.get(str, str2) : str2;
    }

    public static boolean getBoolean(String str, boolean z) {
        return isSupportGetBoolean ? android.os.SystemProperties.getBoolean(str, z) : z;
    }

    public static int getInt(String str, int i) {
        return isSupportGetInt ? android.os.SystemProperties.getInt(str, i) : i;
    }

    public static long getLong(String str, long j) {
        return isSupportGetLong ? android.os.SystemProperties.getLong(str, j) : j;
    }

    public static void set(String str, int i) {
        set(str, Integer.toString(i));
    }

    public static void set(String str, long j) {
        set(str, Long.toString(j));
    }

    public static void set(String str, String str2) {
        if (isSupportSet) {
            if (str.length() > 31) {
                throw new IllegalArgumentException("key.length > 31");
            }
            if (str2 != null && str2.length() > 91) {
                throw new IllegalArgumentException("val.length > 91");
            }
            android.os.SystemProperties.set(str, str2);
        }
    }

    public static void set(String str, boolean z) {
        set(str, Boolean.toString(z));
    }
}
