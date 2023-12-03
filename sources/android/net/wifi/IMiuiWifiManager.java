package android.net.wifi;

import android.net.wifi.hotspot2.PasspointR1Provider;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public interface IMiuiWifiManager {
    boolean addHotSpotMacBlackListOffload(String str);

    boolean delHotSpotMacBlackListOffload(String str);

    List<WifiClient> getConnectedWifiClient();

    Map getMatchingPasspointConfigsForPasspointR1Providers(List<PasspointR1Provider> list);

    Map getMatchingPasspointR1Providers(List<ScanResult> list);

    boolean isWpa3SaeSupported();
}
