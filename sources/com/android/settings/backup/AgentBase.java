package com.android.settings.backup;

import android.app.backup.FullBackupDataOutput;
import android.os.ParcelFileDescriptor;
import java.io.IOException;
import miui.app.backup.BackupMeta;
import miui.app.backup.FullBackupAgent;

/* loaded from: classes.dex */
public abstract class AgentBase {
    protected FullBackupAgent mAgent;

    public AgentBase(FullBackupAgent fullBackupAgent) {
        this.mAgent = fullBackupAgent;
    }

    public abstract int endRestore(BackupMeta backupMeta);

    public abstract int fullBackup(ParcelFileDescriptor parcelFileDescriptor) throws IOException;

    public abstract int getBackupVersion();

    public abstract int restoreAttaches(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor, String str);

    public abstract int restoreData(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor) throws IOException;

    public abstract int tarAttaches(String str, FullBackupDataOutput fullBackupDataOutput);
}
