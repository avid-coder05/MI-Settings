package com.android.settings.bluetooth.plugin.reporter;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitUninstallReporter;
import java.util.List;

/* loaded from: classes.dex */
public class SampleSplitUninstallReporter extends DefaultSplitUninstallReporter {
    public SampleSplitUninstallReporter(Context context) {
        super(context);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitUninstallReporter, com.iqiyi.android.qigsaw.core.splitreport.SplitUninstallReporter
    public void onSplitUninstallOK(List<String> list, long j) {
        super.onSplitUninstallOK(list, j);
    }
}
