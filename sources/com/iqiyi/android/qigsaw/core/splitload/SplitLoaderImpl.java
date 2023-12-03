package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Context;
import java.io.File;
import java.util.List;

/* loaded from: classes2.dex */
final class SplitLoaderImpl extends SplitLoader {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoaderImpl(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoader
    public SplitDexClassLoader loadCode(String str, List<String> list, File file, File file2, List<String> list2) throws SplitLoadException {
        try {
            return SplitDexClassLoader.create(str, list, file, file2, list2);
        } catch (Throwable th) {
            throw new SplitLoadException(-27, th);
        }
    }
}
