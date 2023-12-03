package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitInfoVersionDataStorageImpl implements SplitInfoVersionDataStorage {
    private static final String NEW_VERSION = "newVersion";
    private static final String OLD_VERSION = "oldVersion";
    private static final String TAG = "SplitInfoVersionStorageImpl";
    private static final String VERSION_DATA_LOCK_NAME = "version.lock";
    private static final String VERSION_DATA_NAME = "version.info";
    private final FileLock cacheLock;
    private final FileChannel lockChannel;
    private final RandomAccessFile lockRaf;
    private final File versionDataFile;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInfoVersionDataStorageImpl(File file) throws IOException {
        this.versionDataFile = new File(file, VERSION_DATA_NAME);
        File file2 = new File(file, VERSION_DATA_LOCK_NAME);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
        this.lockRaf = randomAccessFile;
        try {
            FileChannel channel = randomAccessFile.getChannel();
            this.lockChannel = channel;
            try {
                SplitLog.i(TAG, "Blocking on lock " + file2.getPath(), new Object[0]);
                this.cacheLock = channel.lock();
                SplitLog.i(TAG, file2.getPath() + " locked", new Object[0]);
            } catch (IOException | Error | RuntimeException e) {
                FileUtil.closeQuietly(this.lockChannel);
                throw e;
            }
        } catch (IOException | Error | RuntimeException e2) {
            FileUtil.closeQuietly(this.lockRaf);
            throw e2;
        }
    }

    private static SplitInfoVersionData readVersionDataProperties(File file) {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2 = null;
        int i = 0;
        boolean z = false;
        String str = null;
        String str2 = null;
        while (i < 3 && !z) {
            i++;
            Properties properties = new Properties();
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    try {
                        properties.load(fileInputStream);
                        str = properties.getProperty(OLD_VERSION);
                        str2 = properties.getProperty(NEW_VERSION);
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream2 = fileInputStream;
                        FileUtil.closeQuietly(fileInputStream2);
                        throw th;
                    }
                } catch (IOException e) {
                    e = e;
                    SplitLog.w(TAG, "read property failed, e:" + e, new Object[0]);
                    FileUtil.closeQuietly(fileInputStream);
                    if (str != null) {
                        z = true;
                    }
                }
            } catch (IOException e2) {
                e = e2;
                fileInputStream = null;
            } catch (Throwable th2) {
                th = th2;
            }
            FileUtil.closeQuietly(fileInputStream);
            if (str != null && str2 != null) {
                z = true;
            }
        }
        if (z) {
            return new SplitInfoVersionData(str, str2);
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x00cf A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0047 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean updateVersionDataProperties(java.io.File r7, com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionData r8) {
        /*
            r6 = this;
            r6 = 0
            if (r7 == 0) goto Ld9
            if (r8 != 0) goto L7
            goto Ld9
        L7:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "updateVersionDataProperties file path:"
            r0.append(r1)
            java.lang.String r1 = r7.getAbsolutePath()
            r0.append(r1)
            java.lang.String r1 = " , oldVer:"
            r0.append(r1)
            java.lang.String r1 = r8.oldVersion
            r0.append(r1)
            java.lang.String r1 = ", newVer:"
            r0.append(r1)
            java.lang.String r1 = r8.newVersion
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = "SplitInfoVersionStorageImpl"
            com.iqiyi.android.qigsaw.core.common.SplitLog.i(r2, r0, r1)
            java.io.File r0 = r7.getParentFile()
            boolean r1 = r0.exists()
            if (r1 != 0) goto L45
            r0.mkdirs()
        L45:
            r0 = r6
            r1 = r0
        L47:
            r3 = 3
            if (r0 >= r3) goto Ld8
            if (r1 != 0) goto Ld8
            int r0 = r0 + 1
            java.util.Properties r1 = new java.util.Properties
            r1.<init>()
            java.lang.String r3 = r8.oldVersion
            java.lang.String r4 = "oldVersion"
            r1.put(r4, r3)
            java.lang.String r3 = r8.newVersion
            java.lang.String r4 = "newVersion"
            r1.put(r4, r3)
            r3 = 0
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L93 java.lang.Exception -> L95
            r4.<init>(r7, r6)     // Catch: java.lang.Throwable -> L93 java.lang.Exception -> L95
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            r3.<init>()     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            java.lang.String r5 = "from old version:"
            r3.append(r5)     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            java.lang.String r5 = r8.oldVersion     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            r3.append(r5)     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            java.lang.String r5 = " to new version:"
            r3.append(r5)     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            java.lang.String r5 = r8.newVersion     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            r3.append(r5)     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            r1.store(r4, r3)     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            com.iqiyi.android.qigsaw.core.common.FileUtil.closeQuietly(r4)
            goto Lb0
        L8d:
            r6 = move-exception
            r3 = r4
            goto Ld4
        L90:
            r1 = move-exception
            r3 = r4
            goto L96
        L93:
            r6 = move-exception
            goto Ld4
        L95:
            r1 = move-exception
        L96:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L93
            r4.<init>()     // Catch: java.lang.Throwable -> L93
            java.lang.String r5 = "write property failed, e:"
            r4.append(r5)     // Catch: java.lang.Throwable -> L93
            r4.append(r1)     // Catch: java.lang.Throwable -> L93
            java.lang.String r1 = r4.toString()     // Catch: java.lang.Throwable -> L93
            java.lang.Object[] r4 = new java.lang.Object[r6]     // Catch: java.lang.Throwable -> L93
            com.iqiyi.android.qigsaw.core.common.SplitLog.w(r2, r1, r4)     // Catch: java.lang.Throwable -> L93
            com.iqiyi.android.qigsaw.core.common.FileUtil.closeQuietly(r3)
        Lb0:
            com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionData r1 = readVersionDataProperties(r7)
            if (r1 == 0) goto Lcc
            java.lang.String r3 = r1.oldVersion
            java.lang.String r4 = r8.oldVersion
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto Lcc
            java.lang.String r1 = r1.newVersion
            java.lang.String r3 = r8.newVersion
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto Lcc
            r1 = 1
            goto Lcd
        Lcc:
            r1 = r6
        Lcd:
            if (r1 != 0) goto L47
            r7.delete()
            goto L47
        Ld4:
            com.iqiyi.android.qigsaw.core.common.FileUtil.closeQuietly(r3)
            throw r6
        Ld8:
            return r1
        Ld9:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionDataStorageImpl.updateVersionDataProperties(java.io.File, com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionData):boolean");
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.lockChannel.close();
        this.lockRaf.close();
        this.cacheLock.release();
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionDataStorage
    public SplitInfoVersionData readVersionData() {
        if (this.cacheLock.isValid()) {
            if (this.versionDataFile.exists()) {
                return readVersionDataProperties(this.versionDataFile);
            }
            return null;
        }
        throw new IllegalStateException("SplitInfoVersionDataStorage was closed");
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionDataStorage
    public boolean updateVersionData(SplitInfoVersionData splitInfoVersionData) {
        if (this.cacheLock.isValid()) {
            return updateVersionDataProperties(this.versionDataFile, splitInfoVersionData);
        }
        throw new IllegalStateException("SplitInfoVersionDataStorage was closed");
    }
}
