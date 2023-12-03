package com.googlecode.tesseract.android;

import android.graphics.Rect;

/* loaded from: classes2.dex */
public class PageIterator {
    private final long mNativePageIterator;

    static {
        System.loadLibrary("lept");
        System.loadLibrary("tess");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PageIterator(long j) {
        this.mNativePageIterator = j;
    }

    private static native void nativeBegin(long j);

    private static native int[] nativeBoundingBox(long j, int i);

    private static native boolean nativeNext(long j, int i);

    public void begin() {
        nativeBegin(this.mNativePageIterator);
    }

    public int[] getBoundingBox(int i) {
        return nativeBoundingBox(this.mNativePageIterator, i);
    }

    public Rect getBoundingRect(int i) {
        int[] boundingBox = getBoundingBox(i);
        return new Rect(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3]);
    }

    public boolean next(int i) {
        return nativeNext(this.mNativePageIterator, i);
    }
}
