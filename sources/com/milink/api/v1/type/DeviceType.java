package com.milink.api.v1.type;

/* loaded from: classes2.dex */
public enum DeviceType {
    Unknown,
    TV,
    Speaker,
    Miracast,
    Lelink,
    Bluetooth;

    public static final String AIRKAN = "airkan";
    public static final String AIRPLAY = "airplay";
    public static final String AIRTUNES = "airtunes";
    public static final String BLUETOOTH = "bluetooth";
    public static final String DLNA_SPEAKER = "dlna.speaker";
    public static final String DLNA_TV = "dlna.tv";
    public static final String LELINK = "lelink";
    public static final String MIRACAST = "miracast";

    public static DeviceType create(String str) {
        if (!str.equalsIgnoreCase(AIRKAN) && !str.equalsIgnoreCase(AIRPLAY)) {
            return str.equalsIgnoreCase(AIRTUNES) ? Speaker : str.equalsIgnoreCase(DLNA_TV) ? TV : str.equalsIgnoreCase(DLNA_SPEAKER) ? Speaker : str.equalsIgnoreCase(MIRACAST) ? Miracast : str.equalsIgnoreCase(LELINK) ? Lelink : str.equalsIgnoreCase(BLUETOOTH) ? Bluetooth : Unknown;
        }
        return TV;
    }
}
