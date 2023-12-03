package miui.upnp.typedef.device;

import com.milink.api.v1.type.DeviceType;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes4.dex */
public enum DiscoveryType {
    UNDEFINED("undefined"),
    LOCAL(YellowPageContract.Search.LOCAL_SEARCH),
    LAN("lan"),
    BLUETOOTH(DeviceType.BLUETOOTH),
    BLE("ble"),
    AP("ap"),
    AIRTUNES(DeviceType.AIRTUNES);

    private String string;

    DiscoveryType(String str) {
        this.string = str;
    }

    public static DiscoveryType retrieveType(String str) {
        for (DiscoveryType discoveryType : values()) {
            if (discoveryType.toString().equals(str)) {
                return discoveryType;
            }
        }
        return UNDEFINED;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.string;
    }
}
