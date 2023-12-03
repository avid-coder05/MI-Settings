package com.xiaomi.accountsdk.account.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class PasswordLoginParams implements Parcelable {
    public static final Parcelable.Creator<PasswordLoginParams> CREATOR = new Parcelable.Creator<PasswordLoginParams>() { // from class: com.xiaomi.accountsdk.account.data.PasswordLoginParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PasswordLoginParams createFromParcel(Parcel parcel) {
            return new PasswordLoginParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PasswordLoginParams[] newArray(int i) {
            return new PasswordLoginParams[i];
        }
    };
    public ActivatorPhoneInfo activatorPhoneInfo;
    public final String captCode;
    public final String captIck;
    public String countryCode;
    public String deviceId;
    public String[] hashedEnvFactors;
    public MetaLoginData metaLoginData;
    public boolean needProcessNotification;
    public final String password;
    public boolean returnStsUrl;
    public final String serviceId;
    public String ticketToken;
    public final String userId;
    public final String verifyToken;

    public PasswordLoginParams(Parcel parcel) {
        this.userId = parcel.readString();
        this.password = parcel.readString();
        this.serviceId = parcel.readString();
        this.verifyToken = parcel.readString();
        this.captCode = parcel.readString();
        this.captIck = parcel.readString();
        Bundle readBundle = parcel.readBundle();
        if (readBundle != null) {
            this.deviceId = readBundle.getString("deviceId");
            this.ticketToken = readBundle.getString("ticketToken");
            this.metaLoginData = (MetaLoginData) readBundle.getParcelable("metaLoginData");
            this.returnStsUrl = readBundle.getBoolean("returnStsUrl", false);
            this.needProcessNotification = readBundle.getBoolean("needProcessNotification", true);
            this.hashedEnvFactors = readBundle.getStringArray("hashedEnvFactors");
            this.activatorPhoneInfo = (ActivatorPhoneInfo) readBundle.getParcelable("activatorPhoneInfo");
            this.countryCode = readBundle.getString("countryCode");
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.userId);
        parcel.writeString(this.password);
        parcel.writeString(this.serviceId);
        parcel.writeString(this.verifyToken);
        parcel.writeString(this.captCode);
        parcel.writeString(this.captIck);
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", this.deviceId);
        bundle.putString("ticketToken", this.ticketToken);
        bundle.putParcelable("metaLoginData", this.metaLoginData);
        bundle.putBoolean("returnStsUrl", this.returnStsUrl);
        bundle.putBoolean("needProcessNotification", this.needProcessNotification);
        bundle.putStringArray("hashedEnvFactors", this.hashedEnvFactors);
        bundle.putParcelable("activatorPhoneInfo", this.activatorPhoneInfo);
        bundle.putString("countryCode", this.countryCode);
        parcel.writeBundle(bundle);
    }
}
