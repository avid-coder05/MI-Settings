package com.iqiyi.android.qigsaw.core.splitreport;

import java.util.List;

/* loaded from: classes2.dex */
public interface SplitInstallReporter {
    void onDeferredInstallFailed(List<SplitBriefInfo> list, List<SplitInstallError> list2, long j);

    void onDeferredInstallOK(List<SplitBriefInfo> list, long j);

    void onStartInstallFailed(List<SplitBriefInfo> list, SplitInstallError splitInstallError, long j);

    void onStartInstallOK(List<SplitBriefInfo> list, long j);
}
