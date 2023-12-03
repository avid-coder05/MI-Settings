package miui.upnp.service.controlpoint;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;
import miui.upnp.service.handler.ICompletionHandler;
import miui.upnp.service.handler.IEventListener;
import miui.upnp.service.handler.IInvokeCompletionHandler;
import miui.upnp.service.handler.IScanListener;
import miui.upnp.service.handler.ISubscribeCompletionHandler;
import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.device.invocation.SubscriptionInfo;
import miui.upnp.typedef.device.urn.Urn;

/* loaded from: classes4.dex */
public interface IUpnpControlPointService extends IInterface {

    /* loaded from: classes4.dex */
    public static class Default implements IUpnpControlPointService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
        public void invoke(ActionInfo actionInfo, IInvokeCompletionHandler iInvokeCompletionHandler) throws RemoteException {
        }

        @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
        public void start() throws RemoteException {
        }

        @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
        public void startScan(List<Urn> list, ICompletionHandler iCompletionHandler, IScanListener iScanListener) throws RemoteException {
        }

        @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
        public void stop() throws RemoteException {
        }

        @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
        public void stopScan(ICompletionHandler iCompletionHandler) throws RemoteException {
        }

        @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
        public void subscribe(SubscriptionInfo subscriptionInfo, ISubscribeCompletionHandler iSubscribeCompletionHandler, IEventListener iEventListener) throws RemoteException {
        }

        @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
        public void unsubscribe(SubscriptionInfo subscriptionInfo, ICompletionHandler iCompletionHandler) throws RemoteException {
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IUpnpControlPointService {
        private static final String DESCRIPTOR = "miui.upnp.service.controlpoint.IUpnpControlPointService";
        static final int TRANSACTION_invoke = 5;
        static final int TRANSACTION_start = 1;
        static final int TRANSACTION_startScan = 3;
        static final int TRANSACTION_stop = 2;
        static final int TRANSACTION_stopScan = 4;
        static final int TRANSACTION_subscribe = 6;
        static final int TRANSACTION_unsubscribe = 7;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IUpnpControlPointService {
            public static IUpnpControlPointService sDefaultImpl;
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

            @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
            public void invoke(ActionInfo actionInfo, IInvokeCompletionHandler iInvokeCompletionHandler) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (actionInfo != null) {
                        obtain.writeInt(1);
                        actionInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iInvokeCompletionHandler != null ? iInvokeCompletionHandler.asBinder() : null);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().invoke(actionInfo, iInvokeCompletionHandler);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
            public void start() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().start();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
            public void startScan(List<Urn> list, ICompletionHandler iCompletionHandler, IScanListener iScanListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedList(list);
                    obtain.writeStrongBinder(iCompletionHandler != null ? iCompletionHandler.asBinder() : null);
                    obtain.writeStrongBinder(iScanListener != null ? iScanListener.asBinder() : null);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().startScan(list, iCompletionHandler, iScanListener);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
            public void stop() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().stop();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
            public void stopScan(ICompletionHandler iCompletionHandler) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iCompletionHandler != null ? iCompletionHandler.asBinder() : null);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().stopScan(iCompletionHandler);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
            public void subscribe(SubscriptionInfo subscriptionInfo, ISubscribeCompletionHandler iSubscribeCompletionHandler, IEventListener iEventListener) throws RemoteException {
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
                    obtain.writeStrongBinder(iSubscribeCompletionHandler != null ? iSubscribeCompletionHandler.asBinder() : null);
                    obtain.writeStrongBinder(iEventListener != null ? iEventListener.asBinder() : null);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().subscribe(subscriptionInfo, iSubscribeCompletionHandler, iEventListener);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.controlpoint.IUpnpControlPointService
            public void unsubscribe(SubscriptionInfo subscriptionInfo, ICompletionHandler iCompletionHandler) throws RemoteException {
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
                    obtain.writeStrongBinder(iCompletionHandler != null ? iCompletionHandler.asBinder() : null);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unsubscribe(subscriptionInfo, iCompletionHandler);
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

        public static IUpnpControlPointService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IUpnpControlPointService)) ? new Proxy(iBinder) : (IUpnpControlPointService) queryLocalInterface;
        }

        public static IUpnpControlPointService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IUpnpControlPointService iUpnpControlPointService) {
            if (Proxy.sDefaultImpl != null || iUpnpControlPointService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iUpnpControlPointService;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    start();
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    stop();
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    startScan(parcel.createTypedArrayList(Urn.CREATOR), ICompletionHandler.Stub.asInterface(parcel.readStrongBinder()), IScanListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    stopScan(ICompletionHandler.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    invoke(parcel.readInt() != 0 ? ActionInfo.CREATOR.createFromParcel(parcel) : null, IInvokeCompletionHandler.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    subscribe(parcel.readInt() != 0 ? SubscriptionInfo.CREATOR.createFromParcel(parcel) : null, ISubscribeCompletionHandler.Stub.asInterface(parcel.readStrongBinder()), IEventListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    unsubscribe(parcel.readInt() != 0 ? SubscriptionInfo.CREATOR.createFromParcel(parcel) : null, ICompletionHandler.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void invoke(ActionInfo actionInfo, IInvokeCompletionHandler iInvokeCompletionHandler) throws RemoteException;

    void start() throws RemoteException;

    void startScan(List<Urn> list, ICompletionHandler iCompletionHandler, IScanListener iScanListener) throws RemoteException;

    void stop() throws RemoteException;

    void stopScan(ICompletionHandler iCompletionHandler) throws RemoteException;

    void subscribe(SubscriptionInfo subscriptionInfo, ISubscribeCompletionHandler iSubscribeCompletionHandler, IEventListener iEventListener) throws RemoteException;

    void unsubscribe(SubscriptionInfo subscriptionInfo, ICompletionHandler iCompletionHandler) throws RemoteException;
}
