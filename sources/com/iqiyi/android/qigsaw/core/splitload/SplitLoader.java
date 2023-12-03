package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Context;
import java.io.File;
import java.util.List;

/* loaded from: classes2.dex */
abstract class SplitLoader {
    private final Context context;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoader(Context context) {
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitDexClassLoader loadCode(String str, List<String> list, File file, File file2, List<String> list2) throws SplitLoadException {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadCode2(List<String> list, File file, File file2) throws SplitLoadException {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void loadResources(String str) throws SplitLoadException {
        try {
            Context context = this.context;
            SplitCompatResourcesLoader.loadResources(context, context.getResources(), str);
        } catch (Throwable th) {
            throw new SplitLoadException(-21, th);
        }
    }
}
