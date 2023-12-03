package com.android.security;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settings.search.tree.SecuritySettingsTree;
import miui.provider.ExtraContacts;

/* loaded from: classes.dex */
public class AdbUtils {
    private static Bundle callPreference(Context context, String str, Bundle bundle) {
        try {
            return context.getContentResolver().call(Uri.parse("content://com.miui.securitycenter.remoteprovider"), "callPreference", str, bundle);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Intent getInterceptIntent(String str, String str2, String str3) {
        Intent intent = new Intent("miui.intent.action.SPECIAL_PERMISSIO_NINTERCEPT");
        intent.putExtra("pkgName", str);
        intent.putExtra("permName", str2);
        intent.putExtra("permDesc", str3);
        intent.setPackage(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
        return intent;
    }

    public static boolean getPreferenceBoolean(Context context, String str, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString(SettingsProvider.ARGS_KEY, str);
        bundle.putBoolean(ExtraContacts.DefaultAccount.NAME, z);
        Bundle callPreference = callPreference(context, "GET", bundle);
        return callPreference == null ? z : callPreference.getBoolean(str, z);
    }

    public static boolean isInputEnabled() {
        return SystemProperties.getBoolean("persist.security.adbinput", false);
    }

    public static boolean isInstallEnabled(Context context) {
        return SystemProperties.getBoolean("persist.security.adbinstall", false);
    }

    public static boolean isIntentEnable(Context context, Intent intent) {
        return !context.getPackageManager().queryIntentActivities(intent, 0).isEmpty();
    }

    public static void setInputEnabled(boolean z) {
        SystemProperties.set("persist.security.adbinput", z ? "1" : "0");
    }

    public static void setInstallEnabled(Context context, boolean z) {
        SystemProperties.set("persist.security.adbinstall", context != null ? "1" : "0");
    }

    public static void setPreferenceBoolean(Context context, String str, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString(SettingsProvider.ARGS_KEY, str);
        bundle.putBoolean("value", z);
        callPreference(context, "SET", bundle);
    }
}
