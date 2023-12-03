package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

/* loaded from: classes4.dex */
public class ServiceDefinition {
    private static final String CONTROL_URL = "controlURL";
    public static FieldDefinition ControlUrl = null;
    private static final String EVENT_SUB_URL = "eventSubURL";
    public static FieldDefinition EventSubUrl = null;
    private static final String SCPDURL = "SCPDURL";
    private static final String SERVICE_ID = "serviceId";
    private static final String SUBSCRIBED = "subscribed";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    public static FieldDefinition ScpdUrl;
    public static FieldDefinition ServiceId;
    public static FieldDefinition Subscribed;
    public static FieldDefinition SubscriptionId;

    static {
        DataType dataType = DataType.STRING;
        ServiceId = new FieldDefinition(SERVICE_ID, dataType);
        ControlUrl = new FieldDefinition(CONTROL_URL, dataType);
        EventSubUrl = new FieldDefinition(EVENT_SUB_URL, dataType);
        ScpdUrl = new FieldDefinition(SCPDURL, dataType);
        Subscribed = new FieldDefinition(SUBSCRIBED, DataType.BOOLEAN);
        SubscriptionId = new FieldDefinition(SUBSCRIPTION_ID, dataType);
    }
}
