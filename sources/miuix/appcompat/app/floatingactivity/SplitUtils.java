package miuix.appcompat.app.floatingactivity;

import android.content.Intent;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes5.dex */
public class SplitUtils {
    private static int getMiuiFlags(Intent intent) {
        if (intent == null) {
            return 0;
        }
        try {
            return ((Integer) intent.getClass().getMethod("getMiuiFlags", new Class[0]).invoke(intent, new Object[0])).intValue();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.e("SplitUtils", "reflect getMiuiFlags error: " + e);
            return 0;
        }
    }

    public static boolean isIntentFromSettingsSplit(Intent intent) {
        return (getMiuiFlags(intent) & 16) != 0;
    }
}
