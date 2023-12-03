package com.android.settings.backup;

import android.app.backup.FullBackupDataOutput;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import miui.app.backup.BackupMeta;
import miui.app.backup.FullBackupAgent;

/* loaded from: classes.dex */
public class AccountAgent extends AgentBase {
    private File mAttachDir;

    public AccountAgent(FullBackupAgent fullBackupAgent) {
        super(fullBackupAgent);
    }

    @Override // com.android.settings.backup.AgentBase
    public int endRestore(BackupMeta backupMeta) {
        AccountRestoreManager accountRestoreManager = new AccountRestoreManager(this.mAgent.getApplicationContext());
        accountRestoreManager.setAttachDir(this.mAttachDir);
        File file = new File(this.mAttachDir, AccountRestoreManager.ACCOUNTS_DB_FILE);
        if (!file.exists()) {
            Log.e("Backup:AccountAgent", "account not exist.");
            return 0;
        }
        accountRestoreManager.prepareImport(file);
        accountRestoreManager.importData();
        accountRestoreManager.setActiveAdmin();
        file.delete();
        return 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:85:0x00e8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:91:0x00de A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v22 */
    /* JADX WARN: Type inference failed for: r8v6, types: [java.io.InputStream] */
    @Override // com.android.settings.backup.AgentBase
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int fullBackup(android.os.ParcelFileDescriptor r8) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 244
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.backup.AccountAgent.fullBackup(android.os.ParcelFileDescriptor):int");
    }

    @Override // com.android.settings.backup.AgentBase
    public int getBackupVersion() {
        return 1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v12 */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* JADX WARN: Type inference failed for: r1v14 */
    /* JADX WARN: Type inference failed for: r1v15 */
    /* JADX WARN: Type inference failed for: r1v16 */
    /* JADX WARN: Type inference failed for: r1v17, types: [java.io.FileInputStream, java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v19 */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v20 */
    /* JADX WARN: Type inference failed for: r1v21 */
    /* JADX WARN: Type inference failed for: r1v22 */
    /* JADX WARN: Type inference failed for: r1v23 */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v8, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v9 */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:57:0x0077 -> B:73:0x007a). Please submit an issue!!! */
    @Override // com.android.settings.backup.AgentBase
    public int restoreAttaches(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor, String str) {
        int read;
        ?? file = new File(this.mAttachDir, str);
        FileOutputStream fileOutputStream = null;
        fileOutputStream = null;
        fileOutputStream = null;
        fileOutputStream = null;
        fileOutputStream = null;
        try {
            try {
                try {
                    FileOutputStream fileOutputStream2 = new FileOutputStream((File) file);
                    try {
                        file = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                        try {
                            byte[] bArr = new byte[8192];
                            while (true) {
                                read = file.read(bArr);
                                if (read > 0) {
                                    fileOutputStream2.write(bArr, 0, read);
                                } else {
                                    try {
                                        break;
                                    } catch (IOException e) {
                                        Log.e("Backup:AccountAgent", "IOException", e);
                                    }
                                }
                            }
                            fileOutputStream2.close();
                            file.close();
                            file = file;
                            fileOutputStream = read;
                        } catch (IOException e2) {
                            e = e2;
                            fileOutputStream = fileOutputStream2;
                            file = file;
                            Log.e("Backup:AccountAgent", "IOException", e);
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e3) {
                                    Log.e("Backup:AccountAgent", "IOException", e3);
                                }
                            }
                            if (file != 0) {
                                file.close();
                                file = file;
                                fileOutputStream = fileOutputStream;
                            }
                            return 0;
                        } catch (IllegalArgumentException e4) {
                            e = e4;
                            fileOutputStream = fileOutputStream2;
                            file = file;
                            Log.e("Backup:AccountAgent", "IllegalArgumentException", e);
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e5) {
                                    Log.e("Backup:AccountAgent", "IOException", e5);
                                }
                            }
                            if (file != 0) {
                                file.close();
                                file = file;
                                fileOutputStream = fileOutputStream;
                            }
                            return 0;
                        } catch (Throwable th) {
                            th = th;
                            fileOutputStream = fileOutputStream2;
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e6) {
                                    Log.e("Backup:AccountAgent", "IOException", e6);
                                }
                            }
                            if (file != 0) {
                                try {
                                    file.close();
                                    throw th;
                                } catch (IOException e7) {
                                    Log.e("Backup:AccountAgent", "IOException", e7);
                                    throw th;
                                }
                            }
                            throw th;
                        }
                    } catch (IOException e8) {
                        e = e8;
                        file = 0;
                    } catch (IllegalArgumentException e9) {
                        e = e9;
                        file = 0;
                    } catch (Throwable th2) {
                        th = th2;
                        file = 0;
                    }
                } catch (IOException e10) {
                    Log.e("Backup:AccountAgent", "IOException", e10);
                    file = file;
                    fileOutputStream = fileOutputStream;
                }
            } catch (IOException e11) {
                e = e11;
                file = 0;
            } catch (IllegalArgumentException e12) {
                e = e12;
                file = 0;
            } catch (Throwable th3) {
                th = th3;
                file = 0;
            }
            return 0;
        } catch (Throwable th4) {
            th = th4;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v10, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v12 */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* JADX WARN: Type inference failed for: r1v14 */
    /* JADX WARN: Type inference failed for: r1v15 */
    /* JADX WARN: Type inference failed for: r1v16 */
    /* JADX WARN: Type inference failed for: r1v17 */
    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v19, types: [java.io.FileInputStream, java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r1v21 */
    /* JADX WARN: Type inference failed for: r1v22 */
    /* JADX WARN: Type inference failed for: r1v23 */
    /* JADX WARN: Type inference failed for: r1v24 */
    /* JADX WARN: Type inference failed for: r1v25 */
    /* JADX WARN: Type inference failed for: r1v26 */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7 */
    /* JADX WARN: Type inference failed for: r1v8 */
    /* JADX WARN: Type inference failed for: r1v9, types: [java.io.InputStream] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:60:0x0093 -> B:76:0x0096). Please submit an issue!!! */
    @Override // com.android.settings.backup.AgentBase
    public int restoreData(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        int read;
        File file = new File(this.mAgent.getCacheDir(), "_tmp_attach");
        this.mAttachDir = file;
        if (!file.exists()) {
            this.mAttachDir.mkdirs();
        }
        ?? file2 = new File(this.mAttachDir, AccountRestoreManager.ACCOUNTS_DB_FILE);
        FileOutputStream fileOutputStream = null;
        fileOutputStream = null;
        fileOutputStream = null;
        fileOutputStream = null;
        fileOutputStream = null;
        try {
            try {
                try {
                    FileOutputStream fileOutputStream2 = new FileOutputStream((File) file2);
                    try {
                        file2 = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                        try {
                            byte[] bArr = new byte[8192];
                            while (true) {
                                read = file2.read(bArr);
                                if (read > 0) {
                                    fileOutputStream2.write(bArr, 0, read);
                                } else {
                                    try {
                                        break;
                                    } catch (IOException e) {
                                        Log.e("Backup:AccountAgent", "IOException", e);
                                    }
                                }
                            }
                            fileOutputStream2.close();
                            file2.close();
                            file2 = file2;
                            fileOutputStream = read;
                        } catch (IOException e2) {
                            e = e2;
                            fileOutputStream = fileOutputStream2;
                            file2 = file2;
                            Log.e("Backup:AccountAgent", "IOException", e);
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e3) {
                                    Log.e("Backup:AccountAgent", "IOException", e3);
                                }
                            }
                            if (file2 != 0) {
                                file2.close();
                                file2 = file2;
                                fileOutputStream = fileOutputStream;
                            }
                            return 0;
                        } catch (IllegalArgumentException e4) {
                            e = e4;
                            fileOutputStream = fileOutputStream2;
                            file2 = file2;
                            Log.e("Backup:AccountAgent", "IllegalArgumentException", e);
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e5) {
                                    Log.e("Backup:AccountAgent", "IOException", e5);
                                }
                            }
                            if (file2 != 0) {
                                file2.close();
                                file2 = file2;
                                fileOutputStream = fileOutputStream;
                            }
                            return 0;
                        } catch (Throwable th) {
                            th = th;
                            fileOutputStream = fileOutputStream2;
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e6) {
                                    Log.e("Backup:AccountAgent", "IOException", e6);
                                }
                            }
                            if (file2 != 0) {
                                try {
                                    file2.close();
                                    throw th;
                                } catch (IOException e7) {
                                    Log.e("Backup:AccountAgent", "IOException", e7);
                                    throw th;
                                }
                            }
                            throw th;
                        }
                    } catch (IOException e8) {
                        e = e8;
                        file2 = 0;
                    } catch (IllegalArgumentException e9) {
                        e = e9;
                        file2 = 0;
                    } catch (Throwable th2) {
                        th = th2;
                        file2 = 0;
                    }
                } catch (IOException e10) {
                    Log.e("Backup:AccountAgent", "IOException", e10);
                    file2 = file2;
                    fileOutputStream = fileOutputStream;
                }
            } catch (IOException e11) {
                e = e11;
                file2 = 0;
            } catch (IllegalArgumentException e12) {
                e = e12;
                file2 = 0;
            } catch (Throwable th3) {
                th = th3;
                file2 = 0;
            }
            return 0;
        } catch (Throwable th4) {
            th = th4;
        }
    }

    @Override // com.android.settings.backup.AgentBase
    public int tarAttaches(String str, FullBackupDataOutput fullBackupDataOutput) {
        return 0;
    }
}
