package miui.cloud.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/* loaded from: classes3.dex */
public class PkgInfoUtil {
    private static final String TAG = "PkgInfoUtil";

    private PkgInfoUtil() {
    }

    public static boolean isPkgEnable(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "isPkgEnable ", e);
            return false;
        }
    }

    public static boolean isPkgExist(Context context, String str) {
        try {
            context.getPackageManager().getApplicationInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }
}
