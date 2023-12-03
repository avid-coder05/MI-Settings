package com.iqiyi.android.qigsaw.core.splitinstall;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import com.iqiyi.android.qigsaw.core.splitdownload.DownloadRequest;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
final class SplitInstallInternalSessionState {
    private long bytesDownloaded;
    final List<DownloadRequest> downloadRequests;
    private int errorCode;
    private final List<String> moduleNames;
    final List<SplitInfo> needInstalledSplits;
    private int sessionId;
    private List<Intent> splitFileIntents;
    private int status;
    private long totalBytesToDownload;
    private PendingIntent userConfirmationIntent;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallInternalSessionState(int i, List<String> list, List<SplitInfo> list2, List<DownloadRequest> list3) {
        this.sessionId = i;
        this.moduleNames = list;
        this.needInstalledSplits = list2;
        this.downloadRequests = list3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bundle transform2Bundle(SplitInstallInternalSessionState splitInstallInternalSessionState) {
        Bundle bundle = new Bundle();
        bundle.putInt("session_id", splitInstallInternalSessionState.sessionId());
        bundle.putInt("status", splitInstallInternalSessionState.status());
        bundle.putInt("error_code", splitInstallInternalSessionState.errorCode);
        bundle.putLong("total_bytes_to_download", splitInstallInternalSessionState.totalBytesToDownload);
        bundle.putLong("bytes_downloaded", splitInstallInternalSessionState.bytesDownloaded);
        bundle.putStringArrayList("module_names", (ArrayList) splitInstallInternalSessionState.moduleNames());
        bundle.putParcelable("user_confirmation_intent", splitInstallInternalSessionState.userConfirmationIntent);
        bundle.putParcelableArrayList("split_file_intents", (ArrayList) splitInstallInternalSessionState.splitFileIntents);
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<String> moduleNames() {
        return this.moduleNames;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int sessionId() {
        return this.sessionId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBytesDownloaded(long j) {
        if (this.bytesDownloaded != j) {
            this.bytesDownloaded = j;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setErrorCode(int i) {
        this.errorCode = i;
    }

    void setSessionId(int i) {
        this.sessionId = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSplitFileIntents(List<Intent> list) {
        this.splitFileIntents = list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStatus(int i) {
        if (this.status != i) {
            this.status = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTotalBytesToDownload(long j) {
        this.totalBytesToDownload = j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUserConfirmationIntent(PendingIntent pendingIntent) {
        this.userConfirmationIntent = pendingIntent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int status() {
        return this.status;
    }
}
