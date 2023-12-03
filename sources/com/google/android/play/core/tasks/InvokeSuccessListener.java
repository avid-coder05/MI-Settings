package com.google.android.play.core.tasks;

import java.util.concurrent.Executor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class InvokeSuccessListener<TResult> implements InvocationListener<TResult> {
    final Object lock = new Object();
    private final Executor mExecutor;
    final OnSuccessListener<? super TResult> mListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InvokeSuccessListener(Executor executor, OnSuccessListener<? super TResult> onSuccessListener) {
        this.mExecutor = executor;
        this.mListener = onSuccessListener;
    }

    @Override // com.google.android.play.core.tasks.InvocationListener
    public void invoke(Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.lock) {
                if (this.mListener == null) {
                    return;
                }
                this.mExecutor.execute(new TaskSuccessRunnable(this, task));
            }
        }
    }
}
