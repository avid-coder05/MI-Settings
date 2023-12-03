package com.iqiyi.android.qigsaw.core.splitreport;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import java.util.List;

/* loaded from: classes2.dex */
public class DefaultSplitInstallReporter implements SplitInstallReporter {
    private static final String TAG = "SplitInstallReporter";
    protected final Context context;

    public DefaultSplitInstallReporter(Context context) {
        this.context = context;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter
    public void onDeferredInstallFailed(List<SplitBriefInfo> list, List<SplitInstallError> list2, long j) {
        for (SplitInstallError splitInstallError : list2) {
            SplitLog.printErrStackTrace(TAG, splitInstallError.cause, "Defer to install split %s failed with error code %d, cost time %d ms.", splitInstallError.splitName, Integer.valueOf(splitInstallError.errorCode), Long.valueOf(j));
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter
    public void onDeferredInstallOK(List<SplitBriefInfo> list, long j) {
        SplitLog.i(TAG, "Deferred install %s OK, cost time %d ms.", list.toString(), Long.valueOf(j));
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter
    public void onStartInstallFailed(List<SplitBriefInfo> list, SplitInstallError splitInstallError, long j) {
        SplitLog.printErrStackTrace(TAG, splitInstallError.cause, "Start to install split %s failed, cost time %d ms.", splitInstallError.splitName, Long.valueOf(j));
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter
    public void onStartInstallOK(List<SplitBriefInfo> list, long j) {
        SplitLog.i(TAG, "Start install %s OK, cost time %d ms.", list.toString(), Long.valueOf(j));
    }
}
