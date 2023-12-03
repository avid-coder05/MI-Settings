package miui.upnp.typedef.deviceclass;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import miui.upnp.typedef.device.urn.DeviceType;

/* loaded from: classes4.dex */
public class DeviceClass implements Parcelable, Serializable {
    public static final Parcelable.Creator<DeviceClass> CREATOR = new Parcelable.Creator<DeviceClass>() { // from class: miui.upnp.typedef.deviceclass.DeviceClass.1
        @Override // android.os.Parcelable.Creator
        public DeviceClass createFromParcel(Parcel parcel) {
            return new DeviceClass(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public DeviceClass[] newArray(int i) {
            return new DeviceClass[i];
        }
    };
    private Class<?> clazz;
    private DeviceType deviceType;

    private DeviceClass(Parcel parcel) {
        readFromParcel(parcel);
    }

    public DeviceClass(DeviceType deviceType, Class<?> cls) {
        this.deviceType = deviceType;
        this.clazz = cls;
    }

    private void readFromParcel(Parcel parcel) {
        this.deviceType = DeviceType.create(parcel.readString());
        this.clazz = (Class) parcel.readSerializable();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DeviceClass) {
            return this.deviceType.equals(((DeviceClass) obj).deviceType);
        }
        return false;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    public int hashCode() {
        return this.deviceType.hashCode();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.deviceType.toString());
        parcel.writeSerializable(this.clazz);
    }
}
