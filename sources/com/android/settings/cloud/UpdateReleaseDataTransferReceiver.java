package com.android.settings.cloud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import miui.settings.commonlib.MemoryOptimizationUtil;

/* loaded from: classes.dex */
public class UpdateReleaseDataTransferReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        Log.i("htmlviewercloudcontrol", "settings app : UpdateReleaseDataTransferReceiver onReceive : " + intent.getAction());
        Intent intent2 = new Intent(intent.getAction());
        intent2.setPackage(MemoryOptimizationUtil.CONTROLLER_PKG);
        context.sendBroadcast(intent2);
    }
}
