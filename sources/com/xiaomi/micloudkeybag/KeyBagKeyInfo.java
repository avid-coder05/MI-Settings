package com.xiaomi.micloudkeybag;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class KeyBagKeyInfo implements Parcelable {
    public static final Parcelable.Creator<KeyBagKeyInfo> CREATOR = new Parcelable.Creator<KeyBagKeyInfo>() { // from class: com.xiaomi.micloudkeybag.KeyBagKeyInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyBagKeyInfo createFromParcel(Parcel parcel) {
            return new KeyBagKeyInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyBagKeyInfo[] newArray(int i) {
            return new KeyBagKeyInfo[i];
        }
    };
    public final short tag;
    public final long version;

    protected KeyBagKeyInfo(Parcel parcel) {
        this.version = parcel.readLong();
        this.tag = (short) parcel.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "KeyBagKeyInfo{version=" + this.version + ", tag=" + ((int) this.tag) + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.version);
        parcel.writeInt(this.tag);
    }
}
