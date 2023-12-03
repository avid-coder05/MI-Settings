package com.google.android.play.core.remote;

/* loaded from: classes2.dex */
public class RemoteServiceException extends RuntimeException {
    public RemoteServiceException() {
        super("Failed to bind to the service.");
    }

    public RemoteServiceException(String str) {
        super(str);
    }

    public RemoteServiceException(String str, Throwable th) {
        super(str, th);
    }
}
