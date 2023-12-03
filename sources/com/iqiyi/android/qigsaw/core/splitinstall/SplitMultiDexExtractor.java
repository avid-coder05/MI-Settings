package com.iqiyi.android.qigsaw.core.splitinstall;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.MiuiWindowManager$LayoutParams;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/* loaded from: classes2.dex */
final class SplitMultiDexExtractor implements Closeable {
    private static final String DEX_PREFIX = "classes";
    private static final String EXTRACTED_NAME_EXT = ".classes";
    private static final String KEY_CRC = "crc";
    private static final String KEY_DEX_CRC = "dex.crc.";
    private static final String KEY_DEX_NUMBER = "dex.number";
    private static final String KEY_DEX_TIME = "dex.time.";
    private static final String KEY_TIME_STAMP = "timestamp";
    private static final String LOCK_FILENAME = "SplitMultiDex.lock";
    private static final long NO_VALUE = -1;
    private static final String PREFS_FILE = "split.multidex.version";
    private static final String TAG = "Split:MultiDexExtractor";
    private final FileLock cacheLock;
    private final File dexDir;
    private final FileChannel lockChannel;
    private final RandomAccessFile lockRaf;
    private final File sourceApk;
    private final long sourceCrc;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class CentralDirectory {
        long offset;
        long size;

        CentralDirectory() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ExtractedDex extends File {
        long crc;

        ExtractedDex(File file, String str) {
            super(file, str);
            this.crc = 1L;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class ZipCrcUtil {
        ZipCrcUtil() {
        }

        private static long computeCrcOfCentralDir(RandomAccessFile randomAccessFile, CentralDirectory centralDirectory) throws IOException {
            CRC32 crc32 = new CRC32();
            long j = centralDirectory.size;
            randomAccessFile.seek(centralDirectory.offset);
            int min = (int) Math.min((long) PlaybackStateCompat.ACTION_PREPARE, j);
            byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION];
            while (true) {
                int read = randomAccessFile.read(bArr, 0, min);
                if (read == -1) {
                    break;
                }
                crc32.update(bArr, 0, read);
                j -= read;
                if (j == 0) {
                    break;
                }
                min = (int) Math.min((long) PlaybackStateCompat.ACTION_PREPARE, j);
            }
            return crc32.getValue();
        }

        private static CentralDirectory findCentralDirectory(RandomAccessFile randomAccessFile) throws IOException, ZipException {
            long length = randomAccessFile.length() - 22;
            if (length < 0) {
                throw new ZipException("File too short to be a zip file: " + randomAccessFile.length());
            }
            long j = length - PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH;
            long j2 = j >= 0 ? j : 0L;
            int reverseBytes = Integer.reverseBytes(101010256);
            do {
                randomAccessFile.seek(length);
                if (randomAccessFile.readInt() == reverseBytes) {
                    randomAccessFile.skipBytes(2);
                    randomAccessFile.skipBytes(2);
                    randomAccessFile.skipBytes(2);
                    randomAccessFile.skipBytes(2);
                    CentralDirectory centralDirectory = new CentralDirectory();
                    centralDirectory.size = Integer.reverseBytes(randomAccessFile.readInt()) & 4294967295L;
                    centralDirectory.offset = Integer.reverseBytes(randomAccessFile.readInt()) & 4294967295L;
                    return centralDirectory;
                }
                length--;
            } while (length >= j2);
            throw new ZipException("End Of Central Directory signature not found");
        }

        static long getZipCrc(File file) throws IOException {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            try {
                return computeCrcOfCentralDir(randomAccessFile, findCentralDirectory(randomAccessFile));
            } finally {
                randomAccessFile.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitMultiDexExtractor(File file, File file2) throws IOException {
        SplitLog.i(TAG, "SplitMultiDexExtractor(" + file.getPath() + ", " + file2.getPath() + ")", new Object[0]);
        this.sourceApk = file;
        this.dexDir = file2;
        this.sourceCrc = getZipCrc(file);
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
                closeQuietly(this.lockChannel);
                throw e;
            }
        } catch (IOException | Error | RuntimeException e2) {
            closeQuietly(this.lockRaf);
            throw e2;
        }
    }

    private void clearDexDir() {
        File[] listFiles = this.dexDir.listFiles(new FileFilter() { // from class: com.iqiyi.android.qigsaw.core.splitinstall.SplitMultiDexExtractor.1
            @Override // java.io.FileFilter
            public boolean accept(File file) {
                return !file.getName().equals(SplitMultiDexExtractor.LOCK_FILENAME);
            }
        });
        if (listFiles == null) {
            SplitLog.w(TAG, "Failed to list secondary dex dir content (" + this.dexDir.getPath() + ").", new Object[0]);
            return;
        }
        for (File file : listFiles) {
            SplitLog.i(TAG, "Trying to delete old file " + file.getPath() + " of size " + file.length(), new Object[0]);
            if (file.delete()) {
                SplitLog.i(TAG, "Deleted old file " + file.getPath(), new Object[0]);
            } else {
                SplitLog.w(TAG, "Failed to delete old file " + file.getPath(), new Object[0]);
            }
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            SplitLog.w(TAG, "Failed to close resource", e);
        }
    }

    private static void extract(ZipFile zipFile, ZipEntry zipEntry, File file, String str) throws IOException {
        InputStream inputStream = zipFile.getInputStream(zipEntry);
        File createTempFile = File.createTempFile("tmp-" + str, SplitConstants.DOT_ZIP, file.getParentFile());
        SplitLog.i(TAG, "Extracting " + createTempFile.getPath(), new Object[0]);
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(createTempFile)));
            try {
                ZipEntry zipEntry2 = new ZipEntry("classes.dex");
                zipEntry2.setTime(zipEntry.getTime());
                zipOutputStream.putNextEntry(zipEntry2);
                byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    zipOutputStream.write(bArr, 0, read);
                }
                zipOutputStream.closeEntry();
                closeQuietly(zipOutputStream);
                if (!createTempFile.setReadOnly()) {
                    throw new IOException("Failed to mark readonly \"" + createTempFile.getAbsolutePath() + "\" (tmp of \"" + file.getAbsolutePath() + "\")");
                }
                SplitLog.i(TAG, "Renaming to " + file.getPath(), new Object[0]);
                if (createTempFile.renameTo(file)) {
                    return;
                }
                throw new IOException("Failed to rename \"" + createTempFile.getAbsolutePath() + "\" to \"" + file.getAbsolutePath() + "\"");
            } catch (Throwable th) {
                closeQuietly(zipOutputStream);
                throw th;
            }
        } finally {
            closeQuietly(inputStream);
            createTempFile.delete();
        }
    }

    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences(PREFS_FILE, 4);
    }

    private static long getTimeStamp(File file) {
        long lastModified = file.lastModified();
        return lastModified == 1 ? lastModified - 1 : lastModified;
    }

    private static long getZipCrc(File file) throws IOException {
        long zipCrc = ZipCrcUtil.getZipCrc(file);
        return zipCrc == 1 ? zipCrc - 1 : zipCrc;
    }

    private static boolean isModified(Context context, File file, long j, String str) {
        SharedPreferences multiDexPreferences = getMultiDexPreferences(context);
        if (multiDexPreferences.getLong(str + "timestamp", 1L) == getTimeStamp(file)) {
            if (multiDexPreferences.getLong(str + KEY_CRC, 1L) == j) {
                return false;
            }
        }
        return true;
    }

    private List<? extends File> loadExistingExtractions(Context context, String str) throws IOException {
        int i = 0;
        SplitLog.i(TAG, "loading existing secondary dex files", new Object[0]);
        String str2 = this.sourceApk.getName() + EXTRACTED_NAME_EXT;
        SharedPreferences multiDexPreferences = getMultiDexPreferences(context);
        int i2 = multiDexPreferences.getInt(str + KEY_DEX_NUMBER, 1);
        ArrayList arrayList = new ArrayList(i2 + (-1));
        int i3 = 2;
        while (i3 <= i2) {
            ExtractedDex extractedDex = new ExtractedDex(this.dexDir, str2 + i3 + SplitConstants.DOT_ZIP);
            if (!extractedDex.isFile()) {
                throw new IOException("Missing extracted secondary dex file '" + extractedDex.getPath() + "'");
            }
            extractedDex.crc = getZipCrc(extractedDex);
            long j = multiDexPreferences.getLong(str + KEY_DEX_CRC + i3, -1L);
            long j2 = multiDexPreferences.getLong(str + KEY_DEX_TIME + i3, -1L);
            String str3 = str2;
            long lastModified = extractedDex.lastModified();
            if (j2 == lastModified) {
                SharedPreferences sharedPreferences = multiDexPreferences;
                int i4 = i2;
                if (j == extractedDex.crc) {
                    arrayList.add(extractedDex);
                    i3++;
                    multiDexPreferences = sharedPreferences;
                    str2 = str3;
                    i2 = i4;
                    i = 0;
                }
            }
            throw new IOException("Invalid extracted dex: " + extractedDex + " (key \"" + str + "\"), expected modification time: " + j2 + ", modification time: " + lastModified + ", expected crc: " + j + ", file crc: " + extractedDex.crc);
        }
        SplitLog.i(TAG, "Existing secondary dex files loaded", new Object[i]);
        return arrayList;
    }

    private List<ExtractedDex> performExtractions() throws IOException {
        boolean z;
        boolean z2;
        String str = this.sourceApk.getName() + EXTRACTED_NAME_EXT;
        clearDexDir();
        ArrayList arrayList = new ArrayList();
        ZipFile zipFile = new ZipFile(this.sourceApk);
        try {
            ZipEntry entry = zipFile.getEntry(DEX_PREFIX + 2 + SplitConstants.DOT_DEX);
            int i = 2;
            while (entry != null) {
                ExtractedDex extractedDex = new ExtractedDex(this.dexDir, str + i + SplitConstants.DOT_ZIP);
                arrayList.add(extractedDex);
                boolean z3 = false;
                SplitLog.i(TAG, "Extraction is needed for file " + extractedDex, new Object[0]);
                int i2 = 0;
                boolean z4 = false;
                while (i2 < 3 && !z4) {
                    int i3 = i2 + 1;
                    extract(zipFile, entry, extractedDex, str);
                    try {
                        extractedDex.crc = getZipCrc(extractedDex);
                        z = true;
                    } catch (IOException e) {
                        SplitLog.w(TAG, "Failed to read crc from " + extractedDex.getAbsolutePath(), e);
                        z = z3;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Extraction ");
                    sb.append(z ? "succeeded" : "failed");
                    sb.append(" '");
                    sb.append(extractedDex.getAbsolutePath());
                    sb.append("': length ");
                    sb.append(extractedDex.length());
                    sb.append(" - crc: ");
                    sb.append(extractedDex.crc);
                    SplitLog.i(TAG, sb.toString(), new Object[0]);
                    if (!z) {
                        extractedDex.delete();
                        if (extractedDex.exists()) {
                            z2 = false;
                            SplitLog.w(TAG, "Failed to delete corrupted secondary dex '" + extractedDex.getPath() + "'", new Object[0]);
                            z3 = z2;
                            z4 = z;
                            i2 = i3;
                        }
                    }
                    z2 = false;
                    z3 = z2;
                    z4 = z;
                    i2 = i3;
                }
                if (!z4) {
                    throw new IOException("Could not create zip file " + extractedDex.getAbsolutePath() + " for secondary dex (" + i + ")");
                }
                i++;
                entry = zipFile.getEntry(DEX_PREFIX + i + SplitConstants.DOT_DEX);
            }
            try {
                zipFile.close();
            } catch (IOException e2) {
                SplitLog.w(TAG, "Failed to close resource", e2);
            }
            return arrayList;
        } finally {
        }
    }

    private static void putStoredApkInfo(Context context, String str, long j, long j2, List<ExtractedDex> list) {
        SharedPreferences.Editor edit = getMultiDexPreferences(context).edit();
        edit.putLong(str + "timestamp", j);
        edit.putLong(str + KEY_CRC, j2);
        edit.putInt(str + KEY_DEX_NUMBER, list.size() + 1);
        int i = 2;
        for (ExtractedDex extractedDex : list) {
            edit.putLong(str + KEY_DEX_CRC + i, extractedDex.crc);
            edit.putLong(str + KEY_DEX_TIME + i, extractedDex.lastModified());
            i++;
        }
        edit.apply();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.cacheLock.release();
        this.lockChannel.close();
        this.lockRaf.close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<? extends File> load(Context context, String str, boolean z) throws IOException {
        List<ExtractedDex> performExtractions;
        List<? extends File> list;
        SplitLog.i(TAG, "SplitMultiDexExtractor.load(" + this.sourceApk.getPath() + ", " + z + ", " + str + ")", new Object[0]);
        if (this.cacheLock.isValid()) {
            if (!z && !isModified(context, this.sourceApk, this.sourceCrc, str)) {
                try {
                    list = loadExistingExtractions(context, str);
                } catch (IOException e) {
                    SplitLog.w(TAG, "Failed to reload existing extracted secondary dex files, falling back to fresh extraction", e);
                    performExtractions = this.performExtractions();
                    putStoredApkInfo(context, str, getTimeStamp(this.sourceApk), this.sourceCrc, performExtractions);
                }
                SplitLog.i(TAG, "load found " + list.size() + " secondary dex files", new Object[0]);
                return list;
            }
            if (z) {
                SplitLog.i(TAG, "Forced extraction must be performed.", new Object[0]);
            } else {
                SplitLog.i(TAG, "Detected that extraction must be performed.", new Object[0]);
            }
            performExtractions = performExtractions();
            putStoredApkInfo(context, str, getTimeStamp(this.sourceApk), this.sourceCrc, performExtractions);
            list = performExtractions;
            SplitLog.i(TAG, "load found " + list.size() + " secondary dex files", new Object[0]);
            return list;
        }
        throw new IllegalStateException("SplitMultiDexExtractor was closed");
    }
}
