package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

/* loaded from: classes4.dex */
public class IconDefinition {
    private static final String DEPTH = "depth";
    public static FieldDefinition Depth = null;
    private static final String HEIGHT = "height";
    public static FieldDefinition Height = null;
    private static final String MIME_TYPE = "mimetype";
    public static FieldDefinition MimeType = null;
    private static final String URL = "url";
    public static FieldDefinition Url = null;
    private static final String WIDTH = "width";
    public static FieldDefinition Width;

    static {
        DataType dataType = DataType.STRING;
        MimeType = new FieldDefinition(MIME_TYPE, dataType);
        DataType dataType2 = DataType.INT;
        Width = new FieldDefinition("width", dataType2);
        Height = new FieldDefinition(HEIGHT, dataType2);
        Depth = new FieldDefinition(DEPTH, dataType2);
        Url = new FieldDefinition("url", dataType);
    }
}
