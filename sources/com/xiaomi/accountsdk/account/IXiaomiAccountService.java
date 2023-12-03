package com.xiaomi.accountsdk.account;

import android.accounts.Account;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface IXiaomiAccountService extends IInterface {

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IXiaomiAccountService {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IXiaomiAccountService {
            public static IXiaomiAccountService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.xiaomi.accountsdk.account.IXiaomiAccountService
            public ParcelFileDescriptor getAvatarFd(Account account) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.accountsdk.account.IXiaomiAccountService");
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0 ? (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(obtain2) : null;
                    }
                    return Stub.getDefaultImpl().getAvatarFd(account);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xiaomi.accountsdk.account.IXiaomiAccountService
            public String getUserName(Account account) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.accountsdk.account.IXiaomiAccountService");
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readString();
                    }
                    return Stub.getDefaultImpl().getUserName(account);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IXiaomiAccountService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.xiaomi.accountsdk.account.IXiaomiAccountService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IXiaomiAccountService)) ? new Proxy(iBinder) : (IXiaomiAccountService) queryLocalInterface;
        }

        public static IXiaomiAccountService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    ParcelFileDescriptor getAvatarFd(Account account) throws RemoteException;

    String getUserName(Account account) throws RemoteException;
}
