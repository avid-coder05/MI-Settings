package com.xiaomi.passport.servicetoken;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface IServiceTokenUIResponse extends IInterface {

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IServiceTokenUIResponse {

        /* loaded from: classes2.dex */
        private static class Proxy implements IServiceTokenUIResponse {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }
        }

        public Stub() {
            attachInterface(this, "com.xiaomi.passport.servicetoken.IServiceTokenUIResponse");
        }

        public static IServiceTokenUIResponse asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.xiaomi.passport.servicetoken.IServiceTokenUIResponse");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IServiceTokenUIResponse)) ? new Proxy(iBinder) : (IServiceTokenUIResponse) queryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.xiaomi.passport.servicetoken.IServiceTokenUIResponse");
                return true;
            } else if (i == 1) {
                parcel.enforceInterface("com.xiaomi.passport.servicetoken.IServiceTokenUIResponse");
                onResult(parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.xiaomi.passport.servicetoken.IServiceTokenUIResponse");
                onRequestContinued();
                parcel2.writeNoException();
                return true;
            } else if (i != 3) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.xiaomi.passport.servicetoken.IServiceTokenUIResponse");
                onError(parcel.readInt(), parcel.readString());
                parcel2.writeNoException();
                return true;
            }
        }
    }

    void onError(int i, String str) throws RemoteException;

    void onRequestContinued() throws RemoteException;

    void onResult(Bundle bundle) throws RemoteException;
}
