package miuix.core.util.screenutils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;
import android.view.WindowMetrics;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.screenutils.MultiWindowModeHelper;

/* loaded from: classes5.dex */
public class FreeFormModeHelper {
    private static MultiWindowModeHelper.WindowInfo acquireFreeFormWindowRatioInternal(Context context) {
        int i;
        int i2;
        if (!MiuixUIUtils.isFreeformMode(context)) {
            MultiWindowModeHelper.WindowInfo windowInfo = new MultiWindowModeHelper.WindowInfo();
            windowInfo.windowMode = 8192;
            return windowInfo;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        if (Build.VERSION.SDK_INT >= 30) {
            WindowMetrics currentWindowMetrics = windowManager.getCurrentWindowMetrics();
            i = 0;
            if (currentWindowMetrics == null || currentWindowMetrics.getBounds().width() == 0) {
                i2 = 0;
            } else {
                i = currentWindowMetrics.getBounds().width();
                i2 = currentWindowMetrics.getBounds().height();
                r0 = (i2 * 1.0f) / i;
            }
        } else {
            Point point = new Point();
            windowManager.getDefaultDisplay().getSize(point);
            int i3 = point.x;
            int i4 = point.y;
            r0 = i3 != 0 ? (i4 * 1.0f) / i3 : 0.0f;
            i = i3;
            i2 = i4;
        }
        return freeFormModeRatioToCodeInternal(r0, i, i2);
    }

    public static int detectFreeFormMode(Context context) {
        return detectWindowInfo(context).windowMode;
    }

    public static MultiWindowModeHelper.WindowInfo detectWindowInfo(Context context) {
        return acquireFreeFormWindowRatioInternal(context);
    }

    private static MultiWindowModeHelper.WindowInfo freeFormModeRatioToCodeInternal(float f, int i, int i2) {
        MultiWindowModeHelper.WindowInfo windowInfo = new MultiWindowModeHelper.WindowInfo();
        if (f <= 0.0f) {
            windowInfo.windowMode = 8192;
        } else if (f >= 0.74f && f < 0.76f) {
            windowInfo.windowMode = 8195;
        } else if (f >= 1.32f && f < 1.34f) {
            windowInfo.windowMode = 8194;
        } else if (f < 1.76f || f >= 1.79f) {
            windowInfo.windowMode = 8196;
        } else {
            windowInfo.windowMode = 8193;
        }
        windowInfo.windowWidth = i;
        windowInfo.windowHeight = i2;
        return windowInfo;
    }
}
