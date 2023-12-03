package com.android.wifitrackerlib;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkScoreManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.PasspointR1Provider;
import android.os.Handler;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda10;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.StandardWifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* loaded from: classes2.dex */
public class WifiPickerTracker extends BaseWifiTracker {
    private WifiEntry mConnectedWifiEntry;
    private NetworkInfo mCurrentNetworkInfo;
    private final WifiPickerTrackerCallback mListener;
    private final Object mLock;
    private MergedCarrierEntry mMergedCarrierEntry;
    private final ArrayMap<StandardWifiEntry.StandardWifiEntryKey, List<WifiConfiguration>> mNetworkRequestConfigCache;
    private NetworkRequestEntry mNetworkRequestEntry;
    private int mNumSavedNetworks;
    private final Map<String, OsuWifiEntry> mOsuWifiEntryCache;
    private final Map<String, PasspointConfiguration> mPasspointConfigCache;
    private boolean mPasspointR1Supported;
    private final Map<String, PasspointR1WifiEntry> mPasspointR1WifiEntryCache;
    private final SparseArray<WifiConfiguration> mPasspointWifiConfigCache;
    private final Map<String, PasspointWifiEntry> mPasspointWifiEntryCache;
    private WifiEntry mSlaveConnectedWifiEntry;
    private NetworkInfo mSlaveCurrentNetworkInfo;
    private final Map<StandardWifiEntry.StandardWifiEntryKey, List<WifiConfiguration>> mStandardWifiConfigCache;
    private final List<StandardWifiEntry> mStandardWifiEntryCache;
    private final Map<StandardWifiEntry.StandardWifiEntryKey, List<WifiConfiguration>> mSuggestedConfigCache;
    private final List<StandardWifiEntry> mSuggestedWifiEntryCache;
    private final List<WifiEntry> mWifiEntries;

    /* loaded from: classes2.dex */
    public interface WifiPickerTrackerCallback extends BaseWifiTracker.BaseWifiTrackerCallback {
        void onNumSavedNetworksChanged();

        void onNumSavedSubscriptionsChanged();

        void onWifiEntriesChanged();
    }

    public WifiPickerTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, WifiPickerTrackerCallback wifiPickerTrackerCallback) {
        super(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, wifiPickerTrackerCallback, "WifiPickerTracker");
        this.mLock = new Object();
        this.mWifiEntries = new ArrayList();
        this.mStandardWifiConfigCache = new ArrayMap();
        this.mSuggestedConfigCache = new ArrayMap();
        this.mNetworkRequestConfigCache = new ArrayMap<>();
        this.mStandardWifiEntryCache = new ArrayList();
        this.mSuggestedWifiEntryCache = new ArrayList();
        this.mPasspointConfigCache = new ArrayMap();
        this.mPasspointWifiConfigCache = new SparseArray<>();
        this.mPasspointWifiEntryCache = new ArrayMap();
        this.mOsuWifiEntryCache = new ArrayMap();
        this.mPasspointR1WifiEntryCache = new ArrayMap();
        this.mListener = wifiPickerTrackerCallback;
        WifiPasspointProvision.getInstance(this.mContext).bindPasspointKeyService();
        this.mPasspointR1Supported = WifiPasspointProvision.isPasspointR1Supported();
    }

    private void conditionallyCreateConnectedPasspointWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        WifiConfiguration wifiConfiguration;
        if (wifiInfo == null || !wifiInfo.isPasspointAp() || (wifiConfiguration = this.mPasspointWifiConfigCache.get(wifiInfo.getNetworkId())) == null) {
            return;
        }
        if (this.mPasspointWifiEntryCache.containsKey(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()))) {
            return;
        }
        PasspointConfiguration passpointConfiguration = this.mPasspointConfigCache.get(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()));
        PasspointWifiEntry passpointWifiEntry = passpointConfiguration != null ? new PasspointWifiEntry(this.mContext, this.mMainHandler, passpointConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false) : new PasspointWifiEntry(this.mContext, this.mMainHandler, wifiConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false);
        passpointWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
        this.mPasspointWifiEntryCache.put(passpointWifiEntry.getKey(), passpointWifiEntry);
    }

    private void conditionallyCreateConnectedStandardWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        conditionallyCreateConnectedStandardWifiEntry(wifiInfo, networkInfo, false);
    }

    private void conditionallyCreateConnectedStandardWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo, boolean z) {
        if (wifiInfo == null || wifiInfo.isPasspointAp() || wifiInfo.isOsuAp()) {
            return;
        }
        final int networkId = wifiInfo.getNetworkId();
        for (List<WifiConfiguration> list : this.mStandardWifiConfigCache.values()) {
            if (list.stream().map(new Function() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda9
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Integer lambda$conditionallyCreateConnectedStandardWifiEntry$29;
                    lambda$conditionallyCreateConnectedStandardWifiEntry$29 = WifiPickerTracker.lambda$conditionallyCreateConnectedStandardWifiEntry$29((WifiConfiguration) obj);
                    return lambda$conditionallyCreateConnectedStandardWifiEntry$29;
                }
            }).filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda13
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$conditionallyCreateConnectedStandardWifiEntry$30;
                    lambda$conditionallyCreateConnectedStandardWifiEntry$30 = WifiPickerTracker.lambda$conditionallyCreateConnectedStandardWifiEntry$30(networkId, (Integer) obj);
                    return lambda$conditionallyCreateConnectedStandardWifiEntry$30;
                }
            }).count() != 0) {
                StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey(list.get(0), true);
                Iterator<StandardWifiEntry> it = this.mStandardWifiEntryCache.iterator();
                while (it.hasNext()) {
                    if (standardWifiEntryKey.equals(it.next().getStandardWifiEntryKey())) {
                        return;
                    }
                }
                StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, list, null, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                if (z) {
                    standardWifiEntry.updateSlaveConnectionInfo(wifiInfo, networkInfo);
                } else {
                    standardWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
                }
                this.mStandardWifiEntryCache.add(standardWifiEntry);
                return;
            }
        }
    }

    private void conditionallyCreateConnectedSuggestedWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo == null || wifiInfo.isPasspointAp() || wifiInfo.isOsuAp()) {
            return;
        }
        int networkId = wifiInfo.getNetworkId();
        for (List<WifiConfiguration> list : this.mSuggestedConfigCache.values()) {
            if (!list.isEmpty() && list.get(0).networkId == networkId) {
                StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey(list.get(0), true);
                Iterator<StandardWifiEntry> it = this.mSuggestedWifiEntryCache.iterator();
                while (it.hasNext()) {
                    if (standardWifiEntryKey.equals(it.next().getStandardWifiEntryKey())) {
                        return;
                    }
                }
                StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, list, null, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                standardWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
                this.mSuggestedWifiEntryCache.add(standardWifiEntry);
                return;
            }
        }
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            updateStandardWifiEntryScans(Collections.emptyList());
            updateSuggestedWifiEntryScans(Collections.emptyList());
            updatePasspointWifiEntryScans(Collections.emptyList());
            updateOsuWifiEntryScans(Collections.emptyList());
            updatePasspointR1WifiEntryScans(Collections.emptyList());
            updateNetworkRequestEntryScans(Collections.emptyList());
            updateContextualWifiEntryScans(Collections.emptyList());
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        } else {
            j += this.mScanIntervalMillis;
        }
        List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults(j);
        updateStandardWifiEntryScans(scanResults);
        updateSuggestedWifiEntryScans(scanResults);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
        updatePasspointR1WifiEntryScans(scanResults);
        updateNetworkRequestEntryScans(scanResults);
        updateContextualWifiEntryScans(scanResults);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Integer lambda$conditionallyCreateConnectedStandardWifiEntry$29(WifiConfiguration wifiConfiguration) {
        return Integer.valueOf(wifiConfiguration.networkId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$conditionallyCreateConnectedStandardWifiEntry$30(int i, Integer num) {
        return num.intValue() == i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateNetworkRequestEntryScans$22(StandardWifiEntry.ScanResultKey scanResultKey, ScanResult scanResult) {
        return scanResultKey.equals(new StandardWifiEntry.ScanResultKey(scanResult));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateOsuWifiEntryScans$18(Map map, OsuWifiEntry osuWifiEntry) {
        PasspointConfiguration passpointConfiguration = (PasspointConfiguration) map.get(osuWifiEntry.getOsuProvider());
        if (passpointConfiguration == null) {
            osuWifiEntry.setAlreadyProvisioned(false);
            return;
        }
        osuWifiEntry.setAlreadyProvisioned(true);
        PasspointWifiEntry passpointWifiEntry = this.mPasspointWifiEntryCache.get(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()));
        if (passpointWifiEntry == null) {
            return;
        }
        passpointWifiEntry.setOsuWifiEntry(osuWifiEntry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateOsuWifiEntryScans$19(Map.Entry entry) {
        return ((OsuWifiEntry) entry.getValue()).getLevel() == -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$updatePasspointConfigurations$27(PasspointConfiguration passpointConfiguration) {
        return PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updatePasspointConfigurations$28(Map.Entry entry) {
        PasspointWifiEntry passpointWifiEntry = (PasspointWifiEntry) entry.getValue();
        passpointWifiEntry.updatePasspointConfig(this.mPasspointConfigCache.get(passpointWifiEntry.getKey()));
        return (passpointWifiEntry.isSubscription() || passpointWifiEntry.isSuggestion()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePasspointR1WifiEntryScans$20(Map map, PasspointR1WifiEntry passpointR1WifiEntry) {
        PasspointConfiguration passpointConfiguration = (PasspointConfiguration) map.get(passpointR1WifiEntry.getPasspointR1Provider());
        if (passpointConfiguration == null) {
            passpointR1WifiEntry.setAlreadyProvisioned(false);
            return;
        }
        passpointR1WifiEntry.setAlreadyProvisioned(true);
        PasspointWifiEntry passpointWifiEntry = this.mPasspointWifiEntryCache.get(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()));
        if (passpointWifiEntry == null) {
            return;
        }
        passpointWifiEntry.setPasspointR1WifiEntry(passpointR1WifiEntry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updatePasspointR1WifiEntryScans$21(Map.Entry entry) {
        return ((PasspointR1WifiEntry) entry.getValue()).getLevel() == -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updatePasspointWifiEntryScans$17(Set set, Map.Entry entry) {
        return ((PasspointWifiEntry) entry.getValue()).getLevel() == -1 || (!set.contains(entry.getKey()) && ((PasspointWifiEntry) entry.getValue()).getConnectedState() == 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateStandardWifiEntryScans$11(ScanResult scanResult) {
        return !TextUtils.isEmpty(scanResult.SSID);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateStandardWifiEntryScans$12(Set set, Map map, StandardWifiEntry standardWifiEntry) {
        StandardWifiEntry.ScanResultKey scanResultKey = standardWifiEntry.getStandardWifiEntryKey().getScanResultKey();
        set.remove(scanResultKey);
        standardWifiEntry.updateScanResultInfo((List) map.get(scanResultKey));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateStandardWifiEntryScans$13(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.getLevel() == -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateSuggestedWifiEntryScans$14(ScanResult scanResult) {
        return !TextUtils.isEmpty(scanResult.SSID);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateSuggestedWifiEntryScans$15(Set set, Map map, StandardWifiEntry standardWifiEntry) {
        StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = standardWifiEntry.getStandardWifiEntryKey();
        set.add(standardWifiEntryKey);
        standardWifiEntry.updateScanResultInfo((List) map.get(standardWifiEntryKey.getScanResultKey()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateSuggestedWifiEntryScans$16(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.getLevel() == -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiConfigurations$23(WifiConfiguration wifiConfiguration) {
        return !wifiConfiguration.isEphemeral();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Integer lambda$updateWifiConfigurations$24(WifiConfiguration wifiConfiguration) {
        return Integer.valueOf(wifiConfiguration.networkId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateWifiConfigurations$25(StandardWifiEntry standardWifiEntry) {
        standardWifiEntry.updateConfig(this.mStandardWifiConfigCache.get(standardWifiEntry.getStandardWifiEntryKey()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWifiConfigurations$26(StandardWifiEntry standardWifiEntry) {
        standardWifiEntry.updateConfig(this.mSuggestedConfigCache.get(standardWifiEntry.getStandardWifiEntryKey()));
        return !standardWifiEntry.isSuggestion();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$0(StandardWifiEntry standardWifiEntry) {
        int connectedState = standardWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$1(StandardWifiEntry standardWifiEntry) {
        int slaveConnectedState = standardWifiEntry.getSlaveConnectedState();
        return slaveConnectedState == 2 || slaveConnectedState == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$10(WifiEntry wifiEntry) {
        return wifiEntry.getConnectedState() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$2(StandardWifiEntry standardWifiEntry) {
        int connectedState = standardWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$3(PasspointWifiEntry passpointWifiEntry) {
        int connectedState = passpointWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWifiEntries$4(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.isUserShareable() || standardWifiEntry == this.mConnectedWifiEntry;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ StandardWifiEntry.ScanResultKey lambda$updateWifiEntries$5(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.getStandardWifiEntryKey().getScanResultKey();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$6(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.getConnectedState() == 0 && standardWifiEntry.isUserShareable();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$7(PasspointWifiEntry passpointWifiEntry) {
        return passpointWifiEntry.getConnectedState() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$8(OsuWifiEntry osuWifiEntry) {
        return osuWifiEntry.getConnectedState() == 0 && !osuWifiEntry.isAlreadyProvisioned();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$9(PasspointR1WifiEntry passpointR1WifiEntry) {
        return passpointR1WifiEntry.getConnectedState() == 0 && !passpointR1WifiEntry.isAlreadyProvisioned();
    }

    private void notifyOnNumSavedNetworksChanged() {
        final WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new Runnable() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WifiPickerTracker.WifiPickerTrackerCallback.this.onNumSavedNetworksChanged();
                }
            });
        }
    }

    private void notifyOnNumSavedSubscriptionsChanged() {
        final WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new Runnable() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    WifiPickerTracker.WifiPickerTrackerCallback.this.onNumSavedSubscriptionsChanged();
                }
            });
        }
    }

    private void notifyOnWifiEntriesChanged() {
        final WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new Runnable() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    WifiPickerTracker.WifiPickerTrackerCallback.this.onWifiEntriesChanged();
                }
            });
        }
    }

    private void updateConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        Iterator<StandardWifiEntry> it = this.mStandardWifiEntryCache.iterator();
        while (it.hasNext()) {
            it.next().updateConnectionInfo(wifiInfo, networkInfo);
        }
        Iterator<StandardWifiEntry> it2 = this.mSuggestedWifiEntryCache.iterator();
        while (it2.hasNext()) {
            it2.next().updateConnectionInfo(wifiInfo, networkInfo);
        }
        Iterator<PasspointWifiEntry> it3 = this.mPasspointWifiEntryCache.values().iterator();
        while (it3.hasNext()) {
            it3.next().updateConnectionInfo(wifiInfo, networkInfo);
        }
        Iterator<OsuWifiEntry> it4 = this.mOsuWifiEntryCache.values().iterator();
        while (it4.hasNext()) {
            it4.next().updateConnectionInfo(wifiInfo, networkInfo);
        }
        Iterator<PasspointR1WifiEntry> it5 = this.mPasspointR1WifiEntryCache.values().iterator();
        while (it5.hasNext()) {
            it5.next().updateConnectionInfo(wifiInfo, networkInfo);
        }
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry != null) {
            networkRequestEntry.updateConnectionInfo(wifiInfo, networkInfo);
        }
        updateNetworkRequestEntryConnectionInfo(wifiInfo, networkInfo);
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            mergedCarrierEntry.updateConnectionInfo(wifiInfo, networkInfo);
        }
        conditionallyCreateConnectedStandardWifiEntry(wifiInfo, networkInfo);
        conditionallyCreateConnectedSuggestedWifiEntry(wifiInfo, networkInfo);
        conditionallyCreateConnectedPasspointWifiEntry(wifiInfo, networkInfo);
    }

    private void updateForSlave() {
        WifiInfo wifiSlaveConnectionInfo = SlaveWifiUtilsStub.getInstance(this.mContext).getWifiSlaveConnectionInfo();
        Network slaveWifiCurrentNetwork = SlaveWifiUtilsStub.getInstance(this.mContext).getSlaveWifiCurrentNetwork();
        if (wifiSlaveConnectionInfo == null || slaveWifiCurrentNetwork == null) {
            return;
        }
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(slaveWifiCurrentNetwork);
        this.mSlaveCurrentNetworkInfo = networkInfo;
        updateSlaveConnectionInfo(wifiSlaveConnectionInfo, networkInfo);
        handleSlaveNetworkCapabilitiesChanged(this.mConnectivityManager.getNetworkCapabilities(slaveWifiCurrentNetwork));
        handleSlaveLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(slaveWifiCurrentNetwork));
    }

    private void updateMergedCarrierEntry(int i) {
        if (i != -1) {
            MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
            if (mergedCarrierEntry != null && i == mergedCarrierEntry.getSubscriptionId()) {
                return;
            }
            MergedCarrierEntry mergedCarrierEntry2 = new MergedCarrierEntry(this.mWorkerHandler, this.mWifiManager, this.mWifiNetworkScoreCache, false, this.mContext, i);
            this.mMergedCarrierEntry = mergedCarrierEntry2;
            mergedCarrierEntry2.updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
        } else if (this.mMergedCarrierEntry == null) {
            return;
        } else {
            this.mMergedCarrierEntry = null;
        }
        notifyOnWifiEntriesChanged();
    }

    private void updateNetworkRequestEntryConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        ArrayList arrayList = new ArrayList();
        if (wifiInfo != null) {
            int i = 0;
            while (true) {
                if (i >= this.mNetworkRequestConfigCache.size()) {
                    break;
                }
                List<WifiConfiguration> valueAt = this.mNetworkRequestConfigCache.valueAt(i);
                if (!valueAt.isEmpty() && valueAt.get(0).networkId == wifiInfo.getNetworkId()) {
                    arrayList.addAll(valueAt);
                    break;
                }
                i++;
            }
        }
        if (arrayList.isEmpty()) {
            this.mNetworkRequestEntry = null;
            return;
        }
        StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey((WifiConfiguration) arrayList.get(0));
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry == null || !networkRequestEntry.getStandardWifiEntryKey().equals(standardWifiEntryKey)) {
            NetworkRequestEntry networkRequestEntry2 = new NetworkRequestEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, this.mWifiManager, this.mWifiNetworkScoreCache, false);
            this.mNetworkRequestEntry = networkRequestEntry2;
            networkRequestEntry2.updateConfig(arrayList);
        }
        this.mNetworkRequestEntry.updateConnectionInfo(wifiInfo, networkInfo);
    }

    private void updateNetworkRequestEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry == null) {
            return;
        }
        final StandardWifiEntry.ScanResultKey scanResultKey = networkRequestEntry.getStandardWifiEntryKey().getScanResultKey();
        this.mNetworkRequestEntry.updateScanResultInfo((List) list.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda14
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateNetworkRequestEntryScans$22;
                lambda$updateNetworkRequestEntryScans$22 = WifiPickerTracker.lambda$updateNetworkRequestEntryScans$22(StandardWifiEntry.ScanResultKey.this, (ScanResult) obj);
                return lambda$updateNetworkRequestEntryScans$22;
            }
        }).collect(Collectors.toList()));
    }

    private void updateOsuWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map matchingOsuProviders = this.mWifiManager.getMatchingOsuProviders(list);
        final Map matchingPasspointConfigsForOsuProviders = this.mWifiManager.getMatchingPasspointConfigsForOsuProviders(matchingOsuProviders.keySet());
        for (OsuWifiEntry osuWifiEntry : this.mOsuWifiEntryCache.values()) {
            osuWifiEntry.updateScanResultInfo((List) matchingOsuProviders.remove(osuWifiEntry.getOsuProvider()));
        }
        for (OsuProvider osuProvider : matchingOsuProviders.keySet()) {
            OsuWifiEntry osuWifiEntry2 = new OsuWifiEntry(this.mContext, this.mMainHandler, osuProvider, this.mWifiManager, this.mWifiNetworkScoreCache, false);
            osuWifiEntry2.updateScanResultInfo((List) matchingOsuProviders.get(osuProvider));
            this.mOsuWifiEntryCache.put(OsuWifiEntry.osuProviderToOsuWifiEntryKey(osuProvider), osuWifiEntry2);
        }
        this.mOsuWifiEntryCache.values().forEach(new Consumer() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WifiPickerTracker.this.lambda$updateOsuWifiEntryScans$18(matchingPasspointConfigsForOsuProviders, (OsuWifiEntry) obj);
            }
        });
        this.mOsuWifiEntryCache.entrySet().removeIf(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda34
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateOsuWifiEntryScans$19;
                lambda$updateOsuWifiEntryScans$19 = WifiPickerTracker.lambda$updateOsuWifiEntryScans$19((Map.Entry) obj);
                return lambda$updateOsuWifiEntryScans$19;
            }
        });
    }

    private void updatePasspointConfigurations(List<PasspointConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mPasspointConfigCache.clear();
        this.mPasspointConfigCache.putAll((Map) list.stream().collect(Collectors.toMap(new Function() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$updatePasspointConfigurations$27;
                lambda$updatePasspointConfigurations$27 = WifiPickerTracker.lambda$updatePasspointConfigurations$27((PasspointConfiguration) obj);
                return lambda$updatePasspointConfigurations$27;
            }
        }, Function.identity())));
        this.mPasspointWifiEntryCache.entrySet().removeIf(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda17
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updatePasspointConfigurations$28;
                lambda$updatePasspointConfigurations$28 = WifiPickerTracker.this.lambda$updatePasspointConfigurations$28((Map.Entry) obj);
                return lambda$updatePasspointConfigurations$28;
            }
        });
    }

    private void updatePasspointR1WifiEntryScans(List<ScanResult> list) {
        if (WifiPasspointProvision.isPasspointR1Supported()) {
            Preconditions.checkNotNull(list, "Scan Result list should not be null!");
            Map<PasspointR1Provider, List<ScanResult>> matchingPasspointR1Providers = PasspointUtilsStub.getInstance(this.mContext).getMatchingPasspointR1Providers(list);
            final Map<PasspointR1Provider, PasspointConfiguration> matchingPasspointConfigsForPasspointR1Providers = PasspointUtilsStub.getInstance(this.mContext).getMatchingPasspointConfigsForPasspointR1Providers(matchingPasspointR1Providers.keySet());
            for (PasspointR1WifiEntry passpointR1WifiEntry : this.mPasspointR1WifiEntryCache.values()) {
                passpointR1WifiEntry.updateScanResultInfo(matchingPasspointR1Providers.remove(passpointR1WifiEntry.getPasspointR1Provider()));
            }
            for (PasspointR1Provider passpointR1Provider : matchingPasspointR1Providers.keySet()) {
                PasspointR1WifiEntry passpointR1WifiEntry2 = new PasspointR1WifiEntry(this.mContext, this.mMainHandler, passpointR1Provider, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                passpointR1WifiEntry2.updateScanResultInfo(matchingPasspointR1Providers.get(passpointR1Provider));
                this.mPasspointR1WifiEntryCache.put(PasspointR1WifiEntry.passpointR1ProviderToPasspointR1WifiEntryKey(passpointR1Provider), passpointR1WifiEntry2);
            }
            this.mPasspointR1WifiEntryCache.values().forEach(new Consumer() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WifiPickerTracker.this.lambda$updatePasspointR1WifiEntryScans$20(matchingPasspointConfigsForPasspointR1Providers, (PasspointR1WifiEntry) obj);
                }
            });
            this.mPasspointR1WifiEntryCache.entrySet().removeIf(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda33
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updatePasspointR1WifiEntryScans$21;
                    lambda$updatePasspointR1WifiEntryScans$21 = WifiPickerTracker.lambda$updatePasspointR1WifiEntryScans$21((Map.Entry) obj);
                    return lambda$updatePasspointR1WifiEntryScans$21;
                }
            });
        }
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        final TreeSet treeSet = new TreeSet();
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            List<ScanResult> list2 = (List) ((Map) pair.second).get(0);
            List<ScanResult> list3 = (List) ((Map) pair.second).get(1);
            String uniqueIdToPasspointWifiEntryKey = PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            treeSet.add(uniqueIdToPasspointWifiEntryKey);
            if (!this.mPasspointWifiEntryCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                if (wifiConfiguration.fromWifiNetworkSuggestion) {
                    this.mPasspointWifiEntryCache.put(uniqueIdToPasspointWifiEntryKey, new PasspointWifiEntry(this.mContext, this.mMainHandler, wifiConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false));
                } else if (this.mPasspointConfigCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                    this.mPasspointWifiEntryCache.put(uniqueIdToPasspointWifiEntryKey, new PasspointWifiEntry(this.mContext, this.mMainHandler, this.mPasspointConfigCache.get(uniqueIdToPasspointWifiEntryKey), this.mWifiManager, this.mWifiNetworkScoreCache, false));
                }
            }
            this.mPasspointWifiEntryCache.get(uniqueIdToPasspointWifiEntryKey).updateScanResultInfo(wifiConfiguration, list2, list3);
        }
        this.mPasspointWifiEntryCache.entrySet().removeIf(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda18
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updatePasspointWifiEntryScans$17;
                lambda$updatePasspointWifiEntryScans$17 = WifiPickerTracker.lambda$updatePasspointWifiEntryScans$17(treeSet, (Map.Entry) obj);
                return lambda$updatePasspointWifiEntryScans$17;
            }
        });
    }

    private void updateSlaveConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        Iterator<StandardWifiEntry> it = this.mStandardWifiEntryCache.iterator();
        while (it.hasNext()) {
            it.next().updateSlaveConnectionInfo(wifiInfo, networkInfo);
        }
        conditionallyCreateConnectedStandardWifiEntry(wifiInfo, networkInfo, true);
    }

    private void updateStandardWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        final Map map = (Map) list.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda20
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateStandardWifiEntryScans$11;
                lambda$updateStandardWifiEntryScans$11 = WifiPickerTracker.lambda$updateStandardWifiEntryScans$11((ScanResult) obj);
                return lambda$updateStandardWifiEntryScans$11;
            }
        }).collect(Collectors.groupingBy(SavedNetworkTracker$$ExternalSyntheticLambda4.INSTANCE));
        final ArraySet<StandardWifiEntry.ScanResultKey> arraySet = new ArraySet(map.keySet());
        this.mStandardWifiEntryCache.forEach(new Consumer() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WifiPickerTracker.lambda$updateStandardWifiEntryScans$12(arraySet, map, (StandardWifiEntry) obj);
            }
        });
        for (StandardWifiEntry.ScanResultKey scanResultKey : arraySet) {
            StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey(scanResultKey, true);
            this.mStandardWifiEntryCache.add(new StandardWifiEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, this.mStandardWifiConfigCache.get(standardWifiEntryKey), (List) map.get(scanResultKey), this.mWifiManager, this.mWifiNetworkScoreCache, false));
        }
        this.mStandardWifiEntryCache.removeIf(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda27
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateStandardWifiEntryScans$13;
                lambda$updateStandardWifiEntryScans$13 = WifiPickerTracker.lambda$updateStandardWifiEntryScans$13((StandardWifiEntry) obj);
                return lambda$updateStandardWifiEntryScans$13;
            }
        });
    }

    private void updateSuggestedWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        final Map map = (Map) list.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda19
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateSuggestedWifiEntryScans$14;
                lambda$updateSuggestedWifiEntryScans$14 = WifiPickerTracker.lambda$updateSuggestedWifiEntryScans$14((ScanResult) obj);
                return lambda$updateSuggestedWifiEntryScans$14;
            }
        }).collect(Collectors.groupingBy(SavedNetworkTracker$$ExternalSyntheticLambda4.INSTANCE));
        final ArraySet arraySet = new ArraySet();
        this.mSuggestedWifiEntryCache.forEach(new Consumer() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WifiPickerTracker.lambda$updateSuggestedWifiEntryScans$15(arraySet, map, (StandardWifiEntry) obj);
            }
        });
        Set set = (Set) this.mWifiManager.getWifiConfigForMatchedNetworkSuggestionsSharedWithUser(list).stream().map(SavedNetworkTracker$$ExternalSyntheticLambda5.INSTANCE).collect(Collectors.toSet());
        for (StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey : this.mSuggestedConfigCache.keySet()) {
            StandardWifiEntry.ScanResultKey scanResultKey = standardWifiEntryKey.getScanResultKey();
            if (!arraySet.contains(standardWifiEntryKey) && map.containsKey(scanResultKey)) {
                StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, this.mSuggestedConfigCache.get(standardWifiEntryKey), (List) map.get(scanResultKey), this.mWifiManager, this.mWifiNetworkScoreCache, false);
                standardWifiEntry.setUserShareable(set.contains(standardWifiEntryKey));
                this.mSuggestedWifiEntryCache.add(standardWifiEntry);
            }
        }
        this.mSuggestedWifiEntryCache.removeIf(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda28
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateSuggestedWifiEntryScans$16;
                lambda$updateSuggestedWifiEntryScans$16 = WifiPickerTracker.lambda$updateSuggestedWifiEntryScans$16((StandardWifiEntry) obj);
                return lambda$updateSuggestedWifiEntryScans$16;
            }
        });
    }

    private void updateWifiConfigurations(List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mStandardWifiConfigCache.clear();
        this.mSuggestedConfigCache.clear();
        this.mNetworkRequestConfigCache.clear();
        new ArrayList();
        for (WifiConfiguration wifiConfiguration : list) {
            if (!wifiConfiguration.carrierMerged) {
                StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey(wifiConfiguration, true);
                if (wifiConfiguration.isPasspoint()) {
                    this.mPasspointWifiConfigCache.put(wifiConfiguration.networkId, wifiConfiguration);
                } else if (wifiConfiguration.fromWifiNetworkSuggestion) {
                    if (!this.mSuggestedConfigCache.containsKey(standardWifiEntryKey)) {
                        this.mSuggestedConfigCache.put(standardWifiEntryKey, new ArrayList());
                    }
                    this.mSuggestedConfigCache.get(standardWifiEntryKey).add(wifiConfiguration);
                } else if (wifiConfiguration.fromWifiNetworkSpecifier) {
                    if (!this.mNetworkRequestConfigCache.containsKey(standardWifiEntryKey)) {
                        this.mNetworkRequestConfigCache.put(standardWifiEntryKey, new ArrayList());
                    }
                    this.mNetworkRequestConfigCache.get(standardWifiEntryKey).add(wifiConfiguration);
                } else {
                    if (!this.mStandardWifiConfigCache.containsKey(standardWifiEntryKey)) {
                        this.mStandardWifiConfigCache.put(standardWifiEntryKey, new ArrayList());
                    }
                    this.mStandardWifiConfigCache.get(standardWifiEntryKey).add(wifiConfiguration);
                }
            }
        }
        this.mNumSavedNetworks = (int) this.mStandardWifiConfigCache.values().stream().flatMap(DashboardFragment$$ExternalSyntheticLambda10.INSTANCE).filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda21
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateWifiConfigurations$23;
                lambda$updateWifiConfigurations$23 = WifiPickerTracker.lambda$updateWifiConfigurations$23((WifiConfiguration) obj);
                return lambda$updateWifiConfigurations$23;
            }
        }).map(new Function() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda10
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$updateWifiConfigurations$24;
                lambda$updateWifiConfigurations$24 = WifiPickerTracker.lambda$updateWifiConfigurations$24((WifiConfiguration) obj);
                return lambda$updateWifiConfigurations$24;
            }
        }).distinct().count();
        this.mStandardWifiEntryCache.forEach(new Consumer() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WifiPickerTracker.this.lambda$updateWifiConfigurations$25((StandardWifiEntry) obj);
            }
        });
        this.mSuggestedWifiEntryCache.removeIf(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda15
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateWifiConfigurations$26;
                lambda$updateWifiConfigurations$26 = WifiPickerTracker.this.lambda$updateWifiConfigurations$26((StandardWifiEntry) obj);
                return lambda$updateWifiConfigurations$26;
            }
        });
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry != null) {
            networkRequestEntry.updateConfig(this.mNetworkRequestConfigCache.get(networkRequestEntry.getStandardWifiEntryKey()));
        }
    }

    public WifiEntry getConnectedWifiEntry() {
        return this.mConnectedWifiEntry;
    }

    protected List<WifiEntry> getContextualWifiEntries() {
        return Collections.emptyList();
    }

    public MergedCarrierEntry getMergedCarrierEntry() {
        return this.mMergedCarrierEntry;
    }

    public int getNumSavedNetworks() {
        return this.mNumSavedNetworks;
    }

    public int getNumSavedSubscriptions() {
        return this.mPasspointConfigCache.size();
    }

    public WifiEntry getSlaveConnectedWifiEntry() {
        return this.mSlaveConnectedWifiEntry;
    }

    public List<WifiEntry> getWifiEntries() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mWifiEntries);
        }
        return arrayList;
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleConfiguredNetworksChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        processConfiguredNetworksChanged();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleDefaultRouteChanged() {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null) {
            wifiEntry.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
            this.mConnectedWifiEntry.setIsLowQuality(this.mIsWifiValidated && this.mIsCellDefaultRoute);
        }
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            if (mergedCarrierEntry.getConnectedState() == 2) {
                this.mMergedCarrierEntry.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
            }
            this.mMergedCarrierEntry.updateIsCellDefaultRoute(this.mIsCellDefaultRoute);
        }
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleDefaultSubscriptionChanged(int i) {
        updateMergedCarrierEntry(i);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleLinkPropertiesChanged(LinkProperties linkProperties) {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null && wifiEntry.getConnectedState() == 2) {
            this.mConnectedWifiEntry.updateLinkProperties(linkProperties);
        }
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            mergedCarrierEntry.updateLinkProperties(linkProperties);
        }
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities) {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null && wifiEntry.getConnectedState() == 2) {
            this.mConnectedWifiEntry.updateNetworkCapabilities(networkCapabilities);
            this.mConnectedWifiEntry.setIsLowQuality(this.mIsWifiValidated && this.mIsCellDefaultRoute);
        }
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            mergedCarrierEntry.updateNetworkCapabilities(networkCapabilities);
        }
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleNetworkScoreCacheUpdated() {
        Iterator<StandardWifiEntry> it = this.mStandardWifiEntryCache.iterator();
        while (it.hasNext()) {
            it.next().onScoreCacheUpdated();
        }
        Iterator<StandardWifiEntry> it2 = this.mSuggestedWifiEntryCache.iterator();
        while (it2.hasNext()) {
            it2.next().onScoreCacheUpdated();
        }
        Iterator<PasspointWifiEntry> it3 = this.mPasspointWifiEntryCache.values().iterator();
        while (it3.hasNext()) {
            it3.next().onScoreCacheUpdated();
        }
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleNetworkStateChangedAction(Intent intent) {
        NetworkInfo networkInfo;
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
            this.mCurrentNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
        } else {
            this.mSlaveCurrentNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            updateSlaveConnectionInfo(SlaveWifiUtilsStub.getInstance(this.mContext).getWifiSlaveConnectionInfo(), this.mSlaveCurrentNetworkInfo);
        }
        NetworkInfo networkInfo2 = this.mCurrentNetworkInfo;
        if ((networkInfo2 != null && networkInfo2.isConnected()) || ((networkInfo = this.mSlaveCurrentNetworkInfo) != null && networkInfo.isConnected())) {
            this.mWorkerHandler.postDelayed(new Runnable() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    WifiPickerTracker.this.updateWifiEntries();
                }
            }, 7100L);
        }
        updateWifiEntries();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleOnStart() {
        updateWifiConfigurations(this.mWifiManager.getPrivilegedConfiguredNetworks());
        updatePasspointConfigurations(this.mWifiManager.getPasspointConfigurations());
        this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        conditionallyUpdateScanResults(true);
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(currentNetwork);
        this.mCurrentNetworkInfo = networkInfo;
        updateConnectionInfo(connectionInfo, networkInfo);
        notifyOnNumSavedNetworksChanged();
        notifyOnNumSavedSubscriptionsChanged();
        handleDefaultSubscriptionChanged(SubscriptionManager.getDefaultDataSubscriptionId());
        updateForSlave();
        updateWifiEntries();
        handleNetworkCapabilitiesChanged(this.mConnectivityManager.getNetworkCapabilities(currentNetwork));
        handleLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(currentNetwork));
        handleDefaultRouteChanged();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleRssiChangedAction() {
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null) {
            wifiEntry.updateConnectionInfo(connectionInfo, this.mCurrentNetworkInfo);
        }
        WifiInfo wifiSlaveConnectionInfo = SlaveWifiUtilsStub.getInstance(this.mContext).getWifiSlaveConnectionInfo();
        WifiEntry wifiEntry2 = this.mSlaveConnectedWifiEntry;
        if (wifiEntry2 != null) {
            wifiEntry2.updateSlaveConnectionInfo(wifiSlaveConnectionInfo, this.mSlaveCurrentNetworkInfo);
        }
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            mergedCarrierEntry.updateConnectionInfo(connectionInfo, this.mCurrentNetworkInfo);
        }
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleScanResultsAvailableAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateScanResults(intent.getBooleanExtra("resultsUpdated", true));
        updateWifiEntries();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleSlaveLinkPropertiesChanged(LinkProperties linkProperties) {
        WifiEntry wifiEntry = this.mSlaveConnectedWifiEntry;
        if (wifiEntry == null || wifiEntry.getSlaveConnectedState() != 2) {
            return;
        }
        this.mSlaveConnectedWifiEntry.updateSlaveLinkProperties(linkProperties);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleSlaveNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities) {
        WifiEntry wifiEntry = this.mSlaveConnectedWifiEntry;
        if (wifiEntry == null || wifiEntry.getSlaveConnectedState() != 2) {
            return;
        }
        this.mSlaveConnectedWifiEntry.updateSlaveNetworkCapabilities(networkCapabilities);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleWifiStateChangedAction() {
        conditionallyUpdateScanResults(true);
        if (this.mWifiManager.getWifiState() != 3) {
            updateConnectionInfo(null, null);
        }
        updateWifiEntries();
    }

    protected void processConfiguredNetworksChanged() {
        updateWifiConfigurations(this.mWifiManager.getPrivilegedConfiguredNetworks());
        updatePasspointConfigurations(this.mWifiManager.getPasspointConfigurations());
        List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults();
        updateStandardWifiEntryScans(scanResults);
        updateNetworkRequestEntryScans(scanResults);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
        updatePasspointR1WifiEntryScans(scanResults);
        notifyOnNumSavedNetworksChanged();
        notifyOnNumSavedSubscriptionsChanged();
        updateWifiEntries();
    }

    protected void updateContextualWifiEntryScans(List<ScanResult> list) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateWifiEntries() {
        NetworkRequestEntry networkRequestEntry;
        synchronized (this.mLock) {
            this.mConnectedWifiEntry = this.mStandardWifiEntryCache.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda29
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateWifiEntries$0;
                    lambda$updateWifiEntries$0 = WifiPickerTracker.lambda$updateWifiEntries$0((StandardWifiEntry) obj);
                    return lambda$updateWifiEntries$0;
                }
            }).findAny().orElse(null);
            this.mSlaveConnectedWifiEntry = this.mStandardWifiEntryCache.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda30
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateWifiEntries$1;
                    lambda$updateWifiEntries$1 = WifiPickerTracker.lambda$updateWifiEntries$1((StandardWifiEntry) obj);
                    return lambda$updateWifiEntries$1;
                }
            }).findAny().orElse(null);
            if (this.mConnectedWifiEntry == null) {
                this.mConnectedWifiEntry = this.mSuggestedWifiEntryCache.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda31
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$updateWifiEntries$2;
                        lambda$updateWifiEntries$2 = WifiPickerTracker.lambda$updateWifiEntries$2((StandardWifiEntry) obj);
                        return lambda$updateWifiEntries$2;
                    }
                }).findAny().orElse(null);
            }
            if (this.mConnectedWifiEntry == null) {
                this.mConnectedWifiEntry = this.mPasspointWifiEntryCache.values().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda25
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$updateWifiEntries$3;
                        lambda$updateWifiEntries$3 = WifiPickerTracker.lambda$updateWifiEntries$3((PasspointWifiEntry) obj);
                        return lambda$updateWifiEntries$3;
                    }
                }).findAny().orElse(null);
            }
            if (this.mConnectedWifiEntry == null && (networkRequestEntry = this.mNetworkRequestEntry) != null && networkRequestEntry.getConnectedState() != 0) {
                this.mConnectedWifiEntry = this.mNetworkRequestEntry;
            }
            this.mWifiEntries.clear();
            Set set = (Set) this.mSuggestedWifiEntryCache.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda16
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateWifiEntries$4;
                    lambda$updateWifiEntries$4 = WifiPickerTracker.this.lambda$updateWifiEntries$4((StandardWifiEntry) obj);
                    return lambda$updateWifiEntries$4;
                }
            }).map(new Function() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda12
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    StandardWifiEntry.ScanResultKey lambda$updateWifiEntries$5;
                    lambda$updateWifiEntries$5 = WifiPickerTracker.lambda$updateWifiEntries$5((StandardWifiEntry) obj);
                    return lambda$updateWifiEntries$5;
                }
            }).collect(Collectors.toSet());
            for (StandardWifiEntry standardWifiEntry : this.mStandardWifiEntryCache) {
                if (standardWifiEntry != this.mConnectedWifiEntry && standardWifiEntry != this.mSlaveConnectedWifiEntry && (standardWifiEntry.isSaved() || !set.contains(standardWifiEntry.getStandardWifiEntryKey().getScanResultKey()))) {
                    this.mWifiEntries.add(standardWifiEntry);
                }
            }
            this.mWifiEntries.addAll((Collection) this.mSuggestedWifiEntryCache.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda26
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateWifiEntries$6;
                    lambda$updateWifiEntries$6 = WifiPickerTracker.lambda$updateWifiEntries$6((StandardWifiEntry) obj);
                    return lambda$updateWifiEntries$6;
                }
            }).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) this.mPasspointWifiEntryCache.values().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda24
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateWifiEntries$7;
                    lambda$updateWifiEntries$7 = WifiPickerTracker.lambda$updateWifiEntries$7((PasspointWifiEntry) obj);
                    return lambda$updateWifiEntries$7;
                }
            }).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) this.mOsuWifiEntryCache.values().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda22
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateWifiEntries$8;
                    lambda$updateWifiEntries$8 = WifiPickerTracker.lambda$updateWifiEntries$8((OsuWifiEntry) obj);
                    return lambda$updateWifiEntries$8;
                }
            }).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) this.mPasspointR1WifiEntryCache.values().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda23
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateWifiEntries$9;
                    lambda$updateWifiEntries$9 = WifiPickerTracker.lambda$updateWifiEntries$9((PasspointR1WifiEntry) obj);
                    return lambda$updateWifiEntries$9;
                }
            }).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) getContextualWifiEntries().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda32
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateWifiEntries$10;
                    lambda$updateWifiEntries$10 = WifiPickerTracker.lambda$updateWifiEntries$10((WifiEntry) obj);
                    return lambda$updateWifiEntries$10;
                }
            }).collect(Collectors.toList()));
            Collections.sort(this.mWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v("WifiPickerTracker", "Connected WifiEntry: " + this.mConnectedWifiEntry);
                Log.v("WifiPickerTracker", "Connected SlaveWifiEntry: " + this.mSlaveConnectedWifiEntry);
                Log.v("WifiPickerTracker", "Updated WifiEntries: " + Arrays.toString(this.mWifiEntries.toArray()));
            }
        }
        notifyOnWifiEntriesChanged();
    }
}
