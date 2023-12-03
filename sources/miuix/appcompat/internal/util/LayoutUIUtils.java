package miuix.appcompat.internal.util;

import android.content.Context;
import miuix.appcompat.R$dimen;

/* loaded from: classes5.dex */
public class LayoutUIUtils {
    public static int getExtraPaddingByLevel(Context context, int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    return 0;
                }
                return context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_window_extra_padding_horizontal_huge);
            }
            return context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_window_extra_padding_horizontal_large);
        }
        return context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_window_extra_padding_horizontal_small);
    }

    public static boolean isLevelValid(int i) {
        return i >= 0 && i <= 3;
    }
}
