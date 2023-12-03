package com.android.settings.backup;

import android.app.backup.FullBackupDataOutput;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.android.settings.cloudbackup.SettingsCloudBackupImpl;
import com.xiaomi.settingsdk.backup.SettingsBackupHelper;
import com.xiaomi.settingsdk.backup.data.DataPackage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import miui.app.backup.BackupMeta;
import miui.app.backup.FullBackupAgent;

/* loaded from: classes.dex */
public class SettingsAgent extends AgentBase {
    private final int VERSION_SETTINGS_AGENT;
    private AgentV1 mV1;
    private AgentV2 mV2;

    /* loaded from: classes.dex */
    private class AgentV1 {
        private File mAttachDir;
        private HashMap<String, String> mFileName2Path;
        private SettingManager mSettingManager;

        private AgentV1() {
        }

        public int endRestore() {
            this.mSettingManager.setFileName2Path(this.mFileName2Path);
            this.mSettingManager.restoreSysData();
            this.mSettingManager.restoreRingtone();
            return 0;
        }

        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:57:0x0084 -> B:58:0x0087). Please submit an issue!!! */
        public int restoreAttaches(ParcelFileDescriptor parcelFileDescriptor, String str) {
            FileInputStream fileInputStream;
            FileOutputStream fileOutputStream;
            File file = new File(this.mAttachDir, str);
            FileOutputStream fileOutputStream2 = null;
            try {
                try {
                    try {
                        fileOutputStream = new FileOutputStream(file);
                        try {
                            fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                        } catch (IOException e) {
                            e = e;
                            fileInputStream = null;
                        } catch (IllegalArgumentException e2) {
                            e = e2;
                            fileInputStream = null;
                        } catch (Throwable th) {
                            th = th;
                            fileInputStream = null;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (IOException e3) {
                    e = e3;
                    fileInputStream = null;
                } catch (IllegalArgumentException e4) {
                    e = e4;
                    fileInputStream = null;
                } catch (Throwable th3) {
                    th = th3;
                    fileInputStream = null;
                }
            } catch (IOException e5) {
                Log.e("Backup:SettingsAgent", "IOException", e5);
            }
            try {
                byte[] bArr = new byte[8192];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read <= 0) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
                this.mFileName2Path.put(file.getName(), file.getAbsolutePath());
                try {
                    fileOutputStream.close();
                } catch (IOException e6) {
                    Log.e("Backup:SettingsAgent", "IOException", e6);
                }
                fileInputStream.close();
            } catch (IOException e7) {
                e = e7;
                fileOutputStream2 = fileOutputStream;
                Log.e("Backup:SettingsAgent", "IOException", e);
                if (fileOutputStream2 != null) {
                    try {
                        fileOutputStream2.close();
                    } catch (IOException e8) {
                        Log.e("Backup:SettingsAgent", "IOException", e8);
                    }
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                return 0;
            } catch (IllegalArgumentException e9) {
                e = e9;
                fileOutputStream2 = fileOutputStream;
                Log.e("Backup:SettingsAgent", "IllegalArgumentException", e);
                if (fileOutputStream2 != null) {
                    try {
                        fileOutputStream2.close();
                    } catch (IOException e10) {
                        Log.e("Backup:SettingsAgent", "IOException", e10);
                    }
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                return 0;
            } catch (Throwable th4) {
                th = th4;
                fileOutputStream2 = fileOutputStream;
                if (fileOutputStream2 != null) {
                    try {
                        fileOutputStream2.close();
                    } catch (IOException e11) {
                        Log.e("Backup:SettingsAgent", "IOException", e11);
                    }
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                        throw th;
                    } catch (IOException e12) {
                        Log.e("Backup:SettingsAgent", "IOException", e12);
                        throw th;
                    }
                }
                throw th;
            }
            return 0;
        }

        public int restoreData(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
            this.mSettingManager = new SettingManager(SettingsAgent.this.mAgent);
            FileInputStream fileInputStream = null;
            try {
                FileInputStream fileInputStream2 = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                try {
                    SyncRootProtos$SyncRoot parseFrom = SyncRootProtos$SyncRoot.parseFrom(fileInputStream2);
                    SettingProtos$Settings setting = parseFrom != null ? parseFrom.getSetting() : null;
                    if (setting == null) {
                        fileInputStream2.close();
                        return 6;
                    }
                    Iterator<SettingProtos$SystemSetting> it = setting.getSystemList().iterator();
                    while (it.hasNext()) {
                        try {
                            this.mSettingManager.addSystemSetting(it.next());
                        } catch (Exception e) {
                            Log.e("Backup:SettingsAgent", "Cannot add system setting ", e);
                        }
                    }
                    Iterator<SettingProtos$SecureSetting> it2 = setting.getSecureList().iterator();
                    while (it2.hasNext()) {
                        try {
                            this.mSettingManager.addSecureSetting(it2.next());
                        } catch (Exception e2) {
                            Log.e("Backup:SettingsAgent", "Cannot add secure setting ", e2);
                        }
                    }
                    Iterator<SettingProtos$LockSetting> it3 = setting.getLockList().iterator();
                    while (it3.hasNext()) {
                        try {
                            this.mSettingManager.addLockSetting(it3.next());
                        } catch (Exception e3) {
                            Log.e("Backup:SettingsAgent", "Cannot add lock setting ", e3);
                        }
                    }
                    fileInputStream2.close();
                    this.mFileName2Path = new HashMap<>();
                    File file = new File(SettingsAgent.this.mAgent.getCacheDir(), "_tmp_attach");
                    this.mAttachDir = file;
                    if (file.exists()) {
                        return 0;
                    }
                    this.mAttachDir.mkdirs();
                    return 0;
                } catch (Throwable th) {
                    th = th;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    /* loaded from: classes.dex */
    private class AgentV2 {
        private AgentV2() {
        }

        public int fullBackup(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
            DataPackage backupSettings = SettingsBackupHelper.backupSettings(SettingsAgent.this.mAgent, parcelFileDescriptor, new SettingsCloudBackupImpl());
            Log.d("Backup:SettingsAgent", "backup attach count: " + backupSettings.getFileItems().size());
            Iterator<String> it = backupSettings.getFileItems().keySet().iterator();
            while (it.hasNext()) {
                SettingsAgent.this.mAgent.addAttachedFile(it.next());
            }
            return 0;
        }

        public int restoreData(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
            SettingsBackupHelper.restoreSettings(SettingsAgent.this.mAgent, parcelFileDescriptor, new SettingsCloudBackupImpl());
            return 0;
        }
    }

    public SettingsAgent(FullBackupAgent fullBackupAgent) {
        super(fullBackupAgent);
        this.VERSION_SETTINGS_AGENT = 2;
    }

    @Override // com.android.settings.backup.AgentBase
    public int endRestore(BackupMeta backupMeta) {
        int i = backupMeta.version;
        if (i == 1) {
            AgentV1 agentV1 = this.mV1;
            if (agentV1 == null) {
                return 1;
            }
            agentV1.endRestore();
        } else if (i != 2) {
            return 4;
        } else {
            if (this.mV2 == null) {
                return 1;
            }
        }
        return 0;
    }

    @Override // com.android.settings.backup.AgentBase
    public int fullBackup(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        Log.d("Backup:SettingsAgent", "full backup");
        AgentV2 agentV2 = new AgentV2();
        this.mV2 = agentV2;
        agentV2.fullBackup(parcelFileDescriptor);
        return 0;
    }

    @Override // com.android.settings.backup.AgentBase
    public int getBackupVersion() {
        return 2;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0018  */
    /* JADX WARN: Removed duplicated region for block: B:17:? A[RETURN, SYNTHETIC] */
    @Override // com.android.settings.backup.AgentBase
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int restoreAttaches(miui.app.backup.BackupMeta r3, android.os.ParcelFileDescriptor r4, java.lang.String r5) {
        /*
            r2 = this;
            int r3 = r3.version
            r0 = 1
            if (r3 == r0) goto La
            r1 = 2
            if (r3 == r1) goto L11
            r0 = 4
            goto L1c
        La:
            com.android.settings.backup.SettingsAgent$AgentV1 r3 = r2.mV1
            if (r3 == 0) goto L13
            r3.restoreAttaches(r4, r5)
        L11:
            r3 = 0
            goto L14
        L13:
            r3 = r0
        L14:
            com.android.settings.backup.SettingsAgent$AgentV2 r2 = r2.mV2
            if (r2 == 0) goto L1c
            com.xiaomi.settingsdk.backup.SettingsBackupHelper.restoreOneFile(r5, r4)
            r0 = r3
        L1c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.backup.SettingsAgent.restoreAttaches(miui.app.backup.BackupMeta, android.os.ParcelFileDescriptor, java.lang.String):int");
    }

    @Override // com.android.settings.backup.AgentBase
    public int restoreData(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        int i = backupMeta.version;
        if (i == 1) {
            AgentV1 agentV1 = new AgentV1();
            this.mV1 = agentV1;
            agentV1.restoreData(parcelFileDescriptor);
        } else if (i != 2) {
            return 4;
        } else {
            Log.d("Backup:SettingsAgent", "restore data");
            AgentV2 agentV2 = new AgentV2();
            this.mV2 = agentV2;
            agentV2.restoreData(parcelFileDescriptor);
        }
        return 0;
    }

    @Override // com.android.settings.backup.AgentBase
    public int tarAttaches(String str, FullBackupDataOutput fullBackupDataOutput) {
        return this.mV2 != null ? 0 : 1;
    }
}
