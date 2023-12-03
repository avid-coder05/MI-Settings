package miui.cloud.backup;

import android.content.Context;
import miui.cloud.backup.data.DataPackage;

/* loaded from: classes3.dex */
public interface ICloudBackup {
    int getCurrentVersion(Context context);

    void onBackupSettings(Context context, DataPackage dataPackage);

    void onRestoreSettings(Context context, DataPackage dataPackage, int i);
}
