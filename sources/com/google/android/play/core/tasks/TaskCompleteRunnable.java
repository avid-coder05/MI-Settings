package com.google.android.play.core.tasks;

/* loaded from: classes2.dex */
final class TaskCompleteRunnable implements Runnable {
    private final InvokeCompleteListener mCompleteExecutor;
    private final Task mTask;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskCompleteRunnable(InvokeCompleteListener invokeCompleteListener, Task task) {
        this.mCompleteExecutor = invokeCompleteListener;
        this.mTask = task;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.mCompleteExecutor.lock) {
            OnCompleteListener<? super TResult> onCompleteListener = this.mCompleteExecutor.mListener;
            if (onCompleteListener != 0) {
                onCompleteListener.onComplete(this.mTask);
            }
        }
    }
}
