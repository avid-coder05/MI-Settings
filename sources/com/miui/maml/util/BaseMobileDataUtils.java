package com.miui.maml.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

/* loaded from: classes2.dex */
public class BaseMobileDataUtils {
    public Uri getMobileDataUri() {
        return Settings.Global.getUriFor("mobile_data");
    }

    public boolean isMobileEnable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            return ((Boolean) ReflectionHelper.invokeObject(connectivityManager.getClass(), connectivityManager, "getMobileDataEnabled", new Class[0], new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("BaseMobileDataUtils", "Invoke | ConnectivityManager_getMobileDataEnabled() occur EXCEPTION: " + e.getMessage());
            return false;
        }
    }
}
