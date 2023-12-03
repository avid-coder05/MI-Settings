package com.google.android.play.core.splitinstall;

import android.os.Bundle;
import com.google.android.play.core.splitinstall.protocol.SplitInstallServiceCallback;
import com.google.android.play.core.tasks.TaskWrapper;
import java.util.List;

/* loaded from: classes2.dex */
class SplitInstallServiceCallbackImpl<T> extends SplitInstallServiceCallback {
    private final SplitInstallService mSplitInstallService;
    final TaskWrapper<T> mTask;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallServiceCallbackImpl(SplitInstallService splitInstallService, TaskWrapper<T> taskWrapper) {
        this.mSplitInstallService = splitInstallService;
        this.mTask = taskWrapper;
    }

    public void onCancelInstall(int i, Bundle bundle) {
        this.mSplitInstallService.mSplitRemoteManager.unbindService();
        SplitInstallService.playCore.info("onCancelInstall(%d)", Integer.valueOf(i));
    }

    @Override // com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceCallbackProxy
    public void onCompleteInstall(int i) {
        this.mSplitInstallService.mSplitRemoteManager.unbindService();
        SplitInstallService.playCore.info("onCompleteInstall(%d)", Integer.valueOf(i));
    }

    public void onDeferredInstall(Bundle bundle) {
        this.mSplitInstallService.mSplitRemoteManager.unbindService();
        SplitInstallService.playCore.info("onDeferredInstall", new Object[0]);
    }

    public void onDeferredUninstall(Bundle bundle) {
        this.mSplitInstallService.mSplitRemoteManager.unbindService();
        SplitInstallService.playCore.info("onDeferredUninstall", new Object[0]);
    }

    @Override // com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceCallbackProxy
    public final void onError(Bundle bundle) {
        this.mSplitInstallService.mSplitRemoteManager.unbindService();
        int i = bundle.getInt("error_code");
        String[] stringArray = bundle.getStringArray("module_names");
        SplitInstallService.playCore.info("onError(%d)", Integer.valueOf(i));
        this.mTask.setException(new SplitInstallException(i, stringArray));
    }

    public void onGetSession(int i, Bundle bundle) {
        this.mSplitInstallService.mSplitRemoteManager.unbindService();
        SplitInstallService.playCore.info("onGetSession(%d)", Integer.valueOf(i));
    }

    public void onGetSessionStates(List<Bundle> list) {
        this.mSplitInstallService.mSplitRemoteManager.unbindService();
        SplitInstallService.playCore.info("onGetSessionStates", new Object[0]);
    }

    @Override // com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceCallbackProxy
    public void onStartInstall(int i, Bundle bundle) {
        this.mSplitInstallService.mSplitRemoteManager.unbindService();
        SplitInstallService.playCore.info("onStartInstall(%d)", Integer.valueOf(i));
    }
}
