package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import java.util.List;
import java.util.concurrent.Executor;

/* loaded from: classes2.dex */
final class SplitSessionInstallerImpl implements SplitSessionInstaller {
    private final Executor executor;
    private final SplitInstallSessionManager sessionManager;
    private final SplitInstaller splitInstaller;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitSessionInstallerImpl(SplitInstaller splitInstaller, SplitInstallSessionManager splitInstallSessionManager, Executor executor) {
        this.splitInstaller = splitInstaller;
        this.sessionManager = splitInstallSessionManager;
        this.executor = executor;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitSessionInstaller
    public void install(int i, List<SplitInfo> list) {
        this.executor.execute(new SplitStartInstallTask(i, this.splitInstaller, this.sessionManager, list));
    }
}
