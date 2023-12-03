package com.xiaomi.passport.servicetoken.data;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class XmAccountVisibility implements Parcelable {
    public static final Parcelable.Creator<XmAccountVisibility> CREATOR = new Parcelable.Creator<XmAccountVisibility>() { // from class: com.xiaomi.passport.servicetoken.data.XmAccountVisibility.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public XmAccountVisibility createFromParcel(Parcel parcel) {
            return new XmAccountVisibility(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public XmAccountVisibility[] newArray(int i) {
            return new XmAccountVisibility[0];
        }
    };
    public final Account account;
    public final int buildSdkVersion;
    public final ErrorCode errorCode;
    public final String errorMsg;
    public final Intent newChooseAccountIntent;
    public final boolean visible;

    /* loaded from: classes2.dex */
    public enum ErrorCode {
        ERROR_NONE("successful"),
        ERROR_NOT_SUPPORT("no support account service"),
        ERROR_PRE_ANDROID_O("no support account service, and pre o version"),
        ERROR_NO_ACCOUNT("no account"),
        ERROR_NO_PERMISSION("no access account service permission"),
        ERROR_CANCELLED("task cancelled"),
        ERROR_EXECUTION("execution error"),
        ERROR_UNKNOWN("unknown");

        String errorMsg;

        ErrorCode(String str) {
            this.errorMsg = str;
        }
    }

    public XmAccountVisibility(Parcel parcel) {
        Bundle readBundle = parcel.readBundle();
        this.errorCode = ErrorCode.values()[readBundle.getInt("error_code")];
        this.errorMsg = readBundle.getString("error_msg");
        this.visible = readBundle.getBoolean("visible");
        this.account = (Account) readBundle.getParcelable("account");
        this.buildSdkVersion = readBundle.getInt("build_sdk_version");
        this.newChooseAccountIntent = (Intent) readBundle.getParcelable("new_choose_account_intent");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("AccountVisibility{");
        stringBuffer.append(", errorCode=");
        stringBuffer.append(this.errorCode);
        stringBuffer.append(", errorMessage='");
        stringBuffer.append(this.errorMsg);
        stringBuffer.append('\'');
        stringBuffer.append(", accountVisible='");
        stringBuffer.append(this.visible);
        stringBuffer.append('\'');
        stringBuffer.append('}');
        return stringBuffer.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("error_code", this.errorCode.ordinal());
        bundle.putString("error_msg", this.errorMsg);
        bundle.putBoolean("visible", this.visible);
        bundle.putParcelable("account", this.account);
        bundle.putInt("build_sdk_version", this.buildSdkVersion);
        bundle.putParcelable("new_choose_account_intent", this.newChooseAccountIntent);
        parcel.writeBundle(bundle);
    }
}
