package com.google.android.play.core.tasks;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes2.dex */
public class Tasks {

    /* loaded from: classes2.dex */
    private static class AwaitTaskListener<TResult> implements OnFailureListener, OnSuccessListener<TResult> {
        private final CountDownLatch countDownLatch;

        private AwaitTaskListener() {
            this.countDownLatch = new CountDownLatch(1);
        }

        void await() throws InterruptedException {
            this.countDownLatch.await();
        }

        boolean awaitTimeout(long j, TimeUnit timeUnit) throws InterruptedException {
            return this.countDownLatch.await(j, timeUnit);
        }

        @Override // com.google.android.play.core.tasks.OnFailureListener
        public void onFailure(Exception exc) {
            this.countDownLatch.countDown();
        }

        @Override // com.google.android.play.core.tasks.OnSuccessListener
        public void onSuccess(TResult tresult) {
            this.countDownLatch.countDown();
        }
    }

    private Tasks() {
    }

    private static void addTaskListener(Task<?> task, AwaitTaskListener awaitTaskListener) {
        Executor executor = TaskExecutors.sExecutor;
        task.addOnSuccessListener(executor, awaitTaskListener);
        task.addOnFailureListener(executor, awaitTaskListener);
    }

    public static <TResult> TResult await(Task<TResult> task) throws ExecutionException, InterruptedException {
        Objects.requireNonNull(task, "Task must not be null");
        if (task.isComplete()) {
            return (TResult) getResult(task);
        }
        AwaitTaskListener awaitTaskListener = new AwaitTaskListener();
        addTaskListener(task, awaitTaskListener);
        awaitTaskListener.await();
        return (TResult) getResult(task);
    }

    public static <TResult> TResult await(Task<TResult> task, long j, TimeUnit timeUnit) throws TimeoutException, InterruptedException, ExecutionException {
        Objects.requireNonNull(task, "Task must not be null");
        Objects.requireNonNull(timeUnit, "TimeUnit must not be null");
        if (task.isComplete()) {
            return (TResult) getResult(task);
        }
        AwaitTaskListener awaitTaskListener = new AwaitTaskListener();
        addTaskListener(task, awaitTaskListener);
        if (awaitTaskListener.awaitTimeout(j, timeUnit)) {
            return (TResult) getResult(task);
        }
        throw new TimeoutException("Timed out waiting for Task");
    }

    public static <TResult> Task<TResult> createTaskAndSetResult(TResult tresult) {
        TaskImpl taskImpl = new TaskImpl();
        taskImpl.setResult(tresult);
        return taskImpl;
    }

    private static <TResult> TResult getResult(Task<TResult> task) throws ExecutionException {
        if (task.isSuccessful()) {
            return task.getResult();
        }
        throw new ExecutionException(task.getException());
    }
}
