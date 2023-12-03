package com.xiaomi.accountsdk.utils;

import android.app.UiModeManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import java.util.Locale;

/* loaded from: classes2.dex */
public class XMPassportUtil {
    public static String buildUrlWithLocaleQueryParam(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        Uri parse = Uri.parse(str);
        String queryParameter = parse.getQueryParameter("_locale");
        Uri.Builder buildUpon = parse.buildUpon();
        String iSOLocaleString = getISOLocaleString(Locale.getDefault());
        if (TextUtils.isEmpty(queryParameter) && !TextUtils.isEmpty(iSOLocaleString)) {
            buildUpon.appendQueryParameter("_locale", iSOLocaleString);
        }
        return buildUpon.build().toString();
    }

    public static String buildUrlWithNightModeQueryParam(Context context, String str) {
        if (TextUtils.isEmpty(str) || context == null) {
            throw new IllegalArgumentException("params invalid");
        }
        String str2 = 2 == ((UiModeManager) context.getSystemService("uimode")).getNightMode() ? "night_yes" : "night_no";
        Uri.Builder buildUpon = Uri.parse(str).buildUpon();
        buildUpon.appendQueryParameter("_device_name", Build.DEVICE);
        buildUpon.appendQueryParameter("_uiThemeMode", str2);
        return buildUpon.build().toString();
    }

    public static String getISOLocaleString(Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        return TextUtils.isEmpty(country) ? language : String.format("%s_%s", language, country);
    }
}
