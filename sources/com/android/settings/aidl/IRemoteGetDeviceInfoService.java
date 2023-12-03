package com.android.settings.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.Map;

/* loaded from: classes.dex */
public interface IRemoteGetDeviceInfoService extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IRemoteGetDeviceInfoService {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IRemoteGetDeviceInfoService {
            public static IRemoteGetDeviceInfoService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.settings.aidl.IRemoteGetDeviceInfoService
            public void getDeviceInfo(int i, Map map) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.settings.aidl.IRemoteGetDeviceInfoService");
                    obtain.writeInt(i);
                    obtain.writeMap(map);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().getDeviceInfo(i, map);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.settings.aidl.IRemoteGetDeviceInfoService
            public void registerCallback(IRequestCallback iRequestCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.settings.aidl.IRemoteGetDeviceInfoService");
                    obtain.writeStrongBinder(iRequestCallback != null ? iRequestCallback.asBinder() : null);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerCallback(iRequestCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.settings.aidl.IRemoteGetDeviceInfoService
            public void unregisteCallback(IRequestCallback iRequestCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.settings.aidl.IRemoteGetDeviceInfoService");
                    obtain.writeStrongBinder(iRequestCallback != null ? iRequestCallback.asBinder() : null);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisteCallback(iRequestCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IRemoteGetDeviceInfoService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.settings.aidl.IRemoteGetDeviceInfoService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IRemoteGetDeviceInfoService)) ? new Proxy(iBinder) : (IRemoteGetDeviceInfoService) queryLocalInterface;
        }

        public static IRemoteGetDeviceInfoService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void getDeviceInfo(int i, Map map) throws RemoteException;

    void registerCallback(IRequestCallback iRequestCallback) throws RemoteException;

    void unregisteCallback(IRequestCallback iRequestCallback) throws RemoteException;
}
