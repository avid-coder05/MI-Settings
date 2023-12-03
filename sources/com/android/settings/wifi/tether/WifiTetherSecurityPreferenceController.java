package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.wifi.SoftApCapability;
import android.net.wifi.WifiManager;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/* loaded from: classes2.dex */
public class WifiTetherSecurityPreferenceController extends WifiTetherBasePreferenceController implements WifiManager.SoftApCallback {
    boolean mIsWpa3Supported;
    private Map<Integer, String> mSecurityMap;
    private int mSecurityValue;

    public WifiTetherSecurityPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        this.mSecurityMap = new LinkedHashMap();
        this.mIsWpa3Supported = true;
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.wifi_tether_security);
        String[] stringArray2 = this.mContext.getResources().getStringArray(R.array.wifi_tether_security_values);
        for (int i = 0; i < stringArray.length; i++) {
            this.mSecurityMap.put(Integer.valueOf(Integer.parseInt(stringArray2[i])), stringArray[i]);
        }
        this.mWifiManager.registerSoftApCallback(context.getMainExecutor(), this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateDisplay$0(Integer num) {
        return num.intValue() > 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$updateDisplay$1(int i) {
        return new CharSequence[i];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$updateDisplay$2(int i) {
        return new CharSequence[i];
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_security_2" : "wifi_tether_security";
    }

    public int getSecurityType() {
        return this.mSecurityValue;
    }

    public void onCapabilityChanged(SoftApCapability softApCapability) {
        boolean areFeaturesSupported = softApCapability.areFeaturesSupported(4L);
        if (!areFeaturesSupported) {
            Log.i("wifi_tether_security", "WPA3 SAE is not supported on this device");
        }
        if (this.mIsWpa3Supported != areFeaturesSupported) {
            this.mIsWpa3Supported = areFeaturesSupported;
            updateDisplay();
        }
        this.mWifiManager.unregisterSoftApCallback(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mSecurityValue = Integer.parseInt((String) obj);
        DropDownPreference dropDownPreference = (DropDownPreference) preference;
        if (dropDownPreference.getEntry() != null) {
            dropDownPreference.setSummary(this.mSecurityMap.get(Integer.valueOf(this.mSecurityValue)));
        }
        WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener = this.mListener;
        if (onTetherConfigUpdateListener != null) {
            onTetherConfigUpdateListener.onTetherConfigUpdated(this);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        Preference preference = this.mPreference;
        if (preference == null) {
            return;
        }
        DropDownPreference dropDownPreference = (DropDownPreference) preference;
        if (!this.mIsWpa3Supported && this.mSecurityMap.keySet().removeIf(new Predicate() { // from class: com.android.settings.wifi.tether.WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateDisplay$0;
                lambda$updateDisplay$0 = WifiTetherSecurityPreferenceController.lambda$updateDisplay$0((Integer) obj);
                return lambda$updateDisplay$0;
            }
        })) {
            dropDownPreference.setEntries((CharSequence[]) this.mSecurityMap.values().stream().toArray(new IntFunction() { // from class: com.android.settings.wifi.tether.WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda1
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    CharSequence[] lambda$updateDisplay$1;
                    lambda$updateDisplay$1 = WifiTetherSecurityPreferenceController.lambda$updateDisplay$1(i);
                    return lambda$updateDisplay$1;
                }
            }));
            dropDownPreference.setEntryValues((CharSequence[]) this.mSecurityMap.keySet().stream().map(new Function() { // from class: com.android.settings.wifi.tether.WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.toBinaryString(((Integer) obj).intValue());
                }
            }).toArray(new IntFunction() { // from class: com.android.settings.wifi.tether.WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda2
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    CharSequence[] lambda$updateDisplay$2;
                    lambda$updateDisplay$2 = WifiTetherSecurityPreferenceController.lambda$updateDisplay$2(i);
                    return lambda$updateDisplay$2;
                }
            }));
        }
        int securityType = this.mWifiManager.getSoftApConfiguration().getSecurityType();
        if (this.mSecurityMap.get(Integer.valueOf(securityType)) == null) {
            securityType = 1;
        }
        this.mSecurityValue = securityType;
        if (dropDownPreference.getEntry() != null) {
            dropDownPreference.setSummary(this.mSecurityMap.get(Integer.valueOf(this.mSecurityValue)));
        }
        dropDownPreference.setValue(String.valueOf(this.mSecurityValue));
    }
}
