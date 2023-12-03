package miui.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/* loaded from: classes4.dex */
public class AccessibilityHapticUtils {
    public static boolean isRemoveScreenReaderVibrator(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "is_remove_screen_reader_vibrator", 1) == 1;
    }

    public static boolean isSupportAccessibilityHaptic(Context context) {
        String str = Build.DEVICE;
        return (str.equals("crux") || str.equals("venus")) && !miui.os.Build.IS_INTERNATIONAL_BUILD && isRemoveScreenReaderVibrator(context);
    }
}
