package com.xiaomi.onetrack.e;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.xiaomi.onetrack.util.j;

/* loaded from: classes2.dex */
public class a {
    private static Context a;
    private static Context b;
    private static int c;
    private static String d;
    private static String e;
    private static long f;
    private static volatile boolean g;

    public static Context a() {
        if (j.d(a)) {
            Context context = b;
            if (context != null) {
                return context;
            }
            synchronized (a.class) {
                if (b == null) {
                    b = j.a(a);
                }
            }
            return b;
        }
        return a;
    }

    public static void a(Context context) {
        if (g) {
            return;
        }
        synchronized (a.class) {
            if (g) {
                return;
            }
            a = context;
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(a.getPackageName(), 0);
                c = packageInfo.versionCode;
                d = packageInfo.versionName;
                f = packageInfo.lastUpdateTime;
                e = a.getPackageName();
            } catch (PackageManager.NameNotFoundException e2) {
                e2.printStackTrace();
            }
            g = true;
        }
    }

    public static Context b() {
        return a;
    }

    public static String c() {
        return d;
    }

    public static int d() {
        return c;
    }

    public static String e() {
        return e;
    }

    public static long f() {
        return f;
    }
}
