package com.xiaomi.opensdk.exception;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import org.apache.http.conn.ConnectTimeoutException;

/* loaded from: classes2.dex */
public class RetriableException extends Exception {
    private static final long serialVersionUID = 1;
    private final String mDescription;
    private final long mRetrytime;
    private final int ERRORCODE_NOT_DEFINE = -1;
    private int mErrorCode = -1;

    public RetriableException(String str, long j) {
        this.mDescription = str;
        this.mRetrytime = j;
    }

    public static boolean isRetriableException(Throwable th) {
        return (th instanceof ConnectException) || (th instanceof ConnectTimeoutException) || (th instanceof SocketException) || (th instanceof SocketTimeoutException) || (th instanceof UnknownHostException) || (th instanceof SSLException);
    }

    @Override // java.lang.Throwable
    public String toString() {
        String simpleName = RetriableException.class.getSimpleName();
        if (this.mDescription != null) {
            return simpleName + ": " + this.mDescription + ", retry after " + (this.mRetrytime / 1000) + " seconds";
        }
        return simpleName;
    }
}
