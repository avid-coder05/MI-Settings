package com.xiaomi.accountsdk.account.exception;

/* loaded from: classes2.dex */
public class HttpException extends Exception {
    public boolean isStsUrlRequestError;
    public final int responseCode;
    public String serviceId;

    public HttpException(int i, String str) {
        super(str);
        this.isStsUrlRequestError = false;
        this.responseCode = i;
    }

    @Override // java.lang.Throwable
    public String toString() {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append("response code: ");
        sb.append(this.responseCode);
        sb.append("\n");
        if (this.isStsUrlRequestError) {
            str = this.serviceId + " sts url request error \n";
        } else {
            str = "";
        }
        sb.append(str);
        return sb.toString() + super.toString();
    }
}
