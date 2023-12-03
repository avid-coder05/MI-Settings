package com.iqiyi.android.qigsaw.core.splitinstall;

import android.content.Context;
import android.text.TextUtils;
import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
final class SplitDownloadPreprocessor implements Closeable {
    private static final String LOCK_FILENAME = "SplitCopier.lock";
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final String TAG = "SplitDownloadPreprocessor";
    private final FileLock cacheLock;
    private final FileChannel lockChannel;
    private final RandomAccessFile lockRaf;
    private final File splitDir;

    /* loaded from: classes2.dex */
    static final class SplitFile extends File {
        long realSize;

        SplitFile(File file, String str, long j) {
            super(file, str);
            this.realSize = j;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitDownloadPreprocessor(File file) throws IOException {
        this.splitDir = file;
        File file2 = new File(file, LOCK_FILENAME);
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

    private static boolean checkSplitMD5(SplitInfo.ApkData apkData, File file) {
        String md5 = FileUtil.getMD5(file);
        return TextUtils.isEmpty(md5) ? apkData.getSize() == file.length() : apkData.getMd5().equals(md5);
    }

    private static void copyBuiltInSplit(Context context, String str, SplitInfo.ApkData apkData, File file) throws IOException {
        File createTempFile = File.createTempFile("tmp-" + str, SplitConstants.DOT_APK, SplitPathManager.require().getSplitTmpDir());
        String str2 = "qigsaw/" + str + "-" + apkData.getAbi() + SplitConstants.DOT_ZIP;
        boolean z = false;
        int i = 0;
        while (!z && i < 3) {
            i++;
            InputStream inputStream = null;
            try {
                inputStream = context.getAssets().open(str2);
            } catch (IOException unused) {
                SplitLog.w(TAG, "Built-in split apk " + str2 + " is not existing, attempts times : " + i, new Object[0]);
            }
            if (inputStream != null) {
                try {
                    FileUtil.copyFile(inputStream, new FileOutputStream(createTempFile));
                    if (createTempFile.renameTo(file)) {
                        z = true;
                    } else {
                        SplitLog.w(TAG, "Failed to rename " + createTempFile.getAbsolutePath() + " to " + file.getAbsolutePath(), new Object[0]);
                    }
                } catch (IOException unused2) {
                    SplitLog.w(TAG, "Failed to copy built-in split apk, attempts times : " + i, new Object[0]);
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Copy built-in split ");
            sb.append(z ? "succeeded" : "failed");
            sb.append(" '");
            sb.append(file.getAbsolutePath());
            sb.append("': length ");
            sb.append(file.length());
            SplitLog.i(TAG, sb.toString(), new Object[0]);
            if (!z) {
                FileUtil.deleteFileSafely(file);
                if (file.exists()) {
                    SplitLog.w(TAG, "Failed to delete copied file %s which has been corrupted ", file.getPath());
                }
            }
        }
        FileUtil.deleteFileSafely(createTempFile);
        if (!z) {
            throw new IOException(String.format("Failed to copy built-in file %s to path %s", str2, file.getPath()));
        }
    }

    private void deleteCorruptedOrObsoletedSplitApk() {
        FileUtil.deleteDir(this.splitDir);
        if (this.splitDir.exists()) {
            SplitLog.w(TAG, "Failed to delete corrupted split files", new Object[0]);
        }
    }

    private boolean verifySplitApk(Context context, SplitInfo.ApkData apkData, File file, boolean z) {
        boolean checkSplitMD5;
        if (FileUtil.isLegalFile(file)) {
            if (z) {
                checkSplitMD5 = SignatureValidator.validateSplit(context, file);
                if (checkSplitMD5) {
                    checkSplitMD5 = checkSplitMD5(apkData, file);
                }
            } else {
                checkSplitMD5 = checkSplitMD5(apkData, file);
            }
            if (!checkSplitMD5) {
                SplitLog.w(TAG, "Oops! Failed to check file %s signature or md5", file.getAbsoluteFile());
                deleteCorruptedOrObsoletedSplitApk();
            }
            return checkSplitMD5;
        }
        return false;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.lockChannel.close();
        this.lockRaf.close();
        this.cacheLock.release();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<SplitFile> load(Context context, SplitInfo splitInfo, boolean z) throws IOException {
        if (this.cacheLock.isValid()) {
            ArrayList arrayList = new ArrayList();
            for (SplitInfo.ApkData apkData : splitInfo.getApkDataList(context)) {
                SplitFile splitFile = new SplitFile(this.splitDir, splitInfo.getSplitName() + "-" + apkData.getAbi() + SplitConstants.DOT_APK, apkData.getSize());
                arrayList.add(splitFile);
                if (splitInfo.isBuiltIn()) {
                    boolean startsWith = apkData.getUrl().startsWith(SplitConstants.URL_ASSETS);
                    if (splitFile.exists()) {
                        SplitLog.v(TAG, "Built-in split %s is existing", splitFile.getAbsolutePath());
                        if (verifySplitApk(context, apkData, splitFile, z)) {
                            continue;
                        } else {
                            if (startsWith) {
                                copyBuiltInSplit(context, splitInfo.getSplitName(), apkData, splitFile);
                            }
                            if (!verifySplitApk(context, apkData, splitFile, z)) {
                                throw new IOException(String.format("Failed to check built-in split %s, it may be corrupted", splitFile.getAbsolutePath()));
                            }
                        }
                    } else {
                        SplitLog.v(TAG, "Built-in split %s is not existing, copy it from asset to %s", splitInfo.getSplitName(), splitFile.getAbsolutePath());
                        if (startsWith) {
                            copyBuiltInSplit(context, splitInfo.getSplitName(), apkData, splitFile);
                        }
                        if (!verifySplitApk(context, apkData, splitFile, z)) {
                            throw new IOException(String.format("Failed to check built-in split %s, it may be corrupted", splitInfo.getSplitName()));
                        }
                    }
                } else if (splitFile.exists()) {
                    SplitLog.v(TAG, "split %s is downloaded", splitInfo.getSplitName());
                    verifySplitApk(context, apkData, splitFile, z);
                } else {
                    SplitLog.v(TAG, " split %s is not downloaded", splitInfo.getSplitName());
                }
            }
            return arrayList;
        }
        throw new IllegalStateException("FileCheckerAndCopier was closed");
    }
}
