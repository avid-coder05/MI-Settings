package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

/* loaded from: classes4.dex */
public class DeviceDefinition {
    private static final String ADDRESS = "address";
    public static FieldDefinition Address = null;
    private static final String DLNA_CAP = "dlna:X_DLNACAP";
    private static final String DLNA_DOC = "dlna:X_DLNADOC";
    public static FieldDefinition DeviceId = null;
    public static FieldDefinition DlnaCap = null;
    public static FieldDefinition DlnaDoc = null;
    private static final String FRIENDLY_NAME = "friendlyName";
    public static FieldDefinition FriendlyName = null;
    private static final String HOST_PORT = "hostPort";
    public static FieldDefinition HostPort = null;
    private static final String LOCATION = "location";
    public static FieldDefinition Location = null;
    private static final String MANUFACTURER = "manufacturer";
    private static final String MANUFACTURER_URL = "manufacturerURL";
    private static final String MODEL_DESCRIPTION = "modelDescription";
    private static final String MODEL_NAME = "modelName";
    private static final String MODEL_NUMBER = "modelNumber";
    private static final String MODEL_URL = "modelURL";
    public static FieldDefinition Manufacturer = null;
    public static FieldDefinition ManufacturerUrl = null;
    public static FieldDefinition ModelDescription = null;
    public static FieldDefinition ModelName = null;
    public static FieldDefinition ModelNumber = null;
    public static FieldDefinition ModelUrl = null;
    private static final String PRESENTATION_URL = "presentationURL";
    public static FieldDefinition PresentationUrl = null;
    private static final String QPLAY_CAPABILITY = "qq:X_QPlay_SoftwareCapability";
    public static FieldDefinition QplayCapability = null;
    private static final String SERIAL_NUMBER = "serialNumber";
    public static FieldDefinition SerialNumber = null;
    private static final String UDN = "UDN";
    private static final String UPC = "UPC";
    private static final String URLBASE = "URLBase";
    public static FieldDefinition Upc;
    public static FieldDefinition UrlBase;

    static {
        DataType dataType = DataType.STRING;
        Location = new FieldDefinition("location", dataType);
        Address = new FieldDefinition("address", dataType);
        HostPort = new FieldDefinition(HOST_PORT, DataType.INT);
        DeviceId = new FieldDefinition(UDN, dataType);
        FriendlyName = new FieldDefinition(FRIENDLY_NAME, dataType);
        Manufacturer = new FieldDefinition(MANUFACTURER, dataType);
        ManufacturerUrl = new FieldDefinition(MANUFACTURER_URL, dataType);
        ModelDescription = new FieldDefinition(MODEL_DESCRIPTION, dataType);
        ModelName = new FieldDefinition(MODEL_NAME, dataType);
        ModelNumber = new FieldDefinition(MODEL_NUMBER, dataType);
        ModelUrl = new FieldDefinition(MODEL_URL, dataType);
        SerialNumber = new FieldDefinition(SERIAL_NUMBER, dataType);
        PresentationUrl = new FieldDefinition(PRESENTATION_URL, dataType);
        UrlBase = new FieldDefinition(URLBASE, dataType);
        Upc = new FieldDefinition(UPC, dataType);
        QplayCapability = new FieldDefinition(QPLAY_CAPABILITY, dataType);
        DlnaDoc = new FieldDefinition(DLNA_DOC, dataType);
        DlnaCap = new FieldDefinition(DLNA_CAP, dataType);
    }
}
