package com.googlecode.leptonica.android;

import android.graphics.Rect;
import android.util.Log;

/* loaded from: classes2.dex */
public class Box {
    public static final int INDEX_H = 3;
    public static final int INDEX_W = 2;
    public static final int INDEX_X = 0;
    public static final int INDEX_Y = 1;
    private static final String TAG;
    private final long mNativeBox;
    private boolean mRecycled;

    static {
        System.loadLibrary("lept");
        TAG = Box.class.getSimpleName();
    }

    public Box(int i, int i2, int i3, int i4) {
        this.mRecycled = false;
        if (i < 0 || i2 < 0 || i3 < 0 || i4 < 0) {
            throw new IllegalArgumentException("All box dimensions must be non-negative");
        }
        long nativeCreate = nativeCreate(i, i2, i3, i4);
        if (nativeCreate == 0) {
            throw new OutOfMemoryError();
        }
        this.mNativeBox = nativeCreate;
        this.mRecycled = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Box(long j) {
        this.mRecycled = false;
        this.mNativeBox = j;
        this.mRecycled = false;
    }

    private static native long nativeCreate(int i, int i2, int i3, int i4);

    private static native void nativeDestroy(long j);

    private static native boolean nativeGetGeometry(long j, int[] iArr);

    private static native int nativeGetHeight(long j);

    private static native int nativeGetWidth(long j);

    private static native int nativeGetX(long j);

    private static native int nativeGetY(long j);

    protected void finalize() throws Throwable {
        try {
            if (!this.mRecycled) {
                Log.w(TAG, "Box was not terminated using recycle()");
                recycle();
            }
        } finally {
            super.finalize();
        }
    }

    public boolean getGeometry(int[] iArr) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        if (iArr.length >= 4) {
            return nativeGetGeometry(this.mNativeBox, iArr);
        }
        throw new IllegalArgumentException("Geometry array must be at least 4 elements long");
    }

    public int[] getGeometry() {
        int[] iArr = new int[4];
        if (getGeometry(iArr)) {
            return iArr;
        }
        return null;
    }

    public int getHeight() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return nativeGetHeight(this.mNativeBox);
    }

    public long getNativeBox() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return this.mNativeBox;
    }

    public Rect getRect() {
        int[] geometry = getGeometry();
        int i = geometry[0];
        int i2 = geometry[1];
        return new Rect(i, i2, geometry[2] + i, geometry[3] + i2);
    }

    public int getWidth() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return nativeGetWidth(this.mNativeBox);
    }

    public int getX() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return nativeGetX(this.mNativeBox);
    }

    public int getY() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return nativeGetY(this.mNativeBox);
    }

    public void recycle() {
        if (this.mRecycled) {
            return;
        }
        nativeDestroy(this.mNativeBox);
        this.mRecycled = true;
    }
}
