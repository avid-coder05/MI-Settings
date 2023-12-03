package miui.telephony;

/* loaded from: classes4.dex */
public class TelephonyManagerUtil {
    public static String getDeviceId() {
        return TelephonyManager.getDefault().getMiuiDeviceId();
    }
}
