package com.iqiyi.android.qigsaw.core.splitreport;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import java.util.List;

/* loaded from: classes2.dex */
public class DefaultSplitUninstallReporter implements SplitUninstallReporter {
    private static final String TAG = "SplitUninstallReporter";
    protected final Context context;

    public DefaultSplitUninstallReporter(Context context) {
        this.context = context;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitUninstallReporter
    public void onSplitUninstallOK(List<String> list, long j) {
        SplitLog.i(TAG, "Succeed to uninstall %s, cost time %d ms.", list.toString(), Long.valueOf(j));
    }
}
