package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class AppStateBaseBridge implements ApplicationsState.Callbacks {
    protected final ApplicationsState.Session mAppSession;
    protected final ApplicationsState mAppState;
    protected final Callback mCallback;
    private boolean mEntriesLoadCompleted = false;
    protected final BackgroundHandler mHandler;
    protected final MainHandler mMainHandler;

    /* loaded from: classes.dex */
    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                AppStateBaseBridge.this.loadAllExtraInfo();
                AppStateBaseBridge.this.mMainHandler.sendEmptyMessage(1);
            } else if (i == 2) {
                ArrayList<ApplicationsState.AppEntry> allApps = AppStateBaseBridge.this.mAppSession.getAllApps();
                int size = allApps.size();
                String str = (String) message.obj;
                int i2 = message.arg1;
                for (int i3 = 0; i3 < size; i3++) {
                    ApplicationsState.AppEntry appEntry = allApps.get(i3);
                    ApplicationInfo applicationInfo = appEntry.info;
                    if (applicationInfo.uid == i2 && str.equals(applicationInfo.packageName)) {
                        AppStateBaseBridge.this.updateExtraInfo(appEntry, str, i2);
                    }
                }
                AppStateBaseBridge.this.mMainHandler.sendEmptyMessage(1);
            }
        }
    }

    /* loaded from: classes.dex */
    public interface Callback {
        void onExtraInfoUpdated();
    }

    /* loaded from: classes.dex */
    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            AppStateBaseBridge.this.mCallback.onExtraInfoUpdated();
        }
    }

    public AppStateBaseBridge(ApplicationsState applicationsState, Callback callback) {
        this.mAppState = applicationsState;
        this.mAppSession = applicationsState != null ? applicationsState.newSession(this) : null;
        this.mCallback = callback;
        this.mHandler = new BackgroundHandler(applicationsState != null ? applicationsState.getBackgroundLooper() : Looper.getMainLooper());
        this.mMainHandler = new MainHandler(Looper.getMainLooper());
    }

    public void forceUpdate(String str, int i) {
        this.mHandler.obtainMessage(2, i, 0, str).sendToTarget();
    }

    protected abstract void loadAllExtraInfo();

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
        this.mEntriesLoadCompleted = true;
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    public void pause() {
        this.mAppSession.onPause();
    }

    public void release() {
        this.mHandler.removeCallbacksAndMessages(null);
        this.mMainHandler.removeCallbacksAndMessages(null);
        this.mAppSession.onDestroy();
    }

    public void resume() {
        this.mHandler.sendEmptyMessageDelayed(1, 500L);
        this.mAppSession.onResume();
    }

    protected abstract void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i);
}
