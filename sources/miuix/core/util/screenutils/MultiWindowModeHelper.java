package miuix.core.util.screenutils;

import android.content.Context;

/* loaded from: classes5.dex */
public class MultiWindowModeHelper {

    /* loaded from: classes5.dex */
    public static class WindowInfo {
        public int windowHeight;
        public int windowMode;
        public int windowWidth;
    }

    public static WindowInfo detectWindowInfo(Context context) {
        WindowInfo detectWindowInfo = FreeFormModeHelper.detectWindowInfo(context);
        if (detectWindowInfo.windowMode == 8192) {
            detectWindowInfo = SplitScreenModeHelper.detectWindowInfo(context);
            if (detectWindowInfo.windowMode == 4100) {
                detectWindowInfo.windowMode = 0;
            }
        }
        return detectWindowInfo;
    }

    public static int detectWindowMode(Context context) {
        return detectWindowInfo(context).windowMode;
    }

    public static boolean isInFreeModeWindow(int i) {
        return (i & 8192) != 0;
    }

    public static boolean isInSplitModeWindow(int i) {
        return (i & 4096) != 0;
    }
}
