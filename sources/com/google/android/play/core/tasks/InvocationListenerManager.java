package com.google.android.play.core.tasks;

import java.util.ArrayDeque;
import java.util.Queue;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class InvocationListenerManager<TResult> {
    private boolean invoked;
    private final Object lock = new Object();
    private Queue<InvocationListener<TResult>> mInvocationListenerQueue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addInvocationListener(InvocationListener<TResult> invocationListener) {
        synchronized (this.lock) {
            if (this.mInvocationListenerQueue == null) {
                this.mInvocationListenerQueue = new ArrayDeque();
            }
            this.mInvocationListenerQueue.add(invocationListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invokeListener(Task<TResult> task) {
        InvocationListener<TResult> poll;
        synchronized (this.lock) {
            if (this.mInvocationListenerQueue != null && !this.invoked) {
                this.invoked = true;
                while (true) {
                    synchronized (this.lock) {
                        poll = this.mInvocationListenerQueue.poll();
                        if (poll == null) {
                            this.invoked = false;
                            return;
                        }
                    }
                    poll.invoke(task);
                }
            }
        }
    }
}
