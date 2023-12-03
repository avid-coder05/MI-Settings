package com.googlecode.leptonica.android;

/* loaded from: classes2.dex */
public class Rotate {
    public static final boolean ROTATE_QUALITY = true;

    static {
        System.loadLibrary("lept");
    }

    private static native long nativeRotate(long j, float f, boolean z, boolean z2);

    private static native long nativeRotateOrth(long j, int i);

    public static Pix rotate(Pix pix, float f) {
        return rotate(pix, f, false);
    }

    public static Pix rotate(Pix pix, float f, boolean z) {
        return rotate(pix, f, z, true);
    }

    public static Pix rotate(Pix pix, float f, boolean z, boolean z2) {
        if (pix != null) {
            long nativeRotate = nativeRotate(pix.getNativePix(), f, z, z2);
            if (nativeRotate == 0) {
                return null;
            }
            return new Pix(nativeRotate);
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    public static Pix rotateOrth(Pix pix, int i) {
        if (pix != null) {
            if (i < 0 || i > 3) {
                throw new IllegalArgumentException("quads not in {0,1,2,3}");
            }
            long nativeRotateOrth = nativeRotateOrth(pix.getNativePix(), i);
            if (nativeRotateOrth == 0) {
                return null;
            }
            return new Pix(nativeRotateOrth);
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
