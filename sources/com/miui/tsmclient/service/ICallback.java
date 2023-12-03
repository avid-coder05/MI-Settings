package com.miui.tsmclient.service;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface ICallback extends IInterface {

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICallback {
        public Stub() {
            attachInterface(this, "com.miui.tsmclient.service.ICallback");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.miui.tsmclient.service.ICallback");
                return true;
            } else if (i == 1) {
                parcel.enforceInterface("com.miui.tsmclient.service.ICallback");
                onSuccess(parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                return true;
            } else if (i != 2) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.miui.tsmclient.service.ICallback");
                onError(parcel.readInt(), parcel.readString());
                parcel2.writeNoException();
                return true;
            }
        }
    }

    void onError(int i, String str) throws RemoteException;

    void onSuccess(Bundle bundle) throws RemoteException;
}
