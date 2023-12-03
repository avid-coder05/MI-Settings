package com.xiaomi.accountsdk.guestaccount.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import miui.yellowpage.Tag;

/* loaded from: classes2.dex */
public final class GuestAccount implements Parcelable {
    public static final Parcelable.Creator<GuestAccount> CREATOR = new Parcelable.Creator<GuestAccount>() { // from class: com.xiaomi.accountsdk.guestaccount.data.GuestAccount.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GuestAccount createFromParcel(Parcel parcel) {
            return new GuestAccount(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GuestAccount[] newArray(int i) {
            return new GuestAccount[i];
        }
    };
    public final String cUserId;
    public final String callback;
    public final String passToken;
    public final String ph;
    public final String security;
    public final String serviceToken;
    public final String sid;
    public final String slh;
    public final GuestAccountType type;
    public final String userId;

    protected GuestAccount(Parcel parcel) {
        Bundle readBundle = parcel.readBundle();
        this.userId = readBundle.getString("userid");
        this.cUserId = readBundle.getString("cuserid");
        this.sid = readBundle.getString(Tag.TagYellowPage.YID);
        this.serviceToken = readBundle.getString("servicetoken");
        this.security = readBundle.getString("security");
        this.passToken = readBundle.getString("passtoken");
        this.callback = readBundle.getString("callback");
        this.slh = readBundle.getString("slh");
        this.ph = readBundle.getString("ph");
        this.type = GuestAccountType.getFromServerValue(readBundle.getInt("type"));
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GuestAccount) {
            GuestAccount guestAccount = (GuestAccount) obj;
            String str = this.userId;
            if (str == null ? guestAccount.userId == null : str.equals(guestAccount.userId)) {
                String str2 = this.cUserId;
                if (str2 == null ? guestAccount.cUserId == null : str2.equals(guestAccount.cUserId)) {
                    String str3 = this.sid;
                    if (str3 == null ? guestAccount.sid == null : str3.equals(guestAccount.sid)) {
                        String str4 = this.serviceToken;
                        if (str4 == null ? guestAccount.serviceToken == null : str4.equals(guestAccount.serviceToken)) {
                            String str5 = this.security;
                            if (str5 == null ? guestAccount.security == null : str5.equals(guestAccount.security)) {
                                String str6 = this.passToken;
                                if (str6 == null ? guestAccount.passToken == null : str6.equals(guestAccount.passToken)) {
                                    String str7 = this.callback;
                                    if (str7 == null ? guestAccount.callback == null : str7.equals(guestAccount.callback)) {
                                        String str8 = this.slh;
                                        if (str8 == null ? guestAccount.slh == null : str8.equals(guestAccount.slh)) {
                                            String str9 = this.ph;
                                            if (str9 == null ? guestAccount.ph == null : str9.equals(guestAccount.ph)) {
                                                return this.type == guestAccount.type;
                                            }
                                            return false;
                                        }
                                        return false;
                                    }
                                    return false;
                                }
                                return false;
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        String str = this.userId;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.cUserId;
        int hashCode2 = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.sid;
        int hashCode3 = (hashCode2 + (str3 != null ? str3.hashCode() : 0)) * 31;
        String str4 = this.serviceToken;
        int hashCode4 = (hashCode3 + (str4 != null ? str4.hashCode() : 0)) * 31;
        String str5 = this.security;
        int hashCode5 = (hashCode4 + (str5 != null ? str5.hashCode() : 0)) * 31;
        String str6 = this.passToken;
        int hashCode6 = (hashCode5 + (str6 != null ? str6.hashCode() : 0)) * 31;
        String str7 = this.callback;
        int hashCode7 = (hashCode6 + (str7 != null ? str7.hashCode() : 0)) * 31;
        String str8 = this.slh;
        int hashCode8 = (hashCode7 + (str8 != null ? str8.hashCode() : 0)) * 31;
        String str9 = this.ph;
        int hashCode9 = (hashCode8 + (str9 != null ? str9.hashCode() : 0)) * 31;
        GuestAccountType guestAccountType = this.type;
        return hashCode9 + (guestAccountType != null ? guestAccountType.hashCode() : 0);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("GuestAccount{");
        stringBuffer.append("userId='");
        stringBuffer.append(this.userId);
        stringBuffer.append('\'');
        stringBuffer.append("cUserId='");
        stringBuffer.append(this.cUserId);
        stringBuffer.append('\'');
        stringBuffer.append(", sid='");
        stringBuffer.append(this.sid);
        stringBuffer.append('\'');
        stringBuffer.append(", serviceToken='");
        stringBuffer.append(this.serviceToken);
        stringBuffer.append('\'');
        stringBuffer.append(", security='");
        stringBuffer.append(this.security);
        stringBuffer.append('\'');
        stringBuffer.append(", passToken='");
        stringBuffer.append(this.passToken);
        stringBuffer.append('\'');
        stringBuffer.append(", callback='");
        stringBuffer.append(this.callback);
        stringBuffer.append('\'');
        stringBuffer.append(", slh='");
        stringBuffer.append(this.slh);
        stringBuffer.append('\'');
        stringBuffer.append(", ph='");
        stringBuffer.append(this.ph);
        stringBuffer.append('\'');
        stringBuffer.append(", type=");
        stringBuffer.append(this.type);
        stringBuffer.append('}');
        return stringBuffer.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("userid", this.userId);
        bundle.putString("cuserid", this.cUserId);
        bundle.putString(Tag.TagYellowPage.YID, this.sid);
        bundle.putString("servicetoken", this.serviceToken);
        bundle.putString("security", this.security);
        bundle.putString("passtoken", this.passToken);
        bundle.putString("callback", this.callback);
        bundle.putString("slh", this.slh);
        bundle.putString("ph", this.ph);
        GuestAccountType guestAccountType = this.type;
        bundle.putInt("type", guestAccountType == null ? -1 : guestAccountType.serverValue);
        parcel.writeBundle(bundle);
    }
}
