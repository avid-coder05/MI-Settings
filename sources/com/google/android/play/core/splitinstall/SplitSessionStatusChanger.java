package com.google.android.play.core.splitinstall;

/* loaded from: classes2.dex */
public class SplitSessionStatusChanger {
    final SplitInstallListenerRegistry mRegistry;
    final SplitInstallSessionState sessionState;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitSessionStatusChanger(SplitInstallListenerRegistry splitInstallListenerRegistry, SplitInstallSessionState splitInstallSessionState) {
        this.mRegistry = splitInstallListenerRegistry;
        this.sessionState = splitInstallSessionState;
    }

    public void changeStatus(int i) {
        this.mRegistry.mMainHandler.post(new ChangeSessionStatusWorker(this, i));
    }

    public void changeStatus(int i, int i2) {
        this.mRegistry.mMainHandler.post(new ChangeSessionStatusWorker(this, i, i2));
    }
}
