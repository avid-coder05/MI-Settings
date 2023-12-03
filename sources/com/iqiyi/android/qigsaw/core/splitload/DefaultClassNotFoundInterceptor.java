package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.extension.AABExtension;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import java.util.List;

/* loaded from: classes2.dex */
final class DefaultClassNotFoundInterceptor implements ClassNotFoundInterceptor {
    private static final String TAG = "Split:ClassNotFound";
    private final Context context;
    private final ClassLoader originClassLoader;
    private final int splitLoadMode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultClassNotFoundInterceptor(Context context, ClassLoader classLoader, int i) {
        this.context = context;
        this.originClassLoader = classLoader;
        this.splitLoadMode = i;
    }

    private Class<?> findClassInSplits(String str) {
        for (SplitDexClassLoader splitDexClassLoader : SplitApplicationLoaders.getInstance().getValidClassLoaders()) {
            try {
                Class<?> loadClassItself = splitDexClassLoader.loadClassItself(str);
                SplitLog.i(TAG, "Class %s is found in %s ClassLoader", str, splitDexClassLoader.moduleName());
                return loadClassItself;
            } catch (ClassNotFoundException unused) {
                SplitLog.w(TAG, "Class %s is not found in %s ClassLoader", str, splitDexClassLoader.moduleName());
            }
        }
        return null;
    }

    private boolean isSplitEntryFragments(String str) {
        List<String> splitEntryFragments;
        SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
        if (splitInfoManagerService == null || (splitEntryFragments = splitInfoManagerService.getSplitEntryFragments(this.context)) == null || splitEntryFragments.isEmpty()) {
            return false;
        }
        return splitEntryFragments.contains(str);
    }

    private Class<?> onClassNotFound(String str) {
        Class<?> findClassInSplits = findClassInSplits(str);
        if (findClassInSplits != null) {
            return findClassInSplits;
        }
        Class<?> fakeComponent = AABExtension.getInstance().getFakeComponent(str);
        if (fakeComponent != null || isSplitEntryFragments(str)) {
            SplitLoadManagerService.getInstance().loadInstalledSplits();
            Class<?> findClassInSplits2 = findClassInSplits(str);
            if (findClassInSplits2 != null) {
                SplitLog.i(TAG, "Class %s is found in Splits after loading all installed splits.", str);
                return findClassInSplits2;
            } else if (fakeComponent != null) {
                SplitLog.w(TAG, "Split component %s is still not found after installing all installed splits, return a %s to avoid crash", str, fakeComponent.getSimpleName());
                return fakeComponent;
            } else {
                return null;
            }
        }
        return null;
    }

    private Class<?> onClassNotFound2(String str) {
        Class<?> fakeComponent = AABExtension.getInstance().getFakeComponent(str);
        if (fakeComponent != null || isSplitEntryFragments(str)) {
            SplitLoadManagerService.getInstance().loadInstalledSplits();
            try {
                return this.originClassLoader.loadClass(str);
            } catch (ClassNotFoundException unused) {
                if (fakeComponent != null) {
                    SplitLog.w(TAG, "Split component %s is still not found after installing all installed splits,return a %s to avoid crash", str, fakeComponent.getSimpleName());
                    return fakeComponent;
                }
                return null;
            }
        }
        return null;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.ClassNotFoundInterceptor
    public Class<?> findClass(String str) {
        if (SplitLoadManagerService.hasInstance()) {
            int i = this.splitLoadMode;
            if (i == 1) {
                return onClassNotFound(str);
            }
            if (i == 2) {
                return onClassNotFound2(str);
            }
            return null;
        }
        return null;
    }
}
