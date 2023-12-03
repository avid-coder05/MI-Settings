package miuix.appcompat.app.floatingactivity.multiapp;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes5.dex */
public interface IServiceNotify extends IInterface {

    /* loaded from: classes5.dex */
    public static abstract class Stub extends Binder implements IServiceNotify {
        public Stub() {
            attachInterface(this, "miuix.appcompat.app.floatingactivity.multiapp.IServiceNotify");
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
                parcel2.writeString("miuix.appcompat.app.floatingactivity.multiapp.IServiceNotify");
                return true;
            }
            parcel.enforceInterface("miuix.appcompat.app.floatingactivity.multiapp.IServiceNotify");
            parcel2.writeBundle(notifyFromService(parcel.readInt(), parcel.readBundle()));
            parcel2.writeNoException();
            return true;
        }
    }

    Bundle notifyFromService(int i, Bundle bundle) throws RemoteException;
}
