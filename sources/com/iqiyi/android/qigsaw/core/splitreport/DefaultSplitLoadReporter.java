package com.iqiyi.android.qigsaw.core.splitreport;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import java.util.List;

/* loaded from: classes2.dex */
public class DefaultSplitLoadReporter implements SplitLoadReporter {
    private static final String TAG = "SplitLoadReporter";
    protected final Context context;

    public DefaultSplitLoadReporter(Context context) {
        this.context = context;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitLoadReporter
    public void onLoadFailed(String str, List<SplitBriefInfo> list, List<SplitLoadError> list2, long j) {
        for (SplitLoadError splitLoadError : list2) {
            SplitLog.printErrStackTrace(TAG, splitLoadError.cause, "Failed to load split %s in process %s cost %d ms, error code: %d!", splitLoadError.splitName, str, Long.valueOf(j), Integer.valueOf(splitLoadError.errorCode));
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitLoadReporter
    public void onLoadOK(String str, List<SplitBriefInfo> list, long j) {
        SplitLog.i(TAG, "Success to load %s in process %s cost %d ms!", list, str, Long.valueOf(j));
    }
}
