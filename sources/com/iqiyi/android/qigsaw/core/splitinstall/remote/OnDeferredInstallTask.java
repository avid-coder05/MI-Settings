package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import android.os.Bundle;
import android.os.RemoteException;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback;
import java.util.List;

/* loaded from: classes2.dex */
final class OnDeferredInstallTask extends DefaultTask {
    private final List<Bundle> mModuleNames;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OnDeferredInstallTask(ISplitInstallServiceCallback iSplitInstallServiceCallback, List<Bundle> list) {
        super(iSplitInstallServiceCallback);
        this.mModuleNames = list;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask
    void execute(SplitInstallSupervisor splitInstallSupervisor) throws RemoteException {
        splitInstallSupervisor.deferredInstall(this.mModuleNames, this);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask, com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onDeferredInstall(Bundle bundle) {
        super.onDeferredInstall(bundle);
        try {
            this.mCallback.onDeferredInstall(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
