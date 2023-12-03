package com.xiaomi.opensdk.exception;

import android.text.TextUtils;

/* loaded from: classes2.dex */
public class AuthenticationException extends Exception {
    private static final long serialVersionUID = 1;

    public AuthenticationException() {
    }

    public AuthenticationException(String str) {
        super(str);
    }

    @Override // java.lang.Throwable
    public String toString() {
        if (TextUtils.isEmpty(getMessage())) {
            return AuthenticationException.class.getSimpleName() + ": 鉴权失败";
        }
        return AuthenticationException.class.getSimpleName() + ": 鉴权失败, " + getMessage();
    }
}
