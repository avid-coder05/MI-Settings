package com.android.settings.privacy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

/* loaded from: classes2.dex */
public class PrivacyPackageUtils {
    public static Drawable getAppIcon(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                return applicationInfo.loadIcon(packageManager);
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public static String getAppLabel(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                return packageManager.getApplicationLabel(applicationInfo).toString();
            }
        } catch (Exception e) {
            Log.e("PrivacyPackageUtils", "getAppLabel error", e);
        }
        return str;
    }

    public static boolean isAppEnabled(Context context, String str) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                return applicationInfo.enabled;
            }
            return true;
        } catch (Exception e) {
            Log.e("PrivacyPackageUtils", "isAppEnabled error", e);
            return true;
        }
    }

    public static boolean isInstalledPackage(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
