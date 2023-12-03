package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

/* loaded from: classes.dex */
public class HubReceiver extends BroadcastReceiver {
    private void resoveIntent(Context context, Intent intent) {
        if ("miui.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
            Log.v("HubReceiver", "airplane mode changed");
            setAirplaneMode(context, intent.getBooleanExtra("state", false));
        }
    }

    private void setAirplaneMode(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "airplane_mode_on", z ? 1 : 0);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.putExtra("state", z);
        context.sendBroadcast(intent);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        resoveIntent(context, intent);
    }
}
