package com.google.android.play.core.tasks;

import java.util.concurrent.Executor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class InvokeCompleteListener<TResult> implements InvocationListener<TResult> {
    final Object lock = new Object();
    private final Executor mExecutor;
    final OnCompleteListener<? super TResult> mListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InvokeCompleteListener(Executor executor, OnCompleteListener<? super TResult> onCompleteListener) {
        this.mExecutor = executor;
        this.mListener = onCompleteListener;
    }

    @Override // com.google.android.play.core.tasks.InvocationListener
    public void invoke(Task<TResult> task) {
        synchronized (this.lock) {
            if (this.mListener == null) {
                return;
            }
            this.mExecutor.execute(new TaskCompleteRunnable(this, task));
        }
    }
}
