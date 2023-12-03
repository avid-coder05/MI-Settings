package com.iqiyi.android.qigsaw.core.splitload;

import android.text.TextUtils;
import androidx.annotation.Keep;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import dalvik.system.BaseDexClassLoader;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
@Keep
/* loaded from: classes2.dex */
public final class SplitDexClassLoader extends BaseDexClassLoader {
    private static final String TAG = "SplitDexClassLoader";
    private Set<SplitDexClassLoader> dependenciesLoaders;
    private final String moduleName;
    private boolean valid;

    private SplitDexClassLoader(String str, List<String> list, File file, String str2, List<String> list2, ClassLoader classLoader) throws Throwable {
        super(list == null ? "" : TextUtils.join(File.pathSeparator, list), file, str2, classLoader);
        this.moduleName = str;
        this.dependenciesLoaders = SplitApplicationLoaders.getInstance().getValidClassLoaders(list2);
        SplitUnKnownFileTypeDexLoader.loadDex(this, list, file);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SplitDexClassLoader create(String str, List<String> list, File file, File file2, List<String> list2) throws Throwable {
        long currentTimeMillis = System.currentTimeMillis();
        SplitDexClassLoader splitDexClassLoader = new SplitDexClassLoader(str, list, file, file2 == null ? null : file2.getAbsolutePath(), list2, SplitDexClassLoader.class.getClassLoader());
        SplitLog.d(TAG, "Cost %d ms to load %s code", Long.valueOf(System.currentTimeMillis() - currentTimeMillis), str);
        return splitDexClassLoader;
    }

    @Override // dalvik.system.BaseDexClassLoader, java.lang.ClassLoader
    protected Class<?> findClass(String str) throws ClassNotFoundException {
        try {
            return super.findClass(str);
        } catch (ClassNotFoundException e) {
            if (this.dependenciesLoaders != null) {
                Iterator<SplitDexClassLoader> it = this.dependenciesLoaders.iterator();
                while (it.hasNext()) {
                    SplitDexClassLoader next = it.next();
                    try {
                        return next.loadClassItself(str);
                    } catch (ClassNotFoundException unused) {
                        SplitLog.w(TAG, "SplitDexClassLoader: Class %s is not found in %s ClassLoader", str, next.moduleName());
                    }
                }
            }
            throw e;
        }
    }

    @Override // dalvik.system.BaseDexClassLoader, java.lang.ClassLoader
    public String findLibrary(String str) {
        Set<SplitDexClassLoader> set;
        String findLibrary = super.findLibrary(str);
        if (findLibrary == null && (set = this.dependenciesLoaders) != null) {
            Iterator<SplitDexClassLoader> it = set.iterator();
            while (it.hasNext() && (findLibrary = it.next().findLibrary(str)) == null) {
            }
        }
        return (findLibrary == null && (getParent() instanceof BaseDexClassLoader)) ? ((BaseDexClassLoader) getParent()).findLibrary(str) : findLibrary;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String findLibraryItself(String str) {
        return super.findLibrary(str);
    }

    @Override // dalvik.system.BaseDexClassLoader, java.lang.ClassLoader
    protected URL findResource(String str) {
        Set<SplitDexClassLoader> set;
        URL findResource = super.findResource(str);
        if (findResource == null && (set = this.dependenciesLoaders) != null) {
            Iterator<SplitDexClassLoader> it = set.iterator();
            while (it.hasNext() && (findResource = it.next().findResourceItself(str)) == null) {
            }
        }
        return findResource;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public URL findResourceItself(String str) {
        return super.findResource(str);
    }

    @Override // dalvik.system.BaseDexClassLoader, java.lang.ClassLoader
    protected Enumeration<URL> findResources(String str) {
        Set<SplitDexClassLoader> set;
        Enumeration<URL> findResources = super.findResources(str);
        if (findResources == null && (set = this.dependenciesLoaders) != null) {
            Iterator<SplitDexClassLoader> it = set.iterator();
            while (it.hasNext() && (findResources = it.next().findResourcesItself(str)) == null) {
            }
        }
        return findResources;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Enumeration<URL> findResourcesItself(String str) {
        return super.findResources(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isValid() {
        return this.valid;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Class<?> loadClassItself(String str) throws ClassNotFoundException {
        Class<?> findLoadedClass = findLoadedClass(str);
        return findLoadedClass != null ? findLoadedClass : super.findClass(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String moduleName() {
        return this.moduleName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setValid(boolean z) {
        this.valid = z;
    }
}
