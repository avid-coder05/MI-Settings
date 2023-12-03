package miui.cloud.finddevice;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes3.dex */
public class FindDeviceInfo implements Parcelable {
    public String displayId;
    public String email;
    public String fid;
    public String findToken;
    public boolean isLocked;
    public boolean isOpen;
    public String phone;
    public String sessionUserId;

    public FindDeviceInfo() {
        throw new RuntimeException("Stub!");
    }

    public void copyFrom(FindDeviceInfo findDeviceInfo) {
        throw new RuntimeException("Stub!");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        throw new RuntimeException("Stub!");
    }
}
