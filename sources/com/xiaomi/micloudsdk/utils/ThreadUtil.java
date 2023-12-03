package com.xiaomi.micloudsdk.utils;

import android.os.Looper;

/* loaded from: classes2.dex */
public class ThreadUtil {
    public static void ensureWorkerThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalArgumentException("You must call this method on the worker thread");
        }
    }
}
