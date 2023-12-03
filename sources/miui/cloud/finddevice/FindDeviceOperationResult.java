package miui.cloud.finddevice;

import android.os.Parcel;
import android.os.Parcelable;

@Deprecated
/* loaded from: classes3.dex */
public class FindDeviceOperationResult implements Parcelable {
    public final int errno;
    public String errorMsg;
    public final int operationFailedCode;
    public final boolean success;

    FindDeviceOperationResult() {
        throw new RuntimeException("Stub!");
    }

    public static FindDeviceOperationResult buildAccessDeniedResult(String str) {
        throw new RuntimeException("Stub!");
    }

    public static FindDeviceOperationResult buildCommonErrorResult(int i, String str) {
        throw new RuntimeException("Stub!");
    }

    public static FindDeviceOperationResult buildOperationFailedErrorResult(int i, String str) {
        throw new RuntimeException("Stub!");
    }

    public static FindDeviceOperationResult buildSuccessResult() {
        throw new RuntimeException("Stub!");
    }

    public static String getDebugString(FindDeviceOperationResult findDeviceOperationResult) {
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
