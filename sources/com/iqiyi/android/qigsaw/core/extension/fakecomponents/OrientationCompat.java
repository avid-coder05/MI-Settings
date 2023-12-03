package com.iqiyi.android.qigsaw.core.extension.fakecomponents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
final class OrientationCompat {
    OrientationCompat() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void fixedOrientation(Activity activity, int i) {
        if (i == -1 || Build.VERSION.SDK_INT != 26 || activity.getApplicationInfo().targetSdkVersion <= 26 || !isTranslucentOrFloating(activity) || isFixedOrientation(activity)) {
            return;
        }
        try {
            Field declaredField = Activity.class.getDeclaredField("mActivityInfo");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(activity);
            Field declaredField2 = ActivityInfo.class.getDeclaredField("screenOrientation");
            declaredField2.setAccessible(true);
            if (declaredField2.getInt(obj) == -1) {
                declaredField2.setInt(obj, i);
            }
        } catch (IllegalAccessException | NoSuchFieldException unused) {
        }
    }

    @SuppressLint({"SoonBlockedPrivateApi"})
    private static boolean isFixedOrientation(Activity activity) {
        try {
            Field declaredField = Activity.class.getDeclaredField("mActivityInfo");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(activity);
            Method declaredMethod = ActivityInfo.class.getDeclaredMethod("isFixedOrientation", new Class[0]);
            declaredMethod.setAccessible(true);
            return ((Boolean) declaredMethod.invoke(obj, new Object[0])).booleanValue();
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException unused) {
            return false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0091, code lost:
    
        if (r1 == null) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0094, code lost:
    
        if (r1 == null) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0097, code lost:
    
        if (r1 == null) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0099, code lost:
    
        r1.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x009c, code lost:
    
        return false;
     */
    @android.annotation.SuppressLint({"PrivateApi"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean isTranslucentOrFloating(android.app.Activity r7) {
        /*
            r0 = 0
            r1 = 0
            java.lang.String r2 = "com.android.internal.R$styleable"
            java.lang.Class r2 = java.lang.Class.forName(r2)     // Catch: java.lang.Throwable -> L8a java.lang.NoSuchFieldException -> L91 java.lang.IllegalAccessException -> L94 java.lang.ClassNotFoundException -> L97
            java.lang.String r3 = "Window"
            java.lang.reflect.Field r3 = r2.getDeclaredField(r3)     // Catch: java.lang.Throwable -> L8a java.lang.NoSuchFieldException -> L91 java.lang.IllegalAccessException -> L94 java.lang.ClassNotFoundException -> L97
            r4 = 1
            r3.setAccessible(r4)     // Catch: java.lang.Throwable -> L8a java.lang.NoSuchFieldException -> L91 java.lang.IllegalAccessException -> L94 java.lang.ClassNotFoundException -> L97
            java.lang.Object r3 = r3.get(r1)     // Catch: java.lang.Throwable -> L8a java.lang.NoSuchFieldException -> L91 java.lang.IllegalAccessException -> L94 java.lang.ClassNotFoundException -> L97
            int[] r3 = (int[]) r3     // Catch: java.lang.Throwable -> L8a java.lang.NoSuchFieldException -> L91 java.lang.IllegalAccessException -> L94 java.lang.ClassNotFoundException -> L97
            android.content.res.TypedArray r7 = r7.obtainStyledAttributes(r3)     // Catch: java.lang.Throwable -> L8a java.lang.NoSuchFieldException -> L91 java.lang.IllegalAccessException -> L94 java.lang.ClassNotFoundException -> L97
            java.lang.String r3 = "Window_windowIsTranslucent"
            java.lang.reflect.Field r3 = r2.getDeclaredField(r3)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            r3.setAccessible(r4)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            java.lang.String r5 = "Window_windowSwipeToDismiss"
            java.lang.reflect.Field r5 = r2.getDeclaredField(r5)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            r5.setAccessible(r4)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            java.lang.String r6 = "Window_windowIsFloating"
            java.lang.reflect.Field r2 = r2.getDeclaredField(r6)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            r2.setAccessible(r4)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            java.lang.Object r6 = r3.get(r1)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            int r6 = r6.intValue()     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            boolean r6 = r7.getBoolean(r6, r0)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            java.lang.Object r3 = r3.get(r1)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            int r3 = r3.intValue()     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            boolean r3 = r7.hasValue(r3)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            if (r3 != 0) goto L67
            java.lang.Object r3 = r5.get(r1)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            int r3 = r3.intValue()     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            boolean r3 = r7.getBoolean(r3, r0)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            if (r3 == 0) goto L67
            r3 = r4
            goto L68
        L67:
            r3 = r0
        L68:
            java.lang.Object r1 = r2.get(r1)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            int r1 = r1.intValue()     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            boolean r1 = r7.getBoolean(r1, r0)     // Catch: java.lang.Throwable -> L81 java.lang.NoSuchFieldException -> L84 java.lang.IllegalAccessException -> L86 java.lang.ClassNotFoundException -> L88
            if (r1 != 0) goto L7c
            if (r6 != 0) goto L7c
            if (r3 == 0) goto L7d
        L7c:
            r0 = r4
        L7d:
            r7.recycle()
            return r0
        L81:
            r0 = move-exception
            r1 = r7
            goto L8b
        L84:
            r1 = r7
            goto L91
        L86:
            r1 = r7
            goto L94
        L88:
            r1 = r7
            goto L97
        L8a:
            r0 = move-exception
        L8b:
            if (r1 == 0) goto L90
            r1.recycle()
        L90:
            throw r0
        L91:
            if (r1 == 0) goto L9c
            goto L99
        L94:
            if (r1 == 0) goto L9c
            goto L99
        L97:
            if (r1 == 0) goto L9c
        L99:
            r1.recycle()
        L9c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iqiyi.android.qigsaw.core.extension.fakecomponents.OrientationCompat.isTranslucentOrFloating(android.app.Activity):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int releaseFixedOrientation(Activity activity) {
        if (Build.VERSION.SDK_INT == 26 && activity.getApplicationInfo().targetSdkVersion > 26 && isTranslucentOrFloating(activity) && isFixedOrientation(activity)) {
            try {
                Field declaredField = Activity.class.getDeclaredField("mActivityInfo");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(activity);
                Field declaredField2 = ActivityInfo.class.getDeclaredField("screenOrientation");
                declaredField2.setAccessible(true);
                int i = declaredField2.getInt(obj);
                if (i != -1) {
                    try {
                        declaredField2.setInt(obj, -1);
                    } catch (IllegalAccessException | NoSuchFieldException unused) {
                    }
                }
                return i;
            } catch (IllegalAccessException | NoSuchFieldException unused2) {
                return -1;
            }
        }
        return -1;
    }
}
