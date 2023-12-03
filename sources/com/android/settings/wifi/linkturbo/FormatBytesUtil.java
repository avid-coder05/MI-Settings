package com.android.settings.wifi.linkturbo;

import android.support.v4.media.session.PlaybackStateCompat;
import com.google.android.material.timepicker.TimeModel;

/* loaded from: classes2.dex */
public class FormatBytesUtil {
    public static String BString = "B";
    public static String GBString = "GB";
    public static String KBString = "KB";
    public static String MBString = "MB";

    public static String formatBytes(long j) {
        double d;
        String str;
        int i = 1;
        if (j >= 1073741824) {
            d = (j * 1.0d) / 1.073741824E9d;
            str = GBString;
            i = 2;
        } else if (j >= PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) {
            d = (j * 1.0d) / 1048576.0d;
            str = MBString;
        } else if (j >= PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            d = (j * 1.0d) / 1024.0d;
            str = KBString;
        } else {
            d = j * 1.0d;
            str = BString;
        }
        return textFormat(d, str, i);
    }

    private static String textFormat(double d, String str, int i) {
        String format;
        if (d > 999.5d || BString.equals(str)) {
            format = String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf((int) d));
        } else if (d > 99.5d) {
            format = String.format("%.01f", Double.valueOf(d));
        } else {
            StringBuilder sb = new StringBuilder(16);
            sb.append("%.0");
            sb.append(i);
            sb.append('f');
            format = String.format(sb.toString(), Double.valueOf(d));
        }
        if (str != null) {
            return format + str;
        }
        return format;
    }
}
