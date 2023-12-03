package com.android.settingslib.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.SystemClock;
import android.provider.Settings;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$string;
import java.util.Iterator;
import java.util.Map;
import miui.cloud.CloudPushConstants;
import miui.provider.ExtraContacts;
import miui.vip.VipService;

/* loaded from: classes2.dex */
public class WifiUtils {
    static final int[] WIFI_PIE = {17302903, 17302904, 17302905, 17302906, 17302907};
    static final int[] NO_INTERNET_WIFI_PIE = {R$drawable.ic_no_internet_wifi_signal_0, R$drawable.ic_no_internet_wifi_signal_1, R$drawable.ic_no_internet_wifi_signal_2, R$drawable.ic_no_internet_wifi_signal_3, R$drawable.ic_no_internet_wifi_signal_4};

    public static String buildLoggingSummary(AccessPoint accessPoint, WifiConfiguration wifiConfiguration) {
        StringBuilder sb = new StringBuilder();
        WifiInfo info = accessPoint.getInfo();
        if (accessPoint.isActive() && info != null) {
            sb.append(" f=" + Integer.toString(info.getFrequency()));
        }
        sb.append(" " + getVisibilityStatus(accessPoint));
        if (wifiConfiguration != null && wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionStatus() != 0) {
            sb.append(" (" + wifiConfiguration.getNetworkSelectionStatus().getNetworkStatusString());
            if (wifiConfiguration.getNetworkSelectionStatus().getDisableTime() > 0) {
                long currentTimeMillis = (System.currentTimeMillis() - wifiConfiguration.getNetworkSelectionStatus().getDisableTime()) / 1000;
                long j = currentTimeMillis % 60;
                long j2 = (currentTimeMillis / 60) % 60;
                long j3 = (j2 / 60) % 60;
                sb.append(", ");
                if (j3 > 0) {
                    sb.append(Long.toString(j3) + "h ");
                }
                sb.append(Long.toString(j2) + "m ");
                sb.append(Long.toString(j) + "s ");
            }
            sb.append(")");
        }
        if (wifiConfiguration != null) {
            WifiConfiguration.NetworkSelectionStatus networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus();
            for (int i = 0; i <= WifiConfiguration.NetworkSelectionStatus.getMaxNetworkSelectionDisableReason(); i++) {
                if (networkSelectionStatus.getDisableReasonCounter(i) != 0) {
                    sb.append(" ");
                    sb.append(WifiConfiguration.NetworkSelectionStatus.getNetworkSelectionDisableReasonString(i));
                    sb.append("=");
                    sb.append(networkSelectionStatus.getDisableReasonCounter(i));
                }
            }
        }
        return sb.toString();
    }

    public static int getDefaultWifiPrivacy(Context context) {
        int i;
        try {
            i = context.getResources().getBoolean(context.getResources().getIdentifier("config_randomization_none_as_default", "bool", "android.miui"));
        } catch (Exception e) {
            e.printStackTrace();
            i = 0;
        }
        return i ^ 1;
    }

    public static int getInternetIconResource(int i, boolean z) {
        if (i >= 0) {
            int[] iArr = WIFI_PIE;
            if (i < iArr.length) {
                return z ? NO_INTERNET_WIFI_PIE[i] : iArr[i];
            }
        }
        throw new IllegalArgumentException("No Wifi icon found for level: " + i);
    }

    public static String getMeteredLabel(Context context, WifiConfiguration wifiConfiguration) {
        return (wifiConfiguration.meteredOverride == 1 || (wifiConfiguration.meteredHint && !isMeteredOverridden(wifiConfiguration))) ? context.getString(R$string.wifi_metered_label) : context.getString(R$string.wifi_unmetered_label);
    }

    private static int getSpecificApSpeed(ScanResult scanResult, Map<String, TimestampedScoredNetwork> map) {
        TimestampedScoredNetwork timestampedScoredNetwork = map.get(scanResult.BSSID);
        if (timestampedScoredNetwork == null) {
            return 0;
        }
        return timestampedScoredNetwork.getScore().calculateBadge(scanResult.level);
    }

    static String getVisibilityStatus(AccessPoint accessPoint) {
        String str;
        StringBuilder sb;
        WifiInfo info = accessPoint.getInfo();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        StringBuilder sb4 = new StringBuilder();
        StringBuilder sb5 = new StringBuilder();
        int i = 0;
        if (!accessPoint.isActive() || info == null) {
            str = null;
        } else {
            str = info.getBSSID();
            if (str != null) {
                sb2.append(" ");
                sb2.append(str);
            }
            sb2.append(" standard = ");
            sb2.append(info.getWifiStandard());
            sb2.append(" rssi=");
            sb2.append(info.getRssi());
            sb2.append(" ");
            sb2.append(" score=");
            sb2.append(info.getScore());
            if (accessPoint.getSpeed() != 0) {
                sb2.append(" speed=");
                sb2.append(accessPoint.getSpeedLabel());
            }
            sb2.append(String.format(" tx=%.1f,", Double.valueOf(info.getSuccessfulTxPacketsPerSecond())));
            sb2.append(String.format("%.1f,", Double.valueOf(info.getRetriedTxPacketsPerSecond())));
            sb2.append(String.format("%.1f ", Double.valueOf(info.getLostTxPacketsPerSecond())));
            sb2.append(String.format("rx=%.1f", Double.valueOf(info.getSuccessfulRxPacketsPerSecond())));
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        Iterator<ScanResult> it = accessPoint.getScanResults().iterator();
        int i2 = 0;
        int i3 = -127;
        int i4 = -127;
        int i5 = -127;
        int i6 = 0;
        while (true) {
            sb = sb2;
            if (!it.hasNext()) {
                break;
            }
            ScanResult next = it.next();
            if (next == null) {
                sb2 = sb;
            } else {
                int i7 = next.frequency;
                Iterator<ScanResult> it2 = it;
                if (i7 >= 4900 && i7 <= 5900) {
                    i6++;
                    int i8 = next.level;
                    if (i8 > i4) {
                        i4 = i8;
                    }
                    if (i6 <= 4) {
                        sb4.append(verboseScanResultSummary(accessPoint, next, str, elapsedRealtime));
                    }
                } else if (i7 >= 2400 && i7 <= 2500) {
                    i++;
                    int i9 = next.level;
                    if (i9 > i3) {
                        i3 = i9;
                    }
                    if (i <= 4) {
                        sb3.append(verboseScanResultSummary(accessPoint, next, str, elapsedRealtime));
                    }
                } else if (i7 >= 58320 && i7 <= 70200) {
                    i2++;
                    int i10 = next.level;
                    if (i10 > i5) {
                        i5 = i10;
                    }
                    if (i2 <= 4) {
                        sb5.append(verboseScanResultSummary(accessPoint, next, str, elapsedRealtime));
                    }
                }
                sb2 = sb;
                it = it2;
            }
        }
        sb.append(" [");
        if (i > 0) {
            sb.append("(");
            sb.append(i);
            sb.append(")");
            if (i > 4) {
                sb.append("max=");
                sb.append(i3);
                sb.append(",");
            }
            sb.append(sb3.toString());
        }
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        if (i6 > 0) {
            sb.append("(");
            sb.append(i6);
            sb.append(")");
            if (i6 > 4) {
                sb.append("max=");
                sb.append(i4);
                sb.append(",");
            }
            sb.append(sb4.toString());
        }
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        if (i2 > 0) {
            sb.append("(");
            sb.append(i2);
            sb.append(")");
            if (i2 > 4) {
                sb.append("max=");
                sb.append(i5);
                sb.append(",");
            }
            sb.append(sb5.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    public static boolean is24GHz(int i) {
        return i > 2400 && i < 2500;
    }

    public static boolean is24GHz(ScanResult scanResult) {
        return is24GHz(scanResult.frequency);
    }

    public static boolean is5GHz(int i) {
        return i > 4900 && i < 5900;
    }

    public static boolean is5GHz(ScanResult scanResult) {
        return is5GHz(scanResult.frequency);
    }

    public static boolean isInMishow(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "disable_security_by_mishow", 0) == 1;
    }

    public static boolean isMeteredOverridden(WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.meteredOverride != 0;
    }

    static String verboseScanResultSummary(AccessPoint accessPoint, ScanResult scanResult, String str, long j) {
        StringBuilder sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        if (scanResult.BSSID.equals(str)) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        sb.append(",");
        sb.append(scanResult.level);
        int specificApSpeed = getSpecificApSpeed(scanResult, accessPoint.getScoredNetworkCache());
        if (specificApSpeed != 0) {
            sb.append(",");
            sb.append(accessPoint.getSpeedLabel(specificApSpeed));
        }
        int i = ((int) (j - (scanResult.timestamp / 1000))) / VipService.VIP_SERVICE_FAILURE;
        sb.append(",");
        sb.append(i);
        sb.append(CloudPushConstants.WATERMARK_TYPE.SUBSCRIPTION);
        sb.append("}");
        return sb.toString();
    }
}
