package com.xiaomi.account.auth;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import com.xiaomi.account.http.Request;
import com.xiaomi.account.http.UrlConnHttpFactory;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import java.util.ArrayList;
import java.util.List;
import miui.accounts.ExtraAccountManager;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes2.dex */
class OAuthServiceManager {
    private static final String ORDER_APP_URL = XiaomiOAuthConstants.OAUTH2_API_URL_BASE + "/extra/appOrder";
    private Context mContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OAuthServiceManager(Context context) {
        this.mContext = context;
    }

    private List<OrderApp> blockGetOrderApps() {
        try {
            JSONObject jSONObject = new JSONObject(new UrlConnHttpFactory().createHttpClient().excute(new Request.Builder().url(ORDER_APP_URL).appendQuery("platform=android").followRedirects(false).build()).body);
            if (jSONObject.getInt(Tag.TagWebService.CommonResult.RESULT_CODE) == 0) {
                JSONArray optJSONArray = jSONObject.getJSONObject("data").optJSONArray("order");
                ArrayList arrayList = new ArrayList();
                int length = optJSONArray != null ? optJSONArray.length() : 0;
                for (int i = 0; i < length; i++) {
                    JSONObject jSONObject2 = (JSONObject) optJSONArray.get(i);
                    String optString = jSONObject2.optString("name");
                    int optInt = jSONObject2.optInt("version");
                    OrderApp orderApp = new OrderApp();
                    orderApp.setName(optString);
                    orderApp.setVersion(optInt);
                    arrayList.add(orderApp);
                }
                return arrayList;
            }
            return null;
        } catch (Exception e) {
            Log.e("OAuthServiceManager", e.toString());
            return null;
        }
    }

    private static Intent getMiuiSupportService(Context context) {
        Intent intent = new Intent("miui.intent.action.XIAOMI_ACCOUNT_AUTHORIZE");
        intent.setPackage(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(intent, 0);
        if (queryIntentServices == null || queryIntentServices.size() <= 0) {
            Intent intent2 = new Intent("android.intent.action.XIAOMI_ACCOUNT_AUTHORIZE");
            intent2.setPackage(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
            List<ResolveInfo> queryIntentServices2 = packageManager.queryIntentServices(intent2, 0);
            if (queryIntentServices2 == null || queryIntentServices2.size() <= 0) {
                return null;
            }
            return intent2;
        }
        return intent;
    }

    private Intent getSupportOAuthService(List<OrderApp> list) {
        PackageInfo packageInfo;
        if (list != null && list.size() != 0) {
            for (OrderApp orderApp : list) {
                String name = orderApp.getName();
                int version = orderApp.getVersion();
                PackageManager packageManager = this.mContext.getPackageManager();
                try {
                    packageInfo = packageManager.getPackageInfo(name, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    packageInfo = null;
                }
                if (version <= (packageInfo != null ? packageInfo.versionCode : 0)) {
                    Intent intent = new Intent("miui.intent.action.XIAOMI_ACCOUNT_AUTHORIZE");
                    intent.setPackage(name);
                    List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(intent, 0);
                    if (queryIntentServices != null && queryIntentServices.size() > 0) {
                        return intent;
                    }
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Intent blockGetDefaultIntent() {
        Intent miuiSupportService = getMiuiSupportService(this.mContext);
        if (miuiSupportService != null) {
            return miuiSupportService;
        }
        Intent supportOAuthService = getSupportOAuthService(blockGetOrderApps());
        if (supportOAuthService != null) {
            return supportOAuthService;
        }
        return null;
    }
}
