package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import java.util.List;

/* loaded from: classes2.dex */
final class StartDownloadCallback implements DownloadCallback {
    private final SplitSessionInstaller installer;
    private final int sessionId;
    private final SplitInstallSessionManager sessionManager;
    private final SplitInstallInternalSessionState sessionState;
    private final List<SplitInfo> splitInfoList;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StartDownloadCallback(SplitInstaller splitInstaller, int i, SplitInstallSessionManager splitInstallSessionManager, List<SplitInfo> list) {
        this.sessionId = i;
        this.sessionManager = splitInstallSessionManager;
        this.installer = new SplitSessionInstallerImpl(splitInstaller, splitInstallSessionManager, SplitInstallerExecutor.getExecutor());
        this.splitInfoList = list;
        this.sessionState = splitInstallSessionManager.getSessionState(i);
    }

    private void broadcastSessionStatusChange() {
        this.sessionManager.emitSessionState(this.sessionState);
    }

    private void onInstall() {
        this.installer.install(this.sessionId, this.splitInfoList);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onCanceled() {
        this.sessionManager.changeSessionState(this.sessionId, 7);
        broadcastSessionStatusChange();
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onCanceling() {
        this.sessionManager.changeSessionState(this.sessionId, 9);
        broadcastSessionStatusChange();
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onCompleted() {
        this.sessionManager.changeSessionState(this.sessionId, 3);
        broadcastSessionStatusChange();
        onInstall();
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onError(int i) {
        this.sessionState.setErrorCode(-10);
        this.sessionManager.changeSessionState(this.sessionId, 6);
        broadcastSessionStatusChange();
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onProgress(long j) {
        this.sessionState.setBytesDownloaded(j);
        this.sessionManager.changeSessionState(this.sessionId, 2);
        broadcastSessionStatusChange();
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onStart() {
        this.sessionManager.changeSessionState(this.sessionId, 2);
        broadcastSessionStatusChange();
    }
}
