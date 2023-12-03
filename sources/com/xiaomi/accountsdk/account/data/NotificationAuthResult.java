package com.xiaomi.accountsdk.account.data;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class NotificationAuthResult implements Parcelable {
    public static final Parcelable.Creator<NotificationAuthResult> CREATOR = new Parcelable.Creator<NotificationAuthResult>() { // from class: com.xiaomi.accountsdk.account.data.NotificationAuthResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationAuthResult createFromParcel(Parcel parcel) {
            return new NotificationAuthResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationAuthResult[] newArray(int i) {
            return new NotificationAuthResult[i];
        }
    };
    public String pSecurity_ph;
    public String pSecurity_slh;
    public String serviceToken;
    public String userId;

    public NotificationAuthResult(Parcel parcel) {
        this.userId = parcel.readString();
        this.serviceToken = parcel.readString();
        this.pSecurity_ph = parcel.readString();
        this.pSecurity_slh = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.userId);
        parcel.writeString(this.serviceToken);
        parcel.writeString(this.pSecurity_ph);
        parcel.writeString(this.pSecurity_slh);
    }
}
