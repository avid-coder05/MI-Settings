package miuix.autodensity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import java.lang.reflect.Method;
import miuix.core.util.SystemProperties;
import miuix.internal.util.DeviceHelper;
import miuix.provider.ExtraSettings$Secure;

/* loaded from: classes5.dex */
public class DensityUtil {
    private static Method sDefaultDensityMethod;

    private static float calcPadScale(Context context) {
        return Math.max(1.0f, (getMaxSizeInch(context) / 9.3f) * 1.06f);
    }

    private static float calcPhoneScale(Context context) {
        float minSizeInch = getMinSizeInch(context);
        if (minSizeInch < 2.7f) {
            return minSizeInch / 2.8f;
        }
        return 1.0f;
    }

    private static void changeDensity(Resources resources, int i, float f) {
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        int standardScalescale = (int) (i * getStandardScalescale() * f);
        float convert = convert((standardScalescale * 1.0f) / displayMetrics.densityDpi);
        DebugUtil.printDensityLog("before changeDensity displayMetrics:" + displayMetrics);
        DebugUtil.printDensityLog("changeDensity oldDpi: " + displayMetrics.densityDpi + ", targetDpi:" + standardScalescale + ", scale:" + convert);
        displayMetrics.densityDpi = standardScalescale;
        displayMetrics.density = displayMetrics.density * convert;
        displayMetrics.scaledDensity = displayMetrics.scaledDensity * convert;
        configuration.densityDpi = standardScalescale;
        configuration.fontScale = configuration.fontScale * convert;
        Resources.getSystem().getDisplayMetrics().densityDpi = standardScalescale;
        Resources.getSystem().getDisplayMetrics().density = displayMetrics.density;
        Resources.getSystem().getDisplayMetrics().scaledDensity = displayMetrics.scaledDensity;
        Resources.getSystem().getConfiguration().densityDpi = standardScalescale;
        Resources.getSystem().getConfiguration().fontScale = configuration.fontScale;
        setBitmapDefaultDensity(standardScalescale);
        DebugUtil.printDensityLog("after changeDensity " + displayMetrics);
        DensityConfigManager.getInstance().checkUpdateCurrent(new DensityConfig(displayMetrics));
    }

    private static float convert(float f) {
        return f;
    }

    public static int getAccessibilityDefaultDisplayDpi(int i) {
        try {
            if (TextUtils.isEmpty(SystemProperties.get("persist.sys.miui_resolution", null))) {
                return getDefaultDisplayDensity(i);
            }
            Point point = new Point();
            WindowManagerGlobal.getWindowManagerService().getInitialDisplaySize(0, point);
            return Math.round(((r2.getInitialDisplayDensity(i) * Integer.valueOf(r0.split(",")[0]).intValue()) * 1.0f) / point.x);
        } catch (Throwable unused) {
            return -1;
        }
    }

    private static float getAccessibilityDelta(Context context) {
        int i;
        int accessibilityDefaultDisplayDpi = getAccessibilityDefaultDisplayDpi(0);
        DebugUtil.printDensityLog("default dpi: " + accessibilityDefaultDisplayDpi);
        if (accessibilityDefaultDisplayDpi != -1) {
            try {
                i = ExtraSettings$Secure.getInt(context.getContentResolver(), "display_density_forced");
            } catch (Settings.SettingNotFoundException e) {
                DebugUtil.printDensityLog("Exception: " + e);
                i = accessibilityDefaultDisplayDpi;
            }
            float f = (i * 1.0f) / accessibilityDefaultDisplayDpi;
            DebugUtil.printDensityLog("accessibility dpi: " + i + ", delta: " + f);
            return f;
        }
        return 1.0f;
    }

    private static float getDebugScale() {
        if (RootUtil.isDeviceRooted()) {
            return DebugUtil.getAutoDensityScaleInDebugMode();
        }
        return 0.0f;
    }

    private static int getDefaultDisplayDensity(int i) {
        try {
            return WindowManagerGlobal.getWindowManagerService().getInitialDisplayDensity(i);
        } catch (Throwable unused) {
            return -1;
        }
    }

    private static float getDeviceScale(Context context) {
        float skuScale = SkuScale.hasSkuScale() ? SkuScale.getSkuScale(context) : DeviceHelper.isFoldDevice() ? isTreatFoldAsPhone() ? calcPhoneScale(context) : 1.0f : DeviceHelper.isTablet(context) ? calcPadScale(context) : calcPhoneScale(context);
        DebugUtil.printDensityLog("getDeviceScale " + skuScale);
        return skuScale;
    }

    private static float getMaxSizeInch(Context context) {
        Point physicalSize = getPhysicalSize(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float max = Math.max(displayMetrics.xdpi, displayMetrics.ydpi);
        return Math.max(Math.min(physicalSize.x, physicalSize.y) / Math.min(displayMetrics.xdpi, displayMetrics.ydpi), Math.max(physicalSize.x, physicalSize.y) / max);
    }

    private static float getMinSizeInch(Context context) {
        Point physicalSize = getPhysicalSize(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float max = Math.max(displayMetrics.xdpi, displayMetrics.ydpi);
        return Math.min(Math.min(physicalSize.x, physicalSize.y) / Math.min(displayMetrics.xdpi, displayMetrics.ydpi), Math.max(physicalSize.x, physicalSize.y) / max);
    }

    public static int getPPIOfDevice(Context context) {
        Point physicalSize = getPhysicalSize(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        DebugUtil.printDensityLog("phsical size: " + physicalSize + ", display xdpi: " + displayMetrics.xdpi + ", ydpi: " + displayMetrics.ydpi);
        float max = Math.max(displayMetrics.xdpi, displayMetrics.ydpi);
        float min = Math.min(displayMetrics.xdpi, displayMetrics.ydpi);
        float max2 = (float) Math.max(physicalSize.x, physicalSize.y);
        float min2 = (float) Math.min(physicalSize.x, physicalSize.y);
        float f = max2 / max;
        float f2 = min2 / min;
        double sqrt = Math.sqrt(Math.pow((double) f, 2.0d) + Math.pow((double) f2, 2.0d));
        int sqrt2 = (int) (Math.sqrt(Math.pow(max2, 2.0d) + Math.pow(min2, 2.0d)) / sqrt);
        DebugUtil.printDensityLog("Screen inches : " + sqrt + ", ppi:" + sqrt2 + ",physicalX:" + f + ",physicalY:" + f2 + ",min size inches: " + (Math.min(f2, f) / 2.8f) + ", real point:" + physicalSize);
        return sqrt2;
    }

    private static Point getPhysicalSize(Context context) {
        Point point = new Point();
        getWindowManager(context).getDefaultDisplay().getRealSize(point);
        return point;
    }

    public static float getStandardScalescale() {
        return 1.1398964f;
    }

    private static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService("window");
    }

    private static boolean isTreatFoldAsPhone() {
        return DeviceHelper.isZizhan();
    }

    private static void setBitmapDefaultDensity(int i) {
        try {
            if (sDefaultDensityMethod == null) {
                sDefaultDensityMethod = Class.forName("android.graphics.Bitmap").getMethod("setDefaultDensity", Integer.TYPE);
            }
            sDefaultDensityMethod.invoke(null, Integer.valueOf(i));
        } catch (Exception e) {
            DebugUtil.printDensityLog("reflect exception: " + e.toString());
        }
    }

    public static void setToDefaultDensity(Context context) {
        if (getDebugScale() < 0.0f) {
            return;
        }
        ResourcesImplHelper.setImpl(context.getResources());
    }

    public static void updateCustomDensity(Context context) {
        if (context == null) {
            Log.w("AutoDensity", "context should not null");
            return;
        }
        float debugScale = getDebugScale();
        if (debugScale < 0.0f) {
            Log.d("AutoDensity", "disable auto density in debug mode");
            return;
        }
        if (debugScale == 0.0f) {
            debugScale = getDeviceScale(context);
        }
        changeDensity(context.getResources(), getPPIOfDevice(context), debugScale * getAccessibilityDelta(context));
    }
}
