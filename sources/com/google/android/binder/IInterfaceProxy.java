package com.google.android.binder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public class IInterfaceProxy implements IInterface {
    private final String mDescriptor;
    private final IBinder mRemote;

    /* JADX INFO: Access modifiers changed from: protected */
    public IInterfaceProxy(IBinder iBinder, String str) {
        this.mRemote = iBinder;
        this.mDescriptor = str;
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this.mRemote;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Parcel obtainData() {
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken(this.mDescriptor);
        return obtain;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void transact(int i, Parcel parcel) throws RemoteException {
        Parcel obtain = Parcel.obtain();
        try {
            this.mRemote.transact(i, parcel, obtain, 1);
            obtain.readException();
        } finally {
            obtain.recycle();
            parcel.recycle();
        }
    }
}
