package com.google.android.play.core.remote;

import android.os.IBinder;
import java.util.Iterator;

/* loaded from: classes2.dex */
final class ServiceConnectedTask extends RemoteTask {
    private final IBinder mService;
    private final ServiceConnectionImpl mServiceConnection;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServiceConnectedTask(ServiceConnectionImpl serviceConnectionImpl, IBinder iBinder) {
        this.mServiceConnection = serviceConnectionImpl;
        this.mService = iBinder;
    }

    @Override // com.google.android.play.core.remote.RemoteTask
    protected void execute() {
        RemoteManager remoteManager = this.mServiceConnection.mRemoteManager;
        remoteManager.mIInterface = (T) remoteManager.mRemote.asInterface(this.mService);
        this.mServiceConnection.mRemoteManager.linkToDeath();
        RemoteManager remoteManager2 = this.mServiceConnection.mRemoteManager;
        remoteManager2.mBindingService = false;
        Iterator<RemoteTask> it = remoteManager2.mPendingTasks.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mServiceConnection.mRemoteManager.mPendingTasks.clear();
    }
}
