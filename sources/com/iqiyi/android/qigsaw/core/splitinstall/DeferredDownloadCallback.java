package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import java.util.List;

/* loaded from: classes2.dex */
final class DeferredDownloadCallback implements DownloadCallback {
    private final List<SplitInfo> splitInfoList;
    private final SplitInstaller splitInstaller;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeferredDownloadCallback(SplitInstaller splitInstaller, List<SplitInfo> list) {
        this.splitInfoList = list;
        this.splitInstaller = splitInstaller;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onCanceled() {
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onCanceling() {
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onCompleted() {
        SplitInstallerExecutor.getExecutor().execute(new SplitDeferredInstallTask(this.splitInstaller, this.splitInfoList));
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onError(int i) {
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onProgress(long j) {
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback
    public void onStart() {
    }
}
