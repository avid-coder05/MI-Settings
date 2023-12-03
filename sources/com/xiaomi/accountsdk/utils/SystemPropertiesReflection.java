package com.xiaomi.accountsdk.utils;

import java.lang.reflect.InvocationTargetException;

/* loaded from: classes2.dex */
public class SystemPropertiesReflection {
    public static String get(String str, String str2) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class).invoke(null, str, str2);
    }
}
