package miui.upnp.service.handler;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.error.UpnpError;

/* loaded from: classes4.dex */
public interface IActionListener extends IInterface {

    /* loaded from: classes4.dex */
    public static class Default implements IActionListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.upnp.service.handler.IActionListener
        public UpnpError onAction(ActionInfo actionInfo) throws RemoteException {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IActionListener {
        private static final String DESCRIPTOR = "miui.upnp.service.handler.IActionListener";
        static final int TRANSACTION_onAction = 1;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IActionListener {
            public static IActionListener sDefaultImpl;
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

            @Override // miui.upnp.service.handler.IActionListener
            public UpnpError onAction(ActionInfo actionInfo) throws RemoteException {
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
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        UpnpError createFromParcel = obtain2.readInt() != 0 ? UpnpError.CREATOR.createFromParcel(obtain2) : null;
                        if (obtain2.readInt() != 0) {
                            actionInfo.readFromParcel(obtain2);
                        }
                        return createFromParcel;
                    }
                    return Stub.getDefaultImpl().onAction(actionInfo);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IActionListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IActionListener)) ? new Proxy(iBinder) : (IActionListener) queryLocalInterface;
        }

        public static IActionListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IActionListener iActionListener) {
            if (Proxy.sDefaultImpl != null || iActionListener == null) {
                return false;
            }
            Proxy.sDefaultImpl = iActionListener;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i != 1) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            parcel.enforceInterface(DESCRIPTOR);
            ActionInfo createFromParcel = parcel.readInt() != 0 ? ActionInfo.CREATOR.createFromParcel(parcel) : null;
            UpnpError onAction = onAction(createFromParcel);
            parcel2.writeNoException();
            if (onAction != null) {
                parcel2.writeInt(1);
                onAction.writeToParcel(parcel2, 1);
            } else {
                parcel2.writeInt(0);
            }
            if (createFromParcel != null) {
                parcel2.writeInt(1);
                createFromParcel.writeToParcel(parcel2, 1);
            } else {
                parcel2.writeInt(0);
            }
            return true;
        }
    }

    UpnpError onAction(ActionInfo actionInfo) throws RemoteException;
}
