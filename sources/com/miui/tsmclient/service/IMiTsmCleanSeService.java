package com.miui.tsmclient.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface IMiTsmCleanSeService extends IInterface {

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMiTsmCleanSeService {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IMiTsmCleanSeService {
            public static IMiTsmCleanSeService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.miui.tsmclient.service.IMiTsmCleanSeService
            public void cleanSeCard(ICallback iCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.tsmclient.service.IMiTsmCleanSeService");
                    obtain.writeStrongBinder(iCallback != null ? iCallback.asBinder() : null);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().cleanSeCard(iCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.miui.tsmclient.service.IMiTsmCleanSeService
            public void keepSeCard(ICallback iCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.tsmclient.service.IMiTsmCleanSeService");
                    obtain.writeStrongBinder(iCallback != null ? iCallback.asBinder() : null);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().keepSeCard(iCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.miui.tsmclient.service.IMiTsmCleanSeService
            public void querySeCard(ICallback iCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.tsmclient.service.IMiTsmCleanSeService");
                    obtain.writeStrongBinder(iCallback != null ? iCallback.asBinder() : null);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().querySeCard(iCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IMiTsmCleanSeService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.tsmclient.service.IMiTsmCleanSeService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMiTsmCleanSeService)) ? new Proxy(iBinder) : (IMiTsmCleanSeService) queryLocalInterface;
        }

        public static IMiTsmCleanSeService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void cleanSeCard(ICallback iCallback) throws RemoteException;

    void keepSeCard(ICallback iCallback) throws RemoteException;

    void querySeCard(ICallback iCallback) throws RemoteException;
}
