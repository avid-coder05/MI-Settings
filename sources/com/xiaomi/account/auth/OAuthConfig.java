package com.xiaomi.account.auth;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.xiaomi.account.openauth.AccountAuth;
import com.xiaomi.account.openauth.AuthorizeActivity;
import com.xiaomi.account.openauth.AuthorizeActivityBase;
import miui.yellowpage.Tag;

/* loaded from: classes2.dex */
public class OAuthConfig {
    final AccountAuth accountAuth;
    final String appId;
    final Class<? extends AuthorizeActivityBase> authorizeActivityClazz;
    final Context context;
    final String deviceID;
    final String display;
    final boolean fastOAuth;
    final Boolean hideSwitch;
    final boolean keepCookies;
    final String loginType;
    final boolean notUseMiui;
    final PhoneInfo phoneInfo;
    final int platform;
    final String redirectUrl;
    final String responseType;
    final String scopes;
    final Boolean skipConfirm;
    final String state;
    final Boolean useSystemAccountLogin;
    final Boolean useSystemBrowserLogin;

    /* loaded from: classes2.dex */
    public static class Builder {
        private static final Class<? extends AuthorizeActivityBase> DEFAULT_AUTHORIZE_ACTIVITY_CLASS = AuthorizeActivity.class;
        private AccountAuth accountAuth;
        private String appId;
        private Class<? extends AuthorizeActivityBase> authorizeActivityClazz;
        private Context context;
        private String deviceID;
        private String display;
        private boolean fastOAuth;
        private Boolean hideSwitch;
        private boolean keepCookies;
        private String loginType;
        private boolean notUseMiui;
        private PhoneInfo phoneInfo;
        private int platform;
        private String redirectUrl;
        private String responseType;
        private int[] scopes;
        private Boolean skipConfirm;
        private String state;
        private Boolean useSystemAccountLogin;
        private Boolean useSystemBrowserLogin;

        public Builder() {
            this.notUseMiui = false;
            this.scopes = null;
            this.appId = null;
            this.redirectUrl = null;
            Boolean bool = Boolean.FALSE;
            this.skipConfirm = bool;
            this.state = null;
            this.keepCookies = false;
            this.authorizeActivityClazz = DEFAULT_AUTHORIZE_ACTIVITY_CLASS;
            this.platform = 0;
            this.deviceID = null;
            this.responseType = Tag.TagWebService.CommonResult.RESULT_CODE;
            this.fastOAuth = false;
            this.useSystemAccountLogin = Boolean.TRUE;
            this.useSystemBrowserLogin = bool;
        }

        public Builder(Builder builder) {
            this.notUseMiui = false;
            this.scopes = null;
            this.appId = null;
            this.redirectUrl = null;
            Boolean bool = Boolean.FALSE;
            this.skipConfirm = bool;
            this.state = null;
            this.keepCookies = false;
            this.authorizeActivityClazz = DEFAULT_AUTHORIZE_ACTIVITY_CLASS;
            this.platform = 0;
            this.deviceID = null;
            this.responseType = Tag.TagWebService.CommonResult.RESULT_CODE;
            this.fastOAuth = false;
            this.useSystemAccountLogin = Boolean.TRUE;
            this.useSystemBrowserLogin = bool;
            this.notUseMiui = builder.notUseMiui;
            this.scopes = builder.scopes;
            this.appId = builder.appId;
            this.redirectUrl = builder.redirectUrl;
            this.skipConfirm = builder.skipConfirm;
            this.state = builder.state;
            this.keepCookies = builder.keepCookies;
            this.authorizeActivityClazz = builder.authorizeActivityClazz;
            this.accountAuth = builder.accountAuth;
            this.platform = builder.platform;
            this.phoneInfo = builder.phoneInfo;
            this.deviceID = builder.deviceID;
            this.responseType = builder.responseType;
            this.fastOAuth = builder.fastOAuth;
            this.context = builder.context;
            this.loginType = builder.loginType;
            this.display = builder.display;
            this.hideSwitch = builder.hideSwitch;
            this.useSystemAccountLogin = builder.useSystemAccountLogin;
            this.useSystemBrowserLogin = builder.useSystemBrowserLogin;
        }

        public Builder appId(long j) {
            this.appId = String.valueOf(j);
            return this;
        }

        public OAuthConfig build() {
            return new OAuthConfig(this);
        }

        public Builder context(Context context) {
            this.context = context.getApplicationContext();
            return this;
        }

        public Context getContext() {
            return this.context;
        }

        public Builder redirectUrl(String str) {
            this.redirectUrl = str;
            return this;
        }

        public Builder responseType(String str) {
            this.responseType = str;
            return this;
        }

        public Builder skipConfirm(boolean z) {
            this.skipConfirm = Boolean.valueOf(z);
            return this;
        }
    }

    public OAuthConfig(Builder builder) {
        this.scopes = makeScopeString(builder.scopes);
        this.notUseMiui = builder.notUseMiui;
        this.appId = builder.appId;
        this.redirectUrl = builder.redirectUrl;
        this.skipConfirm = builder.skipConfirm;
        this.state = builder.state;
        this.keepCookies = builder.keepCookies;
        this.authorizeActivityClazz = builder.authorizeActivityClazz;
        this.accountAuth = builder.accountAuth;
        this.platform = builder.platform;
        this.deviceID = builder.deviceID;
        this.display = builder.display;
        this.responseType = builder.responseType;
        this.phoneInfo = builder.phoneInfo;
        this.fastOAuth = builder.fastOAuth;
        this.context = builder.context;
        this.loginType = builder.loginType;
        this.hideSwitch = builder.hideSwitch;
        this.useSystemAccountLogin = builder.useSystemAccountLogin;
        this.useSystemBrowserLogin = builder.useSystemBrowserLogin;
    }

    private static String makeScopeString(int[] iArr) {
        if (iArr == null || iArr.length <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int length = iArr.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            int i3 = iArr[i];
            int i4 = i2 + 1;
            if (i2 > 0) {
                sb.append(' ');
            }
            sb.append(i3);
            i++;
            i2 = i4;
        }
        return sb.toString();
    }

    public Bundle makeOptions() {
        Bundle bundle = new Bundle();
        bundle.putString("extra_response_type", this.responseType);
        Boolean bool = this.skipConfirm;
        if (bool != null) {
            bundle.putBoolean("extra_skip_confirm", bool.booleanValue());
        }
        if (!TextUtils.isEmpty(this.state)) {
            bundle.putString("extra_state", this.state);
        }
        if (!TextUtils.isEmpty(this.scopes)) {
            bundle.putString("extra_scope", this.scopes);
        }
        if (!TextUtils.isEmpty(this.deviceID)) {
            bundle.putString("extra_deviceid", this.deviceID);
        }
        if (!TextUtils.isEmpty(this.display)) {
            bundle.putString("extra_display", this.display);
        }
        bundle.putInt("extra_platform", this.platform);
        bundle.putBoolean("extra_native_oauth", this.fastOAuth);
        Boolean bool2 = this.hideSwitch;
        if (bool2 != null) {
            bundle.putBoolean("extra_hide_switch", bool2.booleanValue());
        }
        Boolean bool3 = this.useSystemAccountLogin;
        if (bool3 != null) {
            bundle.putBoolean("extra_use_system_account_login", bool3.booleanValue());
        }
        Boolean bool4 = this.useSystemBrowserLogin;
        if (bool4 != null) {
            bundle.putBoolean("extra_use_system_browser_login", bool4.booleanValue());
        }
        if (!TextUtils.isEmpty(this.loginType)) {
            bundle.putString("_loginType", this.loginType);
        }
        return bundle;
    }
}
