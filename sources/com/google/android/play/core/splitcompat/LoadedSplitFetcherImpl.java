package com.google.android.play.core.splitcompat;

import com.google.android.play.core.splitinstall.LoadedSplitFetcher;
import java.util.Set;

/* loaded from: classes2.dex */
final class LoadedSplitFetcherImpl implements LoadedSplitFetcher {
    private final SplitCompat mSplitCompat;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoadedSplitFetcherImpl(SplitCompat splitCompat) {
        this.mSplitCompat = splitCompat;
    }

    @Override // com.google.android.play.core.splitinstall.LoadedSplitFetcher
    public Set<String> loadedSplits() {
        return this.mSplitCompat.getLoadedSplits();
    }
}
