package com.android.settings.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import com.android.wifitrackerlib.WifiEntry;
import java.nio.charset.StandardCharsets;

/* loaded from: classes2.dex */
public class WifiUtils extends com.android.settingslib.wifi.WifiUtils {
    public static int getConnectingType(WifiEntry wifiEntry) {
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        if (wifiEntry.getSecurity() == 0 || wifiEntry.getSecurity() == 4) {
            return 1;
        }
        return (!wifiEntry.isSaved() || wifiConfiguration == null || wifiConfiguration.getNetworkSelectionStatus() == null || !wifiConfiguration.getNetworkSelectionStatus().hasEverConnected()) ? 0 : 2;
    }

    public static WifiConfiguration getWifiConfig(WifiEntry wifiEntry, ScanResult scanResult) {
        int security;
        if (wifiEntry == null && scanResult == null) {
            throw new IllegalArgumentException("At least one of WifiEntry and ScanResult input is required.");
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        if (wifiEntry == null) {
            wifiConfiguration.SSID = "\"" + scanResult.SSID + "\"";
            security = getWifiEntrySecurity(scanResult);
        } else {
            if (wifiEntry.getWifiConfiguration() == null) {
                wifiConfiguration.SSID = "\"" + wifiEntry.getSsid() + "\"";
            } else {
                wifiConfiguration.networkId = wifiEntry.getWifiConfiguration().networkId;
                wifiConfiguration.hiddenSSID = wifiEntry.getWifiConfiguration().hiddenSSID;
            }
            security = wifiEntry.getSecurity();
        }
        switch (security) {
            case 0:
                wifiConfiguration.setSecurityParams(0);
                break;
            case 1:
                wifiConfiguration.setSecurityParams(1);
                break;
            case 2:
                wifiConfiguration.setSecurityParams(2);
                break;
            case 3:
                wifiConfiguration.setSecurityParams(3);
                break;
            case 4:
                wifiConfiguration.setSecurityParams(6);
                break;
            case 5:
                wifiConfiguration.setSecurityParams(4);
                break;
            case 6:
                wifiConfiguration.setSecurityParams(5);
                break;
            case 8:
                wifiConfiguration.allowedKeyManagement.set(13);
                break;
            case 9:
                wifiConfiguration.allowedKeyManagement.set(14);
                break;
        }
        return wifiConfiguration;
    }

    public static int getWifiEntrySecurity(ScanResult scanResult) {
        if (scanResult.capabilities.contains("WEP")) {
            return 1;
        }
        if (scanResult.capabilities.contains("SAE")) {
            return 5;
        }
        if (scanResult.capabilities.contains("PSK")) {
            return 2;
        }
        if (scanResult.capabilities.contains("EAP_SUITE_B_192")) {
            return 6;
        }
        if (scanResult.capabilities.contains("EAP")) {
            return 3;
        }
        return scanResult.capabilities.contains("OWE") ? 4 : 0;
    }

    public static boolean isHotspotPasswordValid(String str, int i) {
        try {
            new SoftApConfiguration.Builder().setPassphrase(str, i);
            return true;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x003b, code lost:
    
        if (r2.getPackageUidAsUser(r4.getPackageName(), r1.getDeviceOwnerUserId()) == r7.creatorUid) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x005d, code lost:
    
        if (r2.getPackageUidAsUser(r1.getPackageName(), r3) == r7.creatorUid) goto L15;
     */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x0060 -> B:24:0x0061). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isNetworkLockedDown(android.content.Context r6, android.net.wifi.WifiConfiguration r7) {
        /*
            r0 = 0
            if (r7 != 0) goto L4
            return r0
        L4:
            java.lang.String r1 = "device_policy"
            java.lang.Object r1 = r6.getSystemService(r1)
            android.app.admin.DevicePolicyManager r1 = (android.app.admin.DevicePolicyManager) r1
            android.content.pm.PackageManager r2 = r6.getPackageManager()
            java.lang.String r3 = "user"
            java.lang.Object r3 = r6.getSystemService(r3)
            android.os.UserManager r3 = (android.os.UserManager) r3
            java.lang.String r4 = "android.software.device_admin"
            boolean r4 = r2.hasSystemFeature(r4)
            r5 = 1
            if (r4 == 0) goto L25
            if (r1 != 0) goto L25
            return r5
        L25:
            if (r1 == 0) goto L60
            android.content.ComponentName r4 = r1.getDeviceOwnerComponentOnAnyUser()
            if (r4 == 0) goto L3f
            int r1 = r1.getDeviceOwnerUserId()
            java.lang.String r3 = r4.getPackageName()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L60
            int r1 = r2.getPackageUidAsUser(r3, r1)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L60
            int r7 = r7.creatorUid     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L60
            if (r1 != r7) goto L60
        L3d:
            r7 = r5
            goto L61
        L3f:
            boolean r4 = r1.isOrganizationOwnedDeviceWithManagedProfile()
            if (r4 == 0) goto L60
            int r4 = android.os.UserHandle.myUserId()
            int r3 = com.android.settings.Utils.getManagedProfileId(r3, r4)
            android.content.ComponentName r1 = r1.getProfileOwnerAsUser(r3)
            if (r1 == 0) goto L60
            java.lang.String r1 = r1.getPackageName()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L60
            int r1 = r2.getPackageUidAsUser(r1, r3)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L60
            int r7 = r7.creatorUid     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L60
            if (r1 != r7) goto L60
            goto L3d
        L60:
            r7 = r0
        L61:
            if (r7 != 0) goto L64
            return r0
        L64:
            android.content.ContentResolver r6 = r6.getContentResolver()
            java.lang.String r7 = "wifi_device_owner_configs_lockdown"
            int r6 = android.provider.Settings.Global.getInt(r6, r7, r0)
            if (r6 == 0) goto L72
            r0 = r5
        L72:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiUtils.isNetworkLockedDown(android.content.Context, android.net.wifi.WifiConfiguration):boolean");
    }

    public static boolean isSSIDTooLong(String str) {
        return !TextUtils.isEmpty(str) && str.getBytes(StandardCharsets.UTF_8).length > 32;
    }

    public static boolean isSSIDTooShort(String str) {
        return TextUtils.isEmpty(str) || str.length() < 1;
    }
}
