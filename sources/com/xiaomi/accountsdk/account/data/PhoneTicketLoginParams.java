package com.xiaomi.accountsdk.account.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import miui.accounts.ExtraAccountManager;

/* loaded from: classes2.dex */
public class PhoneTicketLoginParams implements Parcelable {
    public static final Parcelable.Creator<PhoneTicketLoginParams> CREATOR = new Parcelable.Creator<PhoneTicketLoginParams>() { // from class: com.xiaomi.accountsdk.account.data.PhoneTicketLoginParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PhoneTicketLoginParams createFromParcel(Parcel parcel) {
            Bundle readBundle = parcel.readBundle();
            if (readBundle == null) {
                return null;
            }
            String string = readBundle.getString("phone");
            String string2 = readBundle.getString("ticket_token");
            ActivatorPhoneInfo activatorPhoneInfo = (ActivatorPhoneInfo) readBundle.getParcelable("activator_phone_info");
            return new Builder().phoneTicketToken(string, string2).verifiedActivatorPhone(activatorPhoneInfo).activatorPhoneTicket(activatorPhoneInfo, readBundle.getString("ticket")).deviceId(readBundle.getString("device_id")).serviceId(readBundle.getString(ExtraAccountManager.KEY_SERVICE_ID)).hashedEnvFactors(readBundle.getStringArray("hash_env")).returnStsUrl(readBundle.getBoolean("return_sts_url", false)).build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PhoneTicketLoginParams[] newArray(int i) {
            return new PhoneTicketLoginParams[0];
        }
    };
    public final ActivatorPhoneInfo activatorPhoneInfo;
    public final String activatorToken;
    public final String deviceId;
    public final String[] hashedEnvFactors;
    public final String phone;
    public final String phoneHash;
    public final boolean returnStsUrl;
    public final String serviceId;
    public final String ticket;
    public final String ticketToken;

    /* loaded from: classes2.dex */
    public static class Builder {
        private ActivatorPhoneInfo activatorPhoneInfo;
        private String deviceId;
        private String[] hashedEnvFactors;
        private String phone;
        private boolean returnStsUrl = false;
        private String serviceId;
        private String ticket;
        private String ticketToken;

        public Builder activatorPhoneTicket(ActivatorPhoneInfo activatorPhoneInfo, String str) {
            this.activatorPhoneInfo = activatorPhoneInfo;
            this.ticket = str;
            return this;
        }

        public PhoneTicketLoginParams build() {
            return new PhoneTicketLoginParams(this);
        }

        public Builder deviceId(String str) {
            this.deviceId = str;
            return this;
        }

        public Builder hashedEnvFactors(String[] strArr) {
            this.hashedEnvFactors = strArr;
            return this;
        }

        public Builder phoneTicketToken(String str, String str2) {
            this.phone = str;
            this.ticketToken = str2;
            return this;
        }

        public Builder returnStsUrl(boolean z) {
            this.returnStsUrl = z;
            return this;
        }

        public Builder serviceId(String str) {
            this.serviceId = str;
            return this;
        }

        public Builder verifiedActivatorPhone(ActivatorPhoneInfo activatorPhoneInfo) {
            this.activatorPhoneInfo = activatorPhoneInfo;
            return this;
        }
    }

    private PhoneTicketLoginParams(Builder builder) {
        this.phone = builder.phone;
        this.ticketToken = builder.ticketToken;
        ActivatorPhoneInfo activatorPhoneInfo = builder.activatorPhoneInfo;
        this.activatorPhoneInfo = activatorPhoneInfo;
        this.phoneHash = activatorPhoneInfo != null ? activatorPhoneInfo.phoneHash : null;
        this.activatorToken = activatorPhoneInfo != null ? activatorPhoneInfo.activatorToken : null;
        this.ticket = builder.ticket;
        this.deviceId = builder.deviceId;
        this.serviceId = builder.serviceId;
        this.hashedEnvFactors = builder.hashedEnvFactors;
        this.returnStsUrl = builder.returnStsUrl;
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
        bundle.putString("ticket", this.ticket);
        bundle.putString("device_id", this.deviceId);
        bundle.putString(ExtraAccountManager.KEY_SERVICE_ID, this.serviceId);
        bundle.putStringArray("hash_env", this.hashedEnvFactors);
        bundle.putBoolean("return_sts_url", this.returnStsUrl);
        parcel.writeBundle(bundle);
    }
}
