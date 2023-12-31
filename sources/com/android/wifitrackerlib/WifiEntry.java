package com.android.wifitrackerlib;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import androidx.core.util.Preconditions;
import com.android.net.module.util.NetUtils;
import com.android.settings.wifi.details2.WifiDetailPreferenceController2$$ExternalSyntheticLambda8;
import com.android.wifitrackerlib.WifiEntry;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/* loaded from: classes2.dex */
public class WifiEntry implements Comparable<WifiEntry> {
    protected final Handler mCallbackHandler;
    protected ConnectCallback mConnectCallback;
    protected ConnectedInfo mConnectedInfo;
    private int mDeviceWifiStandard;
    protected DisconnectCallback mDisconnectCallback;
    final boolean mForSavedNetworksPage;
    protected ForgetCallback mForgetCallback;
    protected boolean mIsDefaultNetwork;
    protected boolean mIsLowQuality;
    private boolean mIsValidated;
    private WifiEntryCallback mListener;
    protected NetworkCapabilities mNetworkCapabilities;
    protected NetworkInfo mNetworkInfo;
    protected WifiNetworkScoreCache mScoreCache;
    protected ConnectedInfo mSlaveConnectedInfo;
    private boolean mSlaveIsDefaultNetwork;
    protected boolean mSlaveIsLowQuality;
    private boolean mSlaveIsValidated;
    protected NetworkCapabilities mSlaveNetworkCapabilities;
    protected NetworkInfo mSlaveNetworkInfo;
    protected WifiInfo mSlaveWifiInfo;
    protected WifiInfo mWifiInfo;
    protected final WifiManager mWifiManager;
    protected int mLevel = -1;
    protected int mSpeed = 0;
    protected long mConnectedTimeStamp = -1;
    protected long mSlaveConnectedTimeStamp = -1;
    protected int mSlaveLevel = -1;
    protected int mSlaveSpeed = 0;
    private int mWifiStandard = 1;
    private int mLastMinConnectionCapability = 1;
    protected boolean mCalledConnect = false;
    protected boolean mCalledDisconnect = false;
    private Optional<ManageSubscriptionAction> mManageSubscriptionAction = Optional.empty();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.wifitrackerlib.WifiEntry$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$net$NetworkInfo$DetailedState;

        static {
            int[] iArr = new int[NetworkInfo.DetailedState.values().length];
            $SwitchMap$android$net$NetworkInfo$DetailedState = iArr;
            try {
                iArr[NetworkInfo.DetailedState.SCANNING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.CONNECTING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.AUTHENTICATING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.OBTAINING_IPADDR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.VERIFYING_POOR_LINK.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.CONNECTED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public class ConnectActionListener implements WifiManager.ActionListener {
        /* JADX INFO: Access modifiers changed from: protected */
        public ConnectActionListener() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFailure$1() {
            ConnectCallback connectCallback = WifiEntry.this.mConnectCallback;
            if (connectCallback != null) {
                connectCallback.onConnectResult(2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSuccess$0() {
            WifiEntry wifiEntry = WifiEntry.this;
            if (wifiEntry.mConnectCallback != null && wifiEntry.mCalledConnect && wifiEntry.getConnectedState() == 0) {
                WifiEntry.this.mCalledConnect = false;
            }
        }

        public void onFailure(int i) {
            WifiEntry.this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.WifiEntry$ConnectActionListener$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    WifiEntry.ConnectActionListener.this.lambda$onFailure$1();
                }
            });
        }

        public void onSuccess() {
            WifiEntry wifiEntry;
            synchronized (WifiEntry.this) {
                wifiEntry = WifiEntry.this;
                wifiEntry.mCalledConnect = true;
            }
            wifiEntry.mCallbackHandler.postDelayed(new Runnable() { // from class: com.android.wifitrackerlib.WifiEntry$ConnectActionListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WifiEntry.ConnectActionListener.this.lambda$onSuccess$0();
                }
            }, 10000L);
        }
    }

    /* loaded from: classes2.dex */
    public interface ConnectCallback {
        void onConnectResult(int i);
    }

    /* loaded from: classes2.dex */
    public static class ConnectedInfo {
        public List<String> dnsServers;
        public int frequencyMhz;
        public String gateway;
        public String ipAddress;
        public List<String> ipv6Addresses;
        public int linkSpeedMbps;
        public String subnetMask;
        public int wifiStandard;

        public ConnectedInfo() {
            this.dnsServers = new ArrayList();
            this.ipv6Addresses = new ArrayList();
            this.wifiStandard = 0;
        }

        public ConnectedInfo(ConnectedInfo connectedInfo) {
            this.dnsServers = new ArrayList();
            this.ipv6Addresses = new ArrayList();
            this.wifiStandard = 0;
            this.frequencyMhz = connectedInfo.frequencyMhz;
            this.dnsServers = new ArrayList(this.dnsServers);
            this.linkSpeedMbps = connectedInfo.linkSpeedMbps;
            this.ipAddress = connectedInfo.ipAddress;
            this.ipv6Addresses = new ArrayList(connectedInfo.ipv6Addresses);
            this.gateway = connectedInfo.gateway;
            this.subnetMask = connectedInfo.subnetMask;
            this.wifiStandard = connectedInfo.wifiStandard;
        }
    }

    /* loaded from: classes2.dex */
    public interface DisconnectCallback {
        void onDisconnectResult(int i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public class ForgetActionListener implements WifiManager.ActionListener {
        /* JADX INFO: Access modifiers changed from: protected */
        public ForgetActionListener() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFailure$1() {
            ForgetCallback forgetCallback = WifiEntry.this.mForgetCallback;
            if (forgetCallback != null) {
                forgetCallback.onForgetResult(1);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSuccess$0() {
            ForgetCallback forgetCallback = WifiEntry.this.mForgetCallback;
            if (forgetCallback != null) {
                forgetCallback.onForgetResult(0);
            }
        }

        public void onFailure(int i) {
            WifiEntry.this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.WifiEntry$ForgetActionListener$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    WifiEntry.ForgetActionListener.this.lambda$onFailure$1();
                }
            });
        }

        public void onSuccess() {
            WifiEntry.this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.WifiEntry$ForgetActionListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WifiEntry.ForgetActionListener.this.lambda$onSuccess$0();
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    public interface ForgetCallback {
        void onForgetResult(int i);
    }

    /* loaded from: classes2.dex */
    public interface ManageSubscriptionAction {
        void onExecute();
    }

    /* loaded from: classes2.dex */
    public interface SignInCallback {
    }

    /* loaded from: classes2.dex */
    public interface WifiEntryCallback {
        void onUpdated();
    }

    public WifiEntry(Handler handler, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        Preconditions.checkNotNull(handler, "Cannot construct with null handler!");
        Preconditions.checkNotNull(wifiManager, "Cannot construct with null WifiManager!");
        this.mCallbackHandler = handler;
        this.mForSavedNetworksPage = z;
        this.mWifiManager = wifiManager;
        this.mScoreCache = wifiNetworkScoreCache;
        updatetDeviceWifiGenerationInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyOnUpdated$0() {
        WifiEntryCallback wifiEntryCallback = this.mListener;
        if (wifiEntryCallback != null) {
            wifiEntryCallback.onUpdated();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateConnectionInfo$1() {
        ConnectCallback connectCallback = this.mConnectCallback;
        if (connectCallback != null) {
            connectCallback.onConnectResult(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateConnectionInfo$2() {
        DisconnectCallback disconnectCallback = this.mDisconnectCallback;
        if (disconnectCallback != null) {
            disconnectCallback.onDisconnectResult(0);
        }
    }

    private void updatetDeviceWifiGenerationInfo() {
        if (this.mWifiManager.isWifiStandardSupported(6)) {
            this.mDeviceWifiStandard = 6;
        } else if (this.mWifiManager.isWifiStandardSupported(5)) {
            this.mDeviceWifiStandard = 5;
        } else if (this.mWifiManager.isWifiStandardSupported(4)) {
            this.mDeviceWifiStandard = 4;
        } else {
            this.mDeviceWifiStandard = 1;
        }
    }

    public boolean canConnect() {
        return false;
    }

    public boolean canDisconnect() {
        return false;
    }

    public boolean canEasyConnect() {
        return false;
    }

    public boolean canForget() {
        return false;
    }

    public boolean canManageSubscription() {
        return this.mManageSubscriptionAction.isPresent();
    }

    public boolean canSetAutoJoinEnabled() {
        return false;
    }

    public boolean canSetMeteredChoice() {
        return false;
    }

    public boolean canSetPrivacy() {
        return false;
    }

    public boolean canShare() {
        return false;
    }

    public boolean canSignIn() {
        return false;
    }

    public boolean canSlaveSignIn() {
        return false;
    }

    @Override // java.lang.Comparable
    public int compareTo(WifiEntry wifiEntry) {
        if (getLevel() == -1 || wifiEntry.getLevel() != -1) {
            if (getLevel() != -1 || wifiEntry.getLevel() == -1) {
                if (!isSubscription() || wifiEntry.isSubscription()) {
                    if (isSubscription() || !wifiEntry.isSubscription()) {
                        if (!isSaved() || wifiEntry.isSaved()) {
                            if (isSaved() || !wifiEntry.isSaved()) {
                                if (!isSuggestion() || wifiEntry.isSuggestion()) {
                                    if (isSuggestion() || !wifiEntry.isSuggestion()) {
                                        if (getLevel() > wifiEntry.getLevel()) {
                                            return -1;
                                        }
                                        if (getLevel() < wifiEntry.getLevel()) {
                                            return 1;
                                        }
                                        return getTitle().compareTo(wifiEntry.getTitle());
                                    }
                                    return 1;
                                }
                                return -1;
                            }
                            return 1;
                        }
                        return -1;
                    }
                    return 1;
                }
                return -1;
            }
            return 1;
        }
        return -1;
    }

    public void connect(ConnectCallback connectCallback) {
    }

    public void connect(ConnectCallback connectCallback, boolean z) {
    }

    protected boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        return false;
    }

    public void disconnect(DisconnectCallback disconnectCallback) {
    }

    public boolean equals(Object obj) {
        if (obj instanceof WifiEntry) {
            return getKey().equals(((WifiEntry) obj).getKey());
        }
        return false;
    }

    public void forget(ForgetCallback forgetCallback) {
    }

    public synchronized ConnectedInfo getConnectedInfo() {
        ConnectedInfo connectedInfo;
        if (getConnectedState() == 2 && (connectedInfo = this.mConnectedInfo) != null) {
            return new ConnectedInfo(connectedInfo);
        }
        return null;
    }

    public synchronized int getConnectedState() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        if (networkInfo == null) {
            return 0;
        }
        switch (AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState[networkInfo.getDetailedState().ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return 1;
            case 7:
                return 2;
            default:
                return 0;
        }
    }

    public String getHelpUriString() {
        return null;
    }

    public String getKey() {
        return "";
    }

    public int getLevel() {
        return this.mLevel;
    }

    public String getMacAddress() {
        return null;
    }

    public int getMeteredChoice() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getNetworkCapabilityDescription() {
        StringBuilder sb = new StringBuilder();
        if (getConnectedState() == 2) {
            sb.append("isValidated:");
            sb.append(this.mIsValidated);
            sb.append(", isDefaultNetwork:");
            sb.append(this.mIsDefaultNetwork);
            sb.append(", isLowQuality:");
            sb.append(this.mIsLowQuality);
        }
        return sb.toString();
    }

    public NetworkInfo getNetworkInfo() {
        return this.mNetworkInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getNetworkSelectionDescription() {
        return "";
    }

    public int getPrivacy() {
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getScanResultDescription() {
        return "";
    }

    public Set<ScanResult> getScanResults() {
        return new HashSet();
    }

    public CharSequence getSecondSummary() {
        return "";
    }

    public int getSecurity() {
        switch (Utils.getSingleSecurityTypeFromMultipleSecurityTypes(getSecurityTypes())) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 4;
            case 7:
                return 8;
            case 8:
                return 9;
            case 9:
                return 7;
            case 10:
            default:
                return 0;
            case 11:
            case 12:
                return 3;
        }
    }

    public String getSecurityString(boolean z) {
        return "";
    }

    public List<Integer> getSecurityTypes() {
        return Collections.emptyList();
    }

    public int getSlaveConnectedState() {
        return SlaveWifiUtilsStub.getSlaveConnectedState(this.mSlaveNetworkInfo);
    }

    public NetworkInfo getSlaveNetworkInfo() {
        return this.mSlaveNetworkInfo;
    }

    public WifiInfo getSlaveWifiInfo() {
        return this.mSlaveWifiInfo;
    }

    public int getSpeed() {
        return this.mSpeed;
    }

    public String getSsid() {
        return null;
    }

    public String getSummary() {
        return getSummary(true);
    }

    public String getSummary(boolean z) {
        return "";
    }

    public List<ScanResult> getTargetScanResults() {
        return new ArrayList();
    }

    public String getTitle() {
        return "";
    }

    public WifiConfiguration getWifiConfiguration() {
        return null;
    }

    public WifiInfo getWifiInfo() {
        return this.mWifiInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized String getWifiInfoDescription() {
        StringJoiner stringJoiner;
        stringJoiner = new StringJoiner(" ");
        if (getConnectedState() == 2 && this.mWifiInfo != null) {
            stringJoiner.add("f = " + this.mWifiInfo.getFrequency());
            String bssid = this.mWifiInfo.getBSSID();
            if (bssid != null) {
                stringJoiner.add(bssid);
            }
            stringJoiner.add("standard = " + this.mWifiInfo.getWifiStandard());
            stringJoiner.add("rssi = " + this.mWifiInfo.getRssi());
            stringJoiner.add("score = " + this.mWifiInfo.getScore());
            stringJoiner.add(String.format(" tx=%.1f,", Double.valueOf(this.mWifiInfo.getSuccessfulTxPacketsPerSecond())));
            stringJoiner.add(String.format("%.1f,", Double.valueOf(this.mWifiInfo.getRetriedTxPacketsPerSecond())));
            stringJoiner.add(String.format("%.1f ", Double.valueOf(this.mWifiInfo.getLostTxPacketsPerSecond())));
            stringJoiner.add(String.format("rx=%.1f", Double.valueOf(this.mWifiInfo.getSuccessfulRxPacketsPerSecond())));
        } else if (getSlaveConnectedState() == 2 && this.mSlaveWifiInfo != null) {
            stringJoiner.add("f = " + this.mSlaveWifiInfo.getFrequency());
            String bssid2 = this.mSlaveWifiInfo.getBSSID();
            if (bssid2 != null) {
                stringJoiner.add(bssid2);
            }
            stringJoiner.add("standard = " + this.mSlaveWifiInfo.getWifiStandard());
            stringJoiner.add("rssi = " + this.mSlaveWifiInfo.getRssi());
            stringJoiner.add("score = " + this.mSlaveWifiInfo.getScore());
            stringJoiner.add(String.format(" tx=%.1f,", Double.valueOf(this.mSlaveWifiInfo.getSuccessfulTxPacketsPerSecond())));
            stringJoiner.add(String.format("%.1f,", Double.valueOf(this.mSlaveWifiInfo.getRetriedTxPacketsPerSecond())));
            stringJoiner.add(String.format("%.1f ", Double.valueOf(this.mSlaveWifiInfo.getLostTxPacketsPerSecond())));
            stringJoiner.add(String.format("rx=%.1f", Double.valueOf(this.mSlaveWifiInfo.getSuccessfulRxPacketsPerSecond())));
        }
        return stringJoiner.toString();
    }

    public int getWifiStandard() {
        return (getConnectedInfo() == null || this.mWifiInfo == null || getConnectedState() == 0) ? (this.mSlaveConnectedInfo == null || this.mSlaveWifiInfo == null || getSlaveConnectedState() == 0) ? this.mWifiStandard : this.mSlaveWifiInfo.getWifiStandard() : this.mWifiInfo.getWifiStandard();
    }

    public boolean hasInternetAccess() {
        return this.mIsValidated;
    }

    public boolean isAutoJoinEnabled() {
        return false;
    }

    public boolean isDefaultNetwork() {
        return this.mIsDefaultNetwork;
    }

    public boolean isMetered() {
        return false;
    }

    public boolean isOnly24Ghz() {
        return false;
    }

    public boolean isOnly5Ghz() {
        return false;
    }

    public boolean isPasspointR1() {
        return false;
    }

    public boolean isSaved() {
        return false;
    }

    public boolean isSubscription() {
        return false;
    }

    public boolean isSuggestion() {
        return false;
    }

    public void manageSubscription() {
        this.mManageSubscriptionAction.ifPresent(new Consumer() { // from class: com.android.wifitrackerlib.WifiEntry$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WifiEntry.ManageSubscriptionAction) obj).onExecute();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyOnUpdated() {
        if (this.mListener != null) {
            this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.WifiEntry$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    WifiEntry.this.lambda$notifyOnUpdated$0();
                }
            });
        }
    }

    public void setAutoJoinEnabled(boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setIsDefaultNetwork(boolean z) {
        this.mIsDefaultNetwork = z;
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setIsLowQuality(boolean z) {
        this.mIsLowQuality = z;
    }

    public synchronized void setListener(WifiEntryCallback wifiEntryCallback) {
        this.mListener = wifiEntryCallback;
    }

    public void setMeteredChoice(int i) {
    }

    public void setPrivacy(int i) {
    }

    public boolean shouldEditBeforeConnect() {
        return false;
    }

    public boolean shouldShowXLevelIcon() {
        return (getConnectedState() == 0 || (this.mIsValidated && this.mIsDefaultNetwork) || canSignIn()) ? false : true;
    }

    public void signIn(SignInCallback signInCallback) {
    }

    public void slaveSignIn(SignInCallback signInCallback) {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getKey());
        sb.append(",title:");
        sb.append(getTitle());
        sb.append(",summary:");
        sb.append(getSummary());
        sb.append(",isSaved:");
        sb.append(isSaved());
        sb.append(",isSubscription:");
        sb.append(isSubscription());
        sb.append(",isSuggestion:");
        sb.append(isSuggestion());
        sb.append(",level:");
        sb.append(getLevel());
        sb.append(shouldShowXLevelIcon() ? "X" : "");
        sb.append(",security:");
        sb.append(getSecurityTypes());
        sb.append(",connected:");
        sb.append(getConnectedState() == 2 ? "true" : "false");
        sb.append(",connectedInfo:");
        sb.append(getConnectedInfo());
        sb.append(",isValidated:");
        sb.append(this.mIsValidated);
        sb.append(",isDefaultNetwork:");
        sb.append(this.mIsDefaultNetwork);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo != null && networkInfo != null) {
            if (connectionInfoMatches(wifiInfo, networkInfo)) {
                this.mWifiInfo = wifiInfo;
                this.mNetworkInfo = networkInfo;
                int rssi = wifiInfo.getRssi();
                if (rssi != -127) {
                    this.mLevel = WifiEntryUtilsStub.miuiCalculateSignalLevel(rssi, this.mWifiManager);
                    this.mSpeed = Utils.getSpeedFromWifiInfo(this.mScoreCache, wifiInfo);
                }
                if (getConnectedState() == 2) {
                    if (this.mCalledConnect) {
                        this.mCalledConnect = false;
                        this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.WifiEntry$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                WifiEntry.this.lambda$updateConnectionInfo$1();
                            }
                        });
                    }
                    if (this.mConnectedInfo == null) {
                        this.mConnectedInfo = new ConnectedInfo();
                    }
                    this.mConnectedInfo.frequencyMhz = wifiInfo.getFrequency();
                    this.mConnectedInfo.linkSpeedMbps = wifiInfo.getLinkSpeed();
                    this.mConnectedInfo.wifiStandard = wifiInfo.getWifiStandard();
                }
                if (getConnectedState() == 2) {
                    if (this.mConnectedTimeStamp == 0) {
                        this.mConnectedTimeStamp = System.currentTimeMillis();
                    }
                } else if (getConnectedState() == 1) {
                    this.mConnectedTimeStamp = 0L;
                } else if (getConnectedState() == 0) {
                    this.mConnectedTimeStamp = -1L;
                }
                updateSecurityTypes();
                notifyOnUpdated();
            }
        }
        this.mWifiInfo = null;
        this.mNetworkInfo = null;
        this.mNetworkCapabilities = null;
        this.mConnectedInfo = null;
        this.mIsValidated = false;
        this.mIsDefaultNetwork = false;
        this.mIsLowQuality = false;
        if (this.mCalledDisconnect) {
            this.mCalledDisconnect = false;
            this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.WifiEntry$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WifiEntry.this.lambda$updateConnectionInfo$2();
                }
            });
        }
        updateSecurityTypes();
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateLinkProperties(LinkProperties linkProperties) {
        if (linkProperties != null) {
            if (getConnectedState() == 2) {
                if (this.mConnectedInfo == null) {
                    this.mConnectedInfo = new ConnectedInfo();
                }
                ArrayList arrayList = new ArrayList();
                for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
                    if (linkAddress.getAddress() instanceof Inet4Address) {
                        this.mConnectedInfo.ipAddress = linkAddress.getAddress().getHostAddress();
                        try {
                            this.mConnectedInfo.subnetMask = NetUtils.getNetworkPart(InetAddress.getByAddress(new byte[]{-1, -1, -1, -1}), linkAddress.getPrefixLength()).getHostAddress();
                        } catch (UnknownHostException unused) {
                        }
                    } else if (linkAddress.getAddress() instanceof Inet6Address) {
                        arrayList.add(linkAddress.getAddress().getHostAddress());
                    }
                }
                this.mConnectedInfo.ipv6Addresses = arrayList;
                Iterator<RouteInfo> it = linkProperties.getRoutes().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    RouteInfo next = it.next();
                    if (next.isDefaultRoute() && (next.getDestination().getAddress() instanceof Inet4Address) && next.hasGateway()) {
                        this.mConnectedInfo.gateway = next.getGateway().getHostAddress();
                        break;
                    }
                }
                this.mConnectedInfo.dnsServers = (List) linkProperties.getDnsServers().stream().map(WifiDetailPreferenceController2$$ExternalSyntheticLambda8.INSTANCE).collect(Collectors.toList());
                notifyOnUpdated();
                return;
            }
        }
        this.mConnectedInfo = null;
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        this.mNetworkCapabilities = networkCapabilities;
        if (this.mConnectedInfo == null) {
            return;
        }
        this.mIsValidated = networkCapabilities != null && networkCapabilities.hasCapability(16);
        notifyOnUpdated();
    }

    protected void updateSecurityTypes() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSlaveConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo == null || networkInfo == null || !connectionInfoMatches(wifiInfo, networkInfo)) {
            this.mSlaveNetworkInfo = null;
            this.mSlaveNetworkCapabilities = null;
            this.mSlaveConnectedInfo = null;
            this.mSlaveIsValidated = false;
            this.mSlaveIsDefaultNetwork = false;
            this.mSlaveIsLowQuality = false;
        } else {
            this.mSlaveWifiInfo = wifiInfo;
            this.mSlaveNetworkInfo = networkInfo;
            int rssi = wifiInfo.getRssi();
            if (rssi != -127) {
                this.mLevel = WifiEntryUtilsStub.miuiCalculateSignalLevel(rssi, this.mWifiManager);
                this.mSpeed = Utils.getSpeedFromWifiInfo(this.mScoreCache, wifiInfo);
            }
            if (getSlaveConnectedState() == 2) {
                if (this.mSlaveConnectedInfo == null) {
                    this.mSlaveConnectedInfo = new ConnectedInfo();
                }
                this.mSlaveConnectedInfo.frequencyMhz = wifiInfo.getFrequency();
                this.mSlaveConnectedInfo.linkSpeedMbps = wifiInfo.getLinkSpeed();
                this.mSlaveConnectedInfo.wifiStandard = wifiInfo.getWifiStandard();
            }
            if (getSlaveConnectedState() == 2) {
                if (this.mSlaveConnectedTimeStamp == 0) {
                    this.mSlaveConnectedTimeStamp = System.currentTimeMillis();
                }
            } else if (getSlaveConnectedState() == 1) {
                this.mSlaveConnectedTimeStamp = 0L;
            } else if (getConnectedState() == 0) {
                this.mSlaveConnectedTimeStamp = -1L;
            }
        }
        updateSecurityTypes();
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSlaveLinkProperties(LinkProperties linkProperties) {
        if (linkProperties == null || getSlaveConnectedState() != 2) {
            this.mSlaveConnectedInfo = null;
            notifyOnUpdated();
            return;
        }
        if (this.mSlaveConnectedInfo == null) {
            this.mSlaveConnectedInfo = new ConnectedInfo();
        }
        SlaveWifiUtilsStub.initSlaveConnectedInfo(linkProperties, this.mSlaveConnectedInfo);
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSlaveNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        this.mSlaveNetworkCapabilities = networkCapabilities;
        if (this.mConnectedInfo == null) {
            return;
        }
        this.mSlaveIsValidated = networkCapabilities != null && networkCapabilities.hasCapability(16);
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateWifiGenerationInfo(List<ScanResult> list) {
        int i = this.mDeviceWifiStandard;
        if (list == null || list.isEmpty()) {
            i = this.mLastMinConnectionCapability;
        } else {
            this.mLastMinConnectionCapability = 1;
            Iterator<ScanResult> it = list.iterator();
            while (it.hasNext()) {
                int wifiStandard = it.next().getWifiStandard();
                if (wifiStandard < i) {
                    i = wifiStandard;
                }
            }
            this.mLastMinConnectionCapability = i;
        }
        this.mWifiStandard = i;
    }
}
