package com.android.wifitrackerlib;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.PasspointR1Provider;
import android.net.wifi.hotspot2.ProvisioningCallback;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import androidx.core.util.Preconditions;
import com.android.wifitrackerlib.WifiEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class PasspointR1WifiEntry extends WifiEntry {
    private final Context mContext;
    private final List<ScanResult> mCurrentScanResults;
    private boolean mIsAlreadyProvisioned;
    private final String mKey;
    private final PasspointR1Provider mPasspointR1Provider;
    private String mPasspointR1StatusString;
    private String mSsid;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class PasspointR1WifiEntryProvisioningCallback extends ProvisioningCallback {
        PasspointR1WifiEntryProvisioningCallback() {
        }

        public void onProvisioningComplete() {
            ScanResult bestScanResultByLevel;
            synchronized (PasspointR1WifiEntry.this) {
                PasspointR1WifiEntry passpointR1WifiEntry = PasspointR1WifiEntry.this;
                passpointR1WifiEntry.mPasspointR1StatusString = passpointR1WifiEntry.mContext.getString(R$string.wifitrackerlib_osu_sign_up_complete);
            }
            PasspointR1WifiEntry.this.notifyOnUpdated();
            PasspointConfiguration passpointConfiguration = PasspointUtilsStub.getInstance(PasspointR1WifiEntry.this.mContext).getMatchingPasspointConfigsForPasspointR1Providers(Collections.singleton(PasspointR1WifiEntry.this.mPasspointR1Provider)).get(PasspointR1WifiEntry.this.mPasspointR1Provider);
            WifiEntry.ConnectCallback connectCallback = PasspointR1WifiEntry.this.mConnectCallback;
            if (passpointConfiguration == null) {
                if (connectCallback != null) {
                    connectCallback.onConnectResult(2);
                    return;
                }
                return;
            }
            String uniqueId = passpointConfiguration.getUniqueId();
            WifiManager wifiManager = PasspointR1WifiEntry.this.mWifiManager;
            Iterator it = wifiManager.getAllMatchingWifiConfigs(wifiManager.getScanResults()).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Pair pair = (Pair) it.next();
                WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
                if (TextUtils.equals(wifiConfiguration.getKey(), uniqueId)) {
                    List list = (List) ((Map) pair.second).get(0);
                    List list2 = (List) ((Map) pair.second).get(1);
                    if (list != null && !list.isEmpty()) {
                        bestScanResultByLevel = Utils.getBestScanResultByLevel(list);
                    } else if (list2 != null && !list2.isEmpty()) {
                        bestScanResultByLevel = Utils.getBestScanResultByLevel(list2);
                    }
                    wifiConfiguration.SSID = "\"\"";
                    if (bestScanResultByLevel != null) {
                        wifiConfiguration.SSID = "\"" + bestScanResultByLevel.SSID + "\"";
                    }
                    PasspointR1WifiEntry.this.mWifiManager.connect(wifiConfiguration, null);
                    return;
                }
            }
            if (connectCallback != null) {
                connectCallback.onConnectResult(2);
            }
        }

        public void onProvisioningFailure(int i) {
            synchronized (PasspointR1WifiEntry.this) {
                PasspointR1WifiEntry passpointR1WifiEntry = PasspointR1WifiEntry.this;
                passpointR1WifiEntry.mPasspointR1StatusString = passpointR1WifiEntry.mContext.getString(R$string.wifitrackerlib_osu_sign_up_failed);
            }
            WifiEntry.ConnectCallback connectCallback = PasspointR1WifiEntry.this.mConnectCallback;
            if (connectCallback != null) {
                connectCallback.onConnectResult(2);
            }
            PasspointR1WifiEntry.this.notifyOnUpdated();
        }

        public void onProvisioningStatus(int i) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PasspointR1WifiEntry(Context context, Handler handler, PasspointR1Provider passpointR1Provider, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        super(handler, wifiManager, wifiNetworkScoreCache, z);
        this.mCurrentScanResults = new ArrayList();
        this.mIsAlreadyProvisioned = false;
        Preconditions.checkNotNull(passpointR1Provider, "Cannot construct with null passpointR1Provider!");
        this.mContext = context;
        this.mPasspointR1Provider = passpointR1Provider;
        this.mKey = passpointR1ProviderToPasspointR1WifiEntryKey(passpointR1Provider);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String passpointR1ProviderToPasspointR1WifiEntryKey(PasspointR1Provider passpointR1Provider) {
        Preconditions.checkNotNull(passpointR1Provider, "Cannot create key with null PasspointR1Provider!");
        return "PasspointR1WifiEntry:" + passpointR1Provider.getDomainName();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canConnect() {
        boolean z;
        if (this.mLevel != -1) {
            z = getConnectedState() == 0;
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void connect(WifiEntry.ConnectCallback connectCallback) {
        this.mConnectCallback = connectCallback;
        this.mWifiManager.stopRestrictingAutoJoinToSubscriptionId();
        startPasspointR1Provisioning();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        return wifiInfo.isPasspointAp() && !wifiInfo.isOsuAp() && TextUtils.equals(wifiInfo.getPasspointFqdn(), this.mPasspointR1Provider.getDomainName());
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getKey() {
        return this.mKey;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getMacAddress() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PasspointR1Provider getPasspointR1Provider() {
        return this.mPasspointR1Provider;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.WifiEntry
    public String getScanResultDescription() {
        return "";
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSsid() {
        return this.mSsid;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSummary(boolean z) {
        return this.mContext.getString(R$string.wifitrackerlib_tap_to_sign_up);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getTitle() {
        String domainName = this.mPasspointR1Provider.getDomainName();
        if (!TextUtils.isEmpty(domainName)) {
            return "exands.com".equals(domainName) ? "exands Secure Wi-Fi" : domainName;
        } else if (TextUtils.isEmpty(this.mSsid)) {
            return "";
        } else {
            return this.mSsid;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean isAlreadyProvisioned() {
        return this.mIsAlreadyProvisioned;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setAlreadyProvisioned(boolean z) {
        this.mIsAlreadyProvisioned = z;
    }

    public void startPasspointR1Provisioning() {
        WifiPasspointProvision.getInstance(this.mContext).startR1SubscriptionProvisioning(this.mPasspointR1Provider, new PasspointR1WifiEntryProvisioningCallback());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mCurrentScanResults.clear();
        this.mCurrentScanResults.addAll(list);
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(list);
        if (bestScanResultByLevel != null) {
            this.mSsid = bestScanResultByLevel.SSID;
            if (getConnectedState() == 0) {
                this.mLevel = this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level);
            }
        } else {
            this.mLevel = -1;
        }
        notifyOnUpdated();
    }
}
