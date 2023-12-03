package com.android.settings.cloudbackup;

import com.xiaomi.settingsdk.backup.CloudBackupServiceBase;
import com.xiaomi.settingsdk.backup.ICloudBackup;

/* loaded from: classes.dex */
public class SettingsCloudBackupService extends CloudBackupServiceBase {
    @Override // com.xiaomi.settingsdk.backup.CloudBackupServiceBase
    protected ICloudBackup getBackupImpl() {
        return new SettingsCloudBackupImpl();
    }
}
