package com.android.settings.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IRequestCallback extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IRequestCallback {
        public Stub() {
            attachInterface(this, "com.android.settings.aidl.IRequestCallback");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.android.settings.aidl.IRequestCallback");
                return true;
            } else if (i != 1) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.android.settings.aidl.IRequestCallback");
                onRequestComplete(parcel.readInt(), parcel.readString());
                parcel2.writeNoException();
                return true;
            }
        }
    }

    void onRequestComplete(int i, String str) throws RemoteException;
}
