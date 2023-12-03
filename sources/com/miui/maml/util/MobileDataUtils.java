package com.miui.maml.util;

import android.content.Context;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.util.Log;

/* loaded from: classes2.dex */
public class MobileDataUtils extends BaseMobileDataUtils {
    public static MobileDataUtils getInstance() {
        try {
            return (MobileDataUtils) Class.forName("miui.msim.util.MSimMobileDataUtils").newInstance();
        } catch (Exception unused) {
            return new MobileDataUtils();
        }
    }

    public void enableMobileData(Context context, boolean z) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            ReflectionHelper.invoke(connectivityManager.getClass(), connectivityManager, "setMobileDataEnabled", new Class[]{Boolean.TYPE}, Boolean.FALSE);
        } catch (Exception e) {
            Log.e("BaseMobileDataUtils", "Invoke | ConnectivityManager_enableMobileData() occur EXCEPTION: " + e.getMessage());
        }
    }

    public void registerContentObserver(Context context, ContentObserver contentObserver) {
        context.getContentResolver().registerContentObserver(getMobileDataUri(), false, contentObserver);
    }
}
