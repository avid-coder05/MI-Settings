package com.google.android.play.core.tasks;

/* loaded from: classes2.dex */
final class TaskSuccessRunnable implements Runnable {
    private final InvokeSuccessListener mSuccessExecutor;
    private final Task mTask;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskSuccessRunnable(InvokeSuccessListener invokeSuccessListener, Task task) {
        this.mSuccessExecutor = invokeSuccessListener;
        this.mTask = task;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.mSuccessExecutor.lock) {
            OnSuccessListener<? super TResult> onSuccessListener = this.mSuccessExecutor.mListener;
            if (onSuccessListener != 0) {
                onSuccessListener.onSuccess(this.mTask.getResult());
            }
        }
    }
}
