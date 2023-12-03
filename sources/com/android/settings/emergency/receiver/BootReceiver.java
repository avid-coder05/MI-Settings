package com.android.settings.emergency.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.settings.emergency.util.Config;
import miui.process.ProcessManager;

/* loaded from: classes.dex */
public class BootReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Config.setInSosModeState(context, false);
        if (Config.isLockedApplication(context)) {
            return;
        }
        ProcessManager.updateApplicationLockedState(context.getPackageName(), 0, false);
    }
}
