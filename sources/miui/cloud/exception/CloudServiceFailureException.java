package miui.cloud.exception;

/* loaded from: classes3.dex */
public class CloudServiceFailureException extends Exception {
    private int errorCode;

    public CloudServiceFailureException() {
    }

    public CloudServiceFailureException(String str) {
        super(str);
    }

    public CloudServiceFailureException(Throwable th) {
        super(th);
    }

    public CloudServiceFailureException(Throwable th, int i) {
        super(th);
        this.errorCode = i;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
