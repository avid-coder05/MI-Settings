package com.android.settings.dangerousoptions;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;

/* loaded from: classes.dex */
public class DangerousOptionsWarningService extends Service {
    private ContentObserver mObserver;
    private Handler mHandler = new Handler() { // from class: com.android.settings.dangerousoptions.DangerousOptionsWarningService.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            DangerousOptionsUtil.sendNotificationIfNeeded(DangerousOptionsWarningService.this);
        }
    };
    private final Runnable mSystemPropertiesChanged = new Runnable() { // from class: com.android.settings.dangerousoptions.DangerousOptionsWarningService.2
        @Override // java.lang.Runnable
        public void run() {
            DangerousOptionsWarningService.this.mHandler.removeMessages(1);
            DangerousOptionsWarningService.this.mHandler.sendEmptyMessageDelayed(1, 500L);
        }
    };

    /* loaded from: classes.dex */
    private class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            DangerousOptionsWarningService.this.mHandler.removeMessages(1);
            DangerousOptionsWarningService.this.mHandler.sendEmptyMessageDelayed(1, 500L);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        SystemProperties.addChangeCallback(this.mSystemPropertiesChanged);
        this.mObserver = new SettingsObserver(this.mHandler);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("always_finish_activities"), false, this.mObserver);
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_enabled"), false, this.mObserver);
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("enabled_accessibility_services"), false, this.mObserver);
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("high_text_contrast_enabled"), false, this.mObserver);
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mHandler.removeMessages(1);
        getContentResolver().unregisterContentObserver(this.mObserver);
        super.onDestroy();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        DangerousOptionsUtil.sendNotificationIfNeeded(this);
        return super.onStartCommand(intent, i, i2);
    }
}
