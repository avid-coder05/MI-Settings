package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import android.os.Bundle;
import android.os.RemoteException;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitApkInstaller;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback;
import com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor;
import java.util.List;
import miui.mipub.MipubErrCode;

/* loaded from: classes2.dex */
abstract class DefaultTask implements Runnable, SplitInstallSupervisor.Callback {
    private static final String TAG = "Split:DefaultTask";
    private final SplitInstallSupervisor installSupervisor = SplitApkInstaller.getSplitInstallSupervisor();
    final ISplitInstallServiceCallback mCallback;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultTask(ISplitInstallServiceCallback iSplitInstallServiceCallback) {
        this.mCallback = iSplitInstallServiceCallback;
    }

    abstract void execute(SplitInstallSupervisor splitInstallSupervisor) throws RemoteException;

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onCancelInstall(int i, Bundle bundle) {
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onDeferredInstall(Bundle bundle) {
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onDeferredUninstall(Bundle bundle) {
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onError(Bundle bundle) {
        try {
            this.mCallback.onError(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onGetSession(int i, Bundle bundle) {
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onGetSessionStates(List<Bundle> list) {
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor.Callback
    public void onStartInstall(int i, Bundle bundle) {
    }

    @Override // java.lang.Runnable
    public void run() {
        SplitInstallSupervisor splitInstallSupervisor = this.installSupervisor;
        if (splitInstallSupervisor != null) {
            try {
                execute(splitInstallSupervisor);
                return;
            } catch (RemoteException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            this.mCallback.onError(SplitInstallSupervisor.bundleErrorCode(MipubErrCode.RESULT_ERROR_AUTH_FAIL));
            SplitLog.w(TAG, "Have you call Qigsaw#onApplicationCreated method?", new Object[0]);
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
    }
}
