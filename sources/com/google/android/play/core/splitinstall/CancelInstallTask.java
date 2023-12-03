package com.google.android.play.core.splitinstall;

import android.os.RemoteException;
import com.google.android.play.core.remote.RemoteTask;
import com.google.android.play.core.tasks.TaskWrapper;

/* loaded from: classes2.dex */
final class CancelInstallTask extends RemoteTask {
    private final SplitInstallService mSplitInstallService;
    private final TaskWrapper<Void> mTask;
    private final int sessionId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CancelInstallTask(SplitInstallService splitInstallService, TaskWrapper<Void> taskWrapper, int i, TaskWrapper<Void> taskWrapper2) {
        super(taskWrapper2);
        this.mSplitInstallService = splitInstallService;
        this.mTask = taskWrapper;
        this.sessionId = i;
    }

    @Override // com.google.android.play.core.remote.RemoteTask
    protected void execute() {
        try {
            this.mSplitInstallService.mSplitRemoteManager.getIInterface().cancelInstall(this.mSplitInstallService.mPackageName, this.sessionId, SplitInstallService.wrapVersionCode(), new CancelInstallCallback(this.mSplitInstallService, this.mTask));
        } catch (RemoteException e) {
            SplitInstallService.playCore.error(e, "cancelInstall(%d)", Integer.valueOf(this.sessionId));
            this.mTask.setException(new RuntimeException(e));
        }
    }
}
