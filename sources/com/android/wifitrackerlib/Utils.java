package com.android.wifitrackerlib;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.WifiKey;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.util.FeatureFlagUtils;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.HelpUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import miui.content.res.ThemeResources;

/* loaded from: classes2.dex */
public class Utils {
    @VisibleForTesting
    static FeatureFlagUtilsWrapper sFeatureFlagUtilsWrapper = new FeatureFlagUtilsWrapper();
    private static NetworkScoreManager sNetworkScoreManager;

    /* loaded from: classes2.dex */
    static class FeatureFlagUtilsWrapper {
        FeatureFlagUtilsWrapper() {
        }

        boolean isProviderModelEnabled(Context context) {
            return FeatureFlagUtils.isEnabled(context, "settings_provider_model");
        }
    }

    private static String getActiveScorerPackage(Context context) {
        if (sNetworkScoreManager == null) {
            sNetworkScoreManager = (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class);
        }
        return sNetworkScoreManager.getActiveScorerPackage();
    }

    static String getAppLabel(Context context, String str) {
        try {
            String string = Settings.Global.getString(context.getContentResolver(), "use_open_wifi_package");
            if (!TextUtils.isEmpty(string) && TextUtils.equals(str, getActiveScorerPackage(context))) {
                str = string;
            }
            return context.getPackageManager().getApplicationInfo(str, 0).loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException unused) {
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getAutoConnectDescription(Context context, WifiEntry wifiEntry) {
        return (context == null || wifiEntry == null || !wifiEntry.canSetAutoJoinEnabled() || wifiEntry.isAutoJoinEnabled()) ? "" : context.getString(R$string.wifitrackerlib_auto_connect_disable);
    }

    public static int getAverageSpeedFromScanResults(WifiNetworkScoreCache wifiNetworkScoreCache, List<ScanResult> list) {
        int calculateBadge;
        int i = 0;
        int i2 = 0;
        for (ScanResult scanResult : list) {
            ScoredNetwork scoredNetwork = wifiNetworkScoreCache.getScoredNetwork(scanResult);
            if (scoredNetwork != null && (calculateBadge = scoredNetwork.calculateBadge(scanResult.level)) != 0) {
                i++;
                i2 += calculateBadge;
            }
        }
        if (i == 0) {
            return 0;
        }
        return roundToClosestSpeedEnum(i2 / i);
    }

    public static ScanResult getBestScanResultByLevel(List<ScanResult> list) {
        if (list.isEmpty()) {
            return null;
        }
        return (ScanResult) Collections.max(list, Comparator.comparingInt(new ToIntFunction() { // from class: com.android.wifitrackerlib.Utils$$ExternalSyntheticLambda1
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int i;
                i = ((ScanResult) obj).level;
                return i;
            }
        }));
    }

    static String getCarrierNameForSubId(Context context, int i) {
        TelephonyManager telephonyManager;
        TelephonyManager createForSubscriptionId;
        CharSequence simCarrierIdName;
        if (i == -1 || (telephonyManager = (TelephonyManager) context.getSystemService("phone")) == null || (createForSubscriptionId = telephonyManager.createForSubscriptionId(i)) == null || (simCarrierIdName = createForSubscriptionId.getSimCarrierIdName()) == null) {
            return null;
        }
        return simCarrierIdName.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getConnectedDescription(Context context, WifiConfiguration wifiConfiguration, NetworkCapabilities networkCapabilities, String str, boolean z, boolean z2) {
        StringJoiner stringJoiner = new StringJoiner(context.getString(R$string.wifitrackerlib_summary_separator));
        boolean z3 = !z && sFeatureFlagUtilsWrapper.isProviderModelEnabled(context);
        if (wifiConfiguration != null) {
            if (wifiConfiguration.fromWifiNetworkSuggestion || wifiConfiguration.fromWifiNetworkSpecifier) {
                String suggestionOrSpecifierLabel = getSuggestionOrSpecifierLabel(context, wifiConfiguration);
                if (!TextUtils.isEmpty(suggestionOrSpecifierLabel)) {
                    if (z3) {
                        stringJoiner.add(context.getString(R$string.wifitrackerlib_available_via_app, suggestionOrSpecifierLabel));
                    } else {
                        stringJoiner.add(context.getString(R$string.wifitrackerlib_connected_via_app, suggestionOrSpecifierLabel));
                    }
                }
            } else if (wifiConfiguration.isEphemeral() && !z3) {
                if (TextUtils.isEmpty(str)) {
                    stringJoiner.add(context.getString(R$string.wifitrackerlib_connected_via_network_scorer_default));
                } else {
                    stringJoiner.add(String.format(context.getString(R$string.wifitrackerlib_connected_via_network_scorer), str));
                }
            }
        }
        if (z2) {
            stringJoiner.add(context.getString(R$string.wifi_connected_low_quality));
        }
        String currentNetworkCapabilitiesInformation = getCurrentNetworkCapabilitiesInformation(context, networkCapabilities);
        if (!TextUtils.isEmpty(currentNetworkCapabilitiesInformation)) {
            stringJoiner.add(currentNetworkCapabilitiesInformation);
        }
        return (stringJoiner.length() != 0 || z3) ? stringJoiner.toString() : context.getResources().getStringArray(R$array.wifitrackerlib_wifi_status)[NetworkInfo.DetailedState.CONNECTED.ordinal()];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getConnectingDescription(Context context, NetworkInfo networkInfo) {
        NetworkInfo.DetailedState detailedState;
        if (context == null || networkInfo == null || (detailedState = networkInfo.getDetailedState()) == null) {
            return "";
        }
        String[] stringArray = context.getResources().getStringArray(R$array.wifitrackerlib_wifi_status);
        int ordinal = detailedState.ordinal();
        return ordinal >= stringArray.length ? "" : stringArray[ordinal];
    }

    static String getCurrentNetworkCapabilitiesInformation(Context context, NetworkCapabilities networkCapabilities) {
        if (context != null && networkCapabilities != null) {
            if (networkCapabilities.hasCapability(17)) {
                return context.getString(context.getResources().getIdentifier("network_available_sign_in", "string", ThemeResources.FRAMEWORK_PACKAGE));
            }
            if (networkCapabilities.hasCapability(24)) {
                return getWifiConnected(context);
            }
            if (!networkCapabilities.hasCapability(16)) {
                return networkCapabilities.isPrivateDnsBroken() ? context.getString(R$string.wifitrackerlib_private_dns_broken) : context.getString(R$string.wifitrackerlib_wifi_connected_cannot_provide_internet);
            }
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getDisconnectedDescription(Context context, WifiConfiguration wifiConfiguration, boolean z, boolean z2) {
        if (context == null) {
            return "";
        }
        StringJoiner stringJoiner = new StringJoiner(context.getString(R$string.wifitrackerlib_summary_separator));
        if (z2) {
            stringJoiner.add(context.getString(R$string.wifitrackerlib_wifi_disconnected));
        } else if (wifiConfiguration != null) {
            if (z && !wifiConfiguration.isPasspoint()) {
                String appLabel = getAppLabel(context, wifiConfiguration.creatorName);
                if (!TextUtils.isEmpty(appLabel)) {
                    stringJoiner.add(context.getString(R$string.wifitrackerlib_saved_network, appLabel));
                }
            } else if (wifiConfiguration.fromWifiNetworkSuggestion) {
                String suggestionOrSpecifierLabel = getSuggestionOrSpecifierLabel(context, wifiConfiguration);
                if (!TextUtils.isEmpty(suggestionOrSpecifierLabel)) {
                    stringJoiner.add(context.getString(R$string.wifitrackerlib_available_via_app, suggestionOrSpecifierLabel));
                }
            } else {
                stringJoiner.add(context.getString(R$string.wifitrackerlib_wifi_remembered));
            }
        }
        String wifiConfigurationFailureMessage = getWifiConfigurationFailureMessage(context, wifiConfiguration);
        if (!TextUtils.isEmpty(wifiConfigurationFailureMessage)) {
            stringJoiner.add(wifiConfigurationFailureMessage);
        }
        return stringJoiner.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharSequence getImsiProtectionDescription(Context context, WifiConfiguration wifiConfiguration) {
        if (context != null && wifiConfiguration != null && isSimCredential(wifiConfiguration)) {
            int defaultSubscriptionId = wifiConfiguration.carrierId == -1 ? SubscriptionManager.getDefaultSubscriptionId() : getSubIdForConfig(context, wifiConfiguration);
            if (defaultSubscriptionId != -1 && !isImsiPrivacyProtectionProvided(context, defaultSubscriptionId)) {
                return linkifyAnnotation(context, context.getText(R$string.wifitrackerlib_imsi_protection_warning), "url", context.getString(R$string.wifitrackerlib_help_url_imsi_protection));
            }
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getMeteredDescription(Context context, WifiEntry wifiEntry) {
        return (context == null || wifiEntry == null) ? "" : (wifiEntry.canSetMeteredChoice() || wifiEntry.getMeteredChoice() == 1) ? wifiEntry.getMeteredChoice() == 1 ? context.getString(R$string.wifitrackerlib_wifi_metered_label) : wifiEntry.getMeteredChoice() == 2 ? context.getString(R$string.wifitrackerlib_wifi_unmetered_label) : wifiEntry.isMetered() ? context.getString(R$string.wifitrackerlib_wifi_metered_label) : "" : "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getNetworkSelectionDescription(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        WifiConfiguration.NetworkSelectionStatus networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus();
        if (networkSelectionStatus.getNetworkSelectionStatus() != 0) {
            sb.append(" (" + networkSelectionStatus.getNetworkStatusString());
            if (networkSelectionStatus.getDisableTime() > 0) {
                sb.append(" " + DateUtils.formatElapsedTime((System.currentTimeMillis() - networkSelectionStatus.getDisableTime()) / 1000));
            }
            sb.append(")");
        }
        int maxNetworkSelectionDisableReason = WifiConfiguration.NetworkSelectionStatus.getMaxNetworkSelectionDisableReason();
        for (int i = 0; i <= maxNetworkSelectionDisableReason; i++) {
            int disableReasonCounter = networkSelectionStatus.getDisableReasonCounter(i);
            if (disableReasonCounter != 0) {
                sb.append(" ");
                sb.append(WifiConfiguration.NetworkSelectionStatus.getNetworkSelectionDisableReasonString(i));
                sb.append("=");
                sb.append(disableReasonCounter);
            }
        }
        return sb.toString();
    }

    public static List<Integer> getSecurityTypesFromScanResult(ScanResult scanResult) {
        ArrayList arrayList = new ArrayList();
        if (isScanResultForOweTransitionNetwork(scanResult)) {
            arrayList.add(0);
            arrayList.add(6);
            return arrayList;
        } else if (isScanResultForOweNetwork(scanResult)) {
            arrayList.add(6);
            return arrayList;
        } else if (isScanResultForOpenNetwork(scanResult)) {
            arrayList.add(0);
            return arrayList;
        } else if (isScanResultForWepNetwork(scanResult)) {
            arrayList.add(1);
            return arrayList;
        } else if (isScanResultForWapiPskNetwork(scanResult)) {
            arrayList.add(7);
            return arrayList;
        } else if (isScanResultForWapiCertNetwork(scanResult)) {
            arrayList.add(8);
            return arrayList;
        } else if (isScanResultForPskNetwork(scanResult) && isScanResultForSaeNetwork(scanResult)) {
            arrayList.add(2);
            arrayList.add(4);
            return arrayList;
        } else if (isScanResultForPskNetwork(scanResult)) {
            arrayList.add(2);
            return arrayList;
        } else if (isScanResultForSaeNetwork(scanResult)) {
            arrayList.add(4);
            return arrayList;
        } else {
            if (isScanResultForEapSuiteBNetwork(scanResult)) {
                arrayList.add(5);
            } else if (isScanResultForWpa3EnterpriseTransitionNetwork(scanResult)) {
                arrayList.add(3);
                arrayList.add(9);
            } else if (isScanResultForWpa3EnterpriseOnlyNetwork(scanResult)) {
                arrayList.add(9);
            } else if (isScanResultForEapNetwork(scanResult)) {
                arrayList.add(3);
            }
            return arrayList;
        }
    }

    public static List<Integer> getSecurityTypesFromWifiConfiguration(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(14)) {
            return Arrays.asList(8);
        }
        if (wifiConfiguration.allowedKeyManagement.get(13)) {
            return Arrays.asList(7);
        }
        if (wifiConfiguration.allowedKeyManagement.get(10)) {
            return Arrays.asList(5);
        }
        if (wifiConfiguration.allowedKeyManagement.get(9)) {
            return Arrays.asList(6);
        }
        if (wifiConfiguration.allowedKeyManagement.get(8)) {
            return Arrays.asList(4);
        }
        if (wifiConfiguration.allowedKeyManagement.get(4)) {
            return Arrays.asList(2);
        }
        if (wifiConfiguration.allowedKeyManagement.get(2)) {
            return (wifiConfiguration.requirePmf && !wifiConfiguration.allowedPairwiseCiphers.get(1) && wifiConfiguration.allowedProtocols.get(1)) ? Arrays.asList(9) : Arrays.asList(3, 9);
        } else if (wifiConfiguration.allowedKeyManagement.get(1)) {
            return Arrays.asList(2);
        } else {
            if (wifiConfiguration.allowedKeyManagement.get(0) && wifiConfiguration.wepKeys != null) {
                int i = 0;
                while (true) {
                    String[] strArr = wifiConfiguration.wepKeys;
                    if (i >= strArr.length) {
                        break;
                    } else if (strArr[i] != null) {
                        return Arrays.asList(1);
                    } else {
                        i++;
                    }
                }
            }
            return Arrays.asList(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getSingleSecurityTypeFromMultipleSecurityTypes(List<Integer> list) {
        if (list.size() == 1) {
            return list.get(0).intValue();
        }
        if (list.size() == 2) {
            if (list.contains(0)) {
                return 0;
            }
            if (list.contains(2)) {
                return 2;
            }
            return list.contains(3) ? 3 : -1;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getSpeedDescription(Context context, WifiEntry wifiEntry) {
        if (context == null || wifiEntry == null) {
            return "";
        }
        int speed = wifiEntry.getSpeed();
        return speed != 5 ? speed != 10 ? speed != 20 ? speed != 30 ? "" : context.getString(R$string.wifitrackerlib_speed_label_very_fast) : context.getString(R$string.wifitrackerlib_speed_label_fast) : context.getString(R$string.wifitrackerlib_speed_label_okay) : context.getString(R$string.wifitrackerlib_speed_label_slow);
    }

    public static int getSpeedFromWifiInfo(WifiNetworkScoreCache wifiNetworkScoreCache, WifiInfo wifiInfo) {
        try {
            ScoredNetwork scoredNetwork = wifiNetworkScoreCache.getScoredNetwork(new NetworkKey(new WifiKey(wifiInfo.getSSID(), wifiInfo.getBSSID())));
            if (scoredNetwork == null) {
                return 0;
            }
            return roundToClosestSpeedEnum(scoredNetwork.calculateBadge(wifiInfo.getRssi()));
        } catch (IllegalArgumentException unused) {
            return 0;
        }
    }

    static int getSubIdForConfig(Context context, WifiConfiguration wifiConfiguration) {
        SubscriptionManager subscriptionManager;
        int i = -1;
        if (wifiConfiguration.carrierId == -1 || (subscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service")) == null) {
            return -1;
        }
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null && !activeSubscriptionInfoList.isEmpty()) {
            int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (subscriptionInfo.getCarrierId() == wifiConfiguration.carrierId && (i = subscriptionInfo.getSubscriptionId()) == defaultDataSubscriptionId) {
                    break;
                }
            }
        }
        return i;
    }

    private static String getSuggestionOrSpecifierLabel(Context context, WifiConfiguration wifiConfiguration) {
        if (context == null || wifiConfiguration == null) {
            return "";
        }
        String carrierNameForSubId = getCarrierNameForSubId(context, getSubIdForConfig(context, wifiConfiguration));
        if (TextUtils.isEmpty(carrierNameForSubId)) {
            String appLabel = getAppLabel(context, wifiConfiguration.creatorName);
            return !TextUtils.isEmpty(appLabel) ? appLabel : wifiConfiguration.creatorName;
        }
        return carrierNameForSubId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getVerboseLoggingDescription(WifiEntry wifiEntry) {
        if (!BaseWifiTracker.isVerboseLoggingEnabled() || wifiEntry == null) {
            return "";
        }
        StringJoiner stringJoiner = new StringJoiner(" ");
        String wifiInfoDescription = wifiEntry.getWifiInfoDescription();
        if (!TextUtils.isEmpty(wifiInfoDescription)) {
            stringJoiner.add(wifiInfoDescription);
        }
        String networkCapabilityDescription = wifiEntry.getNetworkCapabilityDescription();
        if (!TextUtils.isEmpty(networkCapabilityDescription)) {
            stringJoiner.add(networkCapabilityDescription);
        }
        String scanResultDescription = wifiEntry.getScanResultDescription();
        if (!TextUtils.isEmpty(scanResultDescription)) {
            stringJoiner.add(scanResultDescription);
        }
        String networkSelectionDescription = wifiEntry.getNetworkSelectionDescription();
        if (!TextUtils.isEmpty(networkSelectionDescription)) {
            stringJoiner.add(networkSelectionDescription);
        }
        return stringJoiner.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0049, code lost:
    
        if (r4 != 9) goto L43;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String getWifiConfigurationFailureMessage(android.content.Context r3, android.net.wifi.WifiConfiguration r4) {
        /*
            java.lang.String r0 = ""
            if (r3 == 0) goto La5
            if (r4 != 0) goto L8
            goto La5
        L8:
            boolean r1 = r4.hasNoInternetAccess()
            r2 = 2
            if (r1 == 0) goto L23
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r4 = r4.getNetworkSelectionStatus()
            int r4 = r4.getNetworkSelectionStatus()
            if (r4 != r2) goto L1c
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_no_internet_no_reconnect
            goto L1e
        L1c:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_no_internet
        L1e:
            java.lang.String r3 = r3.getString(r4)
            return r3
        L23:
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r1 = r4.getNetworkSelectionStatus()
            int r1 = r1.getNetworkSelectionStatus()
            if (r1 == 0) goto L6f
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r4 = r4.getNetworkSelectionStatus()
            int r4 = r4.getNetworkSelectionDisableReason()
            r1 = 1
            if (r4 == r1) goto L68
            if (r4 == r2) goto L61
            r1 = 3
            if (r4 == r1) goto L5a
            r1 = 4
            if (r4 == r1) goto L53
            r1 = 6
            if (r4 == r1) goto L53
            r1 = 8
            if (r4 == r1) goto L4c
            r1 = 9
            if (r4 == r1) goto L61
            goto L7a
        L4c:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_check_password_try_again
            java.lang.String r3 = r3.getString(r4)
            return r3
        L53:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_no_internet_no_reconnect
            java.lang.String r3 = r3.getString(r4)
            return r3
        L5a:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_disabled_network_failure
            java.lang.String r3 = r3.getString(r4)
            return r3
        L61:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_disabled_password_failure
            java.lang.String r3 = r3.getString(r4)
            return r3
        L68:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_disabled_generic
            java.lang.String r3 = r3.getString(r4)
            return r3
        L6f:
            int r4 = r4.getRecentFailureReason()
            r1 = 17
            if (r4 == r1) goto L9e
            switch(r4) {
                case 1002: goto L9e;
                case 1003: goto L97;
                case 1004: goto L9e;
                case 1005: goto L90;
                case 1006: goto L89;
                case 1007: goto L90;
                case 1008: goto L90;
                case 1009: goto L82;
                case 1010: goto L82;
                case 1011: goto L7b;
                default: goto L7a;
            }
        L7a:
            return r0
        L7b:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_network_not_found
            java.lang.String r3 = r3.getString(r4)
            return r3
        L82:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_mbo_oce_assoc_disallowed_insufficient_rssi
            java.lang.String r3 = r3.getString(r4)
            return r3
        L89:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_mbo_assoc_disallowed_max_num_sta_associated
            java.lang.String r3 = r3.getString(r4)
            return r3
        L90:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_mbo_assoc_disallowed_cannot_connect
            java.lang.String r3 = r3.getString(r4)
            return r3
        L97:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_poor_channel_conditions
            java.lang.String r3 = r3.getString(r4)
            return r3
        L9e:
            int r4 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_ap_unable_to_handle_new_sta
            java.lang.String r3 = r3.getString(r4)
            return r3
        La5:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.Utils.getWifiConfigurationFailureMessage(android.content.Context, android.net.wifi.WifiConfiguration):java.lang.String");
    }

    private static String getWifiConnected(Context context) {
        return context.getResources().getStringArray(R$array.wifitrackerlib_wifi_status)[NetworkInfo.DetailedState.CONNECTED.ordinal()];
    }

    static boolean isImsiPrivacyProtectionProvided(Context context, int i) {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        return (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null || (configForSubId.getInt("imsi_key_availability_int") & 2) == 0) ? false : true;
    }

    public static boolean isScanResultForEapNetwork(ScanResult scanResult) {
        return ((!scanResult.capabilities.contains("EAP/SHA1") && !scanResult.capabilities.contains("EAP/SHA256") && !scanResult.capabilities.contains("FT/EAP") && !scanResult.capabilities.contains("EAP-FILS")) || isScanResultForWpa3EnterpriseOnlyNetwork(scanResult) || isScanResultForWpa3EnterpriseTransitionNetwork(scanResult)) ? false : true;
    }

    public static boolean isScanResultForEapSuiteBNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("SUITE_B_192") && scanResult.capabilities.contains("RSN") && !scanResult.capabilities.contains("WEP") && !scanResult.capabilities.contains("TKIP") && isScanResultForPmfMandatoryNetwork(scanResult);
    }

    public static boolean isScanResultForOpenNetwork(ScanResult scanResult) {
        return (isScanResultForWepNetwork(scanResult) || isScanResultForPskNetwork(scanResult) || isScanResultForEapNetwork(scanResult) || isScanResultForSaeNetwork(scanResult) || isScanResultForWpa3EnterpriseTransitionNetwork(scanResult) || isScanResultForWpa3EnterpriseOnlyNetwork(scanResult) || isScanResultForWapiPskNetwork(scanResult) || isScanResultForWapiCertNetwork(scanResult) || isScanResultForEapSuiteBNetwork(scanResult) || isScanResultForUnknownAkmNetwork(scanResult)) ? false : true;
    }

    public static boolean isScanResultForOweNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("OWE");
    }

    public static boolean isScanResultForOweTransitionNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("OWE_TRANSITION");
    }

    private static boolean isScanResultForPmfCapableNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("[MFPC]");
    }

    private static boolean isScanResultForPmfMandatoryNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("[MFPR]");
    }

    public static boolean isScanResultForPskNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("PSK");
    }

    public static boolean isScanResultForSaeNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("SAE");
    }

    public static boolean isScanResultForUnknownAkmNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("?");
    }

    public static boolean isScanResultForWapiCertNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("WAPI-CERT");
    }

    public static boolean isScanResultForWapiPskNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("WAPI-PSK");
    }

    public static boolean isScanResultForWepNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("WEP");
    }

    public static boolean isScanResultForWpa3EnterpriseOnlyNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("EAP/SHA256") && !scanResult.capabilities.contains("EAP/SHA1") && scanResult.capabilities.contains("RSN") && !scanResult.capabilities.contains("WEP") && !scanResult.capabilities.contains("TKIP") && isScanResultForPmfMandatoryNetwork(scanResult) && isScanResultForPmfCapableNetwork(scanResult);
    }

    public static boolean isScanResultForWpa3EnterpriseTransitionNetwork(ScanResult scanResult) {
        return scanResult.capabilities.contains("EAP/SHA1") && scanResult.capabilities.contains("EAP/SHA256") && scanResult.capabilities.contains("RSN") && !scanResult.capabilities.contains("WEP") && !scanResult.capabilities.contains("TKIP") && !isScanResultForPmfMandatoryNetwork(scanResult) && isScanResultForPmfCapableNetwork(scanResult);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSimCredential(WifiConfiguration wifiConfiguration) {
        WifiEnterpriseConfig wifiEnterpriseConfig = wifiConfiguration.enterpriseConfig;
        return wifiEnterpriseConfig != null && wifiEnterpriseConfig.isAuthenticationSimBased();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSimPresent(Context context, final int i) {
        List<SubscriptionInfo> activeSubscriptionInfoList;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
        if (subscriptionManager == null || (activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList()) == null || activeSubscriptionInfoList.isEmpty()) {
            return false;
        }
        return activeSubscriptionInfoList.stream().anyMatch(new Predicate() { // from class: com.android.wifitrackerlib.Utils$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isSimPresent$1;
                lambda$isSimPresent$1 = Utils.lambda$isSimPresent$1(i, (SubscriptionInfo) obj);
                return lambda$isSimPresent$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isSimPresent$1(int i, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getCarrierId() == i;
    }

    static CharSequence linkifyAnnotation(final Context context, CharSequence charSequence, String str, final String str2) {
        if (TextUtils.isEmpty(str2)) {
            return charSequence;
        }
        SpannableString spannableString = new SpannableString(charSequence);
        for (Annotation annotation : (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class)) {
            if (TextUtils.equals(annotation.getValue(), str)) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
                ClickableSpan clickableSpan = new ClickableSpan() { // from class: com.android.wifitrackerlib.Utils.1
                    @Override // android.text.style.ClickableSpan
                    public void onClick(View view) {
                        view.startActivityForResult(HelpUtils.getHelpIntent(context, str2, view.getClass().getName()), 0);
                    }
                };
                spannableStringBuilder.setSpan(clickableSpan, spannableString.getSpanStart(annotation), spannableString.getSpanEnd(annotation), spannableString.getSpanFlags(clickableSpan));
                return spannableStringBuilder;
            }
        }
        return charSequence;
    }

    private static int roundToClosestSpeedEnum(int i) {
        if (i == 0) {
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
}
