package com.xiaomi.onetrack.util;

import android.content.Context;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class n {
    private static Object b;
    private static Class<?> c;
    private static Method d;
    private static Method e;
    private static Method f;
    private static Method g;

    static {
        try {
            Class<?> cls = Class.forName("com.android.id.impl.IdProviderImpl");
            c = cls;
            b = cls.newInstance();
            d = c.getMethod("getUDID", Context.class);
            e = c.getMethod("getOAID", Context.class);
            f = c.getMethod("getVAID", Context.class);
            g = c.getMethod("getAAID", Context.class);
        } catch (Exception e2) {
            p.a("IdentifierManager", "reflect exception!", e2);
        }
    }

    private static String a(Context context, Method method) {
        Object obj = b;
        if (obj == null || method == null) {
            return "";
        }
        try {
            Object invoke = method.invoke(obj, context);
            return invoke != null ? (String) invoke : "";
        } catch (Exception e2) {
            p.a("IdentifierManager", "invoke exception!", e2);
            return "";
        }
    }

    public static String b(Context context) {
        return a(context, e);
    }
}
