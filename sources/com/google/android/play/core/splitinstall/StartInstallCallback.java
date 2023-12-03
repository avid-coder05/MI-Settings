package com.google.android.play.core.splitinstall;

import android.os.Bundle;
import com.google.android.play.core.tasks.TaskWrapper;

/* loaded from: classes2.dex */
final class StartInstallCallback extends SplitInstallServiceCallbackImpl<Integer> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public StartInstallCallback(SplitInstallService splitInstallService, TaskWrapper<Integer> taskWrapper) {
        super(splitInstallService, taskWrapper);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallServiceCallbackImpl, com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceCallbackProxy
    public void onStartInstall(int i, Bundle bundle) {
        super.onStartInstall(i, bundle);
        this.mTask.setResult(Integer.valueOf(i));
    }
}
