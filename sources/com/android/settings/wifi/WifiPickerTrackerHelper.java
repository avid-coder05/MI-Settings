package com.android.settings.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.telephony.CarrierConfigManager;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.overlay.FeatureFactory;
import com.android.wifitrackerlib.MergedCarrierEntry;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.Clock;
import java.time.ZoneOffset;

/* loaded from: classes2.dex */
public class WifiPickerTrackerHelper implements LifecycleObserver {
    private static final Clock ELAPSED_REALTIME_CLOCK = new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.WifiPickerTrackerHelper.1
        public long millis() {
            return SystemClock.elapsedRealtime();
        }
    };
    protected final CarrierConfigManager mCarrierConfigManager;
    protected final WifiManager mWifiManager;
    protected WifiPickerTracker mWifiPickerTracker;
    protected HandlerThread mWorkerThread;

    public WifiPickerTrackerHelper(Lifecycle lifecycle, Context context, WifiPickerTracker.WifiPickerTrackerCallback wifiPickerTrackerCallback) {
        if (lifecycle == null) {
            throw new IllegalArgumentException("lifecycle must be non-null.");
        }
        lifecycle.addObserver(this);
        HandlerThread handlerThread = new HandlerThread("WifiPickerTrackerHelper{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWifiPickerTracker = FeatureFactory.getFactory(context).getWifiTrackerLibProvider().createWifiPickerTracker(lifecycle, context, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), ELAPSED_REALTIME_CLOCK, 15000L, 10000L, wifiPickerTrackerCallback);
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    public boolean connectCarrierNetwork(WifiEntry.ConnectCallback connectCallback) {
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        if (mergedCarrierEntry == null || !mergedCarrierEntry.canConnect()) {
            return false;
        }
        mergedCarrierEntry.connect(connectCallback);
        return true;
    }

    public String getCarrierNetworkSsid() {
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        if (mergedCarrierEntry == null) {
            return null;
        }
        return mergedCarrierEntry.getSsid();
    }

    public WifiPickerTracker getWifiPickerTracker() {
        return this.mWifiPickerTracker;
    }

    public boolean isCarrierNetworkActive() {
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        return mergedCarrierEntry != null && mergedCarrierEntry.isDefaultNetwork();
    }

    public boolean isCarrierNetworkEnabled(int i) {
        return this.mWifiManager.isCarrierNetworkOffloadEnabled(i, true);
    }

    public boolean isCarrierNetworkProvisionEnabled(int i) {
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        if (configForSubId == null) {
            Log.e("WifiPickerTrackerHelper", "Could not get carrier config, subId:" + i);
            return false;
        }
        boolean z = configForSubId.getBoolean("carrier_provisions_wifi_merged_networks_bool");
        Log.i("WifiPickerTrackerHelper", "isCarrierNetworkProvisionEnabled:" + z);
        return z;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        this.mWorkerThread.quit();
    }

    public void setCarrierNetworkEnabled(boolean z) {
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        if (mergedCarrierEntry == null) {
            return;
        }
        mergedCarrierEntry.setEnabled(z);
    }

    @VisibleForTesting
    void setWifiPickerTracker(WifiPickerTracker wifiPickerTracker) {
        this.mWifiPickerTracker = wifiPickerTracker;
    }

    @VisibleForTesting
    void setWorkerThread(HandlerThread handlerThread) {
        this.mWorkerThread = handlerThread;
    }
}
