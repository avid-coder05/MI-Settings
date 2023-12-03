package com.google.android.play.core.splitinstall;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class ChangeSessionStatusWorker implements Runnable {
    private final SplitSessionStatusChanger changer;
    private final int errorCode;
    private final int status;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChangeSessionStatusWorker(SplitSessionStatusChanger splitSessionStatusChanger, int i) {
        this(splitSessionStatusChanger, i, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChangeSessionStatusWorker(SplitSessionStatusChanger splitSessionStatusChanger, int i, int i2) {
        this.changer = splitSessionStatusChanger;
        this.status = i;
        this.errorCode = i2;
    }

    @Override // java.lang.Runnable
    public void run() {
        int i = this.errorCode;
        if (i != 0) {
            SplitSessionStatusChanger splitSessionStatusChanger = this.changer;
            splitSessionStatusChanger.mRegistry.notifyListeners(splitSessionStatusChanger.sessionState.a(this.status, i));
            return;
        }
        SplitSessionStatusChanger splitSessionStatusChanger2 = this.changer;
        splitSessionStatusChanger2.mRegistry.notifyListeners(splitSessionStatusChanger2.sessionState.a(this.status));
    }
}
