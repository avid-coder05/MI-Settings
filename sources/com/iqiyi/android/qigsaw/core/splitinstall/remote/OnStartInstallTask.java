package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import android.os.Bundle;
import android.os.RemoteException;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback;
import java.util.List;

/* loaded from: classes2.dex */
final class OnStartInstallTask extends DefaultTask {
    private final List<Bundle> mModuleNames;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OnStartInstallTask(ISplitInstallServiceCallback iSplitInstallServiceCallback, List<Bundle> list) {
        super(iSplitInstallServiceCallback);
        this.mModuleNames = list;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask
    void execute(SplitInstallSupervisor splitInstallSupervisor) throws RemoteException {
        splitInstallSupervisor.startInstall(this.mModuleNames, this);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask, com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onStartInstall(int i, Bundle bundle) {
        super.onStartInstall(i, bundle);
        try {
            this.mCallback.onStartInstall(i, bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
