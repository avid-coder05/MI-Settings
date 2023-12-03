package com.google.android.play.core.splitcompat;

import android.content.Context;
import com.google.android.play.core.splitinstall.LoadedSplitFetcherSingleton;
import com.google.android.play.core.splitinstall.SplitSessionLoaderSingleton;
import com.google.android.play.core.tasks.TaskExecutors;
import com.iqiyi.android.qigsaw.core.splitload.SplitLoadManagerService;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public class SplitCompat {
    private static final AtomicReference<SplitCompat> sSplitCompatReference = new AtomicReference<>(null);

    private SplitCompat() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasInstance() {
        return sSplitCompatReference.get() != null;
    }

    public static boolean install(Context context) {
        return installInternal(context);
    }

    private static boolean installInternal(Context context) {
        AtomicReference<SplitCompat> atomicReference = sSplitCompatReference;
        if (atomicReference.compareAndSet(null, new SplitCompat())) {
            SplitCompat splitCompat = atomicReference.get();
            SplitSessionLoaderSingleton.set(new SplitSessionLoaderImpl(TaskExecutors.MAIN_THREAD));
            LoadedSplitFetcherSingleton.set(new LoadedSplitFetcherImpl(splitCompat));
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Set<String> getLoadedSplits() {
        return SplitLoadManagerService.getInstance().getLoadedSplitNames();
    }
}
