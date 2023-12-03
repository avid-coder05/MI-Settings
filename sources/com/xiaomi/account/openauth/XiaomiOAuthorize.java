package com.xiaomi.account.openauth;

import android.app.Activity;
import android.os.AsyncTask;
import com.xiaomi.account.auth.OAuthConfig;
import com.xiaomi.account.auth.OAuthFactory;
import com.xiaomi.account.auth.XiaomiOAuthFutureImpl;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import miui.content.ExtraIntent;

/* loaded from: classes2.dex */
public class XiaomiOAuthorize {
    private static final Class<? extends AuthorizeActivityBase> DEFAULT_AUTHORIZE_ACTIVITY_CLASS = AuthorizeActivity.class;
    private OAuthConfig.Builder mConfigBuilder = new OAuthConfig.Builder();

    private XiaomiOAuthFuture<XiaomiOAuthResults> oauth(Activity activity) {
        if (this.mConfigBuilder.getContext() == null) {
            if (activity == null) {
                throw new IllegalArgumentException("please set Context or Activity!!!");
            }
            this.mConfigBuilder.context(activity.getApplicationContext());
        }
        final WeakReference weakReference = new WeakReference(activity);
        XiaomiOAuthFutureImpl xiaomiOAuthFutureImpl = new XiaomiOAuthFutureImpl(new Callable<XiaomiOAuthResults>() { // from class: com.xiaomi.account.openauth.XiaomiOAuthorize.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public XiaomiOAuthResults call() throws Exception {
                OAuthConfig build = new OAuthConfig.Builder(XiaomiOAuthorize.this.mConfigBuilder).build();
                return OAuthFactory.createOAuth(build).startOAuth((Activity) weakReference.get(), build);
            }
        });
        AsyncTask.THREAD_POOL_EXECUTOR.execute(xiaomiOAuthFutureImpl);
        return xiaomiOAuthFutureImpl;
    }

    public XiaomiOAuthorize setAppId(long j) {
        this.mConfigBuilder.appId(j);
        return this;
    }

    public XiaomiOAuthorize setRedirectUrl(String str) {
        this.mConfigBuilder.redirectUrl(str);
        return this;
    }

    public XiaomiOAuthorize setSkipConfirm(boolean z) {
        this.mConfigBuilder.skipConfirm(z);
        return this;
    }

    public XiaomiOAuthFuture<XiaomiOAuthResults> startGetAccessToken(Activity activity) {
        this.mConfigBuilder.responseType(ExtraIntent.XIAOMI_KEY_AUTHTOKEN);
        return oauth(activity);
    }
}
