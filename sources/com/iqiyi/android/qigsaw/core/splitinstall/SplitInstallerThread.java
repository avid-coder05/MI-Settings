package com.iqiyi.android.qigsaw.core.splitinstall;

import android.annotation.SuppressLint;
import java.util.concurrent.ThreadFactory;

/* loaded from: classes2.dex */
final class SplitInstallerThread implements ThreadFactory {
    @Override // java.util.concurrent.ThreadFactory
    @SuppressLint({"NewThreadDirectly"})
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, "split_install_thread");
    }
}
