package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.upnp.typedef.device.urn.DeviceType;
import miui.upnp.typedef.field.FieldList;

/* loaded from: classes4.dex */
public class Device implements Parcelable {
    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() { // from class: miui.upnp.typedef.device.Device.1
        @Override // android.os.Parcelable.Creator
        public Device createFromParcel(Parcel parcel) {
            return new Device(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Device[] newArray(int i) {
            return new Device[i];
        }
    };
    private DeviceType deviceType = new DeviceType();
    private List<DiscoveryType> discoveryTypes = new ArrayList();
    private FieldList fields = new FieldList();
    private Map<String, Service> services = new HashMap();
    private List<Icon> icons = new ArrayList();

    public Device() {
        initialize();
    }

    public Device(Parcel parcel) {
        initialize();
        readFromParcel(parcel);
    }

    public Device(DeviceType deviceType) {
        initialize();
        setDeviceType(deviceType);
    }

    private void initialize() {
        this.fields.initField(DeviceDefinition.Location, null);
        this.fields.initField(DeviceDefinition.Address, null);
        this.fields.initField(DeviceDefinition.HostPort, null);
        this.fields.initField(DeviceDefinition.DeviceId, null);
        this.fields.initField(DeviceDefinition.FriendlyName, null);
        this.fields.initField(DeviceDefinition.Manufacturer, null);
        this.fields.initField(DeviceDefinition.ManufacturerUrl, null);
        this.fields.initField(DeviceDefinition.ModelDescription, null);
        this.fields.initField(DeviceDefinition.ModelName, null);
        this.fields.initField(DeviceDefinition.ModelNumber, null);
        this.fields.initField(DeviceDefinition.ModelUrl, null);
        this.fields.initField(DeviceDefinition.SerialNumber, null);
        this.fields.initField(DeviceDefinition.PresentationUrl, null);
        this.fields.initField(DeviceDefinition.UrlBase, null);
        this.fields.initField(DeviceDefinition.Upc, null);
        this.fields.initField(DeviceDefinition.QplayCapability, null);
        this.fields.initField(DeviceDefinition.DlnaDoc, null);
        this.fields.initField(DeviceDefinition.DlnaCap, null);
    }

    public void addDiscoveryType(DiscoveryType discoveryType) {
        if (this.discoveryTypes.contains(discoveryType)) {
            return;
        }
        this.discoveryTypes.add(discoveryType);
    }

    public void addIcon(Icon icon) {
        this.icons.add(icon);
    }

    public void addService(Service service) {
        service.setDevice(this);
        this.services.put(service.getServiceId(), service);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        Device device = (Device) obj;
        if (this == device) {
            return true;
        }
        return ((String) this.fields.getValue(DeviceDefinition.DeviceId)).equals((String) device.fields.getValue(DeviceDefinition.DeviceId));
    }

    public String getAddress() {
        return (String) this.fields.getValue(DeviceDefinition.Address);
    }

    public String getDeviceId() {
        return (String) this.fields.getValue(DeviceDefinition.DeviceId);
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    public List<DiscoveryType> getDiscoveryTypes() {
        return this.discoveryTypes;
    }

    public String getDlnaCap() {
        return (String) this.fields.getValue(DeviceDefinition.DlnaCap);
    }

    public String getDlnaDoc() {
        return (String) this.fields.getValue(DeviceDefinition.DlnaDoc);
    }

    public String getFriendlyName() {
        return (String) this.fields.getValue(DeviceDefinition.FriendlyName);
    }

    public int getHostPort() {
        return ((Integer) this.fields.getValue(DeviceDefinition.HostPort)).intValue();
    }

    public List<Icon> getIcons() {
        return this.icons;
    }

    public String getLocation() {
        return (String) this.fields.getValue(DeviceDefinition.Location);
    }

    public String getManufacturer() {
        return (String) this.fields.getValue(DeviceDefinition.Manufacturer);
    }

    public String getManufacturerUrl() {
        return (String) this.fields.getValue(DeviceDefinition.ManufacturerUrl);
    }

    public String getModelDescription() {
        return (String) this.fields.getValue(DeviceDefinition.ModelDescription);
    }

    public String getModelName() {
        return (String) this.fields.getValue(DeviceDefinition.ModelName);
    }

    public String getModelNumber() {
        return (String) this.fields.getValue(DeviceDefinition.ModelNumber);
    }

    public String getModelUrl() {
        return (String) this.fields.getValue(DeviceDefinition.ModelUrl);
    }

    public String getPresentationUrl() {
        return (String) this.fields.getValue(DeviceDefinition.PresentationUrl);
    }

    public String getQplayCapability() {
        return (String) this.fields.getValue(DeviceDefinition.QplayCapability);
    }

    public String getSerialNumber() {
        return (String) this.fields.getValue(DeviceDefinition.SerialNumber);
    }

    public Service getService(String str) {
        return this.services.get(str);
    }

    public Map<String, Service> getServices() {
        return this.services;
    }

    public String getUpc() {
        return (String) this.fields.getValue(DeviceDefinition.Upc);
    }

    public String getUrlBase() {
        return (String) this.fields.getValue(DeviceDefinition.UrlBase);
    }

    public int hashCode() {
        String str = (String) this.fields.getValue(DeviceDefinition.DeviceId);
        return 31 + (str == null ? 0 : str.hashCode());
    }

    public void readFromParcel(Parcel parcel) {
        this.deviceType = DeviceType.create(parcel.readString());
        this.fields = (FieldList) parcel.readParcelable(FieldList.class.getClassLoader());
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.discoveryTypes.add(DiscoveryType.retrieveType(parcel.readString()));
        }
        int readInt2 = parcel.readInt();
        for (int i2 = 0; i2 < readInt2; i2++) {
            this.icons.add((Icon) parcel.readParcelable(Icon.class.getClassLoader()));
        }
        int readInt3 = parcel.readInt();
        for (int i3 = 0; i3 < readInt3; i3++) {
            addService((Service) parcel.readParcelable(Service.class.getClassLoader()));
        }
    }

    public boolean setAddress(String str) {
        return this.fields.setValue(DeviceDefinition.Address, str);
    }

    public boolean setDeviceId(String str) {
        return this.fields.setValue(DeviceDefinition.DeviceId, str);
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public boolean setDlnaCap(String str) {
        return this.fields.setValue(DeviceDefinition.DlnaCap, str);
    }

    public boolean setDlnaDoc(String str) {
        return this.fields.setValue(DeviceDefinition.DlnaDoc, str);
    }

    public boolean setFriendlyName(String str) {
        return this.fields.setValue(DeviceDefinition.FriendlyName, str);
    }

    public boolean setHostPort(int i) {
        return this.fields.setValue(DeviceDefinition.HostPort, Integer.valueOf(i));
    }

    public boolean setLocation(String str) {
        return this.fields.setValue(DeviceDefinition.Location, str);
    }

    public boolean setManufacturer(String str) {
        return this.fields.setValue(DeviceDefinition.Manufacturer, str);
    }

    public boolean setManufacturerUrl(String str) {
        return this.fields.setValue(DeviceDefinition.ManufacturerUrl, str);
    }

    public boolean setModelDescription(String str) {
        return this.fields.setValue(DeviceDefinition.ModelDescription, str);
    }

    public boolean setModelName(String str) {
        return this.fields.setValue(DeviceDefinition.ModelName, str);
    }

    public boolean setModelNumber(String str) {
        return this.fields.setValue(DeviceDefinition.ModelNumber, str);
    }

    public boolean setModelUrl(String str) {
        return this.fields.setValue(DeviceDefinition.ModelUrl, str);
    }

    public boolean setPresentationUrl(String str) {
        return this.fields.setValue(DeviceDefinition.PresentationUrl, str);
    }

    public boolean setQplayCapability(String str) {
        return this.fields.setValue(DeviceDefinition.QplayCapability, str);
    }

    public boolean setSerialNumber(String str) {
        return this.fields.setValue(DeviceDefinition.SerialNumber, str);
    }

    public boolean setUpc(String str) {
        return this.fields.setValue(DeviceDefinition.Upc, str);
    }

    public boolean setUrlBase(String str) {
        return this.fields.setValue(DeviceDefinition.UrlBase, str);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.deviceType.toString());
        parcel.writeParcelable(this.fields, i);
        parcel.writeInt(this.discoveryTypes.size());
        Iterator<DiscoveryType> it = this.discoveryTypes.iterator();
        while (it.hasNext()) {
            parcel.writeString(it.next().toString());
        }
        parcel.writeInt(this.icons.size());
        Iterator<Icon> it2 = this.icons.iterator();
        while (it2.hasNext()) {
            parcel.writeParcelable(it2.next(), i);
        }
        parcel.writeInt(this.services.size());
        Iterator<Service> it3 = this.services.values().iterator();
        while (it3.hasNext()) {
            parcel.writeParcelable(it3.next(), i);
        }
    }
}
