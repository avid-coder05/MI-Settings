package com.googlecode.leptonica.android;

/* loaded from: classes2.dex */
public class Clip {
    static {
        System.loadLibrary("lept");
    }

    public static Pix clipRectangle(Pix pix, Box box) {
        long nativeClipRectangle = nativeClipRectangle(pix.getNativePix(), box.getNativeBox());
        if (nativeClipRectangle != 0) {
            return new Pix(nativeClipRectangle);
        }
        return null;
    }

    private static native long nativeClipRectangle(long j, long j2);
}
