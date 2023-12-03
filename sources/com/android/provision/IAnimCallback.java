package com.android.provision;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IAnimCallback extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAnimCallback {
        public Stub() {
            attachInterface(this, "com.android.provision.IAnimCallback");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface("com.android.provision.IAnimCallback");
                onNextAminStart();
                parcel2.writeNoException();
                return true;
            } else if (i != 2) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString("com.android.provision.IAnimCallback");
                return true;
            } else {
                parcel.enforceInterface("com.android.provision.IAnimCallback");
                onBackAnimStart();
                parcel2.writeNoException();
                return true;
            }
        }
    }

    void onBackAnimStart() throws RemoteException;

    void onNextAminStart() throws RemoteException;
}
