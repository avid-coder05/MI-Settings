package com.android.settings.utils;

import android.app.ActivityThread;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.android.settings.R;
import com.android.settings.notification.MiuiNotificationBackend;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.cloud.Constants;

/* loaded from: classes2.dex */
public class XmsfUtils {
    public static void filterChannels(String str, List<NotificationChannelGroup> list) {
        if (!TextUtils.equals(Constants.XMSF_PACKAGE_NAME, str) || list == null) {
            return;
        }
        filterOutXmsfChannels(list);
    }

    private static void filterOutXmsfChannels(List<NotificationChannelGroup> list) {
        ArrayList arrayList = new ArrayList();
        for (NotificationChannelGroup notificationChannelGroup : list) {
            ArrayList arrayList2 = new ArrayList();
            for (NotificationChannel notificationChannel : notificationChannelGroup.getChannels()) {
                if (notificationChannel.getId().startsWith("mipush_")) {
                    arrayList2.add(notificationChannel);
                }
            }
            notificationChannelGroup.getChannels().removeAll(arrayList2);
            if (notificationChannelGroup.getChannels().isEmpty()) {
                arrayList.add(notificationChannelGroup);
            }
        }
        list.removeAll(arrayList);
    }

    public static NotificationChannelGroup getXmsfChannels(Context context, PackageManager packageManager, MiuiNotificationBackend miuiNotificationBackend, String str, int i) {
        int xmsfUid;
        if (!TextUtils.equals(Constants.XMSF_PACKAGE_NAME, str) && (xmsfUid = getXmsfUid(packageManager, i)) >= 0) {
            String format = String.format("mipush_%s_", str);
            ArrayList arrayList = new ArrayList();
            Iterator it = miuiNotificationBackend.getChannelGroups(Constants.XMSF_PACKAGE_NAME, xmsfUid).getList().iterator();
            while (it.hasNext()) {
                for (NotificationChannel notificationChannel : ((NotificationChannelGroup) it.next()).getChannels()) {
                    if (notificationChannel.getId().startsWith(format)) {
                        arrayList.add(notificationChannel);
                    }
                }
            }
            if (arrayList.isEmpty()) {
                return null;
            }
            NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup("xmsf_fake_channel_group", context.getResources().getString(R.string.mipush_fake_channel_group_name));
            notificationChannelGroup.getChannels().addAll(arrayList);
            return notificationChannelGroup;
        }
        return null;
    }

    private static int getXmsfUid(PackageManager packageManager, int i) {
        ApplicationInfo applicationInfo;
        IPackageManager packageManager2 = ActivityThread.getPackageManager();
        if (-1 != i) {
            try {
                applicationInfo = packageManager2.getApplicationInfo(Constants.XMSF_PACKAGE_NAME, 0, i);
            } catch (PackageManager.NameNotFoundException | Exception unused) {
                applicationInfo = null;
            }
        } else {
            applicationInfo = packageManager.getApplicationInfo(Constants.XMSF_PACKAGE_NAME, 0);
        }
        if (applicationInfo != null) {
            return applicationInfo.uid;
        }
        return -1;
    }

    public static boolean isMipushChannel(String str) {
        return !TextUtils.isEmpty(str) && str.startsWith("mipush_");
    }
}
