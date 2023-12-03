package com.googlecode.leptonica.android;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* loaded from: classes2.dex */
public class JpegIO {
    public static final boolean DEFAULT_PROGRESSIVE = false;
    public static final int DEFAULT_QUALITY = 85;

    static {
        System.loadLibrary("lept");
    }

    public static byte[] compressToJpeg(Pix pix) {
        return compressToJpeg(pix, 85, false);
    }

    public static byte[] compressToJpeg(Pix pix, int i, boolean z) {
        if (pix != null) {
            if (i < 0 || i > 100) {
                throw new IllegalArgumentException("Quality must be between 0 and 100 (inclusive)");
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap writeBitmap = WriteFile.writeBitmap(pix);
            writeBitmap.compress(Bitmap.CompressFormat.JPEG, i, byteArrayOutputStream);
            writeBitmap.recycle();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return byteArray;
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    private static native byte[] nativeCompressToJpeg(long j, int i, boolean z);
}
