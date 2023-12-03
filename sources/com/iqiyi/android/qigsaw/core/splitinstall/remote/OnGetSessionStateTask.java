package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import android.os.Bundle;
import android.os.RemoteException;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback;

/* loaded from: classes2.dex */
class OnGetSessionStateTask extends DefaultTask {
    private final int mSessionId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OnGetSessionStateTask(ISplitInstallServiceCallback iSplitInstallServiceCallback, int i) {
        super(iSplitInstallServiceCallback);
        this.mSessionId = i;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask
    void execute(SplitInstallSupervisor splitInstallSupervisor) throws RemoteException {
        splitInstallSupervisor.getSessionState(this.mSessionId, this);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask, com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onGetSession(int i, Bundle bundle) {
        super.onGetSession(i, bundle);
        try {
            this.mCallback.onGetSession(i, bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
