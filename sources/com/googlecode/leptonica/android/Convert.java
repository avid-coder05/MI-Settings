package com.googlecode.leptonica.android;

/* loaded from: classes2.dex */
public class Convert {
    static {
        System.loadLibrary("lept");
    }

    public static Pix convertTo8(Pix pix) {
        if (pix != null) {
            long nativeConvertTo8 = nativeConvertTo8(pix.getNativePix());
            if (nativeConvertTo8 != 0) {
                return new Pix(nativeConvertTo8);
            }
            throw new RuntimeException("Failed to natively convert pix");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    private static native long nativeConvertTo8(long j);
}
