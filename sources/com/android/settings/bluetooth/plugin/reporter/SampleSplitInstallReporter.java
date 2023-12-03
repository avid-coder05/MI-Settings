package com.android.settings.bluetooth.plugin.reporter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitInstallReporter;
import com.iqiyi.android.qigsaw.core.splitreport.SplitBriefInfo;
import com.iqiyi.android.qigsaw.core.splitreport.SplitInstallError;
import java.util.List;

/* loaded from: classes.dex */
public class SampleSplitInstallReporter extends DefaultSplitInstallReporter {
    public SampleSplitInstallReporter(Context context) {
        super(context);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitInstallReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter
    public void onDeferredInstallFailed(List<SplitBriefInfo> list, List<SplitInstallError> list2, long j) {
        super.onDeferredInstallFailed(list, list2, j);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitInstallReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter
    public void onDeferredInstallOK(List<SplitBriefInfo> list, long j) {
        super.onDeferredInstallOK(list, j);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitInstallReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter
    public void onStartInstallFailed(List<SplitBriefInfo> list, SplitInstallError splitInstallError, long j) {
        super.onStartInstallFailed(list, splitInstallError, j);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitInstallReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter
    @SuppressLint({"LongLogTag"})
    public void onStartInstallOK(List<SplitBriefInfo> list, long j) {
        super.onStartInstallOK(list, j);
        for (SplitBriefInfo splitBriefInfo : list) {
            if (splitBriefInfo.getInstallFlag() == 2) {
                Log.d("SampleSplitInstallReporter", String.format("Split %s has been installed, don't need delivery this result", splitBriefInfo.splitName));
            } else if (splitBriefInfo.getInstallFlag() == 1) {
                Log.d("SampleSplitInstallReporter", String.format("Split %s is installed firstly, you can delivery this result", splitBriefInfo.splitName));
            }
        }
    }
}
