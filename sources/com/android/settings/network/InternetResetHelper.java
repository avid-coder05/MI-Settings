package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class InternetResetHelper implements LifecycleObserver, ConnectivitySubsystemsRecoveryManager.RecoveryStatusCallback {
    protected ConnectivitySubsystemsRecoveryManager mConnectivitySubsystemsRecoveryManager;
    protected final Context mContext;
    protected HandlerInjector mHandlerInjector;
    protected boolean mIsRecoveryReady;
    protected boolean mIsWifiReady;
    protected NetworkMobileProviderController mMobileNetworkController;
    protected Preference mResettingPreference;
    protected final WifiManager mWifiManager;
    protected Preference mWifiTogglePreferences;
    protected HandlerThread mWorkerThread;
    protected List<PreferenceGroup> mWifiNetworkPreferences = new ArrayList();
    protected final BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.InternetResetHelper.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !TextUtils.equals(intent.getAction(), "android.net.wifi.STATE_CHANGE")) {
                return;
            }
            InternetResetHelper.this.updateWifiStateChange();
        }
    };
    protected final Runnable mResumeRunnable = new Runnable() { // from class: com.android.settings.network.InternetResetHelper$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            InternetResetHelper.this.lambda$new$0();
        }
    };
    protected final Runnable mTimeoutRunnable = new Runnable() { // from class: com.android.settings.network.InternetResetHelper$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            InternetResetHelper.this.lambda$new$1();
        }
    };
    protected final IntentFilter mWifiStateFilter = new IntentFilter("android.net.wifi.STATE_CHANGE");

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class HandlerInjector {
        protected final Handler mHandler;

        HandlerInjector(Context context) {
            this.mHandler = context.getMainThreadHandler();
        }

        public void postDelayed(Runnable runnable, long j) {
            this.mHandler.postDelayed(runnable, j);
        }

        public void removeCallbacks(Runnable runnable) {
            this.mHandler.removeCallbacks(runnable);
        }
    }

    public InternetResetHelper(Context context, Lifecycle lifecycle) {
        this.mContext = context;
        this.mHandlerInjector = new HandlerInjector(context);
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        HandlerThread handlerThread = new HandlerThread("InternetResetHelper{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mConnectivitySubsystemsRecoveryManager = new ConnectivitySubsystemsRecoveryManager(context, this.mWorkerThread.getThreadHandler());
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.mIsRecoveryReady = true;
        this.mIsWifiReady = true;
        lambda$new$0();
    }

    public void addWifiNetworkPreference(PreferenceGroup preferenceGroup) {
        if (preferenceGroup != null) {
            this.mWifiNetworkPreferences.add(preferenceGroup);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        this.mHandlerInjector.removeCallbacks(this.mResumeRunnable);
        this.mHandlerInjector.removeCallbacks(this.mTimeoutRunnable);
        this.mWorkerThread.quit();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mContext.unregisterReceiver(this.mWifiStateReceiver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mContext.registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
    }

    @Override // com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager.RecoveryStatusCallback
    public void onSubsystemRestartOperationBegin() {
        Log.d("InternetResetHelper", "The connectivity subsystem is starting for recovery.");
    }

    @Override // com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager.RecoveryStatusCallback
    public void onSubsystemRestartOperationEnd() {
        Log.d("InternetResetHelper", "The connectivity subsystem is done for recovery.");
        if (this.mIsRecoveryReady) {
            return;
        }
        this.mIsRecoveryReady = true;
        this.mHandlerInjector.postDelayed(this.mResumeRunnable, 0L);
    }

    public void restart() {
        if (!this.mConnectivitySubsystemsRecoveryManager.isRecoveryAvailable()) {
            Log.e("InternetResetHelper", "The connectivity subsystem is not available to restart.");
            return;
        }
        Log.d("InternetResetHelper", "The connectivity subsystem is restarting for recovery.");
        suspendPreferences();
        this.mIsRecoveryReady = false;
        this.mIsWifiReady = !this.mWifiManager.isWifiEnabled();
        this.mHandlerInjector.postDelayed(this.mTimeoutRunnable, 15000L);
        this.mConnectivitySubsystemsRecoveryManager.triggerSubsystemRestart(null, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: resumePreferences  reason: merged with bridge method [inline-methods] */
    public void lambda$new$0() {
        if (this.mIsRecoveryReady && this.mMobileNetworkController != null) {
            Log.d("InternetResetHelper", "Resume the Mobile Network controller");
            this.mMobileNetworkController.hidePreference(false, false);
        }
        if (this.mIsWifiReady && this.mWifiTogglePreferences != null) {
            Log.d("InternetResetHelper", "Resume the Wi-Fi preferences");
            this.mWifiTogglePreferences.setVisible(true);
            Iterator<PreferenceGroup> it = this.mWifiNetworkPreferences.iterator();
            while (it.hasNext()) {
                it.next().setVisible(true);
            }
        }
        if (this.mIsRecoveryReady && this.mIsWifiReady) {
            this.mHandlerInjector.removeCallbacks(this.mTimeoutRunnable);
            if (this.mResettingPreference != null) {
                Log.d("InternetResetHelper", "Resume the Resetting preference");
                this.mResettingPreference.setVisible(false);
            }
        }
    }

    public void setMobileNetworkController(NetworkMobileProviderController networkMobileProviderController) {
        this.mMobileNetworkController = networkMobileProviderController;
    }

    public void setResettingPreference(Preference preference) {
        this.mResettingPreference = preference;
    }

    public void setWifiTogglePreference(Preference preference) {
        this.mWifiTogglePreferences = preference;
    }

    protected void suspendPreferences() {
        Log.d("InternetResetHelper", "Suspend the subsystem preferences");
        NetworkMobileProviderController networkMobileProviderController = this.mMobileNetworkController;
        if (networkMobileProviderController != null) {
            networkMobileProviderController.hidePreference(true, true);
        }
        Preference preference = this.mWifiTogglePreferences;
        if (preference != null) {
            preference.setVisible(false);
        }
        for (PreferenceGroup preferenceGroup : this.mWifiNetworkPreferences) {
            preferenceGroup.removeAll();
            preferenceGroup.setVisible(false);
        }
        Preference preference2 = this.mResettingPreference;
        if (preference2 != null) {
            preference2.setVisible(true);
        }
    }

    protected void updateWifiStateChange() {
        if (this.mIsWifiReady || !this.mWifiManager.isWifiEnabled()) {
            return;
        }
        Log.d("InternetResetHelper", "The Wi-Fi subsystem is done for recovery.");
        this.mIsWifiReady = true;
        this.mHandlerInjector.postDelayed(this.mResumeRunnable, 0L);
    }
}
