package com.mi.mibridge;

import android.content.Context;
import android.util.Log;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class MiBridge {
    public static Method a;
    public static Method b;
    public static Method c;
    public static Method d;
    public static Method e;
    public static Method f;
    public static Class g;
    public static PathClassLoader h;
    public static Constructor<Class> i;
    public static Object j;

    static {
        try {
            PathClassLoader pathClassLoader = new PathClassLoader("/system/framework/MiuiBooster.jar", ClassLoader.getSystemClassLoader());
            h = pathClassLoader;
            Class loadClass = pathClassLoader.loadClass("com.miui.performance.MiuiBooster");
            g = loadClass;
            i = loadClass.getConstructor(new Class[0]);
            Class<?> cls = Integer.TYPE;
            a = g.getDeclaredMethod("checkPermission", String.class, cls);
            b = g.getDeclaredMethod("checkPermission", Context.class, String.class, cls, String.class);
            c = g.getDeclaredMethod("requestCpuHighFreq", cls, cls, cls);
            d = g.getDeclaredMethod("cancelCpuHighFreq", cls);
            e = g.getDeclaredMethod("requestThreadPriority", cls, cls, cls);
            f = g.getDeclaredMethod("cancelThreadPriority", cls, cls);
        } catch (Exception e2) {
            Log.e("MiBridge", "MiBridge() : Load Class Exception: " + e2);
        }
        try {
            Constructor<Class> constructor = i;
            if (constructor != null) {
                j = constructor.newInstance(new Object[0]);
            }
        } catch (Exception e3) {
            Log.e("MiBridge", "MiBridge() : newInstance Exception:" + e3);
        }
    }

    public static boolean checkPermission(String str, int i2) {
        try {
            return ((Boolean) a.invoke(j, str, Integer.valueOf(i2))).booleanValue();
        } catch (Exception e2) {
            Log.e("MiBridge", "check permission failed , e:" + e2.toString());
            return false;
        }
    }

    public static int requestCpuHighFreq(int i2, int i3, int i4) {
        try {
            return ((Integer) c.invoke(j, Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4))).intValue();
        } catch (Exception e2) {
            Log.e("MiBridge", "request cpu high failed , e:" + e2.toString());
            return -1;
        }
    }
}
