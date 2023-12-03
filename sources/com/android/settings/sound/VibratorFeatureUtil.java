package com.android.settings.sound;

import android.content.Context;
import vendor.hardware.vibratorfeature.Vibrator;

/* loaded from: classes2.dex */
public class VibratorFeatureUtil {
    private static volatile VibratorFeatureUtil sInstance;
    private Vibrator mVibratorExt;

    public VibratorFeatureUtil(Context context) {
        this.mVibratorExt = new Vibrator(context);
    }

    public static VibratorFeatureUtil getInstance(Context context) {
        if (sInstance == null) {
            synchronized (VibratorFeatureUtil.class) {
                if (sInstance == null) {
                    sInstance = new VibratorFeatureUtil(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public void setAmplitude(float f) {
        this.mVibratorExt.setAmplitude(Math.min(f, 1.0f));
    }
}
