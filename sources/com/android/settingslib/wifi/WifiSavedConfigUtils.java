package com.android.settingslib.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.PasspointConfiguration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class WifiSavedConfigUtils {
    public static List<AccessPoint> getAllConfigs(Context context, WifiManager wifiManager) {
        ArrayList arrayList = new ArrayList();
        for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {
            if (!wifiConfiguration.isPasspoint() && !wifiConfiguration.isEphemeral()) {
                arrayList.add(new AccessPoint(context, wifiConfiguration));
            }
        }
        try {
            List<PasspointConfiguration> passpointConfigurations = wifiManager.getPasspointConfigurations();
            if (passpointConfigurations != null) {
                Iterator<PasspointConfiguration> it = passpointConfigurations.iterator();
                while (it.hasNext()) {
                    arrayList.add(new AccessPoint(context, it.next()));
                }
            }
        } catch (UnsupportedOperationException unused) {
        }
        return arrayList;
    }

    public static int getAllConfigsCount(Context context, WifiManager wifiManager) {
        return getAllConfigs(context, wifiManager).size();
    }
}
