package miui.bluetooth.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.RemoteException;
import java.util.List;
import miui.bluetooth.ble.IBluetoothMiBleCallback;
import miui.bluetooth.ble.IBluetoothMiBlePropertyCallback;

/* loaded from: classes3.dex */
public interface IBluetoothMiBle extends IInterface {

    /* loaded from: classes3.dex */
    public static class Default implements IBluetoothMiBle {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean authenticate(String str, ParcelUuid parcelUuid) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean authorize(String str, ParcelUuid parcelUuid, String str2) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public void connect(String str, ParcelUuid parcelUuid) throws RemoteException {
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public void disconnect(String str, ParcelUuid parcelUuid) throws RemoteException {
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public byte[] encrypt(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public List<BluetoothDevice> getConnectedDevices() throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public byte[] getProperty(String str, ParcelUuid parcelUuid, int i) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public int getRssi(String str, ParcelUuid parcelUuid) throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public int getServiceVersion() throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean isConnected(String str) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public void registerClient(IBinder iBinder, String str, ParcelUuid parcelUuid, IBluetoothMiBleCallback iBluetoothMiBleCallback) throws RemoteException {
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean registerPropertyCallback(String str, ParcelUuid parcelUuid, int i, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean setEncryptionKey(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean setProperty(String str, ParcelUuid parcelUuid, int i, byte[] bArr) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean setRssiThreshold(String str, ParcelUuid parcelUuid, int i) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean supportProperty(String str, int i) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public void unregisterClient(IBinder iBinder, String str, ParcelUuid parcelUuid) throws RemoteException {
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean unregisterPropertyCallback(String str, ParcelUuid parcelUuid, int i, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException {
            return false;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IBluetoothMiBle {
        private static final String DESCRIPTOR = "miui.bluetooth.ble.IBluetoothMiBle";
        static final int TRANSACTION_authenticate = 15;
        static final int TRANSACTION_authorize = 13;
        static final int TRANSACTION_connect = 4;
        static final int TRANSACTION_disconnect = 5;
        static final int TRANSACTION_encrypt = 17;
        static final int TRANSACTION_getConnectedDevices = 6;
        static final int TRANSACTION_getProperty = 12;
        static final int TRANSACTION_getRssi = 7;
        static final int TRANSACTION_getServiceVersion = 18;
        static final int TRANSACTION_isConnected = 3;
        static final int TRANSACTION_registerClient = 1;
        static final int TRANSACTION_registerPropertyCallback = 9;
        static final int TRANSACTION_setEncryptionKey = 16;
        static final int TRANSACTION_setProperty = 11;
        static final int TRANSACTION_setRssiThreshold = 14;
        static final int TRANSACTION_supportProperty = 8;
        static final int TRANSACTION_unregisterClient = 2;
        static final int TRANSACTION_unregisterPropertyCallback = 10;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IBluetoothMiBle {
            public static IBluetoothMiBle sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean authenticate(String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(15, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().authenticate(str, parcelUuid);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean authorize(String str, ParcelUuid parcelUuid, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str2);
                    if (this.mRemote.transact(13, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().authorize(str, parcelUuid, str2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public void connect(String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().connect(str, parcelUuid);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public void disconnect(String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().disconnect(str, parcelUuid);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public byte[] encrypt(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(17, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.createByteArray();
                    }
                    return Stub.getDefaultImpl().encrypt(str, parcelUuid, bArr);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public List<BluetoothDevice> getConnectedDevices() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.createTypedArrayList(BluetoothDevice.CREATOR);
                    }
                    return Stub.getDefaultImpl().getConnectedDevices();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public byte[] getProperty(String str, ParcelUuid parcelUuid, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    if (this.mRemote.transact(12, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.createByteArray();
                    }
                    return Stub.getDefaultImpl().getProperty(str, parcelUuid, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public int getRssi(String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getRssi(str, parcelUuid);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public int getServiceVersion() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(18, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getServiceVersion();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean isConnected(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().isConnected(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public void registerClient(IBinder iBinder, String str, ParcelUuid parcelUuid, IBluetoothMiBleCallback iBluetoothMiBleCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iBluetoothMiBleCallback != null ? iBluetoothMiBleCallback.asBinder() : null);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerClient(iBinder, str, parcelUuid, iBluetoothMiBleCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean registerPropertyCallback(String str, ParcelUuid parcelUuid, int i, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBluetoothMiBlePropertyCallback != null ? iBluetoothMiBlePropertyCallback.asBinder() : null);
                    if (this.mRemote.transact(9, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().registerPropertyCallback(str, parcelUuid, i, iBluetoothMiBlePropertyCallback);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean setEncryptionKey(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(16, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().setEncryptionKey(str, parcelUuid, bArr);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean setProperty(String str, ParcelUuid parcelUuid, int i, byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(11, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().setProperty(str, parcelUuid, i, bArr);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean setRssiThreshold(String str, ParcelUuid parcelUuid, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    if (this.mRemote.transact(14, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().setRssiThreshold(str, parcelUuid, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean supportProperty(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(8, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().supportProperty(str, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public void unregisterClient(IBinder iBinder, String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterClient(iBinder, str, parcelUuid);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean unregisterPropertyCallback(String str, ParcelUuid parcelUuid, int i, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBluetoothMiBlePropertyCallback != null ? iBluetoothMiBlePropertyCallback.asBinder() : null);
                    if (this.mRemote.transact(10, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().unregisterPropertyCallback(str, parcelUuid, i, iBluetoothMiBlePropertyCallback);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBluetoothMiBle asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IBluetoothMiBle)) ? new Proxy(iBinder) : (IBluetoothMiBle) queryLocalInterface;
        }

        public static IBluetoothMiBle getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IBluetoothMiBle iBluetoothMiBle) {
            if (Proxy.sDefaultImpl != null || iBluetoothMiBle == null) {
                return false;
            }
            Proxy.sDefaultImpl = iBluetoothMiBle;
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
                    registerClient(parcel.readStrongBinder(), parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, IBluetoothMiBleCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterClient(parcel.readStrongBinder(), parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean isConnected = isConnected(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(isConnected ? 1 : 0);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    connect(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    disconnect(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<BluetoothDevice> connectedDevices = getConnectedDevices();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(connectedDevices);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    int rssi = getRssi(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(rssi);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean supportProperty = supportProperty(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(supportProperty ? 1 : 0);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean registerPropertyCallback = registerPropertyCallback(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), IBluetoothMiBlePropertyCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(registerPropertyCallback ? 1 : 0);
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean unregisterPropertyCallback = unregisterPropertyCallback(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), IBluetoothMiBlePropertyCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(unregisterPropertyCallback ? 1 : 0);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean property = setProperty(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.createByteArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(property ? 1 : 0);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] property2 = getProperty(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeByteArray(property2);
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean authorize = authorize(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(authorize ? 1 : 0);
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean rssiThreshold = setRssiThreshold(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(rssiThreshold ? 1 : 0);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean authenticate = authenticate(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(authenticate ? 1 : 0);
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean encryptionKey = setEncryptionKey(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.createByteArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(encryptionKey ? 1 : 0);
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] encrypt = encrypt(parcel.readString(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.createByteArray());
                    parcel2.writeNoException();
                    parcel2.writeByteArray(encrypt);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    int serviceVersion = getServiceVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(serviceVersion);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    boolean authenticate(String str, ParcelUuid parcelUuid) throws RemoteException;

    boolean authorize(String str, ParcelUuid parcelUuid, String str2) throws RemoteException;

    void connect(String str, ParcelUuid parcelUuid) throws RemoteException;

    void disconnect(String str, ParcelUuid parcelUuid) throws RemoteException;

    byte[] encrypt(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException;

    List<BluetoothDevice> getConnectedDevices() throws RemoteException;

    byte[] getProperty(String str, ParcelUuid parcelUuid, int i) throws RemoteException;

    int getRssi(String str, ParcelUuid parcelUuid) throws RemoteException;

    int getServiceVersion() throws RemoteException;

    boolean isConnected(String str) throws RemoteException;

    void registerClient(IBinder iBinder, String str, ParcelUuid parcelUuid, IBluetoothMiBleCallback iBluetoothMiBleCallback) throws RemoteException;

    boolean registerPropertyCallback(String str, ParcelUuid parcelUuid, int i, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException;

    boolean setEncryptionKey(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException;

    boolean setProperty(String str, ParcelUuid parcelUuid, int i, byte[] bArr) throws RemoteException;

    boolean setRssiThreshold(String str, ParcelUuid parcelUuid, int i) throws RemoteException;

    boolean supportProperty(String str, int i) throws RemoteException;

    void unregisterClient(IBinder iBinder, String str, ParcelUuid parcelUuid) throws RemoteException;

    boolean unregisterPropertyCallback(String str, ParcelUuid parcelUuid, int i, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException;
}
