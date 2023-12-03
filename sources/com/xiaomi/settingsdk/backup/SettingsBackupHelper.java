package com.xiaomi.settingsdk.backup;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import com.xiaomi.settingsdk.backup.data.DataPackage;
import java.io.IOException;

/* loaded from: classes2.dex */
public class SettingsBackupHelper {
    SettingsBackupHelper() {
        throw new RuntimeException("Stub!");
    }

    public static DataPackage backupSettings(Context context, ParcelFileDescriptor parcelFileDescriptor, ICloudBackup iCloudBackup) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public static void restoreFiles(DataPackage dataPackage) {
        throw new RuntimeException("Stub!");
    }

    public static void restoreOneFile(String str, ParcelFileDescriptor parcelFileDescriptor) {
        throw new RuntimeException("Stub!");
    }

    public static void restoreSettings(Context context, ParcelFileDescriptor parcelFileDescriptor, ICloudBackup iCloudBackup) throws IOException {
        throw new RuntimeException("Stub!");
    }
}
