package miuix.core.util.screenutils;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/* loaded from: classes5.dex */
public class SplitScreenModeHelper {
    private static WindowManager sWindowManager;
    private static Point sScreenRealSize = new Point();
    private static Point sCurrentSize = new Point();

    public static int detectScreenMode(Context context) {
        return detectWindowInfo(context).windowMode;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x005f  */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0064  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static miuix.core.util.screenutils.MultiWindowModeHelper.WindowInfo detectWindowInfo(android.content.Context r5) {
        /*
            miuix.core.util.screenutils.MultiWindowModeHelper$WindowInfo r0 = new miuix.core.util.screenutils.MultiWindowModeHelper$WindowInfo
            r0.<init>()
            android.view.WindowManager r1 = getWindowManager(r5)
            android.view.Display r1 = r1.getDefaultDisplay()
            android.graphics.Point r2 = miuix.core.util.screenutils.SplitScreenModeHelper.sScreenRealSize
            r1.getRealSize(r2)
            android.view.WindowManager r5 = getWindowManager(r5)
            android.view.Display r5 = r5.getDefaultDisplay()
            android.graphics.Point r1 = miuix.core.util.screenutils.SplitScreenModeHelper.sCurrentSize
            r5.getSize(r1)
            boolean r5 = isLandscape()
            r1 = 0
            if (r5 == 0) goto L33
            android.graphics.Point r5 = miuix.core.util.screenutils.SplitScreenModeHelper.sCurrentSize
            int r5 = r5.x
            float r5 = (float) r5
            android.graphics.Point r2 = miuix.core.util.screenutils.SplitScreenModeHelper.sScreenRealSize
            int r2 = r2.x
        L2f:
            float r2 = (float) r2
            float r2 = r2 + r1
            float r5 = r5 / r2
            goto L4c
        L33:
            android.graphics.Point r5 = miuix.core.util.screenutils.SplitScreenModeHelper.sCurrentSize
            int r2 = r5.x
            float r2 = (float) r2
            android.graphics.Point r3 = miuix.core.util.screenutils.SplitScreenModeHelper.sScreenRealSize
            int r4 = r3.x
            float r4 = (float) r4
            float r4 = r4 + r1
            float r2 = r2 / r4
            r4 = 1065353216(0x3f800000, float:1.0)
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 != 0) goto L4b
            int r5 = r5.y
            float r5 = (float) r5
            int r2 = r3.y
            goto L2f
        L4b:
            r5 = r2
        L4c:
            android.graphics.Point r2 = miuix.core.util.screenutils.SplitScreenModeHelper.sCurrentSize
            int r3 = r2.x
            r0.windowWidth = r3
            int r2 = r2.y
            r0.windowHeight = r2
            r2 = 1053609165(0x3ecccccd, float:0.4)
            boolean r1 = isInRegion(r5, r1, r2)
            if (r1 == 0) goto L64
            r5 = 4097(0x1001, float:5.741E-42)
            r0.windowMode = r5
            goto L84
        L64:
            r1 = 1058642330(0x3f19999a, float:0.6)
            boolean r2 = isInRegion(r5, r2, r1)
            if (r2 == 0) goto L72
            r5 = 4098(0x1002, float:5.743E-42)
            r0.windowMode = r5
            goto L84
        L72:
            r2 = 1061997773(0x3f4ccccd, float:0.8)
            boolean r5 = isInRegion(r5, r1, r2)
            if (r5 == 0) goto L80
            r5 = 4099(0x1003, float:5.744E-42)
            r0.windowMode = r5
            goto L84
        L80:
            r5 = 4100(0x1004, float:5.745E-42)
            r0.windowMode = r5
        L84:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.core.util.screenutils.SplitScreenModeHelper.detectWindowInfo(android.content.Context):miuix.core.util.screenutils.MultiWindowModeHelper$WindowInfo");
    }

    private static WindowManager getWindowManager(Context context) {
        if (sWindowManager == null) {
            sWindowManager = (WindowManager) context.getApplicationContext().getSystemService("window");
        }
        return sWindowManager;
    }

    private static boolean isInRegion(float f, float f2, float f3) {
        return f >= f2 && f < f3;
    }

    private static boolean isLandscape() {
        Point point = sScreenRealSize;
        return point.x > point.y;
    }
}
