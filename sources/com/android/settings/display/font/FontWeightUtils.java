package com.android.settings.display.font;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.SystemProperties;
import android.provider.Settings;
import miui.provider.ExtraCalendarContracts;

@TargetApi(26)
/* loaded from: classes.dex */
public class FontWeightUtils {
    public static final int MIN_WGHT;
    public static Typeface.Builder MIPRO_FONT;
    private static final int[] MITYPE_WGHT;
    public static Typeface.Builder MIUI13_MILANPRO_FONT;
    public static Typeface.Builder MIUI_VF_FONT;
    private static final int[] MIUI_WGHT;
    private static final int[][][] RULES;
    public static final String SYSTEM_FONTS_MIUI_EX_REGULAR_TTF;

    static {
        String str = SystemProperties.get("ro.miui.ui.font.mi_font_path", "/system/fonts/MiLanProVF.ttf");
        SYSTEM_FONTS_MIUI_EX_REGULAR_TTF = str;
        MIPRO_FONT = null;
        MIUI_VF_FONT = null;
        MIUI13_MILANPRO_FONT = null;
        try {
            MIPRO_FONT = new Typeface.Builder(str);
            MIUI_VF_FONT = new Typeface.Builder("/data/system/theme/fonts/MI_Theme_VF.ttf");
            MIUI13_MILANPRO_FONT = new Typeface.Builder("/data/user_de/0/com.android.settings/files/fonts/Roboto-Regular.ttf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MIUI_WGHT = new int[]{150, 200, 250, 305, 340, 400, 480, 540, 630, ExtraCalendarContracts.CALENDAR_ACCESS_LEVEL_LOCAL};
        MITYPE_WGHT = new int[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        MIN_WGHT = 10;
        RULES = r2;
        int[][][] iArr = {new int[][]{new int[]{0, 5}, new int[]{0, 5}, new int[]{1, 6}, new int[]{2, 6}, new int[]{2, 7}, new int[]{3, 8}, new int[]{4, 8}, new int[]{5, 9}, new int[]{6, 9}, new int[]{7, 9}}, new int[][]{new int[]{0, 2}, new int[]{0, 3}, new int[]{1, 4}, new int[]{1, 5}, new int[]{2, 6}, new int[]{2, 7}, new int[]{3, 8}, new int[]{4, 9}, new int[]{5, 9}, new int[]{6, 9}}, new int[][]{new int[]{0, 5}, new int[]{1, 5}, new int[]{2, 5}, new int[]{3, 6}, new int[]{3, 6}, new int[]{4, 7}, new int[]{5, 8}, new int[]{6, 8}, new int[]{7, 8}, new int[]{8, 9}}};
    }

    public static Typeface createTypefaceWithWeight(Typeface.Builder builder, int i) {
        if (builder != null) {
            return builder.setFontVariationSettings("'wght' " + i).build();
        }
        return null;
    }

    static int getScaleWght(int i, float f, int i2, int i3) {
        float f2;
        int[] wghtRange = getWghtRange(i, f);
        int wghtByType = getWghtByType(wghtRange[0], i2);
        int wghtByType2 = getWghtByType(i, i2);
        int wghtByType3 = getWghtByType(wghtRange[1], i2);
        if (i3 < 50) {
            float f3 = i3 / 50.0f;
            f2 = ((1.0f - f3) * wghtByType) + (f3 * wghtByType2);
        } else if (i3 <= 50) {
            return wghtByType2;
        } else {
            float f4 = (i3 - 50) / 50.0f;
            f2 = ((1.0f - f4) * wghtByType2) + (f4 * wghtByType3);
        }
        return (int) f2;
    }

    public static int getScaleWght(Context context, int i, float f, int i2) {
        return getScaleWght(i, f, i2, getSysFontScale(context));
    }

    static int getSysFontScale(Context context) {
        return Settings.System.getInt(context.getApplicationContext().getContentResolver(), "key_miui_font_weight_scale", 50);
    }

    public static Typeface getVarTypeface(int i, int i2) {
        if (i2 == 3) {
            if (MIUI13_MILANPRO_FONT == null) {
                updateVarFont();
            }
            return MIUI13_MILANPRO_FONT.setFontVariationSettings("'wght' " + i).build();
        } else if (i2 != 4) {
            if (MIPRO_FONT == null) {
                MIPRO_FONT = new Typeface.Builder(SYSTEM_FONTS_MIUI_EX_REGULAR_TTF);
            }
            return MIPRO_FONT.setFontVariationSettings("'wght' " + i).build();
        } else {
            if (MIUI_VF_FONT == null) {
                updateVarFont();
            }
            return MIUI_VF_FONT.setFontVariationSettings("'wght' " + i).build();
        }
    }

    private static int[] getWghtArray(int i) {
        return (i == 1 || i == 2) ? MITYPE_WGHT : MIUI_WGHT;
    }

    private static int getWghtByType(int i, int i2) {
        return getWghtArray(i2)[i];
    }

    private static int[] getWghtRange(int i, float f) {
        return RULES[f > 20.0f ? (char) 1 : (f <= 0.0f || f >= 12.0f) ? (char) 0 : (char) 2][i];
    }

    public static void updateVarFont() {
        try {
            MIUI13_MILANPRO_FONT = new Typeface.Builder("/data/user_de/0/com.android.settings/files/fonts/Roboto-Regular.ttf");
            MIUI_VF_FONT = new Typeface.Builder("/data/system/theme/fonts/MI_Theme_VF.ttf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
