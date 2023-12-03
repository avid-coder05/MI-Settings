package com.android.settings.haptic.utils;

import android.graphics.Color;

/* loaded from: classes.dex */
public class ViewUtils {
    public static int getTransitionColor(float f, int i, int i2) {
        int red = Color.red(i);
        int blue = Color.blue(i);
        int green = Color.green(i);
        int alpha = Color.alpha(i);
        int red2 = Color.red(i2);
        int blue2 = Color.blue(i2);
        return Color.argb((int) (alpha + (f * (Color.alpha(i2) - alpha))), (int) (red + ((red2 - red) * f)), (int) (green + ((Color.green(i2) - green) * f)), (int) (blue + ((blue2 - blue) * f)));
    }
}
