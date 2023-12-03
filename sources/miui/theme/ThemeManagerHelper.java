package miui.theme;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.text.TextUtils;
import com.miui.internal.vip.utils.ReflectionUtils;
import com.miui.internal.vip.utils.Utils;
import miui.os.Build;
import miui.os.SystemProperties;

/* loaded from: classes4.dex */
public class ThemeManagerHelper {
    private static final String TAG = "ThemeManagerHelper";

    private ThemeManagerHelper() {
    }

    private static boolean isHideTheme() {
        String str = SystemProperties.get("ro.miui.customized.region", "");
        return "mx_telcel".equals(str) || "lm_cr".equals(str) || "mx_at".equals(str);
    }

    private static boolean isInWorkManagedMode(Context context) {
        Object invokeMethod = ReflectionUtils.invokeMethod((DevicePolicyManager) context.getSystemService("device_policy"), DevicePolicyManager.class, "isDeviceManaged", (Class[]) null, new Object[0]);
        if (invokeMethod != null) {
            try {
                return ((Boolean) invokeMethod).booleanValue();
            } catch (Exception e) {
                Utils.logW("ThemeManagerHelper, Reflect isDeviceManaged failed, e = %s", new Object[]{e});
            }
        }
        return false;
    }

    public static boolean needDisableTheme(Context context) {
        if (Build.IS_TABLET || isHideTheme()) {
            return true;
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            if (isInWorkManagedMode(context)) {
                return true;
            }
            if (GlobalUtils.isEU(context)) {
                String miUiVersionCode = Build.getMiUiVersionCode();
                return TextUtils.isEmpty(miUiVersionCode) || Integer.valueOf(miUiVersionCode).intValue() < 8;
            } else if (GlobalUtils.isReligiousArea(context)) {
                return true;
            }
        }
        return false;
    }
}
