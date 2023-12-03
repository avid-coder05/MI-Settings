package miuix.appcompat.app.floatingactivity.multiapp;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes5.dex */
public interface IFloatingService extends IInterface {

    /* loaded from: classes5.dex */
    public static abstract class Stub extends Binder implements IFloatingService {

        /* loaded from: classes5.dex */
        private static class Proxy implements IFloatingService {
            private final IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // miuix.appcompat.app.floatingactivity.multiapp.IFloatingService
            public Bundle callServiceMethod(int i, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("miuix.appcompat.app.floatingactivity.multiapp.IFloatingService");
                    obtain.writeInt(i);
                    obtain.writeBundle(bundle);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    Bundle readBundle = obtain2.readBundle(Proxy.class.getClassLoader());
                    obtain2.readException();
                    return readBundle;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miuix.appcompat.app.floatingactivity.multiapp.IFloatingService
            public int registerServiceNotify(IServiceNotify iServiceNotify, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("miuix.appcompat.app.floatingactivity.multiapp.IFloatingService");
                    obtain.writeStrongBinder(iServiceNotify == null ? null : iServiceNotify.asBinder());
                    obtain.writeString(str);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    int readInt = obtain2.readInt();
                    obtain2.readException();
                    return readInt;
                } finally {
                    obtain.recycle();
                    obtain2.recycle();
                }
            }

            @Override // miuix.appcompat.app.floatingactivity.multiapp.IFloatingService
            public void unregisterServiceNotify(IServiceNotify iServiceNotify, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("miuix.appcompat.app.floatingactivity.multiapp.IFloatingService");
                    obtain.writeStrongBinder(iServiceNotify == null ? null : iServiceNotify.asBinder());
                    obtain.writeString(str);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                } finally {
                    obtain.recycle();
                    obtain2.recycle();
                }
            }

            @Override // miuix.appcompat.app.floatingactivity.multiapp.IFloatingService
            public void upDateRemoteActivityInfo(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("miuix.appcompat.app.floatingactivity.multiapp.IFloatingService");
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain.recycle();
                    obtain2.recycle();
                }
            }
        }

        public static IFloatingService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            return iBinder.queryLocalInterface("miuix.appcompat.app.floatingactivity.multiapp.IFloatingService") instanceof IFloatingService ? (IFloatingService) iBinder : new Proxy(iBinder);
        }
    }

    Bundle callServiceMethod(int i, Bundle bundle) throws RemoteException;

    int registerServiceNotify(IServiceNotify iServiceNotify, String str) throws RemoteException;

    void unregisterServiceNotify(IServiceNotify iServiceNotify, String str) throws RemoteException;

    void upDateRemoteActivityInfo(String str, int i) throws RemoteException;
}
