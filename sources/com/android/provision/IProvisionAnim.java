package com.android.provision;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IProvisionAnim extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IProvisionAnim {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IProvisionAnim {
            public static IProvisionAnim sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.provision.IProvisionAnim
            public boolean isAnimEnd() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.provision.IProvisionAnim");
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().isAnimEnd();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.provision.IProvisionAnim
            public void playBackAnim(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.provision.IProvisionAnim");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().playBackAnim(i);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.provision.IProvisionAnim
            public void playNextAnim(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.provision.IProvisionAnim");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().playNextAnim(i);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.provision.IProvisionAnim
            public void registerRemoteCallback(IAnimCallback iAnimCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.provision.IProvisionAnim");
                    obtain.writeStrongBinder(iAnimCallback != null ? iAnimCallback.asBinder() : null);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerRemoteCallback(iAnimCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.provision.IProvisionAnim
            public void unregisterRemoteCallback(IAnimCallback iAnimCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.provision.IProvisionAnim");
                    obtain.writeStrongBinder(iAnimCallback != null ? iAnimCallback.asBinder() : null);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterRemoteCallback(iAnimCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IProvisionAnim asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.provision.IProvisionAnim");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IProvisionAnim)) ? new Proxy(iBinder) : (IProvisionAnim) queryLocalInterface;
        }

        public static IProvisionAnim getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    boolean isAnimEnd() throws RemoteException;

    void playBackAnim(int i) throws RemoteException;

    void playNextAnim(int i) throws RemoteException;

    void registerRemoteCallback(IAnimCallback iAnimCallback) throws RemoteException;

    void unregisterRemoteCallback(IAnimCallback iAnimCallback) throws RemoteException;
}
