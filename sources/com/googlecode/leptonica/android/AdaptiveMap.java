package com.googlecode.leptonica.android;

/* loaded from: classes2.dex */
public class AdaptiveMap {
    public static final int DEFAULT_MIN_COUNT = 40;
    public static final int DEFAULT_TILE_HEIGHT = 15;
    public static final int DEFAULT_TILE_WIDTH = 10;
    public static final int DEFAULT_X_SMOOTH_SIZE = 2;
    public static final int DEFAULT_Y_SMOOTH_SIZE = 1;
    private static final int NORM_BG_VALUE = 200;
    private static final int NORM_REDUCTION = 16;
    private static final int NORM_SIZE = 3;

    static {
        System.loadLibrary("lept");
    }

    public static Pix backgroundNormMorph(Pix pix) {
        return backgroundNormMorph(pix, 16, 3, NORM_BG_VALUE);
    }

    public static Pix backgroundNormMorph(Pix pix, int i, int i2, int i3) {
        if (pix != null) {
            long nativeBackgroundNormMorph = nativeBackgroundNormMorph(pix.getNativePix(), i, i2, i3);
            if (nativeBackgroundNormMorph != 0) {
                return new Pix(nativeBackgroundNormMorph);
            }
            throw new RuntimeException("Failed to normalize image background");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    private static native long nativeBackgroundNormMorph(long j, int i, int i2, int i3);

    private static native long nativePixContrastNorm(long j, int i, int i2, int i3, int i4, int i5);

    public static Pix pixContrastNorm(Pix pix) {
        return pixContrastNorm(pix, 10, 15, 40, 2, 1);
    }

    public static Pix pixContrastNorm(Pix pix, int i, int i2, int i3, int i4, int i5) {
        if (pix != null) {
            long nativePixContrastNorm = nativePixContrastNorm(pix.getNativePix(), i, i2, i3, i4, i5);
            if (nativePixContrastNorm != 0) {
                return new Pix(nativePixContrastNorm);
            }
            throw new RuntimeException("Failed to normalize image contrast");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
