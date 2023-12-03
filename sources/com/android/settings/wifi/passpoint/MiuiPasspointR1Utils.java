package com.android.settings.wifi.passpoint;

import android.app.BroadcastOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.IMiuiCaptivePortal;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.wifitrackerlib.WifiPasspointProvision;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes2.dex */
public class MiuiPasspointR1Utils {
    public static final List<String> PROVIDER_FQDN_LIST = Arrays.asList("exands.com");

    public static boolean getRegisterState(Context context, String str) {
        return context.getSharedPreferences("passpoint", 0).getBoolean(str, false);
    }

    public static void gotoLoginActivity(final Context context, final String str, String str2) {
        try {
            if ("exands.com".equals(str)) {
                String str3 = "https://hs2.exands.com:10443/xiaomi/?UID=" + WifiPasspointProvision.getUserName(context);
                BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
                makeBasic.setBackgroundActivityStartsAllowed(true);
                Intent intent = new Intent("com.miui.action.PASSPOINT_WIFI_LOGIN");
                intent.addFlags(268435456);
                URL url = new URL(str3);
                intent.putExtra("miui.intent.extra.OPEN_WIFI_SSID", str2);
                intent.putExtra("miui.intent.extra.CAPTIVE_PORTAL", (IBinder) new IMiuiCaptivePortal.Stub() { // from class: com.android.settings.wifi.passpoint.MiuiPasspointR1Utils.1
                    public void appResponse(int i) {
                        MiuiPasspointR1Utils.saveRegisterState(context, str, i == 0);
                        if (i == 1) {
                            MiuiPasspointR1Utils.removePasspointConfig(context, str);
                        } else {
                            context.sendBroadcast(new Intent("com.miui.wifi.passpoint.action.PASSPOINT_CONNECTED"));
                        }
                    }
                });
                intent.setData(Uri.parse(url.toString()));
                intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
                context.sendBroadcastAsUser(intent, UserHandle.ALL, null, makeBasic.toBundle());
            }
        } catch (MalformedURLException e) {
            Log.e("MiuiPasspointR1Utils", "MalformedURLException: " + e.toString(), e);
        }
    }

    public static void removeAllUnregisteredConfig(Context context) {
        for (String str : PROVIDER_FQDN_LIST) {
            if (!getRegisterState(context, str)) {
                removePasspointConfig(context, str);
            }
        }
    }

    public static void removePasspointConfig(Context context, String str) {
        if (str == null) {
            return;
        }
        try {
            ((WifiManager) context.getSystemService("wifi")).removePasspointConfiguration(str);
            saveRegisterState(context, str, false);
        } catch (IllegalArgumentException e) {
            Log.e("MiuiPasspointR1Utils", "Failed to remove Passpoint configuration with error: " + e);
        }
    }

    public static void saveRegisterState(Context context, String str, boolean z) {
        if (str == null) {
            str = "exands.com";
        }
        SharedPreferences.Editor edit = context.getSharedPreferences("passpoint", 0).edit();
        edit.putBoolean(str, z);
        edit.commit();
    }
}
