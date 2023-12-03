package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes4.dex */
public class PropertyChanged implements Parcelable {
    public static final Parcelable.Creator<PropertyChanged> CREATOR = new Parcelable.Creator<PropertyChanged>() { // from class: miui.upnp.typedef.device.PropertyChanged.1
        @Override // android.os.Parcelable.Creator
        public PropertyChanged createFromParcel(Parcel parcel) {
            return new PropertyChanged(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public PropertyChanged[] newArray(int i) {
            return new PropertyChanged[i];
        }
    };
    private String name;
    private String value;

    public PropertyChanged(Parcel parcel) {
        readFromParcel(parcel);
    }

    public PropertyChanged(String str, String str2) {
        this.name = str;
        this.value = str2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public void readFromParcel(Parcel parcel) {
        this.name = parcel.readString();
        this.value = parcel.readString();
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setValue(String str) {
        this.value = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.value);
    }
}
