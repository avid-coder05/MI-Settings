package com.google.android.play.core.splitinstall;

import android.os.RemoteException;
import com.google.android.play.core.remote.RemoteTask;
import com.google.android.play.core.tasks.TaskWrapper;
import java.util.List;

/* loaded from: classes2.dex */
final class GetSessionStatesTask extends RemoteTask {
    private final SplitInstallService mSplitInstallService;
    private final TaskWrapper<List<SplitInstallSessionState>> mTask;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GetSessionStatesTask(SplitInstallService splitInstallService, TaskWrapper taskWrapper, TaskWrapper<List<SplitInstallSessionState>> taskWrapper2) {
        super(taskWrapper);
        this.mSplitInstallService = splitInstallService;
        this.mTask = taskWrapper2;
    }

    @Override // com.google.android.play.core.remote.RemoteTask
    protected void execute() {
        try {
            this.mSplitInstallService.mSplitRemoteManager.getIInterface().getSessionStates(this.mSplitInstallService.mPackageName, new GetSessionStatesCallback(this.mSplitInstallService, this.mTask));
        } catch (RemoteException e) {
            SplitInstallService.playCore.error(e, "getSessionStates", new Object[0]);
            this.mTask.setException(new RuntimeException(e));
        }
    }
}
