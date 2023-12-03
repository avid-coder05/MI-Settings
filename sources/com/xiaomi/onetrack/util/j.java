package com.xiaomi.onetrack.util;

import android.content.Context;
import android.os.Build;
import android.os.UserManager;

/* loaded from: classes2.dex */
public class j {
    public static Context a(Context context) {
        if (e(context)) {
            p.a("FbeUtil", "getSafeContext return origin ctx");
            return context;
        }
        p.a("FbeUtil", "getSafeContext , create the safe ctx");
        return context.createDeviceProtectedStorageContext();
    }

    public static boolean d(Context context) {
        return Build.VERSION.SDK_INT >= 24 && !e(context);
    }

    private static boolean e(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                UserManager userManager = (UserManager) context.getSystemService("user");
                if (userManager != null) {
                    return userManager.isUserUnlocked();
                }
                return false;
            } catch (Exception e) {
                p.a("FbeUtil", "isUserUnlocked Exception: " + e.getMessage());
            }
        }
        return false;
    }
}
