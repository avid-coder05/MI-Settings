package com.xiaomi.accountsdk.account.data;

import android.text.TextUtils;

/* loaded from: classes2.dex */
public final class ExtendedAuthToken {
    public final String authToken;
    public final String security;

    private ExtendedAuthToken(String str, String str2) {
        this.authToken = str;
        this.security = str2;
    }

    public static ExtendedAuthToken parse(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] split = str.split(",");
        if (split.length != 2 || TextUtils.isEmpty(split[0]) || TextUtils.isEmpty(split[1])) {
            return null;
        }
        return new ExtendedAuthToken(split[0], split[1]);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || ExtendedAuthToken.class != obj.getClass()) {
            return false;
        }
        ExtendedAuthToken extendedAuthToken = (ExtendedAuthToken) obj;
        String str = this.authToken;
        if (str == null ? extendedAuthToken.authToken == null : str.equals(extendedAuthToken.authToken)) {
            String str2 = this.security;
            return str2 == null ? extendedAuthToken.security == null : str2.equals(extendedAuthToken.security);
        }
        return false;
    }

    public int hashCode() {
        String str = this.authToken;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.security;
        return hashCode + (str2 != null ? str2.hashCode() : 0);
    }
}
