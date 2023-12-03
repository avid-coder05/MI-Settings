package miuix.core.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import miui.content.res.ThemeResources;
import miuix.reflect.Reflects;

/* loaded from: classes5.dex */
public class MiuixUIUtils {
    public static boolean checkDeviceHasNavigationBar(Context context) {
        String str = SystemProperties.get("qemu.hw.mainkeys");
        if ("1".equals(str)) {
            return false;
        }
        if ("0".equals(str)) {
            return true;
        }
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("config_showNavigationBar", "bool", ThemeResources.FRAMEWORK_PACKAGE);
        if (identifier > 0) {
            return resources.getBoolean(identifier);
        }
        return false;
    }

    private static boolean checkMultiWindow(Activity activity) {
        if (Build.VERSION.SDK_INT >= 24) {
            return activity.isInMultiWindowMode();
        }
        return false;
    }

    public static int getNavigationBarHeight(Context context) {
        int realNavigationBarHeight = (isShowNavigationHandle(context) || !isNavigationBarFullScreen(context)) ? getRealNavigationBarHeight(context) : 0;
        int i = realNavigationBarHeight >= 0 ? realNavigationBarHeight : 0;
        Log.i("MiuixUtils", "getNavigationBarHeight = " + i);
        return i;
    }

    private static Point getPhysicalSize(Context context) {
        Point point = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        Display defaultDisplay = windowManager.getDefaultDisplay();
        try {
            Object obj = Reflects.get(defaultDisplay, Reflects.getDeclaredField(defaultDisplay.getClass(), "mDisplayInfo"));
            point.x = ((Integer) Reflects.get(obj, Reflects.getField(obj.getClass(), "logicalWidth"))).intValue();
            point.y = ((Integer) Reflects.get(obj, Reflects.getField(obj.getClass(), "logicalHeight"))).intValue();
        } catch (Exception e) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
            point.x = displayMetrics.widthPixels;
            point.y = displayMetrics.heightPixels;
            Log.w("MiuixUtils", "catch error! failed to get physical size", e);
        }
        return point;
    }

    public static int getRealNavigationBarHeight(Context context) {
        if (checkDeviceHasNavigationBar(context)) {
            Resources resources = context.getResources();
            int identifier = resources.getIdentifier("navigation_bar_height", "dimen", ThemeResources.FRAMEWORK_PACKAGE);
            int dimensionPixelSize = identifier > 0 ? resources.getDimensionPixelSize(identifier) : 0;
            Log.i("MiuixUtils", "getNavigationBarHeightFromProp = " + dimensionPixelSize);
            return dimensionPixelSize;
        }
        return 0;
    }

    public static int getStatusBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", ThemeResources.FRAMEWORK_PACKAGE);
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static boolean isEnableGestureLine(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "hide_gesture_line", 0) == 0;
    }

    public static boolean isFreeformMode(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        Point physicalSize = getPhysicalSize(context);
        return context.getResources().getConfiguration().toString().contains("mWindowingMode=freeform") && ((((float) point.x) + 0.0f) / ((float) physicalSize.x) <= 0.9f || (((float) point.y) + 0.0f) / ((float) physicalSize.y) <= 0.9f);
    }

    public static boolean isInMultiWindowMode(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return checkMultiWindow((Activity) context);
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return false;
    }

    public static boolean isNavigationBarFullScreen(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
    }

    public static boolean isShowNavigationHandle(Context context) {
        return isEnableGestureLine(context) && isNavigationBarFullScreen(context) && isSupportGestureLine(context);
    }

    public static boolean isSupportGestureLine(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "use_gesture_version_three", 0) != 0;
    }
}
