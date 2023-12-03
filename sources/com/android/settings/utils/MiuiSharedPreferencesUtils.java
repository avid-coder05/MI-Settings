package com.android.settings.utils;

import android.content.Context;

/* loaded from: classes2.dex */
public class MiuiSharedPreferencesUtils {
    public static boolean getBooleanPreference(Context context, String str, boolean z) {
        return context.getSharedPreferences(str, 0).getBoolean(str, z);
    }

    public static int getIntPreference(Context context, String str, int i) {
        return context.getSharedPreferences(str, 0).getInt(str, i);
    }

    public static void setBooleanPreference(Context context, String str, boolean z) {
        context.getSharedPreferences(str, 0).edit().putBoolean(str, z).apply();
    }

    public static void setIntPreference(Context context, String str, int i) {
        context.getSharedPreferences(str, 0).edit().putInt(str, i).apply();
    }
}
