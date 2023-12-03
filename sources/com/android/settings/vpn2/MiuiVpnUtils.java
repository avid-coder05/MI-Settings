package com.android.settings.vpn2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.security.KeyStore;
import android.security.LegacyVpnProfileStore;
import android.text.TextUtils;
import androidx.preference.PreferenceManager;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnProfile;
import com.android.internal.widget.LockPatternUtils;

/* loaded from: classes2.dex */
public class MiuiVpnUtils {
    public static boolean autoConnectVpn(Context context) {
        String[] list;
        String connectedVpnKey = getConnectedVpnKey(context);
        if (TextUtils.isEmpty(connectedVpnKey)) {
            return false;
        }
        KeyStore keyStore = KeyStore.getInstance();
        if ((!new LockPatternUtils(context).isSecure(UserHandle.myUserId()) || Settings.Secure.getInt(context.getContentResolver(), "vpn_password_enable", 0) <= 0) && (list = LegacyVpnProfileStore.list("VPN_")) != null) {
            for (String str : list) {
                if (connectedVpnKey.equals(str)) {
                    VpnProfile decode = VpnProfile.decode(str, keyStore.get("VPN_" + str));
                    try {
                        android.net.VpnManager vpnManager = (android.net.VpnManager) context.getSystemService("vpn_management");
                        LegacyVpnInfo legacyVpnInfo = vpnManager.getLegacyVpnInfo(UserHandle.myUserId());
                        if (legacyVpnInfo == null || !legacyVpnInfo.key.equals(decode.key)) {
                            vpnManager.startLegacyVpn(decode);
                            return true;
                        }
                        return true;
                    } catch (Exception unused) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public static int getConfiguredVpnStatus(Context context) {
        boolean z;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        try {
            try {
                z = defaultSharedPreferences.getInt("vpn_configured", 0);
            } catch (ClassCastException unused) {
                z = defaultSharedPreferences.getBoolean("vpn_configured", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (z || VpnSettings.getVpnApps(context, true).size() > 0) ? 1 : 0;
    }

    public static String getConnectedVpnKey(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("connected_vpn_key", "");
    }

    public static void saveVpnConfiguredStatus(Context context, int i) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        edit.putInt("vpn_configured", i);
        edit.apply();
    }

    public static void setConnectedVpnKey(Context context, VpnProfile vpnProfile) {
        if (vpnProfile != null) {
            setConnectedVpnKey(context, vpnProfile.key);
        }
    }

    public static void setConnectedVpnKey(Context context, String str) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("connected_vpn_key", str);
        edit.commit();
    }

    public static void setVpnEnable(Context context, boolean z) {
        MiuiSettings.System.putBoolean(context.getContentResolver(), "vpn_enable_key", z);
    }

    public static void turnOnVpn(Context context, boolean z) {
        setVpnEnable(context, z);
        if (!z || autoConnectVpn(context)) {
            return;
        }
        Intent intent = new Intent("android.net.vpn.SETTINGS");
        intent.setPackage("com.android.settings");
        intent.addFlags(268435456);
        context.startActivity(intent);
    }
}
