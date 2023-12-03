package com.xiaomi.accountsdk.utils;

/* loaded from: classes2.dex */
public class MiuiOsBuildReflection {
    public static boolean isAlpha(boolean z) {
        return reflectField("IS_ALPHA_BUILD", z);
    }

    private static boolean isDev(boolean z) {
        return reflectField("IS_DEVELOPMENT_VERSION", z);
    }

    public static boolean isDevButNotAlpha(boolean z) {
        return isDev(z) && !isAlpha(z);
    }

    public static boolean isStable(boolean z) {
        return reflectField("IS_STABLE_VERSION", z);
    }

    private static boolean reflectField(String str, boolean z) {
        try {
            return ((Boolean) Class.forName("miui.os.Build").getField(str).get(null)).booleanValue();
        } catch (ClassNotFoundException | IllegalAccessException | NoClassDefFoundError | NoSuchFieldException unused) {
            return z;
        }
    }
}
