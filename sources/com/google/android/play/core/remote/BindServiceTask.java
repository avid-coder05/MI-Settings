package com.google.android.play.core.remote;

/* loaded from: classes2.dex */
final class BindServiceTask extends RemoteTask {
    private final RemoteManager remoteManager;
    private final RemoteTask task;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BindServiceTask(RemoteManager remoteManager, RemoteTask remoteTask) {
        this.remoteManager = remoteManager;
        this.task = remoteTask;
    }

    @Override // com.google.android.play.core.remote.RemoteTask
    protected void execute() {
        this.remoteManager.bindServiceInternal(this.task);
    }
}
