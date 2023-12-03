package com.android.settings.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.android.settings.utils.ReflectBuilderUtil;

/* loaded from: classes2.dex */
public class NotificationUtils {
    private static String TAG = "NotificationUtils";

    public static Notification.Builder createNotificationBuilder(Context context, String str) {
        Notification.Builder builder = new Notification.Builder(context);
        if (Build.VERSION.SDK_INT < 26) {
            return builder;
        }
        builder.setChannelId(str);
        return builder;
    }

    public static void createNotificationChannel(NotificationManager notificationManager, String str, String str2, int i) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationManager.createNotificationChannel(new NotificationChannel(str, str2, i));
    }

    public static void setCustomizedIcon(Notification notification, boolean z) {
        try {
            ReflectBuilderUtil.ReflAgent.getObject(notification).getObjectFiled("extraNotification").setResultToSelf().call("setCustomizedIcon", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(TAG, "setCustomizedIcon exception: ", e);
        }
    }

    public static void setEnableFloat(Notification notification, boolean z) {
        try {
            ReflectBuilderUtil.ReflAgent.getObject(notification).getObjectFiled("extraNotification").setResultToSelf().call("setEnableFloat", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(TAG, "setEnableFloat exception: ", e);
        }
    }

    public static void setEnableKeyguard(Notification notification, boolean z) {
        try {
            ReflectBuilderUtil.ReflAgent.getObject(notification).getObjectFiled("extraNotification").setResultToSelf().call("setEnableKeyguard", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(TAG, "setEnableKeyguard exception: ", e);
        }
    }

    public static void setMessageCount(Notification notification, int i) {
        try {
            ReflectBuilderUtil.ReflAgent.getObject(notification).getObjectFiled("extraNotification").setResultToSelf().call("setMessageCount", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e(TAG, "setMessageCount exception: ", e);
        }
    }
}
