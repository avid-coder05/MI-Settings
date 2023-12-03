package com.xiaomi.settingsdk.backup;

import android.content.Context;
import com.xiaomi.settingsdk.backup.data.DataPackage;

/* loaded from: classes2.dex */
public interface ICloudBackup {
    int getCurrentVersion(Context context);

    void onBackupSettings(Context context, DataPackage dataPackage);

    void onRestoreSettings(Context context, DataPackage dataPackage, int i);
}
