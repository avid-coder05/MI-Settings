package com.xiaomi.micloudsdk.sync;

import android.os.Bundle;
import android.os.ResultReceiver;
import java.util.concurrent.CountDownLatch;

/* loaded from: classes2.dex */
class FileSyncLogSender$1 extends ResultReceiver {
    final /* synthetic */ CountDownLatch val$waiter;

    @Override // android.os.ResultReceiver
    protected void onReceiveResult(int i, Bundle bundle) {
        this.val$waiter.countDown();
    }
}
