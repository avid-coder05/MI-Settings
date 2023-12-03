package com.googlecode.leptonica.android;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes2.dex */
public class Edge {
    public static final int L_ALL_EDGES = 2;
    public static final int L_HORIZONTAL_EDGES = 0;
    public static final int L_VERTICAL_EDGES = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface EdgeOrientationFlag {
    }

    static {
        System.loadLibrary("lept");
    }

    private static native long nativePixSobelEdgeFilter(long j, int i);

    public static Pix pixSobelEdgeFilter(Pix pix, int i) {
        if (pix != null) {
            if (pix.getDepth() == 8) {
                if (i < 0 || i > 2) {
                    throw new IllegalArgumentException("Invalid orientation flag");
                }
                long nativePixSobelEdgeFilter = nativePixSobelEdgeFilter(pix.getNativePix(), i);
                if (nativePixSobelEdgeFilter != 0) {
                    return new Pix(nativePixSobelEdgeFilter);
                }
                throw new RuntimeException("Failed to perform Sobel edge filter on image");
            }
            throw new IllegalArgumentException("Source pix depth must be 8bpp");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
