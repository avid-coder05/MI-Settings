package com.xiaomi.accountsdk.account.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import miui.accounts.ExtraAccountManager;

/* loaded from: classes2.dex */
public class PhoneTokenRegisterParams implements Parcelable {
    public static final Parcelable.Creator<PhoneTokenRegisterParams> CREATOR = new Parcelable.Creator<PhoneTokenRegisterParams>() { // from class: com.xiaomi.accountsdk.account.data.PhoneTokenRegisterParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PhoneTokenRegisterParams createFromParcel(Parcel parcel) {
            Bundle readBundle = parcel.readBundle();
            if (readBundle == null) {
                return null;
            }
            String string = readBundle.getString("phone");
            String string2 = readBundle.getString("password");
            String string3 = readBundle.getString("ticket_token");
            ActivatorPhoneInfo activatorPhoneInfo = (ActivatorPhoneInfo) readBundle.getParcelable("activator_phone_info");
            String string4 = readBundle.getString("region");
            return new Builder().phoneTicketToken(string, string3).phoneHashActivatorToken(activatorPhoneInfo).password(string2).region(string4).serviceId(readBundle.getString(ExtraAccountManager.KEY_SERVICE_ID)).build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PhoneTokenRegisterParams[] newArray(int i) {
            return new PhoneTokenRegisterParams[0];
        }
    };
    public final ActivatorPhoneInfo activatorPhoneInfo;
    public final String activatorToken;
    public final boolean noPwd;
    public final String password;
    public final String phone;
    public final String phoneHash;
    public final String region;
    public final String serviceId;
    public final String ticketToken;

    /* loaded from: classes2.dex */
    public static class Builder {
        private ActivatorPhoneInfo activatorPhoneInfo;
        private boolean noPwd;
        private String password;
        private String phone;
        private String region;
        private String serviceId;
        private String ticketToken;

        public PhoneTokenRegisterParams build() {
            this.noPwd = TextUtils.isEmpty(this.password);
            return new PhoneTokenRegisterParams(this);
        }

        public Builder password(String str) {
            this.password = str;
            return this;
        }

        public Builder phoneHashActivatorToken(ActivatorPhoneInfo activatorPhoneInfo) {
            this.activatorPhoneInfo = activatorPhoneInfo;
            return this;
        }

        public Builder phoneTicketToken(String str, String str2) {
            this.phone = str;
            this.ticketToken = str2;
            return this;
        }

        public Builder region(String str) {
            this.region = str;
            return this;
        }

        public Builder serviceId(String str) {
            this.serviceId = str;
            return this;
        }
    }

    private PhoneTokenRegisterParams(Builder builder) {
        this.phone = builder.phone;
        this.ticketToken = builder.ticketToken;
        ActivatorPhoneInfo activatorPhoneInfo = builder.activatorPhoneInfo;
        this.activatorPhoneInfo = activatorPhoneInfo;
        this.phoneHash = activatorPhoneInfo != null ? activatorPhoneInfo.phoneHash : null;
        this.activatorToken = activatorPhoneInfo != null ? activatorPhoneInfo.activatorToken : null;
        this.password = builder.password;
        this.noPwd = builder.noPwd;
        this.region = builder.region;
        this.serviceId = builder.serviceId;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("phone", this.phone);
        bundle.putString("ticket_token", this.ticketToken);
        bundle.putParcelable("activator_phone_info", this.activatorPhoneInfo);
        bundle.putString("password", this.password);
        bundle.putString("region", this.region);
        bundle.putBoolean("is_no_password", this.noPwd);
        bundle.putString("password", this.password);
        bundle.putString("region", this.region);
        bundle.putString(ExtraAccountManager.KEY_SERVICE_ID, this.serviceId);
        parcel.writeBundle(bundle);
    }
}
