package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import android.os.Bundle;
import android.os.RemoteException;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback;
import java.util.List;

/* loaded from: classes2.dex */
final class OnDeferredUninstallTask extends DefaultTask {
    private final List<Bundle> mModuleNames;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OnDeferredUninstallTask(ISplitInstallServiceCallback iSplitInstallServiceCallback, List<Bundle> list) {
        super(iSplitInstallServiceCallback);
        this.mModuleNames = list;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask
    void execute(SplitInstallSupervisor splitInstallSupervisor) throws RemoteException {
        splitInstallSupervisor.deferredUninstall(this.mModuleNames, this);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.DefaultTask, com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onDeferredUninstall(Bundle bundle) {
        super.onDeferredUninstall(bundle);
        try {
            this.mCallback.onDeferredUninstall(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
