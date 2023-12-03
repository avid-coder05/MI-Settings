package com.googlecode.leptonica.android;

/* loaded from: classes2.dex */
public class Binarize {
    public static final float OTSU_SCORE_FRACTION = 0.1f;
    public static final int OTSU_SIZE_X = 32;
    public static final int OTSU_SIZE_Y = 32;
    public static final int OTSU_SMOOTH_X = 2;
    public static final int OTSU_SMOOTH_Y = 2;
    public static final int SAUVOLA_DEFAULT_NUM_TILES_X = 1;
    public static final int SAUVOLA_DEFAULT_NUM_TILES_Y = 1;
    public static final float SAUVOLA_DEFAULT_REDUCTION_FACTOR = 0.35f;
    public static final int SAUVOLA_DEFAULT_WINDOW_HALFWIDTH = 8;

    static {
        System.loadLibrary("lept");
    }

    private static native long nativeOtsuAdaptiveThreshold(long j, int i, int i2, int i3, int i4, float f);

    private static native long nativeSauvolaBinarizeTiled(long j, int i, float f, int i2, int i3);

    public static Pix otsuAdaptiveThreshold(Pix pix) {
        return otsuAdaptiveThreshold(pix, 32, 32, 2, 2, 0.1f);
    }

    public static Pix otsuAdaptiveThreshold(Pix pix, int i, int i2, int i3, int i4, float f) {
        if (pix != null) {
            if (pix.getDepth() == 8) {
                long nativeOtsuAdaptiveThreshold = nativeOtsuAdaptiveThreshold(pix.getNativePix(), i, i2, i3, i4, f);
                if (nativeOtsuAdaptiveThreshold != 0) {
                    return new Pix(nativeOtsuAdaptiveThreshold);
                }
                throw new RuntimeException("Failed to perform Otsu adaptive threshold on image");
            }
            throw new IllegalArgumentException("Source pix depth must be 8bpp");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    public static Pix sauvolaBinarizeTiled(Pix pix) {
        return sauvolaBinarizeTiled(pix, 8, 0.35f, 1, 1);
    }

    public static Pix sauvolaBinarizeTiled(Pix pix, int i, float f, int i2, int i3) {
        if (pix != null) {
            if (pix.getDepth() == 8) {
                long nativeSauvolaBinarizeTiled = nativeSauvolaBinarizeTiled(pix.getNativePix(), i, f, i2, i3);
                if (nativeSauvolaBinarizeTiled != 0) {
                    return new Pix(nativeSauvolaBinarizeTiled);
                }
                throw new RuntimeException("Failed to perform Sauvola binarization on image");
            }
            throw new IllegalArgumentException("Source pix depth must be 8bpp");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
