package miui.upnp.typedef.deviceupdate;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.upnp.typedef.device.DiscoveryType;
import miui.upnp.typedef.field.FieldList;

/* loaded from: classes4.dex */
public class DeviceUpdate implements Parcelable {
    public static final Parcelable.Creator<DeviceUpdate> CREATOR = new Parcelable.Creator<DeviceUpdate>() { // from class: miui.upnp.typedef.deviceupdate.DeviceUpdate.1
        @Override // android.os.Parcelable.Creator
        public DeviceUpdate createFromParcel(Parcel parcel) {
            return new DeviceUpdate(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public DeviceUpdate[] newArray(int i) {
            return new DeviceUpdate[i];
        }
    };
    private List<DeviceUpdateType> types = new ArrayList();
    private FieldList fields = new FieldList();

    public DeviceUpdate() {
        initialize();
    }

    public DeviceUpdate(Parcel parcel) {
        readFromParcel(parcel);
    }

    private void initialize() {
        this.fields.initField(DeviceUpdateDefinition.DiscoveryType, null);
    }

    public void addType(DeviceUpdateType deviceUpdateType) {
        if (this.types.contains(deviceUpdateType)) {
            return;
        }
        this.types.add(deviceUpdateType);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getDeviceId() {
        return (String) this.fields.getValue(DeviceUpdateDefinition.DeviceId);
    }

    public DiscoveryType getDiscoveryType() {
        return DiscoveryType.retrieveType((String) this.fields.getValue(DeviceUpdateDefinition.DiscoveryType));
    }

    public List<DeviceUpdateType> getTypes() {
        return this.types;
    }

    public void readFromParcel(Parcel parcel) {
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.types.add(DeviceUpdateType.retrieveType(parcel.readString()));
        }
        this.fields = (FieldList) parcel.readParcelable(FieldList.class.getClassLoader());
    }

    public boolean setDeviceId(String str) {
        return this.fields.setValue(DeviceUpdateDefinition.DeviceId, str);
    }

    public boolean setDiscoveryType(DiscoveryType discoveryType) {
        return this.fields.setValue(DeviceUpdateDefinition.DiscoveryType, discoveryType.toString());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.types.size());
        Iterator<DeviceUpdateType> it = this.types.iterator();
        while (it.hasNext()) {
            parcel.writeString(it.next().toString());
        }
        parcel.writeParcelable(this.fields, i);
    }
}
