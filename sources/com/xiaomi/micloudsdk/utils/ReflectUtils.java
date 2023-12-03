package com.xiaomi.micloudsdk.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class ReflectUtils {
    public static Method getMethod(Class cls, String str, Class<?>... clsArr) {
        try {
            return cls.getDeclaredMethod(str, clsArr);
        } catch (NoSuchMethodException unused) {
            return null;
        }
    }

    public static int getStaticFieldIntValue(Class cls, String str) {
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.getInt(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class loadClass(String str) {
        try {
            return ReflectUtils.class.getClassLoader().loadClass(str);
        } catch (ClassNotFoundException unused) {
            return null;
        }
    }
}
