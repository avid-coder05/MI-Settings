package com.xiaomi.accountsdk.account.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.xiaomi.accountsdk.account.ServerError;

/* loaded from: classes2.dex */
public class MiLoginResult implements Parcelable {
    public static final Parcelable.Creator<MiLoginResult> CREATOR = new Parcelable.Creator<MiLoginResult>() { // from class: com.xiaomi.accountsdk.account.data.MiLoginResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MiLoginResult createFromParcel(Parcel parcel) {
            return new MiLoginResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MiLoginResult[] newArray(int i) {
            return new MiLoginResult[i];
        }
    };
    public static final int ERROR_ACCESS_DENIED = 7;
    public static final int ERROR_CAPTCHA = 1;
    public static final int ERROR_ILLEGAL_DEVICE_ID = 9;
    public static final int ERROR_NEED_NOTIFICATION = 3;
    public static final int ERROR_NEED_STEP2_LOGIN = 2;
    public static final int ERROR_NETWORK = 5;
    public static final int ERROR_PASSWORD = 4;
    public static final int ERROR_REMOTE_FATAL_ERROR = 13;
    public static final int ERROR_SERVER = 6;
    public static final int ERROR_SSL = 10;
    @Deprecated
    public static final int ERROR_SSL_HAND_SHAKE = 10;
    public static final int ERROR_STEP2_CODE = 11;
    public static final int ERROR_UNKNOWN = 12;
    public static final int ERROR_USER_NAME = 8;
    public static final int SUCCESS = 0;
    public final AccountInfo accountInfo;
    public final String captchaType;
    public final String captchaUrl;
    public final boolean hasPwd;
    public boolean isStsCallbackError;
    public final MetaLoginData metaLoginData;
    public final String notificationUrl;
    public final int resultCode;
    public ServerError serverError;
    public final String serviceId;
    public final String step1Token;
    public final String userId;

    public MiLoginResult(Parcel parcel) {
        this.userId = parcel.readString();
        this.serviceId = parcel.readString();
        this.accountInfo = (AccountInfo) parcel.readParcelable(AccountInfo.class.getClassLoader());
        this.captchaUrl = parcel.readString();
        this.captchaType = parcel.readString();
        this.notificationUrl = parcel.readString();
        this.metaLoginData = (MetaLoginData) parcel.readParcelable(MetaLoginData.class.getClassLoader());
        this.step1Token = parcel.readString();
        this.resultCode = parcel.readInt();
        Bundle readBundle = parcel.readBundle();
        this.hasPwd = readBundle != null ? readBundle.getBoolean("has_pwd") : true;
        this.isStsCallbackError = readBundle != null ? readBundle.getBoolean("sts_error") : false;
        this.serverError = (ServerError) parcel.readParcelable(ServerError.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.userId);
        parcel.writeString(this.serviceId);
        parcel.writeParcelable(this.accountInfo, i);
        parcel.writeString(this.captchaUrl);
        parcel.writeString(this.captchaType);
        parcel.writeString(this.notificationUrl);
        parcel.writeParcelable(this.metaLoginData, i);
        parcel.writeString(this.step1Token);
        parcel.writeInt(this.resultCode);
        Bundle bundle = new Bundle();
        bundle.putBoolean("has_pwd", this.hasPwd);
        bundle.putBoolean("sts_error", this.isStsCallbackError);
        parcel.writeBundle(bundle);
        parcel.writeParcelable(this.serverError, i);
    }
}
