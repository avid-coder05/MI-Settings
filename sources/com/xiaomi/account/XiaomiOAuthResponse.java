package com.xiaomi.account;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import com.xiaomi.account.IXiaomiAuthResponse;

/* loaded from: classes2.dex */
public class XiaomiOAuthResponse implements Parcelable {
    private IXiaomiAuthResponse mResponse;
    private static final String TAG = XiaomiOAuthResponse.class.getName();
    public static final Parcelable.Creator<XiaomiOAuthResponse> CREATOR = new Parcelable.Creator<XiaomiOAuthResponse>() { // from class: com.xiaomi.account.XiaomiOAuthResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public XiaomiOAuthResponse createFromParcel(Parcel parcel) {
            return new XiaomiOAuthResponse(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public XiaomiOAuthResponse[] newArray(int i) {
            return new XiaomiOAuthResponse[i];
        }
    };

    public XiaomiOAuthResponse(Parcel parcel) {
        this.mResponse = IXiaomiAuthResponse.Stub.asInterface(parcel.readStrongBinder());
    }

    public XiaomiOAuthResponse(IXiaomiAuthResponse iXiaomiAuthResponse) {
        this.mResponse = iXiaomiAuthResponse;
    }

    public static void setIXiaomiAuthResponseCancel(IXiaomiAuthResponse iXiaomiAuthResponse) {
        if (iXiaomiAuthResponse == null) {
            return;
        }
        try {
            iXiaomiAuthResponse.onCancel();
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        } catch (RuntimeException e2) {
            Log.e(TAG, "RuntimeException", e2);
        }
    }

    public static void setIXiaomiAuthResponseResult(IXiaomiAuthResponse iXiaomiAuthResponse, Bundle bundle) {
        if (iXiaomiAuthResponse == null || bundle == null) {
            return;
        }
        try {
            iXiaomiAuthResponse.onResult(bundle);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        } catch (RuntimeException e2) {
            Log.e(TAG, "RemoteException", e2);
            Bundle bundle2 = new Bundle();
            bundle2.putInt("extra_error_code", -1);
            bundle2.putString("extra_error_description", e2.getMessage());
            try {
                iXiaomiAuthResponse.onResult(bundle2);
            } catch (RemoteException e3) {
                Log.e(TAG, "RemoteException", e3);
            } catch (RuntimeException e4) {
                Log.e(TAG, "RuntimeException", e4);
            }
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void onCancel() {
        setIXiaomiAuthResponseCancel(this.mResponse);
    }

    public void onResult(Bundle bundle) {
        setIXiaomiAuthResponseResult(this.mResponse, bundle);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.mResponse.asBinder());
    }
}
