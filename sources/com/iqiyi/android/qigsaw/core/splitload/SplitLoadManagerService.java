package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Context;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public class SplitLoadManagerService {
    private static final AtomicReference<SplitLoadManager> sReference = new AtomicReference<>();

    private static SplitLoadManager create(Context context, int i, boolean z, boolean z2, String str, String[] strArr, String[] strArr2) {
        return new SplitLoadManagerImpl(context, i, z, z2, str, strArr, strArr2);
    }

    public static SplitLoadManager getInstance() {
        AtomicReference<SplitLoadManager> atomicReference = sReference;
        if (atomicReference.get() != null) {
            return atomicReference.get();
        }
        throw new RuntimeException("Have you invoke SplitLoadManagerService#install(Context) method?");
    }

    public static boolean hasInstance() {
        return sReference.get() != null;
    }

    public static void install(Context context, int i, boolean z, boolean z2, String str, String[] strArr, String[] strArr2) {
        AtomicReference<SplitLoadManager> atomicReference = sReference;
        if (atomicReference.get() == null) {
            atomicReference.set(create(context, i, z, z2, str, strArr, strArr2));
        }
    }
}
