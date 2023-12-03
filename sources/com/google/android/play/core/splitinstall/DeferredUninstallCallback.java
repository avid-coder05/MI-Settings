package com.google.android.play.core.splitinstall;

import android.os.Bundle;
import com.google.android.play.core.tasks.TaskWrapper;

/* loaded from: classes2.dex */
final class DeferredUninstallCallback extends SplitInstallServiceCallbackImpl<Void> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DeferredUninstallCallback(SplitInstallService splitInstallService, TaskWrapper<Void> taskWrapper) {
        super(splitInstallService, taskWrapper);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallServiceCallbackImpl, com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceCallbackProxy
    public void onDeferredUninstall(Bundle bundle) {
        super.onDeferredUninstall(bundle);
        this.mTask.setResult(null);
    }
}
