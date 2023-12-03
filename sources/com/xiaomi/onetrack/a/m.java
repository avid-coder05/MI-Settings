package com.xiaomi.onetrack.a;

import android.text.TextUtils;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;

/* loaded from: classes2.dex */
public class m {
    private static String a = "ConfigProvider";
    private static volatile boolean b;

    public static int a(int i) {
        int i2;
        if (p.b) {
            p.a(a, "debug upload mode, send events immediately");
            return 0;
        }
        try {
            i2 = d.c().get(Integer.valueOf(i + 1)).intValue();
        } catch (Exception unused) {
            i2 = 60000;
        }
        p.a(a, "getUploadInterval " + i2);
        return i2;
    }

    public static synchronized void a(boolean z) {
        synchronized (m.class) {
            b = z;
        }
    }

    public static boolean a() {
        try {
            String[] b2 = com.xiaomi.onetrack.c.f.a().b();
            return (!TextUtils.isEmpty(b2[0]) && !TextUtils.isEmpty(b2[1])) && !q.a(a);
        } catch (Exception e) {
            p.a(a, "ConfigProvider.available", e);
            return false;
        }
    }

    public static synchronized boolean b() {
        boolean z;
        synchronized (m.class) {
            z = b;
        }
        return z;
    }

    public static boolean c() {
        return com.xiaomi.onetrack.f.c.a();
    }
}
