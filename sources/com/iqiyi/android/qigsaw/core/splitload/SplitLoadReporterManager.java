package com.iqiyi.android.qigsaw.core.splitload;

import com.iqiyi.android.qigsaw.core.splitreport.SplitLoadReporter;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public class SplitLoadReporterManager {
    private static final AtomicReference<SplitLoadReporter> sLoadReporterRef = new AtomicReference<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SplitLoadReporter getLoadReporter() {
        return sLoadReporterRef.get();
    }

    public static void install(SplitLoadReporter splitLoadReporter) {
        sLoadReporterRef.compareAndSet(null, splitLoadReporter);
    }
}
