package com.xiaomi.account.auth;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.account.IXiaomiAuthResponse;
import com.xiaomi.account.IXiaomiAuthService;
import com.xiaomi.account.openauth.XMAuthericationException;
import miui.net.IXiaomiAuthService;

/* loaded from: classes2.dex */
public class XiaomiAuthService implements IXiaomiAuthService {
    private final String TAG = "XiaomiAuthService";
    private IXiaomiAuthService mAuthService;
    private miui.net.IXiaomiAuthService mMiuiV5AuthService;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XiaomiAuthService(IBinder iBinder) {
        try {
            this.mAuthService = IXiaomiAuthService.Stub.asInterface(iBinder);
        } catch (SecurityException unused) {
            this.mMiuiV5AuthService = IXiaomiAuthService.Stub.asInterface(iBinder);
        }
    }

    private boolean supportNativeOAuth() throws RemoteException {
        return getVersionNum() >= 1;
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        throw new IllegalStateException();
    }

    @Override // com.xiaomi.account.IXiaomiAuthService
    public void getAccessTokenInResponse(IXiaomiAuthResponse iXiaomiAuthResponse, Bundle bundle, int i, int i2) throws RemoteException {
        com.xiaomi.account.IXiaomiAuthService iXiaomiAuthService = this.mAuthService;
        if (iXiaomiAuthService != null) {
            iXiaomiAuthService.getAccessTokenInResponse(iXiaomiAuthResponse, bundle, i, i2);
        }
    }

    @Override // com.xiaomi.account.IXiaomiAuthService
    public int getVersionNum() throws RemoteException {
        com.xiaomi.account.IXiaomiAuthService iXiaomiAuthService = this.mAuthService;
        if (iXiaomiAuthService != null) {
            return iXiaomiAuthService.getVersionNum();
        }
        return 0;
    }

    @Override // com.xiaomi.account.IXiaomiAuthService
    public boolean isSupport(String str) throws RemoteException {
        com.xiaomi.account.IXiaomiAuthService iXiaomiAuthService = this.mAuthService;
        if (iXiaomiAuthService != null) {
            return iXiaomiAuthService.isSupport(str);
        }
        return false;
    }

    public void oauthInResponse(Context context, IXiaomiAuthResponse iXiaomiAuthResponse, OAuthConfig oAuthConfig) throws RemoteException, XMAuthericationException, FallBackWebOAuthException {
        Bundle makeOptions = oAuthConfig.makeOptions();
        makeOptions.putString("extra_client_id", String.valueOf(oAuthConfig.appId));
        makeOptions.putString("extra_redirect_uri", oAuthConfig.redirectUrl);
        if (!oAuthConfig.useSystemAccountLogin.booleanValue() && !isSupport("FEATURE_NOT_USE_SYSTEM_ACCOUNT_LOGIN")) {
            Log.e("XiaomiAuthService", "this version of miui only support system account login");
            throw new FallBackWebOAuthException();
        } else if (oAuthConfig.fastOAuth && !supportNativeOAuth()) {
            Log.e("XiaomiAuthService", "this version of miui not support fast Oauth");
            throw new FallBackWebOAuthException();
        } else {
            String str = oAuthConfig.deviceID;
            if (oAuthConfig.platform == 1 && !isSupport("FEATURE_SHUIDI")) {
                throw new FallBackWebOAuthException();
            }
            if (oAuthConfig.platform == 0 && !isSupport("FEATURE_DEV_DEVICEID") && !TextUtils.isEmpty(str)) {
                throw new FallBackWebOAuthException();
            }
            if (!supportResponseWay()) {
                throw new FallBackWebOAuthException();
            }
            this.mAuthService.getAccessTokenInResponse(iXiaomiAuthResponse, makeOptions, 1, 90);
        }
    }

    @Override // com.xiaomi.account.IXiaomiAuthService
    public boolean supportResponseWay() throws RemoteException {
        com.xiaomi.account.IXiaomiAuthService iXiaomiAuthService = this.mAuthService;
        if (iXiaomiAuthService != null) {
            return iXiaomiAuthService.supportResponseWay();
        }
        return false;
    }
}
