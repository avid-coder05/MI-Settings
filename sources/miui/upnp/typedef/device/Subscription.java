package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.field.FieldList;

/* loaded from: classes4.dex */
public class Subscription implements Parcelable {
    public static final Parcelable.Creator<Subscription> CREATOR = new Parcelable.Creator<Subscription>() { // from class: miui.upnp.typedef.device.Subscription.1
        @Override // android.os.Parcelable.Creator
        public Subscription createFromParcel(Parcel parcel) {
            return new Subscription(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Subscription[] newArray(int i) {
            return new Subscription[i];
        }
    };
    private FieldList fields = new FieldList();
    private Service service;

    public Subscription() {
        initialize();
    }

    public Subscription(Parcel parcel) {
        readFromParcel(parcel);
    }

    private void initialize() {
        this.fields.initField(SubscriptionDefinition.CallbackUrl, null);
        this.fields.initField(SubscriptionDefinition.SubscriptionId, null);
        this.fields.initField(SubscriptionDefinition.Timeout, null);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getCallbackUrl() {
        return (String) this.fields.getValue(SubscriptionDefinition.CallbackUrl);
    }

    public Service getService() {
        return this.service;
    }

    public String getSubscriptionId() {
        return (String) this.fields.getValue(SubscriptionDefinition.SubscriptionId);
    }

    public int getTimeout() {
        return ((Integer) this.fields.getValue(SubscriptionDefinition.Timeout)).intValue();
    }

    public void readFromParcel(Parcel parcel) {
        this.fields = (FieldList) parcel.readParcelable(FieldList.class.getClassLoader());
    }

    public boolean setCallbackUrl(String str) {
        return this.fields.setValue(SubscriptionDefinition.CallbackUrl, str);
    }

    public void setService(Service service) {
        this.service = service;
    }

    public boolean setSubscriptionId(String str) {
        return this.fields.setValue(SubscriptionDefinition.SubscriptionId, str);
    }

    public boolean setTimeout(int i) {
        return this.fields.setValue(SubscriptionDefinition.Timeout, Integer.valueOf(i));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.fields, i);
    }
}
