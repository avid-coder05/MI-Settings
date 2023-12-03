package com.android.settings.wifi.slice;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class WifiScanWorker extends SliceBackgroundWorker<WifiSliceItem> implements WifiPickerTracker.WifiPickerTrackerCallback, LifecycleOwner, WifiEntry.WifiEntryCallback {
    final LifecycleRegistry mLifecycleRegistry;
    protected WifiPickerTracker mWifiPickerTracker;
    protected WifiPickerTrackerHelper mWifiPickerTrackerHelper;

    public WifiScanWorker(Context context, Uri uri) {
        super(context, uri);
        LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
        this.mLifecycleRegistry = lifecycleRegistry;
        WifiPickerTrackerHelper wifiPickerTrackerHelper = new WifiPickerTrackerHelper(lifecycleRegistry, context, this);
        this.mWifiPickerTrackerHelper = wifiPickerTrackerHelper;
        this.mWifiPickerTracker = wifiPickerTrackerHelper.getWifiPickerTracker();
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    public void connectCarrierNetwork() {
        this.mWifiPickerTrackerHelper.connectCarrierNetwork(null);
    }

    protected int getApRowCount() {
        return 3;
    }

    @Override // androidx.lifecycle.LifecycleOwner
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }

    public WifiEntry getWifiEntry(String str) {
        WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
        if (connectedWifiEntry == null || !TextUtils.equals(str, connectedWifiEntry.getKey())) {
            for (WifiEntry wifiEntry : this.mWifiPickerTracker.getWifiEntries()) {
                if (TextUtils.equals(str, wifiEntry.getKey())) {
                    return wifiEntry;
                }
            }
            return null;
        }
        return connectedWifiEntry;
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.slices.SliceBackgroundWorker
    public void onSlicePinned() {
        this.mLifecycleRegistry.markState(Lifecycle.State.STARTED);
        this.mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
        updateResults();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.slices.SliceBackgroundWorker
    public void onSliceUnpinned() {
        this.mLifecycleRegistry.markState(Lifecycle.State.STARTED);
        this.mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    }

    @Override // com.android.wifitrackerlib.WifiEntry.WifiEntryCallback
    public void onUpdated() {
        updateResults();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        updateResults();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
        notifySliceChange();
    }

    public void setCarrierNetworkEnabledIfNeeded(boolean z, int i) {
        if (this.mWifiPickerTrackerHelper.isCarrierNetworkProvisionEnabled(i)) {
            return;
        }
        this.mWifiPickerTrackerHelper.setCarrierNetworkEnabled(z);
    }

    void updateResults() {
        if (this.mWifiPickerTracker.getWifiState() != 3 || this.mLifecycleRegistry.getCurrentState() != Lifecycle.State.RESUMED) {
            super.updateResults(null);
            return;
        }
        ArrayList arrayList = new ArrayList();
        WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
        if (connectedWifiEntry != null) {
            connectedWifiEntry.setListener(this);
            arrayList.add(new WifiSliceItem(getContext(), connectedWifiEntry));
        }
        for (WifiEntry wifiEntry : this.mWifiPickerTracker.getWifiEntries()) {
            if (arrayList.size() >= getApRowCount()) {
                break;
            } else if (wifiEntry.getLevel() != -1) {
                wifiEntry.setListener(this);
                arrayList.add(new WifiSliceItem(getContext(), wifiEntry));
            }
        }
        super.updateResults(arrayList);
    }
}
