package src.com.android.settings.emergency.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes5.dex */
public class ThreadPool {
    private static ExecutorService sExecutor = Executors.newFixedThreadPool(10);

    public static void execute(Runnable runnable) {
        sExecutor.execute(runnable);
    }
}
