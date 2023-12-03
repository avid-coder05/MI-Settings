package miuix.appcompat.internal.util;

import android.content.Context;
import android.provider.Settings;
import android.widget.TextView;

/* loaded from: classes5.dex */
public class EasyModeHelper {
    private static boolean isInEasyMode(Context context) {
        return context != null && Settings.System.getInt(context.getContentResolver(), "elderly_mode", 0) == 1;
    }

    public static void updateTextViewSize(TextView textView) {
        if (textView == null || !isInEasyMode(textView.getContext())) {
            return;
        }
        textView.setTextSize(0, 88.0f);
    }
}
