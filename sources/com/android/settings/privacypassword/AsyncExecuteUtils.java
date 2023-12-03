package com.android.settings.privacypassword;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes2.dex */
public class AsyncExecuteUtils {
    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(7);

    public static void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}
