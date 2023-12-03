package com.android.settings.wifi.dpp;

import android.text.TextUtils;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class WifiQrCode implements Serializable {
    private String mInformation;
    private String mPublicKey;
    private String mQrCode;
    private String mScheme;
    private WifiNetworkConfig mWifiNetworkConfig;

    public WifiQrCode(String str) throws IllegalArgumentException {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Empty QR code");
        }
        this.mQrCode = str;
        if (str.startsWith("DPP:")) {
            this.mScheme = "DPP";
            parseWifiDppQrCode(str);
        } else if (!str.startsWith("WIFI:")) {
            throw new IllegalArgumentException("Invalid scheme");
        } else {
            this.mScheme = "WIFI";
            parseZxingWifiQrCode(str);
        }
    }

    private List<String> getKeyValueList(String str, String str2, String str3) {
        return Arrays.asList(str.substring(str2.length()).split("(?<!\\\\)" + Pattern.quote(str3)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WifiQrCode getValidWifiDppQrCodeOrNull(String str) {
        WifiQrCode wifiQrCode;
        try {
            wifiQrCode = new WifiQrCode(str);
        } catch (IllegalArgumentException unused) {
        }
        if ("DPP".equals(wifiQrCode.getScheme())) {
            return wifiQrCode;
        }
        return null;
    }

    private String getValueOrNull(List<String> list, String str) {
        for (String str2 : list) {
            if (str2.startsWith(str)) {
                return str2.substring(str.length());
            }
        }
        return null;
    }

    private void parseWifiDppQrCode(String str) throws IllegalArgumentException {
        List<String> keyValueList = getKeyValueList(str, "DPP:", ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        String valueOrNull = getValueOrNull(keyValueList, "K:");
        if (TextUtils.isEmpty(valueOrNull)) {
            throw new IllegalArgumentException("Invalid format");
        }
        this.mPublicKey = valueOrNull;
        this.mInformation = getValueOrNull(keyValueList, "I:");
    }

    private void parseZxingWifiQrCode(String str) throws IllegalArgumentException {
        List<String> keyValueList = getKeyValueList(str, "WIFI:", ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        String valueOrNull = getValueOrNull(keyValueList, "T:");
        String valueOrNull2 = getValueOrNull(keyValueList, "S:");
        String valueOrNull3 = getValueOrNull(keyValueList, "P:");
        String valueOrNull4 = getValueOrNull(keyValueList, "H:");
        WifiNetworkConfig validConfigOrNull = WifiNetworkConfig.getValidConfigOrNull(valueOrNull, removeBackSlash(valueOrNull2), removeBackSlash(valueOrNull3), valueOrNull4 == null ? true : "true".equalsIgnoreCase(valueOrNull4), -1, false);
        this.mWifiNetworkConfig = validConfigOrNull;
        if (validConfigOrNull == null) {
            throw new IllegalArgumentException("Invalid format");
        }
    }

    public String getInformation() {
        return this.mInformation;
    }

    String getPublicKey() {
        return this.mPublicKey;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getQrCode() {
        return this.mQrCode;
    }

    public String getScheme() {
        return this.mScheme;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiNetworkConfig getWifiNetworkConfig() {
        WifiNetworkConfig wifiNetworkConfig = this.mWifiNetworkConfig;
        if (wifiNetworkConfig == null) {
            return null;
        }
        return new WifiNetworkConfig(wifiNetworkConfig);
    }

    String removeBackSlash(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        for (char c : str.toCharArray()) {
            if (c != '\\') {
                sb.append(c);
            } else if (z) {
                sb.append(c);
            } else {
                z = true;
            }
            z = false;
        }
        return sb.toString();
    }
}
