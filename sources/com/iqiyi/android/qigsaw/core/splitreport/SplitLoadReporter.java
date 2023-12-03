package com.iqiyi.android.qigsaw.core.splitreport;

import java.util.List;

/* loaded from: classes2.dex */
public interface SplitLoadReporter {
    void onLoadFailed(String str, List<SplitBriefInfo> list, List<SplitLoadError> list2, long j);

    void onLoadOK(String str, List<SplitBriefInfo> list, long j);
}
