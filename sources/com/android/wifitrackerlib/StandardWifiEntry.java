package com.android.wifitrackerlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.wifitrackerlib.WifiEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import miui.cloud.CloudPushConstants;
import miui.provider.ExtraContacts;
import miui.vip.VipService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@VisibleForTesting
/* loaded from: classes2.dex */
public class StandardWifiEntry extends WifiEntry {
    private final Context mContext;
    private final List<ScanResult> mCurrentScanResults;
    private final boolean mIsEnhancedOpenSupported;
    private boolean mIsOnly24GHz;
    private boolean mIsOnly5GHz;
    private boolean mIsUserShareable;
    private final boolean mIsWpa3SaeSupported;
    private final boolean mIsWpa3SuiteBSupported;
    private final StandardWifiEntryKey mKey;
    private final Map<Integer, List<ScanResult>> mMatchingScanResults;
    private final Map<Integer, WifiConfiguration> mMatchingWifiConfigs;
    private String mRecommendationServiceLabel;
    private boolean mShouldAutoOpenCaptivePortal;
    private final List<ScanResult> mTargetScanResults;
    private List<Integer> mTargetSecurityTypes;
    private WifiConfiguration mTargetWifiConfig;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class ScanResultKey {
        private Set<Integer> mSecurityTypes;
        private String mSsid;

        ScanResultKey() {
            this.mSecurityTypes = new ArraySet();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ScanResultKey(ScanResult scanResult) {
            this(scanResult.SSID, Utils.getSecurityTypesFromScanResult(scanResult));
        }

        ScanResultKey(WifiConfiguration wifiConfiguration) {
            this(WifiInfo.sanitizeSsid(wifiConfiguration.SSID), Utils.getSecurityTypesFromWifiConfiguration(wifiConfiguration));
        }

        ScanResultKey(String str) {
            this.mSecurityTypes = new ArraySet();
            try {
                JSONObject jSONObject = new JSONObject(str);
                this.mSsid = jSONObject.getString("SSID");
                JSONArray jSONArray = jSONObject.getJSONArray("SECURITY_TYPES");
                for (int i = 0; i < jSONArray.length(); i++) {
                    this.mSecurityTypes.add(Integer.valueOf(jSONArray.getInt(i)));
                }
            } catch (JSONException e) {
                Log.wtf("StandardWifiEntry", "JSONException while constructing ScanResultKey from string: " + e);
            }
        }

        ScanResultKey(String str, List<Integer> list) {
            this.mSecurityTypes = new ArraySet();
            this.mSsid = str;
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                this.mSecurityTypes.add(Integer.valueOf(intValue));
                if (intValue == 0) {
                    this.mSecurityTypes.add(6);
                } else if (intValue == 6) {
                    this.mSecurityTypes.add(0);
                } else if (intValue == 9) {
                    this.mSecurityTypes.add(3);
                } else if (intValue == 2) {
                    this.mSecurityTypes.add(4);
                } else if (intValue == 3) {
                    this.mSecurityTypes.add(9);
                } else if (intValue == 4) {
                    this.mSecurityTypes.add(2);
                }
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || ScanResultKey.class != obj.getClass()) {
                return false;
            }
            ScanResultKey scanResultKey = (ScanResultKey) obj;
            return TextUtils.equals(this.mSsid, scanResultKey.mSsid) && this.mSecurityTypes.equals(scanResultKey.mSecurityTypes);
        }

        Set<Integer> getSecurityTypes() {
            return this.mSecurityTypes;
        }

        String getSsid() {
            return this.mSsid;
        }

        public int hashCode() {
            return Objects.hash(this.mSsid, this.mSecurityTypes);
        }

        public String toString() {
            JSONObject jSONObject = new JSONObject();
            try {
                String str = this.mSsid;
                if (str != null) {
                    jSONObject.put("SSID", str);
                }
                if (!this.mSecurityTypes.isEmpty()) {
                    JSONArray jSONArray = new JSONArray();
                    Iterator<Integer> it = this.mSecurityTypes.iterator();
                    while (it.hasNext()) {
                        jSONArray.put(it.next().intValue());
                    }
                    jSONObject.put("SECURITY_TYPES", jSONArray);
                }
            } catch (JSONException e) {
                Log.e("StandardWifiEntry", "JSONException while converting ScanResultKey to string: " + e);
            }
            return jSONObject.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class StandardWifiEntryKey {
        private boolean mIsNetworkRequest;
        private boolean mIsTargetingNewNetworks;
        private ScanResultKey mScanResultKey;
        private String mSuggestionProfileKey;

        /* JADX INFO: Access modifiers changed from: package-private */
        public StandardWifiEntryKey(WifiConfiguration wifiConfiguration) {
            this(wifiConfiguration, false);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public StandardWifiEntryKey(WifiConfiguration wifiConfiguration, boolean z) {
            this.mIsTargetingNewNetworks = false;
            this.mScanResultKey = new ScanResultKey(wifiConfiguration);
            if (wifiConfiguration.fromWifiNetworkSuggestion) {
                this.mSuggestionProfileKey = new StringJoiner(",").add(wifiConfiguration.creatorName).add(String.valueOf(wifiConfiguration.carrierId)).add(String.valueOf(wifiConfiguration.subscriptionId)).toString();
            } else if (wifiConfiguration.fromWifiNetworkSpecifier) {
                this.mIsNetworkRequest = true;
            }
            this.mIsTargetingNewNetworks = z;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public StandardWifiEntryKey(ScanResultKey scanResultKey, boolean z) {
            this.mIsTargetingNewNetworks = false;
            this.mScanResultKey = scanResultKey;
            this.mIsTargetingNewNetworks = z;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public StandardWifiEntryKey(String str) {
            this.mIsTargetingNewNetworks = false;
            this.mScanResultKey = new ScanResultKey();
            if (!str.startsWith("StandardWifiEntry:")) {
                Log.e("StandardWifiEntry", "String key does not start with key prefix!");
                return;
            }
            try {
                JSONObject jSONObject = new JSONObject(str.substring(18));
                if (jSONObject.has("SCAN_RESULT_KEY")) {
                    this.mScanResultKey = new ScanResultKey(jSONObject.getString("SCAN_RESULT_KEY"));
                }
                if (jSONObject.has("SUGGESTION_PROFILE_KEY")) {
                    this.mSuggestionProfileKey = jSONObject.getString("SUGGESTION_PROFILE_KEY");
                }
                if (jSONObject.has("IS_NETWORK_REQUEST")) {
                    this.mIsNetworkRequest = jSONObject.getBoolean("IS_NETWORK_REQUEST");
                }
                if (jSONObject.has("IS_TARGETING_NEW_NETWORKS")) {
                    this.mIsTargetingNewNetworks = jSONObject.getBoolean("IS_TARGETING_NEW_NETWORKS");
                }
            } catch (JSONException e) {
                Log.e("StandardWifiEntry", "JSONException while converting StandardWifiEntryKey to string: " + e);
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || StandardWifiEntryKey.class != obj.getClass()) {
                return false;
            }
            StandardWifiEntryKey standardWifiEntryKey = (StandardWifiEntryKey) obj;
            return Objects.equals(this.mScanResultKey, standardWifiEntryKey.mScanResultKey) && TextUtils.equals(this.mSuggestionProfileKey, standardWifiEntryKey.mSuggestionProfileKey) && this.mIsNetworkRequest == standardWifiEntryKey.mIsNetworkRequest;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ScanResultKey getScanResultKey() {
            return this.mScanResultKey;
        }

        public int hashCode() {
            return Objects.hash(this.mScanResultKey, this.mSuggestionProfileKey, Boolean.valueOf(this.mIsNetworkRequest));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isNetworkRequest() {
            return this.mIsNetworkRequest;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isTargetingNewNetworks() {
            return this.mIsTargetingNewNetworks;
        }

        public String toString() {
            JSONObject jSONObject = new JSONObject();
            try {
                ScanResultKey scanResultKey = this.mScanResultKey;
                if (scanResultKey != null) {
                    jSONObject.put("SCAN_RESULT_KEY", scanResultKey.toString());
                }
                String str = this.mSuggestionProfileKey;
                if (str != null) {
                    jSONObject.put("SUGGESTION_PROFILE_KEY", str);
                }
                boolean z = this.mIsNetworkRequest;
                if (z) {
                    jSONObject.put("IS_NETWORK_REQUEST", z);
                }
                boolean z2 = this.mIsTargetingNewNetworks;
                if (z2) {
                    jSONObject.put("IS_TARGETING_NEW_NETWORKS", z2);
                }
            } catch (JSONException e) {
                Log.wtf("StandardWifiEntry", "JSONException while converting StandardWifiEntryKey to string: " + e);
            }
            return "StandardWifiEntry:" + jSONObject.toString();
        }
    }

    public StandardWifiEntry(Context context, Handler handler, WifiNetworkScoreCache wifiNetworkScoreCache, WifiManager wifiManager, boolean z, String str, List<Integer> list) throws IllegalArgumentException {
        super(handler, wifiManager, wifiNetworkScoreCache, z);
        this.mMatchingScanResults = new HashMap();
        this.mMatchingWifiConfigs = new HashMap();
        this.mTargetScanResults = new ArrayList();
        this.mTargetSecurityTypes = new ArrayList();
        this.mIsUserShareable = false;
        this.mIsOnly5GHz = false;
        this.mIsOnly24GHz = false;
        this.mCurrentScanResults = new ArrayList();
        this.mShouldAutoOpenCaptivePortal = false;
        this.mContext = context;
        this.mKey = new StandardWifiEntryKey(new ScanResultKey(str, list), true);
        updateSecurityTypes();
        if (wifiManager.isWifiEnabled()) {
            this.mIsWpa3SaeSupported = wifiManager.isWpa3SaeSupported();
            this.mIsWpa3SuiteBSupported = wifiManager.isWpa3SuiteBSupported();
            this.mIsEnhancedOpenSupported = wifiManager.isEnhancedOpenSupported();
            return;
        }
        this.mIsWpa3SaeSupported = true;
        this.mIsWpa3SuiteBSupported = true;
        this.mIsEnhancedOpenSupported = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardWifiEntry(Context context, Handler handler, StandardWifiEntryKey standardWifiEntryKey, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) {
        super(handler, wifiManager, wifiNetworkScoreCache, z);
        this.mMatchingScanResults = new HashMap();
        this.mMatchingWifiConfigs = new HashMap();
        this.mTargetScanResults = new ArrayList();
        this.mTargetSecurityTypes = new ArrayList();
        this.mIsUserShareable = false;
        this.mIsOnly5GHz = false;
        this.mIsOnly24GHz = false;
        this.mCurrentScanResults = new ArrayList();
        this.mShouldAutoOpenCaptivePortal = false;
        this.mContext = context;
        this.mKey = standardWifiEntryKey;
        if (wifiManager.isWifiEnabled()) {
            this.mIsWpa3SaeSupported = wifiManager.isWpa3SaeSupported();
            this.mIsWpa3SuiteBSupported = wifiManager.isWpa3SuiteBSupported();
            this.mIsEnhancedOpenSupported = wifiManager.isEnhancedOpenSupported();
        } else {
            this.mIsWpa3SaeSupported = true;
            this.mIsWpa3SuiteBSupported = true;
            this.mIsEnhancedOpenSupported = true;
        }
        updateRecommendationServiceLabel();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardWifiEntry(Context context, Handler handler, StandardWifiEntryKey standardWifiEntryKey, List<WifiConfiguration> list, List<ScanResult> list2, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        this(context, handler, standardWifiEntryKey, wifiManager, wifiNetworkScoreCache, z);
        if (list != null && !list.isEmpty()) {
            updateConfig(list);
        }
        if (list2 == null || list2.isEmpty()) {
            return;
        }
        updateScanResultInfo(list2);
    }

    private synchronized String getScanResultDescription(final int i, final int i2) {
        List list = (List) this.mTargetScanResults.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getScanResultDescription$3;
                lambda$getScanResultDescription$3 = StandardWifiEntry.lambda$getScanResultDescription$3(i, i2, (ScanResult) obj);
                return lambda$getScanResultDescription$3;
            }
        }).sorted(Comparator.comparingInt(new ToIntFunction() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda6
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int lambda$getScanResultDescription$4;
                lambda$getScanResultDescription$4 = StandardWifiEntry.lambda$getScanResultDescription$4((ScanResult) obj);
                return lambda$getScanResultDescription$4;
            }
        })).collect(Collectors.toList());
        int size = list.size();
        if (size == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(size);
        sb.append(")");
        if (size > 4) {
            int asInt = list.stream().mapToInt(new ToIntFunction() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda5
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int i3;
                    i3 = ((ScanResult) obj).level;
                    return i3;
                }
            }).max().getAsInt();
            sb.append("max=");
            sb.append(asInt);
            sb.append(",");
        }
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        list.forEach(new Consumer() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                StandardWifiEntry.this.lambda$getScanResultDescription$6(sb, elapsedRealtime, (ScanResult) obj);
            }
        });
        return sb.toString();
    }

    private synchronized String getScanResultDescription(ScanResult scanResult, long j) {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null && scanResult.BSSID.equals(wifiInfo.getBSSID())) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        sb.append(",");
        sb.append(scanResult.level);
        int i = ((int) (j - (scanResult.timestamp / 1000))) / VipService.VIP_SERVICE_FAILURE;
        sb.append(",");
        sb.append(i);
        sb.append(CloudPushConstants.WATERMARK_TYPE.SUBSCRIPTION);
        sb.append("}");
        return sb.toString();
    }

    private String getSlaveSummary(String str) {
        int slaveConnectedState = getSlaveConnectedState();
        return slaveConnectedState != 1 ? slaveConnectedState != 2 ? str : Utils.getConnectedDescription(this.mContext, this.mTargetWifiConfig, this.mSlaveNetworkCapabilities, this.mRecommendationServiceLabel, true, this.mIsLowQuality) : Utils.getConnectingDescription(this.mContext, this.mSlaveNetworkInfo);
    }

    private boolean isSecurityTypeSupported(int i) {
        if (i != 4) {
            if (i != 5) {
                if (i != 6) {
                    return true;
                }
                return this.mIsEnhancedOpenSupported;
            }
            return this.mIsWpa3SuiteBSupported;
        }
        return this.mIsWpa3SaeSupported;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$disconnect$2(WifiEntry.DisconnectCallback disconnectCallback) {
        if (disconnectCallback == null || !this.mCalledDisconnect) {
            return;
        }
        disconnectCallback.onDisconnectResult(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getScanResultDescription$3(int i, int i2, ScanResult scanResult) {
        int i3 = scanResult.frequency;
        return i3 >= i && i3 <= i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getScanResultDescription$4(ScanResult scanResult) {
        return scanResult.level * (-1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getScanResultDescription$6(StringBuilder sb, long j, ScanResult scanResult) {
        sb.append(getScanResultDescription(scanResult, j));
    }

    private synchronized void updateRecommendationServiceLabel() {
        NetworkScorerAppData activeScorer = ((NetworkScoreManager) this.mContext.getSystemService("network_score")).getActiveScorer();
        if (activeScorer != null) {
            this.mRecommendationServiceLabel = activeScorer.getRecommendationServiceLabel();
        }
    }

    private synchronized void updateTargetScanResultInfo() {
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(this.mTargetScanResults);
        if (getConnectedState() == 0 && getSlaveConnectedState() == 0) {
            this.mLevel = bestScanResultByLevel != null ? WifiEntryUtilsStub.miuiCalculateSignalLevel(bestScanResultByLevel.level, this.mWifiManager) : -1;
            this.mSpeed = Utils.getAverageSpeedFromScanResults(this.mScoreCache, this.mTargetScanResults);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canConnect() {
        WifiConfiguration wifiConfiguration;
        WifiEnterpriseConfig wifiEnterpriseConfig;
        if (this.mLevel != -1 && getConnectedState() == 0) {
            if (!this.mTargetSecurityTypes.contains(3) || (wifiConfiguration = this.mTargetWifiConfig) == null || (wifiEnterpriseConfig = wifiConfiguration.enterpriseConfig) == null) {
                return true;
            }
            if (wifiEnterpriseConfig.isAuthenticationSimBased()) {
                List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service")).getActiveSubscriptionInfoList();
                if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.size() != 0) {
                    if (this.mTargetWifiConfig.carrierId == -1) {
                        return true;
                    }
                    Iterator<SubscriptionInfo> it = activeSubscriptionInfoList.iterator();
                    while (it.hasNext()) {
                        if (it.next().getCarrierId() == this.mTargetWifiConfig.carrierId) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canDisconnect() {
        return getConnectedState() == 2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x002c, code lost:
    
        if (r3.mTargetSecurityTypes.contains(4) != false) goto L16;
     */
    @Override // com.android.wifitrackerlib.WifiEntry
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean canEasyConnect() {
        /*
            r3 = this;
            monitor-enter(r3)
            android.net.wifi.WifiConfiguration r0 = r3.getWifiConfiguration()     // Catch: java.lang.Throwable -> L31
            r1 = 0
            if (r0 != 0) goto La
            monitor-exit(r3)
            return r1
        La:
            android.net.wifi.WifiManager r0 = r3.mWifiManager     // Catch: java.lang.Throwable -> L31
            boolean r0 = r0.isEasyConnectSupported()     // Catch: java.lang.Throwable -> L31
            if (r0 != 0) goto L14
            monitor-exit(r3)
            return r1
        L14:
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch: java.lang.Throwable -> L31
            r2 = 2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: java.lang.Throwable -> L31
            boolean r0 = r0.contains(r2)     // Catch: java.lang.Throwable -> L31
            if (r0 != 0) goto L2e
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch: java.lang.Throwable -> L31
            r2 = 4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: java.lang.Throwable -> L31
            boolean r0 = r0.contains(r2)     // Catch: java.lang.Throwable -> L31
            if (r0 == 0) goto L2f
        L2e:
            r1 = 1
        L2f:
            monitor-exit(r3)
            return r1
        L31:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.canEasyConnect():boolean");
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canForget() {
        return getWifiConfiguration() != null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetAutoJoinEnabled() {
        return isSaved() || isSuggestion();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetMeteredChoice() {
        return getWifiConfiguration() != null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetPrivacy() {
        return isSaved();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canShare() {
        if (getWifiConfiguration() == null) {
            return false;
        }
        Iterator<Integer> it = this.mTargetSecurityTypes.iterator();
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            if (intValue == 0 || intValue == 1 || intValue == 2 || intValue == 4 || intValue == 6) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canSignIn() {
        boolean z;
        NetworkCapabilities networkCapabilities = this.mNetworkCapabilities;
        if (networkCapabilities != null) {
            z = networkCapabilities.hasCapability(17);
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canSlaveSignIn() {
        boolean z;
        NetworkCapabilities networkCapabilities = this.mSlaveNetworkCapabilities;
        if (networkCapabilities != null) {
            z = networkCapabilities.hasCapability(17);
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void connect(WifiEntry.ConnectCallback connectCallback) {
        connect(connectCallback, false);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void connect(final WifiEntry.ConnectCallback connectCallback, boolean z) {
        this.mConnectCallback = connectCallback;
        this.mShouldAutoOpenCaptivePortal = true;
        this.mWifiManager.stopRestrictingAutoJoinToSubscriptionId();
        if (isSaved() || isSuggestion()) {
            if (Utils.isSimCredential(this.mTargetWifiConfig) && !Utils.isSimPresent(this.mContext, this.mTargetWifiConfig.carrierId)) {
                if (connectCallback != null) {
                    this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            WifiEntry.ConnectCallback.this.onConnectResult(3);
                        }
                    });
                }
            } else if (z) {
                SlaveWifiUtilsStub.getInstance(this.mContext).connectToSlaveAp(this.mTargetWifiConfig.networkId);
            } else {
                this.mWifiManager.connect(this.mTargetWifiConfig.networkId, new WifiEntry.ConnectActionListener());
            }
        } else if (!this.mTargetSecurityTypes.contains(6)) {
            if (!this.mTargetSecurityTypes.contains(0)) {
                if (connectCallback != null) {
                    this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            WifiEntry.ConnectCallback.this.onConnectResult(1);
                        }
                    });
                    return;
                }
                return;
            }
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" + this.mKey.getScanResultKey().getSsid() + "\"";
            wifiConfiguration.setSecurityParams(0);
            if (z) {
                SlaveWifiUtilsStub.getInstance(this.mContext).connectToSlaveAp(wifiConfiguration);
            } else {
                this.mWifiManager.connect(wifiConfiguration, new WifiEntry.ConnectActionListener());
            }
        } else {
            WifiConfiguration wifiConfiguration2 = new WifiConfiguration();
            wifiConfiguration2.SSID = "\"" + this.mKey.getScanResultKey().getSsid() + "\"";
            wifiConfiguration2.setSecurityParams(6);
            this.mWifiManager.connect(wifiConfiguration2, new WifiEntry.ConnectActionListener());
            if (this.mTargetSecurityTypes.contains(0)) {
                WifiConfiguration wifiConfiguration3 = new WifiConfiguration();
                wifiConfiguration3.SSID = "\"" + this.mKey.getScanResultKey().getSsid() + "\"";
                wifiConfiguration3.setSecurityParams(0);
                this.mWifiManager.save(wifiConfiguration3, null);
            }
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected synchronized boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp() && !wifiInfo.isOsuAp()) {
            Iterator<WifiConfiguration> it = this.mMatchingWifiConfigs.values().iterator();
            while (it.hasNext()) {
                if (it.next().networkId == wifiInfo.getNetworkId()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void disconnect(final WifiEntry.DisconnectCallback disconnectCallback) {
        if (canDisconnect()) {
            this.mCalledDisconnect = true;
            this.mDisconnectCallback = disconnectCallback;
            this.mCallbackHandler.postDelayed(new Runnable() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StandardWifiEntry.this.lambda$disconnect$2(disconnectCallback);
                }
            }, 10000L);
            this.mWifiManager.disableEphemeralNetwork("\"" + this.mKey.getScanResultKey().getSsid() + "\"");
            this.mWifiManager.disconnect();
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void forget(WifiEntry.ForgetCallback forgetCallback) {
        if (canForget()) {
            this.mForgetCallback = forgetCallback;
            this.mWifiManager.forget(this.mTargetWifiConfig.networkId, new WifiEntry.ForgetActionListener());
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getKey() {
        return this.mKey.toString();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getMacAddress() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            String macAddress = wifiInfo.getMacAddress();
            if (!TextUtils.isEmpty(macAddress) && !TextUtils.equals(macAddress, "02:00:00:00:00:00")) {
                return macAddress;
            }
        }
        if (this.mTargetWifiConfig != null && getPrivacy() == 1) {
            return this.mTargetWifiConfig.getRandomizedMacAddress().toString();
        }
        String[] factoryMacAddresses = this.mWifiManager.getFactoryMacAddresses();
        if (factoryMacAddresses.length > 0) {
            return factoryMacAddresses[0];
        }
        return null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized int getMeteredChoice() {
        WifiConfiguration wifiConfiguration;
        if (!isSuggestion() && (wifiConfiguration = this.mTargetWifiConfig) != null) {
            int i = wifiConfiguration.meteredOverride;
            if (i == 1) {
                return 1;
            }
            if (i == 2) {
                return 2;
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public String getNetworkSelectionDescription() {
        return Utils.getNetworkSelectionDescription(getWifiConfiguration());
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized int getPrivacy() {
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration != null) {
            if (wifiConfiguration.macRandomizationSetting == 0) {
                return 0;
            }
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getScanResultDescription() {
        if (this.mTargetScanResults.size() == 0) {
            return "";
        }
        return "[" + getScanResultDescription(2400, 2500) + ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION + getScanResultDescription(4900, 5900) + ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION + getScanResultDescription(5925, 7125) + ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION + getScanResultDescription(58320, 70200) + "]";
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized Set<ScanResult> getScanResults() {
        HashSet hashSet;
        hashSet = new HashSet();
        hashSet.addAll(this.mCurrentScanResults);
        return hashSet;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public CharSequence getSecondSummary() {
        return getConnectedState() == 2 ? Utils.getImsiProtectionDescription(this.mContext, getWifiConfiguration()) : "";
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSecurityString(boolean z) {
        if (this.mTargetSecurityTypes.size() == 0) {
            return z ? "" : this.mContext.getString(R$string.wifitrackerlib_wifi_security_none);
        }
        if (this.mTargetSecurityTypes.size() == 1) {
            switch (this.mTargetSecurityTypes.get(0).intValue()) {
                case 0:
                    return z ? "" : this.mContext.getString(R$string.wifitrackerlib_wifi_security_none);
                case 1:
                    return this.mContext.getString(R$string.wifitrackerlib_wifi_security_wep);
                case 2:
                    return z ? this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_wpa_wpa2) : this.mContext.getString(R$string.wifitrackerlib_wifi_security_wpa_wpa2);
                case 3:
                    return z ? this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_eap_wpa_wpa2) : this.mContext.getString(R$string.wifitrackerlib_wifi_security_eap_wpa_wpa2);
                case 4:
                    return z ? this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_sae) : this.mContext.getString(R$string.wifitrackerlib_wifi_security_sae);
                case 5:
                    return z ? this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_eap_suiteb) : this.mContext.getString(R$string.wifitrackerlib_wifi_security_eap_suiteb);
                case 6:
                    return z ? this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_owe) : this.mContext.getString(R$string.wifitrackerlib_wifi_security_owe);
                case 7:
                    return this.mContext.getString(R$string.wifi_security_wapi_psk);
                case 8:
                    return this.mContext.getString(R$string.wifi_security_wapi_certificate);
                case 9:
                    return z ? this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_eap_wpa3) : this.mContext.getString(R$string.wifitrackerlib_wifi_security_eap_wpa3);
            }
        }
        if (this.mTargetSecurityTypes.size() == 2) {
            if (this.mTargetSecurityTypes.contains(0) && this.mTargetSecurityTypes.contains(6)) {
                StringJoiner stringJoiner = new StringJoiner("/");
                stringJoiner.add(this.mContext.getString(R$string.wifitrackerlib_wifi_security_none));
                stringJoiner.add(z ? this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_owe) : this.mContext.getString(R$string.wifitrackerlib_wifi_security_owe));
                return stringJoiner.toString();
            } else if (this.mTargetSecurityTypes.contains(2) && this.mTargetSecurityTypes.contains(4)) {
                return z ? this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_wpa_wpa2_wpa3) : this.mContext.getString(R$string.wifitrackerlib_wifi_security_wpa_wpa2_wpa3);
            } else if (this.mTargetSecurityTypes.contains(3) && this.mTargetSecurityTypes.contains(9)) {
                return z ? this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_eap_wpa_wpa2_wpa3) : this.mContext.getString(R$string.wifitrackerlib_wifi_security_eap_wpa_wpa2_wpa3);
            }
        }
        Log.e("StandardWifiEntry", "Couldn't get string for security types: " + this.mTargetSecurityTypes);
        return z ? "" : this.mContext.getString(R$string.wifitrackerlib_wifi_security_none);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized List<Integer> getSecurityTypes() {
        return new ArrayList(this.mTargetSecurityTypes);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSsid() {
        return this.mKey.getScanResultKey().getSsid();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardWifiEntryKey getStandardWifiEntryKey() {
        return this.mKey;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSummary(boolean z) {
        StringJoiner stringJoiner;
        String disconnectedDescription;
        stringJoiner = new StringJoiner(this.mContext.getString(R$string.wifitrackerlib_summary_separator));
        int connectedState = getConnectedState();
        if (connectedState == 0) {
            disconnectedDescription = Utils.getDisconnectedDescription(this.mContext, this.mTargetWifiConfig, this.mForSavedNetworksPage, z);
        } else if (connectedState == 1) {
            disconnectedDescription = Utils.getConnectingDescription(this.mContext, this.mNetworkInfo);
        } else if (connectedState != 2) {
            Log.e("StandardWifiEntry", "getConnectedState() returned unknown state: " + connectedState);
            disconnectedDescription = null;
        } else {
            disconnectedDescription = Utils.getConnectedDescription(this.mContext, this.mTargetWifiConfig, this.mNetworkCapabilities, this.mRecommendationServiceLabel, true, this.mIsLowQuality);
        }
        String slaveSummary = getSlaveSummary(disconnectedDescription);
        if (!TextUtils.isEmpty(slaveSummary)) {
            stringJoiner.add(slaveSummary);
        }
        String speedDescription = Utils.getSpeedDescription(this.mContext, this);
        if (!TextUtils.isEmpty(speedDescription)) {
            stringJoiner.add(speedDescription);
        }
        String autoConnectDescription = Utils.getAutoConnectDescription(this.mContext, this);
        if (!TextUtils.isEmpty(autoConnectDescription)) {
            stringJoiner.add(autoConnectDescription);
        }
        String meteredDescription = Utils.getMeteredDescription(this.mContext, this);
        if (!TextUtils.isEmpty(meteredDescription)) {
            stringJoiner.add(meteredDescription);
        }
        if (!z) {
            String verboseLoggingDescription = Utils.getVerboseLoggingDescription(this);
            if (!TextUtils.isEmpty(verboseLoggingDescription)) {
                stringJoiner.add(verboseLoggingDescription);
            }
        }
        return stringJoiner.toString();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized List<ScanResult> getTargetScanResults() {
        return new ArrayList(this.mTargetScanResults);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getTitle() {
        return this.mKey.getScanResultKey().getSsid();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized WifiConfiguration getWifiConfiguration() {
        if (isSaved()) {
            return this.mTargetWifiConfig;
        }
        return null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isAutoJoinEnabled() {
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration == null) {
            return false;
        }
        return wifiConfiguration.allowAutojoin;
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x000e, code lost:
    
        if (r0.meteredHint != false) goto L11;
     */
    @Override // com.android.wifitrackerlib.WifiEntry
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean isMetered() {
        /*
            r2 = this;
            monitor-enter(r2)
            int r0 = r2.getMeteredChoice()     // Catch: java.lang.Throwable -> L14
            r1 = 1
            if (r0 == r1) goto L12
            android.net.wifi.WifiConfiguration r0 = r2.mTargetWifiConfig     // Catch: java.lang.Throwable -> L14
            if (r0 == 0) goto L11
            boolean r0 = r0.meteredHint     // Catch: java.lang.Throwable -> L14
            if (r0 == 0) goto L11
            goto L12
        L11:
            r1 = 0
        L12:
            monitor-exit(r2)
            return r1
        L14:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.isMetered():boolean");
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isOnly24Ghz() {
        return this.mIsOnly24GHz;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isOnly5Ghz() {
        return this.mIsOnly5GHz;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isSaved() {
        boolean z;
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration != null && !wifiConfiguration.fromWifiNetworkSuggestion) {
            z = wifiConfiguration.isEphemeral() ? false : true;
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isSuggestion() {
        boolean z;
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration != null) {
            z = wifiConfiguration.fromWifiNetworkSuggestion;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean isUserShareable() {
        return this.mIsUserShareable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void onScoreCacheUpdated() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            this.mSpeed = Utils.getSpeedFromWifiInfo(this.mScoreCache, wifiInfo);
        } else {
            this.mSpeed = Utils.getAverageSpeedFromScanResults(this.mScoreCache, this.mTargetScanResults);
        }
        notifyOnUpdated();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void setAutoJoinEnabled(boolean z) {
        if (this.mTargetWifiConfig != null && canSetAutoJoinEnabled()) {
            this.mWifiManager.allowAutojoin(this.mTargetWifiConfig.networkId, z);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void setMeteredChoice(int i) {
        if (canSetMeteredChoice()) {
            if (i == 0) {
                this.mTargetWifiConfig.meteredOverride = 0;
            } else if (i == 1) {
                this.mTargetWifiConfig.meteredOverride = 1;
            } else if (i == 2) {
                this.mTargetWifiConfig.meteredOverride = 2;
            }
            this.mWifiManager.save(this.mTargetWifiConfig, null);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void setPrivacy(int i) {
        if (canSetPrivacy()) {
            WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
            wifiConfiguration.macRandomizationSetting = i == 1 ? 3 : 0;
            this.mWifiManager.save(wifiConfiguration, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setUserShareable(boolean z) {
        this.mIsUserShareable = z;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0028, code lost:
    
        if (r0.getDisableReasonCounter(5) > 0) goto L16;
     */
    @Override // com.android.wifitrackerlib.WifiEntry
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean shouldEditBeforeConnect() {
        /*
            r3 = this;
            monitor-enter(r3)
            android.net.wifi.WifiConfiguration r0 = r3.getWifiConfiguration()     // Catch: java.lang.Throwable -> L2f
            r1 = 0
            if (r0 != 0) goto La
            monitor-exit(r3)
            return r1
        La:
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r0 = r0.getNetworkSelectionStatus()     // Catch: java.lang.Throwable -> L2f
            int r2 = r0.getNetworkSelectionStatus()     // Catch: java.lang.Throwable -> L2f
            if (r2 == 0) goto L2d
            r2 = 2
            int r2 = r0.getDisableReasonCounter(r2)     // Catch: java.lang.Throwable -> L2f
            if (r2 > 0) goto L2a
            r2 = 8
            int r2 = r0.getDisableReasonCounter(r2)     // Catch: java.lang.Throwable -> L2f
            if (r2 > 0) goto L2a
            r2 = 5
            int r0 = r0.getDisableReasonCounter(r2)     // Catch: java.lang.Throwable -> L2f
            if (r0 <= 0) goto L2d
        L2a:
            r0 = 1
            monitor-exit(r3)
            return r0
        L2d:
            monitor-exit(r3)
            return r1
        L2f:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.shouldEditBeforeConnect():boolean");
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void signIn(WifiEntry.SignInCallback signInCallback) {
        if (canSignIn()) {
            ((ConnectivityManager) this.mContext.getSystemService("connectivity")).startCaptivePortalApp(this.mWifiManager.getCurrentNetwork());
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void slaveSignIn(WifiEntry.SignInCallback signInCallback) {
        if (canSlaveSignIn()) {
            ((ConnectivityManager) this.mContext.getSystemService("connectivity")).startCaptivePortalApp(SlaveWifiUtilsStub.getInstance(this.mContext).getSlaveWifiCurrentNetwork());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateConfig(List<WifiConfiguration> list) throws IllegalArgumentException {
        if (list == null) {
            list = Collections.emptyList();
        }
        ScanResultKey scanResultKey = this.mKey.getScanResultKey();
        String ssid = scanResultKey.getSsid();
        Set<Integer> securityTypes = scanResultKey.getSecurityTypes();
        this.mMatchingWifiConfigs.clear();
        for (WifiConfiguration wifiConfiguration : list) {
            if (!TextUtils.equals(ssid, WifiInfo.sanitizeSsid(wifiConfiguration.SSID))) {
                throw new IllegalArgumentException("Attempted to update with wrong SSID! Expected: " + ssid + ", Actual: " + WifiInfo.sanitizeSsid(wifiConfiguration.SSID) + ", Config: " + wifiConfiguration);
            }
            Iterator<Integer> it = Utils.getSecurityTypesFromWifiConfiguration(wifiConfiguration).iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                if (!securityTypes.contains(Integer.valueOf(intValue))) {
                    throw new IllegalArgumentException("Attempted to update with wrong security! Expected one of: " + securityTypes + ", Actual: " + intValue + ", Config: " + wifiConfiguration);
                } else if (isSecurityTypeSupported(intValue)) {
                    this.mMatchingWifiConfigs.put(Integer.valueOf(intValue), wifiConfiguration);
                }
            }
        }
        updateSecurityTypes();
        updateTargetScanResultInfo();
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void updateNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        super.updateNetworkCapabilities(networkCapabilities);
        if (canSignIn() && this.mShouldAutoOpenCaptivePortal) {
            this.mShouldAutoOpenCaptivePortal = false;
            signIn(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        String ssid = this.mKey.getScanResultKey().getSsid();
        this.mIsOnly5GHz = true;
        this.mIsOnly24GHz = true;
        for (ScanResult scanResult : list) {
            if (!TextUtils.equals(scanResult.SSID, ssid)) {
                throw new IllegalArgumentException("Attempted to update with wrong SSID! Expected: " + ssid + ", Actual: " + scanResult.SSID + ", ScanResult: " + scanResult);
            } else if (SlaveWifiUtilsStub.getInstance(this.mContext).is24GHz(scanResult)) {
                this.mIsOnly5GHz = false;
            } else if (SlaveWifiUtilsStub.getInstance(this.mContext).is5GHz(scanResult)) {
                this.mIsOnly24GHz = false;
            }
        }
        this.mMatchingScanResults.clear();
        Set<Integer> securityTypes = this.mKey.getScanResultKey().getSecurityTypes();
        for (ScanResult scanResult2 : list) {
            Iterator<Integer> it = Utils.getSecurityTypesFromScanResult(scanResult2).iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                if (securityTypes.contains(Integer.valueOf(intValue)) && isSecurityTypeSupported(intValue)) {
                    if (!this.mMatchingScanResults.containsKey(Integer.valueOf(intValue))) {
                        this.mMatchingScanResults.put(Integer.valueOf(intValue), new ArrayList());
                    }
                    this.mMatchingScanResults.get(Integer.valueOf(intValue)).add(scanResult2);
                }
            }
        }
        this.mCurrentScanResults.clear();
        this.mCurrentScanResults.addAll(list);
        updateWifiGenerationInfo(this.mCurrentScanResults);
        updateSecurityTypes();
        updateTargetScanResultInfo();
        notifyOnUpdated();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected synchronized void updateSecurityTypes() {
        this.mTargetSecurityTypes.clear();
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo == null) {
            wifiInfo = this.mSlaveWifiInfo;
        }
        if (wifiInfo != null && wifiInfo.getCurrentSecurityType() != -1) {
            this.mTargetSecurityTypes.add(Integer.valueOf(wifiInfo.getCurrentSecurityType()));
        }
        Set<Integer> keySet = this.mMatchingWifiConfigs.keySet();
        if (this.mTargetSecurityTypes.isEmpty() && this.mKey.isTargetingNewNetworks()) {
            this.mTargetSecurityTypes.addAll(this.mMatchingScanResults.keySet());
        }
        if (this.mTargetSecurityTypes.isEmpty()) {
            this.mTargetSecurityTypes.addAll(keySet);
        }
        if (this.mTargetSecurityTypes.isEmpty()) {
            this.mTargetSecurityTypes.addAll(this.mKey.getScanResultKey().getSecurityTypes());
        }
        WifiConfiguration wifiConfiguration = this.mMatchingWifiConfigs.get(Integer.valueOf(Utils.getSingleSecurityTypeFromMultipleSecurityTypes(this.mTargetSecurityTypes)));
        this.mTargetWifiConfig = wifiConfiguration;
        if (wifiConfiguration == null) {
            ArrayList arrayList = new ArrayList(keySet);
            arrayList.retainAll(this.mTargetSecurityTypes);
            this.mTargetWifiConfig = this.mMatchingWifiConfigs.get(Integer.valueOf(Utils.getSingleSecurityTypeFromMultipleSecurityTypes(arrayList)));
        }
        ArraySet arraySet = new ArraySet();
        Iterator<Integer> it = this.mTargetSecurityTypes.iterator();
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            if (this.mMatchingScanResults.containsKey(Integer.valueOf(intValue))) {
                arraySet.addAll(this.mMatchingScanResults.get(Integer.valueOf(intValue)));
            }
        }
        this.mTargetScanResults.clear();
        this.mTargetScanResults.addAll(arraySet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void updateSlaveNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        super.updateSlaveNetworkCapabilities(networkCapabilities);
        if (canSlaveSignIn() && this.mShouldAutoOpenCaptivePortal) {
            this.mShouldAutoOpenCaptivePortal = false;
            slaveSignIn(null);
        }
    }
}
