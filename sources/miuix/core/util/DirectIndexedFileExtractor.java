package miuix.core.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes5.dex */
public class DirectIndexedFileExtractor {
    public static String IDF_FILES_PATH;

    private static void ensureIdfPath(Context context) {
        if (IDF_FILES_PATH == null) {
            try {
                Context createPackageContext = context.createPackageContext(context.getPackageName(), 2);
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        createPackageContext = (Context) createPackageContext.getClass().getMethod("createDeviceProtectedStorageContext", new Class[0]).invoke(createPackageContext, new Object[0]);
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
                    }
                }
                if (createPackageContext.getFilesDir() != null) {
                    IDF_FILES_PATH = createPackageContext.getFilesDir().getAbsolutePath() + File.separator + "idf";
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (IDF_FILES_PATH == null) {
            Log.w("DirectIndexedFileExt", "Error: Cannot locate IDF_FILES_PATH");
            return;
        }
        File file = new File(IDF_FILES_PATH);
        if (file.exists() || !file.mkdirs() || Build.VERSION.SDK_INT < 21) {
            return;
        }
        try {
            Os.mkdir(IDF_FILES_PATH, 505);
        } catch (ErrnoException e2) {
            e2.printStackTrace();
        }
    }

    public static String getDirectIndexedFilePath(Context context, String str) {
        ensureIdfPath(context);
        if (IDF_FILES_PATH == null) {
            return null;
        }
        return IDF_FILES_PATH + File.separator + str;
    }
}
