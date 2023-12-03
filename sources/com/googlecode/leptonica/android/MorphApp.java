package com.googlecode.leptonica.android;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes2.dex */
public class MorphApp {
    public static final int DEFAULT_HEIGHT = 7;
    public static final int DEFAULT_WIDTH = 7;
    public static final int L_TOPHAT_BLACK = 1;
    public static final int L_TOPHAT_WHITE = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface TophatType {
    }

    static {
        System.loadLibrary("lept");
    }

    private static native long nativePixFastTophat(long j, int i, int i2, int i3);

    private static native long nativePixTophat(long j, int i, int i2, int i3);

    public static Pix pixFastTophat(Pix pix, int i, int i2, int i3) {
        if (pix != null) {
            if (pix.getDepth() == 8) {
                if (i < 1 || i2 < 1) {
                    throw new IllegalArgumentException("size < 1");
                }
                if (i3 < 0 || i3 > 1) {
                    throw new IllegalArgumentException("Type must be L_TOPHAT_BLACK or L_TOPHAT_WHITE");
                }
                long nativePixFastTophat = nativePixFastTophat(pix.getNativePix(), i, i2, i3);
                if (nativePixFastTophat != 0) {
                    return new Pix(nativePixFastTophat);
                }
                throw new RuntimeException("Failed to perform pixFastTophat on image");
            }
            throw new IllegalArgumentException("Source pix depth must be 8bpp");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    public static Pix pixFastTophatBlack(Pix pix) {
        return pixFastTophat(pix, 7, 7, 1);
    }

    public static Pix pixFastTophatWhite(Pix pix) {
        return pixFastTophat(pix, 7, 7, 0);
    }

    public static Pix pixTophat(Pix pix, int i, int i2, int i3) {
        if (pix != null) {
            if (pix.getDepth() == 8) {
                if (i < 1 || i2 < 1) {
                    throw new IllegalArgumentException("hsize or vsize < 1");
                }
                if (i3 < 0 || i3 > 1) {
                    throw new IllegalArgumentException("Type must be L_TOPHAT_BLACK or L_TOPHAT_WHITE");
                }
                long nativePixTophat = nativePixTophat(pix.getNativePix(), i, i2, i3);
                if (nativePixTophat != 0) {
                    return new Pix(nativePixTophat);
                }
                throw new RuntimeException("Failed to perform Tophat on image");
            }
            throw new IllegalArgumentException("Source pix depth must be 8bpp");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
