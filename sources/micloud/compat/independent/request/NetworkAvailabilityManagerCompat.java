package micloud.compat.independent.request;

import android.content.Context;
import com.xiaomi.micloudsdk.utils.MiCloudSDKDependencyUtil;

/* loaded from: classes2.dex */
public class NetworkAvailabilityManagerCompat {
    private static final INetworkAvailabilityManagerCompat sNetworkAvailabilityManagerCompatImpl;

    static {
        if (MiCloudSDKDependencyUtil.SDKEnvironment >= 18) {
            sNetworkAvailabilityManagerCompatImpl = new NetworkAvailabilityManagerCompat_V18();
        } else {
            sNetworkAvailabilityManagerCompatImpl = new NetworkAvailabilityManagerCompat_Base();
        }
    }

    public static void setAvailability(Context context, boolean z) {
        sNetworkAvailabilityManagerCompatImpl.setAvailability(context, z);
    }
}
