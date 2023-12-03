package com.android.settings.wifi.dpp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class WifiNetworkConfig implements Serializable {
    private boolean mHiddenSsid;
    private boolean mIsHotspot;
    private int mNetworkId;
    private String mPreSharedKey;
    private String mSecurity;
    private String mSsid;

    /* loaded from: classes2.dex */
    public interface Retriever {
        WifiNetworkConfig getWifiNetworkConfig();
    }

    public WifiNetworkConfig(WifiNetworkConfig wifiNetworkConfig) {
        this.mSecurity = wifiNetworkConfig.mSecurity;
        this.mSsid = wifiNetworkConfig.mSsid;
        this.mPreSharedKey = wifiNetworkConfig.mPreSharedKey;
        this.mHiddenSsid = wifiNetworkConfig.mHiddenSsid;
        this.mNetworkId = wifiNetworkConfig.mNetworkId;
        this.mIsHotspot = wifiNetworkConfig.mIsHotspot;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiNetworkConfig(String str, String str2, String str3, boolean z, int i, boolean z2) {
        this.mSecurity = str;
        this.mSsid = str2;
        this.mPreSharedKey = str3;
        this.mHiddenSsid = z;
        this.mNetworkId = i;
        this.mIsHotspot = z2;
    }

    private String addQuotationIfNeeded(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        if (str.length() >= 2 && str.startsWith("\"") && str.endsWith("\"")) {
            return str;
        }
        return "\"" + str + "\"";
    }

    private String escapeSpecialCharacters(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '\\' || charAt == ',' || charAt == ';' || charAt == ':') {
                sb.append('\\');
            }
            sb.append(charAt);
        }
        return sb.toString();
    }

    private WifiConfiguration getBasicWifiConfiguration() {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = addQuotationIfNeeded(this.mSsid);
        wifiConfiguration.hiddenSSID = this.mHiddenSsid;
        wifiConfiguration.networkId = this.mNetworkId;
        return wifiConfiguration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WifiNetworkConfig getValidConfigOrNull(Intent intent) {
        return getValidConfigOrNull(intent.getStringExtra("security"), intent.getStringExtra("ssid"), intent.getStringExtra("preSharedKey"), intent.getBooleanExtra("hiddenSsid", false), intent.getIntExtra("networkId", -1), intent.getBooleanExtra("isHotspot", false));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WifiNetworkConfig getValidConfigOrNull(String str, String str2, String str3, boolean z, int i, boolean z2) {
        if (isValidConfig(str, str2, str3, z)) {
            return new WifiNetworkConfig(str, str2, str3, z, i, z2);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isValidConfig(WifiNetworkConfig wifiNetworkConfig) {
        if (wifiNetworkConfig == null) {
            return false;
        }
        return isValidConfig(wifiNetworkConfig.mSecurity, wifiNetworkConfig.mSsid, wifiNetworkConfig.mPreSharedKey, wifiNetworkConfig.mHiddenSsid);
    }

    static boolean isValidConfig(String str, String str2, String str3, boolean z) {
        if (TextUtils.isEmpty(str) || "nopass".equals(str) || !TextUtils.isEmpty(str3)) {
            return z || !TextUtils.isEmpty(str2);
        }
        return false;
    }

    public boolean getHiddenSsid() {
        return this.mHiddenSsid;
    }

    public int getNetworkId() {
        return this.mNetworkId;
    }

    public String getPreSharedKey() {
        return this.mPreSharedKey;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getQrCode() {
        StringBuilder sb = new StringBuilder("WIFI:");
        sb.append("S:");
        sb.append(escapeSpecialCharacters(this.mSsid));
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        sb.append("T:");
        sb.append(TextUtils.isEmpty(this.mSecurity) ? "" : this.mSecurity);
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        sb.append("P:");
        sb.append(TextUtils.isEmpty(this.mPreSharedKey) ? "" : escapeSpecialCharacters(this.mPreSharedKey));
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        sb.append("H:");
        sb.append(this.mHiddenSsid);
        sb.append(";;");
        return sb.toString();
    }

    public String getSecurity() {
        return this.mSecurity;
    }

    public String getSsid() {
        return this.mSsid;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<WifiConfiguration> getWifiConfigurations(Context context) {
        ArrayList arrayList = new ArrayList();
        if (isValidConfig(this)) {
            if (TextUtils.isEmpty(this.mSecurity) || "nopass".equals(this.mSecurity)) {
                WifiConfiguration basicWifiConfiguration = getBasicWifiConfiguration();
                basicWifiConfiguration.setSecurityParams(6);
                arrayList.add(basicWifiConfiguration);
                WifiConfiguration basicWifiConfiguration2 = getBasicWifiConfiguration();
                basicWifiConfiguration2.allowedKeyManagement.set(0);
                arrayList.add(basicWifiConfiguration2);
                return arrayList;
            }
            WifiConfiguration basicWifiConfiguration3 = getBasicWifiConfiguration();
            WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
            if (this.mSecurity.startsWith("WEP")) {
                basicWifiConfiguration3.setSecurityParams(1);
                int length = this.mPreSharedKey.length();
                if ((length == 10 || length == 26 || length == 58) && this.mPreSharedKey.matches("[0-9A-Fa-f]*")) {
                    basicWifiConfiguration3.wepKeys[0] = this.mPreSharedKey;
                } else {
                    basicWifiConfiguration3.wepKeys[0] = addQuotationIfNeeded(this.mPreSharedKey);
                }
            } else if (this.mSecurity.startsWith("WPA")) {
                basicWifiConfiguration3.setSecurityParams(2);
                if (this.mPreSharedKey.matches("[0-9A-Fa-f]{64}")) {
                    basicWifiConfiguration3.preSharedKey = this.mPreSharedKey;
                } else {
                    basicWifiConfiguration3.preSharedKey = addQuotationIfNeeded(this.mPreSharedKey);
                }
            } else if (!this.mSecurity.startsWith("SAE") || !wifiManager.isWpa3SaeSupported()) {
                Log.w("WifiNetworkConfig", "Unsupported security");
                return arrayList;
            } else {
                basicWifiConfiguration3.setSecurityParams(4);
                if (this.mPreSharedKey.length() != 0) {
                    basicWifiConfiguration3.preSharedKey = addQuotationIfNeeded(this.mPreSharedKey);
                }
            }
            arrayList.add(basicWifiConfiguration3);
            return arrayList;
        }
        return arrayList;
    }

    public boolean isHotspot() {
        return this.mIsHotspot;
    }

    public boolean isSupportWifiDpp(Context context) {
        if (WifiDppUtils.isWifiDppEnabled(context) && !TextUtils.isEmpty(this.mSecurity)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
            String str = this.mSecurity;
            str.hashCode();
            if (str.equals("SAE")) {
                if (wifiManager.isWpa3SaeSupported()) {
                    return true;
                }
            } else if (str.equals("WPA")) {
                return true;
            }
            return false;
        }
        return false;
    }
}
