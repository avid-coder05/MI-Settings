package com.android.settings.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

/* loaded from: classes.dex */
public class AppStoreUtil {
    public static Intent getAppStoreLink(Context context, String str) {
        return getAppStoreLink(context, getInstallerPackageName(context, str), str);
    }

    public static Intent getAppStoreLink(Context context, String str, String str2) {
        Intent resolveIntent = resolveIntent(context, new Intent("android.intent.action.SHOW_APP_INFO").setPackage(str));
        if (resolveIntent != null) {
            resolveIntent.putExtra("android.intent.extra.PACKAGE_NAME", str2);
            return resolveIntent;
        }
        return null;
    }

    public static String getInstallerPackageName(Context context, String str) {
        try {
            InstallSourceInfo installSourceInfo = context.getPackageManager().getInstallSourceInfo(str);
            String installingPackageName = installSourceInfo.getInstallingPackageName();
            String originatingPackageName = installSourceInfo.getOriginatingPackageName();
            String initiatingPackageName = installSourceInfo.getInitiatingPackageName();
            return (originatingPackageName == null || initiatingPackageName == null) ? installingPackageName : (context.getPackageManager().getApplicationInfo(initiatingPackageName, 0).flags & 1) != 0 ? originatingPackageName : installingPackageName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AppStoreUtil", "Exception while retrieving the package installer of " + str, e);
            return null;
        }
    }

    private static Intent resolveIntent(Context context, Intent intent) {
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 0);
        if (resolveActivity != null) {
            Intent intent2 = new Intent(intent.getAction());
            ActivityInfo activityInfo = resolveActivity.activityInfo;
            return intent2.setClassName(activityInfo.packageName, activityInfo.name);
        }
        return null;
    }
}
