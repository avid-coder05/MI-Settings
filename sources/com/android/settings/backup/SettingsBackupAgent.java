package com.android.settings.backup;

import android.app.backup.FullBackupDataOutput;
import android.os.ParcelFileDescriptor;
import java.io.IOException;
import miui.app.backup.BackupMeta;
import miui.app.backup.FullBackupAgent;

/* loaded from: classes.dex */
public class SettingsBackupAgent extends FullBackupAgent {
    private AgentBase mAgent;

    private void initAgent(int i) {
        if (i == 1) {
            this.mAgent = new SettingsAgent(this);
        } else if (i == 2) {
            this.mAgent = new WifiAgent(this);
        } else if (i != 3) {
        } else {
            this.mAgent = new AccountAgent(this);
        }
    }

    protected int getVersion(int i) {
        if (this.mAgent == null) {
            initAgent(i);
        }
        return this.mAgent.getBackupVersion();
    }

    protected int onAttachRestore(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor, String str) {
        AgentBase agentBase = this.mAgent;
        if (agentBase != null) {
            return agentBase.restoreAttaches(backupMeta, parcelFileDescriptor, str);
        }
        return 1;
    }

    protected int onDataRestore(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        if (this.mAgent == null) {
            initAgent(backupMeta.feature);
        }
        return this.mAgent.restoreData(backupMeta, parcelFileDescriptor);
    }

    protected int onFullBackup(ParcelFileDescriptor parcelFileDescriptor, int i) throws IOException {
        if (this.mAgent == null) {
            initAgent(i);
        }
        return this.mAgent.fullBackup(parcelFileDescriptor);
    }

    protected int onRestoreEnd(BackupMeta backupMeta) throws IOException {
        AgentBase agentBase = this.mAgent;
        if (agentBase != null) {
            return agentBase.endRestore(backupMeta);
        }
        return 1;
    }

    protected int tarAttaches(String str, FullBackupDataOutput fullBackupDataOutput, int i) throws IOException {
        int tarAttaches = super.tarAttaches(str, fullBackupDataOutput, i);
        if (tarAttaches == 0) {
            AgentBase agentBase = this.mAgent;
            if (agentBase != null) {
                return agentBase.tarAttaches(str, fullBackupDataOutput);
            }
            return 1;
        }
        return tarAttaches;
    }
}
