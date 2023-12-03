package miui.vip;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes4.dex */
public class VipAchievement implements Parcelable {
    public static final Parcelable.Creator<VipAchievement> CREATOR = new Parcelable.Creator<VipAchievement>() { // from class: miui.vip.VipAchievement.1
        @Override // android.os.Parcelable.Creator
        public VipAchievement createFromParcel(Parcel parcel) {
            return VipAchievement.readFromParcel(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public VipAchievement[] newArray(int i) {
            return new VipAchievement[i];
        }
    };
    public long id;
    public boolean isOwned;
    public String name;
    public String url;

    public static VipAchievement readFromParcel(Parcel parcel) {
        VipAchievement vipAchievement = new VipAchievement();
        vipAchievement.id = parcel.readLong();
        vipAchievement.name = parcel.readString();
        vipAchievement.url = parcel.readString();
        vipAchievement.isOwned = parcel.readInt() == 1;
        return vipAchievement;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
        parcel.writeString(this.url);
        parcel.writeInt(this.isOwned ? 1 : 0);
    }
}
