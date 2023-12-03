package miui.yellowpage;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: classes4.dex */
public class ThreadPool {
    private static int THREAD_POOL_COUNT = 32;
    private static ThreadPoolExecutor sExecutor;

    static {
        int i = THREAD_POOL_COUNT;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(i, i, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        sExecutor = threadPoolExecutor;
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    private ThreadPool() {
    }

    public static void execute(Runnable runnable) {
        sExecutor.execute(runnable);
    }
}
