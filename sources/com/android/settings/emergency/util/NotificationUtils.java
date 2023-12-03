package com.android.settings.emergency.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

/* loaded from: classes.dex */
public class NotificationUtils {
    public static Notification.Builder createNotificationBuilder(Context context, String str) {
        return new Notification.Builder(context, str);
    }

    public static void createNotificationChannel(NotificationManager notificationManager, String str, String str2, int i) {
        Log.w("SOS-LocationService", "create channel!");
        NotificationChannel notificationChannel = new NotificationChannel(str, str2, getImportance(i));
        notificationChannel.setSound(null, null);
        notificationChannel.setVibrationPattern(new long[]{0});
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private static int getImportance(int i) {
        if (i != 0) {
            int i2 = 1;
            if (i != 1) {
                i2 = 2;
                if (i != 2) {
                    i2 = 4;
                    if (i != 4) {
                        i2 = 5;
                        if (i != 5) {
                            return 3;
                        }
                    }
                }
            }
            return i2;
        }
        return 0;
    }
}
