package miui.vip;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes4.dex */
public class VipPhoneLevel implements Parcelable {
    public static final Parcelable.Creator<VipPhoneLevel> CREATOR = new Parcelable.Creator<VipPhoneLevel>() { // from class: miui.vip.VipPhoneLevel.1
        @Override // android.os.Parcelable.Creator
        public VipPhoneLevel createFromParcel(Parcel parcel) {
            VipPhoneLevel vipPhoneLevel = new VipPhoneLevel();
            vipPhoneLevel.phone = parcel.readString();
            vipPhoneLevel.mid = parcel.readString();
            vipPhoneLevel.midUpdateTime = parcel.readLong();
            vipPhoneLevel.level = parcel.readInt();
            vipPhoneLevel.levelUpdateTime = parcel.readLong();
            return vipPhoneLevel;
        }

        @Override // android.os.Parcelable.Creator
        public VipPhoneLevel[] newArray(int i) {
            return new VipPhoneLevel[i];
        }
    };
    public static final String UNKNOWN_ID = "-1";
    public static final int UNKNOWN_INT = -1;
    public String mid;
    public String phone;
    public long midUpdateTime = 0;
    public int level = 1;
    public long levelUpdateTime = 0;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.phone);
        parcel.writeString(this.mid);
        parcel.writeLong(this.midUpdateTime);
        parcel.writeInt(this.level);
        parcel.writeLong(this.levelUpdateTime);
    }
}
