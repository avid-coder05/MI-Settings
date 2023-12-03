package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Context;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
final class SplitLoaderImpl2 extends SplitLoader {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoaderImpl2(Context context) {
        super(context);
    }

    private void loadDex(ClassLoader classLoader, List<String> list, File file) throws SplitLoadException {
        if (list != null) {
            ArrayList arrayList = new ArrayList(list.size());
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(new File(it.next()));
            }
            try {
                SplitCompatDexLoader.load(classLoader, file, arrayList);
                SplitUnKnownFileTypeDexLoader.loadDex(classLoader, list, file);
            } catch (Throwable th) {
                throw new SplitLoadException(-23, th);
            }
        }
    }

    private void loadLibrary(ClassLoader classLoader, File file) throws SplitLoadException {
        if (file != null) {
            try {
                SplitCompatLibraryLoader.load(classLoader, file);
            } catch (Throwable th) {
                throw new SplitLoadException(-22, th);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoader
    public void loadCode2(List<String> list, File file, File file2) throws SplitLoadException {
        ClassLoader classLoader = SplitLoader.class.getClassLoader();
        loadLibrary(classLoader, file2);
        loadDex(classLoader, list, file);
    }
}
