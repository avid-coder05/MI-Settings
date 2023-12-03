package miui.popupcamera;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes3.dex */
public interface IPopupCameraManager extends IInterface {
    public static final String DESCRIPTOR = "miui.popupcamera.IPopupCameraManager";

    /* loaded from: classes3.dex */
    public static class Default implements IPopupCameraManager {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.popupcamera.IPopupCameraManager
        public void calibrationMotor() throws RemoteException {
        }

        @Override // miui.popupcamera.IPopupCameraManager
        public int getMotorStatus() throws RemoteException {
            return 0;
        }

        @Override // miui.popupcamera.IPopupCameraManager
        public int getPopupCameraState() throws RemoteException {
            return 0;
        }

        @Override // miui.popupcamera.IPopupCameraManager
        public boolean notifyCameraStatus(int i, int i2, String str) throws RemoteException {
            return false;
        }

        @Override // miui.popupcamera.IPopupCameraManager
        public boolean popupMotor() throws RemoteException {
            return false;
        }

        @Override // miui.popupcamera.IPopupCameraManager
        public boolean takebackMotor() throws RemoteException {
            return false;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPopupCameraManager {
        static final int TRANSACTION_calibrationMotor = 5;
        static final int TRANSACTION_getMotorStatus = 4;
        static final int TRANSACTION_getPopupCameraState = 6;
        static final int TRANSACTION_notifyCameraStatus = 1;
        static final int TRANSACTION_popupMotor = 2;
        static final int TRANSACTION_takebackMotor = 3;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IPopupCameraManager {
            public static IPopupCameraManager sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // miui.popupcamera.IPopupCameraManager
            public void calibrationMotor() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPopupCameraManager.DESCRIPTOR);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().calibrationMotor();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IPopupCameraManager.DESCRIPTOR;
            }

            @Override // miui.popupcamera.IPopupCameraManager
            public int getMotorStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPopupCameraManager.DESCRIPTOR);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getMotorStatus();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.popupcamera.IPopupCameraManager
            public int getPopupCameraState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPopupCameraManager.DESCRIPTOR);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getPopupCameraState();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.popupcamera.IPopupCameraManager
            public boolean notifyCameraStatus(int i, int i2, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPopupCameraManager.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().notifyCameraStatus(i, i2, str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.popupcamera.IPopupCameraManager
            public boolean popupMotor() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPopupCameraManager.DESCRIPTOR);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().popupMotor();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.popupcamera.IPopupCameraManager
            public boolean takebackMotor() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPopupCameraManager.DESCRIPTOR);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().takebackMotor();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IPopupCameraManager.DESCRIPTOR);
        }

        public static IPopupCameraManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IPopupCameraManager.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IPopupCameraManager)) ? new Proxy(iBinder) : (IPopupCameraManager) queryLocalInterface;
        }

        public static IPopupCameraManager getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IPopupCameraManager iPopupCameraManager) {
            if (Proxy.sDefaultImpl == null) {
                if (iPopupCameraManager != null) {
                    Proxy.sDefaultImpl = iPopupCameraManager;
                    return true;
                }
                return false;
            }
            throw new IllegalStateException("setDefaultImpl() called twice");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(IPopupCameraManager.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(IPopupCameraManager.DESCRIPTOR);
                    boolean notifyCameraStatus = notifyCameraStatus(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(notifyCameraStatus ? 1 : 0);
                    return true;
                case 2:
                    parcel.enforceInterface(IPopupCameraManager.DESCRIPTOR);
                    boolean popupMotor = popupMotor();
                    parcel2.writeNoException();
                    parcel2.writeInt(popupMotor ? 1 : 0);
                    return true;
                case 3:
                    parcel.enforceInterface(IPopupCameraManager.DESCRIPTOR);
                    boolean takebackMotor = takebackMotor();
                    parcel2.writeNoException();
                    parcel2.writeInt(takebackMotor ? 1 : 0);
                    return true;
                case 4:
                    parcel.enforceInterface(IPopupCameraManager.DESCRIPTOR);
                    int motorStatus = getMotorStatus();
                    parcel2.writeNoException();
                    parcel2.writeInt(motorStatus);
                    return true;
                case 5:
                    parcel.enforceInterface(IPopupCameraManager.DESCRIPTOR);
                    calibrationMotor();
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(IPopupCameraManager.DESCRIPTOR);
                    int popupCameraState = getPopupCameraState();
                    parcel2.writeNoException();
                    parcel2.writeInt(popupCameraState);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void calibrationMotor() throws RemoteException;

    int getMotorStatus() throws RemoteException;

    int getPopupCameraState() throws RemoteException;

    boolean notifyCameraStatus(int i, int i2, String str) throws RemoteException;

    boolean popupMotor() throws RemoteException;

    boolean takebackMotor() throws RemoteException;
}
