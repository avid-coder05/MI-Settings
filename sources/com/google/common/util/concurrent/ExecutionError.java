package com.google.common.util.concurrent;

/* loaded from: classes2.dex */
public class ExecutionError extends Error {
    private static final long serialVersionUID = 0;

    protected ExecutionError() {
    }

    public ExecutionError(Error error) {
        super(error);
    }

    protected ExecutionError(String str) {
        super(str);
    }
}
