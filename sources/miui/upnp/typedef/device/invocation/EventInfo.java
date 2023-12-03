package miui.upnp.typedef.device.invocation;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.upnp.typedef.device.DiscoveryType;
import miui.upnp.typedef.device.urn.ServiceType;
import miui.upnp.typedef.field.FieldList;
import miui.upnp.typedef.property.Property;

/* loaded from: classes4.dex */
public class EventInfo implements Parcelable {
    public static final Parcelable.Creator<EventInfo> CREATOR = new Parcelable.Creator<EventInfo>() { // from class: miui.upnp.typedef.device.invocation.EventInfo.1
        @Override // android.os.Parcelable.Creator
        public EventInfo createFromParcel(Parcel parcel) {
            return new EventInfo(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public EventInfo[] newArray(int i) {
            return new EventInfo[i];
        }
    };
    private List<DiscoveryType> discoveryTypes = new ArrayList();
    private ServiceType serviceType = new ServiceType();
    private FieldList fields = new FieldList();
    private List<Property> properties = new ArrayList();

    public EventInfo() {
        initialize();
    }

    public EventInfo(Parcel parcel) {
        initialize();
        readFromParcel(parcel);
    }

    private void initialize() {
        this.fields.initField(EventInfoDefinition.DeviceId, null);
        this.fields.initField(EventInfoDefinition.ServiceId, null);
        this.fields.initField(EventInfoDefinition.SessionId, null);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getDeviceId() {
        return (String) this.fields.getValue(EventInfoDefinition.DeviceId);
    }

    public List<DiscoveryType> getDiscoveryTypes() {
        return this.discoveryTypes;
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    public String getServiceId() {
        return (String) this.fields.getValue(EventInfoDefinition.ServiceId);
    }

    public ServiceType getServiceType() {
        return this.serviceType;
    }

    public String getSessionId() {
        return (String) this.fields.getValue(EventInfoDefinition.SessionId);
    }

    public void readFromParcel(Parcel parcel) {
        this.serviceType = ServiceType.create(parcel.readString());
        this.fields = (FieldList) parcel.readParcelable(FieldList.class.getClassLoader());
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.discoveryTypes.add(DiscoveryType.retrieveType(parcel.readString()));
        }
        int readInt2 = parcel.readInt();
        for (int i2 = 0; i2 < readInt2; i2++) {
            this.properties.add((Property) parcel.readParcelable(Property.class.getClassLoader()));
        }
    }

    public boolean setDeviceId(String str) {
        return this.fields.setValue(EventInfoDefinition.DeviceId, str);
    }

    public void setDiscoveryTypes(List<DiscoveryType> list) {
        this.discoveryTypes = list;
    }

    public boolean setServiceId(String str) {
        return this.fields.setValue(EventInfoDefinition.ServiceId, str);
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public boolean setSessionId(String str) {
        return this.fields.setValue(EventInfoDefinition.SessionId, str);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.serviceType.toString());
        parcel.writeParcelable(this.fields, i);
        parcel.writeInt(this.discoveryTypes.size());
        Iterator<DiscoveryType> it = this.discoveryTypes.iterator();
        while (it.hasNext()) {
            parcel.writeString(it.next().toString());
        }
        parcel.writeInt(this.properties.size());
        Iterator<Property> it2 = this.properties.iterator();
        while (it2.hasNext()) {
            parcel.writeParcelable(it2.next(), i);
        }
    }
}
