package com.xiaomi.opensdk.exception;

/* loaded from: classes2.dex */
public class ServerException extends Exception {
    private int statusCode;

    public ServerException() {
    }

    public ServerException(String str) {
        super(str);
    }

    public ServerException(String str, Throwable th) {
        super(str, th);
    }
}
