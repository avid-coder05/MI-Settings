package com.android.settings.search.appseparate;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.android.settings.SettingsFragment;
import com.android.settingslib.search.KeywordsCloudConfigHelper;
import java.lang.ref.WeakReference;

/* loaded from: classes2.dex */
public class SeparateAppSearchThread extends HandlerThread {
    private static final int MSG_INIT = 0;
    private static final int MSG_RELEASE = 1;
    private Handler mHandler;

    /* loaded from: classes2.dex */
    private static class SeparateAppSearchHandler extends Handler {
        private WeakReference<SettingsFragment> mFragmentRef;
        private boolean mIsSeparateAppSearchInited;

        private SeparateAppSearchHandler(Looper looper, SettingsFragment settingsFragment) {
            super(looper);
            this.mIsSeparateAppSearchInited = false;
            this.mFragmentRef = new WeakReference<>(settingsFragment);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Context applicationContext;
            SettingsFragment settingsFragment = this.mFragmentRef.get();
            if (settingsFragment == null) {
                return;
            }
            int i = message.what;
            if (i != 0) {
                if (i == 1 && settingsFragment.getActivity() != null && this.mIsSeparateAppSearchInited) {
                    SeparateAppSearchHelper.releaseInstance();
                    KeywordsCloudConfigHelper.releaseInstance();
                    this.mIsSeparateAppSearchInited = false;
                }
            } else if (settingsFragment.getActivity() == null || this.mIsSeparateAppSearchInited || (applicationContext = settingsFragment.getActivity().getApplicationContext()) == null) {
            } else {
                SeparateAppSearchHelper.getInstance(applicationContext);
                this.mIsSeparateAppSearchInited = true;
                KeywordsCloudConfigHelper.getInstance(applicationContext);
            }
        }
    }

    public SeparateAppSearchThread(String str, SettingsFragment settingsFragment) {
        super(str);
        start();
        this.mHandler = new SeparateAppSearchHandler(getLooper(), settingsFragment);
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void sendInitMessage() {
        this.mHandler.obtainMessage(0).sendToTarget();
    }

    public void sendReleaseMessage() {
        this.mHandler.obtainMessage(1).sendToTarget();
    }
}
