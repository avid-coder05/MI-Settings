package com.android.settings.device;

import android.app.AppGlobals;
import android.os.AsyncTask;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class MemoryInfoHelper {

    /* loaded from: classes.dex */
    public interface Callback {
        void handleTaskResult(long j);
    }

    /* loaded from: classes.dex */
    private static class ReadMemoryInfoTask extends AsyncTask<Void, Void, Void> {
        private long mAvailableMemorySize;
        private WeakReference<Callback> mOuterRef;

        public ReadMemoryInfoTask(Callback callback) {
            this.mOuterRef = new WeakReference<>(callback);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            this.mAvailableMemorySize = MiuiAboutPhoneUtils.getInstance(AppGlobals.getInitialApplication()).getAvailableMemorySize();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r3) {
            Callback callback = this.mOuterRef.get();
            if (callback != null) {
                callback.handleTaskResult(this.mAvailableMemorySize);
            }
        }
    }

    public static void getAvailableMemorySize(Callback callback) {
        new ReadMemoryInfoTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}
