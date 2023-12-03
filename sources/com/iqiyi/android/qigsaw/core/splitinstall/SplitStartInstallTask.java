package com.iqiyi.android.qigsaw.core.splitinstall;

import android.content.Intent;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller;
import com.iqiyi.android.qigsaw.core.splitreport.SplitInstallError;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
final class SplitStartInstallTask extends SplitInstallTask {
    private final SplitInstallSessionManager mSessionManager;
    private final SplitInstallInternalSessionState mSessionState;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitStartInstallTask(int i, SplitInstaller splitInstaller, SplitInstallSessionManager splitInstallSessionManager, List<SplitInfo> list) {
        super(splitInstaller, list);
        this.mSessionState = splitInstallSessionManager.getSessionState(i);
        this.mSessionManager = splitInstallSessionManager;
    }

    private void emitSessionStatus() {
        this.mSessionManager.emitSessionState(this.mSessionState);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallTask
    boolean isStartInstallOperation() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallTask
    public void onInstallCompleted(List<SplitInstaller.InstallResult> list) {
        super.onInstallCompleted(list);
        ArrayList arrayList = new ArrayList(list.size());
        for (SplitInstaller.InstallResult installResult : list) {
            Intent intent = new Intent();
            List<String> list2 = installResult.addedDexPaths;
            if (list2 != null) {
                intent.putStringArrayListExtra(SplitConstants.KEY_ADDED_DEX, (ArrayList) list2);
            }
            File file = installResult.splitDexOptDir;
            if (file != null) {
                intent.putExtra(SplitConstants.KEY_DEX_OPT_DIR, file.getAbsolutePath());
            }
            File file2 = installResult.splitLibDir;
            if (file2 != null) {
                intent.putExtra(SplitConstants.KEY_NATIVE_LIB_DIR, file2.getAbsolutePath());
            }
            intent.putExtra(SplitConstants.KEY_APK, installResult.apkFile.getAbsolutePath());
            intent.putExtra(SplitConstants.KET_NAME, installResult.splitName);
            arrayList.add(intent);
        }
        this.mSessionState.setSplitFileIntents(arrayList);
        this.mSessionManager.changeSessionState(this.mSessionState.sessionId(), 10);
        emitSessionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallTask
    public void onInstallFailed(List<SplitInstallError> list) {
        super.onInstallFailed(list);
        this.mSessionState.setErrorCode(list.get(0).errorCode);
        this.mSessionManager.changeSessionState(this.mSessionState.sessionId(), 6);
        emitSessionStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallTask
    public void onPreInstall() {
        super.onPreInstall();
        this.mSessionManager.changeSessionState(this.mSessionState.sessionId(), 4);
        emitSessionStatus();
    }
}
