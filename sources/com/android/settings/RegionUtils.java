package com.android.settings;

import android.os.SystemProperties;
import com.android.settings.bluetooth.MiuiBTUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class RegionUtils {
    public static final boolean IS_FR_ORANGE;
    public static final boolean IS_FR_SFR;
    public static final boolean IS_INDIA;
    public static final boolean IS_IN_FK;
    public static final boolean IS_JP;
    public static final boolean IS_JP_HARDWARE;
    public static final boolean IS_TH_AS;
    private static final Set<String> EU = new HashSet(Arrays.asList("AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GB", "GR", "HR", "HU", "IE", "IT", "LT", "LU", "LV", "MT", "NL", "PL", "PT", "RO", "SE", "SI", "SK"));
    public static final boolean IS_MEXICO_TELCEL = "mx_telcel".equals(SystemProperties.get("ro.miui.customized.region"));
    public static final boolean IS_KOREA_KT = "kr_kt".equals(SystemProperties.get("ro.miui.customized.region"));
    public static final boolean IS_KOREA = "KR".equals(SystemProperties.get("ro.miui.region", "unknown"));
    public static final boolean IS_LM_CLARO = "lm_cr".equals(SystemProperties.get("ro.miui.customized.region"));
    public static final boolean IS_MX_AT = "mx_at".equals(SystemProperties.get("ro.miui.customized.region"));
    public static final boolean IS_JP_KDDI = "jp_kd".equals(SystemProperties.get("ro.miui.customized.region"));
    public static final boolean IS_JP_SB = "jp_sb".equals(SystemProperties.get("ro.miui.customized.region"));

    static {
        IS_JP_HARDWARE = "Japan".equals(SystemProperties.get("ro.boot.hwc")) || "JP".equals(SystemProperties.get("ro.boot.hwc"));
        IS_FR_ORANGE = "fr_orange".equals(SystemProperties.get("ro.miui.customized.region"));
        IS_FR_SFR = "fr_sfr".equals(SystemProperties.get("ro.miui.customized.region"));
        IS_TH_AS = "th_as".equals(SystemProperties.get("ro.miui.customized.region"));
        IS_INDIA = "in".equalsIgnoreCase(SystemProperties.get("ro.miui.build.region"));
        IS_JP = "jp".equalsIgnoreCase(SystemProperties.get("ro.miui.build.region"));
        IS_IN_FK = "in_fk".equals(SystemProperties.get("ro.miui.customized.region"));
    }

    public static String getRegion() {
        return MiuiBTUtils.getRegion();
    }

    public static boolean isGMSDefault() {
        return "tier1".equals(SystemProperties.get("ro.com.miui.rsa", "")) || "android-xiaomi-trev1".equals(SystemProperties.get("ro.com.google.clientidbase.ms", "")) || "android-xiaomi-trev2".equals(SystemProperties.get("ro.com.google.clientidbase.ms", ""));
    }

    public static boolean isGoogleClientRegion() {
        return "android-xiaomi-rev1".equals(SystemProperties.get("ro.com.google.clientidbase", "")) || "android-xiaomi-rev1".equals(SystemProperties.get("ro.com.google.clientidbase.ms", "")) || "tier2".equals(SystemProperties.get("ro.com.miui.rsa", ""));
    }
}
