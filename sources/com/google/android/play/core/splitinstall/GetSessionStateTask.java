package com.google.android.play.core.splitinstall;

import android.os.RemoteException;
import com.google.android.play.core.remote.RemoteTask;
import com.google.android.play.core.tasks.TaskWrapper;

/* loaded from: classes2.dex */
final class GetSessionStateTask extends RemoteTask {
    private final SplitInstallService mSplitInstallService;
    private final TaskWrapper<SplitInstallSessionState> mTask;
    private final int sessionId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GetSessionStateTask(SplitInstallService splitInstallService, TaskWrapper taskWrapper, int i, TaskWrapper<SplitInstallSessionState> taskWrapper2) {
        super(taskWrapper);
        this.mSplitInstallService = splitInstallService;
        this.sessionId = i;
        this.mTask = taskWrapper2;
    }

    @Override // com.google.android.play.core.remote.RemoteTask
    protected void execute() {
        try {
            this.mSplitInstallService.mSplitRemoteManager.getIInterface().getSessionState(this.mSplitInstallService.mPackageName, this.sessionId, new GetSessionStateCallback(this.mSplitInstallService, this.mTask));
        } catch (RemoteException e) {
            SplitInstallService.playCore.error(e, "getSessionState(%d)", Integer.valueOf(this.sessionId));
            this.mTask.setException(new RuntimeException(e));
        }
    }
}
