package com.android.settings.display.util;

import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: classes.dex */
public class FileUtils {
    private static int chmod(File file, int i) {
        if (file.exists()) {
            try {
                Os.chmod(file.getPath(), i);
                return 0;
            } catch (ErrnoException e) {
                Log.e("bFileUtils", "chmod. fail: " + e);
                return 0;
            }
        }
        return -1;
    }

    public static int chmod(String str, int i) {
        if (str == null || str.length() < 1) {
            return -1;
        }
        return chmod(new File(str), i);
    }

    public static void closeQuietly(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception unused) {
            }
        }
    }

    public static boolean copyToFile(InputStream inputStream, File file) {
        if (inputStream == null) {
            return false;
        }
        try {
            if (file.exists() && !file.delete()) {
                return false;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read < 0) {
                        fileOutputStream.close();
                        return true;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
            } finally {
            }
        } catch (IOException unused) {
            return false;
        }
    }

    public static boolean mkdirs(File file, int i, int i2, int i3) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isDirectory();
        }
        if (mkdirs(file.getParentFile(), i, i2, i3)) {
            try {
                boolean mkdir = file.mkdir();
                Os.chmod(file.getPath(), i);
                Os.chown(file.getPath(), i2, i3);
                return mkdir;
            } catch (Exception e) {
                Log.e("FileUtils", "mkdirs failed. ", e);
                return false;
            }
        }
        return false;
    }

    public static boolean unZip(String str, String str2, String str3, String str4) {
        ZipFile zipFile;
        if (new File(str).exists()) {
            ZipFile zipFile2 = null;
            try {
                try {
                    zipFile = new ZipFile(str);
                } catch (Exception e) {
                    e = e;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                new File(str2).mkdirs();
                boolean z = false;
                while (entries.hasMoreElements()) {
                    ZipEntry nextElement = entries.nextElement();
                    String name = nextElement.getName();
                    if (!name.contains("../") && name.startsWith(str3)) {
                        if (URLDecoder.decode(name, "utf-8").contains("../")) {
                            Log.w("ResourceHelper", "suspect to be a hack act when unzip");
                        } else {
                            String str5 = str2 + "/" + name;
                            if (TextUtils.isEmpty(str4) || name.contains(str4)) {
                                if (nextElement.isDirectory()) {
                                    new File(str5).mkdirs();
                                } else {
                                    z = writeTo(zipFile.getInputStream(nextElement), str5);
                                }
                            }
                        }
                    }
                }
                closeQuietly(zipFile);
                return z;
            } catch (Exception e2) {
                e = e2;
                zipFile2 = zipFile;
                e.printStackTrace();
                closeQuietly(zipFile2);
                return false;
            } catch (Throwable th2) {
                th = th2;
                zipFile2 = zipFile;
                closeQuietly(zipFile2);
                throw th;
            }
        }
        return false;
    }

    public static boolean writeTo(InputStream inputStream, String str) {
        InputStream inputStream2;
        Throwable th;
        Exception e;
        try {
            try {
                File file = new File(str);
                mkdirs(file.getParentFile(), 509, -1, -1);
                inputStream2 = new BufferedInputStream(inputStream);
                try {
                    chmod(str, 509);
                    copyToFile(inputStream2, new File(str));
                    chmod(str, 509);
                    file.setLastModified(System.currentTimeMillis());
                    closeQuietly(inputStream2);
                    return true;
                } catch (Exception e2) {
                    e = e2;
                    e.printStackTrace();
                    closeQuietly(inputStream2);
                    return false;
                }
            } catch (Throwable th2) {
                th = th2;
                closeQuietly(inputStream2);
                throw th;
            }
        } catch (Exception e3) {
            inputStream2 = inputStream;
            e = e3;
        } catch (Throwable th3) {
            inputStream2 = inputStream;
            th = th3;
            closeQuietly(inputStream2);
            throw th;
        }
    }
}
