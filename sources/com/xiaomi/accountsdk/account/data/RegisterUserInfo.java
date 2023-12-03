package com.xiaomi.accountsdk.account.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.xiaomi.accountsdk.utils.AccountLog;
import miui.content.ExtraIntent;

/* loaded from: classes2.dex */
public class RegisterUserInfo implements Parcelable {
    public static final Parcelable.Creator<RegisterUserInfo> CREATOR = new Parcelable.Creator<RegisterUserInfo>() { // from class: com.xiaomi.accountsdk.account.data.RegisterUserInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RegisterUserInfo createFromParcel(Parcel parcel) {
            Bundle readBundle = parcel.readBundle();
            if (readBundle == null) {
                return null;
            }
            return new Builder(readBundle.getInt("register_status")).userId(readBundle.getString(ExtraIntent.EXTRA_XIAOMI_ACCOUNT_USER_ID)).userName(readBundle.getString(ExtraIntent.EXTRA_XIAOMI_ACCOUNT_NAME)).avatarAddress(readBundle.getString("avatar_address")).ticketToken(readBundle.getString("ticket_token")).phone(readBundle.getString("phone")).maskedUserId(readBundle.getString("masked_user_id")).hasPwd(readBundle.getBoolean("has_pwd")).bindTime(readBundle.getLong("bind_time")).needToast(readBundle.getBoolean("need_toast")).needGetActiveTime(readBundle.getBoolean("need_get_active_time")).registerPwd(readBundle.getBoolean("register_pwd")).tmpPhoneToken(readBundle.getString("tmp_phone_token")).build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RegisterUserInfo[] newArray(int i) {
            return new RegisterUserInfo[0];
        }
    };
    @Deprecated
    public static final int STATUS_NOT_REGISTERED = 0;
    @Deprecated
    public static final int STATUS_USED_NOT_RECYCLED = 2;
    @Deprecated
    public static final int STATUS_USED_POSSIBLY_RECYCLED = 1;
    public final String avatarAddress;
    public final long bindTime;
    public final boolean hasPwd;
    public final String maskedUserId;
    public final boolean needGetActiveTime;
    public final boolean needToast;
    public final String phone;
    public final boolean registerPwd;
    public final RegisterStatus status;
    public final String ticketToken;
    public final String tmpPhoneToken;
    public final String userId;
    public final String userName;

    /* loaded from: classes2.dex */
    public static class Builder {
        private String avatarAddress;
        private long bindTime;
        private boolean hasPwd;
        private String maskedUserId;
        private boolean needGetActiveTime;
        private boolean needToast;
        private String phone;
        private boolean registerPwd;
        private int status;
        private String ticketToken;
        private String tmpPhoneToken;
        private String userId;
        private String userName;

        public Builder(int i) {
            this.status = i;
        }

        public Builder avatarAddress(String str) {
            this.avatarAddress = str;
            return this;
        }

        public Builder bindTime(long j) {
            this.bindTime = j;
            return this;
        }

        public RegisterUserInfo build() {
            return new RegisterUserInfo(this);
        }

        public Builder hasPwd(boolean z) {
            this.hasPwd = z;
            return this;
        }

        public Builder maskedUserId(String str) {
            this.maskedUserId = str;
            return this;
        }

        public Builder needGetActiveTime(boolean z) {
            this.needGetActiveTime = z;
            return this;
        }

        public Builder needToast(boolean z) {
            this.needToast = z;
            return this;
        }

        public Builder phone(String str) {
            this.phone = str;
            return this;
        }

        public Builder registerPwd(boolean z) {
            this.registerPwd = z;
            return this;
        }

        public Builder ticketToken(String str) {
            this.ticketToken = str;
            return this;
        }

        public Builder tmpPhoneToken(String str) {
            this.tmpPhoneToken = str;
            return this;
        }

        public Builder userId(String str) {
            this.userId = str;
            return this;
        }

        public Builder userName(String str) {
            this.userName = str;
            return this;
        }
    }

    /* loaded from: classes2.dex */
    public enum RegisterStatus {
        STATUS_NOT_REGISTERED(0),
        STATUS_USED_POSSIBLY_RECYCLED(1),
        STATUS_REGISTERED_NOT_RECYCLED(2);

        public final int value;

        RegisterStatus(int i) {
            this.value = i;
        }

        public static RegisterStatus getInstance(int i) {
            for (RegisterStatus registerStatus : values()) {
                if (i == registerStatus.value) {
                    return registerStatus;
                }
            }
            AccountLog.w("RegisterStatus", "has not this status value: " + i);
            return null;
        }
    }

    private RegisterUserInfo(Builder builder) {
        this.status = RegisterStatus.getInstance(builder.status);
        this.userId = builder.userId;
        this.userName = builder.userName;
        this.avatarAddress = builder.avatarAddress;
        this.ticketToken = builder.ticketToken;
        this.phone = builder.phone;
        this.maskedUserId = builder.maskedUserId;
        this.hasPwd = builder.hasPwd;
        this.bindTime = builder.bindTime;
        this.needGetActiveTime = builder.needGetActiveTime;
        this.needToast = builder.needToast;
        this.registerPwd = builder.registerPwd;
        this.tmpPhoneToken = builder.tmpPhoneToken;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("register_status", this.status.value);
        bundle.putString(ExtraIntent.EXTRA_XIAOMI_ACCOUNT_USER_ID, this.userId);
        bundle.putString(ExtraIntent.EXTRA_XIAOMI_ACCOUNT_NAME, this.userName);
        bundle.putString("avatar_address", this.avatarAddress);
        bundle.putString("ticket_token", this.ticketToken);
        bundle.putString("phone", this.phone);
        bundle.putString("masked_user_id", this.maskedUserId);
        bundle.putBoolean("has_pwd", this.hasPwd);
        bundle.putLong("bind_time", this.bindTime);
        bundle.putBoolean("need_toast", this.needToast);
        bundle.putBoolean("need_get_active_time", this.needGetActiveTime);
        bundle.putBoolean("register_pwd", this.registerPwd);
        bundle.putString("tmp_phone_token", this.tmpPhoneToken);
        parcel.writeBundle(bundle);
    }
}
