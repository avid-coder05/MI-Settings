package miui.upnp.typedef.device.invocation;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

/* loaded from: classes4.dex */
public class EventInfoDefinition {
    private static final String DEVICE_ID = "deviceId";
    public static FieldDefinition DeviceId = null;
    private static final String SERVICE_ID = "serviceId";
    private static final String SESSION_ID = "sessionId";
    public static FieldDefinition ServiceId;
    public static FieldDefinition SessionId;

    static {
        DataType dataType = DataType.STRING;
        DeviceId = new FieldDefinition(DEVICE_ID, dataType);
        ServiceId = new FieldDefinition(SERVICE_ID, dataType);
        SessionId = new FieldDefinition(SESSION_ID, dataType);
    }
}
