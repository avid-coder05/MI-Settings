package com.xiaomi.accountsdk.service;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class DeviceInfoResult implements Parcelable {
    public static final String BUNDLE_KEY_ANDROID_ID = "android_id";
    public static final String BUNDLE_KEY_HASHED_DEVICE_ID = "hashed_device_id";
    public static final Parcelable.Creator<DeviceInfoResult> CREATOR = new Parcelable.Creator<DeviceInfoResult>() { // from class: com.xiaomi.accountsdk.service.DeviceInfoResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DeviceInfoResult createFromParcel(Parcel parcel) {
            return new DeviceInfoResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DeviceInfoResult[] newArray(int i) {
            return new DeviceInfoResult[i];
        }
    };
    public static final int FLAG_ANDROID_ID = 2;
    public static final int FLAG_HASHED_DEVICE_ID = 1;
    public final Bundle deviceInfo;
    public final ErrorCode errorCode;
    public final String errorMessage;
    public final String errorStackTrace;

    /* loaded from: classes2.dex */
    public enum ErrorCode {
        ERROR_UNKNOWN,
        ERROR_NONE,
        ERROR_APP_PERMISSION_FORBIDDEN,
        ERROR_TIME_OUT,
        ERROR_NOT_SUPPORTED,
        ERROR_EXECUTION_EXCEPTION,
        ERROR_QUERY_TOO_FREQUENTLY
    }

    protected DeviceInfoResult(Parcel parcel) {
        Bundle readBundle = parcel.readBundle(DeviceInfoResult.class.getClassLoader());
        this.deviceInfo = readBundle.getBundle("device_info");
        int i = readBundle.getInt("error_code");
        this.errorCode = i == -1 ? null : ErrorCode.values()[i];
        this.errorMessage = readBundle.getString("error_message");
        this.errorStackTrace = readBundle.getString("stacktrace");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DeviceInfoResult) {
            DeviceInfoResult deviceInfoResult = (DeviceInfoResult) obj;
            Bundle bundle = this.deviceInfo;
            if (bundle == null ? deviceInfoResult.deviceInfo == null : bundle.equals(deviceInfoResult.deviceInfo)) {
                if (this.errorCode != deviceInfoResult.errorCode) {
                    return false;
                }
                String str = this.errorMessage;
                if (str == null ? deviceInfoResult.errorMessage == null : str.equals(deviceInfoResult.errorMessage)) {
                    String str2 = this.errorStackTrace;
                    return str2 == null ? deviceInfoResult.errorStackTrace == null : str2.equals(deviceInfoResult.errorStackTrace);
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        Bundle bundle = this.deviceInfo;
        int hashCode = (bundle != null ? bundle.hashCode() : 0) * 31;
        ErrorCode errorCode = this.errorCode;
        int hashCode2 = (hashCode + (errorCode != null ? errorCode.hashCode() : 0)) * 31;
        String str = this.errorMessage;
        int hashCode3 = (hashCode2 + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.errorStackTrace;
        return hashCode3 + (str2 != null ? str2.hashCode() : 0);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putBundle("device_info", this.deviceInfo);
        ErrorCode errorCode = this.errorCode;
        bundle.putInt("error_code", errorCode == null ? -1 : errorCode.ordinal());
        bundle.putString("error_message", this.errorMessage);
        bundle.putString("stacktrace", this.errorStackTrace);
        parcel.writeBundle(bundle);
    }
}
