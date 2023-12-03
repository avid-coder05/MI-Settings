package com.android.settingslib.util;

import android.content.Context;

/* loaded from: classes2.dex */
public class HapticUtil {
    private static volatile HapticUtil INSTANCE;

    private HapticUtil(Context context, boolean z) {
    }

    public static HapticUtil getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HapticUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HapticUtil(context, true);
                }
            }
        }
        return INSTANCE;
    }

    public void performHapticFeedback() {
    }
}
