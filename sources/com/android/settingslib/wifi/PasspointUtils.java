package com.android.settingslib.wifi;

import android.content.Context;
import android.net.wifi.MiuiWifiManager;
import android.net.wifi.ScanResult;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.PasspointR1Provider;
import com.android.wifitrackerlib.IPasspointUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes2.dex */
public class PasspointUtils implements IPasspointUtils {
    private static volatile PasspointUtils mInstance;
    private final MiuiWifiManager mMiuiWifiManager;

    public PasspointUtils(Context context) {
        this.mMiuiWifiManager = (MiuiWifiManager) context.getSystemService("MiuiWifiService");
    }

    public static PasspointUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PasspointUtils.class) {
                if (mInstance == null) {
                    mInstance = new PasspointUtils(context);
                }
            }
        }
        return mInstance;
    }

    @Override // com.android.wifitrackerlib.IPasspointUtils
    public Map<PasspointR1Provider, PasspointConfiguration> getMatchingPasspointConfigsForPasspointR1Providers(Set<PasspointR1Provider> set) {
        MiuiWifiManager miuiWifiManager = this.mMiuiWifiManager;
        return miuiWifiManager != null ? miuiWifiManager.getMatchingPasspointConfigsForPasspointR1Providers(set) : new HashMap();
    }

    @Override // com.android.wifitrackerlib.IPasspointUtils
    public Map<PasspointR1Provider, List<ScanResult>> getMatchingPasspointR1Providers(List<ScanResult> list) {
        MiuiWifiManager miuiWifiManager = this.mMiuiWifiManager;
        return miuiWifiManager != null ? miuiWifiManager.getMatchingPasspointR1Providers(list) : new HashMap();
    }
}
