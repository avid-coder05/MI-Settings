package com.google.android.play.core.remote;

/* loaded from: classes2.dex */
final class UnbindServiceTask extends RemoteTask {
    private final RemoteManager mRemoteManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UnbindServiceTask(RemoteManager remoteManager) {
        this.mRemoteManager = remoteManager;
    }

    @Override // com.google.android.play.core.remote.RemoteTask
    protected void execute() {
        RemoteManager remoteManager = this.mRemoteManager;
        if (remoteManager.mIInterface != 0) {
            remoteManager.mContext.unbindService(remoteManager.mServiceConnection);
            RemoteManager remoteManager2 = this.mRemoteManager;
            remoteManager2.mBindingService = false;
            remoteManager2.mIInterface = null;
            remoteManager2.mServiceConnection = null;
        }
    }
}
