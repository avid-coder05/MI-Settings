package com.miui.maml.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public class ReflectionHelper {
    static HashMap<String, Class<?>> PRIMITIVE_TYPE;
    private static Map<String, Constructor> sConstructorCache;
    private static Map<String, Field> sFieldCache;
    private static Method sForNameMethod;
    private static Method sGetDeclaredConstructorMethod;
    private static Method sGetDeclaredFieldMethod;
    private static Method sGetDeclaredMethodMethod;
    private static Method sGetMethod;
    private static Method sInvokeMethod;
    private static Map<String, Method> sMethodCache;
    private static Method sNewInstanceMethod;
    private static Method sSetAccessibleMethod;
    private static Method sSetMethod;

    static {
        HashMap<String, Class<?>> hashMap = new HashMap<>();
        PRIMITIVE_TYPE = hashMap;
        hashMap.put("byte", Byte.TYPE);
        PRIMITIVE_TYPE.put("short", Short.TYPE);
        PRIMITIVE_TYPE.put("int", Integer.TYPE);
        PRIMITIVE_TYPE.put("long", Long.TYPE);
        PRIMITIVE_TYPE.put("char", Character.TYPE);
        PRIMITIVE_TYPE.put("boolean", Boolean.TYPE);
        PRIMITIVE_TYPE.put("float", Float.TYPE);
        PRIMITIVE_TYPE.put("double", Double.TYPE);
        PRIMITIVE_TYPE.put("byte[]", byte[].class);
        PRIMITIVE_TYPE.put("short[]", short[].class);
        PRIMITIVE_TYPE.put("int[]", int[].class);
        PRIMITIVE_TYPE.put("long[]", long[].class);
        PRIMITIVE_TYPE.put("char[]", char[].class);
        PRIMITIVE_TYPE.put("boolean[]", boolean[].class);
        PRIMITIVE_TYPE.put("float[]", float[].class);
        PRIMITIVE_TYPE.put("double[]", double[].class);
        sMethodCache = new HashMap();
        sFieldCache = new HashMap();
        sConstructorCache = new HashMap();
        sInvokeMethod = null;
        sGetDeclaredFieldMethod = null;
        sGetDeclaredMethodMethod = null;
        sSetAccessibleMethod = null;
        sGetDeclaredConstructorMethod = null;
        sNewInstanceMethod = null;
        sForNameMethod = null;
        sSetMethod = null;
        sGetMethod = null;
    }

    private static Class forNameInternal(String str) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (sForNameMethod == null) {
            sForNameMethod = Class.class.getMethod("forName", String.class);
        }
        return (Class) sForNameMethod.invoke(null, str);
    }

    private static String generateConstructorCacheKey(Class<?> cls, Class<?>... clsArr) {
        return cls.toString() + "/" + Arrays.toString(clsArr);
    }

    private static String generateFieldCacheKey(Class<?> cls, String str) {
        return cls.toString() + "/" + str;
    }

    private static String generateMethodCacheKey(Class<?> cls, String str, Class<?>[] clsArr) {
        return cls.toString() + "/" + str + "/" + Arrays.toString(clsArr);
    }

    public static Class<?> getClass(String str) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return forNameInternal(str);
    }

    public static Constructor getConstructor(Class<?> cls, Class<?>... clsArr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String generateConstructorCacheKey = generateConstructorCacheKey(cls, clsArr);
        Constructor constructor = sConstructorCache.get(generateConstructorCacheKey);
        if (constructor == null) {
            Constructor declaredConstructorInternal = getDeclaredConstructorInternal(cls, clsArr);
            setAccessibleInternal(declaredConstructorInternal, true);
            sConstructorCache.put(generateConstructorCacheKey, declaredConstructorInternal);
            return declaredConstructorInternal;
        }
        return constructor;
    }

    public static <T> T getConstructorInstance(Class<?> cls, Class<?>[] clsArr, Object... objArr) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Constructor constructor = getConstructor(cls, clsArr);
        if (constructor == null) {
            return null;
        }
        return (T) newInstanceInternal(constructor, objArr);
    }

    private static Constructor getDeclaredConstructorInternal(Object obj, Class<?>... clsArr) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (sGetDeclaredConstructorMethod == null) {
            sGetDeclaredConstructorMethod = Class.class.getMethod("getDeclaredConstructor", Class[].class);
        }
        return (Constructor) sGetDeclaredConstructorMethod.invoke(obj, clsArr);
    }

    private static Field getDeclaredFieldInternal(Object obj, String str) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (sGetDeclaredFieldMethod == null) {
            sGetDeclaredFieldMethod = Class.class.getMethod("getDeclaredField", String.class);
        }
        return (Field) sGetDeclaredFieldMethod.invoke(obj, str);
    }

    private static Method getDeclaredMethodInternal(Object obj, String str, Class<?>... clsArr) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (sGetDeclaredMethodMethod == null) {
            sGetDeclaredMethodMethod = Class.class.getMethod("getDeclaredMethod", String.class, Class[].class);
        }
        return (Method) sGetDeclaredMethodMethod.invoke(obj, str, clsArr);
    }

    public static Field getField(Class<?> cls, String str) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String generateFieldCacheKey = generateFieldCacheKey(cls, str);
        Field field = sFieldCache.get(generateFieldCacheKey);
        if (field == null) {
            Field declaredFieldInternal = getDeclaredFieldInternal(cls, str);
            setAccessibleInternal(declaredFieldInternal, true);
            sFieldCache.put(generateFieldCacheKey, declaredFieldInternal);
            return declaredFieldInternal;
        }
        return field;
    }

    public static <T> T getFieldValue(Class<?> cls, Object obj, String str) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = getField(cls, str);
        if (field == null) {
            return null;
        }
        return (T) getInternal(field, obj);
    }

    private static Object getInternal(Object obj, Object obj2) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (sGetMethod == null) {
            sGetMethod = Field.class.getMethod("get", Object.class);
        }
        return sGetMethod.invoke(obj, obj2);
    }

    public static Method getMethod(Class<?> cls, String str, Class<?>... clsArr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String generateMethodCacheKey = generateMethodCacheKey(cls, str, clsArr);
        Method method = sMethodCache.get(generateMethodCacheKey);
        if (method == null) {
            Method declaredMethodInternal = getDeclaredMethodInternal(cls, str, clsArr);
            setAccessibleInternal(declaredMethodInternal, true);
            sMethodCache.put(generateMethodCacheKey, declaredMethodInternal);
            return declaredMethodInternal;
        }
        return method;
    }

    public static void invoke(Class<?> cls, Object obj, String str, Class<?>[] clsArr, Object... objArr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = getMethod(cls, str, clsArr);
        if (method != null) {
            invokeInternal(method, obj, objArr);
        }
    }

    private static Object invokeInternal(Object obj, Object... objArr) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (sInvokeMethod == null) {
            sInvokeMethod = Method.class.getMethod("invoke", Object.class, Object[].class);
        }
        return sInvokeMethod.invoke(obj, objArr);
    }

    public static <T> T invokeObject(Class<?> cls, Object obj, String str, Class<?>[] clsArr, Object... objArr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = getMethod(cls, str, clsArr);
        if (method != null) {
            return (T) invokeInternal(method, obj, objArr);
        }
        return null;
    }

    private static <T> T newInstanceInternal(Object obj, Object... objArr) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (sNewInstanceMethod == null) {
            sNewInstanceMethod = Constructor.class.getMethod("newInstance", Object[].class);
        }
        return (T) sNewInstanceMethod.invoke(obj, objArr);
    }

    private static void setAccessibleInternal(Object obj, boolean z) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (sSetAccessibleMethod == null) {
            sSetAccessibleMethod = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
        }
        sSetAccessibleMethod.invoke(obj, Boolean.valueOf(z));
    }

    public static void setFieldValue(Class<?> cls, Object obj, String str, Object obj2) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = getField(cls, str);
        if (field != null) {
            setInternal(field, obj, obj2);
        }
    }

    private static void setInternal(Object obj, Object obj2, Object obj3) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (sSetMethod == null) {
            sSetMethod = Field.class.getMethod("set", Object.class, Object.class);
        }
        sSetMethod.invoke(obj, obj2, obj3);
    }

    private static Class<?> strTypeToClassThrows(String str) throws ClassNotFoundException {
        if (PRIMITIVE_TYPE.containsKey(str)) {
            return PRIMITIVE_TYPE.get(str);
        }
        if (!str.contains(".")) {
            str = "java.lang." + str;
        }
        return Class.forName(str);
    }

    public static Class<?>[] strTypesToClass(String[] strArr) throws ClassNotFoundException {
        if (strArr == null) {
            return null;
        }
        Class<?>[] clsArr = new Class[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            clsArr[i] = strTypeToClassThrows(strArr[i]);
        }
        return clsArr;
    }
}
