package miui.cloud.finddevice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

@Deprecated
/* loaded from: classes3.dex */
public interface IFindDeviceStatusManager extends IInterface {

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IFindDeviceStatusManager {
        public Stub() {
            throw new RuntimeException("Stub!");
        }

        public static IFindDeviceStatusManager asInterface(IBinder iBinder) {
            throw new RuntimeException("Stub!");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            throw new RuntimeException("Stub!");
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            throw new RuntimeException("Stub!");
        }
    }

    FindDeviceOperationResult close() throws RemoteException;

    FindDeviceOperationResult ft() throws RemoteException;

    FindDeviceInfo getFindDeviceInfo() throws RemoteException;

    FindDeviceOperationResult getFindDeviceInfoFromServer(FindDeviceInfo findDeviceInfo) throws RemoteException;

    FindDeviceOperationResult getFindDeviceInfoWithLockMessageFromServer(FindDeviceInfoWithLockMessage findDeviceInfoWithLockMessage) throws RemoteException;

    String getLastSessionUserId() throws RemoteException;

    boolean isLastStatusLocked() throws RemoteException;

    boolean isLastStatusOpen() throws RemoteException;

    boolean isLocked() throws RemoteException;

    boolean isOpen() throws RemoteException;

    FindDeviceOperationResult open(boolean z) throws RemoteException;

    FindDeviceOperationResult purge() throws RemoteException;

    FindDeviceOperationResult unlock() throws RemoteException;
}
