package com.android.settingslib.deviceinfo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.lang.ref.WeakReference;

/* loaded from: classes2.dex */
public abstract class AbstractUptimePreferenceController extends AbstractPreferenceController implements LifecycleObserver, OnStart, OnStop {
    static final String KEY_UPTIME = "up_time";
    private Handler mHandler;
    private Preference mUptime;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MyHandler extends Handler {
        private WeakReference<AbstractUptimePreferenceController> mStatus;

        public MyHandler(AbstractUptimePreferenceController abstractUptimePreferenceController) {
            this.mStatus = new WeakReference<>(abstractUptimePreferenceController);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AbstractUptimePreferenceController abstractUptimePreferenceController = this.mStatus.get();
            if (abstractUptimePreferenceController == null) {
                return;
            }
            if (message.what == 500) {
                abstractUptimePreferenceController.updateTimes();
                sendEmptyMessageDelayed(500, 1000L);
                return;
            }
            throw new IllegalStateException("Unknown message " + message.what);
        }
    }

    public AbstractUptimePreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    private Handler getHandler() {
        if (this.mHandler == null) {
            this.mHandler = new MyHandler(this);
        }
        return this.mHandler;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTimes() {
        this.mUptime.setSummary(DateUtils.formatElapsedTime(SystemClock.elapsedRealtime() / 1000));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mUptime = preferenceScreen.findPreference(KEY_UPTIME);
        updateTimes();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_UPTIME;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        getHandler().sendEmptyMessage(500);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        getHandler().removeMessages(500);
    }
}
