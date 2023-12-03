package miui.upnp.service.handler;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import miui.upnp.typedef.device.invocation.SubscriptionInfo;
import miui.upnp.typedef.error.UpnpError;

/* loaded from: classes4.dex */
public interface ISubscribeCompletionHandler extends IInterface {

    /* loaded from: classes4.dex */
    public static class Default implements ISubscribeCompletionHandler {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.upnp.service.handler.ISubscribeCompletionHandler
        public void onFailed(UpnpError upnpError) throws RemoteException {
        }

        @Override // miui.upnp.service.handler.ISubscribeCompletionHandler
        public void onSucceed(SubscriptionInfo subscriptionInfo) throws RemoteException {
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ISubscribeCompletionHandler {
        private static final String DESCRIPTOR = "miui.upnp.service.handler.ISubscribeCompletionHandler";
        static final int TRANSACTION_onFailed = 2;
        static final int TRANSACTION_onSucceed = 1;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements ISubscribeCompletionHandler {
            public static ISubscribeCompletionHandler sDefaultImpl;
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

            @Override // miui.upnp.service.handler.ISubscribeCompletionHandler
            public void onFailed(UpnpError upnpError) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (upnpError != null) {
                        obtain.writeInt(1);
                        upnpError.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onFailed(upnpError);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.handler.ISubscribeCompletionHandler
            public void onSucceed(SubscriptionInfo subscriptionInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (subscriptionInfo != null) {
                        obtain.writeInt(1);
                        subscriptionInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onSucceed(subscriptionInfo);
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

        public static ISubscribeCompletionHandler asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ISubscribeCompletionHandler)) ? new Proxy(iBinder) : (ISubscribeCompletionHandler) queryLocalInterface;
        }

        public static ISubscribeCompletionHandler getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ISubscribeCompletionHandler iSubscribeCompletionHandler) {
            if (Proxy.sDefaultImpl != null || iSubscribeCompletionHandler == null) {
                return false;
            }
            Proxy.sDefaultImpl = iSubscribeCompletionHandler;
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
                onSucceed(parcel.readInt() != 0 ? SubscriptionInfo.CREATOR.createFromParcel(parcel) : null);
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
                onFailed(parcel.readInt() != 0 ? UpnpError.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                return true;
            }
        }
    }

    void onFailed(UpnpError upnpError) throws RemoteException;

    void onSucceed(SubscriptionInfo subscriptionInfo) throws RemoteException;
}
