package com.google.android.play.core.remote;

import com.google.android.play.core.tasks.TaskWrapper;

/* loaded from: classes2.dex */
public abstract class RemoteTask implements Runnable {
    private final TaskWrapper<?> task;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RemoteTask() {
        this.task = null;
    }

    public RemoteTask(TaskWrapper<?> taskWrapper) {
        this.task = taskWrapper;
    }

    protected abstract void execute();

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TaskWrapper getTask() {
        return this.task;
    }

    @Override // java.lang.Runnable
    public final void run() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
            if (this.task != null) {
                this.task.setException(e);
            }
        }
    }
}
