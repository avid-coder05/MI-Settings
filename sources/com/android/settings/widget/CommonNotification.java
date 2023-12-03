package com.android.settings.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.widget.RemoteViews;
import com.android.settings.R;
import com.android.settings.utils.NotificationUtils;
import java.util.Objects;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes2.dex */
public class CommonNotification {
    private PendingIntent actionPendingIntent;
    private String actionText;
    private String channelId;
    private String channelName;
    private CharSequence contentText;
    private CharSequence contentTitle;
    private Context context;
    private boolean enableFloat;
    private boolean enableKeyguard;
    private int importance;
    private boolean isResident;
    private int largeIcon;
    private int messageCount = -1;
    private int notificationIcon;
    private int notifyId;
    private int requestCode;
    private Intent resultIntent;
    private int smallIcon;

    /* loaded from: classes2.dex */
    public static class Builder {
        private CommonNotification cN;

        public Builder(Context context) {
            CommonNotification commonNotification = new CommonNotification();
            this.cN = commonNotification;
            commonNotification.context = context;
        }

        public CommonNotification build() {
            return this.cN;
        }

        public Builder setActionPendingIntent(PendingIntent pendingIntent) {
            this.cN.actionPendingIntent = pendingIntent;
            return this;
        }

        public Builder setActionText(String str) {
            this.cN.actionText = str;
            return this;
        }

        public Builder setChannel(String str, String str2) {
            this.cN.channelId = str;
            this.cN.channelName = str2;
            return this;
        }

        public Builder setContentText(CharSequence charSequence) {
            this.cN.contentText = charSequence;
            return this;
        }

        public Builder setContentTitle(CharSequence charSequence) {
            this.cN.contentTitle = charSequence;
            return this;
        }

        public Builder setEnableFloat(boolean z) {
            this.cN.enableFloat = z;
            return this;
        }

        public Builder setEnableKeyguard(boolean z) {
            this.cN.enableKeyguard = z;
            return this;
        }

        public Builder setImportance(int i) {
            this.cN.importance = i;
            return this;
        }

        public Builder setNotificationIcon(int i) {
            this.cN.notificationIcon = i;
            return this;
        }

        public Builder setNotifyId(int i) {
            this.cN.notifyId = i;
            return this;
        }

        public Builder setResident(boolean z) {
            this.cN.isResident = z;
            return this;
        }

        public Builder setResultIntent(Intent intent, int i) {
            this.cN.resultIntent = intent;
            this.cN.requestCode = i;
            return this;
        }

        public Builder setSmallIcon(int i) {
            this.cN.smallIcon = i;
            return this;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || CommonNotification.class != obj.getClass()) {
            return false;
        }
        CommonNotification commonNotification = (CommonNotification) obj;
        return this.notifyId == commonNotification.notifyId && Objects.equals(this.channelId, commonNotification.channelId) && Objects.equals(this.channelName, commonNotification.channelName);
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.notifyId), this.channelId, this.channelName);
    }

    public void sendCommonNotification(boolean z) {
        if (TextUtils.isEmpty(this.contentTitle) || TextUtils.isEmpty(this.channelId) || TextUtils.isEmpty(this.channelName)) {
            Log.w("CommonNotification", "Params not support!");
            return;
        }
        RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(), R.layout.notification_common_layout);
        int i = this.notificationIcon;
        if (i != 0) {
            remoteViews.setImageViewResource(R.id.notification_icon, i);
        }
        if (TextUtils.isEmpty(this.contentText)) {
            remoteViews.setViewVisibility(R.id.notification_summary, 8);
        } else {
            remoteViews.setTextViewText(R.id.notification_summary, this.contentText);
        }
        remoteViews.setTextViewText(R.id.notification_text, this.contentTitle);
        if (TextUtils.isEmpty(this.actionText)) {
            remoteViews.setViewVisibility(R.id.notification_button, 8);
            if (this.largeIcon != 0) {
                int i2 = R.id.large_icon;
                remoteViews.setViewVisibility(i2, 0);
                remoteViews.setImageViewResource(i2, this.largeIcon);
            }
        } else {
            remoteViews.setTextViewText(R.id.notification_button, this.actionText);
        }
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        NotificationUtils.createNotificationChannel(notificationManager, this.channelId, this.channelName, this.importance);
        Notification.Builder createNotificationBuilder = NotificationUtils.createNotificationBuilder(this.context, this.channelId);
        createNotificationBuilder.setWhen(System.currentTimeMillis());
        createNotificationBuilder.setPriority(this.importance);
        createNotificationBuilder.setSmallIcon(this.smallIcon);
        Intent intent = this.resultIntent;
        if (intent != null) {
            createNotificationBuilder.setContentIntent(PendingIntent.getActivity(this.context, this.requestCode, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE));
        }
        PendingIntent pendingIntent = this.actionPendingIntent;
        if (pendingIntent != null) {
            remoteViews.setOnClickPendingIntent(R.id.notification_button, pendingIntent);
        }
        createNotificationBuilder.setContent(remoteViews);
        createNotificationBuilder.build();
        Notification build = createNotificationBuilder.build();
        int i3 = build.flags | 16;
        build.flags = i3;
        if (this.isResident) {
            build.flags = i3 | 32;
        }
        NotificationUtils.setEnableFloat(build, z);
        NotificationUtils.setEnableKeyguard(build, this.enableKeyguard);
        NotificationUtils.setCustomizedIcon(build, true);
        int i4 = this.messageCount;
        if (i4 >= 0) {
            NotificationUtils.setMessageCount(build, i4);
        }
        notificationManager.notify(this.notifyId, build);
    }

    public void show() {
        if (TextUtils.isEmpty(this.contentTitle) || TextUtils.isEmpty(this.channelId) || TextUtils.isEmpty(this.channelName)) {
            Log.w("CommonNotification", "Params not support!");
        } else {
            sendCommonNotification(this.enableFloat);
        }
    }
}
