package com.xiaomi.onetrack.util;

import android.util.Log;

/* loaded from: classes2.dex */
public class ab {
    public static String a(String str) {
        return a(str, "");
    }

    public static String a(String str, String str2) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class).invoke(null, str, str2);
        } catch (Throwable th) {
            Log.e(p.a("SystemProperties"), "get e" + th.getMessage());
            return str2;
        }
    }
}
