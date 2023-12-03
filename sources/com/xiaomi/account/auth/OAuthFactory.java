package com.xiaomi.account.auth;

import android.text.TextUtils;

/* loaded from: classes2.dex */
public class OAuthFactory {
    private static void checkOauthParams(OAuthConfig oAuthConfig) {
        if (oAuthConfig.context == null) {
            throw new IllegalArgumentException("not set Context or Activity!!!");
        }
        if (TextUtils.isEmpty(oAuthConfig.appId)) {
            throw new IllegalArgumentException("client id is error!!!");
        }
        if (TextUtils.isEmpty(oAuthConfig.redirectUrl)) {
            throw new IllegalArgumentException("redirect url is empty!!!");
        }
    }

    public static XiaomiOAuth createOAuth(OAuthConfig oAuthConfig) {
        checkOauthParams(oAuthConfig);
        return oAuthConfig.notUseMiui ? new WebViewOauth(oAuthConfig.context, oAuthConfig.appId, oAuthConfig.redirectUrl) : new MiuiOauth(oAuthConfig.context, oAuthConfig.appId, oAuthConfig.redirectUrl);
    }
}
