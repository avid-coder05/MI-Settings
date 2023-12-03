package com.android.settings.utils;

import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DisplayCutout;
import java.math.BigDecimal;

/* loaded from: classes2.dex */
public class MiuiGxzwUtils {
    private static int DENSITY_DPI;
    public static int GXZW_ANIM_HEIGHT;
    private static final float GXZW_ANIM_HEIGHT_PRCENT;
    public static int GXZW_ANIM_WIDTH;
    private static final float GXZW_ANIM_WIDTH_PRCENT;
    public static float GXZW_HEIGHT_PRCENT;
    public static int GXZW_ICON_HEIGHT;
    public static int GXZW_ICON_WIDTH;
    public static int GXZW_ICON_X;
    public static int GXZW_ICON_Y;
    private static final boolean GXZW_LOWLIGHT_SENSOR;
    private static final String GXZW_POSITION;
    private static final boolean GXZW_SENSOR = SystemProperties.getBoolean("ro.hardware.fp.fod", false);
    public static float GXZW_WIDTH_PRCENT;
    private static float GXZW_X_PRCENT;
    private static float GXZW_Y_PRCENT;
    private static final String MIUI_DEFAULT_RESOLUTION;
    private static int SCREEN_HEIGHT_DP;
    public static int SCREEN_HEIGHT_PHYSICAL;
    public static int SCREEN_HEIGHT_PX;
    private static int SCREEN_WIDTH_DP;
    public static int SCREEN_WIDTH_PHYSICAL;
    public static int SCREEN_WIDTH_PX;

    static {
        GXZW_LOWLIGHT_SENSOR = SystemProperties.getInt("persist.vendor.sys.fp.expolevel", 0) == 136;
        GXZW_ICON_X = 453;
        GXZW_ICON_Y = 1640;
        GXZW_ICON_WIDTH = 173;
        GXZW_ICON_HEIGHT = 173;
        GXZW_ANIM_WIDTH = 1028;
        GXZW_ANIM_HEIGHT = 1028;
        GXZW_POSITION = SystemProperties.get("ro.hardware.fp.fod.location");
        MIUI_DEFAULT_RESOLUTION = SystemProperties.get("persist.sys.miui_default_resolution");
        GXZW_ANIM_WIDTH_PRCENT = getPrcent(isGxzwLowPosition() ? 960 : GXZW_ANIM_WIDTH, 1080);
        GXZW_ANIM_HEIGHT_PRCENT = getPrcent(isGxzwLowPosition() ? 540 : GXZW_ANIM_HEIGHT, 2400);
        SCREEN_WIDTH_PHYSICAL = -1;
        SCREEN_HEIGHT_PHYSICAL = -1;
        DENSITY_DPI = -1;
        SCREEN_WIDTH_DP = -1;
        SCREEN_HEIGHT_DP = -1;
        GXZW_X_PRCENT = -1.0f;
        GXZW_Y_PRCENT = -1.0f;
        GXZW_WIDTH_PRCENT = -1.0f;
        GXZW_HEIGHT_PRCENT = -1.0f;
        SCREEN_WIDTH_PX = -1;
        SCREEN_HEIGHT_PX = -1;
    }

    public static int caculateCutoutHeightIfNeed(Context context) {
        if (MiuiSettings.Global.getBoolean(context.getContentResolver(), "force_black_v2")) {
            Display display = ((DisplayManager) context.getSystemService("display")).getDisplay(0);
            Point point = new Point();
            display.getRealSize(point);
            DisplayCutout fromResourcesRectApproximation = DisplayCutout.fromResourcesRectApproximation(context.getResources(), point.x, point.y);
            int safeInsetTop = fromResourcesRectApproximation != null ? fromResourcesRectApproximation.getSafeInsetTop() : 0;
            return safeInsetTop % 2 != 0 ? safeInsetTop + 1 : safeInsetTop;
        }
        return 0;
    }

    public static void caculateGxzwIconSize(Context context) {
        int i = context.getResources().getConfiguration().densityDpi;
        int i2 = context.getResources().getConfiguration().screenWidthDp;
        int i3 = context.getResources().getConfiguration().screenHeightDp;
        if (i == DENSITY_DPI && i2 == SCREEN_WIDTH_DP && i3 == SCREEN_HEIGHT_DP) {
            return;
        }
        DENSITY_DPI = i;
        SCREEN_WIDTH_DP = i2;
        SCREEN_HEIGHT_DP = i3;
        if (SCREEN_WIDTH_PHYSICAL == -1) {
            phySicalScreenPx(context);
        }
        screenWhPx(context);
        String str = SystemProperties.get("persist.vendor.sys.fp.fod.location.X_Y", "");
        String str2 = SystemProperties.get("persist.vendor.sys.fp.fod.size.width_height", "");
        if (str.isEmpty() || str2.isEmpty()) {
            resetDefaultValue();
            return;
        }
        try {
            GXZW_ICON_X = Integer.parseInt(str.split(",")[0]);
            GXZW_ICON_Y = Integer.parseInt(str.split(",")[1]);
            GXZW_ICON_WIDTH = Integer.parseInt(str2.split(",")[0]);
            GXZW_ICON_HEIGHT = Integer.parseInt(str2.split(",")[1]);
            GXZW_X_PRCENT = getPrcent(GXZW_ICON_X, SCREEN_WIDTH_PHYSICAL);
            GXZW_Y_PRCENT = getPrcent(GXZW_ICON_Y, SCREEN_HEIGHT_PHYSICAL);
            GXZW_WIDTH_PRCENT = getPrcent(GXZW_ICON_WIDTH, SCREEN_WIDTH_PHYSICAL);
            float prcent = getPrcent(GXZW_ICON_HEIGHT, SCREEN_HEIGHT_PHYSICAL);
            GXZW_HEIGHT_PRCENT = prcent;
            int i4 = SCREEN_WIDTH_PX;
            GXZW_ICON_X = (int) (i4 * GXZW_X_PRCENT);
            int i5 = SCREEN_HEIGHT_PX;
            GXZW_ICON_Y = (int) (i5 * GXZW_Y_PRCENT);
            GXZW_ICON_WIDTH = (int) (i4 * GXZW_WIDTH_PRCENT);
            GXZW_ICON_HEIGHT = (int) (i5 * prcent);
            GXZW_ANIM_WIDTH = (int) (i4 * GXZW_ANIM_WIDTH_PRCENT);
            GXZW_ANIM_HEIGHT = (int) (i5 * GXZW_ANIM_HEIGHT_PRCENT);
            int caculateCutoutHeightIfNeed = caculateCutoutHeightIfNeed(context);
            float f = GXZW_ICON_Y;
            int i6 = SCREEN_HEIGHT_PHYSICAL;
            int prcent2 = (int) (f * getPrcent(i6, i6 - caculateCutoutHeightIfNeed));
            GXZW_ICON_Y = prcent2;
            GXZW_ICON_Y = prcent2 - caculateCutoutHeightIfNeed;
        } catch (Exception e) {
            e.printStackTrace();
            resetDefaultValue();
        }
    }

    public static float getPrcent(int i, int i2) {
        if (i2 == 0 || i == 0) {
            return 1.0f;
        }
        return new BigDecimal(i).divide(new BigDecimal(i2), 10, 5).floatValue();
    }

    public static boolean isGxzwLowPosition() {
        return "low".equals(GXZW_POSITION);
    }

    public static boolean isGxzwSensor() {
        return GXZW_SENSOR;
    }

    public static boolean isLargeFod() {
        return false;
    }

    public static boolean isSupportLowlight() {
        return GXZW_LOWLIGHT_SENSOR;
    }

    public static boolean isSupportQuickOpen() {
        return GXZW_SENSOR;
    }

    public static void phySicalScreenPx(Context context) {
        String str = MIUI_DEFAULT_RESOLUTION;
        if (!str.isEmpty()) {
            SCREEN_WIDTH_PHYSICAL = Integer.parseInt(str.split("x")[0]);
            SCREEN_HEIGHT_PHYSICAL = Integer.parseInt(str.split("x")[1]);
            return;
        }
        Display display = ((DisplayManager) context.getSystemService("display")).getDisplay(0);
        SCREEN_WIDTH_PHYSICAL = display.getMode().getPhysicalWidth();
        SCREEN_HEIGHT_PHYSICAL = display.getMode().getPhysicalHeight();
    }

    private static void resetDefaultValue() {
        GXZW_ICON_X = 453;
        GXZW_ICON_Y = 1640;
        GXZW_ICON_WIDTH = 173;
        GXZW_ICON_HEIGHT = 173;
    }

    private static void screenWhPx(Context context) {
        DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayManager.getDisplay(0).getRealMetrics(displayMetrics);
        boolean z = context.getResources().getConfiguration().orientation == 1;
        SCREEN_WIDTH_PX = z ? displayMetrics.widthPixels : displayMetrics.heightPixels;
        SCREEN_HEIGHT_PX = z ? displayMetrics.heightPixels : displayMetrics.widthPixels;
    }
}
