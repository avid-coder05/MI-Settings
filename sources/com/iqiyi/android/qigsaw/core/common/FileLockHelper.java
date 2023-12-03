package com.iqiyi.android.qigsaw.core.common;

import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

/* loaded from: classes2.dex */
class FileLockHelper implements Closeable {
    private static final int LOCK_WAIT_EACH_TIME = 10;
    private static final String TAG = "Split.FileLockHelper";
    private final FileLock fileLock;
    private final FileOutputStream outputStream;

    private FileLockHelper(File file) throws IOException {
        this.outputStream = new FileOutputStream(file);
        FileLock fileLock = null;
        Exception e = null;
        int i = 0;
        while (i < 3) {
            i++;
            try {
                fileLock = this.outputStream.getChannel().lock();
                if (fileLock != null) {
                    break;
                }
            } catch (Exception e2) {
                e = e2;
                Log.e(TAG, "getInfoLock Thread failed time:10");
            }
            try {
                Thread.sleep(10L);
            } catch (Exception e3) {
                Log.e(TAG, "getInfoLock Thread sleep exception", e3);
            }
        }
        if (fileLock != null) {
            this.fileLock = fileLock;
            return;
        }
        throw new IOException("Tinker Exception:FileLockHelper lock file failed: " + file.getAbsolutePath(), e);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FileLockHelper getFileLock(File file) throws IOException {
        return new FileLockHelper(file);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            FileLock fileLock = this.fileLock;
            if (fileLock != null) {
                fileLock.release();
            }
        } finally {
            FileOutputStream fileOutputStream = this.outputStream;
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }
}
