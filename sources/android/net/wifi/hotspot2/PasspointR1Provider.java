package android.net.wifi.hotspot2;

import android.net.wifi.WifiSsid;
import java.util.Objects;

/* loaded from: classes.dex */
public final class PasspointR1Provider {
    private final String mDomainName;
    private WifiSsid mR1Ssid;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PasspointR1Provider) {
            PasspointR1Provider passpointR1Provider = (PasspointR1Provider) obj;
            WifiSsid wifiSsid = this.mR1Ssid;
            if (wifiSsid != null ? wifiSsid.equals(passpointR1Provider.mR1Ssid) : passpointR1Provider.mR1Ssid == null) {
                if (this.mDomainName == null) {
                    return passpointR1Provider.mDomainName == null;
                }
            }
            return this.mDomainName.equals(passpointR1Provider.mDomainName);
        }
        return false;
    }

    public String getDomainName() {
        return this.mDomainName;
    }

    public int hashCode() {
        return Objects.hash(this.mR1Ssid, this.mDomainName);
    }

    public String toString() {
        return "PasspointR1Provider{mR1Ssid=" + this.mR1Ssid + " mDomainName=" + this.mDomainName;
    }
}
