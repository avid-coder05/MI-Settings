package miui.upnp.typedef.device.invocation;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.upnp.typedef.device.Action;
import miui.upnp.typedef.device.Argument;
import miui.upnp.typedef.device.DiscoveryType;
import miui.upnp.typedef.device.urn.ServiceType;
import miui.upnp.typedef.field.FieldList;
import miui.upnp.typedef.property.Property;

/* loaded from: classes4.dex */
public class ActionInfo implements Parcelable {
    public static final Parcelable.Creator<ActionInfo> CREATOR = new Parcelable.Creator<ActionInfo>() { // from class: miui.upnp.typedef.device.invocation.ActionInfo.1
        @Override // android.os.Parcelable.Creator
        public ActionInfo createFromParcel(Parcel parcel) {
            return new ActionInfo(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public ActionInfo[] newArray(int i) {
            return new ActionInfo[i];
        }
    };
    private static final String TAG = "ActionInfo";
    private Action action;
    private List<DiscoveryType> discoveryTypes = new ArrayList();
    private ServiceType serviceType = new ServiceType();
    private FieldList fields = new FieldList();
    private Map<String, Property> properties = new HashMap();

    public ActionInfo() {
        initialize();
    }

    public ActionInfo(Parcel parcel) {
        initialize();
        readFromParcel(parcel);
    }

    private Property getRelatedProperty(String str, Argument.Direction direction) {
        Property property;
        Argument argument;
        Iterator<Argument> it = this.action.getArguments().iterator();
        while (true) {
            property = null;
            if (!it.hasNext()) {
                argument = null;
                break;
            }
            argument = it.next();
            if (argument.getDirection() == direction && argument.getName().equals(str)) {
                break;
            }
        }
        if (argument == null) {
            Log.d(TAG, "argument not found: " + str);
        } else {
            property = this.properties.get(argument.getRelatedProperty());
            if (property == null) {
                Log.d(TAG, "Property not found: " + argument.getRelatedProperty());
            }
        }
        return property;
    }

    private void initialize() {
        this.fields.initField(ActionInfoDefinition.Address, null);
        this.fields.initField(ActionInfoDefinition.HostPort, null);
        this.fields.initField(ActionInfoDefinition.ControlUrl, null);
        this.fields.initField(ActionInfoDefinition.DeviceId, null);
        this.fields.initField(ActionInfoDefinition.ServiceId, null);
        this.fields.initField(ActionInfoDefinition.SessionId, null);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Action getAction() {
        return this.action;
    }

    public String getAddress() {
        return (String) this.fields.getValue(ActionInfoDefinition.Address);
    }

    public Property getArgument(String str) {
        return getRelatedProperty(str, Argument.Direction.IN);
    }

    public Object getArgumentValue(String str) {
        Property argument = getArgument(str);
        if (argument != null) {
            return argument.getCurrentValue();
        }
        return null;
    }

    public String getControlUrl() {
        return (String) this.fields.getValue(ActionInfoDefinition.ControlUrl);
    }

    public String getDeviceId() {
        return (String) this.fields.getValue(ActionInfoDefinition.DeviceId);
    }

    public List<DiscoveryType> getDiscoveryTypes() {
        return this.discoveryTypes;
    }

    public int getHostPort() {
        return ((Integer) this.fields.getValue(ActionInfoDefinition.HostPort)).intValue();
    }

    public Map<String, Property> getProperties() {
        return this.properties;
    }

    public Property getResult(String str) {
        return getRelatedProperty(str, Argument.Direction.OUT);
    }

    public String getServiceId() {
        return (String) this.fields.getValue(ActionInfoDefinition.ServiceId);
    }

    public ServiceType getServiceType() {
        return this.serviceType;
    }

    public String getSessionId() {
        return (String) this.fields.getValue(ActionInfoDefinition.SessionId);
    }

    public void readFromParcel(Parcel parcel) {
        this.action = (Action) parcel.readParcelable(Action.class.getClassLoader());
        this.serviceType = ServiceType.create(parcel.readString());
        this.fields = (FieldList) parcel.readParcelable(FieldList.class.getClassLoader());
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            DiscoveryType retrieveType = DiscoveryType.retrieveType(parcel.readString());
            if (!this.discoveryTypes.contains(retrieveType)) {
                this.discoveryTypes.add(retrieveType);
            }
        }
        int readInt2 = parcel.readInt();
        for (int i2 = 0; i2 < readInt2; i2++) {
            Property property = (Property) parcel.readParcelable(Property.class.getClassLoader());
            this.properties.put(property.getDefinition().getName(), property);
        }
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean setAddress(String str) {
        return this.fields.setValue(ActionInfoDefinition.Address, str);
    }

    public boolean setArgumentValue(String str, Object obj, Argument.Direction direction) {
        Property relatedProperty = getRelatedProperty(str, direction);
        if (relatedProperty == null) {
            Log.d(TAG, "relatedProperty not found: " + str);
            return false;
        }
        return relatedProperty.setDataValue(obj);
    }

    public boolean setArgumentValueByString(String str, String str2, boolean z, Argument.Direction direction) {
        Property relatedProperty = getRelatedProperty(str, direction);
        if (relatedProperty == null) {
            Log.d(TAG, "relatedProperty not found: " + str);
            return false;
        }
        return relatedProperty.setDataValueByString(str2, z);
    }

    public boolean setControlUrl(String str) {
        return this.fields.setValue(ActionInfoDefinition.ControlUrl, str);
    }

    public boolean setDeviceId(String str) {
        return this.fields.setValue(ActionInfoDefinition.DeviceId, str);
    }

    public void setDiscoveryTypes(List<DiscoveryType> list) {
        this.discoveryTypes = list;
    }

    public boolean setHostPort(int i) {
        return this.fields.setValue(ActionInfoDefinition.HostPort, Integer.valueOf(i));
    }

    public boolean setServiceId(String str) {
        return this.fields.setValue(ActionInfoDefinition.ServiceId, str);
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public boolean setSessionId(String str) {
        return this.fields.setValue(ActionInfoDefinition.SessionId, str);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.action, i);
        parcel.writeString(this.serviceType.toString());
        parcel.writeParcelable(this.fields, i);
        parcel.writeInt(this.discoveryTypes.size());
        Iterator<DiscoveryType> it = this.discoveryTypes.iterator();
        while (it.hasNext()) {
            parcel.writeString(it.next().toString());
        }
        parcel.writeInt(this.properties.size());
        Iterator<Property> it2 = this.properties.values().iterator();
        while (it2.hasNext()) {
            parcel.writeParcelable(it2.next(), i);
        }
    }
}
