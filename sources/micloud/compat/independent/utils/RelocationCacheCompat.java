package micloud.compat.independent.utils;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.micloudsdk.request.utils.RomCountryUtil;
import com.xiaomi.micloudsdk.utils.MiCloudSDKDependencyUtil;

/* loaded from: classes2.dex */
public class RelocationCacheCompat {
    private static final IRelocationCacheCompat sRelocationCacheCompatImpl;

    static {
        if (MiCloudSDKDependencyUtil.SDKEnvironment < 18) {
            sRelocationCacheCompatImpl = new RelocationCacheCompat_Base();
        } else if (TextUtils.isEmpty(RomCountryUtil.getRomCountry())) {
            sRelocationCacheCompatImpl = new RelocationCacheCompat_Base();
        } else {
            sRelocationCacheCompatImpl = new RelocationCacheCompat_V18();
        }
    }

    public static void cacheHostList(Context context, String str) {
        sRelocationCacheCompatImpl.cacheHostList(context, str);
    }

    public static void cacheXiaomiAccountName(Context context, String str) {
        sRelocationCacheCompatImpl.cacheXiaomiAccountName(context, str);
    }

    public static String getCachedHostList(Context context) {
        return sRelocationCacheCompatImpl.getCachedHostList(context);
    }

    public static String getCachedXiaomiAccountName(Context context) {
        return sRelocationCacheCompatImpl.getCachedXiaomiAccountName(context);
    }
}
