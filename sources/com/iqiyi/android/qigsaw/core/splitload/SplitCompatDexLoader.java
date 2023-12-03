package com.iqiyi.android.qigsaw.core.splitload;

import android.os.Build;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
final class SplitCompatDexLoader {
    private static final String TAG = "SplitCompatDexLoader";
    private static int sPatchDexCount;

    /* loaded from: classes2.dex */
    private static final class V14 {
        private V14() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void load(ClassLoader classLoader, List<File> list, File file) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            Object obj = HiddenApiReflection.findField(classLoader, "pathList").get(classLoader);
            HiddenApiReflection.expandFieldArray(obj, "dexElements", makeDexElements(obj, new ArrayList(list), file));
        }

        private static Object[] makeDexElements(Object obj, ArrayList<File> arrayList, File file) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            return (Object[]) HiddenApiReflection.findMethod(obj, "makeDexElements", ArrayList.class, File.class).invoke(obj, arrayList, file);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class V19 {
        private V19() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void load(ClassLoader classLoader, List<File> list, File file) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {
            Object obj = HiddenApiReflection.findField(classLoader, "pathList").get(classLoader);
            ArrayList arrayList = new ArrayList();
            HiddenApiReflection.expandFieldArray(obj, "dexElements", makeDexElements(obj, new ArrayList(list), file, arrayList));
            if (arrayList.size() > 0) {
                Iterator it = arrayList.iterator();
                if (it.hasNext()) {
                    IOException iOException = (IOException) it.next();
                    SplitLog.e(SplitCompatDexLoader.TAG, "Exception in makeDexElement", iOException);
                    throw iOException;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Object[] makeDexElements(Object obj, ArrayList<File> arrayList, File file, ArrayList<IOException> arrayList2) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Method findMethod;
            try {
                findMethod = HiddenApiReflection.findMethod(obj, "makeDexElements", ArrayList.class, File.class, ArrayList.class);
            } catch (NoSuchMethodException unused) {
                SplitLog.e(SplitCompatDexLoader.TAG, "NoSuchMethodException: makeDexElements(ArrayList,File,ArrayList) failure", new Object[0]);
                try {
                    findMethod = HiddenApiReflection.findMethod(obj, "makeDexElements", List.class, File.class, List.class);
                } catch (NoSuchMethodException e) {
                    SplitLog.e(SplitCompatDexLoader.TAG, "NoSuchMethodException: makeDexElements(List,File,List) failure", new Object[0]);
                    throw e;
                }
            }
            return (Object[]) findMethod.invoke(obj, arrayList, file, arrayList2);
        }
    }

    /* loaded from: classes2.dex */
    private static final class V23 {
        private V23() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void load(ClassLoader classLoader, List<File> list, File file) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {
            Object obj = HiddenApiReflection.findField(classLoader, "pathList").get(classLoader);
            ArrayList arrayList = new ArrayList();
            HiddenApiReflection.expandFieldArray(obj, "dexElements", makePathElements(obj, new ArrayList(list), file, arrayList));
            if (arrayList.size() > 0) {
                Iterator it = arrayList.iterator();
                if (it.hasNext()) {
                    IOException iOException = (IOException) it.next();
                    SplitLog.e(SplitCompatDexLoader.TAG, "Exception in makePathElement", iOException);
                    throw iOException;
                }
            }
        }

        private static Object[] makePathElements(Object obj, ArrayList<File> arrayList, File file, ArrayList<IOException> arrayList2) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Method findMethod;
            try {
                findMethod = HiddenApiReflection.findMethod(obj, "makePathElements", List.class, File.class, List.class);
            } catch (NoSuchMethodException unused) {
                SplitLog.e(SplitCompatDexLoader.TAG, "NoSuchMethodException: makePathElements(List,File,List) failure", new Object[0]);
                try {
                    findMethod = HiddenApiReflection.findMethod(obj, "makePathElements", ArrayList.class, File.class, ArrayList.class);
                } catch (NoSuchMethodException unused2) {
                    SplitLog.e(SplitCompatDexLoader.TAG, "NoSuchMethodException: makeDexElements(ArrayList,File,ArrayList) failure", new Object[0]);
                    try {
                        SplitLog.w(SplitCompatDexLoader.TAG, "NoSuchMethodException: try use v19 instead", new Object[0]);
                        return V19.makeDexElements(obj, arrayList, file, arrayList2);
                    } catch (NoSuchMethodException e) {
                        SplitLog.e(SplitCompatDexLoader.TAG, "NoSuchMethodException: makeDexElements(List,File,List) failure", new Object[0]);
                        throw e;
                    }
                }
            }
            return (Object[]) findMethod.invoke(obj, arrayList, file, arrayList2);
        }
    }

    SplitCompatDexLoader() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void load(ClassLoader classLoader, File file, List<File> list) throws Throwable {
        if (list.isEmpty()) {
            return;
        }
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            V23.load(classLoader, list, file);
        } else if (i >= 19) {
            V19.load(classLoader, list, file);
        } else if (i < 14) {
            throw new UnsupportedOperationException("don't support under SDK version 14!");
        } else {
            V14.load(classLoader, list, file);
        }
        sPatchDexCount = list.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unLoad(ClassLoader classLoader) throws Throwable {
        if (sPatchDexCount <= 0) {
            return;
        }
        if (Build.VERSION.SDK_INT < 14) {
            throw new RuntimeException("don't support under SDK version 14!");
        }
        HiddenApiReflection.reduceFieldArray(HiddenApiReflection.findField(classLoader, "pathList").get(classLoader), "dexElements", sPatchDexCount);
    }
}
