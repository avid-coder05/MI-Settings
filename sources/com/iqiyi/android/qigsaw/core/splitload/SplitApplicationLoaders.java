package com.iqiyi.android.qigsaw.core.splitload;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
final class SplitApplicationLoaders {
    private static final AtomicReference<SplitApplicationLoaders> sInstance = new AtomicReference<>();
    private final Set<SplitDexClassLoader> splitDexClassLoaders = Collections.newSetFromMap(new ConcurrentHashMap());

    SplitApplicationLoaders() {
    }

    public static SplitApplicationLoaders getInstance() {
        AtomicReference<SplitApplicationLoaders> atomicReference = sInstance;
        if (atomicReference.get() == null) {
            atomicReference.set(new SplitApplicationLoaders());
        }
        return atomicReference.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addClassLoader(SplitDexClassLoader splitDexClassLoader) {
        this.splitDexClassLoaders.add(splitDexClassLoader);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitDexClassLoader getClassLoader(String str) {
        for (SplitDexClassLoader splitDexClassLoader : this.splitDexClassLoaders) {
            if (splitDexClassLoader.moduleName().equals(str)) {
                return splitDexClassLoader;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitDexClassLoader getValidClassLoader(String str) {
        for (SplitDexClassLoader splitDexClassLoader : this.splitDexClassLoaders) {
            if (splitDexClassLoader.moduleName().equals(str) && splitDexClassLoader.isValid()) {
                return splitDexClassLoader;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<SplitDexClassLoader> getValidClassLoaders() {
        HashSet hashSet = new HashSet(this.splitDexClassLoaders.size());
        for (SplitDexClassLoader splitDexClassLoader : this.splitDexClassLoaders) {
            if (splitDexClassLoader.isValid()) {
                hashSet.add(splitDexClassLoader);
            }
        }
        return hashSet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<SplitDexClassLoader> getValidClassLoaders(List<String> list) {
        if (list == null) {
            return null;
        }
        HashSet hashSet = new HashSet(list.size());
        for (SplitDexClassLoader splitDexClassLoader : this.splitDexClassLoaders) {
            if (list.contains(splitDexClassLoader.moduleName()) && splitDexClassLoader.isValid()) {
                hashSet.add(splitDexClassLoader);
            }
        }
        return hashSet;
    }
}
