package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.server.notification.NotificationManagerServiceCompat;
import com.android.settings.search.FunctionColumns;
import miui.util.NotificationFilterHelper;

/* loaded from: classes2.dex */
public class NotificationSettingsHelper {
    private static final Uri URI_NOTIFICATION_CENTER = Uri.parse("content://com.miui.notification.provider");
    public static final Uri URI_PKG_CONFIG = Uri.parse("content://com.miui.notification.provider/pkgConfig");
    public static final Uri URI_FOLD_IMPORTANCE = Uri.parse("content://statusbar.notification/foldImportance");

    private NotificationSettingsHelper() {
    }

    private static Bundle call(Context context, Uri uri, String str, Bundle bundle) {
        try {
            return context.getContentResolver().call(uri, str, (String) null, bundle);
        } catch (Exception e) {
            Log.d("NotifiSettingsHelper", "Error call " + e);
            return null;
        }
    }

    private static Bundle call(Context context, String str, Bundle bundle) {
        try {
            return context.getContentResolver().call(Uri.parse("content://statusbar.notification"), str, (String) null, bundle);
        } catch (Exception e) {
            Log.d("NotifiSettingsHelper", "Error call " + e);
            return null;
        }
    }

    public static boolean canFloat(Context context, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        Bundle call = call(context, "canFloat", bundle);
        return call != null && call.getBoolean("canShowFloat", false);
    }

    public static boolean canLights(Context context, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        Bundle call = call(context, "canLights", bundle);
        return call != null && call.getBoolean("canLights", false);
    }

    public static boolean canShowBadge(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        Bundle call = call(context, "canShowBadge", bundle);
        return call != null && call.getBoolean("canShowBadge", false);
    }

    public static boolean canShowKeyguard(Context context, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        Bundle call = call(context, "canShowOnKeyguard", bundle);
        return call != null && call.getBoolean("canShowOnKeyguard", false);
    }

    public static boolean canSound(Context context, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        Bundle call = call(context, "canSound", bundle);
        return call != null && call.getBoolean("canSound", false);
    }

    public static boolean canVibrate(Context context, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        Bundle call = call(context, "canVibrate", bundle);
        return call != null && call.getBoolean("canVibrate", true);
    }

    public static int getAggregateConfig(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        Bundle call = call(context, URI_NOTIFICATION_CENTER, "getAggregatePkgConfig", bundle);
        if (call != null) {
            return call.getInt("config", 0);
        }
        return 0;
    }

    public static Intent getFloatNotificationIntent(Context context) {
        Intent intent = new Intent("miui.settings.FLOAT_NOTIFICATION_SETTINGS");
        intent.putExtra("display_type", 243);
        intent.putExtra("launch_source", 1);
        intent.setPackage("com.miui.notification");
        if (context.getPackageManager().resolveActivity(intent, 0) != null) {
            return intent;
        }
        return null;
    }

    public static int getFoldImportance(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        Bundle call = call(context, "getFoldImportance", bundle);
        if (call != null) {
            return call.getInt("foldImportance", 0);
        }
        return 0;
    }

    public static Intent getLockScreenNotificationIntent(Context context) {
        Intent intent = new Intent("miui.settings.LOCK_SCREEN_NOTIFICATION_SETTINGS");
        intent.putExtra("display_type", 242);
        intent.putExtra("launch_source", 1);
        intent.setPackage("com.miui.notification");
        if (context.getPackageManager().resolveActivity(intent, 0) != null) {
            return intent;
        }
        return null;
    }

    private static int getPackageUid(Context context, String str) {
        try {
            return context.getPackageManager().getPackageUid(str, 0);
        } catch (Exception e) {
            Log.d("NotifiSettingsHelper", "Error getPackageUid " + e);
            return 0;
        }
    }

    public static Intent getPreferManageEntranceIntent(Context context) {
        Intent intent = new Intent("android.settings.ALL_APPS_NOTIFICATION_SETTINGS");
        intent.putExtra("display_type", 241);
        intent.putExtra("launch_source", 1);
        intent.setPackage("com.miui.notification");
        if (context.getPackageManager().resolveActivity(intent, 0) != null) {
            return intent;
        }
        return null;
    }

    public static Intent getShowBadgeNotificationIntent(Context context) {
        Intent intent = new Intent("miui.settings.SHOW_BADGE_NOTIFICATION_SETTINGS");
        intent.putExtra("display_type", 244);
        intent.putExtra("launch_source", 1);
        intent.setPackage("com.miui.notification");
        if (context.getPackageManager().resolveActivity(intent, 0) != null) {
            return intent;
        }
        return null;
    }

    public static boolean isFoldable(Context context, String str) {
        String[] stringArray = context.getResources().getStringArray(17236066);
        if (stringArray == null || stringArray.length <= 0) {
            return true;
        }
        for (String str2 : stringArray) {
            if (str.equals(str2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotificationsBanned(Context context, String str) {
        return isNotificationsBanned(str, getPackageUid(context, str));
    }

    public static boolean isNotificationsBanned(String str, int i) {
        try {
            return !NotificationManagerServiceCompat.areNotificationsEnabledForPackage(str, i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static boolean isUidSystem(int i) {
        int appId = UserHandle.getAppId(i);
        return appId == 1000 || appId == 1001 || i == 0;
    }

    public static void notifyAggregateConfig(Context context, String str, int i, ContentObserver contentObserver) {
        context.getContentResolver().notifyChange(URI_PKG_CONFIG.buildUpon().appendQueryParameter(FunctionColumns.PACKAGE, str).appendQueryParameter("config", String.valueOf(i)).build(), contentObserver);
    }

    public static void setFloat(Context context, String str, String str2, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        bundle.putBoolean("canShowFloat", z);
        call(context, "setFloat", bundle);
    }

    public static void setFoldImportance(Context context, String str, int i) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putInt("foldImportance", i);
        call(context, "setFoldImportance", bundle);
    }

    public static void setLights(Context context, String str, String str2, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        bundle.putBoolean("canLights", z);
        call(context, "setLights", bundle);
    }

    public static void setNotificationsEnabledForPackage(Context context, String str, boolean z) {
        NotificationFilterHelper.enableNotifications(context, str, z);
    }

    public static void setShowBadge(Context context, String str, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putBoolean("canShowBadge", z);
        call(context, "setShowBadge", bundle);
    }

    public static void setShowKeyguard(Context context, String str, String str2, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        bundle.putBoolean("canShowOnKeyguard", z);
        call(context, "setShowOnKeyguard", bundle);
    }

    public static void setSound(Context context, String str, String str2, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        bundle.putBoolean("canSound", z);
        call(context, "setSound", bundle);
    }

    public static void setVibrate(Context context, String str, String str2, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, str);
        bundle.putString("channel_id", str2);
        bundle.putBoolean("canVibrate", z);
        call(context, "setVibrate", bundle);
    }
}
