package miui.upnp.service.handler;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;
import miui.upnp.typedef.device.PropertyChanged;

/* loaded from: classes4.dex */
public interface IEventListener extends IInterface {

    /* loaded from: classes4.dex */
    public static class Default implements IEventListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.upnp.service.handler.IEventListener
        public void onEvent(String str, List<PropertyChanged> list) throws RemoteException {
        }

        @Override // miui.upnp.service.handler.IEventListener
        public void onSubscriptionExpired(String str) throws RemoteException {
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IEventListener {
        private static final String DESCRIPTOR = "miui.upnp.service.handler.IEventListener";
        static final int TRANSACTION_onEvent = 2;
        static final int TRANSACTION_onSubscriptionExpired = 1;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IEventListener {
            public static IEventListener sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // miui.upnp.service.handler.IEventListener
            public void onEvent(String str, List<PropertyChanged> list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeTypedList(list);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onEvent(str, list);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.handler.IEventListener
            public void onSubscriptionExpired(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onSubscriptionExpired(str);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IEventListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IEventListener)) ? new Proxy(iBinder) : (IEventListener) queryLocalInterface;
        }

        public static IEventListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IEventListener iEventListener) {
            if (Proxy.sDefaultImpl != null || iEventListener == null) {
                return false;
            }
            Proxy.sDefaultImpl = iEventListener;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                onSubscriptionExpired(parcel.readString());
                parcel2.writeNoException();
                return true;
            } else if (i != 2) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString(DESCRIPTOR);
                return true;
            } else {
                parcel.enforceInterface(DESCRIPTOR);
                onEvent(parcel.readString(), parcel.createTypedArrayList(PropertyChanged.CREATOR));
                parcel2.writeNoException();
                return true;
            }
        }
    }

    void onEvent(String str, List<PropertyChanged> list) throws RemoteException;

    void onSubscriptionExpired(String str) throws RemoteException;
}
