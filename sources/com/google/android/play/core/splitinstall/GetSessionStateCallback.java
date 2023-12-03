package com.google.android.play.core.splitinstall;

import android.os.Bundle;
import com.google.android.play.core.tasks.TaskWrapper;

/* loaded from: classes2.dex */
final class GetSessionStateCallback extends SplitInstallServiceCallbackImpl<SplitInstallSessionState> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public GetSessionStateCallback(SplitInstallService splitInstallService, TaskWrapper<SplitInstallSessionState> taskWrapper) {
        super(splitInstallService, taskWrapper);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallServiceCallbackImpl, com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceCallbackProxy
    public void onGetSession(int i, Bundle bundle) {
        super.onGetSession(i, bundle);
        this.mTask.setResult(SplitInstallSessionState.createFrom(bundle));
    }
}
