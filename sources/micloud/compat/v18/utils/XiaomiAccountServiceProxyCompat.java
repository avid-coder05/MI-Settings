package micloud.compat.v18.utils;

import android.os.IBinder;
import android.os.RemoteException;
import com.xiaomi.micloudsdk.utils.MiCloudSdkBuild;

/* loaded from: classes2.dex */
public class XiaomiAccountServiceProxyCompat {
    private static final IXiaomiAccountServiceProxyCompat sXiaomiAccountServiceProxyCompatImpl;
    private static final int version;

    static {
        int i = MiCloudSdkBuild.CURRENT_VERSION;
        version = i;
        if (i >= 20) {
            sXiaomiAccountServiceProxyCompatImpl = new XiaomiAccountServiceProxyCompat_V20();
        } else {
            sXiaomiAccountServiceProxyCompatImpl = new XiaomiAccountServiceProxyCompat_Base();
        }
    }

    public static String getSnsAccessToken(IBinder iBinder, String str) throws RemoteException {
        return sXiaomiAccountServiceProxyCompatImpl.getSnsAccessToken(iBinder, str);
    }

    public static boolean invalidateSnsAccessToken(IBinder iBinder, String str, String str2) throws RemoteException {
        return sXiaomiAccountServiceProxyCompatImpl.invalidateSnsAccessToken(iBinder, str, str2);
    }
}
