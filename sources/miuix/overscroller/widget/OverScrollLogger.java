package miuix.overscroller.widget;

import android.util.Log;
import java.util.Locale;

/* loaded from: classes5.dex */
class OverScrollLogger {
    private static final boolean DEBUG = Log.isLoggable("OverScroll", 3);
    private static final boolean VERBOSE = Log.isLoggable("OverScroll", 2);

    public static void debug(String str) {
        if (DEBUG) {
            Log.d("OverScroll", str);
        }
    }

    public static void debug(String str, Object... objArr) {
        if (DEBUG) {
            Log.d("OverScroll", String.format(Locale.US, str, objArr));
        }
    }

    public static void verbose(String str) {
        if (VERBOSE) {
            Log.v("OverScroll", str);
        }
    }

    public static void verbose(String str, Object... objArr) {
        if (VERBOSE) {
            Log.v("OverScroll", String.format(Locale.US, str, objArr));
        }
    }
}
