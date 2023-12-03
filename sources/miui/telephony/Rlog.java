package miui.telephony;

import miui.provider.MiCloudSmsCmd;
import miui.reflect.Method;

/* loaded from: classes4.dex */
class Rlog {
    private Rlog() {
    }

    public static void e(String str, String str2) {
        try {
            Class<?> cls = Class.forName("android.telephony.Rlog");
            Method.of(cls, "e", Integer.TYPE, new Class[]{String.class, String.class}).invoke(cls, (Object) null, new Object[]{str, str2});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void i(String str, String str2) {
        try {
            Class<?> cls = Class.forName("android.telephony.Rlog");
            Method.of(cls, "i", Integer.TYPE, new Class[]{String.class, String.class}).invoke(cls, (Object) null, new Object[]{str, str2});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void w(String str, String str2) {
        try {
            Class<?> cls = Class.forName("android.telephony.Rlog");
            Method.of(cls, MiCloudSmsCmd.TYPE_WIPE, Integer.TYPE, new Class[]{String.class, String.class}).invoke(cls, (Object) null, new Object[]{str, str2});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
