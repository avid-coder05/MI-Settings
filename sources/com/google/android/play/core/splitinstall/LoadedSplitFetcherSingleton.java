package com.google.android.play.core.splitinstall;

import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public final class LoadedSplitFetcherSingleton {
    private static final AtomicReference<LoadedSplitFetcher> sInstalledSplitsFetcherRef = new AtomicReference<>(null);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static LoadedSplitFetcher get() {
        return sInstalledSplitsFetcherRef.get();
    }

    public static void set(LoadedSplitFetcher loadedSplitFetcher) {
        sInstalledSplitsFetcherRef.compareAndSet(null, loadedSplitFetcher);
    }
}
