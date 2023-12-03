package miuix.preference.utils;

import android.content.Context;
import miuix.preference.R$dimen;

/* loaded from: classes5.dex */
public class PreferenceLayoutUtils {
    public static int getExtraPaddingByLevel(Context context, int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    return 0;
                }
                return context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_two_state_extra_padding_horizontal_huge);
            }
            return context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_two_state_extra_padding_horizontal_large);
        }
        return context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_two_state_extra_padding_horizontal_small);
    }
}
