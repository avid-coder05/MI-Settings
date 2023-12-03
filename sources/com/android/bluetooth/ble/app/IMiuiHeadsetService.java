package com.android.bluetooth.ble.app;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IMiuiHeadsetService extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IMiuiHeadsetService {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IMiuiHeadsetService {
            public static IMiuiHeadsetService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void changeAncLevel(String str, BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    obtain.writeString(str);
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(10, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().changeAncLevel(str, bluetoothDevice);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void changeAncMode(int i, BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    obtain.writeInt(i);
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(9, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().changeAncMode(i, bluetoothDevice);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void changePlayStatus(int i, BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    obtain.writeInt(i);
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(8, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().changePlayStatus(i, bluetoothDevice);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public String checkSupport(BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readString();
                    }
                    return Stub.getDefaultImpl().checkSupport(bluetoothDevice);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void connect(BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().connect(bluetoothDevice);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void disconnect(BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().disconnect(bluetoothDevice);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void getDeviceConfig(BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(12, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().getDeviceConfig(bluetoothDevice);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public String getDeviceInfo(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    obtain.writeString(str);
                    if (this.mRemote.transact(11, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readString();
                    }
                    return Stub.getDefaultImpl().getDeviceInfo(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void localOta(BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(13, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().localOta(bluetoothDevice);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void register(IMiuiHeadsetCallback iMiuiHeadsetCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    obtain.writeStrongBinder(iMiuiHeadsetCallback != null ? iMiuiHeadsetCallback.asBinder() : null);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().register(iMiuiHeadsetCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public String setCommonCommand(int i, String str, BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(14, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readString();
                    }
                    return Stub.getDefaultImpl().setCommonCommand(i, str, bluetoothDevice);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void setFunKey(int i, int i2, BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setFunKey(i, i2, bluetoothDevice);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void startOta(BluetoothDevice bluetoothDevice, String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().startOta(bluetoothDevice, str, str2, str3);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.bluetooth.ble.app.IMiuiHeadsetService
            public void unregister(IMiuiHeadsetCallback iMiuiHeadsetCallback, BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.bluetooth.ble.app.IMiuiHeadsetService");
                    obtain.writeStrongBinder(iMiuiHeadsetCallback != null ? iMiuiHeadsetCallback.asBinder() : null);
                    if (bluetoothDevice != null) {
                        obtain.writeInt(1);
                        bluetoothDevice.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregister(iMiuiHeadsetCallback, bluetoothDevice);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IMiuiHeadsetService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.bluetooth.ble.app.IMiuiHeadsetService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMiuiHeadsetService)) ? new Proxy(iBinder) : (IMiuiHeadsetService) queryLocalInterface;
        }

        public static IMiuiHeadsetService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void changeAncLevel(String str, BluetoothDevice bluetoothDevice) throws RemoteException;

    void changeAncMode(int i, BluetoothDevice bluetoothDevice) throws RemoteException;

    void changePlayStatus(int i, BluetoothDevice bluetoothDevice) throws RemoteException;

    String checkSupport(BluetoothDevice bluetoothDevice) throws RemoteException;

    void connect(BluetoothDevice bluetoothDevice) throws RemoteException;

    void disconnect(BluetoothDevice bluetoothDevice) throws RemoteException;

    void getDeviceConfig(BluetoothDevice bluetoothDevice) throws RemoteException;

    String getDeviceInfo(String str) throws RemoteException;

    void localOta(BluetoothDevice bluetoothDevice) throws RemoteException;

    void register(IMiuiHeadsetCallback iMiuiHeadsetCallback) throws RemoteException;

    String setCommonCommand(int i, String str, BluetoothDevice bluetoothDevice) throws RemoteException;

    void setFunKey(int i, int i2, BluetoothDevice bluetoothDevice) throws RemoteException;

    void startOta(BluetoothDevice bluetoothDevice, String str, String str2, String str3) throws RemoteException;

    void unregister(IMiuiHeadsetCallback iMiuiHeadsetCallback, BluetoothDevice bluetoothDevice) throws RemoteException;
}
