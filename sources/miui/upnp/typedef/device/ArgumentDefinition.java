package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

/* loaded from: classes4.dex */
public class ArgumentDefinition {
    private static final String DIRECTION = "direction";
    public static FieldDefinition Direction = null;
    private static final String NAME = "name";
    public static FieldDefinition Name = null;
    private static final String RELATED_PROPERTY = "relatedStateVariable";
    public static FieldDefinition RelatedProperty;

    static {
        DataType dataType = DataType.STRING;
        Name = new FieldDefinition("name", dataType);
        Direction = new FieldDefinition("direction", dataType);
        RelatedProperty = new FieldDefinition(RELATED_PROPERTY, dataType);
    }
}
