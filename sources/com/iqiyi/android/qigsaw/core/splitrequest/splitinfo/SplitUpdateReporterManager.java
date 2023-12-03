package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public class SplitUpdateReporterManager {
    private static final AtomicReference<SplitUpdateReporter> sUpdateReporterRef = new AtomicReference<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SplitUpdateReporter getUpdateReporter() {
        return sUpdateReporterRef.get();
    }

    public static void install(SplitUpdateReporter splitUpdateReporter) {
        sUpdateReporterRef.compareAndSet(null, splitUpdateReporter);
    }
}
