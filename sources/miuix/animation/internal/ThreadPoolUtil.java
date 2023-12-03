package miuix.animation.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes5.dex */
public class ThreadPoolUtil {
    private static final int CPU_COUNT;
    private static final int KEEP_POOL_SIZE;
    public static final int MAX_SPLIT_COUNT;
    private static final ThreadPoolExecutor sCacheThread;
    private static final Executor sSingleThread;

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        int i = (availableProcessors * 2) + 1;
        MAX_SPLIT_COUNT = i;
        int i2 = availableProcessors < 4 ? 0 : (availableProcessors / 2) + 1;
        KEEP_POOL_SIZE = i2;
        sCacheThread = new ThreadPoolExecutor(i2, i + 3, 30L, TimeUnit.SECONDS, new SynchronousQueue(), getThreadFactory("AnimThread"), new RejectedExecutionHandler() { // from class: miuix.animation.internal.ThreadPoolUtil.1
            @Override // java.util.concurrent.RejectedExecutionHandler
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                ThreadPoolUtil.sSingleThread.execute(runnable);
            }
        });
        sSingleThread = Executors.newSingleThreadExecutor(getThreadFactory("WorkThread"));
    }

    public static void getSplitCount(int i, int[] iArr) {
        int max = Math.max(i / 4000, 1);
        int i2 = MAX_SPLIT_COUNT;
        if (max > i2) {
            max = i2;
        }
        int ceil = (int) Math.ceil(i / max);
        iArr[0] = max;
        iArr[1] = ceil;
    }

    private static ThreadFactory getThreadFactory(final String str) {
        return new ThreadFactory() { // from class: miuix.animation.internal.ThreadPoolUtil.2
            final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, str + "-" + this.threadNumber.getAndIncrement());
                thread.setPriority(5);
                return thread;
            }
        };
    }

    public static void post(Runnable runnable) {
        sCacheThread.execute(runnable);
    }
}
