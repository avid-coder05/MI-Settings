package com.android.settings.bluetooth.plugin.reporter;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitLoadReporter;
import com.iqiyi.android.qigsaw.core.splitreport.SplitBriefInfo;
import com.iqiyi.android.qigsaw.core.splitreport.SplitLoadError;
import java.util.List;

/* loaded from: classes.dex */
public class SampleSplitLoadReporter extends DefaultSplitLoadReporter {
    public SampleSplitLoadReporter(Context context) {
        super(context);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitLoadReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitLoadReporter
    public void onLoadFailed(String str, List<SplitBriefInfo> list, List<SplitLoadError> list2, long j) {
        super.onLoadFailed(str, list, list2, j);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitLoadReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitLoadReporter
    public void onLoadOK(String str, List<SplitBriefInfo> list, long j) {
        super.onLoadOK(str, list, j);
    }
}
