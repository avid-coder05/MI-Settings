package com.iqiyi.android.qigsaw.core.splitinstall;

import android.app.Activity;
import android.content.Context;
import com.iqiyi.android.qigsaw.core.splitdownload.Downloader;
import com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public final class SplitApkInstaller {
    private static final AtomicReference<SplitInstallSupervisor> sSplitApkInstallerRef = new AtomicReference<>();

    private SplitApkInstaller() {
    }

    public static SplitInstallSupervisor getSplitInstallSupervisor() {
        return sSplitApkInstallerRef.get();
    }

    public static void install(Context context, Downloader downloader, Class<? extends Activity> cls, boolean z) {
        AtomicReference<SplitInstallSupervisor> atomicReference = sSplitApkInstallerRef;
        if (atomicReference.get() == null) {
            atomicReference.set(new SplitInstallSupervisorImpl(context, new SplitInstallSessionManagerImpl(context), downloader, cls, z));
        }
    }

    public static void startUninstallSplits(Context context) {
        AtomicReference<SplitInstallSupervisor> atomicReference = sSplitApkInstallerRef;
        if (atomicReference.get() == null) {
            throw new RuntimeException("Have you install SplitApkInstaller?");
        }
        atomicReference.get().startUninstall(context);
    }
}
