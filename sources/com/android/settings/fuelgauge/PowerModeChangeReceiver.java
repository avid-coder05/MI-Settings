package com.android.settings.fuelgauge;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemProperties;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import miui.app.constants.ThemeManagerConstants;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class PowerModeChangeReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (FeatureParser.getBoolean("support_power_mode", false)) {
            updateNotification(context, "high".equals(SystemProperties.get("persist.sys.aries.power_profile", "middle")));
        }
    }

    public void updateNotification(Context context, boolean z) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        if (!z) {
            notificationManager.cancel(R.string.high_performance_notif_title);
            return;
        }
        Intent intent = new Intent(context, PowerModeSettings.class);
        intent.addFlags(268435456);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        int i = R.string.high_performance_notif_title;
        String string = context.getString(i);
        Notification.Builder builder = new Notification.Builder(context.getApplicationContext());
        builder.setTicker(string);
        builder.setContentTitle(string);
        builder.setContentText(context.getString(R.string.high_performance_notif_summary));
        builder.setContentIntent(activity);
        builder.setDefaults(4);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.drawable.high_performance_notif_small);
        Drawable drawable = context.getResources().getDrawable(R.drawable.high_performance_notif);
        Bitmap bitmap = drawable instanceof BitmapDrawable ? ((BitmapDrawable) drawable).getBitmap() : null;
        if (bitmap != null) {
            builder.setLargeIcon(bitmap);
        }
        Notification build = builder.build();
        build.extraNotification.setMessageCount(0);
        build.extraNotification.customizedIcon = true;
        notificationManager.notify(i, build);
    }
}
