package miui.util;

import android.app.MiuiNotification;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import miui.os.Build;
import miui.reflect.Field;

/* loaded from: classes4.dex */
public class NotificationHelper {
    private static final String EXTRA_SUBSTITUTE_APP_NAME = "android.substName";
    private static final String TAG = "NotificationHelper";

    private NotificationHelper() {
    }

    private static void setSubstituteAppName(Notification.Builder builder, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SUBSTITUTE_APP_NAME, str);
        builder.addExtras(bundle);
    }

    public static Notification setTargetPkg(Context context, Notification.Builder builder, String str) {
        return Build.IS_MIUI ? setTargetPkgForMiui(builder, str) : setTargetPkgForAndroid(context, builder, str);
    }

    private static Notification setTargetPkgForAndroid(Context context, Notification.Builder builder, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            Drawable loadIcon = applicationInfo.loadIcon(packageManager);
            if (loadIcon instanceof BitmapDrawable) {
                builder.setLargeIcon(((BitmapDrawable) loadIcon).getBitmap());
            }
            if (Build.VERSION.SDK_INT >= 20) {
                setSubstituteAppName(builder, applicationInfo.loadLabel(packageManager).toString());
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "setTargetPkg failed", e);
        }
        return builder.build();
    }

    private static Notification setTargetPkgForMiui(Notification.Builder builder, String str) {
        Notification build = builder.build();
        ((MiuiNotification) Field.of(Notification.class, "extraNotification", MiuiNotification.class).get(build)).setTargetPkg(str);
        return build;
    }
}
