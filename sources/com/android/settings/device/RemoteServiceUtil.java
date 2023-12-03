package com.android.settings.device;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import miui.settings.commonlib.MemoryOptimizationUtil;

/* loaded from: classes.dex */
public class RemoteServiceUtil {
    public static boolean bindRemoteService(Context context, ServiceConnection serviceConnection) {
        Intent intent = new Intent("com.android.settings.GET_DEVICE_INFO_SERVICE");
        intent.setPackage(MemoryOptimizationUtil.CONTROLLER_PKG);
        return context.bindService(intent, serviceConnection, 1);
    }

    public static void unBindRemoteService(Context context, ServiceConnection serviceConnection) {
        if (serviceConnection == null || context == null) {
            return;
        }
        context.unbindService(serviceConnection);
    }
}
