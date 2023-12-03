package com.google.android.play.core.splitcompat;

import android.content.Intent;
import com.google.android.play.core.splitinstall.SplitSessionLoader;
import com.google.android.play.core.splitinstall.SplitSessionStatusChanger;
import java.util.List;
import java.util.concurrent.Executor;

/* loaded from: classes2.dex */
final class SplitSessionLoaderImpl implements SplitSessionLoader {
    private final Executor mExecutor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitSessionLoaderImpl(Executor executor) {
        this.mExecutor = executor;
    }

    @Override // com.google.android.play.core.splitinstall.SplitSessionLoader
    public void load(List<Intent> list, SplitSessionStatusChanger splitSessionStatusChanger) {
        if (!SplitCompat.hasInstance()) {
            throw new IllegalStateException("Ingestion should only be called in SplitCompat mode.");
        }
        this.mExecutor.execute(new SplitLoadSessionTask(list, splitSessionStatusChanger));
    }
}
