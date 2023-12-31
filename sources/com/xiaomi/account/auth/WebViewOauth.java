package com.xiaomi.account.auth;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.xiaomi.account.IXiaomiAuthResponse;
import com.xiaomi.account.XiaomiOAuthResponse;
import com.xiaomi.account.http.HttpClient;
import com.xiaomi.account.http.Request;
import com.xiaomi.account.http.Response;
import com.xiaomi.account.http.UrlConnHttpFactory;
import com.xiaomi.account.openauth.AccountAuth;
import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.utils.OAuthUrlPaser;
import com.xiaomi.accountsdk.diagnosis.DiagnosisLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/* loaded from: classes2.dex */
public class WebViewOauth implements XiaomiOAuth {
    private static final String AUTHORIZE_PATH = XiaomiOAuthConstants.OAUTH2_HOST + "/oauth2/authorize";
    private String mAppId;
    private Context mContext;
    private String mRedirectUrl;

    public WebViewOauth(Context context, String str, String str2) {
        this.mContext = context;
        this.mAppId = str;
        this.mRedirectUrl = str2;
    }

    private void addLocaleIfNeeded(Bundle bundle) {
        if (bundle == null || bundle.containsKey("_locale")) {
            return;
        }
        String localeString = getLocaleString(Locale.getDefault());
        if (TextUtils.isEmpty(localeString)) {
            return;
        }
        bundle.putString("_locale", localeString);
    }

    private int getDefaultSmsSlotId() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
                List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoList();
                if (activeSubscriptionInfoList == null) {
                    return 0;
                }
                for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                    if (subscriptionInfo.getSubscriptionId() == defaultDataSubscriptionId) {
                        return subscriptionInfo.getSimSlotIndex();
                    }
                }
                return 0;
            } catch (SecurityException e) {
                Log.e("WebViewOauth", e.toString());
                return 0;
            }
        }
        return 0;
    }

    private Intent getIntent(Activity activity, OAuthConfig oAuthConfig, IXiaomiAuthResponse iXiaomiAuthResponse) {
        Intent intent = new Intent(activity, oAuthConfig.authorizeActivityClazz);
        intent.putExtra("url", getUrl(oAuthConfig));
        intent.putExtra("redirect_uri", this.mRedirectUrl);
        intent.putExtra("extra_keep_cookies ", oAuthConfig.keepCookies);
        intent.putExtra("extra_response", new XiaomiOAuthResponse(iXiaomiAuthResponse));
        AccountAuth accountAuth = oAuthConfig.accountAuth;
        if (accountAuth != null) {
            HashMap hashMap = new HashMap();
            hashMap.put("userId", accountAuth.getUserId());
            hashMap.put("serviceToken", accountAuth.getServiceToken());
            intent.putExtra("userid", "userId=" + accountAuth.getUserId());
            intent.putExtra("serviceToken", "serviceToken=" + accountAuth.getServiceToken());
        }
        PhoneInfo phoneInfo = oAuthConfig.phoneInfo;
        if (phoneInfo != null) {
            intent.putExtras(phoneInfo.blokingGetPhoneInfo(getDefaultSmsSlotId()));
        }
        return intent;
    }

    private static String getLocaleString(Locale locale) {
        if (locale == null) {
            return null;
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        return TextUtils.isEmpty(country) ? language : String.format("%s_%s", language, country);
    }

    private String getUrl(OAuthConfig oAuthConfig) {
        Bundle bundle = new Bundle();
        bundle.putString("client_id", this.mAppId);
        bundle.putString("redirect_uri", this.mRedirectUrl);
        bundle.putString("response_type", oAuthConfig.responseType);
        bundle.putString("scope", oAuthConfig.scopes);
        bundle.putString("state", oAuthConfig.state);
        Boolean bool = oAuthConfig.skipConfirm;
        if (bool != null) {
            bundle.putString("skip_confirm", String.valueOf(bool));
        }
        String str = oAuthConfig.loginType;
        if (str != null) {
            bundle.putString("_loginType", str);
        }
        if (oAuthConfig.hideSwitch != null) {
            bundle.putString("_hideSwitch", "true");
        }
        bundle.putString("pt", "" + oAuthConfig.platform);
        bundle.putString("device_id", oAuthConfig.deviceID);
        bundle.putString("display", oAuthConfig.display);
        addLocaleIfNeeded(bundle);
        return AUTHORIZE_PATH + "?" + parseBundle(bundle);
    }

    protected static String joinMap(Map<String, String> map, String str) {
        if (map == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (i > 0) {
                sb.append(str);
            }
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key);
            sb.append("=");
            sb.append(value);
            i++;
        }
        return sb.toString();
    }

    private String parseBundle(Bundle bundle) {
        if (bundle == null) {
            return "";
        }
        ArrayList arrayList = new ArrayList();
        for (String str : bundle.keySet()) {
            String string = bundle.getString(str);
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(string)) {
                arrayList.add(new BasicNameValuePair(str, string));
            }
        }
        return URLEncodedUtils.format(arrayList, "UTF-8");
    }

    private XiaomiOAuthResults quietOAuth(OAuthConfig oAuthConfig) throws AuthenticatorException, IOException, XMAuthericationException {
        Log.i("WebViewOauth", "WebViewOauth quietOAuth start...");
        AccountAuth accountAuth = oAuthConfig.accountAuth;
        if (accountAuth == null) {
            Log.i("WebViewOauth", "WebViewOauth..quietOAuth..accountAuth is null");
            throw new AuthenticatorException();
        }
        for (int i = 0; i < 2; i++) {
            HttpClient createHttpClient = new UrlConnHttpFactory().createHttpClient();
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            hashMap2.put("userId", accountAuth.getUserId());
            hashMap2.put("serviceToken", accountAuth.getServiceToken());
            hashMap.put("Cookie", joinMap(hashMap2, "; "));
            hashMap.put("User-Agent", (System.getProperty("http.agent") + " Passport/OAuthSDK/2.0.10") + " mi/OAuthSDK/VersionCode/90");
            Response excute = createHttpClient.excute(new Request.Builder().url(getUrl(oAuthConfig)).followRedirects(false).headers(hashMap).build());
            DiagnosisLog.get().log("quietOAuth.response.location=" + excute.location);
            String str = excute.location;
            if (str == null) {
                throw new AuthenticatorException();
            }
            if (str.startsWith(this.mRedirectUrl)) {
                Bundle parse = OAuthUrlPaser.parse(excute.location);
                if (parse != null) {
                    Log.i("WebViewOauth", "WebViewOauth.quietOAuth.sucess");
                    return XiaomiOAuthResults.parseBundle(parse);
                }
                Log.e("WebViewOauth", "location is null need user to Authorization");
                throw new XMAuthericationException("parse url fail:" + excute.location);
            }
            accountAuth.invalideServiceToken();
        }
        throw new AuthenticatorException();
    }

    @Override // com.xiaomi.account.auth.XiaomiOAuth
    public XiaomiOAuthResults startOAuth(Activity activity, OAuthConfig oAuthConfig) throws AuthenticatorException, IOException, XMAuthericationException, InterruptedException, OperationCanceledException {
        if (oAuthConfig.useSystemBrowserLogin.booleanValue()) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(getUrl(oAuthConfig)));
            intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
            intent.addFlags(268435456);
            activity.startActivity(intent);
            return null;
        }
        try {
            return quietOAuth(oAuthConfig);
        } catch (AuthenticatorException unused) {
            Log.e("WebViewOauth", "quietOAuth failed");
            if (activity == null || activity.isFinishing()) {
                Log.e("WebViewOauth", "activity is null");
                Bundle bundle = new Bundle();
                bundle.putInt("extra_error_code", -1001);
                bundle.putString("extra_error_description", "activity is null");
                return XiaomiOAuthResults.parseBundle(bundle);
            }
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final ArrayList arrayList = new ArrayList();
            activity.startActivity(this.getIntent(activity, oAuthConfig, new IXiaomiAuthResponse.Stub() { // from class: com.xiaomi.account.auth.WebViewOauth.1
                @Override // com.xiaomi.account.IXiaomiAuthResponse
                public void onCancel() throws RemoteException {
                    countDownLatch.countDown();
                }

                @Override // com.xiaomi.account.IXiaomiAuthResponse
                public void onResult(Bundle bundle2) throws RemoteException {
                    arrayList.add(XiaomiOAuthResults.parseBundle(bundle2));
                    countDownLatch.countDown();
                }
            }));
            countDownLatch.await();
            if (arrayList.size() > 0) {
                return (XiaomiOAuthResults) arrayList.get(0);
            }
            throw new OperationCanceledException();
        }
    }
}
