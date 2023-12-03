package com.android.settingslib.utils;

import android.os.Build;

/* loaded from: classes2.dex */
public final class BuildCompatUtils {
    public static boolean isAtLeastS() {
        int i = Build.VERSION.SDK_INT;
        if (i < 30) {
            return false;
        }
        String str = Build.VERSION.CODENAME;
        return (str.equals("REL") && i >= 31) || (str.length() == 1 && str.compareTo("S") >= 0 && str.compareTo("Z") <= 0);
    }
}
