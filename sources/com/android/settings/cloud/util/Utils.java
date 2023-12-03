package com.android.settings.cloud.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import java.io.File;

/* loaded from: classes.dex */
public class Utils {
    public static boolean DEBUG = new File("/data/system/cloudsettings_staging").exists();

    public static boolean isConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showShortToast(Context context, int i) {
        Toast.makeText(context.getApplicationContext(), i, 0).show();
    }
}
