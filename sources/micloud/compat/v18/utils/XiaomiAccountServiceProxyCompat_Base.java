package micloud.compat.v18.utils;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
class XiaomiAccountServiceProxyCompat_Base implements IXiaomiAccountServiceProxyCompat {
    @Override // micloud.compat.v18.utils.IXiaomiAccountServiceProxyCompat
    public String getSnsAccessToken(IBinder iBinder, String str) throws RemoteException {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("com.xiaomi.accountsdk.account.IXiaomiAccountService");
            obtain.writeString(str);
            iBinder.transact(10, obtain, obtain2, 0);
            obtain2.readException();
            return obtain2.readString();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    @Override // micloud.compat.v18.utils.IXiaomiAccountServiceProxyCompat
    public boolean invalidateSnsAccessToken(IBinder iBinder, String str, String str2) throws RemoteException {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("com.xiaomi.accountsdk.account.IXiaomiAccountService");
            obtain.writeString(str);
            obtain.writeString(str2);
            iBinder.transact(11, obtain, obtain2, 0);
            obtain2.readException();
            return obtain2.readInt() != 0;
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }
}
