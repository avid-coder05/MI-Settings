package miuix.text.utilities;

import android.content.Context;

/* loaded from: classes5.dex */
public class ExtraTextUtils {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String formatFileSize(Context context, long j) {
        return formatFileSize(context, j, false);
    }

    private static String formatFileSize(Context context, long j, boolean z) {
        if (context == null) {
            return "";
        }
        float f = (float) j;
        int i = R$string.size_byte;
        if (f > 900.0f) {
            i = R$string.size_kilo_byte;
            f /= 1000.0f;
        }
        if (f > 900.0f) {
            i = R$string.size_mega_byte;
            f /= 1000.0f;
        }
        if (f > 900.0f) {
            i = R$string.size_giga_byte;
            f /= 1000.0f;
        }
        if (f > 900.0f) {
            i = R$string.size_tera_byte;
            f /= 1000.0f;
        }
        if (f > 900.0f) {
            i = R$string.size_peta_byte;
            f /= 1000.0f;
        }
        String format = f < 1.0f ? String.format("%.2f", Float.valueOf(f)) : f < 10.0f ? z ? String.format("%.1f", Float.valueOf(f)) : String.format("%.2f", Float.valueOf(f)) : f < 100.0f ? z ? String.format("%.0f", Float.valueOf(f)) : String.format("%.2f", Float.valueOf(f)) : String.format("%.0f", Float.valueOf(f));
        int length = format.length();
        if (length > 3) {
            int i2 = length - 3;
            if (format.charAt(i2) == '.' && format.charAt(length - 2) == '0' && format.charAt(length - 1) == '0') {
                format = format.substring(0, i2);
                return context.getResources().getString(R$string.size_suffix, format, context.getString(i));
            }
        }
        if (length > 2) {
            int i3 = length - 2;
            if (format.charAt(i3) == '.' && format.charAt(length - 1) == '0') {
                format = format.substring(0, i3);
            }
        }
        return context.getResources().getString(R$string.size_suffix, format, context.getString(i));
    }
}
