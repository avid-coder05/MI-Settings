package com.xiaomi.micloudsdk.utils;

import android.content.Context;
import micloud.compat.independent.sync.GdprUtilsCompat;

/* loaded from: classes2.dex */
public class PermissionUtils {
    public static boolean isGdprPermissionGranted(Context context) {
        return GdprUtilsCompat.isGdprPermissionGranted(context);
    }
}
