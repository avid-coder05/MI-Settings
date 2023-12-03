package com.xiaomi.accountsdk.request;

import com.xiaomi.accountsdk.account.exception.HttpException;

/* loaded from: classes2.dex */
public class AuthenticationFailureException extends HttpException {
    private static final long serialVersionUID = 1933476556350874440L;
    private String caDisableSecondsHeader;
    private String wwwAuthenticateHeader;

    public AuthenticationFailureException(int i, String str) {
        super(i, str);
        this.wwwAuthenticateHeader = null;
        this.caDisableSecondsHeader = null;
    }

    public AuthenticationFailureException(String str) {
        super(0, str);
        this.wwwAuthenticateHeader = null;
        this.caDisableSecondsHeader = null;
    }

    public void setCaDisableSecondsHeader(String str) {
        this.caDisableSecondsHeader = str;
    }

    public void setWwwAuthenticateHeader(String str) {
        this.wwwAuthenticateHeader = str;
    }
}
