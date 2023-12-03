package com.miui.privacypolicy;

import android.content.Context;
import android.content.SharedPreferences;

/* loaded from: classes2.dex */
public class SharePreferenceUtils {
    private static SharedPreferences mInstance;

    /* JADX INFO: Access modifiers changed from: protected */
    public static void clear(Context context) {
        SharedPreferences.Editor edit = getInstance(context).edit();
        edit.clear();
        edit.commit();
    }

    private static SharedPreferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = context.getSharedPreferences("privacy_sdk", 0);
        }
        return mInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static long getLong(Context context, String str, long j) {
        return getInstance(context).getLong(str, j);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void putLong(Context context, String str, long j) {
        SharedPreferences.Editor edit = getInstance(context).edit();
        edit.putLong(str, j);
        edit.commit();
    }
}
