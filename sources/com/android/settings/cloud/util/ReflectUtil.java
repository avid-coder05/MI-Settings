package com.android.settings.cloud.util;

import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class ReflectUtil {
    public static Object getStaticObjectField(Class<?> cls, String str) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(null);
    }
}
