package micloud.compat.independent.request;

import android.content.Context;
import android.content.ServiceConnection;
import com.xiaomi.micloudsdk.utils.MiCloudSDKDependencyUtil;

/* loaded from: classes2.dex */
public class BindAccountServiceCompat {
    private static final IBindAccountServiceCompat sBindAccountServiceImpl;

    static {
        if (MiCloudSDKDependencyUtil.SDKEnvironment >= 18) {
            sBindAccountServiceImpl = new BindAccountServiceCompat_V18();
        } else {
            sBindAccountServiceImpl = new BindAccountServiceCompat_Base();
        }
    }

    public static boolean bindAccountService(Context context, ServiceConnection serviceConnection) {
        return sBindAccountServiceImpl.bindAccountService(context, serviceConnection);
    }
}
