package com.iqiyi.android.qigsaw.core.common;

import java.util.Iterator;
import java.util.ServiceLoader;

/* loaded from: classes2.dex */
public class CompatBundle {
    public static final ICompatBundle instance;

    static {
        Iterator it = ServiceLoader.load(ICompatBundle.class).iterator();
        instance = it.hasNext() ? (ICompatBundle) it.next() : null;
    }
}
