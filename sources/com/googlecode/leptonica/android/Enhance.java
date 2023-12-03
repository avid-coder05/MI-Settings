package com.googlecode.leptonica.android;

/* loaded from: classes2.dex */
public class Enhance {
    public static final float DEFAULT_UNSHARP_FRACTION = 0.3f;
    public static final int DEFAULT_UNSHARP_HALFWIDTH = 1;

    static {
        System.loadLibrary("lept");
    }

    private static native long nativeUnsharpMasking(long j, int i, float f);

    public static Pix unsharpMasking(Pix pix) {
        return unsharpMasking(pix, 1, 0.3f);
    }

    public static Pix unsharpMasking(Pix pix, int i, float f) {
        if (pix != null) {
            long nativeUnsharpMasking = nativeUnsharpMasking(pix.getNativePix(), i, f);
            if (nativeUnsharpMasking != 0) {
                return new Pix(nativeUnsharpMasking);
            }
            throw new OutOfMemoryError();
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
