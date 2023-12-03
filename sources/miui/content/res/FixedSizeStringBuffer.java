package miui.content.res;

import android.util.Log;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/* loaded from: classes3.dex */
public class FixedSizeStringBuffer {
    private static final int STRING_CAPACITY = 1000;
    private static final String TAG = "FixedSizeStringBuffer";
    private static LinkedList<SoftReference<FixedSizeStringBuffer>> sBufferPool = new LinkedList<>();
    private static final Object sMutex = new Object();
    private char[] mBuf;
    private int mLen = 0;

    private FixedSizeStringBuffer(int i) {
        this.mBuf = new char[i];
    }

    public static void freeBuffer(FixedSizeStringBuffer fixedSizeStringBuffer) {
        synchronized (sMutex) {
            sBufferPool.add(new SoftReference<>(fixedSizeStringBuffer));
        }
    }

    public static FixedSizeStringBuffer getBuffer() {
        FixedSizeStringBuffer fixedSizeStringBuffer;
        synchronized (sMutex) {
            fixedSizeStringBuffer = null;
            if (!sBufferPool.isEmpty()) {
                try {
                    SoftReference<FixedSizeStringBuffer> remove = sBufferPool.remove();
                    if (remove != null) {
                        fixedSizeStringBuffer = remove.get();
                    }
                } catch (NoSuchElementException e) {
                    Log.e(TAG, "sBufferPool remove() throw exception: " + e.toString());
                    sBufferPool = new LinkedList<>();
                }
            }
        }
        if (fixedSizeStringBuffer == null) {
            fixedSizeStringBuffer = new FixedSizeStringBuffer(1000);
        }
        fixedSizeStringBuffer.setLength(0);
        return fixedSizeStringBuffer;
    }

    public void append(String str) {
        append(str, 0, str.length());
    }

    public void append(String str, int i, int i2) {
        int i3 = i2 - i;
        int i4 = this.mLen;
        int i5 = i3 + i4;
        char[] cArr = this.mBuf;
        if (i5 <= cArr.length) {
            str.getChars(i, i2, cArr, i4);
            this.mLen += i3;
        }
    }

    public void assign(String str) {
        assign(str, str.length());
    }

    public void assign(String str, int i) {
        char[] cArr = this.mBuf;
        if (i <= cArr.length) {
            this.mLen = i;
            str.getChars(0, i, cArr, 0);
        }
    }

    public void move(int i) {
        this.mLen += i;
    }

    public void setLength(int i) {
        this.mLen = i;
    }

    public String toString() {
        return String.valueOf(this.mBuf, 0, this.mLen);
    }
}
