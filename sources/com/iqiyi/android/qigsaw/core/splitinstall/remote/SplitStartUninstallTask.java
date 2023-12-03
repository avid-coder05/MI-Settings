package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitPendingUninstallManager;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitUninstallReporterManager;
import com.iqiyi.android.qigsaw.core.splitreport.SplitUninstallReporter;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitStartUninstallTask implements Runnable {
    private static final String TAG = "SplitStartUninstallTask";
    private final List<SplitInfo> uninstallSplits;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitStartUninstallTask(List<SplitInfo> list) {
        this.uninstallSplits = list;
    }

    @Override // java.lang.Runnable
    public void run() {
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList arrayList = new ArrayList(this.uninstallSplits.size());
        for (SplitInfo splitInfo : this.uninstallSplits) {
            SplitLog.d(TAG, "split %s need to be uninstalled, try to delete its files", splitInfo.getSplitName());
            FileUtil.deleteDir(SplitPathManager.require().getSplitRootDir(splitInfo));
            arrayList.add(splitInfo.getSplitName());
        }
        SplitUninstallReporter uninstallReporter = SplitUninstallReporterManager.getUninstallReporter();
        if (uninstallReporter != null) {
            uninstallReporter.onSplitUninstallOK(arrayList, System.currentTimeMillis() - currentTimeMillis);
        }
        Object[] objArr = new Object[1];
        objArr[0] = new SplitPendingUninstallManager().deletePendingUninstallSplitsRecord() ? "Succeed" : "Failed";
        SplitLog.d(TAG, "%s to delete record file of pending uninstall splits!", objArr);
    }
}
