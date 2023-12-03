package miui.payment.exception;

import android.os.Bundle;

/* loaded from: classes3.dex */
public class PaymentServiceFailureException extends Exception {
    private static final long serialVersionUID = 1;
    private int mErrorCode;
    private Bundle mErrorResult;

    public PaymentServiceFailureException(int i, String str) {
        this(i, str, null);
    }

    public PaymentServiceFailureException(int i, String str, Bundle bundle) {
        super(str);
        this.mErrorCode = i;
        if (bundle == null) {
            this.mErrorResult = new Bundle();
        } else {
            this.mErrorResult = bundle;
        }
    }

    public int getError() {
        return this.mErrorCode;
    }

    public Bundle getErrorResult() {
        return this.mErrorResult;
    }
}
