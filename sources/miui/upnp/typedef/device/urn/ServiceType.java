package miui.upnp.typedef.device.urn;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.device.urn.Urn;
import miui.upnp.typedef.device.urn.schemas.Schemas;

/* loaded from: classes4.dex */
public class ServiceType extends Urn {
    public static final Parcelable.Creator<ServiceType> CREATOR = new Parcelable.Creator<ServiceType>() { // from class: miui.upnp.typedef.device.urn.ServiceType.1
        @Override // android.os.Parcelable.Creator
        public ServiceType createFromParcel(Parcel parcel) {
            return new ServiceType(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public ServiceType[] newArray(int i) {
            return new ServiceType[i];
        }
    };

    public ServiceType() {
    }

    public ServiceType(Parcel parcel) {
        readFromParcel(parcel);
    }

    public ServiceType(String str, String str2) {
        super.setType(Urn.Type.SERVICE);
        super.setDomain(Schemas.UPNP);
        super.setSubType(str);
        super.setVersion(str2);
    }

    public static ServiceType create(String str) {
        ServiceType serviceType = new ServiceType();
        if (serviceType.parse(str)) {
            return serviceType;
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
        if (parse && getType() == Urn.Type.SERVICE) {
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
