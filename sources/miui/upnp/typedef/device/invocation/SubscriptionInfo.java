package miui.upnp.typedef.device.invocation;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.upnp.typedef.device.DiscoveryType;
import miui.upnp.typedef.field.FieldList;

/* loaded from: classes4.dex */
public class SubscriptionInfo implements Parcelable {
    public static final Parcelable.Creator<SubscriptionInfo> CREATOR = new Parcelable.Creator<SubscriptionInfo>() { // from class: miui.upnp.typedef.device.invocation.SubscriptionInfo.1
        @Override // android.os.Parcelable.Creator
        public SubscriptionInfo createFromParcel(Parcel parcel) {
            return new SubscriptionInfo(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SubscriptionInfo[] newArray(int i) {
            return new SubscriptionInfo[i];
        }
    };
    private List<DiscoveryType> discoveryTypes = new ArrayList();
    private FieldList fields = new FieldList();

    public SubscriptionInfo() {
        initialize();
    }

    public SubscriptionInfo(Parcel parcel) {
        initialize();
        readFromParcel(parcel);
    }

    private void initialize() {
        this.fields.initField(SubscriptionInfoDefinition.HostAddress, null);
        this.fields.initField(SubscriptionInfoDefinition.HostPort, null);
        this.fields.initField(SubscriptionInfoDefinition.DeviceId, null);
        this.fields.initField(SubscriptionInfoDefinition.ServiceId, null);
        this.fields.initField(SubscriptionInfoDefinition.EventSubUrl, null);
        this.fields.initField(SubscriptionInfoDefinition.SubscriptionId, null);
        this.fields.initField(SubscriptionInfoDefinition.Subscribed, null);
        this.fields.initField(SubscriptionInfoDefinition.Timeout, null);
        this.fields.initField(SubscriptionInfoDefinition.SessionId, null);
        this.fields.initField(SubscriptionInfoDefinition.CallbackUrl, null);
        this.fields.initField(SubscriptionInfoDefinition.CpAddress, null);
        this.fields.initField(SubscriptionInfoDefinition.CpPort, null);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getCallbackUrl() {
        return (String) this.fields.getValue(SubscriptionInfoDefinition.CallbackUrl);
    }

    public String getCpAddress() {
        return (String) this.fields.getValue(SubscriptionInfoDefinition.CpAddress);
    }

    public int getCpPort() {
        return ((Integer) this.fields.getValue(SubscriptionInfoDefinition.CpPort)).intValue();
    }

    public String getDeviceId() {
        return (String) this.fields.getValue(SubscriptionInfoDefinition.DeviceId);
    }

    public List<DiscoveryType> getDiscoveryTypes() {
        return this.discoveryTypes;
    }

    public String getEventSubUrl() {
        return (String) this.fields.getValue(SubscriptionInfoDefinition.EventSubUrl);
    }

    public String getFullEventSubUrl() {
        return String.format("http://%s:%d%s", getHostAddress(), Integer.valueOf(getHostPort()), getEventSubUrl());
    }

    public String getHost() {
        return String.format("%s:%d", getHostAddress(), Integer.valueOf(getHostPort()));
    }

    public String getHostAddress() {
        return (String) this.fields.getValue(SubscriptionInfoDefinition.HostAddress);
    }

    public int getHostPort() {
        return ((Integer) this.fields.getValue(SubscriptionInfoDefinition.HostPort)).intValue();
    }

    public String getServiceId() {
        return (String) this.fields.getValue(SubscriptionInfoDefinition.ServiceId);
    }

    public String getSessionId() {
        return (String) this.fields.getValue(SubscriptionInfoDefinition.SessionId);
    }

    public String getSubscriptionId() {
        return (String) this.fields.getValue(SubscriptionInfoDefinition.SubscriptionId);
    }

    public int getTimeout() {
        return ((Integer) this.fields.getValue(SubscriptionInfoDefinition.Timeout)).intValue();
    }

    public boolean isSubscribed() {
        return ((Boolean) this.fields.getValue(SubscriptionInfoDefinition.Subscribed)).booleanValue();
    }

    public void readFromParcel(Parcel parcel) {
        this.fields = (FieldList) parcel.readParcelable(FieldList.class.getClassLoader());
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.discoveryTypes.add(DiscoveryType.retrieveType(parcel.readString()));
        }
    }

    public boolean setCallbackUrl(String str) {
        return this.fields.setValue(SubscriptionInfoDefinition.CallbackUrl, str);
    }

    public boolean setCpAddress(String str) {
        return this.fields.setValue(SubscriptionInfoDefinition.CpAddress, str);
    }

    public boolean setCpPort(int i) {
        return this.fields.setValue(SubscriptionInfoDefinition.CpPort, Integer.valueOf(i));
    }

    public boolean setDeviceId(String str) {
        return this.fields.setValue(SubscriptionInfoDefinition.DeviceId, str);
    }

    public void setDiscoveryTypes(List<DiscoveryType> list) {
        this.discoveryTypes = list;
    }

    public boolean setEventSubUrl(String str) {
        return this.fields.setValue(SubscriptionInfoDefinition.EventSubUrl, str);
    }

    public boolean setHostAddress(String str) {
        return this.fields.setValue(SubscriptionInfoDefinition.HostAddress, str);
    }

    public boolean setHostPort(int i) {
        return this.fields.setValue(SubscriptionInfoDefinition.HostPort, Integer.valueOf(i));
    }

    public boolean setServiceId(String str) {
        return this.fields.setValue(SubscriptionInfoDefinition.ServiceId, str);
    }

    public boolean setSessionId(String str) {
        return this.fields.setValue(SubscriptionInfoDefinition.SessionId, str);
    }

    public boolean setSubscribed(boolean z) {
        return this.fields.setValue(SubscriptionInfoDefinition.Subscribed, Boolean.valueOf(z));
    }

    public boolean setSubscriptionId(String str) {
        return this.fields.setValue(SubscriptionInfoDefinition.SubscriptionId, str);
    }

    public boolean setTimeout(int i) {
        return this.fields.setValue(SubscriptionInfoDefinition.Timeout, Integer.valueOf(i));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.fields, i);
        parcel.writeInt(this.discoveryTypes.size());
        Iterator<DiscoveryType> it = this.discoveryTypes.iterator();
        while (it.hasNext()) {
            parcel.writeString(it.next().toString());
        }
    }
}
