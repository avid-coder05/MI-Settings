package android.net.wifi;

import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.PasspointR1Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class MiuiWifiManager {
    IMiuiWifiManager mService;

    public static int calculateSignalLevel(int i, int i2) {
        if (i <= -100) {
            return 0;
        }
        if (i >= -65) {
            return i2 - 1;
        }
        return (int) (((i - (-100)) * ((float) (i2 - 1))) / 35.0f);
    }

    public boolean addHotSpotMacBlackListOffload(String str) {
        return this.mService.addHotSpotMacBlackListOffload(str);
    }

    public boolean delHotSpotMacBlackListOffload(String str) {
        return this.mService.delHotSpotMacBlackListOffload(str);
    }

    public List<WifiClient> getConnectedWifiClient() {
        return this.mService.getConnectedWifiClient();
    }

    public Map<PasspointR1Provider, PasspointConfiguration> getMatchingPasspointConfigsForPasspointR1Providers(Set<PasspointR1Provider> set) {
        return this.mService.getMatchingPasspointConfigsForPasspointR1Providers(new ArrayList(set));
    }

    public Map<PasspointR1Provider, List<ScanResult>> getMatchingPasspointR1Providers(List<ScanResult> list) {
        return this.mService.getMatchingPasspointR1Providers(list);
    }

    public boolean isWpa3SaeSupported() {
        return this.mService.isWpa3SaeSupported();
    }

    public void setCompatibleMode(boolean z) {
    }

    public void setObservedAccessPionts(List<String> list) {
    }
}
