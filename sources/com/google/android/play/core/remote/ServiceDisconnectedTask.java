package com.google.android.play.core.remote;

/* loaded from: classes2.dex */
final class ServiceDisconnectedTask extends RemoteTask {
    private final ServiceConnectionImpl mServiceConnection;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServiceDisconnectedTask(ServiceConnectionImpl serviceConnectionImpl) {
        this.mServiceConnection = serviceConnectionImpl;
    }

    @Override // com.google.android.play.core.remote.RemoteTask
    protected void execute() {
        this.mServiceConnection.mRemoteManager.unlinkToDeath();
        RemoteManager remoteManager = this.mServiceConnection.mRemoteManager;
        remoteManager.mIInterface = null;
        remoteManager.mBindingService = false;
    }
}
