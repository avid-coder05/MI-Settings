package com.google.android.play.core.tasks;

import java.util.concurrent.Executor;

/* loaded from: classes2.dex */
class TaskImpl<TResult> extends Task<TResult> {
    private boolean isComplete;
    private Exception mException;
    private TResult mResult;
    private final Object lock = new Object();
    private InvocationListenerManager<TResult> mListenerManager = new InvocationListenerManager<>();

    private void assertComplete() {
        if (!this.isComplete) {
            throw new RuntimeException("Task is not yet complete");
        }
    }

    private void invokeListeners() {
        synchronized (this.lock) {
            if (this.isComplete) {
                this.mListenerManager.invokeListener(this);
            }
        }
    }

    @Override // com.google.android.play.core.tasks.Task
    public Task<TResult> addOnCompleteListener(OnCompleteListener<TResult> onCompleteListener) {
        return addOnCompleteListener(TaskExecutors.MAIN_THREAD, onCompleteListener);
    }

    @Override // com.google.android.play.core.tasks.Task
    public Task<TResult> addOnCompleteListener(Executor executor, OnCompleteListener<TResult> onCompleteListener) {
        this.mListenerManager.addInvocationListener(new InvokeCompleteListener(executor, onCompleteListener));
        invokeListeners();
        return this;
    }

    @Override // com.google.android.play.core.tasks.Task
    public Task<TResult> addOnFailureListener(OnFailureListener onFailureListener) {
        return addOnFailureListener(TaskExecutors.MAIN_THREAD, onFailureListener);
    }

    @Override // com.google.android.play.core.tasks.Task
    public Task<TResult> addOnFailureListener(Executor executor, OnFailureListener onFailureListener) {
        this.mListenerManager.addInvocationListener(new InvokeFailureListener(executor, onFailureListener));
        invokeListeners();
        return this;
    }

    @Override // com.google.android.play.core.tasks.Task
    public Task<TResult> addOnSuccessListener(OnSuccessListener<? super TResult> onSuccessListener) {
        return addOnSuccessListener(TaskExecutors.MAIN_THREAD, onSuccessListener);
    }

    @Override // com.google.android.play.core.tasks.Task
    public Task<TResult> addOnSuccessListener(Executor executor, OnSuccessListener<? super TResult> onSuccessListener) {
        this.mListenerManager.addInvocationListener(new InvokeSuccessListener(executor, onSuccessListener));
        invokeListeners();
        return this;
    }

    @Override // com.google.android.play.core.tasks.Task
    public Exception getException() {
        Exception exc;
        synchronized (this.lock) {
            exc = this.mException;
        }
        return exc;
    }

    @Override // com.google.android.play.core.tasks.Task
    public TResult getResult() {
        TResult tresult;
        synchronized (this.lock) {
            assertComplete();
            if (this.mException != null) {
                throw new RuntimeExecutionException(this.mException);
            }
            tresult = this.mResult;
        }
        return tresult;
    }

    @Override // com.google.android.play.core.tasks.Task
    public <X extends Throwable> TResult getResult(Class<X> cls) throws Throwable {
        return null;
    }

    @Override // com.google.android.play.core.tasks.Task
    public boolean isComplete() {
        boolean z;
        synchronized (this.lock) {
            z = this.isComplete;
        }
        return z;
    }

    @Override // com.google.android.play.core.tasks.Task
    public boolean isSuccessful() {
        boolean z;
        synchronized (this.lock) {
            z = this.isComplete && this.mException == null;
        }
        return z;
    }

    public boolean setException(Exception exc) {
        synchronized (this.lock) {
            if (this.isComplete) {
                return false;
            }
            this.isComplete = true;
            this.mException = exc;
            this.mListenerManager.invokeListener(this);
            return true;
        }
    }

    public boolean setResult(TResult tresult) {
        synchronized (this.lock) {
            if (this.isComplete) {
                return false;
            }
            this.isComplete = true;
            this.mResult = tresult;
            this.mListenerManager.invokeListener(this);
            return true;
        }
    }

    public final void setResultCheck(TResult tresult) {
        synchronized (this.lock) {
            if (this.isComplete) {
                throw new RuntimeException("Task is already complete");
            }
            this.isComplete = true;
            this.mResult = tresult;
        }
        this.mListenerManager.invokeListener(this);
    }
}
