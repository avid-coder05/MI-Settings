package com.android.server.notification;

import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.pm.ParceledListSlice;
import android.os.RemoteException;
import android.os.ServiceManager;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public class NotificationManagerServiceCompat {
    static INotificationManager sINM = INotificationManager.Stub.asInterface(ServiceManager.getService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION));

    public static boolean areNotificationsEnabledForPackage(String str, int i) throws RemoteException {
        return sINM.areNotificationsEnabledForPackage(str, i);
    }

    public static int getBubblePreferenceForPackage(String str, int i) throws RemoteException {
        return sINM.getBubblePreferenceForPackage(str, i);
    }

    public static NotificationChannel getNotificationChannelForPackage(String str, int i, String str2, String str3, boolean z) throws RemoteException {
        return sINM.getNotificationChannelForPackage(str, i, str2, str3, z);
    }

    public static ParceledListSlice<NotificationChannelGroup> getNotificationChannelGroupsForPackage(String str, int i, boolean z) throws RemoteException {
        return sINM.getNotificationChannelGroupsForPackage(str, i, z);
    }

    public static int getPriority(String str, int i) throws RemoteException {
        return -1;
    }

    public static int getVisibilityOverride(String str, int i) throws RemoteException {
        return -1;
    }

    public static boolean onlyHasDefaultChannel(String str, int i) throws RemoteException {
        return sINM.onlyHasDefaultChannel(str, i);
    }

    public static void setPriority(String str, int i, int i2) throws RemoteException {
    }

    public static void setShowBadge(String str, int i, boolean z) throws RemoteException {
        sINM.setShowBadge(str, i, z);
    }

    public static void updateNotificationChannelForPackage(String str, int i, NotificationChannel notificationChannel) throws RemoteException {
        sINM.updateNotificationChannelForPackage(str, i, notificationChannel);
    }

    public static void updateNotificationChannelGroupForPackage(String str, int i, NotificationChannelGroup notificationChannelGroup) throws RemoteException {
        sINM.updateNotificationChannelGroupForPackage(str, i, notificationChannelGroup);
    }
}
