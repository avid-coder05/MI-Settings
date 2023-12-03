package com.android.settings.applications;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.applications.defaultapps.DefaultBrowserPicker;
import java.util.List;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public class DefaultAppsHelper {
    public static String getApplicationLabel(Context context, String str, PackageManager packageManager) {
        if (str.equals(ThemeResources.FRAMEWORK_PACKAGE)) {
            return context.getString(R.string.preferred_app_settings_not_selected);
        }
        try {
            CharSequence loadLabel = packageManager.getApplicationInfo(str, 0).loadLabel(packageManager);
            return !TextUtils.isEmpty(loadLabel) ? loadLabel.toString() : str;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("DefaultAppsHelper", "Package: " + str + " not found");
            return str;
        }
    }

    public static Intent getIntent(IntentFilter intentFilter) {
        Uri uri;
        Intent intent = new Intent(intentFilter.getAction(0));
        if (intentFilter.countCategories() > 0 && !TextUtils.isEmpty(intentFilter.getCategory(0))) {
            intent.addCategory(intentFilter.getCategory(0));
        }
        String str = null;
        if (intentFilter.countDataSchemes() <= 0 || TextUtils.isEmpty(intentFilter.getDataScheme(0))) {
            uri = null;
        } else {
            uri = Uri.parse(intentFilter.getDataScheme(0) + ":");
        }
        if (intentFilter.countDataTypes() > 0 && !TextUtils.isEmpty(intentFilter.getDataType(0))) {
            str = intentFilter.getDataType(0);
            if (!str.contains("\\") && !str.contains("/")) {
                str = str + "/*";
            }
        }
        intent.setDataAndType(uri, str);
        return intent;
    }

    public static IntentFilter getIntentFilter(int i) {
        IntentFilter intentFilter = new IntentFilter();
        switch (i) {
            case 0:
                intentFilter.addAction("android.intent.action.MAIN");
                intentFilter.addCategory("android.intent.category.HOME");
                break;
            case 1:
                intentFilter.addAction("android.intent.action.DIAL");
                intentFilter.addDataScheme("tel");
                break;
            case 2:
                intentFilter.addAction("android.intent.action.SENDTO");
                intentFilter.addDataScheme("smsto");
                break;
            case 3:
                intentFilter.addAction("android.intent.action.VIEW");
                intentFilter.addCategory("android.intent.category.DEFAULT");
                intentFilter.addDataScheme("http");
                intentFilter.addDataScheme("https");
                break;
            case 4:
                intentFilter.addAction("android.media.action.IMAGE_CAPTURE");
                break;
            case 5:
                intentFilter.addAction("android.intent.action.VIEW");
                intentFilter.addDataScheme("content");
                intentFilter.addDataScheme("file");
                try {
                    intentFilter.addDataType("image/*");
                    break;
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    e.printStackTrace();
                    break;
                }
            case 6:
                intentFilter.addAction("android.intent.action.VIEW");
                intentFilter.addCategory("android.intent.category.DEFAULT");
                intentFilter.addDataScheme("content");
                intentFilter.addDataScheme("file");
                try {
                    intentFilter.addDataType("audio/*");
                    break;
                } catch (IntentFilter.MalformedMimeTypeException e2) {
                    e2.printStackTrace();
                    break;
                }
            case 7:
                intentFilter.addAction("android.intent.action.SENDTO");
                intentFilter.addDataScheme("mailto");
                break;
            case 8:
                intentFilter.addAction("android.intent.action.VIEW");
                intentFilter.addDataScheme("content");
                intentFilter.addDataScheme("file");
                try {
                    intentFilter.addDataType("video/*");
                    break;
                } catch (IntentFilter.MalformedMimeTypeException e3) {
                    e3.printStackTrace();
                    break;
                }
        }
        return intentFilter;
    }

    public static boolean isAppInstalled(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isAtLeastS() {
        return Build.VERSION.SDK_INT >= 31;
    }

    public static void loadDefaultBrowser(Context context) {
        boolean z = miui.os.Build.IS_INTERNATIONAL_BUILD;
        if (z && RegionUtils.isGoogleClientRegion()) {
            PackageManager packageManager = context.getPackageManager();
            String defaultBrowserPackageNameAsUser = packageManager != null ? packageManager.getDefaultBrowserPackageNameAsUser(context.getUserId()) : null;
            Log.i("DefaultAppsHelper", "loadDefaultBrowser: currentDefaultPackageName = " + defaultBrowserPackageNameAsUser);
            if (isAtLeastS() || TextUtils.isEmpty(defaultBrowserPackageNameAsUser)) {
                if (MiuiUtils.isApplicationInstalled(context, "com.mi.globalbrowser") && MiuiUtils.isAppEnabled(context, "com.mi.globalbrowser")) {
                    setDefaultBrowser(context, "com.mi.globalbrowser");
                    return;
                } else if (MiuiUtils.isApplicationInstalled(context, "com.android.browser") && MiuiUtils.isAppEnabled(context, "com.android.browser")) {
                    setDefaultBrowser(context, "com.android.browser");
                }
            }
        }
        if (z && RegionUtils.isGMSDefault()) {
            PackageManager packageManager2 = context.getPackageManager();
            if (TextUtils.isEmpty(packageManager2 != null ? packageManager2.getDefaultBrowserPackageNameAsUser(context.getUserId()) : null) && MiuiUtils.isApplicationInstalled(context, "com.android.chrome")) {
                setDefaultBrowser(context, "com.android.chrome");
            }
        }
    }

    public static void loadDefaultVideoPlayer(Context context) {
        PackageManager packageManager;
        if (miui.os.Build.IS_INTERNATIONAL_BUILD && !RegionUtils.IS_INDIA && (packageManager = context.getPackageManager()) != null && MiuiUtils.isApplicationInstalled(context, "com.miui.videoplayer")) {
            IntentFilter intentFilter = getIntentFilter(8);
            Intent intent = getIntent(intentFilter);
            ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 0);
            if (resolveActivity == null) {
                Log.w("DefaultAppsHelper", "Do not find such type app");
                return;
            }
            ActivityInfo activityInfo = resolveActivity.activityInfo;
            String str = activityInfo != null ? activityInfo.packageName : null;
            Log.i("DefaultAppsHelper", "loadDefaultVideoPlayer: currentDefaultPackageName = " + str);
            if (ThemeResources.FRAMEWORK_PACKAGE.equals(str)) {
                Intent intent2 = new Intent(intent);
                intent2.setPackage("com.miui.videoplayer");
                intent2.setAction("duokan.intent.action.VIDEO_PLAY");
                ComponentName resolveActivity2 = intent2.resolveActivity(packageManager);
                if (resolveActivity2 == null) {
                    Log.w("DefaultAppsHelper", "Cannot resolve activity from current intent: " + intent2.toString());
                    return;
                }
                List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 131072);
                int size = queryIntentActivities.size();
                if (size == 0) {
                    Log.w("DefaultAppsHelper", "ResolveInfo list is empty");
                    return;
                }
                ComponentName[] componentNameArr = new ComponentName[size];
                int i = 0;
                for (int i2 = 0; i2 < size; i2++) {
                    ResolveInfo resolveInfo = queryIntentActivities.get(i2);
                    ActivityInfo activityInfo2 = resolveInfo.activityInfo;
                    componentNameArr[i2] = new ComponentName(activityInfo2.packageName, activityInfo2.name);
                    int i3 = resolveInfo.match;
                    if (i3 > i) {
                        i = i3;
                    }
                }
                if (str != null) {
                    packageManager.clearPackagePreferredActivities(str);
                }
                intentFilter.addCategory("android.intent.category.DEFAULT");
                intentFilter.addCategory("android.intent.category.BROWSABLE");
                packageManager.addPreferredActivity(intentFilter, i, componentNameArr, new ComponentName("com.miui.videoplayer", resolveActivity2.getClassName()));
            }
        }
    }

    public static void setDefaultBrowser(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        if (DefaultBrowserPicker.addBrowserRoleHolderAsUser(context, str)) {
            Log.i("DefaultAppsHelper", "addBrowserRoleHolderAsUser success");
        } else if (packageManager != null) {
            packageManager.setDefaultBrowserPackageNameAsUser(str, context.getUserId());
        } else {
            Log.w("DefaultAppsHelper", "Can not get PackageManager!");
        }
    }
}
