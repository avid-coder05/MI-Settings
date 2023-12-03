package micloud.compat.v18.utils;

import android.os.IBinder;
import android.os.RemoteException;
import com.xiaomi.micloudsdk.utils.IXiaomiAccountServiceProxy;

/* loaded from: classes2.dex */
class XiaomiAccountServiceProxyCompat_V20 extends XiaomiAccountServiceProxyCompat_Base {
    @Override // micloud.compat.v18.utils.XiaomiAccountServiceProxyCompat_Base, micloud.compat.v18.utils.IXiaomiAccountServiceProxyCompat
    public String getSnsAccessToken(IBinder iBinder, String str) throws RemoteException {
        return IXiaomiAccountServiceProxy.getSnsAccessToken(iBinder, str);
    }

    @Override // micloud.compat.v18.utils.XiaomiAccountServiceProxyCompat_Base, micloud.compat.v18.utils.IXiaomiAccountServiceProxyCompat
    public boolean invalidateSnsAccessToken(IBinder iBinder, String str, String str2) throws RemoteException {
        return IXiaomiAccountServiceProxy.invalidateSnsAccessToken(iBinder, str, str2);
    }
}
