package com.android.settings.wifi.openwifi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import java.util.List;
import miui.os.Build;

/* loaded from: classes2.dex */
public class Utils {
    private static final String[] BROWSER_PKG_ARRAY = {"com.mi.globalbrowser", "com.android.browser"};

    public static Intent getDefaultBrowserPkgIntent(Context context, Intent intent) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            boolean z = false;
            for (String str : BROWSER_PKG_ARRAY) {
                intent.setPackage(str);
                z = isBrowserPkgIntentExist(context, intent);
                if (z) {
                    break;
                }
            }
            if (!z) {
                intent.setPackage("com.android.chrome");
                if (!isBrowserPkgIntentExist(context, intent)) {
                    intent.setPackage(null);
                }
            }
        } else {
            intent.setPackage("com.android.browser");
        }
        return intent;
    }

    public static boolean isBrowserPkgIntentExist(Context context, Intent intent) {
        try {
            List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 32);
            if (queryIntentActivities != null) {
                return queryIntentActivities.size() > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
