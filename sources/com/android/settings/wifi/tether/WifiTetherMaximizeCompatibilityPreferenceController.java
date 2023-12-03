package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;

/* loaded from: classes2.dex */
public class WifiTetherMaximizeCompatibilityPreferenceController extends WifiTetherBasePreferenceController {
    private boolean mIsChecked;

    public WifiTetherMaximizeCompatibilityPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        this.mIsChecked = isMaximizeCompatibilityEnabled();
    }

    private boolean is5GhzBandSupported() {
        WifiManager wifiManager = this.mWifiManager;
        return (wifiManager == null || !wifiManager.is5GHzBandSupported() || this.mWifiManager.getCountryCode() == null) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_tether_maximize_compatibility";
    }

    boolean isMaximizeCompatibilityEnabled() {
        SoftApConfiguration softApConfiguration;
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager == null || (softApConfiguration = wifiManager.getSoftApConfiguration()) == null) {
            return false;
        }
        if (this.mWifiManager.isBridgedApConcurrencySupported()) {
            boolean isBridgedModeOpportunisticShutdownEnabled = softApConfiguration.isBridgedModeOpportunisticShutdownEnabled();
            Log.d("WifiTetherMaximizeCompatibilityPref", "isBridgedModeOpportunisticShutdownEnabled:" + isBridgedModeOpportunisticShutdownEnabled);
            return !isBridgedModeOpportunisticShutdownEnabled;
        }
        int band = softApConfiguration.getBand();
        Log.d("WifiTetherMaximizeCompatibilityPref", "getBand:" + band);
        return band == 1;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mIsChecked = ((Boolean) obj).booleanValue();
        WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener = this.mListener;
        if (onTetherConfigUpdateListener != null) {
            onTetherConfigUpdateListener.onTetherConfigUpdated(this);
            return true;
        }
        return true;
    }

    public void setupMaximizeCompatibility(SoftApConfiguration.Builder builder) {
        if (builder == null) {
            return;
        }
        boolean z = this.mIsChecked;
        if (this.mWifiManager.isBridgedApConcurrencySupported()) {
            builder.setBands(new int[]{1, 3});
            Log.d("WifiTetherMaximizeCompatibilityPref", "setBridgedModeOpportunisticShutdownEnabled:" + z);
            builder.setBridgedModeOpportunisticShutdownEnabled(z ^ true);
            return;
        }
        int i = z ? 1 : 3;
        Log.d("WifiTetherMaximizeCompatibilityPref", "setBand:" + i);
        builder.setBand(i);
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        Preference preference = this.mPreference;
        if (preference == null) {
            return;
        }
        preference.setEnabled(is5GhzBandSupported());
        ((SwitchPreference) this.mPreference).setChecked(this.mIsChecked);
        this.mPreference.setSummary(this.mWifiManager.isBridgedApConcurrencySupported() ? R.string.wifi_hotspot_maximize_compatibility_dual_ap_summary : R.string.wifi_hotspot_maximize_compatibility_single_ap_summary);
    }
}
