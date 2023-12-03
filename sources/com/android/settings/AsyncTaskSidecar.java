package com.android.settings;

import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.Future;

/* loaded from: classes.dex */
public abstract class AsyncTaskSidecar<Param, Result> extends SidecarFragment {
    private Future<Result> mAsyncTask;

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$run$1(Object obj) {
        final Result doInBackground = doInBackground(obj);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.AsyncTaskSidecar$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AsyncTaskSidecar.this.lambda$run$0(doInBackground);
            }
        });
    }

    protected abstract Result doInBackground(Param param);

    @Override // com.android.settings.SidecarFragment, android.app.Fragment
    public void onDestroy() {
        Future<Result> future = this.mAsyncTask;
        if (future != null) {
            future.cancel(true);
        }
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: onPostExecute  reason: merged with bridge method [inline-methods] */
    public void lambda$run$0(Result result) {
    }

    public void run(final Param param) {
        setState(1, 0);
        Future<Result> future = this.mAsyncTask;
        if (future != null) {
            future.cancel(true);
        }
        this.mAsyncTask = ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.AsyncTaskSidecar$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AsyncTaskSidecar.this.lambda$run$1(param);
            }
        });
    }
}
