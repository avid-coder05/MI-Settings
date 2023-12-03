package com.google.android.play.core.tasks;

import java.util.concurrent.Executor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class InvokeFailureListener<TResult> implements InvocationListener<TResult> {
    final Object lock = new Object();
    private final Executor mExecutor;
    final OnFailureListener mListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InvokeFailureListener(Executor executor, OnFailureListener onFailureListener) {
        this.mExecutor = executor;
        this.mListener = onFailureListener;
    }

    @Override // com.google.android.play.core.tasks.InvocationListener
    public void invoke(Task<TResult> task) {
        if (task.isSuccessful()) {
            return;
        }
        synchronized (this.lock) {
            if (this.mListener == null) {
                return;
            }
            this.mExecutor.execute(new TaskFailureRunnable(this, task));
        }
    }
}
