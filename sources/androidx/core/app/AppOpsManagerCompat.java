package androidx.core.app;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;

/* loaded from: classes.dex */
public final class AppOpsManagerCompat {
    public static int noteProxyOpNoThrow(Context context, String op, String proxiedPackageName) {
        if (Build.VERSION.SDK_INT >= 23) {
            return ((AppOpsManager) context.getSystemService(AppOpsManager.class)).noteProxyOpNoThrow(op, proxiedPackageName);
        }
        return 1;
    }

    public static String permissionToOp(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            return AppOpsManager.permissionToOp(permission);
        }
        return null;
    }
}
