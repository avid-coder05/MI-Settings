package com.android.settings;

import android.os.MessageQueue;

/* loaded from: classes.dex */
public class TrimMemoryUtils {
    private final MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() { // from class: com.android.settings.TrimMemoryUtils$$ExternalSyntheticLambda0
        @Override // android.os.MessageQueue.IdleHandler
        public final boolean queueIdle() {
            boolean lambda$new$0;
            lambda$new$0 = TrimMemoryUtils.lambda$new$0();
            return lambda$new$0;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$0() {
        return true;
    }

    public void addIdleHandler() {
    }

    public void removeIdleHandler() {
    }
}
