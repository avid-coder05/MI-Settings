package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.splitreport.SplitUninstallReporter;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public class SplitUninstallReporterManager {
    private static final AtomicReference<SplitUninstallReporter> sUninstallReporterRef = new AtomicReference<>();

    public static SplitUninstallReporter getUninstallReporter() {
        return sUninstallReporterRef.get();
    }

    public static void install(SplitUninstallReporter splitUninstallReporter) {
        sUninstallReporterRef.compareAndSet(null, splitUninstallReporter);
    }
}
