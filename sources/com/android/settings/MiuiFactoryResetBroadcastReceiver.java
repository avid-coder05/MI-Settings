package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class MiuiFactoryResetBroadcastReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String host = intent.getData().getHost();
        if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {
            Log.d("MiuiFactoryResetBroadcastReceiver", "Recived the secret code:" + host);
            if ("1217".equals(host)) {
                Intent intent2 = new Intent(context, MiuiFactoryResetReceived.class);
                intent2.addFlags(268435456);
                context.startActivity(intent2);
            } else if (FeatureParser.getBoolean("support_secret_dc_backlight", false) && "3223".equals(host)) {
                Settings.System.putInt(context.getContentResolver(), "dc_back_light", 0);
                SystemProperties.set("debug.secret_dc_backlight.enable", "true");
            }
        }
    }
}
