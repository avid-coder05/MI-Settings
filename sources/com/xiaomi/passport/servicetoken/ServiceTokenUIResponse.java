package com.xiaomi.passport.servicetoken;

import android.os.Parcel;
import android.os.Parcelable;
import com.xiaomi.passport.servicetoken.IServiceTokenUIResponse;

/* loaded from: classes2.dex */
public class ServiceTokenUIResponse implements Parcelable {
    public static final Parcelable.Creator<ServiceTokenUIResponse> CREATOR = new Parcelable.Creator<ServiceTokenUIResponse>() { // from class: com.xiaomi.passport.servicetoken.ServiceTokenUIResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceTokenUIResponse createFromParcel(Parcel parcel) {
            return new ServiceTokenUIResponse(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceTokenUIResponse[] newArray(int i) {
            return new ServiceTokenUIResponse[i];
        }
    };
    private IServiceTokenUIResponse mResponse;

    public ServiceTokenUIResponse(Parcel parcel) {
        this.mResponse = IServiceTokenUIResponse.Stub.asInterface(parcel.readStrongBinder());
    }

    public ServiceTokenUIResponse(IServiceTokenUIResponse iServiceTokenUIResponse) {
        this.mResponse = iServiceTokenUIResponse;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.mResponse.asBinder());
    }
}
