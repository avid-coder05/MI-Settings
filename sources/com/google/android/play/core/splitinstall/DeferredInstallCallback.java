package com.google.android.play.core.splitinstall;

import android.os.Bundle;
import com.google.android.play.core.tasks.TaskWrapper;

/* loaded from: classes2.dex */
final class DeferredInstallCallback extends SplitInstallServiceCallbackImpl<Void> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DeferredInstallCallback(SplitInstallService splitInstallService, TaskWrapper<Void> taskWrapper) {
        super(splitInstallService, taskWrapper);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallServiceCallbackImpl, com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceCallbackProxy
    public void onDeferredInstall(Bundle bundle) {
        super.onDeferredInstall(bundle);
        this.mTask.setResult(null);
    }
}
