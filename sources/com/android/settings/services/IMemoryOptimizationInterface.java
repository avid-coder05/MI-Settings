package com.android.settings.services;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface IMemoryOptimizationInterface extends IInterface {

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMemoryOptimizationInterface {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IMemoryOptimizationInterface {
            public static IMemoryOptimizationInterface sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.settings.services.IMemoryOptimizationInterface
            public void startMemoryOptimization(Intent intent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.settings.services.IMemoryOptimizationInterface");
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().startMemoryOptimization(intent);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IMemoryOptimizationInterface asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.settings.services.IMemoryOptimizationInterface");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMemoryOptimizationInterface)) ? new Proxy(iBinder) : (IMemoryOptimizationInterface) queryLocalInterface;
        }

        public static IMemoryOptimizationInterface getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void startMemoryOptimization(Intent intent) throws RemoteException;
}
