package miui.bluetooth.ble;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.RemoteException;
import java.util.List;
import java.util.Map;
import miui.bluetooth.ble.IBleEventCallback;
import miui.bluetooth.ble.IScanDeviceCallback;

/* loaded from: classes3.dex */
public interface IMiBleDeviceManager extends IInterface {

    /* loaded from: classes3.dex */
    public static class Default implements IMiBleDeviceManager {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean deleteSettings(String str) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public List<String> getBoundDevices() throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public Map getDeviceSettings(String str) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public int getDeviceType(String str) throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public String getRegisterAppForBleEvent(String str, int i) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public ScanResult getScanResult(String str) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public int getServiceVersion() throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public int getSettingInteger(String str, String str2) throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public String getSettingString(String str, String str2) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean registerAppForBleEvent(String str, int i) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean registerBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean setSettingInteger(String str, String str2, int i) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean setSettingString(String str, String str2, String str3) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean setToken(String str, byte[] bArr) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean startScanDevice(IBinder iBinder, ParcelUuid parcelUuid, int i, IScanDeviceCallback iScanDeviceCallback) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public void stopScanDevice(ParcelUuid parcelUuid) throws RemoteException {
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean unregisterAppForBleEvent(String str, int i) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean unregisterBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) throws RemoteException {
            return false;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IMiBleDeviceManager {
        private static final String DESCRIPTOR = "miui.bluetooth.ble.IMiBleDeviceManager";
        static final int TRANSACTION_deleteSettings = 6;
        static final int TRANSACTION_getBoundDevices = 13;
        static final int TRANSACTION_getDeviceSettings = 5;
        static final int TRANSACTION_getDeviceType = 7;
        static final int TRANSACTION_getRegisterAppForBleEvent = 16;
        static final int TRANSACTION_getScanResult = 17;
        static final int TRANSACTION_getServiceVersion = 12;
        static final int TRANSACTION_getSettingInteger = 4;
        static final int TRANSACTION_getSettingString = 2;
        static final int TRANSACTION_registerAppForBleEvent = 14;
        static final int TRANSACTION_registerBleEventListener = 10;
        static final int TRANSACTION_setSettingInteger = 3;
        static final int TRANSACTION_setSettingString = 1;
        static final int TRANSACTION_setToken = 18;
        static final int TRANSACTION_startScanDevice = 8;
        static final int TRANSACTION_stopScanDevice = 9;
        static final int TRANSACTION_unregisterAppForBleEvent = 15;
        static final int TRANSACTION_unregisterBleEventListener = 11;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IMiBleDeviceManager {
            public static IMiBleDeviceManager sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean deleteSettings(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().deleteSettings(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public List<String> getBoundDevices() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(13, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.createStringArrayList();
                    }
                    return Stub.getDefaultImpl().getBoundDevices();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public Map getDeviceSettings(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readHashMap(getClass().getClassLoader());
                    }
                    return Stub.getDefaultImpl().getDeviceSettings(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public int getDeviceType(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getDeviceType(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public String getRegisterAppForBleEvent(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(16, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readString();
                    }
                    return Stub.getDefaultImpl().getRegisterAppForBleEvent(str, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public ScanResult getScanResult(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(17, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0 ? ScanResult.CREATOR.createFromParcel(obtain2) : null;
                    }
                    return Stub.getDefaultImpl().getScanResult(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public int getServiceVersion() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(12, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getServiceVersion();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public int getSettingInteger(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getSettingInteger(str, str2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public String getSettingString(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readString();
                    }
                    return Stub.getDefaultImpl().getSettingString(str, str2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean registerAppForBleEvent(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(14, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().registerAppForBleEvent(str, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean registerBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBleEventCallback != null ? iBleEventCallback.asBinder() : null);
                    if (this.mRemote.transact(10, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().registerBleEventListener(str, i, iBleEventCallback);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean setSettingInteger(String str, String str2, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().setSettingInteger(str, str2, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean setSettingString(String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().setSettingString(str, str2, str3);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean setToken(String str, byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(18, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().setToken(str, bArr);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean startScanDevice(IBinder iBinder, ParcelUuid parcelUuid, int i, IScanDeviceCallback iScanDeviceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iScanDeviceCallback != null ? iScanDeviceCallback.asBinder() : null);
                    if (this.mRemote.transact(8, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().startScanDevice(iBinder, parcelUuid, i, iScanDeviceCallback);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public void stopScanDevice(ParcelUuid parcelUuid) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parcelUuid != null) {
                        obtain.writeInt(1);
                        parcelUuid.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(9, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().stopScanDevice(parcelUuid);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean unregisterAppForBleEvent(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(15, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().unregisterAppForBleEvent(str, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean unregisterBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBleEventCallback != null ? iBleEventCallback.asBinder() : null);
                    if (this.mRemote.transact(11, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().unregisterBleEventListener(str, i, iBleEventCallback);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMiBleDeviceManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMiBleDeviceManager)) ? new Proxy(iBinder) : (IMiBleDeviceManager) queryLocalInterface;
        }

        public static IMiBleDeviceManager getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IMiBleDeviceManager iMiBleDeviceManager) {
            if (Proxy.sDefaultImpl != null || iMiBleDeviceManager == null) {
                return false;
            }
            Proxy.sDefaultImpl = iMiBleDeviceManager;
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
                    boolean settingString = setSettingString(parcel.readString(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(settingString ? 1 : 0);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    String settingString2 = getSettingString(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeString(settingString2);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean settingInteger = setSettingInteger(parcel.readString(), parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(settingInteger ? 1 : 0);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    int settingInteger2 = getSettingInteger(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(settingInteger2);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    Map deviceSettings = getDeviceSettings(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeMap(deviceSettings);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean deleteSettings = deleteSettings(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(deleteSettings ? 1 : 0);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    int deviceType = getDeviceType(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(deviceType);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean startScanDevice = startScanDevice(parcel.readStrongBinder(), parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), IScanDeviceCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(startScanDevice ? 1 : 0);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    stopScanDevice(parcel.readInt() != 0 ? (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean registerBleEventListener = registerBleEventListener(parcel.readString(), parcel.readInt(), IBleEventCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(registerBleEventListener ? 1 : 0);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean unregisterBleEventListener = unregisterBleEventListener(parcel.readString(), parcel.readInt(), IBleEventCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(unregisterBleEventListener ? 1 : 0);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    int serviceVersion = getServiceVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(serviceVersion);
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<String> boundDevices = getBoundDevices();
                    parcel2.writeNoException();
                    parcel2.writeStringList(boundDevices);
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean registerAppForBleEvent = registerAppForBleEvent(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(registerAppForBleEvent ? 1 : 0);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean unregisterAppForBleEvent = unregisterAppForBleEvent(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(unregisterAppForBleEvent ? 1 : 0);
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    String registerAppForBleEvent2 = getRegisterAppForBleEvent(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeString(registerAppForBleEvent2);
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    ScanResult scanResult = getScanResult(parcel.readString());
                    parcel2.writeNoException();
                    if (scanResult != null) {
                        parcel2.writeInt(1);
                        scanResult.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean token = setToken(parcel.readString(), parcel.createByteArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(token ? 1 : 0);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    boolean deleteSettings(String str) throws RemoteException;

    List<String> getBoundDevices() throws RemoteException;

    Map getDeviceSettings(String str) throws RemoteException;

    int getDeviceType(String str) throws RemoteException;

    String getRegisterAppForBleEvent(String str, int i) throws RemoteException;

    ScanResult getScanResult(String str) throws RemoteException;

    int getServiceVersion() throws RemoteException;

    int getSettingInteger(String str, String str2) throws RemoteException;

    String getSettingString(String str, String str2) throws RemoteException;

    boolean registerAppForBleEvent(String str, int i) throws RemoteException;

    boolean registerBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) throws RemoteException;

    boolean setSettingInteger(String str, String str2, int i) throws RemoteException;

    boolean setSettingString(String str, String str2, String str3) throws RemoteException;

    boolean setToken(String str, byte[] bArr) throws RemoteException;

    boolean startScanDevice(IBinder iBinder, ParcelUuid parcelUuid, int i, IScanDeviceCallback iScanDeviceCallback) throws RemoteException;

    void stopScanDevice(ParcelUuid parcelUuid) throws RemoteException;

    boolean unregisterAppForBleEvent(String str, int i) throws RemoteException;

    boolean unregisterBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) throws RemoteException;
}
