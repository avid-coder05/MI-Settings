package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitload.SplitLoadHandler;
import com.iqiyi.android.qigsaw.core.splitload.listener.OnSplitLoadListener;
import com.iqiyi.android.qigsaw.core.splitreport.SplitBriefInfo;
import com.iqiyi.android.qigsaw.core.splitreport.SplitLoadError;
import com.iqiyi.android.qigsaw.core.splitreport.SplitLoadReporter;
import java.util.List;

/* loaded from: classes2.dex */
abstract class SplitLoadTask implements SplitLoaderWrapper, Runnable, SplitLoadHandler.OnSplitLoadFinishListener {
    private static final String TAG = "SplitLoadTask";
    private final SplitLoadHandler loadHandler;
    private final OnSplitLoadListener loadListener;
    private SplitLoader splitLoader;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoadTask(SplitLoadManager splitLoadManager, List<Intent> list, OnSplitLoadListener onSplitLoadListener) {
        this.loadHandler = new SplitLoadHandler(this, splitLoadManager, list);
        this.loadListener = onSplitLoadListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Context getContext() {
        return this.loadHandler.getContext();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoader getSplitLoader() {
        if (this.splitLoader == null) {
            this.splitLoader = createSplitLoader();
        }
        return this.splitLoader;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoaderWrapper
    public void loadResources(String str) throws SplitLoadException {
        getSplitLoader().loadResources(str);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoadHandler.OnSplitLoadFinishListener
    public void onLoadFinish(List<SplitBriefInfo> list, List<SplitLoadError> list2, String str, long j) {
        SplitLoadReporter loadReporter = SplitLoadReporterManager.getLoadReporter();
        if (list2.isEmpty()) {
            OnSplitLoadListener onSplitLoadListener = this.loadListener;
            if (onSplitLoadListener != null) {
                onSplitLoadListener.onCompleted();
            }
            if (loadReporter != null) {
                loadReporter.onLoadOK(str, list, j);
                return;
            }
            return;
        }
        if (this.loadListener != null) {
            this.loadListener.onFailed(list2.get(list2.size() - 1).errorCode);
        }
        if (loadReporter != null) {
            loadReporter.onLoadFailed(str, list, list2, j);
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            this.loadHandler.loadSplitsSync(this);
            return;
        }
        synchronized (this) {
            this.loadHandler.getMainHandler().post(new Runnable() { // from class: com.iqiyi.android.qigsaw.core.splitload.SplitLoadTask.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (SplitLoadTask.this) {
                        SplitLoadTask.this.loadHandler.loadSplitsSync(SplitLoadTask.this);
                        SplitLoadTask.this.notifyAll();
                    }
                }
            });
            try {
                wait();
            } catch (InterruptedException e) {
                SplitLog.w(TAG, "Failed to block thread " + Thread.currentThread().getName(), e);
                if (this.loadListener != null) {
                    this.loadListener.onFailed(-99);
                }
            }
        }
    }
}
