package com.googlecode.leptonica.android;

import android.graphics.Rect;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class Pixa implements Iterable<Pix> {
    private static final String TAG;
    final int mHeight;
    private final long mNativePixa;
    private boolean mRecycled = false;
    final int mWidth;

    /* loaded from: classes2.dex */
    private class PixIterator implements Iterator<Pix> {
        private int mIndex;

        private PixIterator() {
            this.mIndex = 0;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            int size = Pixa.this.size();
            return size > 0 && this.mIndex < size;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Pix next() {
            Pixa pixa = Pixa.this;
            int i = this.mIndex;
            this.mIndex = i + 1;
            return pixa.getPix(i);
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static {
        System.loadLibrary("lept");
        TAG = Pixa.class.getSimpleName();
    }

    public Pixa(long j, int i, int i2) {
        this.mNativePixa = j;
        this.mWidth = i;
        this.mHeight = i2;
    }

    public static Pixa createPixa(int i) {
        return createPixa(i, 0, 0);
    }

    public static Pixa createPixa(int i, int i2, int i3) {
        long nativeCreate = nativeCreate(i);
        if (nativeCreate != 0) {
            return new Pixa(nativeCreate, i2, i3);
        }
        throw new OutOfMemoryError();
    }

    private static native void nativeAdd(long j, long j2, long j3, int i);

    private static native void nativeAddBox(long j, long j2, int i);

    private static native void nativeAddPix(long j, long j2, int i);

    private static native long nativeCopy(long j);

    private static native long nativeCreate(int i);

    private static native void nativeDestroy(long j);

    private static native long nativeGetBox(long j, int i);

    private static native boolean nativeGetBoxGeometry(long j, int i, int[] iArr);

    private static native int nativeGetCount(long j);

    private static native long nativeGetPix(long j, int i);

    private static native boolean nativeJoin(long j, long j2);

    private static native void nativeMergeAndReplacePix(long j, int i, int i2);

    private static native void nativeReplacePix(long j, int i, long j2, long j3);

    private static native long nativeSort(long j, int i, int i2);

    private static native boolean nativeWriteToFileRandomCmap(long j, String str, int i, int i2);

    public void add(Pix pix, Box box, int i) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        nativeAdd(this.mNativePixa, pix.getNativePix(), box.getNativeBox(), i);
    }

    public void addBox(Box box, int i) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        nativeAddBox(this.mNativePixa, box.getNativeBox(), i);
    }

    public void addPix(Pix pix, int i) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        nativeAddPix(this.mNativePixa, pix.getNativePix(), i);
    }

    public Pixa copy() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        long nativeCopy = nativeCopy(this.mNativePixa);
        if (nativeCopy != 0) {
            return new Pixa(nativeCopy, this.mWidth, this.mHeight);
        }
        throw new OutOfMemoryError();
    }

    protected void finalize() throws Throwable {
        try {
            if (!this.mRecycled) {
                Log.w(TAG, "Pixa was not terminated using recycle()");
                recycle();
            }
        } finally {
            super.finalize();
        }
    }

    public Box getBox(int i) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        long nativeGetBox = nativeGetBox(this.mNativePixa, i);
        if (nativeGetBox == 0) {
            return null;
        }
        return new Box(nativeGetBox);
    }

    public boolean getBoxGeometry(int i, int[] iArr) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return nativeGetBoxGeometry(this.mNativePixa, i, iArr);
    }

    public int[] getBoxGeometry(int i) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        int[] iArr = new int[4];
        if (getBoxGeometry(i, iArr)) {
            return iArr;
        }
        return null;
    }

    public Rect getBoxRect(int i) {
        int[] boxGeometry = getBoxGeometry(i);
        if (boxGeometry == null) {
            return null;
        }
        int i2 = boxGeometry[0];
        int i3 = boxGeometry[1];
        return new Rect(i2, i3, boxGeometry[2] + i2, boxGeometry[3] + i3);
    }

    public ArrayList<Rect> getBoxRects() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        int nativeGetCount = nativeGetCount(this.mNativePixa);
        int[] iArr = new int[4];
        ArrayList<Rect> arrayList = new ArrayList<>(nativeGetCount);
        for (int i = 0; i < nativeGetCount; i++) {
            getBoxGeometry(i, iArr);
            int i2 = iArr[0];
            int i3 = iArr[1];
            arrayList.add(new Rect(i2, i3, iArr[2] + i2, iArr[3] + i3));
        }
        return arrayList;
    }

    public int getHeight() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return this.mHeight;
    }

    public long getNativePixa() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return this.mNativePixa;
    }

    public Pix getPix(int i) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        long nativeGetPix = nativeGetPix(this.mNativePixa, i);
        if (nativeGetPix == 0) {
            return null;
        }
        return new Pix(nativeGetPix);
    }

    public Rect getRect() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return new Rect(0, 0, this.mWidth, this.mHeight);
    }

    public int getWidth() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return this.mWidth;
    }

    @Override // java.lang.Iterable
    public Iterator<Pix> iterator() {
        return new PixIterator();
    }

    public boolean join(Pixa pixa) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return nativeJoin(this.mNativePixa, pixa.mNativePixa);
    }

    public void mergeAndReplacePix(int i, int i2) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        nativeMergeAndReplacePix(this.mNativePixa, i, i2);
    }

    public synchronized void recycle() {
        if (!this.mRecycled) {
            nativeDestroy(this.mNativePixa);
            this.mRecycled = true;
        }
    }

    public void replacePix(int i, Pix pix, Box box) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        nativeReplacePix(this.mNativePixa, i, pix.getNativePix(), box.getNativeBox());
    }

    public int size() {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return nativeGetCount(this.mNativePixa);
    }

    public Pixa sort(int i, int i2) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        long nativeSort = nativeSort(this.mNativePixa, i, i2);
        if (nativeSort != 0) {
            return new Pixa(nativeSort, this.mWidth, this.mHeight);
        }
        throw new OutOfMemoryError();
    }

    public boolean writeToFileRandomCmap(File file) {
        if (this.mRecycled) {
            throw new IllegalStateException();
        }
        return nativeWriteToFileRandomCmap(this.mNativePixa, file.getAbsolutePath(), this.mWidth, this.mHeight);
    }
}
