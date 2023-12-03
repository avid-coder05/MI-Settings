package com.xiaomi.accountsdk.account;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class ServerError implements Parcelable {
    public static final Parcelable.Creator<ServerError> CREATOR = new Parcelable.Creator<ServerError>() { // from class: com.xiaomi.accountsdk.account.ServerError.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServerError createFromParcel(Parcel parcel) {
            return new ServerError(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServerError[] newArray(int i) {
            return new ServerError[i];
        }
    };
    private String tips;
    private String title;

    private ServerError(Parcel parcel) {
        this.title = parcel.readString();
        this.tips = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.tips);
    }
}
