package miui.cloud;

import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.xiaomi.micloudsdk.remote.RemoteMethodInvoker;
import micloud.compat.independent.request.BindAccountServiceCompat;
import micloud.compat.v18.utils.XiaomiAccountServiceProxyCompat;

/* loaded from: classes3.dex */
public class XiaomiAccountManager {
    private static final String TAG = "XiaomiAccountManager";

    public static String getSnsAccessToken(Context context, final String str) {
        return new RemoteMethodInvoker<String>(context) { // from class: miui.cloud.XiaomiAccountManager.1
            @Override // com.xiaomi.micloudsdk.remote.RemoteMethodInvoker
            protected boolean bindService(Context context2, ServiceConnection serviceConnection) {
                return BindAccountServiceCompat.bindAccountService(context2, serviceConnection);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaomi.micloudsdk.remote.RemoteMethodInvoker
            public String invokeRemoteMethod(IBinder iBinder) throws RemoteException {
                return XiaomiAccountServiceProxyCompat.getSnsAccessToken(iBinder, str);
            }
        }.invoke();
    }

    public static boolean invalidateSnsAccessToken(Context context, final String str, final String str2) {
        Boolean invoke = new RemoteMethodInvoker<Boolean>(context) { // from class: miui.cloud.XiaomiAccountManager.2
            @Override // com.xiaomi.micloudsdk.remote.RemoteMethodInvoker
            protected boolean bindService(Context context2, ServiceConnection serviceConnection) {
                return BindAccountServiceCompat.bindAccountService(context2, serviceConnection);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.xiaomi.micloudsdk.remote.RemoteMethodInvoker
            public Boolean invokeRemoteMethod(IBinder iBinder) throws RemoteException {
                return Boolean.valueOf(XiaomiAccountServiceProxyCompat.invalidateSnsAccessToken(iBinder, str, str2));
            }
        }.invoke();
        if (invoke == null) {
            return false;
        }
        return invoke.booleanValue();
    }
}
