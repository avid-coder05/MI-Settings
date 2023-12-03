package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Context;
import androidx.annotation.Keep;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.PathClassLoader;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

@Keep
/* loaded from: classes2.dex */
final class SplitDelegateClassloader extends PathClassLoader {
    private static BaseDexClassLoader originClassLoader;
    private ClassNotFoundInterceptor classNotFoundInterceptor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitDelegateClassloader(ClassLoader classLoader) {
        super("", classLoader);
        originClassLoader = (BaseDexClassLoader) classLoader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void inject(ClassLoader classLoader, Context context) throws Exception {
        reflectPackageInfoClassloader(context, new SplitDelegateClassloader(classLoader));
    }

    private static void reflectPackageInfoClassloader(Context context, ClassLoader classLoader) throws Exception {
        Object obj = HiddenApiReflection.findField(context, "mPackageInfo").get(context);
        if (obj != null) {
            HiddenApiReflection.findField(obj, "mClassLoader").set(obj, classLoader);
        }
        try {
            HiddenApiReflection.findField(context, "mClassLoader").set(context, classLoader);
        } catch (Throwable unused) {
        }
    }

    @Override // dalvik.system.BaseDexClassLoader, java.lang.ClassLoader
    protected Class<?> findClass(String str) throws ClassNotFoundException {
        Class<?> findClass;
        try {
            return originClassLoader.loadClass(str);
        } catch (ClassNotFoundException e) {
            ClassNotFoundInterceptor classNotFoundInterceptor = this.classNotFoundInterceptor;
            if (classNotFoundInterceptor == null || (findClass = classNotFoundInterceptor.findClass(str)) == null) {
                throw e;
            }
            return findClass;
        }
    }

    @Override // dalvik.system.BaseDexClassLoader, java.lang.ClassLoader
    public String findLibrary(String str) {
        String findLibrary = originClassLoader.findLibrary(str);
        if (findLibrary == null) {
            Iterator<SplitDexClassLoader> it = SplitApplicationLoaders.getInstance().getValidClassLoaders().iterator();
            while (it.hasNext() && (findLibrary = it.next().findLibraryItself(str)) == null) {
            }
        }
        return findLibrary;
    }

    @Override // dalvik.system.BaseDexClassLoader, java.lang.ClassLoader
    protected URL findResource(String str) {
        URL findResource = super.findResource(str);
        if (findResource == null) {
            Iterator<SplitDexClassLoader> it = SplitApplicationLoaders.getInstance().getValidClassLoaders().iterator();
            while (it.hasNext() && (findResource = it.next().findResourceItself(str)) == null) {
            }
        }
        return findResource;
    }

    @Override // dalvik.system.BaseDexClassLoader, java.lang.ClassLoader
    protected Enumeration<URL> findResources(String str) {
        Enumeration<URL> findResources = super.findResources(str);
        if (findResources == null) {
            Iterator<SplitDexClassLoader> it = SplitApplicationLoaders.getInstance().getValidClassLoaders().iterator();
            while (it.hasNext() && (findResources = it.next().findResourcesItself(str)) == null) {
            }
        }
        return findResources;
    }

    @Override // java.lang.ClassLoader
    public URL getResource(String str) {
        return originClassLoader.getResource(str);
    }

    @Override // java.lang.ClassLoader
    public Enumeration<URL> getResources(String str) throws IOException {
        return originClassLoader.getResources(str);
    }

    @Override // java.lang.ClassLoader
    public Class<?> loadClass(String str) throws ClassNotFoundException {
        return findClass(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setClassNotFoundInterceptor(ClassNotFoundInterceptor classNotFoundInterceptor) {
        this.classNotFoundInterceptor = classNotFoundInterceptor;
    }
}
