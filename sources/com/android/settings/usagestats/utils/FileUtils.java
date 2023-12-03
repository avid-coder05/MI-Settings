package com.android.settings.usagestats.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.android.settings.usagestats.cache.DiskLruCacheUtils;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes2.dex */
public class FileUtils {
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v0, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r5v1, types: [java.io.IOException] */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v3, types: [java.io.InputStream] */
    public static void InputStreamToFile(InputStream e, File file) {
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (Throwable th) {
                    th = th;
                }
            } catch (IOException e2) {
                e = e2;
            }
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = e.read(bArr, 0, 4096);
                    if (read == -1) {
                        fileOutputStream.close();
                        e.close();
                        return;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
            } catch (IOException e3) {
                e = e3;
                fileOutputStream2 = fileOutputStream;
                e.printStackTrace();
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                e.close();
            } catch (Throwable th2) {
                th = th2;
                fileOutputStream2 = fileOutputStream;
                if (fileOutputStream2 != null) {
                    try {
                        fileOutputStream2.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                        throw th;
                    }
                }
                e.close();
                throw th;
            }
        } catch (IOException e5) {
            e = e5;
            e.printStackTrace();
        }
    }

    public static void clearIllegalData(Context context, long j) {
        File[] listFiles;
        File file = new File(getCacheDirPath(context));
        if (file.exists() && (listFiles = file.listFiles()) != null && listFiles.length > 0) {
            long j2 = DateUtils.today();
            for (int i = 0; i < listFiles.length; i++) {
                long j3 = i;
                clearUnnecessaryFile(context, (DateUtils.INTERVAL_DAY * j3) + j2);
                clearUnnecessaryFile(context, j - (j3 * DateUtils.INTERVAL_DAY));
            }
        }
    }

    public static void clearUnnecessaryFile(Context context, long j) {
        DiskLruCacheUtils.getInstance(context).remove(String.valueOf(j));
    }

    public static void closeIO(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCacheDirPath(Context context) {
        String str = ((!isSDCardAvailable() || context.getExternalCacheDir() == null) ? context.getCacheDir().getAbsolutePath() : context.getExternalCacheDir().getAbsolutePath()) + File.separator + "AppTimer";
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        Log.d("ZJJ_FileUtils", "getCacheDirPath: " + str);
        return str;
    }

    public static String getNewCacheDirPath(Context context) {
        String sb;
        if (Environment.getExternalStorageDirectory() != null) {
            sb = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AppTimer";
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(context.getFilesDir().getAbsolutePath());
            String str = File.separator;
            sb2.append(str);
            sb2.append("Android");
            sb2.append(str);
            sb2.append("AppTimer");
            sb = sb2.toString();
        }
        File file = new File(sb);
        if (!file.exists()) {
            file.mkdirs();
        }
        Log.d("ZJJ_FileUtils", "getNewCacheDirPath: " + context.getFilesDir());
        Log.d("ZJJ_FileUtils", "getNewCacheDirPath: " + sb);
        return sb;
    }

    public static boolean isSDCardAvailable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static String readFromFile(Context context, long j) {
        return DiskLruCacheUtils.getInstance(context).getString(String.valueOf(j));
    }

    public static void writeToFile(Context context, String str, long j) {
        DiskLruCacheUtils.getInstance(context).putString(String.valueOf(j), str);
    }
}
