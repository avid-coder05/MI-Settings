package com.iqiyi.android.qigsaw.core.splitinstall;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/* loaded from: classes2.dex */
final class SplitInstallerExecutor {
    private static final Executor sExecutor = Executors.newSingleThreadScheduledExecutor(new SplitInstallerThread());

    SplitInstallerExecutor() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Executor getExecutor() {
        return sExecutor;
    }
}
