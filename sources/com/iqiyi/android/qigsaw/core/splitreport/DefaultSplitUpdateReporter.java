package com.iqiyi.android.qigsaw.core.splitreport;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import java.util.List;

/* loaded from: classes2.dex */
public class DefaultSplitUpdateReporter implements SplitUpdateReporter {
    private static final String TAG = "SplitUpdateReporter";
    protected final Context context;

    public DefaultSplitUpdateReporter(Context context) {
        this.context = context;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter
    public void onNewSplitInfoVersionLoaded(String str) {
        SplitLog.i(TAG, "Success to load new split info version ", str);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter
    public void onUpdateFailed(String str, String str2, int i) {
        SplitLog.i(TAG, "Failed to update version from %s to %s, errorCode %d.", str, str2, Integer.valueOf(i));
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter
    public void onUpdateOK(String str, String str2, List<String> list) {
        SplitLog.i(TAG, "Success to update version from %s to %s, update splits: %s.", str, str2, list.toString());
    }
}
