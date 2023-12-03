package com.google.android.play.core.remote;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class ServiceConnectionImpl implements ServiceConnection {
    final RemoteManager mRemoteManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServiceConnectionImpl(RemoteManager remoteManager) {
        this.mRemoteManager = remoteManager;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.mRemoteManager.mPlayCore.info("ServiceConnectionImpl.onServiceConnected(%s)", componentName);
        this.mRemoteManager.post(new ServiceConnectedTask(this, iBinder));
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        this.mRemoteManager.mPlayCore.info("ServiceConnectionImpl.onServiceDisconnected(%s)", componentName);
        this.mRemoteManager.post(new ServiceDisconnectedTask(this));
    }
}
