package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Intent;
import com.iqiyi.android.qigsaw.core.splitload.listener.OnSplitLoadListener;
import java.io.File;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitLoadTaskImpl extends SplitLoadTask {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoadTaskImpl(SplitLoadManager splitLoadManager, List<Intent> list, OnSplitLoadListener onSplitLoadListener) {
        super(splitLoadManager, list, onSplitLoadListener);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoaderWrapper
    public SplitLoader createSplitLoader() {
        return new SplitLoaderImpl(getContext());
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoaderWrapper
    public ClassLoader loadCode(String str, List<String> list, File file, File file2, List<String> list2) throws SplitLoadException {
        SplitDexClassLoader classLoader = SplitApplicationLoaders.getInstance().getClassLoader(str);
        if (classLoader == null) {
            SplitDexClassLoader loadCode = getSplitLoader().loadCode(str, list, file, file2, list2);
            loadCode.setValid(true);
            SplitApplicationLoaders.getInstance().addClassLoader(loadCode);
            return loadCode;
        }
        return classLoader;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoaderWrapper
    public void unloadCode(ClassLoader classLoader) {
        if (classLoader instanceof SplitDexClassLoader) {
            ((SplitDexClassLoader) classLoader).setValid(false);
        }
    }
}
