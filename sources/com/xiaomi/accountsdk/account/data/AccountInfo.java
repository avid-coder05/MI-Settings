package com.xiaomi.accountsdk.account.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class AccountInfo implements Parcelable {
    public static final Parcelable.Creator<AccountInfo> CREATOR = new Parcelable.Creator<AccountInfo>() { // from class: com.xiaomi.accountsdk.account.data.AccountInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccountInfo createFromParcel(Parcel parcel) {
            return new AccountInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccountInfo[] newArray(int i) {
            return new AccountInfo[i];
        }
    };
    public final String autoLoginUrl;
    public final String encryptedUserId;
    public final Boolean hasLocalChannel;
    public final boolean hasPwd;
    public final String passToken;
    public final String ph;
    public final String psecurity;
    public final String rePassToken;
    public final String security;
    public final String serviceId;
    public final String serviceToken;
    public final String slh;
    public final String userId;
    public final String userSyncedUrl;

    private AccountInfo(Parcel parcel) {
        this.userId = parcel.readString();
        this.serviceId = parcel.readString();
        this.passToken = parcel.readString();
        this.encryptedUserId = parcel.readString();
        this.serviceToken = parcel.readString();
        this.security = parcel.readString();
        this.psecurity = parcel.readString();
        this.autoLoginUrl = parcel.readString();
        this.rePassToken = parcel.readString();
        this.slh = parcel.readString();
        this.ph = parcel.readString();
        Bundle readBundle = parcel.readBundle();
        this.hasPwd = readBundle != null ? readBundle.getBoolean("has_pwd") : true;
        Boolean bool = null;
        this.userSyncedUrl = readBundle != null ? readBundle.getString("user_synced_url") : null;
        byte readByte = parcel.readByte();
        if (readByte != 0) {
            bool = Boolean.valueOf(readByte == 1);
        }
        this.hasLocalChannel = bool;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "AccountInfo{userId='" + this.userId + "', security='" + this.security + "'}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.userId);
        parcel.writeString(this.serviceId);
        parcel.writeString(this.passToken);
        parcel.writeString(this.encryptedUserId);
        parcel.writeString(this.serviceToken);
        parcel.writeString(this.security);
        parcel.writeString(this.psecurity);
        parcel.writeString(this.autoLoginUrl);
        parcel.writeString(this.rePassToken);
        parcel.writeString(this.slh);
        parcel.writeString(this.ph);
        Bundle bundle = new Bundle();
        bundle.putBoolean("has_pwd", this.hasPwd);
        bundle.putString("user_synced_url", this.userSyncedUrl);
        parcel.writeBundle(bundle);
        Boolean bool = this.hasLocalChannel;
        parcel.writeByte((byte) (bool == null ? 0 : bool.booleanValue() ? 1 : 2));
    }
}
