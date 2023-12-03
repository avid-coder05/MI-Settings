package miuix.provision;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.Window;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import miui.content.res.ThemeResources;

/* loaded from: classes5.dex */
public class OobeUtil {
    public static final String BUILD_DEVICE;
    public static final boolean IS_L2;
    public static final boolean IS_L3;

    static {
        String str = Build.DEVICE;
        BUILD_DEVICE = str;
        IS_L2 = "zeus".equals(str);
        IS_L3 = "cupid".equals(str);
    }

    private static int getFsgState(boolean z, boolean z2) {
        int stausBarBackcode = z2 ? getStausBarBackcode() | 0 : (~getStausBarBackcode()) & 0;
        return z ? getStausBarHomecode() | stausBarBackcode : (~getStausBarHomecode()) & stausBarBackcode;
    }

    private static int getStausBarBackcode() {
        try {
            Field field = Class.forName("android.app.StatusBarManager").getField("DISABLE_BACK");
            field.setAccessible(true);
            return ((Integer) field.get(null)).intValue();
        } catch (Exception unused) {
            return MiuiWindowManager$LayoutParams.EXTRA_FLAG_ACQUIRES_SLEEP_TOKEN;
        }
    }

    private static int getStausBarHomecode() {
        try {
            Field field = Class.forName("android.app.StatusBarManager").getField("DISABLE_HOME");
            field.setAccessible(true);
            return ((Integer) field.get(null)).intValue();
        } catch (Exception unused) {
            return 2097152;
        }
    }

    public static boolean isDeviceProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
    }

    public static boolean isGreaterOrEqualMIUI130() {
        int i = -1;
        try {
            Method method = Class.forName("android.os.SystemProperties").getMethod("getInt", String.class, Integer.TYPE);
            if (method != null) {
                i = ((Integer) method.invoke(null, "ro.miui.ui.version.code", -1)).intValue();
            }
        } catch (Exception e) {
            Log.i("OobeUtil2", "isGreaterOrEqualMIUI13 get versionCode error " + e.getMessage());
        }
        Log.i("OobeUtil2", "isGreaterOrEqualMIUI13 versionCode:" + i);
        return i >= 13;
    }

    public static boolean isInternationalBuild() {
        String str = null;
        try {
            Method method = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            if (method != null) {
                str = (String) method.invoke(null, "ro.product.mod_device", "");
            }
        } catch (Exception e) {
            Log.i("OobeUtil2", "isInternationalBuild get mod_device error " + e.getMessage());
        }
        Log.i("OobeUtil2", "isInternationalBuild modeDevice:" + str);
        return !TextUtils.isEmpty(str) && str.contains("_global");
    }

    public static boolean isL2orL3() {
        return IS_L2 || IS_L3;
    }

    public static boolean isLandOrientation(Context context) {
        return context != null && context.getResources().getConfiguration().orientation == 2;
    }

    public static boolean isShowIconBtn() {
        return !TextUtils.equals(Locale.getDefault().getLanguage(), Locale.CHINESE.getLanguage());
    }

    public static boolean isTabletDevice() {
        String str = null;
        try {
            Method method = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            if (method != null) {
                str = (String) method.invoke(null, "ro.build.characteristics", "");
            }
        } catch (Exception e) {
            Log.i("OobeUtil2", "isTabletDevice get characteristics error " + e.getMessage());
        }
        Log.i("OobeUtil2", "isTabletDevice characteristics:" + str);
        return !TextUtils.isEmpty(str) && str.contains("tablet");
    }

    public static boolean isTabletLand(Context context) {
        return isLandOrientation(context) && isTabletDevice();
    }

    public static boolean needFastAnimation() {
        return isGreaterOrEqualMIUI130() && Build.VERSION.SDK_INT > 30 && !isInternationalBuild() && !isL2orL3();
    }

    public static void setGestureHomeClose(Context context, boolean z, boolean z2) {
        Log.i("OobeUtil2", "setGestureHomeClose isDisableHomeOrRecent=" + z + ",isDisableBack=" + z2 + ",getFsgState=" + getFsgState(z, z2));
        Intent intent = new Intent();
        intent.setPackage(ThemeResources.SYSTEMUI_NAME);
        intent.setAction("com.android.systemui.fsgesture");
        intent.putExtra("typeFrom", "typefrom_provision");
        intent.putExtra("isEnter", z);
        intent.putExtra("fsgState", getFsgState(z, z2));
        context.sendBroadcast(intent);
    }

    public static void setHideNavigationBar(Window window) {
        window.getDecorView().setSystemUiVisibility(8450);
    }

    public static void setNavigationBarFullScreen(Context context, boolean z) {
        Settings.Global.putInt(context.getContentResolver(), "force_fsg_nav_bar", z ? 1 : 0);
    }

    public static void updateViewVisibility(View view, View view2) {
        if (view2 == null) {
            return;
        }
        boolean isShowIconBtn = isShowIconBtn();
        if (view != null) {
            view.setVisibility(isShowIconBtn ? 8 : 0);
        }
        view2.setVisibility(isShowIconBtn ? 0 : 8);
    }
}
