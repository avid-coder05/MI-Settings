package com.android.settings.notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.notification.NotificationManagerServiceCompat;
import java.util.Collections;
import miui.content.res.ThemeResources;

/* loaded from: classes2.dex */
public class MiuiNotificationBackend {

    /* loaded from: classes2.dex */
    public static class AppRow extends Row {
        public int appVisOverride;
        public boolean banned;
        public int bubblePreference = 0;
        public Drawable icon;
        public CharSequence label;
        public String lockedChannelId;
        public boolean lockedImportance;
        public String pkg;
        public Intent settingsIntent;
        public boolean showBadge;
        public boolean systemApp;
        public int targetSdkVersion;
        public int uid;
        public int userId;

        public String toString() {
            return "AppRow{pkg='" + this.pkg + "', uid=" + this.uid + ", label=" + ((Object) this.label) + ", banned=" + this.banned + ", appVisOverride=" + this.appVisOverride + ", systemApp=" + this.systemApp + ", lockedImportance=" + this.lockedImportance + ", lockedChannelId='" + this.lockedChannelId + "', showBadge=" + this.showBadge + ", bubblePreference=" + this.bubblePreference + ", userId=" + this.userId + ", targetSdkVersion=" + this.targetSdkVersion + '}';
        }
    }

    /* loaded from: classes2.dex */
    static class Row {
        Row() {
        }
    }

    @VisibleForTesting
    static void markAppRowWithBlockables(String[] strArr, AppRow appRow, String str) {
        if (strArr != null) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                String str2 = strArr[i];
                if (str2 != null) {
                    if (str2.contains(":")) {
                        if (str.equals(str2.split(":", 2)[0])) {
                            appRow.lockedChannelId = str2.split(":", 2)[1];
                        }
                    } else if (str.equals(strArr[i])) {
                        appRow.lockedImportance = true;
                        appRow.systemApp = true;
                    }
                }
            }
        }
    }

    public int getBubblePreference(String str, int i) {
        try {
            return NotificationManagerServiceCompat.getBubblePreferenceForPackage(str, i);
        } catch (Exception e) {
            Log.w("MiuiNotificationBackend", "Error calling NoMan", e);
            return -1;
        }
    }

    public NotificationChannel getChannel(String str, int i, String str2, String str3) {
        if (str2 == null) {
            return null;
        }
        try {
            return NotificationManagerServiceCompat.getNotificationChannelForPackage(str, i, str2, str3, false);
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error getChannel " + str + ":" + str2, e);
            return null;
        }
    }

    public ParceledListSlice<NotificationChannelGroup> getChannelGroups(String str, int i) {
        try {
            return NotificationManagerServiceCompat.getNotificationChannelGroupsForPackage(str, i, false);
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error getChannelGroups " + str, e);
            return new ParceledListSlice<>(Collections.emptyList());
        }
    }

    public int getPriority(String str, int i) {
        try {
            return NotificationManagerServiceCompat.getPriority(str, i);
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error getPriority " + str, e);
            return 0;
        }
    }

    public int getVisibilityOverride(String str, int i) {
        try {
            return NotificationManagerServiceCompat.getVisibilityOverride(str, i);
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error getVisibilityOverride " + str, e);
            return -1000;
        }
    }

    public AppRow loadAppRow(Context context, PackageManager packageManager, ApplicationInfo applicationInfo) {
        AppRow appRow = new AppRow();
        appRow.pkg = applicationInfo.packageName;
        appRow.uid = applicationInfo.uid;
        try {
            appRow.label = applicationInfo.loadLabel(packageManager);
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error loading application label for " + appRow.pkg, e);
            appRow.label = appRow.pkg;
        }
        appRow.icon = applicationInfo.loadIcon(packageManager);
        appRow.banned = NotificationSettingsHelper.isNotificationsBanned(appRow.pkg, appRow.uid);
        appRow.appVisOverride = getVisibilityOverride(appRow.pkg, appRow.uid);
        appRow.showBadge = NotificationSettingsHelper.canShowBadge(context, appRow.pkg);
        appRow.bubblePreference = getBubblePreference(appRow.pkg, appRow.uid);
        appRow.userId = UserHandle.getUserId(appRow.uid);
        appRow.targetSdkVersion = applicationInfo.targetSdkVersion;
        return appRow;
    }

    public AppRow loadAppRow(Context context, PackageManager packageManager, PackageInfo packageInfo) {
        AppRow loadAppRow = loadAppRow(context, packageManager, packageInfo.applicationInfo);
        int identifier = context.getResources().getIdentifier("config_nonBlockableNotificationPackages", "array", ThemeResources.FRAMEWORK_PACKAGE);
        if (identifier != 0) {
            markAppRowWithBlockables(context.getResources().getStringArray(identifier), loadAppRow, packageInfo.packageName);
        }
        markAppRowWithBlockables(context.getResources().getStringArray(17236066), loadAppRow, packageInfo.packageName);
        return loadAppRow;
    }

    public boolean onlyHasDefaultChannel(String str, int i) {
        try {
            return NotificationManagerServiceCompat.onlyHasDefaultChannel(str, i);
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error onlyHasDefaultChannel " + str, e);
            return false;
        }
    }

    public void setPriority(String str, int i, int i2) {
        try {
            NotificationManagerServiceCompat.setPriority(str, i, i2);
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error setPriority " + str, e);
        }
    }

    public boolean setShowBadge(Context context, String str, int i, boolean z) {
        try {
            NotificationManagerServiceCompat.setShowBadge(str, i, z);
            return true;
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error setShowBadge " + str, e);
            return false;
        }
    }

    public void updateChannel(String str, int i, NotificationChannel notificationChannel) {
        try {
            NotificationManagerServiceCompat.updateNotificationChannelForPackage(str, i, notificationChannel);
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error updateChannel " + str + ":" + notificationChannel.getId(), e);
        }
    }

    public void updateChannelGroup(String str, int i, NotificationChannelGroup notificationChannelGroup) {
        try {
            NotificationManagerServiceCompat.updateNotificationChannelGroupForPackage(str, i, notificationChannelGroup);
        } catch (Exception e) {
            Log.e("MiuiNotificationBackend", "Error updateChannelGroup " + str + ":" + notificationChannelGroup.getId(), e);
        }
    }
}
