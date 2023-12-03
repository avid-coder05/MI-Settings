package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Intent;
import com.iqiyi.android.qigsaw.core.splitload.listener.OnSplitLoadListener;
import java.io.File;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitLoadTaskImpl2 extends SplitLoadTask {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoadTaskImpl2(SplitLoadManager splitLoadManager, List<Intent> list, OnSplitLoadListener onSplitLoadListener) {
        super(splitLoadManager, list, onSplitLoadListener);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoaderWrapper
    public SplitLoader createSplitLoader() {
        return new SplitLoaderImpl2(getContext());
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoaderWrapper
    public ClassLoader loadCode(String str, List<String> list, File file, File file2, List<String> list2) throws SplitLoadException {
        getSplitLoader().loadCode2(list, file, file2);
        return SplitLoadTask.class.getClassLoader();
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoaderWrapper
    public void unloadCode(ClassLoader classLoader) {
        try {
            SplitCompatDexLoader.unLoad(classLoader);
        } catch (Throwable unused) {
        }
    }
}
