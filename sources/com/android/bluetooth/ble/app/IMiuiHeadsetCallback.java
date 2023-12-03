package com.android.bluetooth.ble.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IMiuiHeadsetCallback extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IMiuiHeadsetCallback {
        public Stub() {
            attachInterface(this, "com.android.bluetooth.ble.app.IMiuiHeadsetCallback");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.android.bluetooth.ble.app.IMiuiHeadsetCallback");
                return true;
            } else if (i != 1) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.android.bluetooth.ble.app.IMiuiHeadsetCallback");
                refreshStatus(parcel.readString(), parcel.readString());
                return true;
            }
        }
    }

    void refreshStatus(String str, String str2) throws RemoteException;
}
