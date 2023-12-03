package com.google.android.play.core.splitinstall;

import android.os.RemoteException;
import com.google.android.play.core.remote.RemoteTask;
import com.google.android.play.core.tasks.TaskWrapper;
import java.util.List;

/* loaded from: classes2.dex */
final class DeferredUninstallTask extends RemoteTask {
    private final SplitInstallService mSplitInstallService;
    private final TaskWrapper<Void> mTask;
    private final List<String> moduleNames;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeferredUninstallTask(SplitInstallService splitInstallService, TaskWrapper taskWrapper, List<String> list, TaskWrapper<Void> taskWrapper2) {
        super(taskWrapper);
        this.mSplitInstallService = splitInstallService;
        this.moduleNames = list;
        this.mTask = taskWrapper2;
    }

    @Override // com.google.android.play.core.remote.RemoteTask
    protected void execute() {
        try {
            this.mSplitInstallService.mSplitRemoteManager.getIInterface().deferredUninstall(this.mSplitInstallService.mPackageName, SplitInstallService.wrapModuleNames(this.moduleNames), SplitInstallService.wrapVersionCode(), new DeferredUninstallCallback(this.mSplitInstallService, this.mTask));
        } catch (RemoteException e) {
            SplitInstallService.playCore.error(e, "deferredUninstall(%s)", this.moduleNames);
            this.mTask.setException(new RuntimeException(e));
        }
    }
}
