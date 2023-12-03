package com.xiaomi.accountsdk.account.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class Step2LoginParams implements Parcelable {
    public static final Parcelable.Creator<Step2LoginParams> CREATOR = new Parcelable.Creator<Step2LoginParams>() { // from class: com.xiaomi.accountsdk.account.data.Step2LoginParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Step2LoginParams createFromParcel(Parcel parcel) {
            return new Step2LoginParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Step2LoginParams[] newArray(int i) {
            return new Step2LoginParams[i];
        }
    };
    public String deviceId;
    public final MetaLoginData metaLoginData;
    public boolean returnStsUrl;
    public final String serviceId;
    public final String step1Token;
    public final String step2code;
    public final boolean trust;
    public final String userId;

    public Step2LoginParams(Parcel parcel) {
        this.userId = parcel.readString();
        this.serviceId = parcel.readString();
        this.step1Token = parcel.readString();
        this.step2code = parcel.readString();
        this.trust = parcel.readInt() != 0;
        this.metaLoginData = (MetaLoginData) parcel.readParcelable(MetaLoginData.class.getClassLoader());
        Bundle readBundle = parcel.readBundle();
        if (readBundle != null) {
            this.returnStsUrl = readBundle.getBoolean("returnStsUrl", false);
            this.deviceId = readBundle.getString("deviceId");
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.userId);
        parcel.writeString(this.serviceId);
        parcel.writeString(this.step1Token);
        parcel.writeString(this.step2code);
        parcel.writeInt(this.trust ? 1 : 0);
        parcel.writeParcelable(this.metaLoginData, i);
        Bundle bundle = new Bundle();
        bundle.putBoolean("returnStsUrl", this.returnStsUrl);
        bundle.putString("deviceId", this.deviceId);
        parcel.writeBundle(bundle);
    }
}
