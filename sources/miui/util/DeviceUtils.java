package miui.util;

import android.content.Context;
import android.telephony.TelephonyManager;

@Deprecated
/* loaded from: classes4.dex */
public class DeviceUtils {
    @Deprecated
    public static String getDeviceId(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
    }
}
