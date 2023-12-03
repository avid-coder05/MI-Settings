package miui.cloud.push;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes3.dex */
public interface ISecurityContextManager extends IInterface {

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISecurityContextManager {
        public Stub() {
            throw new RuntimeException("Stub!");
        }

        public static ISecurityContextManager asInterface(IBinder iBinder) {
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

    void update(ISecurityContextManagerUpdateResultCallback iSecurityContextManagerUpdateResultCallback) throws RemoteException;
}
