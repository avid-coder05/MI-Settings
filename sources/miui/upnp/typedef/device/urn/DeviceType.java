package miui.upnp.typedef.device.urn;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.device.urn.Urn;
import miui.upnp.typedef.device.urn.schemas.Schemas;

/* loaded from: classes4.dex */
public class DeviceType extends Urn {
    public static final Parcelable.Creator<DeviceType> CREATOR = new Parcelable.Creator<DeviceType>() { // from class: miui.upnp.typedef.device.urn.DeviceType.1
        @Override // android.os.Parcelable.Creator
        public DeviceType createFromParcel(Parcel parcel) {
            return new DeviceType(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public DeviceType[] newArray(int i) {
            return new DeviceType[i];
        }
    };

    public DeviceType() {
    }

    public DeviceType(Parcel parcel) {
        readFromParcel(parcel);
    }

    public DeviceType(String str, String str2) {
        super.setType(Urn.Type.DEVICE);
        super.setDomain(Schemas.UPNP);
        super.setSubType(str);
        super.setVersion(str2);
    }

    public static DeviceType create(String str) {
        DeviceType deviceType = new DeviceType();
        if (deviceType.parse(str)) {
            return deviceType;
        }
        return null;
    }

    @Override // miui.upnp.typedef.device.urn.Urn, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return getSubType();
    }

    @Override // miui.upnp.typedef.device.urn.Urn
    public boolean parse(String str) {
        boolean parse = super.parse(str);
        if (parse && getType() == Urn.Type.DEVICE) {
            return true;
        }
        return parse;
    }

    @Override // miui.upnp.typedef.device.urn.Urn
    public void readFromParcel(Parcel parcel) {
        parse(parcel.readString());
    }

    @Override // miui.upnp.typedef.device.urn.Urn, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(toString());
    }
}
