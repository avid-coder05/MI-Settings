package com.android.settingslib.wifi;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.net.ScoredNetwork;
import android.net.wifi.MiuiWifiManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.PasspointR1Provider;
import android.net.wifi.hotspot2.ProvisioningCallback;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.CollectionUtils;
import com.android.settingslib.R$array;
import com.android.settingslib.R$string;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiPasspointProvision;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import miui.content.res.ThemeResources;
import miui.vip.VipService;

@Deprecated
/* loaded from: classes2.dex */
public class AccessPoint implements Comparable<AccessPoint> {
    static final AtomicInteger sLastId = new AtomicInteger(0);
    private String bssid;
    AccessPointListener mAccessPointListener;
    private WifiConfiguration mConfig;
    private WifiManager.ActionListener mConnectListener;
    private final Context mContext;
    private int mDeviceWifiStandard;
    private int mEapType;
    private final ArraySet<ScanResult> mExtraScanResults;
    private String mFqdn;
    private boolean mHe8ssCapableAp;
    private WifiInfo mInfo;
    private boolean mIsOpenOweCoexist;
    private boolean mIsOweTransitionMode;
    private boolean mIsPskSaeTransitionMode;
    private boolean mIsRoaming;
    private boolean mIsScoredNetworkMetered;
    private boolean mIsWpa2Wpa3Coexist;
    private String mKey;
    private final Object mLock;
    private NetworkInfo mNetworkInfo;
    private String mOsuFailure;
    private OsuProvider mOsuProvider;
    private boolean mOsuProvisioningComplete;
    private String mOsuStatus;
    private int mPasspointConfigurationVersion;
    private String mPasspointR1Failure;
    private PasspointR1Provider mPasspointR1Provider;
    private boolean mPasspointR1ProvisioningComplete;
    private String mPasspointR1Status;
    private String mPasspointUniqueId;
    private String mProviderFriendlyName;
    private int mRssi;
    public ScanResult mScanResult;
    private final ArraySet<ScanResult> mScanResults;
    private final Object mScanResultsLock;
    private final Map<String, TimestampedScoredNetwork> mScoredNetworkCache;
    public boolean mShowPassword;
    private WifiInfo mSlaveInfo;
    private NetworkInfo mSlaveNetworkInfo;
    private int mSpeed;
    private long mSubscriptionExpirationTimeInMillis;
    private Object mTag;
    private boolean mVhtMax8SpatialStreamsSupport;
    private WifiManager mWifiManager;
    private int mWifiStandard;
    public int networkId;
    private int pskType;
    private int security;
    private String ssid;

    /* loaded from: classes2.dex */
    public interface AccessPointListener {
        void onAccessPointChanged(AccessPoint accessPoint);

        void onLevelChanged(AccessPoint accessPoint);
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    class AccessPointPasspointR1ProvisioningCallback extends WifiPasspointProvision.PasspointR1ProvisioningCallback {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes2.dex */
    public class AccessPointProvisioningCallback extends ProvisioningCallback {
        final /* synthetic */ AccessPoint this$0;

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProvisioningComplete$2() {
            AccessPoint accessPoint = this.this$0;
            AccessPointListener accessPointListener = accessPoint.mAccessPointListener;
            if (accessPointListener != null) {
                accessPointListener.onAccessPointChanged(accessPoint);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProvisioningFailure$0() {
            AccessPoint accessPoint = this.this$0;
            AccessPointListener accessPointListener = accessPoint.mAccessPointListener;
            if (accessPointListener != null) {
                accessPointListener.onAccessPointChanged(accessPoint);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProvisioningStatus$1() {
            AccessPoint accessPoint = this.this$0;
            AccessPointListener accessPointListener = accessPoint.mAccessPointListener;
            if (accessPointListener != null) {
                accessPointListener.onAccessPointChanged(accessPoint);
            }
        }

        public void onProvisioningComplete() {
            this.this$0.mOsuProvisioningComplete = true;
            this.this$0.mOsuFailure = null;
            this.this$0.mOsuStatus = null;
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.AccessPoint$AccessPointProvisioningCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AccessPoint.AccessPointProvisioningCallback.this.lambda$onProvisioningComplete$2();
                }
            });
            WifiManager wifiManager = this.this$0.getWifiManager();
            PasspointConfiguration passpointConfiguration = (PasspointConfiguration) wifiManager.getMatchingPasspointConfigsForOsuProviders(Collections.singleton(this.this$0.mOsuProvider)).get(this.this$0.mOsuProvider);
            if (passpointConfiguration == null) {
                Log.e("SettingsLib.AccessPoint", "Missing PasspointConfiguration for newly provisioned network!");
                if (this.this$0.mConnectListener != null) {
                    this.this$0.mConnectListener.onFailure(0);
                    return;
                }
                return;
            }
            String uniqueId = passpointConfiguration.getUniqueId();
            for (Pair pair : wifiManager.getAllMatchingWifiConfigs(wifiManager.getScanResults())) {
                WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
                if (TextUtils.equals(wifiConfiguration.getKey(), uniqueId)) {
                    wifiManager.connect(new AccessPoint(this.this$0.mContext, wifiConfiguration, (List) ((Map) pair.second).get(0), (List) ((Map) pair.second).get(1)).getConfig(), this.this$0.mConnectListener);
                    return;
                }
            }
            if (this.this$0.mConnectListener != null) {
                this.this$0.mConnectListener.onFailure(0);
            }
        }

        public void onProvisioningFailure(int i) {
            if (TextUtils.equals(this.this$0.mOsuStatus, this.this$0.mContext.getString(R$string.osu_completing_sign_up))) {
                AccessPoint accessPoint = this.this$0;
                accessPoint.mOsuFailure = accessPoint.mContext.getString(R$string.osu_sign_up_failed);
            } else {
                AccessPoint accessPoint2 = this.this$0;
                accessPoint2.mOsuFailure = accessPoint2.mContext.getString(R$string.osu_connect_failed);
            }
            this.this$0.mOsuStatus = null;
            this.this$0.mOsuProvisioningComplete = false;
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.AccessPoint$AccessPointProvisioningCallback$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AccessPoint.AccessPointProvisioningCallback.this.lambda$onProvisioningFailure$0();
                }
            });
        }

        public void onProvisioningStatus(int i) {
            String format;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    format = String.format(this.this$0.mContext.getString(R$string.osu_opening_provider), this.this$0.mOsuProvider.getFriendlyName());
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                    format = this.this$0.mContext.getString(R$string.osu_completing_sign_up);
                    break;
                default:
                    format = null;
                    break;
            }
            boolean equals = true ^ TextUtils.equals(this.this$0.mOsuStatus, format);
            this.this$0.mOsuStatus = format;
            this.this$0.mOsuFailure = null;
            this.this$0.mOsuProvisioningComplete = false;
            if (equals) {
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.AccessPoint$AccessPointProvisioningCallback$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccessPoint.AccessPointProvisioningCallback.this.lambda$onProvisioningStatus$1();
                    }
                });
            }
        }
    }

    public AccessPoint(Context context, WifiConfiguration wifiConfiguration) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mEapType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mWifiStandard = 1;
        this.mScanResultsLock = new Object();
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mIsRoaming = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mPasspointR1ProvisioningComplete = false;
        this.mContext = context;
        loadConfig(wifiConfiguration);
        updateKey();
        updateDeviceWifiGenerationInfo();
    }

    public AccessPoint(Context context, WifiConfiguration wifiConfiguration, Collection<ScanResult> collection, Collection<ScanResult> collection2) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mEapType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mWifiStandard = 1;
        this.mScanResultsLock = new Object();
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mIsRoaming = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mPasspointR1ProvisioningComplete = false;
        this.mContext = context;
        this.networkId = wifiConfiguration.networkId;
        this.mConfig = wifiConfiguration;
        this.mPasspointUniqueId = wifiConfiguration.getKey();
        this.mFqdn = wifiConfiguration.FQDN;
        updateDeviceWifiGenerationInfo();
        setScanResultsPasspoint(collection, collection2);
        updateKey();
    }

    public AccessPoint(Context context, OsuProvider osuProvider, Collection<ScanResult> collection) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mEapType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mWifiStandard = 1;
        this.mScanResultsLock = new Object();
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mIsRoaming = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mPasspointR1ProvisioningComplete = false;
        this.mContext = context;
        this.mOsuProvider = osuProvider;
        updateDeviceWifiGenerationInfo();
        setScanResults(collection);
        updateKey();
    }

    public AccessPoint(Context context, PasspointConfiguration passpointConfiguration) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mEapType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mWifiStandard = 1;
        this.mScanResultsLock = new Object();
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mIsRoaming = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mPasspointR1ProvisioningComplete = false;
        this.mContext = context;
        this.mPasspointUniqueId = passpointConfiguration.getUniqueId();
        this.mFqdn = passpointConfiguration.getHomeSp().getFqdn();
        this.mProviderFriendlyName = passpointConfiguration.getHomeSp().getFriendlyName();
        this.mSubscriptionExpirationTimeInMillis = passpointConfiguration.getSubscriptionExpirationTimeMillis();
        if (passpointConfiguration.isOsuProvisioned()) {
            this.mPasspointConfigurationVersion = 2;
        } else {
            this.mPasspointConfigurationVersion = 1;
        }
        updateKey();
        updateDeviceWifiGenerationInfo();
    }

    public AccessPoint(Context context, PasspointR1Provider passpointR1Provider, Collection<ScanResult> collection) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mEapType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mWifiStandard = 1;
        this.mScanResultsLock = new Object();
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mIsRoaming = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mPasspointR1ProvisioningComplete = false;
        this.mContext = context;
        this.mPasspointR1Provider = passpointR1Provider;
        setScanResults(collection);
        updateKey();
    }

    public AccessPoint(Context context, Bundle bundle) {
        this.mLock = new Object();
        ArraySet<ScanResult> arraySet = new ArraySet<>();
        this.mScanResults = arraySet;
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mEapType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mWifiStandard = 1;
        this.mScanResultsLock = new Object();
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mIsRoaming = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mPasspointR1ProvisioningComplete = false;
        this.mContext = context;
        if (bundle.containsKey("key_config")) {
            this.mConfig = (WifiConfiguration) bundle.getParcelable("key_config");
        }
        WifiConfiguration wifiConfiguration = this.mConfig;
        if (wifiConfiguration != null) {
            loadConfig(wifiConfiguration);
        }
        this.mShowPassword = bundle.getBoolean("key_show_password");
        if (bundle.containsKey("key_ssid")) {
            this.ssid = bundle.getString("key_ssid");
        }
        if (bundle.containsKey("key_security")) {
            this.security = bundle.getInt("key_security");
        }
        if (bundle.containsKey("key_speed")) {
            this.mSpeed = bundle.getInt("key_speed");
        }
        if (bundle.containsKey("key_psktype")) {
            this.pskType = bundle.getInt("key_psktype");
        }
        if (bundle.containsKey("eap_psktype")) {
            this.mEapType = bundle.getInt("eap_psktype");
        }
        this.mInfo = (WifiInfo) bundle.getParcelable("key_wifiinfo");
        if (bundle.containsKey("key_networkinfo")) {
            this.mNetworkInfo = (NetworkInfo) bundle.getParcelable("key_networkinfo");
        }
        this.mSlaveInfo = (WifiInfo) bundle.getParcelable("key_slave_wifiinfo");
        if (bundle.containsKey("key_slave_networkinfo")) {
            this.mSlaveNetworkInfo = (NetworkInfo) bundle.getParcelable("key_slave_networkinfo");
        }
        if (bundle.containsKey("key_scanresults")) {
            Parcelable[] parcelableArray = bundle.getParcelableArray("key_scanresults");
            arraySet.clear();
            for (Parcelable parcelable : parcelableArray) {
                this.mScanResults.add((ScanResult) parcelable);
            }
        }
        if (bundle.containsKey("key_scorednetworkcache")) {
            Iterator it = bundle.getParcelableArrayList("key_scorednetworkcache").iterator();
            while (it.hasNext()) {
                TimestampedScoredNetwork timestampedScoredNetwork = (TimestampedScoredNetwork) it.next();
                this.mScoredNetworkCache.put(timestampedScoredNetwork.getScore().networkKey.wifiKey.bssid, timestampedScoredNetwork);
            }
        }
        if (bundle.containsKey("key_passpoint_unique_id")) {
            this.mPasspointUniqueId = bundle.getString("key_passpoint_unique_id");
        }
        if (bundle.containsKey("key_fqdn")) {
            this.mFqdn = bundle.getString("key_fqdn");
        }
        if (bundle.containsKey("key_provider_friendly_name")) {
            this.mProviderFriendlyName = bundle.getString("key_provider_friendly_name");
        }
        if (bundle.containsKey("key_subscription_expiration_time_in_millis")) {
            this.mSubscriptionExpirationTimeInMillis = bundle.getLong("key_subscription_expiration_time_in_millis");
        }
        if (bundle.containsKey("key_passpoint_configuration_version")) {
            this.mPasspointConfigurationVersion = bundle.getInt("key_passpoint_configuration_version");
        }
        if (!this.mScanResults.isEmpty()) {
            this.mScanResult = this.mScanResults.iterator().next();
        }
        if (bundle.containsKey("key_is_psk_sae_transition_mode")) {
            this.mIsPskSaeTransitionMode = bundle.getBoolean("key_is_psk_sae_transition_mode");
        }
        if (bundle.containsKey("key_is_owe_transition_mode")) {
            this.mIsOweTransitionMode = bundle.getBoolean("key_is_owe_transition_mode");
        }
        update(this.mConfig, this.mInfo, this.mNetworkInfo);
        updateSlave(this.mConfig, this.mSlaveInfo, this.mSlaveNetworkInfo);
        updateKey();
        updateBestRssiInfo();
        updateDeviceWifiGenerationInfo();
        updateWifiGeneration();
    }

    public AccessPoint(Context context, Collection<ScanResult> collection) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mEapType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mWifiStandard = 1;
        this.mScanResultsLock = new Object();
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mIsRoaming = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mPasspointR1ProvisioningComplete = false;
        this.mContext = context;
        updateDeviceWifiGenerationInfo();
        setScanResults(collection);
        updateKey();
    }

    public static String convertToQuotedString(String str) {
        return "\"" + str + "\"";
    }

    private int generateAverageSpeedForSsid() {
        if (this.mScoredNetworkCache.isEmpty()) {
            return 0;
        }
        if (Log.isLoggable("SettingsLib.AccessPoint", 3)) {
            Log.d("SettingsLib.AccessPoint", String.format("Generating fallbackspeed for %s using cache: %s", getSsidStr(), this.mScoredNetworkCache));
        }
        Iterator<TimestampedScoredNetwork> it = this.mScoredNetworkCache.values().iterator();
        int i = 0;
        int i2 = 0;
        while (it.hasNext()) {
            int calculateBadge = it.next().getScore().calculateBadge(this.mRssi);
            if (calculateBadge != 0) {
                i++;
                i2 += calculateBadge;
            }
        }
        int i3 = i == 0 ? 0 : i2 / i;
        if (isVerboseLoggingEnabled()) {
            Log.i("SettingsLib.AccessPoint", String.format("%s generated fallback speed is: %d", getSsidStr(), Integer.valueOf(i3)));
        }
        return roundToClosestSpeedEnum(i3);
    }

    private static CharSequence getAppLabel(String str, PackageManager packageManager) {
        try {
            ApplicationInfo applicationInfoAsUser = packageManager.getApplicationInfoAsUser(str, 0, UserHandle.getUserId(-2));
            return applicationInfoAsUser != null ? applicationInfoAsUser.loadLabel(packageManager) : "";
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("SettingsLib.AccessPoint", "Failed to get app info", e);
            return "";
        }
    }

    private static int getEapType(ScanResult scanResult) {
        if (scanResult.capabilities.contains("RSN-EAP")) {
            return 2;
        }
        return scanResult.capabilities.contains("WPA-EAP") ? 1 : 0;
    }

    public static String getKey(Context context, ScanResult scanResult) {
        return getKey(scanResult.SSID, scanResult.BSSID, getSecurity(context, scanResult));
    }

    public static String getKey(WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.isPasspoint() ? getKey(wifiConfiguration.getKey()) : getKey(removeDoubleQuotes(wifiConfiguration.SSID), wifiConfiguration.BSSID, getSecurity(wifiConfiguration));
    }

    public static String getKey(OsuProvider osuProvider) {
        return "OSU:" + osuProvider.getFriendlyName() + ',' + osuProvider.getServerUri();
    }

    public static String getKey(PasspointR1Provider passpointR1Provider) {
        return "PASSPOINT1:" + passpointR1Provider.getDomainName();
    }

    public static String getKey(String str) {
        return "PASSPOINT:" + str;
    }

    private static String getKey(String str, String str2, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("AP:");
        if (TextUtils.isEmpty(str)) {
            sb.append(str2);
        } else {
            sb.append(str);
        }
        sb.append(',');
        sb.append(i);
        return sb.toString();
    }

    private static int getPskType(ScanResult scanResult) {
        boolean contains = scanResult.capabilities.contains("WPA-PSK");
        boolean contains2 = scanResult.capabilities.contains("RSN-PSK");
        boolean contains3 = scanResult.capabilities.contains("RSN-SAE");
        if (contains2 && contains) {
            return 3;
        }
        if (contains2) {
            return 2;
        }
        if (contains) {
            return 1;
        }
        if (contains3) {
            return 0;
        }
        Log.w("SettingsLib.AccessPoint", "Received abnormal flag string: " + scanResult.capabilities);
        return 0;
    }

    public static int getSecurity(Context context, ScanResult scanResult) {
        boolean contains = scanResult.capabilities.contains("WEP");
        boolean contains2 = scanResult.capabilities.contains("SAE");
        boolean contains3 = scanResult.capabilities.contains("PSK");
        boolean contains4 = scanResult.capabilities.contains("EAP_SUITE_B_192");
        boolean contains5 = scanResult.capabilities.contains("EAP");
        boolean contains6 = scanResult.capabilities.contains("OWE");
        boolean contains7 = scanResult.capabilities.contains("OWE_TRANSITION");
        scanResult.capabilities.contains("DPP");
        boolean z = scanResult.capabilities.contains("WAPI-PSK") || scanResult.capabilities.contains("WAPI-KEY");
        boolean contains8 = scanResult.capabilities.contains("WAPI-CERT");
        if (contains2 && contains3) {
            return ((WifiManager) context.getSystemService("wifi")).isWpa3SaeSupported() ? 5 : 2;
        } else if (contains7) {
            return ((WifiManager) context.getSystemService("wifi")).isEnhancedOpenSupported() ? 4 : 0;
        } else if (contains) {
            return 1;
        } else {
            if (contains2) {
                return 5;
            }
            if (z) {
                return 11;
            }
            if (contains3) {
                return 2;
            }
            if (contains4) {
                return 6;
            }
            if (contains5) {
                return 3;
            }
            if (contains6) {
                return 4;
            }
            return contains8 ? 12 : 0;
        }
    }

    public static int getSecurity(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(8)) {
            return 5;
        }
        if (wifiConfiguration.allowedKeyManagement.get(1)) {
            return 2;
        }
        if (wifiConfiguration.allowedKeyManagement.get(10)) {
            return 6;
        }
        if (wifiConfiguration.allowedKeyManagement.get(2) || wifiConfiguration.allowedKeyManagement.get(3)) {
            return 3;
        }
        if (wifiConfiguration.allowedKeyManagement.get(9)) {
            return 4;
        }
        if (wifiConfiguration.allowedKeyManagement.get(13)) {
            return 11;
        }
        if (wifiConfiguration.allowedKeyManagement.get(14)) {
            return 12;
        }
        int i = wifiConfiguration.wepTxKeyIndex;
        if (i >= 0) {
            String[] strArr = wifiConfiguration.wepKeys;
            if (i < strArr.length && strArr[i] != null) {
                return 1;
            }
        }
        return 0;
    }

    public static String getSlaveSummary(Context context, String str, NetworkInfo.DetailedState detailedState, boolean z, String str2) {
        NetworkCapabilities networkCapabilities;
        if (detailedState == NetworkInfo.DetailedState.CONNECTED) {
            if (z && !TextUtils.isEmpty(str2)) {
                return context.getString(R$string.connected_via_app, getAppLabel(str2, context.getPackageManager()));
            }
            if (z) {
                NetworkScorerAppData activeScorer = ((NetworkScoreManager) context.getSystemService(NetworkScoreManager.class)).getActiveScorer();
                return (activeScorer == null || activeScorer.getRecommendationServiceLabel() == null) ? context.getString(R$string.connected_via_network_scorer_default) : String.format(context.getString(R$string.connected_via_network_scorer), activeScorer.getRecommendationServiceLabel());
            }
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (detailedState == NetworkInfo.DetailedState.CONNECTED && (networkCapabilities = connectivityManager.getNetworkCapabilities(new SlaveWifiUtils(context).getSlaveWifiCurrentNetwork())) != null) {
            if (networkCapabilities.hasCapability(17)) {
                return context.getString(context.getResources().getIdentifier("network_available_sign_in", "string", ThemeResources.FRAMEWORK_PACKAGE));
            }
            if (networkCapabilities.hasCapability(24)) {
                return getWifiConnected(context);
            }
            if (!networkCapabilities.hasCapability(16)) {
                return context.getString(R$string.wifi_connected_no_internet);
            }
        }
        if (detailedState == null) {
            Log.w("SettingsLib.AccessPoint", "state is null, returning empty summary");
            return "";
        }
        String[] stringArray = context.getResources().getStringArray(str == null ? R$array.wifi_status : R$array.wifi_status_with_ssid);
        int ordinal = detailedState.ordinal();
        return (ordinal >= stringArray.length || stringArray[ordinal].length() == 0) ? "" : String.format(stringArray[ordinal], str);
    }

    private static String getSpeedLabel(Context context, int i) {
        if (i != 5) {
            if (i != 10) {
                if (i != 20) {
                    if (i != 30) {
                        return null;
                    }
                    return context.getString(R$string.speed_label_very_fast);
                }
                return context.getString(R$string.speed_label_fast);
            }
            return context.getString(R$string.speed_label_okay);
        }
        return context.getString(R$string.speed_label_slow);
    }

    public static String getSpeedLabel(Context context, ScoredNetwork scoredNetwork, int i) {
        return getSpeedLabel(context, roundToClosestSpeedEnum(scoredNetwork.calculateBadge(i)));
    }

    public static String getSummary(Context context, String str, NetworkInfo.DetailedState detailedState, boolean z, String str2) {
        NetworkCapabilities networkCapabilities;
        if (detailedState == NetworkInfo.DetailedState.CONNECTED) {
            if (z && !TextUtils.isEmpty(str2)) {
                return context.getString(R$string.connected_via_app, getAppLabel(str2, context.getPackageManager()));
            }
            if (z) {
                NetworkScorerAppData activeScorer = ((NetworkScoreManager) context.getSystemService(NetworkScoreManager.class)).getActiveScorer();
                return (activeScorer == null || activeScorer.getRecommendationServiceLabel() == null) ? context.getString(R$string.connected_via_network_scorer_default) : String.format(context.getString(R$string.connected_via_network_scorer), activeScorer.getRecommendationServiceLabel());
            }
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (detailedState == NetworkInfo.DetailedState.CONNECTED && (networkCapabilities = connectivityManager.getNetworkCapabilities(((WifiManager) context.getSystemService(WifiManager.class)).getCurrentNetwork())) != null) {
            if (networkCapabilities.hasCapability(17)) {
                return context.getString(context.getResources().getIdentifier("network_available_sign_in", "string", ThemeResources.FRAMEWORK_PACKAGE));
            }
            if (networkCapabilities.hasCapability(24)) {
                return getWifiConnected(context);
            }
            if (!networkCapabilities.hasCapability(16)) {
                Settings.Global.getString(context.getContentResolver(), "private_dns_mode");
                return networkCapabilities.isPrivateDnsBroken() ? context.getString(R$string.private_dns_broken) : context.getString(R$string.wifitrackerlib_wifi_connected_cannot_provide_internet);
            }
        }
        if (detailedState == null) {
            Log.w("SettingsLib.AccessPoint", "state is null, returning empty summary");
            return "";
        }
        String[] stringArray = context.getResources().getStringArray(str == null ? R$array.wifi_status : R$array.wifi_status_with_ssid);
        int ordinal = detailedState.ordinal();
        return (ordinal >= stringArray.length || stringArray[ordinal].length() == 0) ? "" : String.format(stringArray[ordinal], str);
    }

    private static String getWifiConnected(Context context) {
        return context.getResources().getStringArray(R$array.wifi_status)[NetworkInfo.DetailedState.CONNECTED.ordinal()];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public WifiManager getWifiManager() {
        if (this.mWifiManager == null) {
            this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        }
        return this.mWifiManager;
    }

    private boolean isInfoForThisAccessPoint(WifiConfiguration wifiConfiguration, WifiInfo wifiInfo) {
        return (wifiInfo.isOsuAp() || this.mOsuStatus != null) ? wifiInfo.isOsuAp() && this.mOsuStatus != null : (wifiInfo.isPasspointAp() || isPasspoint()) ? wifiInfo.isPasspointAp() && isPasspoint() && TextUtils.equals(wifiInfo.getPasspointFqdn(), this.mConfig.FQDN) && TextUtils.equals(wifiInfo.getPasspointProviderFriendlyName(), this.mConfig.providerFriendlyName) : wifiConfiguration != null ? matches(wifiConfiguration, wifiInfo) : TextUtils.equals(removeDoubleQuotes(wifiInfo.getSSID()), this.ssid);
    }

    private boolean isOpenOweCoexist(Collection<ScanResult> collection) {
        Iterator<ScanResult> it = collection.iterator();
        boolean z = false;
        boolean z2 = false;
        while (it.hasNext()) {
            int security = getSecurity(this.mContext, it.next());
            if (security == 0) {
                z = true;
            }
            if (4 == security) {
                z2 = true;
            }
            if (z && z2) {
                return true;
            }
            if (!z && !z2) {
                return false;
            }
        }
        return z && z2;
    }

    private static boolean isOweTransitionMode(ScanResult scanResult) {
        return scanResult.capabilities.contains("OWE_TRANSITION");
    }

    private static boolean isPskSaeTransitionMode(ScanResult scanResult) {
        return scanResult.capabilities.contains("PSK") && scanResult.capabilities.contains("SAE");
    }

    private boolean isSameSsidOrBssid(ScanResult scanResult) {
        if (scanResult == null) {
            return false;
        }
        if (TextUtils.equals(this.ssid, scanResult.SSID)) {
            return true;
        }
        String str = scanResult.BSSID;
        return str != null && TextUtils.equals(this.bssid, str);
    }

    private boolean isSameSsidOrBssid(WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return false;
        }
        if (TextUtils.equals(this.ssid, removeDoubleQuotes(wifiInfo.getSSID()))) {
            return true;
        }
        return wifiInfo.getBSSID() != null && TextUtils.equals(this.bssid, wifiInfo.getBSSID());
    }

    public static boolean isVerboseLoggingEnabled() {
        return WifiTracker.sVerboseLogging || Log.isLoggable("SettingsLib.AccessPoint", 2);
    }

    private boolean isWpa2Wpa3Coexist(Collection<ScanResult> collection) {
        Iterator<ScanResult> it = collection.iterator();
        boolean z = false;
        boolean z2 = false;
        while (it.hasNext()) {
            int security = getSecurity(this.mContext, it.next());
            if (5 == security) {
                z2 = true;
            }
            if (2 == security) {
                z = true;
            }
            if (z2 && z) {
                return true;
            }
            if (!z2 && !z) {
                return false;
            }
        }
        return z && z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setScanResults$1() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onLevelChanged(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setScanResults$2() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onAccessPointChanged(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$3() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onAccessPointChanged(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$4() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onLevelChanged(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$7() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onAccessPointChanged(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateScores$0(long j, Iterator it, TimestampedScoredNetwork timestampedScoredNetwork) {
        if (timestampedScoredNetwork.getUpdatedTimestampMillis() < j) {
            it.remove();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSlave$5() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onAccessPointChanged(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSlave$6() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onLevelChanged(this);
        }
    }

    private boolean matches(WifiConfiguration wifiConfiguration, WifiInfo wifiInfo) {
        if (wifiConfiguration == null || wifiInfo == null) {
            return false;
        }
        if (wifiConfiguration.isPasspoint() || isSameSsidOrBssid(wifiInfo)) {
            return matches(wifiConfiguration);
        }
        return false;
    }

    public static String removeDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int length = str.length();
        if (length <= 1 || str.charAt(0) != '\"') {
            return str;
        }
        int i = length - 1;
        return str.charAt(i) == '\"' ? str.substring(1, i) : str;
    }

    private static int roundToClosestSpeedEnum(int i) {
        if (i < 5) {
            return 0;
        }
        if (i < 7) {
            return 5;
        }
        if (i < 15) {
            return 10;
        }
        return i < 25 ? 20 : 30;
    }

    public static String securityToString(int i, int i2) {
        return i == 1 ? "WEP" : i == 2 ? i2 == 1 ? "WPA" : i2 == 2 ? "WPA2" : i2 == 3 ? "WPA_WPA2" : "PSK" : i == 3 ? "EAP" : i == 10 ? "DPP" : i == 5 ? "SAE" : i == 6 ? "SUITE_B" : i == 4 ? "OWE" : "NONE";
    }

    private void updateBestRssiInfo() {
        int i;
        int i2;
        if (isActive()) {
            return;
        }
        ScanResult scanResult = null;
        synchronized (this.mLock) {
            Iterator<ScanResult> it = this.mScanResults.iterator();
            i = Integer.MIN_VALUE;
            while (it.hasNext()) {
                ScanResult next = it.next();
                int i3 = next.level;
                if (i3 > i) {
                    scanResult = next;
                    i = i3;
                }
            }
        }
        if (i == Integer.MIN_VALUE || (i2 = this.mRssi) == Integer.MIN_VALUE) {
            this.mRssi = i;
        } else {
            this.mRssi = (i2 + i) / 2;
        }
        if (scanResult != null) {
            this.ssid = scanResult.SSID;
            this.bssid = scanResult.BSSID;
            int security = getSecurity(this.mContext, scanResult);
            this.security = security;
            if (security == 2 || security == 5) {
                this.pskType = getPskType(scanResult);
            }
            if (this.security == 3) {
                this.mEapType = getEapType(scanResult);
            }
            this.mIsPskSaeTransitionMode = isPskSaeTransitionMode(scanResult);
            this.mIsOweTransitionMode = isOweTransitionMode(scanResult);
        }
        if (isPasspoint()) {
            this.mConfig.SSID = convertToQuotedString(this.ssid);
        }
    }

    private void updateCapabilities(ScanResult scanResult) {
        if (scanResult == null) {
            return;
        }
        if (isActive() || isSlaveActive()) {
            this.pskType = getPskType(scanResult);
        }
    }

    private void updateDeviceWifiGenerationInfo() {
        WifiManager wifiManager = getWifiManager();
        if (wifiManager.isWifiStandardSupported(6)) {
            this.mDeviceWifiStandard = 6;
        } else if (wifiManager.isWifiStandardSupported(5)) {
            this.mDeviceWifiStandard = 5;
        } else if (wifiManager.isWifiStandardSupported(4)) {
            this.mDeviceWifiStandard = 4;
        } else {
            this.mDeviceWifiStandard = 1;
        }
        this.mVhtMax8SpatialStreamsSupport = Wifi6ApiCompatible.isVht8ssCapableDevice(wifiManager);
    }

    private void updateKey() {
        if (isPasspoint()) {
            this.mKey = getKey(this.mConfig);
        } else if (isPasspointConfig()) {
            this.mKey = getKey(this.mPasspointUniqueId);
        } else if (isOsuProvider()) {
            this.mKey = getKey(this.mOsuProvider);
        } else if (isPasspointR1Provider()) {
            this.mKey = getKey(this.mPasspointR1Provider);
        } else {
            this.mKey = getKey(getSsidStr(), getBssid(), getSecurity());
        }
    }

    private boolean updateMetered(WifiNetworkScoreCache wifiNetworkScoreCache) {
        WifiInfo wifiInfo;
        boolean z = this.mIsScoredNetworkMetered;
        this.mIsScoredNetworkMetered = false;
        if (!isActive() || (wifiInfo = this.mInfo) == null) {
            synchronized (this.mLock) {
                Iterator<ScanResult> it = this.mScanResults.iterator();
                while (it.hasNext()) {
                    ScoredNetwork scoredNetwork = wifiNetworkScoreCache.getScoredNetwork(it.next());
                    if (scoredNetwork != null) {
                        this.mIsScoredNetworkMetered = scoredNetwork.meteredHint | this.mIsScoredNetworkMetered;
                    }
                }
            }
        } else {
            ScoredNetwork scoredNetwork2 = wifiNetworkScoreCache.getScoredNetwork(NetworkKey.createFromWifiInfo(wifiInfo));
            if (scoredNetwork2 != null) {
                this.mIsScoredNetworkMetered = scoredNetwork2.meteredHint | this.mIsScoredNetworkMetered;
            }
        }
        return z != this.mIsScoredNetworkMetered;
    }

    private boolean updateScores(WifiNetworkScoreCache wifiNetworkScoreCache, long j) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        synchronized (this.mLock) {
            Iterator<ScanResult> it = this.mScanResults.iterator();
            while (it.hasNext()) {
                ScanResult next = it.next();
                ScoredNetwork scoredNetwork = wifiNetworkScoreCache.getScoredNetwork(next);
                if (scoredNetwork != null) {
                    TimestampedScoredNetwork timestampedScoredNetwork = this.mScoredNetworkCache.get(next.BSSID);
                    if (timestampedScoredNetwork == null) {
                        this.mScoredNetworkCache.put(next.BSSID, new TimestampedScoredNetwork(scoredNetwork, elapsedRealtime));
                    } else {
                        timestampedScoredNetwork.update(scoredNetwork, elapsedRealtime);
                    }
                }
            }
        }
        final long j2 = elapsedRealtime - j;
        final Iterator<TimestampedScoredNetwork> it2 = this.mScoredNetworkCache.values().iterator();
        it2.forEachRemaining(new Consumer() { // from class: com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AccessPoint.lambda$updateScores$0(j2, it2, (TimestampedScoredNetwork) obj);
            }
        });
        return updateSpeed();
    }

    private boolean updateSpeed() {
        int i = this.mSpeed;
        int generateAverageSpeedForSsid = generateAverageSpeedForSsid();
        this.mSpeed = generateAverageSpeedForSsid;
        boolean z = i != generateAverageSpeedForSsid;
        if (isVerboseLoggingEnabled() && z) {
            Log.i("SettingsLib.AccessPoint", String.format("%s: Set speed to %d", this.ssid, Integer.valueOf(this.mSpeed)));
        }
        return z;
    }

    private void updateWifiGeneration() {
        int i = this.mDeviceWifiStandard;
        this.mHe8ssCapableAp = true;
        Iterator<ScanResult> it = this.mScanResults.iterator();
        while (it.hasNext()) {
            ScanResult next = it.next();
            int wifiStandard = next.getWifiStandard();
            if (!next.capabilities.contains("WFA-HE-READY") && this.mHe8ssCapableAp) {
                this.mHe8ssCapableAp = false;
            }
            if (wifiStandard < i) {
                i = wifiStandard;
            }
        }
        this.mWifiStandard = i;
    }

    @Override // java.lang.Comparable
    public int compareTo(AccessPoint accessPoint) {
        if (!isActive() || accessPoint.isActive()) {
            if (isActive() || !accessPoint.isActive()) {
                if (!isSlaveActive() || accessPoint.isSlaveActive()) {
                    if (isSlaveActive() || !accessPoint.isSlaveActive()) {
                        if (!isReachable() || accessPoint.isReachable()) {
                            if (isReachable() || !accessPoint.isReachable()) {
                                if (!isSaved() || accessPoint.isSaved()) {
                                    if (isSaved() || !accessPoint.isSaved()) {
                                        if (getSpeed() != accessPoint.getSpeed()) {
                                            return accessPoint.getSpeed() - getSpeed();
                                        }
                                        getWifiManager();
                                        int calculateSignalLevel = MiuiWifiManager.calculateSignalLevel(accessPoint.mRssi, 5) - MiuiWifiManager.calculateSignalLevel(this.mRssi, 5);
                                        if (calculateSignalLevel != 0) {
                                            return calculateSignalLevel;
                                        }
                                        int compareToIgnoreCase = getTitle().compareToIgnoreCase(accessPoint.getTitle());
                                        return compareToIgnoreCase != 0 ? compareToIgnoreCase : getSsidStr().compareTo(accessPoint.getSsidStr());
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

    public boolean equals(Object obj) {
        return (obj instanceof AccessPoint) && compareTo((AccessPoint) obj) == 0;
    }

    public String getBssid() {
        return this.bssid;
    }

    public WifiConfiguration getConfig() {
        return this.mConfig;
    }

    public NetworkInfo.DetailedState getDetailedState() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        if (networkInfo != null) {
            return networkInfo.getDetailedState();
        }
        Log.w("SettingsLib.AccessPoint", "NetworkInfo is null, cannot return detailed state");
        return null;
    }

    public WifiInfo getInfo() {
        return this.mInfo;
    }

    public String getKey() {
        return this.mKey;
    }

    public int getLevel() {
        return MiuiWifiManager.calculateSignalLevel(this.mRssi, 5);
    }

    public NetworkInfo getNetworkInfo() {
        return this.mNetworkInfo;
    }

    public String getPrimaryWifiTitleForSlave() {
        WifiInfo connectionInfo = getWifiManager().getConnectionInfo();
        boolean z = connectionInfo != null && WifiUtils.is24GHz(connectionInfo.getFrequency());
        boolean z2 = connectionInfo != null && WifiUtils.is5GHz(connectionInfo.getFrequency());
        StringBuilder sb = new StringBuilder();
        sb.append(getTitle());
        if (z) {
            sb.append(this.mContext.getString(R$string.band_24G));
        } else if (z2) {
            sb.append(this.mContext.getString(R$string.band_5G));
        }
        return sb.toString();
    }

    public int getRssi() {
        return this.mRssi;
    }

    public String getSavedNetworkSummary() {
        WifiConfiguration wifiConfiguration = this.mConfig;
        if (wifiConfiguration != null) {
            PackageManager packageManager = this.mContext.getPackageManager();
            String nameForUid = packageManager.getNameForUid(VipService.VIP_SERVICE_FAILURE);
            int userId = UserHandle.getUserId(wifiConfiguration.creatorUid);
            ApplicationInfo applicationInfo = null;
            String str = wifiConfiguration.creatorName;
            if (str == null || !str.equals(nameForUid)) {
                try {
                    applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(wifiConfiguration.creatorName, 0, userId);
                } catch (RemoteException unused) {
                }
            } else {
                applicationInfo = this.mContext.getApplicationInfo();
            }
            if (applicationInfo != null && !applicationInfo.packageName.equals(this.mContext.getString(R$string.settings_package)) && !applicationInfo.packageName.equals(this.mContext.getString(R$string.certinstaller_package))) {
                return this.mContext.getString(R$string.saved_network, applicationInfo.loadLabel(packageManager));
            }
        }
        return (isPasspointConfigurationR1() && isExpired()) ? this.mContext.getString(R$string.wifi_passpoint_expired) : "";
    }

    public Set<ScanResult> getScanResults() {
        ArraySet arraySet;
        synchronized (this.mScanResultsLock) {
            arraySet = new ArraySet();
            arraySet.addAll((Collection) this.mScanResults);
            arraySet.addAll((Collection) this.mExtraScanResults);
        }
        return arraySet;
    }

    public Map<String, TimestampedScoredNetwork> getScoredNetworkCache() {
        return this.mScoredNetworkCache;
    }

    public int getSecurity() {
        return this.security;
    }

    public String getSecurityString(boolean z) {
        Context context = this.mContext;
        if (isPasspoint() || isPasspointConfig()) {
            return z ? context.getString(R$string.wifi_security_short_eap) : context.getString(R$string.wifi_security_eap);
        } else if (this.mIsPskSaeTransitionMode) {
            return z ? context.getString(R$string.wifi_security_short_psk_sae) : context.getString(R$string.wifi_security_psk_sae);
        } else if (this.mIsOweTransitionMode) {
            return z ? context.getString(R$string.wifi_security_short_none_owe) : context.getString(R$string.wifi_security_none_owe);
        } else {
            switch (this.security) {
                case 1:
                    return z ? context.getString(R$string.wifi_security_short_wep) : context.getString(R$string.wifi_security_wep);
                case 2:
                    int i = this.pskType;
                    return i != 1 ? i != 2 ? i != 3 ? z ? context.getString(R$string.wifi_security_short_psk_generic) : context.getString(R$string.wifi_security_psk_generic) : z ? context.getString(R$string.wifi_security_short_wpa_wpa2) : context.getString(R$string.wifi_security_wpa_wpa2) : z ? context.getString(R$string.wifi_security_short_wpa2) : context.getString(R$string.wifi_security_wpa2) : z ? context.getString(R$string.wifi_security_short_wpa) : context.getString(R$string.wifi_security_wpa);
                case 3:
                    int i2 = this.mEapType;
                    return i2 != 1 ? i2 != 2 ? z ? context.getString(R$string.wifi_security_short_eap) : context.getString(R$string.wifi_security_eap) : z ? context.getString(R$string.wifi_security_short_eap_wpa2_wpa3) : context.getString(R$string.wifi_security_eap_wpa2_wpa3) : z ? context.getString(R$string.wifi_security_short_eap_wpa) : context.getString(R$string.wifi_security_eap_wpa);
                case 4:
                    return z ? context.getString(R$string.wifi_security_short_owe) : context.getString(R$string.wifi_security_owe);
                case 5:
                    return z ? context.getString(R$string.wifi_security_short_sae) : context.getString(R$string.wifi_security_sae);
                case 6:
                    return z ? context.getString(R$string.wifi_security_short_eap_suiteb) : context.getString(R$string.wifi_security_eap_suiteb);
                case 7:
                case 8:
                case 9:
                default:
                    return z ? "" : context.getString(R$string.wifi_security_none);
                case 10:
                    return z ? context.getString(R$string.wifi_security_short_dpp) : context.getString(R$string.wifi_security_dpp);
                case 11:
                    return context.getString(R$string.wifi_security_wapi_psk);
                case 12:
                    return context.getString(R$string.wifi_security_wapi_cert);
            }
        }
    }

    public String getSettingsSummary() {
        return getSettingsSummary(false);
    }

    public String getSettingsSummary(boolean z) {
        if (isPasspointConfigurationR1() && isExpired()) {
            return this.mContext.getString(R$string.wifi_passpoint_expired);
        }
        StringBuilder sb = new StringBuilder();
        if (isOsuProvider()) {
            if (this.mOsuProvisioningComplete) {
                sb.append(this.mContext.getString(R$string.osu_sign_up_complete));
            } else {
                String str = this.mOsuFailure;
                if (str != null) {
                    sb.append(str);
                } else {
                    String str2 = this.mOsuStatus;
                    if (str2 != null) {
                        sb.append(str2);
                    } else {
                        sb.append(this.mContext.getString(R$string.tap_to_sign_up));
                    }
                }
            }
        } else if (isPasspointR1Provider()) {
            if (this.mPasspointR1ProvisioningComplete) {
                sb.append("");
            } else {
                String str3 = this.mPasspointR1Failure;
                if (str3 != null) {
                    sb.append(str3);
                } else {
                    String str4 = this.mPasspointR1Status;
                    if (str4 != null) {
                        sb.append(str4);
                    } else {
                        sb.append(this.mContext.getString(R$string.tap_to_sign_up));
                    }
                }
            }
        } else if (isActive()) {
            Context context = this.mContext;
            NetworkInfo.DetailedState detailedState = getDetailedState();
            WifiInfo wifiInfo = this.mInfo;
            boolean z2 = wifiInfo != null && wifiInfo.isEphemeral();
            WifiInfo wifiInfo2 = this.mInfo;
            sb.append(getSummary(context, null, detailedState, z2, wifiInfo2 != null ? wifiInfo2.getRequestingPackageName() : null));
        } else {
            WifiConfiguration wifiConfiguration = this.mConfig;
            if (wifiConfiguration == null || !(wifiConfiguration.hasNoInternetAccess() || ((this.mConfig.getNetworkSelectionStatus().hasEverConnected() && this.mConfig.isNoInternetAccessExpected()) || this.mConfig.getNetworkSelectionStatus().getNetworkSelectionDisableReason() == 6 || this.mConfig.getNetworkSelectionStatus().getNetworkSelectionDisableReason() == 4))) {
                WifiConfiguration wifiConfiguration2 = this.mConfig;
                if (wifiConfiguration2 != null && wifiConfiguration2.getNetworkSelectionStatus().getNetworkSelectionStatus() != 0) {
                    int networkSelectionDisableReason = this.mConfig.getNetworkSelectionStatus().getNetworkSelectionDisableReason();
                    if (networkSelectionDisableReason == 1) {
                        sb.append(this.mContext.getString(R$string.wifi_disabled_generic));
                    } else if (networkSelectionDisableReason == 2) {
                        sb.append(this.mContext.getString(R$string.wifi_disabled_password_failure));
                    } else if (networkSelectionDisableReason == 3) {
                        sb.append(this.mContext.getString(R$string.wifi_disabled_network_failure));
                    } else if (networkSelectionDisableReason == 8) {
                        sb.append(this.mContext.getString(R$string.wifi_check_password_try_again));
                    }
                } else if (isReachable()) {
                    WifiConfiguration wifiConfiguration3 = this.mConfig;
                    if (wifiConfiguration3 != null) {
                        if (wifiConfiguration3.getRecentFailureReason() == 17) {
                            sb.append(this.mContext.getString(R$string.wifi_ap_unable_to_handle_new_sta));
                        } else if (z) {
                            sb.append(this.mContext.getString(R$string.wifi_disconnected));
                        } else {
                            sb.append(this.mContext.getString(R$string.wifi_remembered));
                        }
                    }
                } else {
                    sb.append(this.mContext.getString(R$string.wifi_not_in_range));
                }
            } else {
                sb.append(this.mContext.getString(this.mConfig.getNetworkSelectionStatus().getNetworkSelectionStatus() == 2 ? R$string.wifi_no_internet_no_reconnect : R$string.wifi_no_internet));
            }
        }
        if (isVerboseLoggingEnabled()) {
            sb.append(WifiUtils.buildLoggingSummary(this, this.mConfig));
        }
        try {
            WifiConfiguration wifiConfiguration4 = this.mConfig;
            if (wifiConfiguration4 != null && (WifiUtils.isMeteredOverridden(wifiConfiguration4) || this.mConfig.meteredHint)) {
                return this.mContext.getResources().getString(R$string.preference_summary_default_combination, WifiUtils.getMeteredLabel(this.mContext, this.mConfig), sb.toString());
            }
        } catch (NullPointerException e) {
            Log.w("SettingsLib.AccessPoint", e.toString());
        }
        return (this.getSpeedLabel() == null || sb.length() == 0) ? this.getSpeedLabel() != null ? this.getSpeedLabel() : sb.toString() : this.mContext.getResources().getString(R$string.preference_summary_default_combination, this.getSpeedLabel(), sb.toString());
    }

    public NetworkInfo.DetailedState getSlaveDetailedState() {
        NetworkInfo networkInfo = this.mSlaveNetworkInfo;
        if (networkInfo != null) {
            return networkInfo.getDetailedState();
        }
        Log.w("SettingsLib.AccessPoint", "mSlaveNetworkInfo is null, cannot return detailed state");
        return null;
    }

    public WifiInfo getSlaveInfo() {
        return this.mSlaveInfo;
    }

    public String getSlaveSettingsSummary(boolean z) {
        StringBuilder sb = new StringBuilder();
        if (!isActive()) {
            if (isOsuProvider()) {
                if (this.mOsuProvisioningComplete) {
                    sb.append(this.mContext.getString(R$string.osu_sign_up_complete));
                } else {
                    String str = this.mOsuFailure;
                    if (str != null) {
                        sb.append(str);
                    } else {
                        String str2 = this.mOsuStatus;
                        if (str2 != null) {
                            sb.append(str2);
                        } else {
                            sb.append(this.mContext.getString(R$string.tap_to_sign_up));
                        }
                    }
                }
            } else if (isSlaveActive()) {
                Context context = this.mContext;
                NetworkInfo.DetailedState slaveDetailedState = getSlaveDetailedState();
                WifiInfo wifiInfo = this.mInfo;
                boolean z2 = wifiInfo != null && wifiInfo.isEphemeral();
                WifiInfo wifiInfo2 = this.mInfo;
                sb.append(getSlaveSummary(context, null, slaveDetailedState, z2, wifiInfo2 != null ? wifiInfo2.getRequestingPackageName() : null));
            } else {
                WifiConfiguration wifiConfiguration = this.mConfig;
                if (wifiConfiguration == null || !(wifiConfiguration.hasNoInternetAccess() || ((this.mConfig.getNetworkSelectionStatus().hasEverConnected() && this.mConfig.isNoInternetAccessExpected()) || this.mConfig.getNetworkSelectionStatus().getNetworkSelectionDisableReason() == 6))) {
                    WifiConfiguration wifiConfiguration2 = this.mConfig;
                    if (wifiConfiguration2 != null && wifiConfiguration2.getNetworkSelectionStatus().getNetworkSelectionStatus() != 0) {
                        int networkSelectionDisableReason = this.mConfig.getNetworkSelectionStatus().getNetworkSelectionDisableReason();
                        if (networkSelectionDisableReason == 1) {
                            sb.append(this.mContext.getString(R$string.wifi_disabled_generic));
                        } else if (networkSelectionDisableReason == 2) {
                            sb.append(this.mContext.getString(R$string.wifi_disabled_password_failure));
                        } else if (networkSelectionDisableReason == 3) {
                            sb.append(this.mContext.getString(R$string.wifi_disabled_network_failure));
                        } else if (networkSelectionDisableReason == 8) {
                            sb.append(this.mContext.getString(R$string.wifi_check_password_try_again));
                        }
                    } else if (isReachable()) {
                        WifiConfiguration wifiConfiguration3 = this.mConfig;
                        if (wifiConfiguration3 != null) {
                            if (wifiConfiguration3.getRecentFailureReason() == 17) {
                                sb.append(this.mContext.getString(R$string.wifi_ap_unable_to_handle_new_sta));
                            } else if (z) {
                                sb.append(this.mContext.getString(R$string.wifi_disconnected));
                            } else {
                                sb.append(this.mContext.getString(R$string.wifi_remembered));
                            }
                        }
                    } else {
                        sb.append(this.mContext.getString(R$string.wifi_not_in_range));
                    }
                } else {
                    sb.append(this.mContext.getString(this.mConfig.getNetworkSelectionStatus().getNetworkSelectionStatus() == 2 ? R$string.wifi_no_internet_no_reconnect : R$string.wifi_no_internet));
                }
            }
        }
        if (isVerboseLoggingEnabled()) {
            sb.append(WifiUtils.buildLoggingSummary(this, this.mConfig));
        }
        WifiConfiguration wifiConfiguration4 = this.mConfig;
        return (wifiConfiguration4 == null || !(WifiUtils.isMeteredOverridden(wifiConfiguration4) || this.mConfig.meteredHint)) ? (getSpeedLabel() == null || sb.length() == 0) ? getSpeedLabel() != null ? getSpeedLabel() : sb.toString() : this.mContext.getResources().getString(R$string.preference_summary_default_combination, getSpeedLabel(), sb.toString()) : this.mContext.getResources().getString(R$string.preference_summary_default_combination, WifiUtils.getMeteredLabel(this.mContext, this.mConfig), sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSpeed() {
        return this.mSpeed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getSpeedLabel() {
        return getSpeedLabel(this.mSpeed);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getSpeedLabel(int i) {
        return getSpeedLabel(this.mContext, i);
    }

    public String getSsidStr() {
        return this.ssid;
    }

    public String getSummary() {
        return getSettingsSummary();
    }

    public Object getTag() {
        return this.mTag;
    }

    public String getTitle() {
        return (!isPasspoint() || TextUtils.isEmpty(this.mConfig.providerFriendlyName)) ? (!isPasspointConfig() || TextUtils.isEmpty(this.mProviderFriendlyName)) ? isPasspointR1Provider() ? "exands.com".equals(this.mPasspointR1Provider.getDomainName()) ? "exands Secure Wi-Fi" : this.mPasspointR1Provider.getDomainName() : (!isOsuProvider() || TextUtils.isEmpty(this.mOsuProvider.getFriendlyName())) ? !TextUtils.isEmpty(getSsidStr()) ? getSsidStr() : "" : this.mOsuProvider.getFriendlyName() : this.mProviderFriendlyName : this.mConfig.providerFriendlyName;
    }

    public int getWifiStandard() {
        WifiInfo wifiInfo;
        return (!isActive() || (wifiInfo = this.mInfo) == null) ? this.mWifiStandard : wifiInfo.getWifiStandard();
    }

    public int hashCode() {
        WifiInfo wifiInfo = this.mInfo;
        return (wifiInfo != null ? 0 + (wifiInfo.hashCode() * 13) : 0) + (this.mRssi * 19) + (this.networkId * 23) + (this.ssid.hashCode() * 29);
    }

    public boolean isActive() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        return (networkInfo == null || (this.networkId == -1 && networkInfo.getState() == NetworkInfo.State.DISCONNECTED)) ? false : true;
    }

    public boolean isConnectable() {
        return getLevel() != -1 && getDetailedState() == null;
    }

    public boolean isEphemeral() {
        NetworkInfo networkInfo;
        WifiInfo wifiInfo = this.mInfo;
        return (wifiInfo == null || !wifiInfo.isEphemeral() || (networkInfo = this.mNetworkInfo) == null || networkInfo.getState() == NetworkInfo.State.DISCONNECTED) ? false : true;
    }

    public boolean isExpired() {
        return this.mSubscriptionExpirationTimeInMillis > 0 && System.currentTimeMillis() >= this.mSubscriptionExpirationTimeInMillis;
    }

    public boolean isHe8ssCapableAp() {
        WifiInfo wifiInfo;
        return (!isActive() || (wifiInfo = this.mInfo) == null) ? this.mHe8ssCapableAp : Wifi6ApiCompatible.isHe8ssCapableAp(wifiInfo);
    }

    public boolean isMetered() {
        return this.mIsScoredNetworkMetered || WifiConfiguration.isMetered(this.mConfig, this.mInfo);
    }

    public boolean isOsuProvider() {
        return this.mOsuProvider != null;
    }

    public boolean isPasspoint() {
        WifiConfiguration wifiConfiguration = this.mConfig;
        return wifiConfiguration != null && wifiConfiguration.isPasspoint();
    }

    public boolean isPasspointConfig() {
        return this.mPasspointUniqueId != null && this.mConfig == null;
    }

    public boolean isPasspointConfigurationR1() {
        return this.mPasspointConfigurationVersion == 1;
    }

    public boolean isPasspointR1Provider() {
        return this.mPasspointR1Provider != null;
    }

    public boolean isReachable() {
        return this.mRssi != Integer.MIN_VALUE;
    }

    public boolean isSaved() {
        return this.mConfig != null;
    }

    public boolean isSlaveActive() {
        NetworkInfo networkInfo = this.mSlaveNetworkInfo;
        return (networkInfo == null || (this.networkId == -1 && networkInfo.getState() == NetworkInfo.State.DISCONNECTED)) ? false : true;
    }

    public boolean isVhtMax8SpatialStreamsSupported() {
        WifiInfo wifiInfo;
        return (!isActive() || (wifiInfo = this.mInfo) == null) ? this.mVhtMax8SpatialStreamsSupport : Wifi6ApiCompatible.isVhtMax8SpatialStreamsSupported(wifiInfo);
    }

    @VisibleForTesting
    void loadConfig(WifiConfiguration wifiConfiguration) {
        String str = wifiConfiguration.SSID;
        this.ssid = str == null ? "" : removeDoubleQuotes(str);
        this.bssid = wifiConfiguration.BSSID;
        this.security = getSecurity(wifiConfiguration);
        this.networkId = wifiConfiguration.networkId;
        this.mConfig = wifiConfiguration;
    }

    @VisibleForTesting
    boolean matches(ScanResult scanResult) {
        if (scanResult == null) {
            return false;
        }
        if (isPasspoint() || isOsuProvider()) {
            throw new IllegalStateException("Should not matches a Passpoint by ScanResult");
        }
        if (isSameSsidOrBssid(scanResult)) {
            if (!this.mIsPskSaeTransitionMode) {
                int i = this.security;
                if ((i == 5 || i == 2) && isPskSaeTransitionMode(scanResult)) {
                    return true;
                }
            } else if ((scanResult.capabilities.contains("SAE") && getWifiManager().isWpa3SaeSupported()) || scanResult.capabilities.contains("PSK")) {
                return true;
            }
            if (this.mIsOweTransitionMode) {
                int security = getSecurity(this.mContext, scanResult);
                if ((security == 4 && getWifiManager().isEnhancedOpenSupported()) || security == 0) {
                    return true;
                }
            } else {
                int i2 = this.security;
                if ((i2 == 4 || i2 == 0) && isOweTransitionMode(scanResult)) {
                    return true;
                }
            }
            return this.security == getSecurity(this.mContext, scanResult);
        }
        return false;
    }

    public boolean matches(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.isPasspoint()) {
            return isPasspoint() && wifiConfiguration.getKey().equals(this.mConfig.getKey());
        } else if (this.ssid.equals(removeDoubleQuotes(wifiConfiguration.SSID))) {
            WifiConfiguration wifiConfiguration2 = this.mConfig;
            if (wifiConfiguration2 == null || wifiConfiguration2.shared == wifiConfiguration.shared) {
                int security = getSecurity(wifiConfiguration);
                if ((this.mIsPskSaeTransitionMode || this.mIsWpa2Wpa3Coexist) && ((security == 5 && getWifiManager().isWpa3SaeSupported()) || security == 2)) {
                    return true;
                }
                return ((this.mIsOweTransitionMode || this.mIsOpenOweCoexist) && ((security == 4 && getWifiManager().isEnhancedOpenSupported()) || security == 0)) || this.security == getSecurity(wifiConfiguration);
            }
            return false;
        } else {
            return false;
        }
    }

    public void saveWifiState(Bundle bundle) {
        if (this.ssid != null) {
            bundle.putString("key_ssid", getSsidStr());
        }
        bundle.putInt("key_security", this.security);
        bundle.putInt("key_speed", this.mSpeed);
        bundle.putInt("key_psktype", this.pskType);
        bundle.putInt("eap_psktype", this.mEapType);
        WifiConfiguration wifiConfiguration = this.mConfig;
        if (wifiConfiguration != null) {
            bundle.putParcelable("key_config", wifiConfiguration);
        }
        bundle.putParcelable("key_wifiinfo", this.mInfo);
        bundle.putParcelable("key_slave_wifiinfo", this.mSlaveInfo);
        synchronized (this.mLock) {
            ArraySet<ScanResult> arraySet = this.mScanResults;
            bundle.putParcelableArray("key_scanresults", (Parcelable[]) arraySet.toArray(new Parcelable[arraySet.size() + this.mExtraScanResults.size()]));
        }
        bundle.putParcelableArrayList("key_scorednetworkcache", new ArrayList<>(this.mScoredNetworkCache.values()));
        NetworkInfo networkInfo = this.mNetworkInfo;
        if (networkInfo != null) {
            bundle.putParcelable("key_networkinfo", networkInfo);
        }
        String str = this.mPasspointUniqueId;
        if (str != null) {
            bundle.putString("key_passpoint_unique_id", str);
        }
        NetworkInfo networkInfo2 = this.mSlaveNetworkInfo;
        if (networkInfo2 != null) {
            bundle.putParcelable("key_slave_networkinfo", networkInfo2);
        }
        String str2 = this.mFqdn;
        if (str2 != null) {
            bundle.putString("key_fqdn", str2);
        }
        String str3 = this.mProviderFriendlyName;
        if (str3 != null) {
            bundle.putString("key_provider_friendly_name", str3);
        }
        bundle.putLong("key_subscription_expiration_time_in_millis", this.mSubscriptionExpirationTimeInMillis);
        bundle.putInt("key_passpoint_configuration_version", this.mPasspointConfigurationVersion);
        bundle.putBoolean("key_is_psk_sae_transition_mode", this.mIsPskSaeTransitionMode);
        bundle.putBoolean("key_is_owe_transition_mode", this.mIsOweTransitionMode);
    }

    public void setListener(AccessPointListener accessPointListener) {
        this.mAccessPointListener = accessPointListener;
    }

    @VisibleForTesting
    void setRssi(int i) {
        this.mRssi = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setScanResults(Collection<ScanResult> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            Log.d("SettingsLib.AccessPoint", "Cannot set scan results to empty list");
            return;
        }
        if (this.mKey != null && !isPasspoint() && !isOsuProvider() && !isPasspointR1Provider()) {
            for (ScanResult scanResult : collection) {
                if (!matches(scanResult)) {
                    Log.d("SettingsLib.AccessPoint", String.format("ScanResult %s\nkey of %s did not match current AP key %s", scanResult, getKey(this.mContext, scanResult), this.mKey));
                    return;
                }
            }
        }
        int level = getLevel();
        synchronized (this.mScanResultsLock) {
            this.mScanResults.clear();
            this.mScanResults.addAll(collection);
        }
        updateBestRssiInfo();
        updateWifiGeneration();
        updateCapabilities(collection.iterator().next());
        this.mIsWpa2Wpa3Coexist = isWpa2Wpa3Coexist(collection);
        this.mIsOpenOweCoexist = isOpenOweCoexist(collection);
        int level2 = getLevel();
        if (level2 > 0 && level2 != level) {
            updateSpeed();
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AccessPoint.this.lambda$setScanResults$1();
                }
            });
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                AccessPoint.this.lambda$setScanResults$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setScanResultsPasspoint(Collection<ScanResult> collection, Collection<ScanResult> collection2) {
        synchronized (this.mLock) {
            this.mExtraScanResults.clear();
            if (!CollectionUtils.isEmpty(collection)) {
                this.mIsRoaming = false;
                if (!CollectionUtils.isEmpty(collection2)) {
                    this.mExtraScanResults.addAll(collection2);
                }
                setScanResults(collection);
            } else if (!CollectionUtils.isEmpty(collection2)) {
                this.mIsRoaming = true;
                setScanResults(collection2);
            }
        }
    }

    public void setTag(Object obj) {
        this.mTag = obj;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccessPoint(");
        sb.append(this.ssid);
        if (this.bssid != null) {
            sb.append(":");
            sb.append(this.bssid);
        }
        if (isSaved()) {
            sb.append(',');
            sb.append("saved");
        }
        if (isActive()) {
            sb.append(',');
            sb.append("active");
        }
        if (isEphemeral()) {
            sb.append(',');
            sb.append("ephemeral");
        }
        if (isConnectable()) {
            sb.append(',');
            sb.append("connectable");
        }
        int i = this.security;
        if (i != 0 && i != 4) {
            sb.append(',');
            sb.append(securityToString(this.security, this.pskType));
        }
        sb.append(",level=");
        sb.append(getLevel());
        if (this.mSpeed != 0) {
            sb.append(",speed=");
            sb.append(this.mSpeed);
        }
        sb.append(",metered=");
        sb.append(isMetered());
        if (isVerboseLoggingEnabled()) {
            sb.append(",rssi=");
            sb.append(this.mRssi);
            synchronized (this.mLock) {
                sb.append(",scan cache size=");
                sb.append(this.mScanResults.size() + this.mExtraScanResults.size());
            }
        }
        sb.append(')');
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void update(WifiConfiguration wifiConfiguration) {
        this.mConfig = wifiConfiguration;
        if (wifiConfiguration != null && !isPasspoint()) {
            this.ssid = removeDoubleQuotes(this.mConfig.SSID);
        }
        this.networkId = wifiConfiguration != null ? wifiConfiguration.networkId : -1;
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                AccessPoint.this.lambda$update$7();
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x0079, code lost:
    
        if (r5.getDetailedState() != r7.getDetailedState()) goto L30;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean update(android.net.wifi.WifiConfiguration r5, android.net.wifi.WifiInfo r6, android.net.NetworkInfo r7) {
        /*
            r4 = this;
            int r0 = r4.getLevel()
            r1 = 0
            r2 = 1
            if (r6 == 0) goto L81
            boolean r3 = r4.isInfoForThisAccessPoint(r5, r6)
            if (r3 == 0) goto L81
            android.net.wifi.WifiInfo r3 = r4.mInfo
            if (r3 != 0) goto L13
            r1 = r2
        L13:
            boolean r3 = r4.isPasspoint()
            if (r3 != 0) goto L20
            android.net.wifi.WifiConfiguration r3 = r4.mConfig
            if (r3 == r5) goto L20
            r4.update(r5)
        L20:
            int r5 = r4.getWifiStandard()
            int r3 = r6.getWifiStandard()
            if (r5 != r3) goto L3e
            boolean r5 = r4.isHe8ssCapableAp()
            boolean r3 = com.android.settingslib.wifi.Wifi6ApiCompatible.isHe8ssCapableAp(r6)
            if (r5 != r3) goto L3e
            boolean r5 = r4.isVhtMax8SpatialStreamsSupported()
            boolean r3 = com.android.settingslib.wifi.Wifi6ApiCompatible.isVhtMax8SpatialStreamsSupported(r6)
            if (r5 == r3) goto L3f
        L3e:
            r1 = r2
        L3f:
            int r5 = r4.mRssi
            int r3 = r6.getRssi()
            if (r5 == r3) goto L6b
            int r5 = r6.getRssi()
            r3 = -127(0xffffffffffffff81, float:NaN)
            if (r5 == r3) goto L6b
            int r5 = r6.getRssi()
            r4.mRssi = r5
            java.lang.String r5 = r6.getBSSID()
            r4.bssid = r5
            int r5 = r4.mRssi
            r1 = -100
            if (r5 > r1) goto L69
            android.net.wifi.ScanResult r5 = r4.mScanResult
            if (r5 == 0) goto L69
            int r5 = r5.level
            r4.mRssi = r5
        L69:
            r1 = r2
            goto L7c
        L6b:
            android.net.NetworkInfo r5 = r4.mNetworkInfo
            if (r5 == 0) goto L7c
            if (r7 == 0) goto L7c
            android.net.NetworkInfo$DetailedState r5 = r5.getDetailedState()
            android.net.NetworkInfo$DetailedState r3 = r7.getDetailedState()
            if (r5 == r3) goto L7c
            goto L69
        L7c:
            r4.mInfo = r6
            r4.mNetworkInfo = r7
            goto L8e
        L81:
            android.net.wifi.WifiInfo r5 = r4.mInfo
            if (r5 == 0) goto L8e
            r5 = 0
            r4.mInfo = r5
            r4.mNetworkInfo = r5
            r4.updateWifiGeneration()
            r1 = r2
        L8e:
            if (r1 == 0) goto Laa
            com.android.settingslib.wifi.AccessPoint$AccessPointListener r5 = r4.mAccessPointListener
            if (r5 == 0) goto Laa
            com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda2 r5 = new com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda2
            r5.<init>()
            com.android.settingslib.utils.ThreadUtils.postOnMainThread(r5)
            int r5 = r4.getLevel()
            if (r0 == r5) goto Laa
            com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda1 r5 = new com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda1
            r5.<init>()
            com.android.settingslib.utils.ThreadUtils.postOnMainThread(r5)
        Laa:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.wifi.AccessPoint.update(android.net.wifi.WifiConfiguration, android.net.wifi.WifiInfo, android.net.NetworkInfo):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean update(WifiNetworkScoreCache wifiNetworkScoreCache, boolean z, long j) {
        return updateMetered(wifiNetworkScoreCache) || (z ? updateScores(wifiNetworkScoreCache, j) : false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x0079, code lost:
    
        if (r5.getDetailedState() != r7.getDetailedState()) goto L30;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean updateSlave(android.net.wifi.WifiConfiguration r5, android.net.wifi.WifiInfo r6, android.net.NetworkInfo r7) {
        /*
            r4 = this;
            int r0 = r4.getLevel()
            r1 = 0
            r2 = 1
            if (r6 == 0) goto L81
            boolean r3 = r4.isInfoForThisAccessPoint(r5, r6)
            if (r3 == 0) goto L81
            android.net.wifi.WifiInfo r3 = r4.mSlaveInfo
            if (r3 != 0) goto L13
            r1 = r2
        L13:
            boolean r3 = r4.isPasspoint()
            if (r3 != 0) goto L20
            android.net.wifi.WifiConfiguration r3 = r4.mConfig
            if (r3 == r5) goto L20
            r4.update(r5)
        L20:
            int r5 = r4.getWifiStandard()
            int r3 = r6.getWifiStandard()
            if (r5 != r3) goto L3e
            boolean r5 = r4.isHe8ssCapableAp()
            boolean r3 = com.android.settingslib.wifi.Wifi6ApiCompatible.isHe8ssCapableAp(r6)
            if (r5 != r3) goto L3e
            boolean r5 = r4.isVhtMax8SpatialStreamsSupported()
            boolean r3 = com.android.settingslib.wifi.Wifi6ApiCompatible.isVhtMax8SpatialStreamsSupported(r6)
            if (r5 == r3) goto L3f
        L3e:
            r1 = r2
        L3f:
            int r5 = r4.mRssi
            int r3 = r6.getRssi()
            if (r5 == r3) goto L6b
            int r5 = r6.getRssi()
            r3 = -127(0xffffffffffffff81, float:NaN)
            if (r5 == r3) goto L6b
            int r5 = r6.getRssi()
            r4.mRssi = r5
            java.lang.String r5 = r6.getBSSID()
            r4.bssid = r5
            int r5 = r4.mRssi
            r1 = -100
            if (r5 > r1) goto L69
            android.net.wifi.ScanResult r5 = r4.mScanResult
            if (r5 == 0) goto L69
            int r5 = r5.level
            r4.mRssi = r5
        L69:
            r1 = r2
            goto L7c
        L6b:
            android.net.NetworkInfo r5 = r4.mSlaveNetworkInfo
            if (r5 == 0) goto L7c
            if (r7 == 0) goto L7c
            android.net.NetworkInfo$DetailedState r5 = r5.getDetailedState()
            android.net.NetworkInfo$DetailedState r3 = r7.getDetailedState()
            if (r5 == r3) goto L7c
            goto L69
        L7c:
            r4.mSlaveInfo = r6
            r4.mSlaveNetworkInfo = r7
            goto L8e
        L81:
            android.net.wifi.WifiInfo r5 = r4.mSlaveInfo
            if (r5 == 0) goto L8e
            r5 = 0
            r4.mSlaveInfo = r5
            r4.mSlaveNetworkInfo = r5
            r4.updateWifiGeneration()
            r1 = r2
        L8e:
            if (r1 == 0) goto Laa
            com.android.settingslib.wifi.AccessPoint$AccessPointListener r5 = r4.mAccessPointListener
            if (r5 == 0) goto Laa
            com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda3 r5 = new com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda3
            r5.<init>()
            com.android.settingslib.utils.ThreadUtils.postOnMainThread(r5)
            int r5 = r4.getLevel()
            if (r0 == r5) goto Laa
            com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda5 r5 = new com.android.settingslib.wifi.AccessPoint$$ExternalSyntheticLambda5
            r5.<init>()
            com.android.settingslib.utils.ThreadUtils.postOnMainThread(r5)
        Laa:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.wifi.AccessPoint.updateSlave(android.net.wifi.WifiConfiguration, android.net.wifi.WifiInfo, android.net.NetworkInfo):boolean");
    }
}
