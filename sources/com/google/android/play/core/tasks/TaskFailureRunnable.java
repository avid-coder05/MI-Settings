package com.google.android.play.core.tasks;

/* loaded from: classes2.dex */
final class TaskFailureRunnable implements Runnable {
    private final InvokeFailureListener mFailureExecutor;
    private final Task mTask;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskFailureRunnable(InvokeFailureListener invokeFailureListener, Task task) {
        this.mFailureExecutor = invokeFailureListener;
        this.mTask = task;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.mFailureExecutor.lock) {
            OnFailureListener onFailureListener = this.mFailureExecutor.mListener;
            if (onFailureListener != null) {
                onFailureListener.onFailure(this.mTask.getException());
            }
        }
    }
}
