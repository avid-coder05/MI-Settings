package com.android.settings.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import com.android.settings.wifi.details2.WifiPrivacyPreferenceController2;
import miuix.appcompat.widget.Spinner;

/* loaded from: classes2.dex */
public class MiuiWifiPrivacyUtils {
    private Context mContext;
    private boolean mIsEphemeral = false;
    private boolean mIsPasspoint = false;
    private WifiConfiguration mWifiConfiguration = null;

    public MiuiWifiPrivacyUtils(Context context) {
        this.mContext = context;
    }

    public static boolean isSamePrefValue(int i, int i2) {
        return WifiPrivacyPreferenceController2.translateMacRandomizedValueToPrefValue(i) == WifiPrivacyPreferenceController2.translateMacRandomizedValueToPrefValue(i2);
    }

    public int getRandomizationValue() {
        WifiConfiguration wifiConfiguration = this.mWifiConfiguration;
        return wifiConfiguration != null ? wifiConfiguration.macRandomizationSetting : com.android.settingslib.wifi.WifiUtils.getDefaultWifiPrivacy(this.mContext);
    }

    public boolean isAvailable() {
        return ((WifiManager) this.mContext.getSystemService("wifi")).isConnectedMacRandomizationSupported();
    }

    public void setIsEphemeral(boolean z) {
        this.mIsEphemeral = z;
    }

    public void setIsPasspoint(boolean z) {
        this.mIsPasspoint = z;
    }

    public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
        this.mWifiConfiguration = wifiConfiguration;
    }

    public void update(Spinner spinner) {
        spinner.setSelection(WifiPrivacyPreferenceController2.translateMacRandomizedValueToPrefValue(getRandomizationValue()));
        if (this.mIsEphemeral || this.mIsPasspoint) {
            spinner.setEnabled(false);
            spinner.setSelection(0);
        }
    }
}
