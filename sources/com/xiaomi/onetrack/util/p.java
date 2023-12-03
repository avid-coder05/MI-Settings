package com.xiaomi.onetrack.util;

import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.onetrack.e.a;

/* loaded from: classes2.dex */
public class p {
    public static boolean a;
    public static boolean b;
    public static boolean c;
    private static boolean k;
    private static boolean l;
    private static boolean m;
    private static boolean n;

    public static String a(String str) {
        return "OneTrack-Api-" + str;
    }

    public static void a() {
        try {
            String e = a.e();
            String a2 = ab.a("debug.onetrack.log");
            boolean z = true;
            l = (TextUtils.isEmpty(a2) || TextUtils.isEmpty(e) || !TextUtils.equals(e, a2)) ? false : true;
            String a3 = ab.a("debug.onetrack.upload");
            b = (TextUtils.isEmpty(a3) || TextUtils.isEmpty(e) || !TextUtils.equals(e, a3)) ? false : true;
            String a4 = ab.a("debug.onetrack.test");
            if (TextUtils.isEmpty(a4) || TextUtils.isEmpty(e) || !TextUtils.equals(e, a4)) {
                z = false;
            }
            n = z;
            b();
            c();
        } catch (Exception e2) {
            Log.e("OneTrackSdk", "LogUtil static initializer: " + e2.toString());
        }
        Log.d("OneTrackSdk", "log on: " + l + ", quick upload on: " + b);
    }

    public static void a(String str, String str2) {
        if (a) {
            a(a(str), str2, 3);
        }
    }

    private static void a(String str, String str2, int i) {
        if (str2 == null) {
            return;
        }
        int i2 = 0;
        while (i2 <= str2.length() / 3000) {
            int i3 = i2 * 3000;
            i2++;
            int min = Math.min(str2.length(), i2 * 3000);
            if (i3 < min) {
                String substring = str2.substring(i3, min);
                if (i == 0) {
                    Log.e(str, substring);
                } else if (i == 1) {
                    Log.w(str, substring);
                } else if (i == 2) {
                    Log.i(str, substring);
                } else if (i == 3) {
                    Log.d(str, substring);
                } else if (i == 4) {
                    Log.v(str, substring);
                }
            }
        }
    }

    public static void a(String str, String str2, Throwable th) {
        if (a) {
            Log.d(a(str), str2, th);
        }
    }

    public static void a(boolean z) {
        k = z;
        b();
    }

    private static void b() {
        a = k || l;
        Log.d("OneTrackSdk", "updateDebugSwitch sEnable: " + a + " sDebugMode：" + k + " sDebugProperty：" + l);
    }

    public static void b(String str, String str2) {
        if (a) {
            a(a(str), str2, 0);
        }
    }

    public static void b(String str, String str2, Throwable th) {
        if (a) {
            Log.e(a(str), str2, th);
        }
    }

    private static void c() {
        c = m || n;
        Log.d("OneTrackSdk", "updateTestSwitch sTestEnable: " + c + " sTestMode：" + m + " sTestProperty：" + n);
    }

    public static void c(String str, String str2) {
        if (a) {
            a(a(str), str2, 1);
        }
    }

    public static void c(String str, String str2, Throwable th) {
        if (a) {
            Log.w(a(str), str2, th);
        }
    }

    public static void d(String str, String str2) {
        if (a) {
            a(a(str), str2, 2);
        }
    }
}
