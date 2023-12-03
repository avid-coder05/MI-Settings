package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import android.os.Bundle;
import android.os.RemoteException;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback;
import java.util.List;

/* loaded from: classes2.dex */
final class OnGetSessionStatesTask extends DefaultTask {
    /* JADX INFO: Access modifiers changed from: package-private */
    public OnGetSessionStatesTask(ISplitInstallServiceCallback iSplitInstallServiceCallback) {
        super(iSplitInstallServiceCallback);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask
    void execute(SplitInstallSupervisor splitInstallSupervisor) throws RemoteException {
        splitInstallSupervisor.getSessionStates(this);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask, com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onGetSessionStates(List<Bundle> list) {
        super.onGetSessionStates(list);
        try {
            this.mCallback.onGetSessionStates(list);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
