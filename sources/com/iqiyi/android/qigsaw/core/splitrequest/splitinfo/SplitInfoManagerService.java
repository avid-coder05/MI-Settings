package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import android.content.Context;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public class SplitInfoManagerService {
    private static final AtomicReference<SplitInfoManager> sReference = new AtomicReference<>();

    private static SplitInfoManagerImpl createSplitInfoManager(Context context, boolean z) {
        SplitInfoVersionManager createSplitInfoVersionManager = SplitInfoVersionManagerImpl.createSplitInfoVersionManager(context, z);
        SplitInfoManagerImpl splitInfoManagerImpl = new SplitInfoManagerImpl();
        splitInfoManagerImpl.attach(createSplitInfoVersionManager);
        return splitInfoManagerImpl;
    }

    public static SplitInfoManager getInstance() {
        return sReference.get();
    }

    public static void install(Context context, boolean z) {
        sReference.compareAndSet(null, createSplitInfoManager(context, z));
    }
}
