package com.iqiyi.android.qigsaw.core.splitload;

/* loaded from: classes2.dex */
public class SplitDelegateClassLoaderFactory {
    public static ClassLoader instantiateClassLoader(ClassLoader classLoader) {
        return new SplitDelegateClassloader(classLoader);
    }
}
