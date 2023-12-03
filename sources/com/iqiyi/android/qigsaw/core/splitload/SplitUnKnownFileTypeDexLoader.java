package com.iqiyi.android.qigsaw.core.splitload;

import android.os.Build;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

/* loaded from: classes2.dex */
final class SplitUnKnownFileTypeDexLoader {
    private static final String TAG = "SplitUnKnownFileTypeDexLoader";

    SplitUnKnownFileTypeDexLoader() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void loadDex(ClassLoader classLoader, List<String> list, File file) throws Throwable {
        boolean z;
        Object newInstance;
        if (Build.VERSION.SDK_INT >= 21 || list == null) {
            return;
        }
        ArrayList<File> arrayList = new ArrayList();
        for (String str : list) {
            if (str.endsWith(SplitConstants.DOT_SO)) {
                arrayList.add(new File(str));
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        Object obj = HiddenApiReflection.findField(classLoader, "pathList").get(classLoader);
        Method findMethod = HiddenApiReflection.findMethod(Class.forName("dalvik.system.DexPathList"), "loadDexFile", (Class<?>[]) new Class[]{File.class, File.class});
        ArrayList arrayList2 = new ArrayList(arrayList.size());
        for (File file2 : arrayList) {
            try {
                DexFile dexFile = (DexFile) findMethod.invoke(null, file2, file);
                Class<?> cls = Class.forName("dalvik.system.DexPathList$Element");
                try {
                    try {
                        newInstance = HiddenApiReflection.findConstructor(cls, (Class<?>[]) new Class[]{File.class, Boolean.TYPE, File.class, DexFile.class}).newInstance(file2, Boolean.FALSE, file2, dexFile);
                    } catch (NoSuchMethodException unused) {
                        newInstance = HiddenApiReflection.findConstructor(cls, (Class<?>[]) new Class[]{File.class, File.class, DexFile.class}).newInstance(file2, file2, dexFile);
                    }
                } catch (NoSuchMethodException unused2) {
                    try {
                        newInstance = HiddenApiReflection.findConstructor(cls, (Class<?>[]) new Class[]{File.class, ZipFile.class, DexFile.class}).newInstance(file2, new ZipFile(file2), dexFile);
                    } catch (IOException e) {
                        SplitLog.printErrStackTrace(TAG, e, "Unable to open zip file: " + file2.getAbsolutePath(), new Object[0]);
                    }
                }
                arrayList2.add(newInstance);
            } finally {
                if (z) {
                }
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        HiddenApiReflection.expandFieldArray(obj, "dexElements", arrayList2.toArray());
    }
}
