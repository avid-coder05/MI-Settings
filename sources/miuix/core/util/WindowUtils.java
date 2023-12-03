package miuix.core.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/* loaded from: classes5.dex */
public class WindowUtils {
    public static Display getDisplay(Context context) {
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                return context.getDisplay();
            } catch (UnsupportedOperationException unused) {
                Log.w("WindowUtils", "This context is not associated with a display. You should use createDisplayContext() to create a display context to work with windows.");
            }
        }
        return getWindowManager(context).getDefaultDisplay();
    }

    public static void getScreenAndWindowSize(Context context, Point point, Point point2) {
        WindowManager windowManager = getWindowManager(context);
        int i = Build.VERSION.SDK_INT;
        if (i >= 31) {
            Rect bounds = windowManager.getMaximumWindowMetrics().getBounds();
            point.x = bounds.width();
            point.y = bounds.height();
            Rect bounds2 = windowManager.getCurrentWindowMetrics().getBounds();
            point2.x = bounds2.width();
            point2.y = bounds2.height();
        } else if (i != 30) {
            if (MiuixUIUtils.isInMultiWindowMode(context)) {
                getDisplay(context).getRealSize(point);
                getDisplay(context).getSize(point2);
                return;
            }
            getDisplay(context).getRealSize(point);
            point2.x = point.x;
            point2.y = point.y;
        } else {
            while ((context instanceof ContextWrapper) && !(context instanceof Activity)) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            Rect bounds3 = windowManager.getMaximumWindowMetrics().getBounds();
            point.x = bounds3.width();
            point.y = bounds3.height();
            if (context instanceof Activity) {
                Rect bounds4 = windowManager.getCurrentWindowMetrics().getBounds();
                point2.x = bounds4.width();
                point2.y = bounds4.height();
                return;
            }
            windowManager.getMaximumWindowMetrics().getBounds();
            point2.x = point.x;
            point2.y = point.y;
        }
    }

    public static void getScreenSize(Context context, Point point) {
        if (Build.VERSION.SDK_INT < 30) {
            getDisplay(context).getRealSize(point);
            return;
        }
        Rect bounds = getWindowManager(context).getMaximumWindowMetrics().getBounds();
        point.x = bounds.width();
        point.y = bounds.height();
    }

    @Deprecated
    public static int getWindowHeight(Context context) {
        return getWindowSize(context).y;
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService("window");
    }

    public static Point getWindowSize(Context context) {
        Point point = new Point();
        getWindowSize(context, point);
        return point;
    }

    public static void getWindowSize(Context context, Point point) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 31) {
            Rect bounds = getWindowManager(context).getCurrentWindowMetrics().getBounds();
            point.x = bounds.width();
            point.y = bounds.height();
        } else if (i != 30) {
            if (MiuixUIUtils.isInMultiWindowMode(context)) {
                getDisplay(context).getSize(point);
            } else {
                getDisplay(context).getRealSize(point);
            }
        } else {
            Context context2 = context;
            while ((context2 instanceof ContextWrapper) && !(context2 instanceof Activity)) {
                context2 = ((ContextWrapper) context2).getBaseContext();
            }
            Rect bounds2 = context2 instanceof Activity ? getWindowManager(context).getCurrentWindowMetrics().getBounds() : getWindowManager(context).getMaximumWindowMetrics().getBounds();
            point.x = bounds2.width();
            point.y = bounds2.height();
        }
    }
}
