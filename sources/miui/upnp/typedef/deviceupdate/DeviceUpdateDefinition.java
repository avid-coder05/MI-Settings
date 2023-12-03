package miui.upnp.typedef.deviceupdate;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

/* loaded from: classes4.dex */
public class DeviceUpdateDefinition {
    private static final String DISCOVERY_TYPE = "discoveryType";
    public static FieldDefinition DeviceId = null;
    public static FieldDefinition DiscoveryType = null;
    private static final String UDN = "UDN";

    static {
        DataType dataType = DataType.STRING;
        DeviceId = new FieldDefinition(UDN, dataType);
        DiscoveryType = new FieldDefinition(DISCOVERY_TYPE, dataType);
    }
}
