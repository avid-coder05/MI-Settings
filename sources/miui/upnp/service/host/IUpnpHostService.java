package miui.upnp.service.host;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import miui.upnp.service.handler.IActionListener;
import miui.upnp.service.handler.ICompletionHandler;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.invocation.EventInfo;

/* loaded from: classes4.dex */
public interface IUpnpHostService extends IInterface {

    /* loaded from: classes4.dex */
    public static class Default implements IUpnpHostService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.upnp.service.host.IUpnpHostService
        public void register(Device device, ICompletionHandler iCompletionHandler, IActionListener iActionListener) throws RemoteException {
        }

        @Override // miui.upnp.service.host.IUpnpHostService
        public void sendEvents(EventInfo eventInfo) throws RemoteException {
        }

        @Override // miui.upnp.service.host.IUpnpHostService
        public void start() throws RemoteException {
        }

        @Override // miui.upnp.service.host.IUpnpHostService
        public void stop() throws RemoteException {
        }

        @Override // miui.upnp.service.host.IUpnpHostService
        public void unregister(Device device, ICompletionHandler iCompletionHandler) throws RemoteException {
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IUpnpHostService {
        private static final String DESCRIPTOR = "miui.upnp.service.host.IUpnpHostService";
        static final int TRANSACTION_register = 3;
        static final int TRANSACTION_sendEvents = 5;
        static final int TRANSACTION_start = 1;
        static final int TRANSACTION_stop = 2;
        static final int TRANSACTION_unregister = 4;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IUpnpHostService {
            public static IUpnpHostService sDefaultImpl;
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

            @Override // miui.upnp.service.host.IUpnpHostService
            public void register(Device device, ICompletionHandler iCompletionHandler, IActionListener iActionListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        obtain.writeInt(1);
                        device.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iCompletionHandler != null ? iCompletionHandler.asBinder() : null);
                    obtain.writeStrongBinder(iActionListener != null ? iActionListener.asBinder() : null);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().register(device, iCompletionHandler, iActionListener);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.host.IUpnpHostService
            public void sendEvents(EventInfo eventInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (eventInfo != null) {
                        obtain.writeInt(1);
                        eventInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().sendEvents(eventInfo);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.upnp.service.host.IUpnpHostService
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

            @Override // miui.upnp.service.host.IUpnpHostService
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

            @Override // miui.upnp.service.host.IUpnpHostService
            public void unregister(Device device, ICompletionHandler iCompletionHandler) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        obtain.writeInt(1);
                        device.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iCompletionHandler != null ? iCompletionHandler.asBinder() : null);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregister(device, iCompletionHandler);
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

        public static IUpnpHostService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IUpnpHostService)) ? new Proxy(iBinder) : (IUpnpHostService) queryLocalInterface;
        }

        public static IUpnpHostService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IUpnpHostService iUpnpHostService) {
            if (Proxy.sDefaultImpl != null || iUpnpHostService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iUpnpHostService;
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
                start();
                parcel2.writeNoException();
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                stop();
                parcel2.writeNoException();
                return true;
            } else {
                if (i == 3) {
                    parcel.enforceInterface(DESCRIPTOR);
                    register(parcel.readInt() != 0 ? Device.CREATOR.createFromParcel(parcel) : null, ICompletionHandler.Stub.asInterface(parcel.readStrongBinder()), IActionListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                } else if (i == 4) {
                    parcel.enforceInterface(DESCRIPTOR);
                    unregister(parcel.readInt() != 0 ? Device.CREATOR.createFromParcel(parcel) : null, ICompletionHandler.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                } else if (i != 5) {
                    if (i != 1598968902) {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                } else {
                    parcel.enforceInterface(DESCRIPTOR);
                    sendEvents(parcel.readInt() != 0 ? EventInfo.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                }
            }
        }
    }

    void register(Device device, ICompletionHandler iCompletionHandler, IActionListener iActionListener) throws RemoteException;

    void sendEvents(EventInfo eventInfo) throws RemoteException;

    void start() throws RemoteException;

    void stop() throws RemoteException;

    void unregister(Device device, ICompletionHandler iCompletionHandler) throws RemoteException;
}
