package miuix.internal.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.WindowManager;
import miuix.core.util.SystemProperties;

/* loaded from: classes5.dex */
public class DeviceHelper {
    public static final boolean IS_DEBUGGABLE;
    public static float PAD_THRESHOLD = 0.0f;
    private static int sMuiltDisplayType = -1;
    private static Point sScreenRealSize;

    static {
        IS_DEBUGGABLE = SystemProperties.getInt("ro.debuggable", 0) == 1;
    }

    public static boolean isFeatureWholeAnim() {
        return true;
    }

    public static boolean isFoldDevice() {
        if (sMuiltDisplayType == -1) {
            sMuiltDisplayType = SystemProperties.getInt("persist.sys.muiltdisplay_type", 0);
        }
        return sMuiltDisplayType == 2;
    }

    public static boolean isHideGestureLine(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "hide_gesture_line", 0) != 0;
    }

    public static boolean isOled() {
        return false;
    }

    public static boolean isTablet(Context context) {
        if (sScreenRealSize == null || isFoldDevice()) {
            sScreenRealSize = new Point();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRealSize(sScreenRealSize);
            PAD_THRESHOLD = Resources.getSystem().getDisplayMetrics().density * 600.0f;
        }
        Point point = sScreenRealSize;
        return ((float) Math.min(point.x, point.y)) >= PAD_THRESHOLD;
    }

    public static boolean isZizhan() {
        return TextUtils.equals("zizhan", SystemProperties.get("ro.product.device"));
    }
}
