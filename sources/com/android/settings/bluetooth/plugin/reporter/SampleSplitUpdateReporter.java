package com.android.settings.bluetooth.plugin.reporter;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitUpdateReporter;
import java.util.List;

/* loaded from: classes.dex */
public class SampleSplitUpdateReporter extends DefaultSplitUpdateReporter {
    public SampleSplitUpdateReporter(Context context) {
        super(context);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitUpdateReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter
    public void onNewSplitInfoVersionLoaded(String str) {
        super.onNewSplitInfoVersionLoaded(str);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitUpdateReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter
    public void onUpdateFailed(String str, String str2, int i) {
        super.onUpdateFailed(str, str2, i);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitUpdateReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter
    public void onUpdateOK(String str, String str2, List<String> list) {
        super.onUpdateOK(str, str2, list);
    }
}
