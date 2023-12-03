package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public class SplitInstallReporterManager {
    private static final AtomicReference<SplitInstallReporter> sInstallReporterRef = new AtomicReference<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SplitInstallReporter getInstallReporter() {
        return sInstallReporterRef.get();
    }

    public static void install(SplitInstallReporter splitInstallReporter) {
        sInstallReporterRef.compareAndSet(null, splitInstallReporter);
    }
}
