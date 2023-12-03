package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

/* loaded from: classes4.dex */
public class SubscriptionDefinition {
    private static final String CALLBACK_URL = "callbackUrl";
    public static FieldDefinition CallbackUrl = null;
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    public static FieldDefinition SubscriptionId = null;
    private static final String TIMEOUT = "timeout";
    public static FieldDefinition Timeout;

    static {
        DataType dataType = DataType.STRING;
        CallbackUrl = new FieldDefinition(CALLBACK_URL, dataType);
        SubscriptionId = new FieldDefinition(SUBSCRIPTION_ID, dataType);
        Timeout = new FieldDefinition(TIMEOUT, DataType.INT);
    }
}
