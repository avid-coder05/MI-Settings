package com.iqiyi.android.qigsaw.core.splitload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes2.dex */
public final class SplitLoad {
    public static final int MULTIPLE_CLASSLOADER = 1;
    public static final int SINGLE_CLASSLOADER = 2;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SplitLoadMode {
    }
}
