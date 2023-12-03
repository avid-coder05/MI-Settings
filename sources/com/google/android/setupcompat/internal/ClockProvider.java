package com.google.android.setupcompat.internal;

import com.google.android.setupcompat.internal.ClockProvider;
import java.util.concurrent.TimeUnit;

/* loaded from: classes2.dex */
public class ClockProvider {
    private static final Ticker SYSTEM_TICKER;
    private static Ticker ticker;

    /* loaded from: classes2.dex */
    public interface Supplier<T> {
        T get();
    }

    static {
        ClockProvider$$ExternalSyntheticLambda1 clockProvider$$ExternalSyntheticLambda1 = new Ticker() { // from class: com.google.android.setupcompat.internal.ClockProvider$$ExternalSyntheticLambda1
            @Override // com.google.android.setupcompat.internal.Ticker
            public final long read() {
                long lambda$static$1;
                lambda$static$1 = ClockProvider.lambda$static$1();
                return lambda$static$1;
            }
        };
        SYSTEM_TICKER = clockProvider$$ExternalSyntheticLambda1;
        ticker = clockProvider$$ExternalSyntheticLambda1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ long lambda$setInstance$0(Supplier supplier) {
        return ((Long) supplier.get()).longValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ long lambda$static$1() {
        return System.nanoTime();
    }

    public static void resetInstance() {
        ticker = SYSTEM_TICKER;
    }

    public static void setInstance(final Supplier<Long> supplier) {
        ticker = new Ticker() { // from class: com.google.android.setupcompat.internal.ClockProvider$$ExternalSyntheticLambda0
            @Override // com.google.android.setupcompat.internal.Ticker
            public final long read() {
                long lambda$setInstance$0;
                lambda$setInstance$0 = ClockProvider.lambda$setInstance$0(ClockProvider.Supplier.this);
                return lambda$setInstance$0;
            }
        };
    }

    public static long timeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(timeInNanos());
    }

    public static long timeInNanos() {
        return ticker.read();
    }
}
