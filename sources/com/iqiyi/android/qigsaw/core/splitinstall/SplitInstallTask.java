package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller;
import com.iqiyi.android.qigsaw.core.splitreport.SplitBriefInfo;
import com.iqiyi.android.qigsaw.core.splitreport.SplitInstallError;
import com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: classes2.dex */
abstract class SplitInstallTask implements Runnable {
    private final SplitInstaller installer;
    private final Collection<SplitInfo> needUpdateSplits;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallTask(SplitInstaller splitInstaller, Collection<SplitInfo> collection) {
        this.installer = splitInstaller;
        this.needUpdateSplits = collection;
    }

    abstract boolean isStartInstallOperation();

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onInstallCompleted(List<SplitInstaller.InstallResult> list) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onInstallFailed(List<SplitInstallError> list) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPreInstall() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        onPreInstall();
        long currentTimeMillis = System.currentTimeMillis();
        boolean isStartInstallOperation = isStartInstallOperation();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(this.needUpdateSplits.size());
        ArrayList arrayList3 = new ArrayList();
        boolean z = true;
        for (SplitInfo splitInfo : this.needUpdateSplits) {
            SplitBriefInfo splitBriefInfo = new SplitBriefInfo(splitInfo.getSplitName(), splitInfo.getSplitVersion(), splitInfo.isBuiltIn());
            try {
                long currentTimeMillis2 = System.currentTimeMillis();
                SplitInstaller.InstallResult install = this.installer.install(isStartInstallOperation, splitInfo);
                arrayList2.add(splitBriefInfo.setInstallFlag(install.firstInstalled ? 1 : 2).setTimeCost(System.currentTimeMillis() - currentTimeMillis2));
                arrayList.add(install);
            } catch (SplitInstaller.InstallException e) {
                arrayList3.add(new SplitInstallError(splitBriefInfo, e.getErrorCode(), e.getCause()));
                z = false;
                if (isStartInstallOperation) {
                    break;
                }
            }
        }
        SplitInstallReporter installReporter = SplitInstallReporterManager.getInstallReporter();
        if (z) {
            onInstallCompleted(arrayList);
            if (installReporter != null) {
                if (isStartInstallOperation) {
                    installReporter.onStartInstallOK(arrayList2, System.currentTimeMillis() - currentTimeMillis);
                    return;
                } else {
                    installReporter.onDeferredInstallOK(arrayList2, System.currentTimeMillis() - currentTimeMillis);
                    return;
                }
            }
            return;
        }
        onInstallFailed(arrayList3);
        if (installReporter != null) {
            if (isStartInstallOperation) {
                installReporter.onStartInstallFailed(arrayList2, arrayList3.get(0), System.currentTimeMillis() - currentTimeMillis);
            } else {
                installReporter.onDeferredInstallFailed(arrayList2, arrayList3, System.currentTimeMillis() - currentTimeMillis);
            }
        }
    }
}
