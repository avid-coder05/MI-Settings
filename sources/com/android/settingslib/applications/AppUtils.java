package com.android.settingslib.applications;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.usb.IUsbManager;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.applications.instantapps.InstantAppDataProvider;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class AppUtils {
    private static final Intent sBrowserIntent = new Intent().setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE").setData(Uri.parse("http:"));
    private static InstantAppDataProvider sInstantAppDataProvider;

    public static CharSequence getApplicationLabel(PackageManager packageManager, String str) {
        return com.android.settingslib.utils.applications.AppUtils.getApplicationLabel(packageManager, str);
    }

    public static boolean hasPreferredActivities(PackageManager packageManager, String str) {
        ArrayList arrayList = new ArrayList();
        packageManager.getPreferredActivities(new ArrayList(), arrayList, str);
        Log.d("AppUtils", "Have " + arrayList.size() + " number of activities in preferred list");
        return arrayList.size() > 0;
    }

    public static boolean hasUsbDefaults(IUsbManager iUsbManager, String str) {
        if (iUsbManager != null) {
            try {
                return iUsbManager.hasDefaults(str, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.e("AppUtils", "mUsbManager.hasDefaults", e);
                return false;
            }
        }
        return false;
    }

    public static boolean isBrowserApp(Context context, String str, int i) {
        Intent intent = sBrowserIntent;
        intent.setPackage(str);
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivitiesAsUser(intent, 131072, i)) {
            if (resolveInfo.activityInfo != null && resolveInfo.handleAllWebDataURI) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDefaultBrowser(Context context, String str) {
        return TextUtils.equals(str, context.getPackageManager().getDefaultBrowserPackageNameAsUser(UserHandle.myUserId()));
    }

    public static boolean isHiddenSystemModule(Context context, String str) {
        return ApplicationsState.getInstance((Application) context.getApplicationContext()).isHiddenModule(str);
    }

    public static boolean isInstant(ApplicationInfo applicationInfo) {
        String[] split;
        InstantAppDataProvider instantAppDataProvider = sInstantAppDataProvider;
        if (instantAppDataProvider != null) {
            if (instantAppDataProvider.isInstantApp(applicationInfo)) {
                return true;
            }
        } else if (applicationInfo.isInstantApp()) {
            return true;
        }
        String str = SystemProperties.get("settingsdebug.instant.packages");
        if (str != null && !str.isEmpty() && applicationInfo.packageName != null && (split = str.split(",")) != null) {
            for (String str2 : split) {
                if (applicationInfo.packageName.contains(str2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMainlineModule(PackageManager packageManager, String str) {
        try {
            try {
                packageManager.getModuleInfo(str, 0);
                return true;
            } catch (PackageManager.NameNotFoundException unused) {
                return packageManager.getPackageInfo(str, 0).applicationInfo.sourceDir.startsWith(Environment.getApexDirectory().getAbsolutePath());
            }
        } catch (PackageManager.NameNotFoundException unused2) {
            return false;
        }
    }

    public static boolean isSystemModule(Context context, String str) {
        return ApplicationsState.getInstance((Application) context.getApplicationContext()).isSystemModule(str);
    }
}
