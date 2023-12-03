package com.google.android.play.core.splitinstall;

import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public final class SplitSessionLoaderSingleton {
    private static final AtomicReference<SplitSessionLoader> sSplitLoaderHolder = new AtomicReference<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SplitSessionLoader get() {
        return sSplitLoaderHolder.get();
    }

    public static void set(SplitSessionLoader splitSessionLoader) {
        sSplitLoaderHolder.compareAndSet(null, splitSessionLoader);
    }
}
