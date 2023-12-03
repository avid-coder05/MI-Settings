package com.android.settings;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import miui.util.CustomizeUtil;

/* loaded from: classes.dex */
public class TitleManager {
    public static int getFeedbackSettingsTitle(Context context) {
        if (context == null || !TextUtils.equals(context.getPackageName(), "com.miui.miservice")) {
            return -1;
        }
        PackageInfo packageInfo = null;
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return -1;
        }
        try {
            packageInfo = packageManager.getPackageInfo("com.miui.miservice", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return -1;
        }
        return packageInfo.applicationInfo.labelRes;
    }

    public static int getScreenTitle(Context context) {
        return AodCompatibilityHelper.isAodAvailable(context) ? R.string.aod_and_lock_screen_settings_title : R.string.lock_screen_settings_title;
    }

    public static int getStatusBarTitle() {
        return CustomizeUtil.HAS_NOTCH ? R.string.notch_and_status_bar_settings : R.string.status_bar_title;
    }
}
