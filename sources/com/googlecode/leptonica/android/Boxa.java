package com.googlecode.leptonica.android;

import android.graphics.Rect;
import android.util.Log;

/* loaded from: classes2.dex */
public class Boxa {
    private static final String TAG;
    private final long mNativeBoxa;
    private boolean mRecycled;

    static {
        System.loadLibrary("lept");
        TAG = Boxa.class.getSimpleName();
    }

    public Boxa(long j) {
        this.mRecycled = false;
        this.mNativeBoxa = j;
        this.mRecycled = false;
    }

    private static native void nativeDestroy(long j);

    private static native int nativeGetCount(long j);

    private static native boolean nativeGetGeometry(long j, int i, int[] iArr);

    protected void finalize() throws Throwable {
        try {
            if (!this.mRecycled) {
                Log.w(TAG, "Boxa was not terminated using recycle()");
                recycle();
            }
        } finally {
            super.finalize();
        }
    }

    public int getCount() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return nativeGetCount(this.mNativeBoxa);
    }

    public boolean getGeometry(int i, int[] iArr) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        if (iArr.length >= 4) {
            return nativeGetGeometry(this.mNativeBoxa, i, iArr);
        }
        throw new IllegalArgumentException("Geometry array must be at least 4 elements long");
    }

    public int[] getGeometry(int i) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        int[] iArr = new int[4];
        if (getGeometry(i, iArr)) {
            return iArr;
        }
        return null;
    }

    public long getNativeBoxa() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return this.mNativeBoxa;
    }

    public Rect getRect(int i) {
        int[] geometry = getGeometry(i);
        int i2 = geometry[0];
        int i3 = geometry[1];
        return new Rect(i2, i3, geometry[2] + i2, geometry[3] + i3);
    }

    public synchronized void recycle() {
        if (!this.mRecycled) {
            nativeDestroy(this.mNativeBoxa);
            this.mRecycled = true;
        }
    }
}
