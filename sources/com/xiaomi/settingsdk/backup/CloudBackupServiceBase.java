package com.xiaomi.settingsdk.backup;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

/* loaded from: classes2.dex */
public abstract class CloudBackupServiceBase extends IntentService {
    @Deprecated
    public static final String ACTION_CLOUD_BACKUP_SETTINGS = "miui.action.CLOUD_BACKUP_SETTINGS";
    @Deprecated
    public static final String ACTION_CLOUD_RESTORE_SETTINGS = "miui.action.CLOUD_RESTORE_SETTINGS";
    @Deprecated
    public static final String KEY_RESULT_RECEIVER = "result_receiver";

    public CloudBackupServiceBase() {
        super(null);
        throw new RuntimeException("Stub!");
    }

    protected abstract ICloudBackup getBackupImpl();

    @Override // android.app.IntentService, android.app.Service
    public IBinder onBind(Intent intent) {
        throw new RuntimeException("Stub!");
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        throw new RuntimeException("Stub!");
    }

    @Override // android.app.IntentService, android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        throw new RuntimeException("Stub!");
    }
}
