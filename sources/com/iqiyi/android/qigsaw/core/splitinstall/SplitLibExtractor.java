package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: classes2.dex */
final class SplitLibExtractor implements Closeable {
    private static final String LOCK_FILENAME = "SplitLib.lock";
    private static final String TAG = "Split:LibExtractor";
    private final FileLock cacheLock;
    private final File libDir;
    private final FileChannel lockChannel;
    private final RandomAccessFile lockRaf;
    private final File sourceApk;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLibExtractor(File file, File file2) throws IOException {
        this.sourceApk = file;
        this.libDir = file2;
        File file3 = new File(file2, LOCK_FILENAME);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file3, "rw");
        this.lockRaf = randomAccessFile;
        try {
            FileChannel channel = randomAccessFile.getChannel();
            this.lockChannel = channel;
            try {
                SplitLog.i(TAG, "Blocking on lock " + file3.getPath(), new Object[0]);
                this.cacheLock = channel.lock();
                SplitLog.i(TAG, file3.getPath() + " locked", new Object[0]);
            } catch (IOException | Error | RuntimeException e) {
                FileUtil.closeQuietly(this.lockChannel);
                throw e;
            }
        } catch (IOException | Error | RuntimeException e2) {
            FileUtil.closeQuietly(this.lockRaf);
            throw e2;
        }
    }

    private SplitInfo.LibData.Lib findLib(String str, List<SplitInfo.LibData.Lib> list) {
        for (SplitInfo.LibData.Lib lib : list) {
            if (lib.getName().equals(str)) {
                return lib;
            }
        }
        return null;
    }

    private List<File> loadExistingExtractions(List<SplitInfo.LibData.Lib> list) throws IOException {
        SplitLog.i(TAG, "loading existing lib files", new Object[0]);
        File[] listFiles = this.libDir.listFiles();
        if (listFiles == null || listFiles.length <= 0) {
            throw new IOException("Missing extracted lib file '" + this.libDir.getPath() + "'");
        }
        ArrayList arrayList = new ArrayList(listFiles.length);
        for (SplitInfo.LibData.Lib lib : list) {
            boolean z = false;
            for (File file : listFiles) {
                if (lib.getName().equals(file.getName())) {
                    if (!lib.getMd5().equals(FileUtil.getMD5(file))) {
                        throw new IOException("Invalid extracted lib : file md5 is unmatched!");
                    }
                    arrayList.add(file);
                    z = true;
                }
            }
            if (!z) {
                throw new IOException(String.format("Invalid extracted lib: file %s is not existing!", lib.getName()));
            }
        }
        SplitLog.i(TAG, "Existing lib files loaded", new Object[0]);
        return arrayList;
    }

    private List<File> performExtractions(SplitInfo.LibData libData) throws IOException {
        SplitLibExtractor splitLibExtractor = this;
        ZipFile zipFile = new ZipFile(splitLibExtractor.sourceApk);
        int i = 1;
        int i2 = 0;
        String format = String.format("lib/%s/", libData.getAbi());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        ArrayList arrayList = new ArrayList();
        while (entries.hasMoreElements()) {
            ZipEntry nextElement = entries.nextElement();
            String name = nextElement.getName();
            if (name.charAt(i2) >= 'l' && name.charAt(i2) <= 'l' && name.startsWith("lib/")) {
                if (!name.endsWith(SplitConstants.DOT_SO)) {
                    splitLibExtractor = this;
                    i2 = i2;
                    format = format;
                } else if (name.startsWith(format)) {
                    String substring = name.substring(name.lastIndexOf(47) + i);
                    SplitInfo.LibData.Lib findLib = splitLibExtractor.findLib(substring, libData.getLibs());
                    if (findLib == null) {
                        throw new IOException(String.format("Failed to find %s in split-info", substring));
                    }
                    File file = new File(splitLibExtractor.libDir, substring);
                    if (file.exists()) {
                        if (findLib.getMd5().equals(FileUtil.getMD5(file))) {
                            arrayList.add(file);
                        } else {
                            FileUtil.deleteFileSafely(file);
                            if (file.exists()) {
                                SplitLog.w(TAG, "Failed to delete corrupted lib file '" + file.getPath() + "'", new Object[i2]);
                            }
                        }
                    }
                    SplitLog.i(TAG, "Extraction is needed for lib: " + file.getAbsolutePath(), new Object[i2]);
                    File createTempFile = File.createTempFile("tmp-" + substring, "", SplitPathManager.require().getSplitTmpDir());
                    int i3 = i2;
                    int i4 = i3;
                    while (i3 < 3 && i4 == 0) {
                        i3++;
                        try {
                            FileUtil.copyFile(zipFile.getInputStream(nextElement), new FileOutputStream(createTempFile));
                            if (createTempFile.renameTo(file)) {
                                i4 = 1;
                            } else {
                                SplitLog.w(TAG, "Failed to rename \"" + createTempFile.getAbsolutePath() + "\" to \"" + file.getAbsolutePath() + "\"", new Object[0]);
                            }
                        } catch (IOException unused) {
                            SplitLog.w(TAG, "Failed to extract so :" + substring + ", attempts times : " + i3, new Object[0]);
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append("Extraction ");
                        sb.append(i4 != 0 ? "succeeded" : "failed");
                        sb.append(" '");
                        sb.append(file.getAbsolutePath());
                        sb.append("': length ");
                        String str = format;
                        sb.append(file.length());
                        SplitLog.i(TAG, sb.toString(), new Object[0]);
                        String md5 = FileUtil.getMD5(file);
                        if (!findLib.getMd5().equals(md5)) {
                            SplitLog.w(TAG, "Failed to check %s md5, excepted %s but %s", substring, findLib.getMd5(), md5);
                            i4 = 0;
                        }
                        if (i4 == 0) {
                            FileUtil.deleteFileSafely(file);
                            if (file.exists()) {
                                SplitLog.w(TAG, "Failed to delete extracted lib that has been corrupted'" + file.getPath() + "'", new Object[0]);
                            }
                        } else {
                            arrayList.add(file);
                        }
                        format = str;
                    }
                    String str2 = format;
                    FileUtil.deleteFileSafely(createTempFile);
                    if (i4 == 0) {
                        throw new IOException("Could not create lib file " + file.getAbsolutePath() + ")");
                    }
                    splitLibExtractor = this;
                    format = str2;
                    i = 1;
                    i2 = 0;
                } else {
                    continue;
                }
            }
        }
        FileUtil.closeQuietly(zipFile);
        if (arrayList.size() == libData.getLibs().size()) {
            return arrayList;
        }
        throw new IOException("Number of extracted so files is mismatch, expected: " + libData.getLibs().size() + " ,but: " + arrayList.size());
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.lockChannel.close();
        this.lockRaf.close();
        this.cacheLock.release();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<File> load(SplitInfo.LibData libData, boolean z) throws IOException {
        List<File> performExtractions;
        if (this.cacheLock.isValid()) {
            if (z) {
                performExtractions = performExtractions(libData);
            } else {
                try {
                    performExtractions = loadExistingExtractions(libData.getLibs());
                } catch (IOException unused) {
                    SplitLog.w(TAG, "Failed to reload existing extracted lib files, falling back to fresh extraction", new Object[0]);
                    performExtractions = this.performExtractions(libData);
                }
            }
            SplitLog.i(TAG, "load found " + performExtractions.size() + " lib files", new Object[0]);
            return performExtractions;
        }
        throw new IllegalStateException("SplitLibExtractor was closed");
    }
}
