package com.android.settings.utils;

import android.app.Application;
import android.content.Context;
import android.pc.MiuiPcManager;
import android.util.MiuiFreeformUtil;

/* loaded from: classes2.dex */
public final class TabletUtils {
    public static boolean IS_TABLET;
    private static Context mContext;

    public static void attachApplication(Context context) {
        if (mContext == null && (context instanceof Application)) {
            mContext = context;
            if ((context.getResources().getConfiguration().screenLayout & 15) == 3) {
                changeDeviceForm(1);
            } else {
                changeDeviceForm(0);
            }
        }
    }

    public static void changeDeviceForm(int i) {
        if (mContext == null || i < -1 || i > 1 || !MiuiFreeformUtil.PC_ENABLED) {
            return;
        }
        MiuiPcManager.getInstance().isOnPcMode();
    }
}
