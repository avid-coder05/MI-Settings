package com.android.settings.webview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.webkit.UserPackage;
import android.webkit.WebViewFactory;
import android.webkit.WebViewProviderInfo;
import android.widget.Toast;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class WebViewUpdateServiceWrapper {
    public PackageInfo getCurrentWebViewPackage() {
        try {
            return WebViewFactory.getUpdateService().getCurrentWebViewPackage();
        } catch (RemoteException e) {
            Log.e("WVUSWrapper", e.toString());
            return null;
        }
    }

    public List<UserPackage> getPackageInfosAllUsers(Context context, String str) {
        return UserPackage.getPackageInfosAllUsers(context, str, (int) MiuiWindowManager$LayoutParams.EXTRA_FLAG_ACQUIRES_SLEEP_TOKEN);
    }

    public List<ApplicationInfo> getValidWebViewApplicationInfos(Context context) {
        WebViewProviderInfo[] webViewProviderInfoArr;
        try {
            webViewProviderInfoArr = WebViewFactory.getUpdateService().getValidWebViewPackages();
        } catch (RemoteException unused) {
            webViewProviderInfoArr = null;
        }
        ArrayList arrayList = new ArrayList();
        for (WebViewProviderInfo webViewProviderInfo : webViewProviderInfoArr) {
            try {
                arrayList.add(context.getPackageManager().getApplicationInfo(webViewProviderInfo.packageName, MiuiWindowManager$LayoutParams.EXTRA_FLAG_ACQUIRES_SLEEP_TOKEN));
            } catch (PackageManager.NameNotFoundException unused2) {
            }
        }
        return arrayList;
    }

    public boolean setWebViewProvider(String str) {
        try {
            return str.equals(WebViewFactory.getUpdateService().changeProviderAndSetting(str));
        } catch (RemoteException e) {
            Log.e("WVUSWrapper", "RemoteException when trying to change provider to " + str, e);
            return false;
        }
    }

    public void showInvalidChoiceToast(Context context) {
        Toast.makeText(context, R.string.select_webview_provider_toast_text, 0).show();
    }
}
