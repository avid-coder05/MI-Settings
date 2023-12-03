package com.google.android.play.core.remote;

import android.os.IBinder;

/* loaded from: classes2.dex */
final class DeathRecipientImpl implements IBinder.DeathRecipient {
    private final RemoteManager mRemoteManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeathRecipientImpl(RemoteManager remoteManager) {
        this.mRemoteManager = remoteManager;
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        this.mRemoteManager.reportBinderDeath();
    }
}
