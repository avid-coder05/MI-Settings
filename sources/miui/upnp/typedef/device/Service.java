package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import miui.upnp.typedef.device.urn.ServiceType;
import miui.upnp.typedef.field.FieldList;
import miui.upnp.typedef.property.Property;
import miui.upnp.typedef.property.PropertyDefinition;

/* loaded from: classes4.dex */
public class Service implements Parcelable {
    private Device device;
    private static final String TAG = Service.class.getSimpleName();
    public static final Parcelable.Creator<Service> CREATOR = new Parcelable.Creator<Service>() { // from class: miui.upnp.typedef.device.Service.1
        @Override // android.os.Parcelable.Creator
        public Service createFromParcel(Parcel parcel) {
            return new Service(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Service[] newArray(int i) {
            return new Service[i];
        }
    };
    private ServiceType type = new ServiceType();
    private FieldList fields = new FieldList();
    private Map<String, Action> actions = new HashMap();
    private volatile Map<String, Property> properties = new HashMap();
    private Map<String, Subscription> subscribers = new HashMap();

    public Service() {
        initialize();
    }

    public Service(Parcel parcel) {
        readFromParcel(parcel);
    }

    public Service(ServiceType serviceType) {
        initialize();
        setType(serviceType);
    }

    private void initialize() {
        this.fields.initField(ServiceDefinition.ServiceId, null);
        this.fields.initField(ServiceDefinition.ControlUrl, null);
        this.fields.initField(ServiceDefinition.EventSubUrl, null);
        this.fields.initField(ServiceDefinition.ScpdUrl, null);
        this.fields.initField(ServiceDefinition.Subscribed, null);
        this.fields.initField(ServiceDefinition.SubscriptionId, null);
    }

    public void addAction(Action action) {
        action.setService(this);
        this.actions.put(action.getName(), action);
    }

    public void addProperty(Property property) {
        this.properties.put(property.getDefinition().getName(), property);
    }

    public void addProperty(PropertyDefinition propertyDefinition) {
        this.properties.put(propertyDefinition.getName(), new Property(propertyDefinition, null));
    }

    public void addSubscription(Subscription subscription) {
        subscription.setService(this);
        this.subscribers.put(subscription.getCallbackUrl(), subscription);
    }

    public void cleanPropertyState() {
        for (Property property : this.properties.values()) {
            if (property.getDefinition().isSendEvents()) {
                property.cleanState();
            }
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Map<String, Action> getActions() {
        return this.actions;
    }

    public String getControlUrl() {
        return (String) this.fields.getValue(ServiceDefinition.ControlUrl);
    }

    public Device getDevice() {
        return this.device;
    }

    public String getEventSubUrl() {
        return (String) this.fields.getValue(ServiceDefinition.EventSubUrl);
    }

    public Collection<Property> getProperties() {
        return this.properties.values();
    }

    public Property getProperty(String str) {
        return this.properties.get(str);
    }

    public PropertyDefinition getPropertyDefinition(String str) {
        Property property = this.properties.get(str);
        if (property == null) {
            return null;
        }
        return property.getDefinition();
    }

    public Object getPropertyValue(String str) {
        Property property = this.properties.get(str);
        Object currentValue = property == null ? null : property.getCurrentValue();
        Log.d(TAG, "getPropertyValue name:" + str + " value:" + currentValue);
        return currentValue;
    }

    public String getScpdUrl() {
        return (String) this.fields.getValue(ServiceDefinition.ScpdUrl);
    }

    public String getServiceId() {
        return (String) this.fields.getValue(ServiceDefinition.ServiceId);
    }

    public Map<String, Subscription> getSubscribers() {
        return this.subscribers;
    }

    public String getSubscriptionId() {
        return (String) this.fields.getValue(ServiceDefinition.SubscriptionId);
    }

    public ServiceType getType() {
        return this.type;
    }

    public boolean isPropertyChanged() {
        for (Property property : this.properties.values()) {
            if (property.getDefinition().isSendEvents() && property.isChanged()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSubscribed() {
        return ((Boolean) this.fields.getValue(ServiceDefinition.Subscribed)).booleanValue();
    }

    public void readFromParcel(Parcel parcel) {
        this.type = ServiceType.create(parcel.readString());
        this.fields = (FieldList) parcel.readParcelable(FieldList.class.getClassLoader());
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            addAction((Action) parcel.readParcelable(Action.class.getClassLoader()));
        }
        int readInt2 = parcel.readInt();
        for (int i2 = 0; i2 < readInt2; i2++) {
            addProperty((Property) parcel.readParcelable(Property.class.getClassLoader()));
        }
        int readInt3 = parcel.readInt();
        for (int i3 = 0; i3 < readInt3; i3++) {
            addSubscription((Subscription) parcel.readParcelable(Subscription.class.getClassLoader()));
        }
    }

    public void removeSubscription(String str) {
        this.subscribers.remove(str);
    }

    public boolean setControlUrl(String str) {
        return this.fields.setValue(ServiceDefinition.ControlUrl, str);
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean setEventSubUrl(String str) {
        return this.fields.setValue(ServiceDefinition.EventSubUrl, str);
    }

    public boolean setPropertyValue(String str, Object obj) {
        Property property = this.properties.get(str);
        boolean dataValue = property == null ? false : property.setDataValue(obj);
        Log.d(TAG, "setPropertyValue name:" + str + " value:" + obj + " ret:" + dataValue);
        return dataValue;
    }

    public boolean setScpdUrl(String str) {
        return this.fields.setValue(ServiceDefinition.ScpdUrl, str);
    }

    public boolean setServiceId(String str) {
        return this.fields.setValue(ServiceDefinition.ServiceId, str);
    }

    public boolean setSubscribed(boolean z) {
        return this.fields.setValue(ServiceDefinition.Subscribed, Boolean.valueOf(z));
    }

    public boolean setSubscriptionId(String str) {
        return this.fields.setValue(ServiceDefinition.SubscriptionId, str);
    }

    public void setType(ServiceType serviceType) {
        this.type = serviceType;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.type.toString());
        parcel.writeParcelable(this.fields, i);
        parcel.writeInt(this.actions.size());
        Iterator<Action> it = this.actions.values().iterator();
        while (it.hasNext()) {
            parcel.writeParcelable(it.next(), i);
        }
        parcel.writeInt(this.properties.size());
        Iterator<Property> it2 = this.properties.values().iterator();
        while (it2.hasNext()) {
            parcel.writeParcelable(it2.next(), i);
        }
        parcel.writeInt(this.subscribers.size());
        Iterator<Subscription> it3 = this.subscribers.values().iterator();
        while (it3.hasNext()) {
            parcel.writeParcelable(it3.next(), i);
        }
    }
}
