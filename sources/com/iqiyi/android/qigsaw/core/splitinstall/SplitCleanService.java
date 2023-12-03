package com.iqiyi.android.qigsaw.core.splitinstall;

import android.app.IntentService;
import android.content.Intent;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;

/* loaded from: classes2.dex */
public class SplitCleanService extends IntentService {
    public SplitCleanService() {
        super("qigsaw_split_clean");
    }

    private void doClean() {
        SplitPathManager.require().clearCache();
        SplitPathManager.require().cleanMiCache(this);
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        try {
            doClean();
        } catch (Exception unused) {
        }
    }
}
