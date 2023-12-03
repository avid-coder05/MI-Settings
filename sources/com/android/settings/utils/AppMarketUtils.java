package com.android.settings.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/* loaded from: classes2.dex */
public class AppMarketUtils {
    public static String getMarketPkgName(PackageManager packageManager) {
        return isExistXiaomiMarket(packageManager) ? "com.xiaomi.market" : "";
    }

    private static Intent getOtherMarketIntent(String str, String str2) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str2));
        intent.setPackage(str);
        intent.addFlags(268435456);
        return intent;
    }

    private static Intent getSamsungMarketIntent(String str) {
        Uri parse = Uri.parse(String.format("http://www.samsungapps.com/appquery/appDetail.as?appId=%S", str));
        Intent intent = new Intent();
        intent.setData(parse);
        intent.setClassName("com.sec.android.app.samsungapps", "com.sec.android.app.samsungapps.Main");
        return intent;
    }

    private static Intent getXiaomiMarketIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://comments?id=" + str));
        intent.setPackage("com.xiaomi.market");
        intent.addFlags(268435456);
        return intent;
    }

    private static boolean isExistXiaomiMarket(PackageManager packageManager) {
        return isPkgExist(packageManager, "com.xiaomi.market");
    }

    public static boolean isPkgExist(PackageManager packageManager, String str) {
        try {
            packageManager.getPackageInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static void toMarket(Context context, String str, String str2, String str3) {
        Intent xiaomiMarketIntent = "com.xiaomi.market".equals(str) ? getXiaomiMarketIntent(str2) : "com.sec.android.app.samsungapps".equals(str) ? getSamsungMarketIntent(str2) : getOtherMarketIntent(str, str2);
        if (xiaomiMarketIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(xiaomiMarketIntent);
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str3));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
