package com.miui.cloudservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface IPasspointKeyInterface extends IInterface {

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IPasspointKeyInterface {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IPasspointKeyInterface {
            public static IPasspointKeyInterface sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.miui.cloudservice.IPasspointKeyInterface
            public String getPassword(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.cloudservice.IPasspointKeyInterface");
                    obtain.writeString(str);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readString();
                    }
                    return Stub.getDefaultImpl().getPassword(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IPasspointKeyInterface asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.cloudservice.IPasspointKeyInterface");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IPasspointKeyInterface)) ? new Proxy(iBinder) : (IPasspointKeyInterface) queryLocalInterface;
        }

        public static IPasspointKeyInterface getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    String getPassword(String str) throws RemoteException;
}
