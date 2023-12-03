package miui.upnp.typedef.device.invocation;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

/* loaded from: classes4.dex */
public class SubscriptionInfoDefinition {
    private static final String CALLBACK_URL = "callbackUrl";
    private static final String CP_ADDRESS = "cpAddress";
    private static final String CP_PORT = "cpPort";
    public static FieldDefinition CallbackUrl = null;
    public static FieldDefinition CpAddress = null;
    public static FieldDefinition CpPort = null;
    private static final String DEVICE_ID = "deviceId";
    public static FieldDefinition DeviceId = null;
    private static final String EVENT_SUB_URL = "eventSubURL";
    public static FieldDefinition EventSubUrl = null;
    private static final String HOST_ADDRESS = "hostAddress";
    private static final String HOST_PORT = "hostPort";
    public static FieldDefinition HostAddress = null;
    public static FieldDefinition HostPort = null;
    private static final String SERVICE_ID = "serviceId";
    private static final String SESSION_ID = "sessionId";
    private static final String SUBSCRIBED = "subscribed";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    public static FieldDefinition ServiceId = null;
    public static FieldDefinition SessionId = null;
    public static FieldDefinition Subscribed = null;
    public static FieldDefinition SubscriptionId = null;
    private static final String TIMEOUT = "timeout";
    public static FieldDefinition Timeout;

    static {
        DataType dataType = DataType.STRING;
        HostAddress = new FieldDefinition(HOST_ADDRESS, dataType);
        DataType dataType2 = DataType.INT;
        HostPort = new FieldDefinition(HOST_PORT, dataType2);
        DeviceId = new FieldDefinition(DEVICE_ID, dataType);
        ServiceId = new FieldDefinition(SERVICE_ID, dataType);
        EventSubUrl = new FieldDefinition(EVENT_SUB_URL, dataType);
        SubscriptionId = new FieldDefinition(SUBSCRIPTION_ID, dataType);
        Subscribed = new FieldDefinition(SUBSCRIBED, DataType.BOOLEAN);
        Timeout = new FieldDefinition(TIMEOUT, dataType2);
        SessionId = new FieldDefinition(SESSION_ID, dataType);
        CallbackUrl = new FieldDefinition(CALLBACK_URL, dataType);
        CpAddress = new FieldDefinition(CP_ADDRESS, dataType);
        CpPort = new FieldDefinition(CP_PORT, dataType2);
    }
}
