package com.googlecode.leptonica.android;

/* loaded from: classes2.dex */
public class GrayQuant {
    static {
        System.loadLibrary("lept");
    }

    private static native long nativePixThresholdToBinary(long j, int i);

    public static Pix pixThresholdToBinary(Pix pix, int i) {
        if (pix != null) {
            int depth = pix.getDepth();
            if (depth == 4 || depth == 8) {
                if (depth != 4 || i <= 16) {
                    if (depth != 8 || i <= 256) {
                        long nativePixThresholdToBinary = nativePixThresholdToBinary(pix.getNativePix(), i);
                        if (nativePixThresholdToBinary != 0) {
                            return new Pix(nativePixThresholdToBinary);
                        }
                        throw new RuntimeException("Failed to perform binarization");
                    }
                    throw new IllegalArgumentException("8 bpp thresh not in {0-256}");
                }
                throw new IllegalArgumentException("4 bpp thresh not in {0-16}");
            }
            throw new IllegalArgumentException("Source pix depth must be 4 or 8 bpp");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
