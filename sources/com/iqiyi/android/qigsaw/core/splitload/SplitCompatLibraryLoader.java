package com.iqiyi.android.qigsaw.core.splitload;

import android.os.Build;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
final class SplitCompatLibraryLoader {
    private static final String TAG = "SplitCompatLibraryLoader";

    /* loaded from: classes2.dex */
    private static final class V14 {
        private V14() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void load(ClassLoader classLoader, File file) throws Throwable {
            Object obj = HiddenApiReflection.findField(classLoader, "pathList").get(classLoader);
            Field findField = HiddenApiReflection.findField(obj, "nativeLibraryDirectories");
            File[] fileArr = (File[]) findField.get(obj);
            ArrayList arrayList = new ArrayList(fileArr.length + 1);
            arrayList.add(file);
            for (File file2 : fileArr) {
                if (!file.equals(file2)) {
                    arrayList.add(file2);
                }
            }
            findField.set(obj, arrayList.toArray(new File[0]));
        }
    }

    /* loaded from: classes2.dex */
    private static final class V23 {
        private V23() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void load(ClassLoader classLoader, File file) throws Throwable {
            Object obj = HiddenApiReflection.findField(classLoader, "pathList").get(classLoader);
            List list = (List) HiddenApiReflection.findField(obj, "nativeLibraryDirectories").get(obj);
            if (list == null) {
                list = new ArrayList(2);
            }
            Iterator it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                } else if (file.equals((File) it.next())) {
                    it.remove();
                    break;
                }
            }
            list.add(0, file);
            List list2 = (List) HiddenApiReflection.findField(obj, "systemNativeLibraryDirectories").get(obj);
            if (list2 == null) {
                list2 = new ArrayList(2);
            }
            ArrayList arrayList = new ArrayList(list.size() + list2.size() + 1);
            arrayList.addAll(list);
            arrayList.addAll(list2);
            HiddenApiReflection.findField(obj, "nativeLibraryPathElements").set(obj, (Object[]) HiddenApiReflection.findMethod(obj, "makePathElements", List.class, File.class, List.class).invoke(obj, arrayList, null, new ArrayList()));
        }
    }

    /* loaded from: classes2.dex */
    private static final class V25 {
        private V25() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void load(ClassLoader classLoader, File file) throws Throwable {
            Object obj = HiddenApiReflection.findField(classLoader, "pathList").get(classLoader);
            List list = (List) HiddenApiReflection.findField(obj, "nativeLibraryDirectories").get(obj);
            if (list == null) {
                list = new ArrayList(2);
            }
            Iterator it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                } else if (file.equals((File) it.next())) {
                    it.remove();
                    break;
                }
            }
            list.add(0, file);
            List list2 = (List) HiddenApiReflection.findField(obj, "systemNativeLibraryDirectories").get(obj);
            if (list2 == null) {
                list2 = new ArrayList(2);
            }
            ArrayList arrayList = new ArrayList(list.size() + list2.size() + 1);
            arrayList.addAll(list);
            arrayList.addAll(list2);
            HiddenApiReflection.findField(obj, "nativeLibraryPathElements").set(obj, (Object[]) HiddenApiReflection.findMethod(obj, "makePathElements", List.class).invoke(obj, arrayList));
        }
    }

    SplitCompatLibraryLoader() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void load(ClassLoader classLoader, File file) throws Throwable {
        if (file == null || !file.exists()) {
            SplitLog.e(TAG, "load, folder %s is illegal", file);
            return;
        }
        int i = Build.VERSION.SDK_INT;
        if ((i == 25 && Build.VERSION.PREVIEW_SDK_INT != 0) || i > 25) {
            try {
                V25.load(classLoader, file);
            } catch (Throwable th) {
                SplitLog.e(TAG, "load, v25 fail, sdk: %d, error: %s, try to fallback to V23", Integer.valueOf(Build.VERSION.SDK_INT), th.getMessage());
                V23.load(classLoader, file);
            }
        } else if (i < 23) {
            if (i < 14) {
                throw new UnsupportedOperationException("don't support under SDK version 14!");
            }
            V14.load(classLoader, file);
        } else {
            try {
                V23.load(classLoader, file);
            } catch (Throwable th2) {
                SplitLog.e(TAG, "load, v23 fail, sdk: %d, error: %s, try to fallback to V14", Integer.valueOf(Build.VERSION.SDK_INT), th2.getMessage());
                V14.load(classLoader, file);
            }
        }
    }
}
