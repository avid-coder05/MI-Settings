package miui.upnp.typedef.deviceupdate;

/* loaded from: classes4.dex */
public enum DeviceUpdateType {
    UNDEFINED("undefined"),
    DEVICE_FOUND("deviceFound"),
    DEVICE_LOST("deviceLost");

    private String string;

    DeviceUpdateType(String str) {
        this.string = str;
    }

    public static DeviceUpdateType retrieveType(String str) {
        for (DeviceUpdateType deviceUpdateType : values()) {
            if (deviceUpdateType.toString().equals(str)) {
                return deviceUpdateType;
            }
        }
        return UNDEFINED;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.string;
    }
}
