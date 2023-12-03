package com.xiaomi.accountsdk.utils;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

/* loaded from: classes2.dex */
public class UIUtils {
    public static void adaptForceDarkInApi29(Context context, WebView webView) {
        if (webView == null) {
            throw new IllegalArgumentException("param webView shouldn't be null!");
        }
        WebSettings settings = webView.getSettings();
        if (Build.VERSION.SDK_INT >= 29) {
            if (isSystemNightMode(context)) {
                setWebSettingsForceDark(settings, 2);
            } else {
                setWebSettingsForceDark(settings, 1);
            }
        }
    }

    public static boolean isSystemNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }

    private static void setWebSettingsForceDark(WebSettings webSettings, int i) {
        try {
            webSettings.getClass().getMethod("setForceDark", Integer.TYPE).invoke(webSettings, Integer.valueOf(i));
        } catch (Exception e) {
            AccountLog.e("UIUtils", "setWebSettingsForceDark reflect error", e);
        }
    }
}
