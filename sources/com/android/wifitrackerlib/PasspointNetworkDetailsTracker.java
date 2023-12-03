package com.android.wifitrackerlib;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
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
import android.text.TextUtils;
import android.util.Pair;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* loaded from: classes2.dex */
public class PasspointNetworkDetailsTracker extends NetworkDetailsTracker {
    private final PasspointWifiEntry mChosenEntry;
    private NetworkInfo mCurrentNetworkInfo;
    private WifiConfiguration mCurrentWifiConfig;
    private OsuWifiEntry mOsuWifiEntry;
    private PasspointR1WifiEntry mPasspointR1WifiEntry;

    public PasspointNetworkDetailsTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, final String str) {
        super(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, "PasspointNetworkDetailsTracker");
        Optional<PasspointConfiguration> findAny = this.mWifiManager.getPasspointConfigurations().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.PasspointNetworkDetailsTracker$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$new$0;
                lambda$new$0 = PasspointNetworkDetailsTracker.lambda$new$0(str, (PasspointConfiguration) obj);
                return lambda$new$0;
            }
        }).findAny();
        if (findAny.isPresent()) {
            this.mChosenEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, findAny.get(), this.mWifiManager, this.mWifiNetworkScoreCache, false);
        } else {
            Optional findAny2 = this.mWifiManager.getPrivilegedConfiguredNetworks().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.PasspointNetworkDetailsTracker$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$new$1;
                    lambda$new$1 = PasspointNetworkDetailsTracker.lambda$new$1(str, (WifiConfiguration) obj);
                    return lambda$new$1;
                }
            }).findAny();
            if (!findAny2.isPresent()) {
                throw new IllegalArgumentException("Cannot find config for given PasspointWifiEntry key!");
            }
            this.mChosenEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, (WifiConfiguration) findAny2.get(), this.mWifiManager, this.mWifiNetworkScoreCache, false);
        }
        updateStartInfo();
    }

    private void cacheNewScanResults() {
        this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
    }

    private void conditionallyUpdateConfig() {
        this.mWifiManager.getPasspointConfigurations().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.PasspointNetworkDetailsTracker$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$conditionallyUpdateConfig$2;
                lambda$conditionallyUpdateConfig$2 = PasspointNetworkDetailsTracker.this.lambda$conditionallyUpdateConfig$2((PasspointConfiguration) obj);
                return lambda$conditionallyUpdateConfig$2;
            }
        }).findAny().ifPresent(new Consumer() { // from class: com.android.wifitrackerlib.PasspointNetworkDetailsTracker$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PasspointNetworkDetailsTracker.this.lambda$conditionallyUpdateConfig$3((PasspointConfiguration) obj);
            }
        });
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            this.mChosenEntry.updateScanResultInfo(this.mCurrentWifiConfig, null, null);
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            cacheNewScanResults();
        } else {
            j += this.mScanIntervalMillis;
        }
        List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults(j);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
        updatePasspointR1WifiEntryScans(scanResults);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$conditionallyUpdateConfig$2(PasspointConfiguration passpointConfiguration) {
        return TextUtils.equals(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()), this.mChosenEntry.getKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$conditionallyUpdateConfig$3(PasspointConfiguration passpointConfiguration) {
        this.mChosenEntry.updatePasspointConfig(passpointConfiguration);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$0(String str, PasspointConfiguration passpointConfiguration) {
        return TextUtils.equals(str, PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$1(String str, WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.isPasspoint() && TextUtils.equals(str, PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()));
    }

    private void updateOsuWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map matchingOsuProviders = this.mWifiManager.getMatchingOsuProviders(list);
        Map matchingPasspointConfigsForOsuProviders = this.mWifiManager.getMatchingPasspointConfigsForOsuProviders(matchingOsuProviders.keySet());
        OsuWifiEntry osuWifiEntry = this.mOsuWifiEntry;
        if (osuWifiEntry != null) {
            osuWifiEntry.updateScanResultInfo((List) matchingOsuProviders.get(osuWifiEntry.getOsuProvider()));
        } else {
            for (OsuProvider osuProvider : matchingOsuProviders.keySet()) {
                PasspointConfiguration passpointConfiguration = (PasspointConfiguration) matchingPasspointConfigsForOsuProviders.get(osuProvider);
                if (passpointConfiguration != null && TextUtils.equals(this.mChosenEntry.getKey(), PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()))) {
                    OsuWifiEntry osuWifiEntry2 = new OsuWifiEntry(this.mContext, this.mMainHandler, osuProvider, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                    this.mOsuWifiEntry = osuWifiEntry2;
                    osuWifiEntry2.updateScanResultInfo((List) matchingOsuProviders.get(osuProvider));
                    this.mOsuWifiEntry.setAlreadyProvisioned(true);
                    this.mChosenEntry.setOsuWifiEntry(this.mOsuWifiEntry);
                    return;
                }
            }
        }
        OsuWifiEntry osuWifiEntry3 = this.mOsuWifiEntry;
        if (osuWifiEntry3 == null || osuWifiEntry3.getLevel() != -1) {
            return;
        }
        this.mChosenEntry.setOsuWifiEntry(null);
        this.mOsuWifiEntry = null;
    }

    private void updatePasspointR1WifiEntryScans(List<ScanResult> list) {
        if (WifiPasspointProvision.isPasspointR1Supported()) {
            Preconditions.checkNotNull(list, "Scan Result list should not be null!");
            Map<PasspointR1Provider, List<ScanResult>> matchingPasspointR1Providers = PasspointUtilsStub.getInstance(this.mContext).getMatchingPasspointR1Providers(list);
            Map<PasspointR1Provider, PasspointConfiguration> matchingPasspointConfigsForPasspointR1Providers = PasspointUtilsStub.getInstance(this.mContext).getMatchingPasspointConfigsForPasspointR1Providers(matchingPasspointR1Providers.keySet());
            PasspointR1WifiEntry passpointR1WifiEntry = this.mPasspointR1WifiEntry;
            if (passpointR1WifiEntry != null) {
                passpointR1WifiEntry.updateScanResultInfo(matchingPasspointR1Providers.get(passpointR1WifiEntry.getPasspointR1Provider()));
            } else {
                for (PasspointR1Provider passpointR1Provider : matchingPasspointR1Providers.keySet()) {
                    PasspointConfiguration passpointConfiguration = matchingPasspointConfigsForPasspointR1Providers.get(passpointR1Provider);
                    if (passpointConfiguration != null && TextUtils.equals(this.mChosenEntry.getKey(), PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()))) {
                        PasspointR1WifiEntry passpointR1WifiEntry2 = new PasspointR1WifiEntry(this.mContext, this.mMainHandler, passpointR1Provider, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                        this.mPasspointR1WifiEntry = passpointR1WifiEntry2;
                        passpointR1WifiEntry2.updateScanResultInfo(matchingPasspointR1Providers.get(passpointR1Provider));
                        this.mPasspointR1WifiEntry.setAlreadyProvisioned(true);
                        this.mChosenEntry.setPasspointR1WifiEntry(this.mPasspointR1WifiEntry);
                        return;
                    }
                }
            }
            PasspointR1WifiEntry passpointR1WifiEntry3 = this.mPasspointR1WifiEntry;
            if (passpointR1WifiEntry3 == null || passpointR1WifiEntry3.getLevel() != -1) {
                return;
            }
            this.mChosenEntry.setPasspointR1WifiEntry(null);
            this.mPasspointR1WifiEntry = null;
        }
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            if (TextUtils.equals(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()), this.mChosenEntry.getKey())) {
                this.mCurrentWifiConfig = wifiConfiguration;
                this.mChosenEntry.updateScanResultInfo(wifiConfiguration, (List) ((Map) pair.second).get(0), (List) ((Map) pair.second).get(1));
                return;
            }
        }
        this.mChosenEntry.updateScanResultInfo(this.mCurrentWifiConfig, null, null);
    }

    private void updateStartInfo() {
        conditionallyUpdateScanResults(true);
        conditionallyUpdateConfig();
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(currentNetwork);
        this.mCurrentNetworkInfo = networkInfo;
        this.mChosenEntry.updateConnectionInfo(connectionInfo, networkInfo);
        handleNetworkCapabilitiesChanged(this.mConnectivityManager.getNetworkCapabilities(currentNetwork));
        handleLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(currentNetwork));
        this.mChosenEntry.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
        this.mChosenEntry.setIsLowQuality(this.mIsWifiValidated && this.mIsCellDefaultRoute);
    }

    @Override // com.android.wifitrackerlib.NetworkDetailsTracker
    public WifiEntry getWifiEntry() {
        return this.mChosenEntry;
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleConfiguredNetworksChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateConfig();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleOnStart() {
        updateStartInfo();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleScanResultsAvailableAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateScanResults(intent.getBooleanExtra("resultsUpdated", true));
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleWifiStateChangedAction() {
        conditionallyUpdateScanResults(true);
    }
}
