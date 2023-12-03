package com.xiaomi.opensdk.exception;

/* loaded from: classes2.dex */
public class UnretriableException extends Exception {
    private static final long serialVersionUID = 1;
    private String mDescription;
    private int mErrorCode;

    public UnretriableException(String str) {
        this.mDescription = str;
    }

    public UnretriableException(Throwable th) {
        super(th);
    }

    @Override // java.lang.Throwable
    public String toString() {
        String simpleName = UnretriableException.class.getSimpleName();
        Throwable cause = getCause();
        if (cause != null) {
            String str = simpleName + "[" + cause.getClass().getSimpleName();
            if (cause.getMessage() != null) {
                str = str + ":" + cause.getMessage();
            }
            simpleName = str + "]";
        }
        if (this.mErrorCode > 0) {
            simpleName = simpleName + "[" + this.mErrorCode + "]";
        }
        if (this.mDescription != null) {
            return simpleName + ": " + this.mDescription;
        }
        return simpleName;
    }
}
