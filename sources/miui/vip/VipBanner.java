package miui.vip;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes4.dex */
public class VipBanner implements Parcelable {
    public static final Parcelable.Creator<VipBanner> CREATOR = new Parcelable.Creator<VipBanner>() { // from class: miui.vip.VipBanner.1
        @Override // android.os.Parcelable.Creator
        public VipBanner createFromParcel(Parcel parcel) {
            return VipBanner.readFromParcel(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public VipBanner[] newArray(int i) {
            return new VipBanner[i];
        }
    };
    public String action;
    public String extraParams;
    public String icon;
    public long id;
    public String info;
    public String name;

    public static VipBanner readFromParcel(Parcel parcel) {
        VipBanner vipBanner = new VipBanner();
        vipBanner.id = parcel.readLong();
        vipBanner.icon = parcel.readString();
        vipBanner.name = parcel.readString();
        vipBanner.info = parcel.readString();
        vipBanner.action = parcel.readString();
        vipBanner.extraParams = parcel.readString();
        return vipBanner;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "VipBanner{id=" + this.id + ", icon='" + this.icon + "', name='" + this.name + "', info='" + this.info + "', action='" + this.action + "', extraParams='" + this.extraParams + "'}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.icon);
        parcel.writeString(this.name);
        parcel.writeString(this.info);
        parcel.writeString(this.action);
        parcel.writeString(this.extraParams);
    }
}
