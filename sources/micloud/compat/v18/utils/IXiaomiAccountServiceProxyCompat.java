package micloud.compat.v18.utils;

import android.os.IBinder;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface IXiaomiAccountServiceProxyCompat {
    String getSnsAccessToken(IBinder iBinder, String str) throws RemoteException;

    boolean invalidateSnsAccessToken(IBinder iBinder, String str, String str2) throws RemoteException;
}
