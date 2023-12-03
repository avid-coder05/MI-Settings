package com.xiaomi.accountsdk.account.data;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class NotificationLoginEndParams implements Parcelable {
    public static final Parcelable.Creator<NotificationLoginEndParams> CREATOR = new Parcelable.Creator<NotificationLoginEndParams>() { // from class: com.xiaomi.accountsdk.account.data.NotificationLoginEndParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationLoginEndParams createFromParcel(Parcel parcel) {
            return new NotificationLoginEndParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationLoginEndParams[] newArray(int i) {
            return new NotificationLoginEndParams[i];
        }
    };
    public final String passToken;
    public final String serviceId;
    public final String userId;

    public NotificationLoginEndParams(Parcel parcel) {
        this.userId = parcel.readString();
        this.passToken = parcel.readString();
        this.serviceId = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.userId);
        parcel.writeString(this.passToken);
        parcel.writeString(this.serviceId);
    }
}
