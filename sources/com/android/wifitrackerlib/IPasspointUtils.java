package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.PasspointR1Provider;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes2.dex */
public interface IPasspointUtils {
    Map<PasspointR1Provider, PasspointConfiguration> getMatchingPasspointConfigsForPasspointR1Providers(Set<PasspointR1Provider> set);

    Map<PasspointR1Provider, List<ScanResult>> getMatchingPasspointR1Providers(List<ScanResult> list);
}
