package com.android.settingslib.display;

import android.app.ActivityThread;
import android.content.res.Resources;
import android.os.PowerManager;
import android.util.MathUtils;

/* loaded from: classes2.dex */
public class BrightnessUtils {
    private static final float A;
    private static final float B;
    private static final float C;
    public static final int GAMMA_SPACE_MAX = PowerManager.BRIGHTNESS_ON;
    private static final float R;
    private static final Resources resources;

    static {
        Resources resources2 = ActivityThread.currentApplication().getResources();
        resources = resources2;
        R = resources2.getFloat(285671456);
        A = resources2.getFloat(285671453);
        B = resources2.getFloat(285671454);
        C = resources2.getFloat(285671455);
    }

    public static final float convertGammaToLinearFloat(int i, float f, float f2) {
        float norm = MathUtils.norm(0.0f, GAMMA_SPACE_MAX, i);
        float f3 = R;
        return MathUtils.lerp(f, f2, MathUtils.constrain(norm <= f3 ? MathUtils.sq(norm / f3) : MathUtils.exp((norm - C) / A) + B, 0.0f, 12.0f) / 12.0f);
    }

    public static final int convertLinearToGammaFloat(float f, float f2, float f3) {
        float log;
        float norm = MathUtils.norm(f2, f3, f) * 12.0f;
        if (norm <= 1.0f) {
            log = MathUtils.sqrt(norm) * R;
        } else {
            log = C + (A * MathUtils.log(norm - B));
        }
        return Math.round(MathUtils.lerp(0.0f, GAMMA_SPACE_MAX, log));
    }
}
