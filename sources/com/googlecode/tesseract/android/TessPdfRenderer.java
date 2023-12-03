package com.googlecode.tesseract.android;

/* loaded from: classes2.dex */
public class TessPdfRenderer {
    private final long mNativePdfRenderer;
    private boolean mRecycled = false;

    static {
        System.loadLibrary("lept");
        System.loadLibrary("tess");
    }

    public TessPdfRenderer(TessBaseAPI tessBaseAPI, String str) {
        this.mNativePdfRenderer = nativeCreate(tessBaseAPI.getNativeData(), str);
    }

    private static native long nativeCreate(long j, String str);

    private static native void nativeRecycle(long j);

    public long getNativePdfRenderer() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return this.mNativePdfRenderer;
    }

    public void recycle() {
        nativeRecycle(this.mNativePdfRenderer);
        this.mRecycled = true;
    }
}
