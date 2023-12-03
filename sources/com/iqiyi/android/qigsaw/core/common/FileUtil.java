package com.iqiyi.android.qigsaw.core.common;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.MiuiWindowManager$LayoutParams;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.zip.ZipFile;
import miui.util.HashUtils;

/* loaded from: classes2.dex */
public class FileUtil {
    private static final String TAG = "Split.FileUtil";

    private FileUtil() {
    }

    @SuppressLint({"NewApi"})
    public static void closeQuietly(Object obj) {
        if (obj == null) {
            return;
        }
        try {
            if (obj instanceof Closeable) {
                ((Closeable) obj).close();
            } else if (Build.VERSION.SDK_INT >= 19 && (obj instanceof AutoCloseable)) {
                ((AutoCloseable) obj).close();
            } else if (!(obj instanceof ZipFile)) {
                throw new IllegalArgumentException("obj: " + obj + " cannot be closed.");
            } else {
                ((ZipFile) obj).close();
            }
        } catch (Throwable unused) {
        }
    }

    public static void copyFile(File file, File file2) throws IOException {
        copyFile(new FileInputStream(file), new FileOutputStream(file2));
    }

    public static void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        try {
            byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION];
            while (true) {
                int read = bufferedInputStream.read(bArr);
                if (read == -1) {
                    bufferedOutputStream.flush();
                    return;
                }
                bufferedOutputStream.write(bArr, 0, read);
            }
        } finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
    }

    public static void createFileSafely(File file) throws IOException {
        if (file.exists()) {
            return;
        }
        Exception e = null;
        int i = 0;
        boolean z = false;
        while (i < 3 && !z) {
            i++;
            try {
                if (!file.createNewFile()) {
                    SplitLog.w(TAG, "File %s already exists", file.getAbsolutePath());
                }
                z = true;
            } catch (Exception e2) {
                e = e2;
                z = false;
            }
        }
        if (!z) {
            throw new IOException("Failed to create file " + file.getAbsolutePath(), e);
        }
        SplitLog.v(TAG, "Succeed to create file " + file.getAbsolutePath(), new Object[0]);
    }

    public static synchronized void createFileSafelyLock(File file, File file2) throws IOException {
        synchronized (FileUtil.class) {
            if (file.exists()) {
                return;
            }
            try {
                try {
                    FileLockHelper fileLock = FileLockHelper.getFileLock(file2);
                    try {
                        createFileSafely(file);
                        if (fileLock != null || file2 != null) {
                            closeQuietly(fileLock);
                        }
                    } catch (IOException unused) {
                        throw new IOException("Failed to create file " + file.getAbsolutePath());
                    }
                } catch (IOException unused2) {
                    throw new IOException("Failed to lock file " + file2.getAbsolutePath());
                }
            } catch (Throwable th) {
                if (0 != 0 || file2 != null) {
                    closeQuietly(null);
                }
                throw th;
            }
        }
    }

    public static boolean deleteDir(File file) {
        return deleteDir(file, true);
    }

    public static boolean deleteDir(File file, boolean z) {
        File[] listFiles;
        if (file == null || !file.exists()) {
            return false;
        }
        if (file.isFile()) {
            deleteFileSafely(file);
            return true;
        } else if (!file.isDirectory() || (listFiles = file.listFiles()) == null) {
            return true;
        } else {
            for (File file2 : listFiles) {
                deleteDir(file2);
            }
            if (z) {
                deleteFileSafely(file);
                return true;
            }
            return true;
        }
    }

    public static boolean deleteFileSafely(File file) {
        if (file.exists()) {
            int i = 0;
            boolean z = false;
            while (i < 3 && !z) {
                i++;
                if (file.delete()) {
                    z = true;
                }
            }
            String str = "%s to delete file: " + file.getAbsolutePath();
            Object[] objArr = new Object[1];
            objArr[0] = z ? "Succeed" : "Failed";
            SplitLog.d(TAG, str, objArr);
            return z;
        }
        return true;
    }

    public static synchronized boolean deleteFileSafelyLock(File file, File file2) throws IOException {
        synchronized (FileUtil.class) {
            if (file.exists()) {
                FileLockHelper fileLockHelper = null;
                try {
                    try {
                        fileLockHelper = FileLockHelper.getFileLock(file2);
                        return deleteFileSafely(file);
                    } catch (IOException unused) {
                        throw new IOException("Failed to lock file " + file2.getAbsolutePath());
                    }
                } finally {
                    if (fileLockHelper != null || file2 != null) {
                        closeQuietly(fileLockHelper);
                    }
                }
            }
            return true;
        }
    }

    public static String getMD5(File file) {
        FileInputStream fileInputStream;
        ICompatBundle iCompatBundle = CompatBundle.instance;
        if (iCompatBundle != null) {
            return iCompatBundle.getMD5(file);
        }
        FileInputStream fileInputStream2 = null;
        if (file != null && file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    String md5 = getMD5(fileInputStream);
                    closeQuietly(fileInputStream);
                    return md5;
                } catch (Exception unused) {
                    closeQuietly(fileInputStream);
                    return null;
                } catch (Throwable th) {
                    th = th;
                    fileInputStream2 = fileInputStream;
                    closeQuietly(fileInputStream2);
                    throw th;
                }
            } catch (Exception unused2) {
                fileInputStream = null;
            } catch (Throwable th2) {
                th = th2;
            }
        }
        return null;
    }

    public static String getMD5(InputStream inputStream) {
        int i;
        ICompatBundle iCompatBundle = CompatBundle.instance;
        if (iCompatBundle != null) {
            return iCompatBundle.getMD5(inputStream);
        }
        if (inputStream == null) {
            return null;
        }
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
            StringBuilder sb = new StringBuilder(32);
            byte[] bArr = new byte[102400];
            while (true) {
                int read = bufferedInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                messageDigest.update(bArr, 0, read);
            }
            for (byte b : messageDigest.digest()) {
                sb.append(Integer.toString((b & 255) + 256, 16).substring(1));
            }
            return sb.toString();
        } catch (Exception unused) {
            return null;
        }
    }

    public static boolean isLegalFile(File file) {
        return file != null && file.exists() && file.canRead() && file.isFile() && file.length() > 0;
    }
}
