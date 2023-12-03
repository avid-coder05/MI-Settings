package com.android.settings.vpn2;

import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;
import android.security.LegacyVpnProfileStore;
import com.android.internal.net.VpnConfig;

/* loaded from: classes2.dex */
public class VpnUtils {
    public static void clearLockdownVpn(Context context) {
        LegacyVpnProfileStore.remove("LOCKDOWN_VPN");
        getVpnManager(context).updateLockdownVpn();
    }

    public static boolean disconnectLegacyVpn(Context context) {
        int userId = context.getUserId();
        if (getVpnManager(context).getLegacyVpnInfo(userId) != null) {
            clearLockdownVpn(context);
            getVpnManager(context).prepareVpn(null, "[Legacy VPN]", userId);
            return true;
        }
        return false;
    }

    public static String getConnectedPackage(android.net.VpnManager vpnManager, int i) {
        VpnConfig vpnConfig = vpnManager.getVpnConfig(i);
        if (vpnConfig != null) {
            return vpnConfig.user;
        }
        return null;
    }

    public static String getLockdownVpn() {
        byte[] bArr = LegacyVpnProfileStore.get("LOCKDOWN_VPN");
        if (bArr == null) {
            return null;
        }
        return new String(bArr);
    }

    private static android.net.VpnManager getVpnManager(Context context) {
        return (android.net.VpnManager) context.getSystemService(android.net.VpnManager.class);
    }

    public static boolean isAlwaysOnVpnSet(android.net.VpnManager vpnManager, int i) {
        return vpnManager.getAlwaysOnVpnPackageForUser(i) != null;
    }

    public static boolean isAnyLockdownActive(Context context) {
        int userId = context.getUserId();
        if (getLockdownVpn() != null) {
            return true;
        }
        return (getVpnManager(context).getAlwaysOnVpnPackageForUser(userId) == null || Settings.Secure.getIntForUser(context.getContentResolver(), "always_on_vpn_lockdown", 0, userId) == 0) ? false : true;
    }

    public static boolean isVpnActive(Context context) throws RemoteException {
        return getVpnManager(context).getVpnConfig(context.getUserId()) != null;
    }

    public static boolean isVpnLockdown(String str) {
        return str.equals(getLockdownVpn());
    }

    public static void setLockdownVpn(Context context, String str) {
        LegacyVpnProfileStore.put("LOCKDOWN_VPN", str.getBytes());
        getVpnManager(context).updateLockdownVpn();
    }
}
