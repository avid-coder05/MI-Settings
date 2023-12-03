package miui.upnp.manager.ctrlpoint;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.urn.DeviceType;

/* loaded from: classes4.dex */
public class UnknownDevice extends AbstractDevice {
    public static final DeviceType DEVICE_TYPE = new DeviceType("?", "?");
    private static final Object classLock = UnknownDevice.class;
    public static final Parcelable.Creator<UnknownDevice> CREATOR = new Parcelable.Creator<UnknownDevice>() { // from class: miui.upnp.manager.ctrlpoint.UnknownDevice.1
        @Override // android.os.Parcelable.Creator
        public UnknownDevice createFromParcel(Parcel parcel) {
            return new UnknownDevice(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public UnknownDevice[] newArray(int i) {
            return new UnknownDevice[i];
        }
    };

    private UnknownDevice(Parcel parcel) {
        readFromParcel(parcel);
    }

    private UnknownDevice(Device device) {
        this.device = device;
    }

    public static UnknownDevice create(Device device) {
        UnknownDevice unknownDevice;
        synchronized (classLock) {
            unknownDevice = new UnknownDevice(device);
        }
        return unknownDevice;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel parcel) {
        this.device = (Device) parcel.readParcelable(Device.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.device, i);
    }
}
