package com.google.android.play.core.tasks;

/* loaded from: classes2.dex */
public class TaskWrapper<TResult> {
    private final TaskImpl<TResult> mTask = new TaskImpl<>();

    public final Task<TResult> getTask() {
        return this.mTask;
    }

    public final boolean setException(Exception exc) {
        return this.mTask.setException(exc);
    }

    public final boolean setResult(TResult tresult) {
        return this.mTask.setResult(tresult);
    }
}
