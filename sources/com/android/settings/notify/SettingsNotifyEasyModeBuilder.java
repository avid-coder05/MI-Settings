package com.android.settings.notify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class SettingsNotifyEasyModeBuilder {
    private static SettingsNotifyEasyModeBuilder builder;

    /* loaded from: classes2.dex */
    public class SettingsNotify {
        int notifyId;
        int shownResId;
        Intent targetIntent;

        public SettingsNotify() {
        }

        public void goToTarget(Activity activity) {
            if (activity.getPackageManager().resolveActivity(this.targetIntent, 0) != null) {
                activity.startActivity(this.targetIntent);
            }
        }

        public void setNotifyId(int i) {
            this.notifyId = i;
        }

        public void setShownResId(int i) {
            this.shownResId = i;
        }

        public void setTargetIntent(Intent intent) {
            this.targetIntent = intent;
        }
    }

    public static SettingsNotifyEasyModeBuilder getInstance() {
        if (builder == null) {
            builder = new SettingsNotifyEasyModeBuilder();
        }
        return builder;
    }

    private SettingsNotify tryBuild(Context context, int i) {
        if (SettingsNotifyHelper.isEasyModeToNotify(context)) {
            SettingsNotify settingsNotify = new SettingsNotify();
            settingsNotify.setTargetIntent(new Intent("com.xiaomi.action.ENTER_ELDERLY_MODE"));
            settingsNotify.setShownResId(R.string.easymode_hint);
            settingsNotify.setNotifyId(i);
            return settingsNotify;
        }
        return null;
    }

    public SettingsNotify build(Context context) {
        return tryBuild(context, 0);
    }
}
