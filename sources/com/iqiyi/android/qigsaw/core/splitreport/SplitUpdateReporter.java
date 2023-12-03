package com.iqiyi.android.qigsaw.core.splitreport;

import java.util.List;

/* loaded from: classes2.dex */
public interface SplitUpdateReporter {
    void onNewSplitInfoVersionLoaded(String str);

    void onUpdateFailed(String str, String str2, int i);

    void onUpdateOK(String str, String str2, List<String> list);
}
